/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.free.p7zip;

import android.app.Activity;
import android.widget.TextView;
import android.os.Bundle;
import java.io.*;

import android.util.*;
import java.util.*;


public class HelloJni extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /* Create a TextView and set its content.
         * the text is retrieved by calling a native
         * function.
         */
        TextView  tv = new TextView(this);
		try
		{
//			InputStream is = getAssets().open("7za");
//			FileUtil.is2OS(is, new FileOutputStream("/data/data/com.free.p7zip/7za"));
//			System.out.println(CommandUtils.exec("chmod", "777", "/data/data/com.free.p7zip/7za"));
//			System.out.println(CommandUtils.exec("ls", "-l", "/data/data/com.free.p7zip"));
//			System.out.println(CommandUtils.exec("/data/data/com.free.p7zip/7za"));
			Object[] run7za = new Andro7za().runListing7za(true, "l", "/storage/emulated/0/ftjj500.7z");
			System.out.println(run7za[0]);
			System.out.println(run7za[1]);
			Log.d("HelloJni", getFiles(new File("/data/data/com.free.p7zip"), false) + ".");
			run7za = new Andro7za().run7za(true, "i");
			tv.setText(run7za[1].toString());
			//com.free.util.CommandUtils.exec("/data/data/com.free.p7zip/").toString();
			setContentView(tv);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static Collection<File> getFiles(File f, boolean includeFolder) {
		Log.d("getFiles f", f.getAbsolutePath());
		final LinkedList<File> fList = new LinkedList<File>();
		if (f != null) {
			final LinkedList<File> folderQueue = new LinkedList<File>();
			if (f.isDirectory()) {
				if (includeFolder) {
					fList.add(f);
				}
				folderQueue.push(f);
			} else {
				fList.add(f);
			}
			File fi = null;
			File[] fs;
			while (folderQueue.size() > 0) {
				fi = folderQueue.pop();
				fs = fi.listFiles();
				if (fs != null) {
					for (File f2 : fs) {
						if (f2.isDirectory()) {
							folderQueue.push(f2);
							if (includeFolder) {
								fList.add(f2);
							}
						} else {
							fList.add(f2);
						}
					}
				}
			}
		}
		return fList;
	}
    static {
        //System.loadLibrary("7za");
    }
}
