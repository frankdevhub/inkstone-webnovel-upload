package nyoibo.inkstone.upload.web.pages;

import org.openqa.selenium.By;

import nyoibo.inkstone.upload.selenium.Query;
import nyoibo.inkstone.upload.selenium.config.SeleniumInkstone;

/**
 * <p>Title:InkstoneHomePage.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-08 11:00
 */

public class InkstoneHomePage {
	private final String accountName;
	private final String accountPwd;

	private final Query accountIcon;
	private final Query selectEmailLoginBtn;
	private final Query accountInput;
	private final Query pwdInput;
	private final Query submitBtn;
	
	public InkstoneHomePage(boolean foreign) {
		if (foreign) {
			this.accountName = SeleniumInkstone.INKSTONE_ACCOUNT_NAME_EN;
			this.accountPwd = SeleniumInkstone.INKSTONE_ACCOUNT_PWD_CN;
		} else {
			this.accountName = SeleniumInkstone.INKSTONE_ACCOUNT_NAME_CN;
			this.accountPwd = SeleniumInkstone.INKSTONE_ACCOUNT_PWD_CN;
		}

		accountIcon = new Query().defaultLocator(By.className(SeleniumInkstone.INKSTONE_HOME_ACCOUNT_CLASS));
		selectEmailLoginBtn = new Query()
				.defaultLocator(By.className(SeleniumInkstone.INKSTONE_LOGIN_PANEL_EMAIL_CLASS));
		accountInput = new Query().defaultLocator(By.name(SeleniumInkstone.INKSTONE_LOGIN_INPUT_EMAIL_NAME));
		pwdInput = new Query().defaultLocator(By.name(SeleniumInkstone.INKSTONE_LOGIN_INPUT_PWD_NAME));
		submitBtn = new Query().defaultLocator(By.id(SeleniumInkstone.INKSTONE_LOGIN_SUBMIT_ID));

	}

}
