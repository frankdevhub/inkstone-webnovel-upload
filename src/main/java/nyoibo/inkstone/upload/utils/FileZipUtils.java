package nyoibo.inkstone.upload.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.codehaus.plexus.util.FileUtils;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;

public class FileZipUtils {
	private static final String ZIP_SUFFIX = "zip";
	private ArrayList<File> unZipFiles = new ArrayList<File>();
	private ArrayList<String> unZipFolderNames = new ArrayList<String>();

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

		for (File zip : unZipFiles) {
			System.out.println(filePath);
			unZipFile(zip, filePath);
			FileUtils.forceDelete(zip);
		}

	}

	public String getFileEncode(File file) {
		String encode = "UTF-8";
		Charset charset = null;
		try {
			CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
			charset = detector.detectCodepage(file.toURI().toURL());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (charset != null) {
			encode = charset.name();
		}
		return encode;
	}

	@SuppressWarnings("resource")
	private void unZipFile(File file, String filePath) throws ZipException, IOException {
		ZipFile zipFile = null;

		System.out.println("file path:" + filePath);

		String fileEncode = getFileEncode(file);
		System.out.println(fileEncode);
		zipFile = new ZipFile(file.getAbsolutePath(), Charset.forName(fileEncode));

		Enumeration<?> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();

			System.out.println("Entry-Name:"+entry.getName());
			
			if (entry.isDirectory()) {
				unZipFolderNames.add(entry.getName());

				String dirPath = filePath + "/" + entry.getName();
				File dir = new File(dirPath);

				dir.mkdirs();
			} else {

				File targetFile = new File(filePath + "/" + entry.getName());
				if (!targetFile.getParentFile().exists()) {
					targetFile.getParentFile().mkdirs();
				}

				System.out.println("=absolute_path==" + targetFile.getAbsolutePath());
				File p =targetFile.getParentFile();
				System.out.println("parent:"+ p.getAbsolutePath());
				System.out.println("parent-exist:"+p.exists());
				
				System.out.println(targetFile.exists());
				
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

}
