/*
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002 Freedom, Inc. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.F11.scada.server.deploy;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import org.F11.scada.EnvironmentManager;
import org.apache.log4j.Logger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ページ定義ファイルの配備スキャナークラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageFileDeploymentScanner {
	/** ロギングAPI */
	private static Logger logger = Logger
			.getLogger(PageFileDeploymentScanner.class);
	/** スキャンを繰り返す間隔です。 */
	private volatile long period;
	/** スキャンするタイマーオブジェクト */
	private Timer timer;
	/** 配備ルートディレクトリのリストです */
	private List fileList = Collections.synchronizedList(new ArrayList());
	/**
	 * ディレクトリか拡張子がjavaのファイルを抽出するフィルターです
	 */
	private static FileFilter FILTER = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.isDirectory()
					|| pathname.getName().endsWith(".xml") ? true : false;
		}
	};
	/** 配備済みページ定義ファイルのSetオブジェクトです */
	private Set deployedSet = Collections.synchronizedSet(new HashSet());
	/** 配備ファイルクラスのコンパレータです */
	private static Comparator SORTER = new Comparator() {
		public int compare(Object o1, Object o2) {
			DeployedFile f1 = (DeployedFile) o1;
			DeployedFile f2 = (DeployedFile) o2;
			return f1.file.compareTo(f2.file);
		}
	};
	/** 配備オブジェクトの参照 */
	private Deployer deployer;
	/** ロックオブジェクト */
	private Lock lock;
	/** ロック条件変数 */
	private Condition condition;

	/**
	 * ページ定義配備オブジェクトを指定して初期化します。
	 * 
	 * @param deployer ページ定義配備オブジェクト
	 */
	public PageFileDeploymentScanner(Deployer deployer, long period) {
		this.deployer = deployer;
		this.lock = new ReentrantLock();
		this.condition = lock.newCondition();
		this.period = period;
		reSchedule();
	}

	/**
	 * ページ定義配備オブジェクトを指定して初期化します。
	 * 
	 * @param deployer ページ定義配備オブジェクト
	 */
	public PageFileDeploymentScanner(
			Deployer deployer,
			Lock lock,
			Condition condition) {
		this.deployer = deployer;
		this.lock = lock;
		this.condition = condition;
		period = createPeripod();
		reSchedule();
	}

	private long createPeripod() {
		return Long.parseLong(EnvironmentManager.get(
				"/server/deploy/period",
				"69896"));
	}

	private void reSchedule() {
		timer = new Timer();
		timer.schedule(new ScanTimerTask(), new Date(), period);
	}

	/**
	 * ページ定義のスキャニング処理を停止します。
	 */
	public void terminate() {
		timer.cancel();
	}

	/**
	 * スキャニング頻度をミリ秒で返します
	 * 
	 * @return スキャニング頻度をミリ秒で返します
	 */
	public long getPeriod() {
		return period;
	}

	/**
	 * スキャニング頻度をミリ秒で設定します
	 * 
	 * @param period スキャニング頻度をミリ秒で設定します
	 */
	public void setPeriod(long period) {
		this.period = period;
		terminate();
		reSchedule();
	}

	/*
	 * yagni 配備ルートディレクトリを設定します @param list 配備ルートディレクトリのリスト public void
	 * setFileList(List list) { if (list == null) { throw new
	 * IllegalArgumentException("list is null."); }
	 * 
	 * fileList.clear();
	 * 
	 * for (Iterator it = list.iterator(); it.hasNext(); ) { File file = (File)
	 * it.next(); addFile(file); } }
	 */

	/**
	 * 配備ルートディレクトリを追加します。
	 * 
	 * @param file 追加する配備ルートディレクトリ
	 */
	public void addFile(File file) {
		if (file == null) {
			throw new IllegalArgumentException("file is null.");
		}

		fileList.add(file);
	}

	/**
	 * 配備ルートディレクトリを削除します。
	 * 
	 * @param file 削除する配備ルートディレクトリ
	 */
	public void removeFile(File file) {
		if (file == null) {
			throw new IllegalArgumentException("file is null.");
		}

		fileList.remove(file);
	}

	/**
	 * スキャンするタイマータスクの実装クラスです。
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	private class ScanTimerTask extends TimerTask {

		/**
		 * ファイルを調査して、配備・非配備処理を行います。
		 */
		public void run() {
			// ファイル一覧を取る
			SortedSet filesToDeploy = new TreeSet();

			synchronized (fileList) {
				for (Iterator it = fileList.iterator(); it.hasNext();) {
					File root = (File) it.next();
					if (root.exists()) {
						if (root.isDirectory()) {
							FileLister lister = new FileLister();
							filesToDeploy
									.addAll(lister.listFiles(root, FILTER));
						} else {
							filesToDeploy.add(root);
						}
					}
				}
			}

			// 新規ファイルと削除ファイルを判定
			List filesToRemove = new ArrayList();
			List filesToCheckForUpdate = new ArrayList();
			synchronized (deployedSet) {
				// 削除ファイルと更新チェックファイルを振り分ける
				for (Iterator i = deployedSet.iterator(); i.hasNext();) {
					DeployedFile deployedFile = (DeployedFile) i.next();
					if (filesToDeploy.contains(deployedFile.file)) {
						filesToCheckForUpdate.add(deployedFile);
					} else {
						filesToRemove.add(deployedFile);
					}
				}
			}

			lock.lock();
			try {
				deployAndUndeploy(
						filesToDeploy,
						filesToRemove,
						filesToCheckForUpdate);
				condition.signal();
			} finally {
				lock.unlock();
			}
		}

		private void deployAndUndeploy(
				SortedSet filesToDeploy,
				List filesToRemove,
				List filesToCheckForUpdate) {
			// 削除ファイルを非配備処理
			for (Iterator i = filesToRemove.iterator(); i.hasNext();) {
				DeployedFile deployedFile = (DeployedFile) i.next();
				if (logger.isDebugEnabled()) {
					logger.debug("Removing " + deployedFile.file);
				}
				undeploy(deployedFile);
			}

			// 更新チェックファイルから更新ファイルを抽出
			ArrayList filesToUpdate = new ArrayList(filesToCheckForUpdate
					.size());
			for (Iterator i = filesToCheckForUpdate.iterator(); i.hasNext();) {
				DeployedFile deployedFile = (DeployedFile) i.next();
				if (deployedFile.isModified()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Re-deploying " + deployedFile.file);
					}
					filesToUpdate.add(deployedFile);
				}
			}
			// 更新ファイルをソート
			Collections.sort(filesToUpdate, SORTER);
			// 更新ファイルを再配備処理
			for (int i = filesToUpdate.size() - 1; i >= 0; i--) {
				undeploy((DeployedFile) filesToUpdate.get(i));
			}
			for (int i = 0; i < filesToUpdate.size(); i++) {
				deploy((DeployedFile) filesToUpdate.get(i));
			}

			// 新規ファイルを配備処理
			// Collections.sort(filesToDeploy);
			for (Iterator i = filesToDeploy.iterator(); i.hasNext();) {
				File file = (File) i.next();
				DeployedFile deployedFile = new DeployedFile(file);
				if (!deployedSet.contains(deployedFile)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Deploying " + deployedFile.file);
					}
					deploy(deployedFile);
				}
			}
		}
	}

	/**
	 * 配備処理
	 * 
	 * @param df 配備ファイルオブジェクト
	 */
	private void deploy(final DeployedFile df) {
		if (deployer == null) {
			logger.fatal("Deployer not set.");
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Deploying: " + df);
		}

		try {
			deployer.deploy(df.file);
			// } catch (IncompleteDeploymentException e) {
			// lastIncompleteDeploymentException = e;
		} catch (DeploymentException e) {
			logger.error("Failed to deploy: " + df + e);
			e.printStackTrace();
			return;
		} // end of try-catch

		df.deployed();

		if (!deployedSet.contains(df)) {
			deployedSet.add(df);
		}
	}

	/**
	 * 非配備処理
	 * 
	 * @param df 配備ファイルオブジェクト
	 */
	private void undeploy(final DeployedFile df) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Undeploying: " + df);
			}
			deployer.undeploy(df.file);
			deployedSet.remove(df);
		} catch (Exception e) {
			logger.error("Failed to undeploy: " + df, e);
		}
	}

	/**
	 * 配備済みのファイルオブジェクトを表すクラスです。
	 */
	private class DeployedFile {
		/** 配備したページ定義ファイル */
		public File file;
		/** 配備した最終日付 */
		public long deployedLastModified;

		/**
		 * コンストラクタ
		 * 
		 * @param file ページ定義ファイル
		 */
		public DeployedFile(final File file) {
			this.file = file;
		}

		/**
		 * このオブジェクトの配備処理を行います。
		 */
		public void deployed() {
			deployedLastModified = getLastModified();
		}

		/**
		 * このファイルオブジェクトが、ファイルを表す場合に true を返します。
		 * 
		 * @return このファイルオブジェクトが、ファイルを表す場合に true を返します。
		 */
		public boolean isFile() {
			return file.isFile();
		}

		/**
		 * ファイルオブジェクトを返します。
		 * 
		 * @return ファイルオブジェクトを返します。
		 */
		public File getFile() {
			return file;
		}

		/**
		 * この配備済みページ定義ファイルが削除されている場合に true を返します。
		 * 
		 * @return この配備済みページ定義ファイルが削除されている場合に true を返します。
		 */
		public boolean isRemoved() {
			if (isFile()) {
				return !file.exists();
			}
			return false;
		}

		/**
		 * ファイルの最終更新日付を返します
		 * 
		 * @return ファイルの最終更新日付を返します
		 */
		public long getLastModified() {
			return file.lastModified();
		}

		/**
		 * ファイルが更新されている場合に true を返します
		 * 
		 * @return ファイルが更新されている場合に true を返します
		 */
		public boolean isModified() {
			long lastModified = getLastModified();
			if (lastModified == -1) {
				return false;
			}
			return deployedLastModified != lastModified;
		}

		/**
		 * ハッシュコードを返します
		 * 
		 * @return ハッシュコードを返します
		 */
		public int hashCode() {
			return file.hashCode();
		}

		/**
		 * 他のオブジェクトとこのオブジェクトを比較した結果を返します。
		 * 
		 * @param other 比較するオブジェクト
		 */
		public boolean equals(final Object other) {
			if (other instanceof DeployedFile) {
				return ((DeployedFile) other).file.equals(this.file);
			}
			return false;
		}

		/**
		 * このオブジェクトの文字列表現を返します。
		 */
		public String toString() {
			return super.toString() + "{ file=" + file
					+ ", deployedLastModified=" + deployedLastModified + " }";
		}
	}
}
