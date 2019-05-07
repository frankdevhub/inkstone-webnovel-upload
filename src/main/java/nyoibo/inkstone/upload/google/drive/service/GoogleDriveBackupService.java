package nyoibo.inkstone.upload.google.drive.service;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import nyoibo.inkstone.upload.google.drive.ftp.adapter.GoogleDriveFtpAdapter;
import nyoibo.inkstone.upload.google.drive.ftp.adapter.GoogleDriveFtpAdapterFactory;
import nyoibo.inkstone.upload.google.drive.ftp.adapter.model.GFile;
import nyoibo.inkstone.upload.google.drive.ftp.adapter.model.GoogleDrive;
import nyoibo.inkstone.upload.google.drive.ftp.adapter.model.GoogleDriveFactory;

/**
 * <p>Title:GoogleDriveBackupService.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-03 19:16
 */

public class GoogleDriveBackupService {

	private static final String GOOGLE_DRIVE_DOWNLOAD = "google_drive_backup";

	public synchronized static Thread check() {
		Thread alive = null;
		ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
		int noThread = currentGroup.activeCount();

		Thread[] lstThreads = new Thread[noThread];
		currentGroup.enumerate(lstThreads);

		for (int i = 0; i < noThread; i++) {
			String currentThreadName = lstThreads[i].getName();
			if (currentThreadName.equals(GOOGLE_DRIVE_DOWNLOAD)) {
				alive = lstThreads[i];
				break;
			}
		}
		return alive;
	}

	public synchronized static void backup(String localRootFolder) throws Exception {
		Thread check = check();
		if (null != check)
			throw new Exception("last download task is alive, please choose kill or continue");
		GoogleDriveFtpAdapter adapter = GoogleDriveFtpAdapterFactory.getInstance();
		GoogleDriveFactory factory = adapter.getGoogleDriveFactory();
		GoogleDrive drive = new GoogleDrive(factory.getDrive());

		ExecutorService service = InkstoneCustomThreadPoolExecutor.googleBackUpPool();
		String rootFolderId = drive.getROOT_FOLDER_ID();
		List<GFile> files = drive.list(rootFolderId);
		for (GFile f : files) {
			if (f.isDirectory()) {
				loop(f, drive, localRootFolder, service);
			} else {
				InputStream stream = drive.downloadFile(f);
				String folderPath = new StringBuilder().append(localRootFolder).toString();
				String filePath = new StringBuilder().append(folderPath).append("/").append(f.getName()).toString();
				Callable<Boolean> task = new GoogleFileDownloadTask(folderPath, filePath, stream);
				service.submit(task);
			}

		}

		service.shutdown();
	}

	private static void loop(GFile file, GoogleDrive drive, String path, ExecutorService service) {
		List<GFile> files = drive.list(file.getId());
		for (GFile f : files) {
			if (f.isDirectory()) {
				String next = new StringBuilder().append(path).append("/").append(f.getName()).toString();
				loop(f, drive, next, service);
			} else {
				String folderPath = new StringBuilder().append(path).toString();
				InputStream stream = drive.downloadFile(f);

				String filePath = new StringBuilder().append(folderPath).append("/").append(f.getName()).toString();
				Callable<Boolean> task = new GoogleFileDownloadTask(folderPath, filePath, stream);
				service.submit(task);
			}

		}
	}

	public synchronized static void cleanUp(Thread alive) {
		if (null != alive)
			alive.interrupt();
	}


}
