package nyoibo.inkstone.upload.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * <p>Title:SpringUtils.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-20 23:06
 */

@Component
public class SpringUtils implements ApplicationContextAware {

	@Autowired
	private static ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static <T> T getBean(Class<T> requiredType) throws BeansException {
		return applicationContext.getBean(requiredType);
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (applicationContext == null) {
			SpringUtils.applicationContext = applicationContext;
		}
	}
}
