package nyoibo.inkstone.upload.google.drive.ftp.adapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import nyoibo.inkstone.upload.NyoiboApp;
import nyoibo.inkstone.upload.google.drive.ftp.adapter.utils.JarUtils;

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
	public static final Log LOGGER = LogFactory.getLog(GoogleDriveFtpAdapterFactory.class);

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
		try {
			JarUtils.printManifestAttributesToString();
			LOGGER.info("Program info: " + JarUtils.getManifestAttributesAsMap());
			LOGGER.info("Loading configuration...");

			Properties configuration = loadPropertiesFromClasspath();
			configuration.putAll(loadProperties("configuration.properties"));

			LOGGER.info("Creating application with configuration '" + configuration + "'");
			googleDriveFtpAdapter = new GoogleDriveFtpAdapter(configuration);

			registerShutdownHook();

			start();

		} catch (Exception e) {
			LOGGER.error("Error loading app", e);
		}

	}

	private static void start() {
		googleDriveFtpAdapter.start();
	}

	private static void stop() {
		googleDriveFtpAdapter.stop();
	}

	private static void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				LOGGER.info("Shuting down...");
				GoogleDriveFtpAdapterFactory.stop();
				LOGGER.info("Good bye!");
			}
		});
	}

	private static Properties loadProperties(String propertiesFilename) {
		Properties properties = new Properties();
		FileInputStream inStream = null;
		try {
			LOGGER.info("Loading properfiles file '" + propertiesFilename + "'...");
			inStream = new FileInputStream(propertiesFilename);
			properties.load(inStream);
		} catch (Exception ex) {
			LOGGER.warn("Exception loading file '" + propertiesFilename + "'.");
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}
		}
		LOGGER.info("Properfiles loaded: '" + properties + "'");
		return properties;
	}

	static Properties loadPropertiesFromClasspath() {
		Properties properties = new Properties();

		InputStream configurationStream = NyoiboApp.class.getResourceAsStream("/configuration.properties");
		if (configurationStream == null) {
			return properties;
		}

		try {
			LOGGER.info("Loading properties from classpath...");
			properties.load(configurationStream);
			LOGGER.info("Properties loaded: '" + properties + "'");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (configurationStream != null) {
				try {
					configurationStream.close();
				} catch (Exception ex) {
					LOGGER.error(ex.getMessage(), ex);
				}
			}
		}
		return properties;
	}
}
