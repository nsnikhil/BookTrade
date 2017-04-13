package com.trade.book.booktrade.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by Nikhil on 13-Apr-17.
 */

public class serviceFileDeletion extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void deleteAll(){
        File folder = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(folder.exists()){
            if(folder.isDirectory()){
                String[] files = folder.list();
                for(String file :files){
                    File f = new File(folder,file);
                    if(f.exists()){
                        f.delete();
                    }
                }
            }
        }
    }
}
