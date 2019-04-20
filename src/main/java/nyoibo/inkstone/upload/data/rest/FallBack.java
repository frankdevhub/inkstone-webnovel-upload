package nyoibo.inkstone.upload.data.rest;

/**
 * <p>Title:FallBack.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-20 22:20
 */

public class FallBack<T> {
	private static final String ERR_CODE = "406";
	  private static final String ERR_MSG = "远程服务调用失败。";
	  
	  public NoResult getNoResult()
	  {
	    NoResult result = new NoResult();
	    result.setMessage("远程服务调用失败。");
	    result.setResult(Boolean.valueOf(false));
	    result.setStatus("406");
	    return result;
	  }
	  
	  public PageResult<T> getPageResult()
	  {
	    PageResult<T> result = new PageResult();
	    result.setMessage("远程服务调用失败。");
	    result.setStatus("406");
	    return result;
	  }
	  
	  public SingleResult<T> getSingleResult()
	  {
	    SingleResult<T> result = new SingleResult();
	    result.setMessage("远程服务调用失败。");
	    result.setStatus("406");
	    return result;
	  }
}
