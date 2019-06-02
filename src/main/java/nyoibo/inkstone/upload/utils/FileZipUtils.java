package nyoibo.inkstone.upload.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.util.FileUtils;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.message.MessageMethod;

public class FileZipUtils {

	private static final String ZIP_SUFFIX = "zip";
	private ArrayList<File> unZipFiles = new ArrayList<File>();
	private ArrayList<String> unZipFolderNames = new ArrayList<String>();
	private ArrayList<String> failedUnZipNames = new ArrayList<String>();

	private final Logger LOGGER = LoggerFactory.getLogger(FileZipUtils.class);

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

	public ArrayList<String> unZipFile(File file, String filePath) throws ZipException, IOException {
		unZipFolderNames = new ArrayList<String>();
		failedUnZipNames = new ArrayList<String>();

		ZipFile zipFile = null;
		LOGGER.begin().headerAction(MessageMethod.EVENT).info(String.format("zip path is :[%s]", filePath));
		String fileEncode = getFileEncode(file);
		zipFile = new ZipFile(file.getAbsolutePath(), Charset.forName(fileEncode));
		Enumeration<?> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			String fileName = reFormatPath(entry.getName());
			InputStream is = null;
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			byte[] buf = new byte[2048];

			if (entry.isDirectory()) {
				unZipFolderNames.add(fileName);
				String dirPath = filePath + File.separator + fileName;
				File dir = new File(dirPath);
				dir.mkdirs();
			} else {
				File targetFile = new File(filePath + File.separator + fileName);
				try {
					if (targetFile.getParentFile() != null && !targetFile.getParentFile().exists()) {
						createParentPath(targetFile.getAbsolutePath());
					}
					targetFile.createNewFile();

				} catch (Exception e) {
					e.printStackTrace();
					failedUnZipNames.add(targetFile.getAbsolutePath());
				} finally {
					is = zipFile.getInputStream(entry);
					fos = new FileOutputStream(targetFile);
					bos = new BufferedOutputStream(fos);

					int len;
					while ((len = is.read(buf)) != -1) {
						fos.write(buf, 0, len);
					}

					bos.flush();
					bos.close();
					fos.close();

					is.close();
				}

			}
		}
		zipFile.close();
		return failedUnZipNames;
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

	private static void closeStream(Object stream) {
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

	private String trim(String entryName) {
		char[] value = entryName.toCharArray();
		int len = value.length;
		while (value[len - 1] <= ' ')
			len--;
		return (len < value.length) ? entryName.substring(0, len) : entryName;
	}

	private String reFormatPath(String path) {
		String format = StringUtils.EMPTY;
		String[] splitList = path.split("/");
		int group = splitList.length;

		for (int i = 0; i < group; i++) {
			if (i < group - 1) {
				format = format + trim(splitList[i]) + File.separator;
			} else {
				format = format + splitList[i];
			}
		}
		return format;
	}

	private void createParentPath(String childPath) {
		String[] splitList = childPath.split("\\\\");
		StringBuilder pathBuilder = new StringBuilder();

		System.out.println("create:" + childPath);

		for (int i = 0; i < splitList.length - 1; i++) {
			pathBuilder = pathBuilder.append(splitList[i]).append(File.separator);
			File check = new File(pathBuilder.toString());
			if (!check.exists())
				check.mkdirs();
		}

	}

	public static void main(String[] args) throws ZipException, IOException {

		new FileZipUtils().unZipFile(new File("D:\\nyoibo_automation\\5-Finished Editing-20190528T172455Z-001.zip"),
				"D:\\nyoibo_automation");

	}

}
