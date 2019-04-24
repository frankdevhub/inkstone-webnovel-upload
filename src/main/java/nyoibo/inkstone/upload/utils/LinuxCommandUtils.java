/*package nyoibo.inkstone.upload.utils;

import java.io.IOException;

import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.message.MessageMethod;

*//**
 * <p>Title:LinuxCommandUtils.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-24 11:04
 *//*

public class LinuxCommandUtils {

	private static final String COMMAND_ENV = "#!/bin/bash/";
	private static final String CHMOD_COMMAND = "chmod777";
	private static boolean chmod = false;

	private static final Logger LOGGER = LoggerFactory.getLogger(LinuxCommandUtils.class);

	private static String command(String linuxCommand) {
		StringBuilder commandBuilder = new StringBuilder();
		return commandBuilder.append(COMMAND_ENV).append(linuxCommand).toString();
	}

	public static void execute(String linuxCommand) throws IOException, InterruptedException {
		if (!chmod)
			chmod777();
		Runtime.getRuntime().exec(command(linuxCommand));

		LOGGER.begin().headerAction(MessageMethod.EVENT).info(linuxCommand);
	}

	private static void chmod777() throws IOException, InterruptedException {
		Process process = null;
		process = Runtime.getRuntime().exec(command(CHMOD_COMMAND));
		process.waitFor();

		LOGGER.begin().headerAction(MessageMethod.EVENT).info(commandInfo(CHMOD_COMMAND));
	}

	private static String commandInfo(String linuxCommand) {
		StringBuilder commandInfoBuilder = new StringBuilder();
		return commandInfoBuilder.append("running linux command").append("\\b ").append(linuxCommand).toString();
	}

}
*/