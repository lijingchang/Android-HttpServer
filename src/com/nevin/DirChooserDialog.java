package com.nevin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.nevin.LocalClient.MyListener;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author HKP女生发型
 * 2011-6-17
 *
 */
public class DirChooserDialog extends Dialog implements android.view.View.OnClickListener{
	
	private ListView list;
	ArrayAdapter<String> Adapter;
	ArrayList<String> arr=new ArrayList<String>();
	
	Context context;
	private String path;
	private MyListener listen;
	
	private TextView title;
	private Button home,back,ok;
//	private LinearLayout titleView;
	
	private int type = 1;
	private String[] fileType = null;
	
	public final static int TypeOpen = 1;
	public final static int TypeSave = 2;
	
	/**
	 * @param context
	 * @param type 值为1表示创建打开目录类型的对话框，2为创建保存文件到目录类型的对话框
	 * @param fileType 要过滤的文件类型,null表示只选择目录
	 * @param resultPath 点OK按钮返回的结果，目录或者目录+文件名
	 */
	public DirChooserDialog(Context context,int type,String[]fileType,String resultPath,MyListener listen) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.type = type;
		this.fileType = fileType;
		this.path = resultPath;//"/mnt/usb";//resultPath;
		this.listen=listen;
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectmenu);
		
		//path = getRootDir();
		arr = (ArrayList<String>) getDirs(path);
		Adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, arr);
		
		list = (ListView)findViewById(R.id.list_dir);
		list.setAdapter(Adapter);
		list.setItemsCanFocus(true);
		list.setOnItemClickListener(lvLis);

		home = (Button) findViewById(R.id.btn_home);

		home.setOnClickListener(this);
		
		back = (Button) findViewById(R.id.btn_back);
		back.setOnClickListener(this);
		
		ok = (Button) findViewById(R.id.btn_ok);
		ok.setOnClickListener(this);
		
//		titleView = (LinearLayout) findViewById(R.id.dir_layout);
		
		if(type == TypeOpen){
			title = new TextView(context);
	//		titleView.addView(title);
			title.setText(path);
		}else if(type == TypeSave){

		}
		
	}
	Runnable add=new Runnable(){

		@Override
		public void run() {
 			arr.clear();
 
 			List<String> temp = getDirs(path);
			for(int i = 0;i < temp.size();i++)
				arr.add(temp.get(i));
			Adapter.notifyDataSetChanged();
		}   	
    };
   
    private OnItemClickListener lvLis=new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String temp = (String) arg0.getItemAtPosition(arg2);
 			if(temp.equals(".."))
				path = getSubDir(path);
			else if(path.equals("/"))
				path = path+temp;
			else
				path = path+"/"+temp;
			
 			if(type == TypeOpen)
				title.setText(path);
			
			Handler handler=new Handler();
	    	handler.post(add);
		}
    };
	
	private List<String> getDirs(String ipath){
		List<String> file = new ArrayList<String>();
 		File[] myFile = new File(ipath).listFiles();
		if(myFile == null){
			file.add("..");
			
		}else
			for(File f: myFile){
 				if(f.isDirectory()){
					String tempf = f.toString();
					int pos = tempf.lastIndexOf("/");
					String subTemp = tempf.substring(pos+1, tempf.length());
 					file.add(subTemp);	
 				}
 				if(f.isFile() && fileType != null){
					for(int i = 0;i< fileType.length;i++){
						int typeStrLen = fileType[i].length();
						
						String fileName = f.getPath().substring(f.getPath().length()- typeStrLen);
						if (fileName.toLowerCase().equals(fileType[i])) {
							file.add(f.toString().substring(path.length()+1,f.toString().length()));	
						}
					}
				}
			}
		
		if(file.size()==0)
			file.add("..");
		
 		return file;
	}
	
 
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == home.getId()){
			path = "/mnt";//"storage"
			if(type == TypeOpen)
				title.setText(path);			
			Handler handler=new Handler();
	    	handler.post(add);
		}else if(v.getId() == back.getId()){
			path = getSubDir(path);
			if(type == TypeOpen)
				title.setText(path);			
			Handler handler=new Handler();
	    	handler.post(add);
		}else if(v.getId() == ok.getId()){
			dismiss();
		this.listen.refreshActivity(path);
		}
	}
	
	private String getSDPath(){ 
	       File sdDir = null; 
	       boolean sdCardExist = Environment.getExternalStorageState()   
	                           .equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在 
	       if(sdCardExist)   
	       {                               
	         sdDir = Environment.getExternalStorageDirectory();//获取根目录 
	      }   
	       if(sdDir == null){
 	    	   return null;
	       }
	       return sdDir.toString(); 
	       
	} 
	
	private String getRootDir(){
		String root = "/";
		path = getSDPath();
		if (path == null)
			path="/";
		return root;
	}
	
	private String getSubDir(String path){
		String subpath = null;
		
		int pos = path.lastIndexOf("/");
		
		if(pos == path.length()){
			path = path.substring(0,path.length()-1);
			pos = path.lastIndexOf("/");
		}
		
		subpath = path.substring(0,pos);
		
		if(pos == 0)
			subpath = path;
		
		return subpath;
	}
}