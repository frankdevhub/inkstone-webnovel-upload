package nyoibo.inkstone.upload.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * <p>Title:DriveZipUtils.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-23 17:17
 */

public class DriveZipUtils {
	private static final String ZIP_SUFFIX = "zip";
    private ArrayList<File> unZipFiles = new ArrayList<File>();
	
	public void unZipDriveZip(String filePath) throws Exception {
		boolean hasZip = false;

		File downloadZipDir = new File(filePath);

		if (!downloadZipDir.exists()) {
			throw new Exception(String.format("File path [%s] does not exist", filePath));
		}

		File[] fileList = downloadZipDir.listFiles();
		for (File file : fileList) {
			if (file.isFile()) {
				String fileName = file.getName();
				String out = fileName.substring(fileName.lastIndexOf(".") + 1);
				if (out.equals(ZIP_SUFFIX)) {
					hasZip = true;
					unZipFiles.add(file);
				}
			}
		}

		if (!hasZip)
			throw new Exception(String.format("Cannot find zip file in path [%s]", filePath));

		for (File zip : unZipFiles)
			unZipFile(zip, filePath);

	}

	
	private void unZipFile(File file, String filePath) throws ZipException, IOException {
		ZipFile zipFile = null;
		zipFile = new ZipFile(file);

		Enumeration<?> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();

			if (entry.isDirectory()) {
				String dirPath = filePath + "/" + entry.getName();
				File dir = new File(dirPath);

				dir.mkdirs();
			} else {
				File targetFile = new File(filePath + "/" + entry.getName());
				if (!targetFile.getParentFile().exists()) {
					targetFile.getParentFile().mkdirs();
				}

				targetFile.createNewFile();
				InputStream is = zipFile.getInputStream(entry);
				FileOutputStream fos = new FileOutputStream(targetFile);

				int len;
				byte[] buf = new byte[2048];
				while ((len = is.read(buf)) != -1) {
					fos.write(buf, 0, len);
				}
				fos.close();
				fos.flush();
			}
		}
		 
	}
	
	
	public static void main(String[] args) throws Exception {
		new DriveZipUtils().unZipDriveZip("D:\\蜜爱1V1-首席宠上天");
	}
}
