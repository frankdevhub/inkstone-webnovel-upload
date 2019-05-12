package nyoibo.inkstone.upload.selenium.config;


import java.io.IOException;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;


/**
 * <p>Title:DriverFactory.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-21 01:18
 */

public class DriverFactory {

    private RemoteWebDriver driver;
    private DriverType selectedDriverType;

    private final String operatingSystem = System.getProperty("os.name").toUpperCase();
    private final String systemArchitecture = System.getProperty("os.arch");
    private final boolean useRemoteWebDriver = Boolean.getBoolean("remoteDriver");
    private static final String CHROME_DRIVER_PATH = "src/main/resources/chromedriver.exe";
    
	public DriverFactory() {
		/*System.setProperty("webdriver.chrome.driver",
				"C:\\Users\\Administrator\\AppData\\Local\\Google\\Chrome\\Application\\chromedriver.exe");*/
		System.setProperty("webdriver.chrome.driver",
				CHROME_DRIVER_PATH);
		DriverType driverType = DriverType.CHROME;
	    
		String browser = System.getProperty("browser", driverType.toString()).toUpperCase();
		try {
			driverType = DriverType.valueOf(browser);
		} catch (IllegalArgumentException ignored) {
			System.err.println("Unknown driver specified, defaulting to '" + driverType + "'...");
		} catch (NullPointerException ignored) {
			System.err.println("No driver specified, defaulting to '" + driverType + "'...");
		}
		selectedDriverType = driverType;
	}

	public RemoteWebDriver getDriver(String thread) throws Exception {
		if (null == driver) {
			instantiateWebDriver(selectedDriverType, thread);
		}

		return driver;
	}

    public RemoteWebDriver getStoredDriver() {
        return driver;
    }

    public void quitDriver() {
        if (null != driver) {
            driver.quit();
            driver = null;
		}
	}

	private void instantiateWebDriver(DriverType driverType, String path) throws IOException {
		System.out.println(" ");
		System.out.println("Local Operating System: " + operatingSystem);
		System.out.println("Local Architecture: " + systemArchitecture);
		System.out.println("Selected Browser: " + selectedDriverType);
		System.out.println("Connecting to Selenium Grid: " + useRemoteWebDriver);
		System.out.println(" ");

		DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
		desiredCapabilities = DesiredCapabilities.chrome();

		driver = driverType.getWebDriverObject(desiredCapabilities, path);

		desiredCapabilities.setCapability("pageLoadStrategy", "eager");

	}
}

