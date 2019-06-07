package nyoibo.inkstone.upload;

import org.eclipse.swt.widgets.Display;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import nyoibo.inkstone.upload.gui.InkstoneUploadMainWindow;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = { "nyoibo.inkstone.upload" })
public class NyoiboApp extends SpringBootServletInitializer {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(NyoiboApp.class, args);
		InkstoneUploadMainWindow main = new InkstoneUploadMainWindow(Display.getDefault().getActiveShell());
		main.open();
	}
}
