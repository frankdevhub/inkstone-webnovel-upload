package nyoibo.inkstone.upload.data.rest;

import nyoibo.inkstone.upload.data.rest.results.Result;

/**
 * <p>Title:NoResult.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-20 22:29
 */

public class NoResult extends Result {
	private Boolean result;

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

}
