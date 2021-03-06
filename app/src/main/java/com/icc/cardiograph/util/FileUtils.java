package com.icc.cardiograph.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

/**
 * 文件操作工具包
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class FileUtils {
	/**
	 * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
	 * 
	 * @param context
	 * @param msg
	 */
	public static void write(Context context, String fileName, String content) {
		if (content == null) {
			content = "";
		}

		try {
			FileOutputStream fos = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			fos.write(content.getBytes());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取文本文件
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String read(Context context, String fileName) {
		try {
			FileInputStream in = context.openFileInput(fileName);
			return readInStream(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Stream 转换成 String
	 * 
	 * @param inStream
	 *            {@link java.io.FileInputStream}
	 * @return
	 */
	private static String readInStream(FileInputStream inStream) {
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}
			outStream.close();
			inStream.close();
			return outStream.toString();
		} catch (IOException e) {
			Log.i("FileTest", e.getMessage());
		}
		return null;
	}

	/**
	 * 创建文件
	 * 
	 * @param folderPath
	 * @param fileName
	 * @return
	 */
	public static File createFile(String folderPath, String fileName) {
		File destDir = new File(folderPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		return new File(folderPath, fileName);
	}

//	/**
//	 * 向手机写图片
//	 * 
//	 * @param buffer
//	 * @param folder
//	 * @param fileName
//	 * @return
//	 */
//	public static boolean writeFile(byte[] buffer, String folder,
//			String fileName) {
//		boolean writeSucc = false;
//		boolean sdCardExist = IOUtil.isSdCardAvailable();
//		String folderPath = "";
//		if (sdCardExist) {
//			folderPath = Environment.getExternalStorageDirectory()
//					+ File.separator + folder + File.separator;
//		} else {
//			writeSucc = false;
//		}
//
//		File fileDir = new File(folderPath);
//		if (!fileDir.exists()) {
//			fileDir.mkdirs();
//		}
//
//		File file = new File(folderPath + fileName);
//		FileOutputStream out = null;
//		try {
//			out = new FileOutputStream(file);
//			out.write(buffer);
//			writeSucc = true;
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (out != null) {
//					out.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//		return writeSucc;
//	}
//
//	/**
//	 * 根据文件绝对路径获取文件名
//	 * 
//	 * @param filePath
//	 * @return
//	 */
//	public static String getFileName(String filePath) {
//		// if( isEmpty(filePath) ) return "";
//		// return filePath.substring( filePath.lastIndexOf( File.separator )+1
//		// );
//
//		return isEmpty(filePath) ? "" : filePath.substring(filePath
//				.lastIndexOf(File.separator) + 1);
//	}
//
//	/**
//	 * 根据文件的绝对路径获取文件名但不包含扩展名
//	 * 
//	 * @param filePath
//	 * @return
//	 */
//	public static String getFileNameNoFormat(String filePath) {
//		if (isEmpty(filePath)) {
//			return "";
//		}
//		int start = filePath.lastIndexOf(File.separator) + 1;
//		int end = filePath.lastIndexOf('.');
//		return filePath.substring(start, end);
//	}
//
//	/**
//	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
//	 * 
//	 * @param input
//	 * @return boolean
//	 */
//	public static boolean isEmpty(String input) {
//		if (input == null || "".equals(input))
//			return true;
//
//		for (int i = 0; i < input.length(); i++) {
//			char c = input.charAt(i);
//			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	/**
//	 * 获取文件扩展名
//	 * 
//	 * @param fileName
//	 * @return
//	 */
//	public static String getFileFormat(String fileName) {
//		if (isEmpty(fileName)) {
//			return "";
//		}
//		int start = fileName.lastIndexOf('.');
//		return fileName.substring(start + 1);
//	}
//
//	/**
//	 * 获取文件大小
//	 * 
//	 * @param filePath
//	 * @return
//	 */
//	public static long getFileSize(String filePath) {
//		long size = 0;
//		File file = new File(filePath);
//		if (file != null && file.exists() && file.isFile()) {
//			size = file.length();
//		}
//		return size;
//	}
//
//	/**
//	 * 获取文件大小
//	 * 
//	 * @param size
//	 *            字节
//	 * @return
//	 */
//	public static String getFileSize(long size) {
//		if (size <= 0)
//			return "0";
//		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
//		float temp = (float) size / 1024;
//		if (temp >= 1024) {
//			return df.format(temp / 1024) + "M";
//		} else {
//			return df.format(temp) + "K";
//		}
//	}
//
//	/**
//	 * 转换文件大小
//	 * 
//	 * @param fileS
//	 * @return B/KB/MB/GB
//	 */
//	public static String formatFileSize(long fileS) {
//		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
//		String fileSizeString = "";
//		if (fileS < 1024) {
//			fileSizeString = df.format((double) fileS) + "B";
//		} else if (fileS < 1048576) {
//			fileSizeString = df.format((double) fileS / 1024) + "KB";
//		} else if (fileS < 1073741824) {
//			fileSizeString = df.format((double) fileS / 1048576) + "MB";
//		} else {
//			fileSizeString = df.format((double) fileS / 1073741824) + "G";
//		}
//		return fileSizeString;
//	}
//
//	/**
//	 * 获取目录文件大小
//	 * 
//	 * @param dir
//	 * @return
//	 */
//	public static long getDirSize(File dir) {
//		if (dir == null) {
//			return 0;
//		}
//		if (!dir.isDirectory()) {
//			return 0;
//		}
//		long dirSize = 0;
//		File[] files = dir.listFiles();
//		for (File file : files) {
//			if (file.isFile()) {
//				dirSize += file.length();
//			} else if (file.isDirectory()) {
//				dirSize += file.length();
//				dirSize += getDirSize(file); // 递归调用继续统计
//			}
//		}
//		return dirSize;
//	}
//
//	/**
//	 * 获取目录文件个数
//	 * 
//	 * @param f
//	 * @return
//	 */
//	public long getFileList(File dir) {
//		long count = 0;
//		File[] files = dir.listFiles();
//		count = files.length;
//		for (File file : files) {
//			if (file.isDirectory()) {
//				count = count + getFileList(file);// 递归
//				count--;
//			}
//		}
//		return count;
//	}
//
//	public static byte[] toBytes(InputStream in) throws IOException {
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		int ch;
//		while ((ch = in.read()) != -1) {
//			out.write(ch);
//		}
//		byte buffer[] = out.toByteArray();
//		out.close();
//		return buffer;
//	}
//
//	/**
//	 * 检查文件是否存在
//	 * 
//	 * @param name
//	 * @return
//	 */
//	public static boolean checkFileExists(String name) {
//		boolean status;
//		if (!name.equals("")) {
//			File path = Environment.getExternalStorageDirectory();
//			File newPath = new File(path.toString() + name);
//			status = newPath.exists();
//		} else {
//			status = false;
//		}
//		return status;
//
//	}
//
//	/**
//	 * 计算SD卡的剩余空间
//	 * 
//	 * @return 返回-1，说明没有安装sd卡
//	 */
//	public static long getFreeDiskSpace() {
//		boolean existCard = IOUtil.isSdCardAvailable();
//		long freeSpace = 0;
//		if (existCard) {
//			try {
//				File path = Environment.getExternalStorageDirectory();
//				StatFs stat = new StatFs(path.getPath());
//				long blockSize = stat.getBlockSize();
//				long availableBlocks = stat.getAvailableBlocks();
//				freeSpace = availableBlocks * blockSize / 1024;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} else {
//			return -1;
//		}
//		return (freeSpace);
//	}
//
//	/**
//	 * 新建目录
//	 * 
//	 * @param directoryName
//	 * @return
//	 */
//	public static boolean createDirectory(String directoryName) {
//		boolean status;
//		// if (!directoryName.equals("")) {
//		if (!isEmpty(directoryName)) {
//			File path = Environment.getExternalStorageDirectory();
//			File newPath = new File(path.toString() + directoryName);
//			status = newPath.mkdir();
//			status = true;
//		} else
//			status = false;
//		return status;
//	}
//
//	/**
//	 * 检查是否安装SD卡
//	 * 
//	 * @return
//	 */
//	public static boolean checkSaveLocationExists() {
//		String sDCardStatus = Environment.getExternalStorageState();
//		boolean status;
//		if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
//			status = true;
//		} else
//			status = false;
//		return status;
//	}
//
//	/**
//	 * 删除目录(包括：目录里的所有文件)
//	 * 
//	 * @param fileName
//	 * @return
//	 */
//	public static boolean deleteDirectory(String fileName) {
//		boolean status;
//		SecurityManager checker = new SecurityManager();
//
//		if (!fileName.equals("")) {
//
//			File path = Environment.getExternalStorageDirectory();
//			File newPath = new File(path.toString() + fileName);
//			checker.checkDelete(newPath.toString());
//			if (newPath.isDirectory()) {
//				String[] listfile = newPath.list();
//				// delete all files within the specified directory and then
//				// delete the directory
//				try {
//					final int length = listfile.length;
//					for (int i = 0; i < length; i++) {
//						final String name = listfile[i];
//						File deletedFile = new File(newPath.toString()
//								+ File.separator + name);
//						if (deletedFile.isDirectory()) {//如果是目录，递归删除
//							deleteDirectory(fileName + File.separator + name);
//						} else {
//							deletedFile.delete();
//						}
//					}
//					newPath.delete();
//					Log.i("DirectoryManager deleteDirectory", fileName);
//					status = true;
//				} catch (Exception e) {
//					e.printStackTrace();
//					status = false;
//				}
//
//			} else
//				status = false;
//		} else
//			status = false;
//		return status;
//	}
//
//	/**
//	 * 删除文件
//	 * 
//	 * @param fileName
//	 * @return
//	 */
//	public static boolean deleteFile(String fileName) {
//		boolean status;
//		SecurityManager checker = new SecurityManager();
//
//		if (!fileName.equals("")) {
//
//			File path = Environment.getExternalStorageDirectory();
//			File newPath = new File(path.toString() + fileName);
//			checker.checkDelete(newPath.toString());
//			if (newPath.isFile()) {
//				try {
//					Log.i("DirectoryManager deleteFile", fileName);
//					newPath.delete();
//					status = true;
//				} catch (SecurityException se) {
//					se.printStackTrace();
//					status = false;
//				}
//			} else
//				status = false;
//		} else
//			status = false;
//		return status;
//	}
//
//	/**
//	 * 复制单个文件
//	 * 
//	 * @param oldPath
//	 *            String 原文件路径 如：c:/fqf.txt
//	 * @param newPath
//	 *            String 复制后路径 如：f:/fqf.txt
//	 * @return boolean
//	 */
//	public static void copyFile(String oldPath, String newPath) {
//		try {
//			int bytesum = 0;
//			int byteread = 0;
//			File oldfile = new File(oldPath);
//			if (!oldfile.exists()) { // 文件不存在时
//				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
//				FileOutputStream fs = new FileOutputStream(newPath);
//				byte[] buffer = new byte[1444];
//				// int length;
//				while ((byteread = inStream.read(buffer)) != -1) {
//					bytesum += byteread; // 字节数 文件大小
//					fs.write(buffer, 0, byteread);
//				}
//				inStream.close();
//				fs.close();
//			}
//		} catch (Exception e) {
//			new Debugger().log(e, "copy file fail");
//		}
//	}
//
//	/**
//	 * 复制整个文件夹内容
//	 * 
//	 * @param oldPath
//	 *            String 原文件路径 如：c:/fqf
//	 * @param newPath
//	 *            String 复制后路径 如：f:/fqf/ff
//	 * @return boolean
//	 */
//	public static void copyFolder(String oldPath, String newPath) {
//
//		try {
//			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
//			File a = new File(oldPath);
//			String[] file = a.list();
//			File temp = null;
//			for (int i = 0; i < file.length; i++) {
//				if (oldPath.endsWith(File.separator)) {
//					temp = new File(oldPath + file[i]);
//				} else {
//					temp = new File(oldPath + File.separator + file[i]);
//				}
//
//				if (temp.isFile()) {
//					FileInputStream input = new FileInputStream(temp);
//					FileOutputStream output = new FileOutputStream(newPath
//							+ "/" + (temp.getName()).toString());
//					byte[] b = new byte[1024 * 5];
//					int len;
//					while ((len = input.read(b)) != -1) {
//						output.write(b, 0, len);
//					}
//					output.flush();
//					output.close();
//					input.close();
//				}
//				if (temp.isDirectory()) {// 如果是子文件夹
//					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
//					Util.log("FileUtils", "oldPath+/+file[i]  " + oldPath + "/"
//							+ file[i]);
//					Util.log("FileUtils", "newPath+/+file[i]  " + newPath + "/"
//							+ file[i]);
//				}
//			}
//		} catch (Exception e) {
//			new Debugger().log(e, "copy dir fail");
//		}
//	}
	
	public static List<Map<String,Object>> findFile(File file) {
		List<Map<String,Object>> lstFile = new ArrayList<Map<String,Object>>();
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].exists() && files[i].isFile()) {
				Map<String, Object> cp = new HashMap<String, Object>();
				cp.put("photo", android.R.drawable.btn_star_big_on);
				cp.put("name", files[i].getName());
				cp.put("cbx", "");
				lstFile.add(cp);
				System.out.println("文件名:" + "\t" + files[i].getName());
			}
		}
		return lstFile;
	}
}