package nyoibo.inkstone.upload.google.drive.ftp.adapter.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

/**
 * <p>Title:FtpGdriveSynchService.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-23 16:52
 */

public final class FtpGdriveSynchService {
	private static final Log LOG = LogFactory.getLog(FtpGdriveSynchService.class);

	private GoogleDrive googleDrive;

	private Cache cache;

	private ExecutorService executor;

	private Timer timer;

	public FtpGdriveSynchService(Cache cache, GoogleDrive googleDrive) {
		this.googleDrive = googleDrive;
		this.cache = cache;
		this.executor = Executors.newFixedThreadPool(4);
		this.timer = new Timer(true);
		init();
	}

	private void init() {
		GFile rootFile = cache.getFile("root");
		if (rootFile == null) {
			rootFile = new GFile("");
			rootFile.setId("root");
			rootFile.setDirectory(true);
			rootFile.setParents(Collections.emptySet());
			cache.addOrUpdateFile(rootFile);
		}
	}

	/**
	 * Start synchronization local database vs remote google drive
	 */
	public void start() {
		timer.schedule(createSyncChangesTask(), 0, 10000);
	}

	public void updateFolderNow(String fileId) {
		synchFolder(fileId);
	}

	public void stop() {
		LOG.info("Stopping synch service...");
		executor.shutdownNow();
		timer.cancel();
	}

	private TimerTask createSyncChangesTask() {
		return new TimerTask() {

			@Override
			public void run() {
				try {
					// check google drive changes
					checkForRemoteChanges();

					// sync pending folders
					syncPendingFolders();
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					try {
						if (e.getCause() instanceof GoogleJsonResponseException) {
							int code = ((GoogleJsonResponseException) e).getDetails().getCode();
							if (code == 401) {
								LOG.error("Unauthorized. Cancelling timer..." + e.getMessage(), e);
								timer.cancel();
							}
						}
					} catch (Exception e1) {
						LOG.error("Could not handle exception: " + e.getMessage(), e);
					}
				}
			}

			private void checkForRemoteChanges() {

				String revision = cache.getRevision();
				LOG.debug("Local revision: " + revision);
				if (revision == null) {
					revision = googleDrive.getStartRevision();
					cache.updateRevision(revision);
					LOG.debug("New revision: " + revision);
				}

				List<GChange> googleChanges;
				while ((googleChanges = googleDrive.getAllChanges(revision)).size() > 0) {

					LOG.info("Remote changes: " + googleChanges.size());

					for (GChange change : googleChanges) {
						processChange(change);
						revision = change.getRevision();
					}

					// update revision to start next time there
					cache.updateRevision(revision);
					LOG.info("New revision: " + revision);
				}

				LOG.debug("No remote changes...");
			}

			private void processChange(GChange change) {

				if (change.isRemoved() || change.getFile().getTrashed()) {
					final GFile localFile = cache.getFile(change.getFileId());
					if (localFile != null) {
						LOG.info("Remote deletion: " + localFile.getId());
						int deletedFiles = cache.deleteFile(localFile.getId());
						LOG.debug("Total records deleted: " + deletedFiles);
					}
					return;
				}

				final GFile localFile = cache.getFile(change.getFileId());
				if (localFile == null) {
					// TODO: arreglar el path?
					GFile changedFile = change.getFile();
					if (!changedFile.isDirectory()) {
						// if it's a directory we don't set revision so it's
						// synchronized afterwards
						changedFile.setRevision(change.getRevision());
					}
					LOG.info("New remote file: " + changedFile.getId());
					cache.addOrUpdateFile(changedFile);
				} else {
					// File updated
					GFile changedFile = change.getFile();
					changedFile.setRevision(change.getRevision());
					cache.addOrUpdateFile(changedFile);
					LOG.info("Remote update: " + localFile);
				}
			}

			private void syncPendingFolders() {
				LOG.debug("Checking for pending folders to synchronize...");
				try {
					// always sync pending directories first
					List<String> unsynchChilds;
					while (!(unsynchChilds = cache.getAllFoldersWithoutRevision()).isEmpty()) {

						LOG.info("Folders to synchronize: " + unsynchChilds.size());

						List<Callable<Void>> tasks = new ArrayList<>();
						for (int i = 0; i < 10 && i < unsynchChilds.size(); i++) {
							final String unsynchChild = unsynchChilds.get(i);
							LOG.debug("Creating synch task for '" + unsynchChild + "'...");
							tasks.add(new Callable<Void>() {
								final String folderId = unsynchChild;

								@Override
								public Void call() {
									synchFolder(folderId);
									return null;
								}
							});
						}

						LOG.debug("Executing " + tasks.size() + " tasks...");
						List<Future<Void>> futures = executor.invokeAll(tasks);
						LOG.debug("Waiting for all executions to finish...");
						while (!futures.isEmpty()) {
							Thread.sleep(200);
							LOG.trace(".");
							futures.removeIf(Future::isDone);
						}

						LOG.debug("All executions finished to run");
						syncPendingFolders();
					}
				} catch (InterruptedException e) {
					LOG.error(e.getMessage(), e);
				}
				LOG.debug("Synchronization finalized OK");
			}
		};

	}

	/**
	 * Get remote folder and it's children and updates database
	 *
	 * @param folderId
	 *            el id de la carpeta remota ("root" para especificar la raiz)
	 */
	private void synchFolder(String folderId) {
		try {
			GFile remoteFile;
			if (folderId.equals("root")) {
				remoteFile = cache.getFile("root");
			} else {
				remoteFile = googleDrive.getFile(folderId);
			}

			if (remoteFile == null || remoteFile.getTrashed()) {
				LOG.info("Remote deletion: " + folderId);
				final int deleted = cache.deleteFile(folderId);
				if (deleted > 0) {
					LOG.info("Local deletion: " + folderId);
				} else {
					LOG.info("Location deletion: 0");
				}
				return;
			}

			if (!remoteFile.isDirectory()) {
				throw new IllegalArgumentException("Can't sync folder '" + folderId + "' because it is a regular file");
			}

			// Log action
			if (cache.getFile(folderId) == null) {
				LOG.info("Adding folder '" + remoteFile.getId() + "'");
			} else {
				LOG.info("Updating folder '" + remoteFile.getId() + "'");
			}

			String largestChangeId = cache.getRevision();

			LOG.debug("Recreating childs for folder '" + folderId + "'");
			List<GFile> newChilds = googleDrive.list(folderId);
			for (GFile file : newChilds) {
				if (!file.isDirectory())
					file.setRevision(largestChangeId);
			}

			remoteFile.setRevision(largestChangeId);

			LOG.info("Adding folder: '" + remoteFile.getId() + "' childs: " + newChilds.size());
			cache.updateChilds(remoteFile, newChilds);
		} catch (Error e) {
			LOG.fatal(e.getMessage(), e);
		}
	}

}
