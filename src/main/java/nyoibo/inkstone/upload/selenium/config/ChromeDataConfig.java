package nyoibo.inkstone.upload.selenium.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * <p>Title:ChromeDataConfig.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-12 17:28
 */

public class ChromeDataConfig {
    public static final String WIN_CHROME_APP = "Application";
    public static final String WIN_CHROME_DATA = "User Data";
    
	public static String createDataName(String thread) {
		StringBuilder builder = new StringBuilder();
		long time = System.currentTimeMillis();
		String timeStr = Long.toString(time);

		String dataName = builder.append(timeStr).append("-").append(thread).toString();
		return dataName;
	}
   
	public static String getLocal() {
		String path = null;
		path = "C:/Users/Administrator/AppData/Local/Google/Chrome/User Data";
		return path;
	}

	public static void config(String thread, String root,String folderName) throws IOException {
		FileUtils configUtils = new FileUtils();
		String destDir = root + "/" + folderName;

		File rootFile = new File(root);
		File destFile = new File(destDir);
		configUtils.copyDirectory(rootFile, destFile);
	}

	public static void cleanData(String thread) throws IOException {
		FileUtils configUtils = new FileUtils();
		String path = getLocal() + "/" + thread;
		File data = new File(path);

		configUtils.deleteDirectory(data);
	}
   
}
