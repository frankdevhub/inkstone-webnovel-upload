package nyoibo.inkstone.upload.data.rest;

import com.github.pagehelper.PageInfo;

import nyoibo.inkstone.upload.data.rest.results.Result;

/**
 * <p>Title:PageResult.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-20 22:31
 */

public class PageResult<T> extends Result {
	private PageInfo<T> data;

	public PageInfo<T> getData() {
		return data;
	}

	public void setData(PageInfo<T> data) {
		this.data = data;
	}

}
