package nyoibo.inkstone.upload.web.pages;


import org.openqa.selenium.By;

import nyoibo.inkstone.upload.selenium.AssignDriver;
import nyoibo.inkstone.upload.selenium.DriverBase;
import nyoibo.inkstone.upload.selenium.Query;

/**
 * <p>Title:InkstoneHomePage.java</p>  
 * <p>Description:InkstoneHomePage </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-21 01:48
 */

public class InkstoneHomePage {
	/*private Query searchBar = new Query().defaultLocator(By.name("q"));
	private Query googleSearch = new Query().defaultLocator(By.name("btnK"));
	private Query imFeelingLucky = new Query().defaultLocator(By.name("btnI"));*/
	
	private static String USERNAME = "frankdevhub@126.com";
	private static String PASSWORD = "arrowhead2@icbc";
	
	private Query userNameTextBox = new Query().defaultLocator(By.name("login"));
	private Query passwordTextBox = new Query().defaultLocator(By.name("password"));
	private Query submitButton  = new Query().defaultLocator(By.name("commit"));
	
    
	public InkstoneHomePage() throws Exception {
		AssignDriver.initQueryObjects(this, DriverBase.getDriver());
	}

	
	public InkstoneHomePage enterCredential() {
		userNameTextBox.findWebElement().clear();
		passwordTextBox.findWebElement().clear();
		
		userNameTextBox.findWebElement().sendKeys(USERNAME);
		passwordTextBox.findWebElement().sendKeys(PASSWORD);
		
		submitButton.findWebElement().submit();
		
		return this;
	}

	/*public InkstoneHomePage enterSearchTerm(String searchTerm) {
		searchBar.findWebElement().clear();
		searchBar.findWebElement().sendKeys(searchTerm);

		return this;
	}

	public InkstoneHomePage submitSearch() {
		googleSearch.findWebElement().submit();

		return this;
	}

	public void getLucky() {
		imFeelingLucky.findWebElement().click();
	}*/
}
