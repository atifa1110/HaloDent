package com.dentist.halodent.Utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class MemoryData {

    public static void saveLastMessage(String data, String groupId, Context context){
        try{
            FileOutputStream fileOutputStream = context.openFileOutput(groupId+".txt",Context.MODE_PRIVATE);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getLastMessage(Context context, String groupId){
        String data = "";
        try{
            FileInputStream fis = context.openFileInput(groupId+".txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine())!=null){
                sb.append(line);
            }
            data = sb.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }
}
