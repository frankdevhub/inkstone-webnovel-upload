package nyoibo.inkstone.upload.data.rest;

import nyoibo.inkstone.upload.data.rest.results.Result;

/**
 * <p>Title:SingleResult.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-20 22:34
 */

public class SingleResult<T> extends Result {
	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
