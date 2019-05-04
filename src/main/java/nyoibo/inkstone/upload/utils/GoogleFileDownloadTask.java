package nyoibo.inkstone.upload.utils;

import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * <p>Title:GoogleFileDownloadThread.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-05 00:26
 */

public class GoogleFileDownloadTask implements Callable<Map> {

	private final String remotePath;
	private final String localPath;
	private final OutputStream outputStream;

	
	public GoogleFileDownloadTask (String remote, String local, OutputStream os){
		super();
		this.remotePath = remote;
		this.localPath = local;
		this.outputStream = os;
	}
	
	@Override
	public Map call() throws Exception {
	
		return null;
	}

	
	
}
