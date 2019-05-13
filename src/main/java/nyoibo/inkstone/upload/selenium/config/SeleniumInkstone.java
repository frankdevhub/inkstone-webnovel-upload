package nyoibo.inkstone.upload.selenium.config;

public class SeleniumInkstone {
	//service checkpoint
	public static final String INKSTONE_TRANS_STATUS_RAW = "raw";
	public static final String INKSTONE_TRANS_STATUS_INPROGRESS = "inprogress";
	public static final String INKSTONE_TRANS_STATUS_TRANSLATEING ="translating";
	public static final String INKSTONE_TRANS_STATUS_EDITING = "editing";
	public static final String INKSTONE_TRANS_STATUS_READY_PUBLISH = "readytopublish";
	
	public static final String INKSTONE_TRANS_LOCAL_FOLDER_RAW = "raw";
	public static final String INKSTONE_TRANS_LOCAL_FOLDER_INPROGRESS = "inprogress";
	public static final String INKSTONE_TRANS_LOCAL_FOLDER_READYPUBLISH = "readytopublish";
	
	// inkstone service exception
	public static final String INKSTONE_FILE_UPLOAD_MULTI_TITLE = "find multiple titles in this chapter";
	public static final String INKSTONE_FILE_TITLE_NOT_FOUND = "cannot find title in this chapter";
	public static final String INKSTONE_ACCOUNT_NOT_LOGIN = "user didnot login";

	// inkstone credential config
	public static final String INKSTONE = "https://inkstone.webnovel.com";
	public static final String INKSTONE_HOME_TITLE = "Inkstone-QiDian International's novel translation platform";
	public static final String INKSTONE_HOME_PAGE_URL = "https://www.webnovel.com";
	public static final String INKSTONE_PRO_DASHBOARD = "https://inkstone.webnovel.com/dashboard.html";
	public static final String INKSTONE_PRO_BOOK = "https://inkstone.webnovel.com/book";
	public static final String INKSTONE_ACCOUNT_NAME_CN = "doris.yang@jianlaiglobal.com";
	public static final String INKSTONE_ACCOUNT_PWD_CN = "Dorisdoris2018";
	public static final String INKSTONE_ACCOUNT_NAME_EN = "doris.y.sh@hotmail.com";
	public static final String INKSTONE_ACCOUNT_PWD_EN = "2018Nyoibo";

	// inkstone titles
	public static final String INKSTONE_TRANSLATION = "Chapter translation-Inkstone";
    public static final String INKSTONE_DASHBOARD = "Dashboard-Inkstone";
	
	// login page
    public static final String INKSTONE_SIGN_BTN_CLASS="bt j_login";
	public static final String INKSTONE_HOME_ACCOUNT_CLASS = "g_user";
	public static final String INKSTONE_MAIL_LOGIN_FRAME_ID = "loginIframe";
	public static final String INKSTONE_LOGIN_PANEL_EMAIL_CLASS = "bt bt-block _e";
	public static final String INKSTONE_LOGIN_INPUT_EMAIL_NAME = "email";
	public static final String INKSTONE_LOGIN_INPUT_PWD_NAME = "password";
	public static final String INKSTONE_LOGIN_SUBMIT_ID = "submit";
	public static final String INKSTONE_LOHIN_PANEL_SUBMIT_CLASS = "bt bt-block";

	// dashboard raw page
	public static final String INKSTONE_PROJECT_RAW_DIV_CLASS = "book-menu fs16 cf  mb_g";
	public static final String INKSTONE_DASHBOARD_BTN_CLASS = "g_sd_option _on";
	public static final String INKSTONE_DASHBOARD_BTN_TITLE = "Dashboard";
	
	// translate page
	public static final String INKSTONE_TRANSLATE_ID = "claim";
	public static final String INKSTONE_TRANSLATE_ALERT_CLASS = "ui-dialog-container ui-dialog-confirm ui-dialog-animation";
	public static final String INKSTONE_TRANSLATE_TAKE_CLASS = "ui-button ui-button-warning";
	public static final String INKSTONE_TRANSLATE_SAVE_ID= "dosave";
	public static final String INKSTONE_TRANSLATE_NEXT_ID = "";
	public static final String INKSTONE_TRANSLATE_EDIT_TITLE_ID = "editTitle";
	public static final String INKSTONE_TRANSLATE_EDIT_CONTENT_ID = "editContent";
	
}
