package com.nevin;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;






import android.app.Activity;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.view.KeyEvent;

public class Boot extends BroadcastReceiver {
    static final String action_boot="android.intent.action.BOOT_COMPLETED";
    private Context begin;
    private String dir;
 
    @Override
    public void onReceive(Context context, Intent intent) {
    	

		
        if (intent.getAction().equals(action_boot)){
            TimerTask task = new TimerTask() {
				public void run() {
					try {
						NanoHTTPD	 httpServer = new NanoHTTPD(8070,new File("/mnt/usb/sda1/video"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			Timer timer = new Timer();
			timer.schedule(task, 5000);
        }
    }
    
}

