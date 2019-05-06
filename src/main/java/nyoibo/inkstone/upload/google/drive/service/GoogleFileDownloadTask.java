package nyoibo.inkstone.upload.google.drive.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.message.MessageMethod;

/**
 * <p>Title:GoogleFileDownloadThread.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-05 00:26
 */

public class GoogleFileDownloadTask extends FutureTask<Boolean> {

	private final String localPath;
	private final InputStream stream;
	private Logger LOGGER = LoggerFactory.getLogger(GoogleFileDownloadTask.class);

	public GoogleFileDownloadTask createTask(String localPath, InputStream stream) {

		Callable<Boolean> service = create(localPath, stream);
		return new GoogleFileDownloadTask(service, localPath, stream);
	}

	private Callable<Boolean> create(String localPath, InputStream stream) {
		return new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				try {
					downloadSource(localPath, stream);
				} catch (Exception e) {
					LOGGER.begin().headerAction(MessageMethod.ERROR).error(e.getMessage());
				}
				return Boolean.TRUE;
			}
		};
	}

	private GoogleFileDownloadTask(Callable<Boolean> callable, String localPath, InputStream stream) {
		super(callable);
		this.localPath = localPath;
		this.stream = stream;
	}

	private void downloadSource(String localPath, InputStream stream) throws IOException {
		File downloadFile = new File(localPath);
		byte[] buffer = new byte[1024];
		FileOutputStream fos = new FileOutputStream(downloadFile);

		int len = 0;
		BufferedInputStream bis = new BufferedInputStream(stream);
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		while ((len = bis.read(buffer)) > 0) {
			bos.write(buffer, 0, len);
		}

		bis.close();
		bos.flush();
		bos.close();
	}

	public String getLocalPath() {
		return localPath;
	}

}
