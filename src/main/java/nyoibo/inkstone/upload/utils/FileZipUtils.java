package nyoibo.inkstone.upload.utils;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.UnicodeDetector;
import nyoibo.inkstone.upload.data.logging.Logger;
import nyoibo.inkstone.upload.data.logging.LoggerFactory;
import nyoibo.inkstone.upload.message.MessageMethod;
import nyoibo.inkstone.upload.selenium.config.SeleniumInkstone;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileZipUtils {

    private static final String ZIP_SUFFIX = "zip";
    private ArrayList<File> unZipFiles = new ArrayList<>();
    private ArrayList<String> unZipFolderNames = new ArrayList<>();
    private ArrayList<String> failedUnZipNames = new ArrayList<>();

    private final Logger LOGGER = LoggerFactory.getLogger(FileZipUtils.class);

    public void unZipDriveZip(String filePath) throws Exception {
        boolean hasZip = false;
        unZipFolderNames = new ArrayList<>();

        File downloadZipDir = new File(filePath);

        if (!downloadZipDir.exists()) {
            throw new Exception(String.format("File path [%s] does not exist.", filePath));
        }

        File[] fileList = downloadZipDir.listFiles();
        for (File file : fileList) {
            if (file.isFile()) {
                String fileName = file.getName();
                String out = fileName.substring(fileName.lastIndexOf(".") + 1);
                if (out.equals(ZIP_SUFFIX)) {
                    hasZip = true;
                    unZipFiles.add(file);
                }
            }
        }

        if (!hasZip)
            throw new Exception(String.format("Cannot find zip file in path [%s]", filePath));
        for (File zip : unZipFiles) {
            unZipFile(zip, filePath);
            FileUtils.forceDelete(zip);
        }
    }

    public String getFileEncode(File file) {
        String encode = SeleniumInkstone.Default_CN_Code;
        Charset charset = null;
        try {
            CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
            detector.add(UnicodeDetector.getInstance());
            detector.add(JChardetFacade.getInstance());
            detector.add(ASCIIDetector.getInstance());

            charset = detector.detectCodepage(file.toURI().toURL());
            charset = charset.name().equals("void") ? null : charset;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (charset != null) {
            if (charset.name().equals("US-ASCII")) {
                encode = "ISO_8859_1";
            } else {
                encode = charset.name();
            }
        }

        return encode;
    }

    private int getZipEntryCount(Enumeration<?> entries) {
        int count = 0;
        while (entries.hasMoreElements()) {
            entries.nextElement();
            count++;
        }
        LOGGER.begin().headerAction(MessageMethod.EVENT).info(String.format("entry count complete :[%s]", count));
        return count;
    }

    public ArrayList<String> unZipFile(File file, String filePath) throws IOException {
        unZipFolderNames = new ArrayList<>();
        failedUnZipNames = new ArrayList<>();

        LOGGER.begin().headerAction(MessageMethod.EVENT).info(String.format("zip path is :[%s]", filePath));
        String fileEncode = getFileEncode(file);
        LOGGER.begin().headerAction(MessageMethod.EVENT).info(String.format("zip encode is :[%s]", fileEncode));
        final ZipFile zipFile = new ZipFile(file.getAbsolutePath(), Charset.forName(fileEncode));
        int entryCount = getZipEntryCount(zipFile.entries());
        Enumeration<?> entries = zipFile.entries();

        class UnZipProgressMonitorDialog {
            private void showDialog() {
                try {
                    ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(
                            Display.getCurrent().getActiveShell());
                    IRunnableWithProgress runnalble = monitor -> {
                        monitor.beginTask("do unzip chapter files ...", entryCount);
                        double step;
                        boolean groupStep = false;
                        int group = 0;
                        if (entryCount <= 100) {
                            step = 100 / entryCount;
                        } else {
                            step = ((double) 1) / (entryCount / 100);
                            groupStep = true;
                            group = (int) (((double) 1) / (step));
                        }

                        int index = -1;
                        while (entries.hasMoreElements()) {
                            index++;
                            ZipEntry entry = (ZipEntry) entries.nextElement();
                            String fileName = reFormatPath(entry.getName());
                            InputStream is;
                            FileOutputStream fos;
                            BufferedOutputStream bos;
                            byte[] buf = new byte[2048];

                            if (entry.isDirectory()) {
                                unZipFolderNames.add(fileName);
                                String dirPath = filePath + File.separator + fileName;
                                File dir = new File(dirPath);
                                dir.mkdirs();
                            } else {
                                File targetFile = new File(filePath + File.separator + fileName);
                                try {
                                    if (targetFile.getParentFile() != null
                                            && !targetFile.getParentFile().exists()) {
                                        createParentPath(targetFile.getAbsolutePath());
                                    }
                                    targetFile.createNewFile();
                                    if (groupStep) {
                                        if (index % group == 0)
                                            monitor.worked(1);
                                    } else {
                                        monitor.worked((int) step);
                                    }
                                    monitor.subTask(String.format("Unzip file complete:[%s]",
                                            targetFile.getAbsolutePath()));
                                    is = zipFile.getInputStream(entry);
                                    fos = new FileOutputStream(targetFile);
                                    bos = new BufferedOutputStream(fos);
                                    int len;
                                    while ((len = is.read(buf)) != -1) {
                                        fos.write(buf, 0, len);
                                    }

                                    bos.flush();
                                    bos.close();
                                    fos.close();

                                    is.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    failedUnZipNames.add(targetFile.getAbsolutePath());
                                }
                            }
                        }
                        if (monitor.isCanceled())
                            throw new InterruptedException("unzip has been canceled mannually.");
                    };
                    progressDialog.run(true, false, runnalble);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        UnZipProgressMonitorDialog dialog = new UnZipProgressMonitorDialog();
        LOGGER.begin().headerAction(MessageMethod.EVENT).info(String.format("show unzip monitor."));
        dialog.showDialog();

        zipFile.close();
        return failedUnZipNames;
    }

    public void pack(File source, String dir, ZipArchiveOutputStream zipOutStream) throws IOException {
        if (source.isFile()) {
            zipOutStream.putArchiveEntry(new ZipArchiveEntry(source, dir));
            IOUtils.copy(new FileInputStream(source), zipOutStream);
            zipOutStream.closeArchiveEntry();
        } else {
            File[] subFiles = source.listFiles();
            if (subFiles != null && subFiles.length > 0) {
                for (File tmp : subFiles) {
                    pack(tmp, dir + File.separator + tmp.getName(), zipOutStream);
                }
            } else {
                zipOutStream.putArchiveEntry(new ZipArchiveEntry(source, dir));
                zipOutStream.closeArchiveEntry();
            }
        }

    }

    public File packZip(File source, String packDir, String packName) {
        File target = new File(packDir);
        if (!target.exists()) {
            target.mkdirs();
        }
        target = new File(packDir + File.separator + packName);
        try {
            ZipArchiveOutputStream zipOutStream = new ZipArchiveOutputStream(target);
            pack(source, source.getName(), zipOutStream);
            closeStream(zipOutStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return target;
    }

    private static void closeStream(Object stream) {
        if (stream != null) {
            if (stream instanceof InputStream) {
                InputStream in = (InputStream) stream;
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (stream instanceof OutputStream) {
                OutputStream out = (OutputStream) stream;
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String trim(String entryName) {
        char[] value = entryName.toCharArray();
        int len = value.length;
        while (value[len - 1] <= ' ')
            len--;
        return (len < value.length) ? entryName.substring(0, len) : entryName;
    }

    private String reFormatPath(String path) {
        String format = StringUtils.EMPTY;
        String[] splitList = path.split("/");
        int group = splitList.length;

        for (int i = 0; i < group; i++) {
            if (i < group - 1) {
                format = format + trim(splitList[i]) + File.separator;
            } else {
                format = format + splitList[i];
            }
        }
        return format;
    }

    private void createParentPath(String childPath) {
        String[] splitList = childPath.split("\\\\");
        StringBuilder pathBuilder = new StringBuilder();

        LOGGER.begin().headerAction(MessageMethod.EVENT)
                .info(String.format("create children path : [%s]", childPath));

        for (int i = 0; i < splitList.length - 1; i++) {
            pathBuilder = pathBuilder.append(splitList[i]).append(File.separator);
            File check = new File(pathBuilder.toString());
            if (!check.exists())
                check.mkdirs();
        }

    }

    public static void main(String[] args) throws Exception {
        String filePath = "D:\\Automation";
        new FileZipUtils().unZipDriveZip(filePath);

    }
}
