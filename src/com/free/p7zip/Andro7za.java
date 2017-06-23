package com.free.p7zip;

import java.io.File;

import android.util.Log;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import com.free.util.*;

/**
 * <code>Andro7za</code> provided the 7za JNI interface.
 * 
 */
public final class Andro7za {
	private static String JNI_TAG = "7zaJNI";

	//	7z <command> [<switch>...] <base_archive_name> [<arguments>...]
	//	<arguments> ::= <switch> | <wildcard> | <filename> | <list_file>
	//	<switch>::= <switch_symbol><switch_characters>[<option>]
	//	<switch_symbol> ::= '/' | '-' 
	//	<list_file> ::= @{filename}
	/**
	 * command a x l
	 * archive 7z zip
	 * -t7z -tzip |  | 
	 * pPassword | -pPassword | -pPassword
	 * compression level | outputDir  |
	 * pathToCompress/fList | fileToExtract/fList |  
	 * 
	 * In compress use type. In extract use overwrite mode
	 *   -aoa	Overwrite All existing files without prompt.
	 *   -aos	Skip extracting of existing files.
	 *   -aou	aUto rename extracting file (for example, name.txt will be renamed to name_1.txt).
	 *   -aot	auto rename existing file (for example, name.txt will be renamed to name_1.txt).
	 *
	 Code Meaning
	 0 No error
	 1 Warning (Non fatal error(s)). For example,
	 one or more files were locked by some other
	 application, so they were not compressed.
	 2 Fatal error
	 7 Command line error
	 8 Not enough memory for operation
	 255 User stopped the process
	 */
	 
	public native int a7zaCommandAll(String... command7z);
	
	public native int a7zaCommand(String command7z, 
			String archive7z, 
			String type, 
			String password, 
			String compressionLevelOrOutputDir, 
			String exclude, 
			String fList4CompressOrExtract);

	public native int a7zaCommand2(
			String command7z, //1
			String archive7z, //2
			String type, //3
			String password, //4
			String compressionLevelOrOutputDir, //5
			String volume, //6
			String sortMethod, //-mqs=on //7
			String encryptFileName, //he=[off | on] //8
			//Enables or disables archive header encryption. The default mode is he=off. 
			String updateMode, // add and update: -up1q1r2x1y2z1 //9
			// add and replace: p1q1r2x2y2z1
			// fresh p1q1r0x1y2z1
			// synchronize p1q0r2x1y2z1
			// add    p1q1r2x2y2z2
			// update p1q1r2x1y2z1
			String pathMode, //10 // -spf Use absolute paths including drive letter. 
			//-spf2 Use full paths without drive letter. 
			// default is relative
			String solid, //11//-ms=off|one // Use a separate solid block for each new file extension (e)
			String compressSharedFiles, //12// -ssw (Compress files open for writing) switch. 
			// If this switch is not set, 7-zip doesn't include such files to archive.
			String fList4CompressOrExtract,//13
			String exclude, //14
			String a1,
			String a2,
			String a3,
			String a4
			);

	public native String stringFromJNI(String outfile, String infile);
	public native void closeStreamJNI();

	public static final String PRIVATE_PATH = "/sdcard/.com.free.p7zip";
	private String mOutfile = PRIVATE_PATH + "/7zaOut.txt";
	private String mInfile = PRIVATE_PATH + "/7zaIn.txt";
	private String listFile = PRIVATE_PATH + "/7zaFileList.txt";
	
	public Andro7za() {
		String sPath = PRIVATE_PATH;
		mOutfile = sPath + "/7zaOut.txt";
		mInfile = sPath + "/7zaIn.txt";
		listFile = sPath + "/7zaFileList.txt";
	}
	
	public Andro7za(String logPath) {
		mOutfile = logPath + "/7zaOut.txt";
		mInfile = logPath + "/7zaIn.txt";
		listFile = logPath + "/7zaFileList.txt";
	}
	
	public void initStream() throws IOException {
		resetFile(mOutfile);
		resetFile(mInfile);
		resetFile(listFile);
		stringFromJNI(mOutfile, mInfile);
	}

	private void resetFile(String f) throws IOException {
		File file = new File(f);
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		} else {
			file.delete();
		}
		file.createNewFile();
	}

	public Object[] run7za(boolean showDebug, String... args) throws IOException {
		try {
			initStream();
			
			Log.d(JNI_TAG, "Call run7za(): " + args[0]);
			int ret = a7zaCommandAll(args);
			Log.d(JNI_TAG, "run7za() ret " + ret);
			FileReader fileReader = new FileReader(mOutfile);
			BufferedReader br = new BufferedReader(fileReader, 32768);
			StringBuilder sb = new StringBuilder();
			if (!showDebug) {
				while (br.ready()) {
					sb.append(br.readLine()).append("\n");
				}
			} else {
				String readLine;
				while (br.ready()) {
					readLine = br.readLine();
					Log.d(JNI_TAG, readLine);
					sb.append(readLine).append("\n");
				}
			}
			return new Object[] {ret, sb};
		} finally {
			closeStreamJNI();
		}
	}
	
	public Object[] runListing7za(boolean showDebug, String... args) throws IOException {
		Object[] run7za = run7za(showDebug, args);
		String stRet = run7za[1].toString();
		Collection<String> nameList = new HashSet<String>();
		String line ="";
		BufferedReader br = new BufferedReader(new StringReader(stRet));
		int count = 0;
		//System.out.println(ENTRY_PATTERN);
		while (count < 2 && line != null) {
			line = br.readLine();
			if (line == null) {
				break;
			}
			//System.out.println(line);
			if ("------------------- ----- ------------ ------------  ------------------------".equals(line)) {
				count++;
			}
			if (count == 1) {
				Matcher matcher = ENTRY_PATTERN.matcher(line);
				//System.out.println(line);
				if (matcher.matches()) {
					if ("D".equals(matcher.group(1))) {
						nameList.add(matcher.group(2) + "/");
					} else {
						nameList.add(matcher.group(2));
					}
				}
			}
		}
		br.close();
		//Collections.sort(nameList);
		//Log.i("nameList", collectionToString(nameList, true, "\n"));
		return new Object[] {run7za[0], nameList};
	}
	
	final private static StringBuilder sb = new StringBuilder();
	public String read() throws IOException {
		synchronized (sb) {
			sb.setLength(0);
			BufferedReader in = null;
			try {
				//initStream(); // not check
				in = new BufferedReader(new FileReader(mOutfile));
				while(in.ready()) {
					String readLine = in.readLine();
					if (readLine == null || "".equals(readLine)) {
						in.close();
						closeStreamJNI();
						break;
					} else {
						sb.append(readLine);
					}
				}
				return sb.toString();
			} finally {
				FileUtil.close(in);
			}
		}
	}

	public void write(String content) {
		BufferedWriter out = null;
		try {
			//initStream(); // not check
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mInfile)));
			out.write(content.toCharArray(), 0, content.toCharArray().length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			FileUtil.flushClose(out);
		}
	}

	public int compress(String pathArchive7z, String type, String password, String compressLevel, String pathToCompress) {
		if (type == null) {
			type = "";
		}
		if (password == null || password.trim().length() == 0) {
			password = "";
		} else {
			password = "-p" + password;
		}
		if (compressLevel == null) {
			compressLevel = "";
		}
		try {
			initStream();
			Log.d(JNI_TAG, "Call a7zaCommand(), compress: a " + pathArchive7z + " " + type + " " + password + " " + compressLevel + " " + pathToCompress);
			int ret = a7zaCommand("a", pathArchive7z, type, password, compressLevel, pathToCompress, "");
			Log.d(JNI_TAG, "a7zaCommand() compress ret " + ret + ", file: " + pathArchive7z);
			return ret;
		} catch (IOException e) {
			return 2;
		} finally {
			closeStreamJNI();
		}
	}

	public int compress(String pathArchive7z, String type, String password, String compressLevel, Collection<String> fileList) throws IOException {
		if (type == null) {
			type = "";
		}
		if (password == null || password.trim().length() == 0) {
			password = "";
		} else {
			password = "-p" + password;
		}
		if (compressLevel == null) {
			compressLevel = "";
		}
		if (fileList.size() > 0) {
			String outputList = Util.collectionToString(fileList, false, "\n");
			FileUtil.stringToFile(listFile, outputList);
			
			Log.d(JNI_TAG, "Call a7zaCommand(), compress: \"" + fileList + " to " + pathArchive7z + "\" level " + compressLevel);
			int ret = a7zaCommand("a",pathArchive7z, type, password, compressLevel, "@" + listFile, "");
			Log.d(JNI_TAG, "a7zaCommand() compress ret " + ret + ", file: " + pathArchive7z);
			new File(listFile).delete();
			return ret;
		} else {
			return -1;
		}
	}
	
	public int compress(
			String archiveName, 
			//String type, 
			String password, 
			String level, 
			List<String> fileList, 
			String volume, 
			String excludes) 
					throws IOException {

		Log.i(JNI_TAG, archiveName+","+
				//String type, 
				//password +","+
				level +","+
				fileList + "," +
				volume +","+
				excludes);
		if (password == null || password.trim().length() == 0) {
			password = "";
		} else {
			password = "-p" + password;
		}
		if (volume == null || volume.trim().length() == 0) {
			volume = "";
		} else {
			volume = "-v" + volume ;
		}
		String fileListTmp = archiveName + ".tmp";
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileListTmp));
		try {
			for (String st : fileList) {
				bw.write(st);
				bw.newLine();
			}
		} finally {
			bw.flush();
			bw.close();
		}

		String excludesTmp = "";
		if (excludes != null && excludes.trim().length() > 0) {
			excludesTmp = archiveName + ".exc." + System.currentTimeMillis();
			bw = new BufferedWriter(new FileWriter(excludesTmp));
			try {
				List<String> l = Util.stringToList(excludes, "[\r\n]+");
				for (String st : l) {
					bw.write(st);
					bw.newLine();
				}
			} finally {
				bw.flush();
				bw.close();
			}
		}
		int ret = 0;
		if (Util.isEmpty(excludes)) {
			Log.d(JNI_TAG, "empty excludes");
			ret = a7zaCommand("a",archiveName, password, "@" + fileListTmp, level, volume, "");
		} else {
			Log.d(JNI_TAG, "all params");
			ret = a7zaCommand("a",archiveName, password, "@" + fileListTmp, level, volume, "-xr@"+excludesTmp);
		}
		Log.d(JNI_TAG, "a7zaCommand() compress ret " + ret + ", file: " + archiveName);
		Log.i(JNI_TAG, archiveName+","+
				//String type, 
				//password +","+
				level +","+
				fileList + "," +
				volume +","+
				excludes);
		new File(fileListTmp).delete();
		new File(excludesTmp).delete();
		return ret;
	}

	// extract all
	public int extract(String pathArchive7z, String overwriteMode, String password, String pathToExtract) {

		if (Util.isEmpty(overwriteMode)) {
			overwriteMode = "-aos";
		}

		if (password == null || password.trim().length() == 0) {
			password = "";
		} else {
			password = "-p" + password;
		}

		File f = new File(pathToExtract);
		if (!f.exists()) {
			f.mkdirs();
		}
		pathToExtract = "-o" + pathToExtract;

		Log.d(JNI_TAG, "Call a7zaCommand(), extracting: \"" + pathArchive7z + "\" to " + pathToExtract);
		int ret = a7zaCommand("x", pathArchive7z, overwriteMode, password, pathToExtract, "", "");
		Log.d(JNI_TAG, "a7zaCommand() extracting ret " + ret + ", file: " + pathArchive7z);
		return ret;
	}

	// extract nhieu file
	public int extract(String pathArchive7z, String overwriteMode, String password, String pathToExtract, Collection<String> fileList) throws IOException {
		if (fileList.size() > 0) {
			if (Util.isEmpty(overwriteMode)) {
				overwriteMode = "-aos";
			}

			if (password == null || password.trim().length() == 0) {
				password = "";
			} else {
				password = "-p" + password;
			}

			File f = new File(pathToExtract);
			if (!f.exists()) {
				f.mkdirs();
			}
			pathToExtract = "-o" + pathToExtract;

			String outputList = Util.collectionToString(fileList, false, "\n");
			File outListF = new File(listFile);
			outListF.delete();
			FileUtil.stringToFile(listFile, outputList);

			Log.d(JNI_TAG, "Call a7zaCommand(), extract: \"" + fileList + " in " + pathArchive7z + "\" to " + pathToExtract);
			int ret = a7zaCommand("x",pathArchive7z, overwriteMode, password, pathToExtract, "@" + listFile, "");
			Log.d(JNI_TAG, "a7zaCommand() extract ret " + ret + ", file: " + pathArchive7z);
			return ret;
		} else {
			return -1;
		}
	}

	// extract 1 file
	public int extract(String pathArchive7z, String overwriteMode, String password, String pathToExtract, String fileName) {
		if (Util.isEmpty(overwriteMode)) {
			overwriteMode = "-aos";
		}

		if (password == null || password.trim().length() == 0) {
			password = "";
		} else {
			password = "-p" + password;
		}

		File f = new File(pathToExtract);
		if (!f.exists()) {
			f.mkdirs();
		}
		pathToExtract = "-o" + pathToExtract;

		Log.d(JNI_TAG, "Call a7zaCommand(), extract: \"" + fileName + " in " + pathArchive7z + "\" to " + pathToExtract);
		int ret = a7zaCommand("x",pathArchive7z, overwriteMode, password, pathToExtract, fileName, "");
		Log.d(JNI_TAG, "a7zaCommand() extract ret " + ret + ", file: " + pathArchive7z);
		return ret;
	}

	// extract with include and exclude
	public int extractInEx(
			String zArchive, 
			String password, 
			String overwriteMode, 
			String pathToExtract, 
			String includes, 
			String excludes
			) 
					throws IOException {

		Log.i(JNI_TAG, zArchive+","+
				//password +","+
				overwriteMode +","+
				pathToExtract +","+
				includes +","+
				excludes);
		
		if (password == null || password.trim().length() == 0) {
			password = "";
		} else {
			password = "-p" + password;
		}

		if (Util.isEmpty(overwriteMode)) {
			overwriteMode = "-aos";
		}

		File f = new File(pathToExtract);
		if (!f.exists()) {
			f.mkdirs();
		}
		pathToExtract = "-o" + pathToExtract;

		String includesTmp = "";
		if (includes != null && includes.trim().length() > 0) {
			includesTmp = zArchive + ".inc." + System.currentTimeMillis();
			BufferedWriter bw = new BufferedWriter(new FileWriter(includesTmp));
			try {
				List<String> l = Util.stringToList(includes, "[\r\n]+");
				for (String st : l) {
					bw.write(st);
					bw.newLine();
				}
			} finally {
				bw.flush();
				bw.close();
			}
		}

		String excludesTmp = "";
		if (excludes != null && excludes.trim().length() > 0) {
			excludesTmp = zArchive + ".exc." + System.currentTimeMillis();BufferedWriter bw = new BufferedWriter(new FileWriter(excludesTmp));
			try {
				List<String> l = Util.stringToList(excludes, "[\r\n]+");
				for (String st : l) {
					bw.write(st);
					bw.newLine();
				}
			} finally {
				bw.flush();
				bw.close();
			}
		}
		int ret = 0;
		Log.d(JNI_TAG, "overwrite mode" + overwriteMode);
		if (Util.isEmpty(includes) && Util.isEmpty(excludes)) {
			ret = a7zaCommand("x",zArchive, password, pathToExtract, overwriteMode, "", "");
		} else if (Util.isEmpty(includes)) {
			ret = a7zaCommand("x",zArchive, password, pathToExtract, overwriteMode, "-xr@" + excludesTmp, "");
		} else if (Util.isEmpty(excludes)) {
			ret = a7zaCommand("x",zArchive, password, pathToExtract, overwriteMode, "-ir@" + includesTmp, "");
		} else {
			ret = a7zaCommand("x",zArchive, password, pathToExtract, overwriteMode, "-ir@" + includesTmp, "-xr@"+excludesTmp);
		}
		Log.d(JNI_TAG, "a7zaCommand() compress ret " + ret + ", file: " + zArchive);
		new File(includesTmp).delete();
		new File(excludesTmp).delete();
		return ret;
	}

	// ------------------- ----- ------------ ------------  ------------------------
	// 2016-03-13 22:49:48 ....A         4212               jni/CPP/myWindows/makefile.depend
	// 2016-03-13 23:44:49 D....            0            0  gen/com/hostzi
	// 2015-08-02 17:49:11 .R..A         9405               CPP/Windows/Window.h
	//                     .....                            p7zip_15.14_src_all.tar
	private final static Pattern ENTRY_PATTERN = Pattern.compile("[ \\d]{4}[-/ ][ \\d]{2}[- /][ \\d]{2} [ \\d]{2}[ :][ \\d]{2}[ :][ \\d]{2} ([D\\.]).{3}[A\\.] [ \\d]{12}[ \\d]{15}([^\r\n]+)", Pattern.UNICODE_CASE);

	public Collection<String> listing(File archive7z, String password) throws IOException {
		return listing(archive7z.getAbsolutePath(), password);
	}

	public Collection<String> listing(String pathArchive7z, String password) throws IOException {
		try {
			initStream();
			if (password == null || password.trim().length() == 0) {
				password = "";
			} else {
				password = "-p" + password;
			}
			Log.d(JNI_TAG, "Call a7zaCommand(), listing: " + pathArchive7z);
			int ret = a7zaCommand("l", pathArchive7z, "", password, "", "", "");
			Log.d(JNI_TAG, "a7zaCommand() listing ret " + ret + ", file: " + pathArchive7z);

			Collection<String> nameList = new HashSet<String>();
			String line ="";
			FileReader fileReader = new FileReader(mOutfile);
			BufferedReader br = new BufferedReader(fileReader, 32768);
			int count = 0;
			while (br.ready() && count < 2) {
				line = br.readLine();
				//System.out.println(line);
				if ("------------------- ----- ------------ ------------  ------------------------".equals(line)) {
					count++;
				}
				if (count == 1) {
					Matcher matcher = ENTRY_PATTERN.matcher(line);
					//				System.out.println(line);
					//				System.out.println(ENTRY_PATTERN);
					if (matcher.matches()) {
						if ("D".equals(matcher.group(1))) {
							nameList.add(matcher.group(2) + "/");
						} else {
							nameList.add(matcher.group(2));
						}
					}
				}
			}
			br.close();
			fileReader.close();
			//Collections.sort(nameList);
			//			Log.i("nameList", collectionToString(nameList, true, "\n"));
			return nameList;
		} finally {
			closeStreamJNI();
		}
	}

	static {
		// Dynamically load stl_port, see jni/Application.mk
		// System.loadLibrary("stlport_shared");
		System.loadLibrary("7za");
	}
}
