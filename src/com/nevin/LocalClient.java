package com.nevin;

import android.view.View;
import android.webkit.WebChromeClient;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import com.nevin.NanoHTTPD;
import com.nevin.downloader.DownlaodStateListener.*;
import com.nevin.downloader.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebView;
import android.widget.Button;
import android.util.Log;
import android.webkit.WebViewClient;

@SuppressLint("SdCardPath")
public class LocalClient extends Activity {
	private final static String TAG = "Local";
	
	private ProgressDialog mProgressDialog;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	WebView webview;
	NanoHTTPD httpServer;
	TextView tv;
	Button butClose;
	private EditText addr;
	private Button tijiao;
	private SharedPreferences.Editor editor;
	private SharedPreferences mySharedPreferences;

	/* To ensure we don't open a new window each click. */
	public class LocalWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			char last = '\0';
			if( url != null && url.length() > 0 ) last = url.charAt( url.length() - 1 ) ;
			if (!(last == '/')) {
				Log.d(TAG,"url: "+url);
				String decodeUrl = decodeUri(url);
				String fileName = decodeUrl.substring(decodeUrl.lastIndexOf("/")+1);
				startDownload(fileName,url);
				return(true);
			}
			else {
				view.loadUrl(url);
				return(false);
			}
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		webview = (WebView) findViewById(R.id.webview);
		webview.setVisibility(android.view.View.INVISIBLE);
		addr=(EditText)findViewById(R.id.addr);
		tijiao=(Button)findViewById(R.id.submit);
		tv = (TextView) this.findViewById(R.id.textview);
		tv.setText("Ready!");
		mySharedPreferences= getSharedPreferences("httpdir", Activity.MODE_PRIVATE); 
		tijiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	String path = "/mnt";
				String [] fileType = {"dst"};//要过滤的文件类型列表
				DirChooserDialog dlg = new DirChooserDialog(LocalClient.this,2,fileType,path,myListener);
				dlg.setTitle("Choose http file dir");
				dlg.show();
            }
		});
	}
	
	
	@Override
	protected void onStart(){
		super.onStart();
		try {
			String dir = mySharedPreferences.getString("dir", null); 
			if(dir!=null)
				httpServer = new NanoHTTPD(8070,new File(dir));
			else
			httpServer = new NanoHTTPD(8070,new File("/mnt/usb"));///storage/extSdCard//mnt/usb /mnt/usb/sdb2/video//"/storage/external_SD"
			webview = (WebView) findViewById(R.id.webview);
            webview.setWebViewClient(new LocalWebViewClient());
            webview.getSettings().setJavaScriptEnabled(true);
            webview.loadUrl("http://localhost:8070/");
            webview.setVisibility(android.view.View.VISIBLE);
		}
		catch(Exception e) {
			
			tv.setText(e.toString());
			webview = (WebView) findViewById(R.id.webview);
            webview.setWebViewClient(new LocalWebViewClient());
            webview.getSettings().setJavaScriptEnabled(true);
            webview.loadUrl("http://localhost:8070/");
            webview.setVisibility(android.view.View.VISIBLE);
		}
	}
	
	@Override
	protected void onStop(){
		super.onStop();
//		httpServer.stop();
	}

	interface MyListener{
	       public void refreshActivity(String text);
	}
	
	private MyListener myListener = new MyListener(){
        @Override
        public void refreshActivity(String text){
        	if(!text.equals("/storage")){
				editor = mySharedPreferences.edit(); 
				editor.putString("dir", text);
				editor.commit(); 
				try {
					Toast.makeText(getApplicationContext(), text,
						     Toast.LENGTH_SHORT).show();
					httpServer.stop();
					httpServer = new NanoHTTPD(8070,new File(text));
					webview.loadUrl("http://localhost:8070/");
				}
    		catch(Exception e) {
    			tv.setText(e.toString());
    		}}
        }
	};

	
	private void startDownload(final String fileName, final String downloadUrl){
		Log.d(TAG,"startDownload...: "+fileName);
		Log.d(TAG,"startDownload...: "+downloadUrl);
		
		final DownloadFileAsync downloader = new DownloadFileAsync(this,fileName,downloadUrl);
		downloader.setOnDownloadStartedListener(new OnDownloadStartedListener() {
			@Override
			public void onDownloadStarted(String fileName, String downloadUrl,int startProgress) {
				showDialog(DIALOG_DOWNLOAD_PROGRESS);
				mProgressDialog.setMessage("downloading "+fileName);
				mProgressDialog.setProgress(startProgress);
				mProgressDialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						// TODO Auto-generated method stub
						downloader.stopDownload();
					}
				});
				mProgressDialog.show();
			}
		});
		downloader.setOnProgressUpdateListener(new OnProgressUpdateListener() {
			@Override
			public void onProgressUpdate(String fileName, String downloadUrl,int progress) {
				mProgressDialog.setMessage("downloading "+fileName);
				mProgressDialog.setProgress(progress);
			}
		});
		downloader.setOnDownloadFinishedListener(new OnDownloadFinishedListener() {
			@Override
			public void onDownloadFinished(String fileName, String downloadUrl) {
				dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
			}
		});
		
		downloader.execute(fileName,downloadUrl);
	}
	
	//our progress bar settings
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS: //we set this to 0
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading file...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }
    
	private String decodeUri(String uri){
		String newUri="";
		try{
			newUri = URLDecoder.decode(uri, "utf-8");	
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return newUri;
	}
}
