package nyoibo.inkstone.upload.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
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
			// unZipFile(zip, filePath);
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
	public void unZipFile(File file, String filePath) throws ZipException, IOException {
		ZipFile zipFile = null;

		System.out.println("file path:" + filePath);

		String fileEncode = getFileEncode(file);
		System.out.println(fileEncode);
		System.out.println("file absolute path:" + file.getAbsolutePath());
		zipFile = new ZipFile(file.getAbsolutePath(), Charset.forName(fileEncode));

		Enumeration<?> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			System.out.println("Entry-Name:" + entry.getName());

			if (entry.isDirectory()) {
				unZipFolderNames.add(entry.getName());

				String dirPath = filePath + "/" + entry.getName();
				File dir = new File(dirPath);

				dir.mkdirs();
			} else {

				File targetFile = new File(filePath + "/" + entry.getName());
				if (file.getParentFile() != null && !file.getParentFile().exists()) {
					targetFile.getParentFile().mkdirs();
				}

				System.out.println("=absolute_path==" + targetFile.getAbsolutePath());
				File p = targetFile.getParentFile();
				System.out.println("parent:" + p.getAbsolutePath());
				System.out.println("parent-exist:" + p.exists());

				targetFile.createNewFile();
				InputStream is = zipFile.getInputStream(entry);
				FileOutputStream fos = new FileOutputStream(targetFile);

				int len;
				byte[] buf = new byte[2048];
				while ((len = is.read(buf)) != -1) {
					fos.write(buf, 0, len);
				}
				fos.flush();
				fos.close();
			}
		}

	}

	public void pack(File source, String dir, ZipArchiveOutputStream zipOutStream) throws IOException {
		if (source.isFile()) {
			zipOutStream.putArchiveEntry(new ZipArchiveEntry(source, dir));
			IOUtils.copy(new FileInputStream(source), zipOutStream);
			zipOutStream.closeArchiveEntry();
		} else {
			File[] subFiles = source.listFiles();
			if (subFiles != null && subFiles.length > 0) {
				for (File tmp : subFiles) {
					pack(tmp, dir + File.separator + tmp.getName(), zipOutStream);
				}
			} else {
				zipOutStream.putArchiveEntry(new ZipArchiveEntry(source, dir));
				zipOutStream.closeArchiveEntry();
			}
		}

	}

	public File packZip(File source, String packDir, String packName) {
		File target = new File(packDir);
		if (!target.exists()) {
			target.mkdirs();
		}
		target = new File(packDir + File.separator + packName);
		try {
			ZipArchiveOutputStream zipOutStream = new ZipArchiveOutputStream(target);
			pack(source, source.getName(), zipOutStream);
			closeStream(zipOutStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return target;
	}

	public static void closeStream(Object stream) {
		if (stream != null) {
			if (stream instanceof InputStream) {
				InputStream in = (InputStream) stream;
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (stream instanceof OutputStream) {
				OutputStream out = (OutputStream) stream;
				try {
					out.flush();
					out.close();
					out = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String unPackZip(String zipFileName, String outputDirectory) {
		String rootdir = "";
		File dir = new File(outputDirectory);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(zipFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ZipArchiveInputStream zis = new ZipArchiveInputStream(fis);
		try {
			ZipArchiveEntry zipEntry = zis.getNextZipEntry();
			while (zipEntry != null) {
				File zip = new File(outputDirectory + File.separator + zipEntry.getName());
				File parent = zip.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
				System.out.println(zip.getName());
				FileOutputStream fos = new FileOutputStream(zip.getParent() + File.separator + zip.getName(), false);
				byte[] buffer = new byte[1024];
				int i = zis.read(buffer);
				while (i > 0) {
					fos.write(buffer, 0, i);
					i = zis.read(buffer);
				}
				closeStream(fos);
				zipEntry = zis.getNextZipEntry();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		File root = new File(zipFileName);
		String rootname = root.getName();
		rootname = rootname.substring(0, rootname.lastIndexOf("."));
		rootdir = outputDirectory + File.separator + rootname;
		return rootdir;
	}
  
	public static void main(String[] args) {
		System.out.println(File.separator);
		new FileZipUtils().unPackZip("D:\\nyoibo_automation\\5. Finished Editing-20190528T051007Z-001.zip",
				"D:\\nyoibo_automation");
	}

}
