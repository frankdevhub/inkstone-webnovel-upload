package nyoibo.inkstone.upload.google.drive.ftp.adapter;

import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;

/**
 * <p>Title:GoogleDriveFtpAdapter.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-23 02:05
 */

public class GoogleDriveFtpAdapter {
	 private static final Log LOG = LogFactory.getLog(GoogleDriveFtpAdapter.class);

	    private final org.apache.ftpserver.FtpServer server;
	    private final FtpGdriveSynchService cacheUpdater;

	    GoogleDriveFtpAdapter(Properties configuration) {

	        int port = Integer.parseInt(configuration.getProperty("port", String.valueOf(1821)));
	        if (!available(port)) {
	            throw new IllegalArgumentException("Invalid argument. Port '" + port + "' already in used");
	        }

	        Cache cache = new SQLiteCache(configuration);
	        GoogleDriveFactory googleDriveFactory = new GoogleDriveFactory(configuration);
	        googleDriveFactory.init();

	        GoogleDrive googleDrive = new GoogleDrive(googleDriveFactory.getDrive());
	        cacheUpdater = new FtpGdriveSynchService(cache, googleDrive);
	        Controller controller = new Controller(cache, googleDrive, cacheUpdater);

	        // FTP Setup
	        FtpServerFactory serverFactory = new GFtpServerFactory(controller, cache, configuration, cacheUpdater);
	        server = serverFactory.createServer();

	    }

	    private static boolean available(int port) {
	        try (Socket ignored = new Socket("localhost", port)) {
	            return false;
	        } catch (IOException ignored) {
	            return true;
	        }
	    }

	    void start() {
	        try {
	            cacheUpdater.start();
	            server.start();
	            LOG.info("Application started!");
	        } catch (FtpException e) {
	            throw new RuntimeException(e);
	        }
	    }

	    void stop() {
	        cacheUpdater.stop();
	        server.stop();
	        LOG.info("Application stopped.");
	    }
}