package nyoibo.inkstone.upload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.message.MessageMethod;

/**
 * <p>Title:App.java</p>  
 * <p>Description: the FTP client using to upload files to google drive and inkstone platform</p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * 
 * @author frankdevhub
 * @date 2019-04-20 20:12:35
 */

@SpringBootApplication
@ComponentScan(basePackages = { "nyoibo.inkstone.upload" })
public class NyoiboApp {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(NyoiboApp.class, args);
		LoggerFactory.getLogger(NyoiboApp.class).begin().headerAction(MessageMethod.EVENT).info("application start!");
	}
}
