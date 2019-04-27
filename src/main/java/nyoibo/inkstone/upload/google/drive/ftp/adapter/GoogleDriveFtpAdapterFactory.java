package nyoibo.inkstone.upload.google.drive.ftp.adapter;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;

/**
 * <p>Title:GoogleDriveFtpAdapterFactory.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-27 19:25
 */

public class GoogleDriveFtpAdapterFactory {
	public static final Logger LOGGER = LoggerFactory.getLogger(GoogleDriveFtpAdapterFactory.class);

	private static GoogleDriveFtpAdapter googleDriveFtpAdapter;

	public static GoogleDriveFtpAdapter getInstance() {
		if (googleDriveFtpAdapter == null) {
			synchronized (GoogleDriveFtpAdapter.class) {
				if (googleDriveFtpAdapter == null) {
					init();
					return googleDriveFtpAdapter;
				}
			}
		}
		return googleDriveFtpAdapter;
	}

	private static void init() {

	}

}
