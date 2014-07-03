package com.example.autismdiagnose.androidHelpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;


public class FileProcessor {
	
	public static ArrayList<String> videos = new ArrayList<String>();
	
	public static boolean write_append(Activity activity, String data, String path) {
		FileWriter writer;
			
		// Do not write duplicate files or empty strings
		if (videos.contains(data) && data.length() > 0){
			return false;
		}
		
		videos.add(data);
		try{
			FileOutputStream fos = activity.openFileOutput(path, Context.MODE_APPEND);
			
			if (data.length() != 0)
			data = data + "\n";
			fos.write(data.getBytes());
			fos.close();	
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	public static void write(Activity activity, String data, ArrayList<String> dataList, String path) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(activity.openFileOutput(path, Context.MODE_PRIVATE)));
			
			// Either writer one single file or a list of file names when editing the master file.
			if (dataList == null){
				writer.write(data);
			}
			else {
				for (String loc: dataList){
					writer.write(loc + "\n");
				}
			}    
			writer.close();
			
		} catch (Exception e) {
		e.printStackTrace();
		}   
	}
	
	// Read each line from the file and return an arraylist containing them
	public static ArrayList<String> read(Activity activity, String path) {
		ArrayList<String> temp = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(activity.openFileInput(path)));
			String line = reader.readLine();
			while(line != null) {
				temp.add(line);
				line = reader.readLine();
			}
			
			reader.close();
		} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		
		return temp;
	}

}
