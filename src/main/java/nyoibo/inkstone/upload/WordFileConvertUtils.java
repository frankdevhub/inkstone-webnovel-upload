package nyoibo.inkstone.upload;

import java.io.File;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class WordFileConvertUtils {

	public boolean change(String sourceFilesPath, String destinationFilesPath, int destinationFilesType) {
		File sourcePathFile = new File(sourceFilesPath);
		File sourceFilesList[] = sourcePathFile.listFiles();
		System.out.println(String.format("Folder contains: [%s] files", sourceFilesList.length));
		String sourceChildPath = new String("");
		String destiNationChildPath = new String("");
		for (int i = 0; i < sourceFilesList.length; i++) {
			if (sourceFilesList[i].isFile()) {
				System.out.println(String.format("Handling file number %s", i + 1));
				String fileName = sourceFilesList[i].getName();
				String fileType = new String("");
				fileType = fileName.substring((fileName.length() - 4), fileName.length());
				if (fileType.equals("docx")) {
					System.out.println("Transferring......");
					System.out.println(String.format("SourceFiles Path:%s", sourceFilesPath));

					System.out.println(String.format("Word File Name: %s", fileName));
					ActiveXComponent app = new ActiveXComponent("Word.Application");
					String docPath = sourceFilesPath + "\\" + fileName;
					String othersPath = destinationFilesPath + "\\" + fileName.substring(0, (fileName.length() - 5));
					String inFile = docPath;
					String outFile = othersPath;

					try {
						app.setProperty("Visible", new Variant(false));
						Dispatch docs = app.getProperty("Documents").toDispatch();
						Dispatch doc = Dispatch
								.invoke(docs, "Open", Dispatch.Method,
										new Object[] { inFile, new Variant(false), new Variant(true) }, new int[1])
								.toDispatch();
						Dispatch.invoke(doc, "SaveAs", Dispatch.Method,
								new Object[] { outFile, new Variant(destinationFilesType) }, new int[1]);
						Variant file = new Variant(false);
						Dispatch.call(doc, "Close", file);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Document Transfer Failed.");
					} finally {
						app.invoke("Quit", new Variant[] {});
					}
					System.out.println("Tranfer Complete");
				}

				else if (fileType.equals(".doc")) {
					System.out.println("Transferring......");
					System.out.println(String.format("SourceFiles Path:%s", sourceFilesPath));
					System.out.println(String.format("Word File Name: %s", fileName));
					ActiveXComponent app = new ActiveXComponent("Word.Application");
					String docPath = sourceFilesPath + "\\" + fileName;
					String othersPath = destinationFilesPath + "\\" + fileName.substring(0, (fileName.length() - 4));
					String inFile = docPath;
					String outFile = othersPath;
					try {
						app.setProperty("Visible", new Variant(false));
						Dispatch docs = app.getProperty("Documents").toDispatch();
						Dispatch doc = Dispatch
								.invoke(docs, "Open", Dispatch.Method,
										new Object[] { inFile, new Variant(false), new Variant(true) }, new int[1])
								.toDispatch();
						Dispatch.invoke(doc, "SaveAs", Dispatch.Method,
								new Object[] { outFile, new Variant(destinationFilesType) }, new int[1]);
						Variant file = new Variant(false);
						Dispatch.call(doc, "Close", file);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Document Transfer Failed.");
					} finally {
						app.invoke("Quit", new Variant[] {});
					}
					System.out.println("Document Transfer Failed.");
				}

				else {
					System.out.println("Not a word document");
				}
			}

			else {
				sourceChildPath = sourceFilesPath;
				sourceChildPath = sourceChildPath + "\\" + sourceFilesList[i].getName() + "\\";
				System.out.println(String.format("SourceFiles Path:%s", sourceFilesPath));

				destiNationChildPath = destinationFilesPath;
				destiNationChildPath = destinationFilesPath + "\\" + sourceFilesList[i].getName() + "\\";
				System.out.println(String.format("Transfered File Saved Path: %s", destiNationChildPath));

				mkdir(destiNationChildPath);
				change(sourceChildPath, destiNationChildPath, destinationFilesType);
			}
		}
		System.out.println("[all files transfer complete!]");
		return true;
	}

	public void mkdir(String mkdirName) {
		try {
			File dirFile = new File(mkdirName);
			boolean bFile = dirFile.exists();
			if (bFile == true) {
				throw new Exception(String.format("Already exist a Folder:[%s]", mkdirName));
			}

			else {
				System.out.println(String.format("New dataFolder:[%d]", mkdirName));
				bFile = dirFile.mkdir();
				if (bFile == true) {
					System.out.println("Create a folder successfully");
				} else {
					throw new Exception("Create folder error, please make sure access right of disk and enough memory");

				}
			}
		} catch (Exception err) {
			System.err.println("ELS - Chart : Cannot create new folder");
			err.printStackTrace();
		} 
	}

}