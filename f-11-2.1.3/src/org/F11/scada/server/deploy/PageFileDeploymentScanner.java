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
 * �y�[�W��`�t�@�C���̔z���X�L���i�[�N���X�ł��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageFileDeploymentScanner {
	/** ���M���OAPI */
	private static Logger logger = Logger
			.getLogger(PageFileDeploymentScanner.class);
	/** �X�L�������J��Ԃ��Ԋu�ł��B */
	private volatile long period;
	/** �X�L��������^�C�}�[�I�u�W�F�N�g */
	private Timer timer;
	/** �z�����[�g�f�B���N�g���̃��X�g�ł� */
	private List fileList = Collections.synchronizedList(new ArrayList());
	/**
	 * �f�B���N�g�����g���q��java�̃t�@�C���𒊏o����t�B���^�[�ł�
	 */
	private static FileFilter FILTER = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.isDirectory()
					|| pathname.getName().endsWith(".xml") ? true : false;
		}
	};
	/** �z���ς݃y�[�W��`�t�@�C����Set�I�u�W�F�N�g�ł� */
	private Set deployedSet = Collections.synchronizedSet(new HashSet());
	/** �z���t�@�C���N���X�̃R���p���[�^�ł� */
	private static Comparator SORTER = new Comparator() {
		public int compare(Object o1, Object o2) {
			DeployedFile f1 = (DeployedFile) o1;
			DeployedFile f2 = (DeployedFile) o2;
			return f1.file.compareTo(f2.file);
		}
	};
	/** �z���I�u�W�F�N�g�̎Q�� */
	private Deployer deployer;
	/** ���b�N�I�u�W�F�N�g */
	private Lock lock;
	/** ���b�N�����ϐ� */
	private Condition condition;

	/**
	 * �y�[�W��`�z���I�u�W�F�N�g���w�肵�ď��������܂��B
	 * 
	 * @param deployer �y�[�W��`�z���I�u�W�F�N�g
	 */
	public PageFileDeploymentScanner(Deployer deployer, long period) {
		this.deployer = deployer;
		this.lock = new ReentrantLock();
		this.condition = lock.newCondition();
		this.period = period;
		reSchedule();
	}

	/**
	 * �y�[�W��`�z���I�u�W�F�N�g���w�肵�ď��������܂��B
	 * 
	 * @param deployer �y�[�W��`�z���I�u�W�F�N�g
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
	 * �y�[�W��`�̃X�L���j���O�������~���܂��B
	 */
	public void terminate() {
		timer.cancel();
	}

	/**
	 * �X�L���j���O�p�x���~���b�ŕԂ��܂�
	 * 
	 * @return �X�L���j���O�p�x���~���b�ŕԂ��܂�
	 */
	public long getPeriod() {
		return period;
	}

	/**
	 * �X�L���j���O�p�x���~���b�Őݒ肵�܂�
	 * 
	 * @param period �X�L���j���O�p�x���~���b�Őݒ肵�܂�
	 */
	public void setPeriod(long period) {
		this.period = period;
		terminate();
		reSchedule();
	}

	/*
	 * yagni �z�����[�g�f�B���N�g����ݒ肵�܂� @param list �z�����[�g�f�B���N�g���̃��X�g public void
	 * setFileList(List list) { if (list == null) { throw new
	 * IllegalArgumentException("list is null."); }
	 * 
	 * fileList.clear();
	 * 
	 * for (Iterator it = list.iterator(); it.hasNext(); ) { File file = (File)
	 * it.next(); addFile(file); } }
	 */

	/**
	 * �z�����[�g�f�B���N�g����ǉ����܂��B
	 * 
	 * @param file �ǉ�����z�����[�g�f�B���N�g��
	 */
	public void addFile(File file) {
		if (file == null) {
			throw new IllegalArgumentException("file is null.");
		}

		fileList.add(file);
	}

	/**
	 * �z�����[�g�f�B���N�g�����폜���܂��B
	 * 
	 * @param file �폜����z�����[�g�f�B���N�g��
	 */
	public void removeFile(File file) {
		if (file == null) {
			throw new IllegalArgumentException("file is null.");
		}

		fileList.remove(file);
	}

	/**
	 * �X�L��������^�C�}�[�^�X�N�̎����N���X�ł��B
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	private class ScanTimerTask extends TimerTask {

		/**
		 * �t�@�C���𒲍����āA�z���E��z���������s���܂��B
		 */
		public void run() {
			// �t�@�C���ꗗ�����
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

			// �V�K�t�@�C���ƍ폜�t�@�C���𔻒�
			List filesToRemove = new ArrayList();
			List filesToCheckForUpdate = new ArrayList();
			synchronized (deployedSet) {
				// �폜�t�@�C���ƍX�V�`�F�b�N�t�@�C����U�蕪����
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
			// �폜�t�@�C�����z������
			for (Iterator i = filesToRemove.iterator(); i.hasNext();) {
				DeployedFile deployedFile = (DeployedFile) i.next();
				if (logger.isDebugEnabled()) {
					logger.debug("Removing " + deployedFile.file);
				}
				undeploy(deployedFile);
			}

			// �X�V�`�F�b�N�t�@�C������X�V�t�@�C���𒊏o
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
			// �X�V�t�@�C�����\�[�g
			Collections.sort(filesToUpdate, SORTER);
			// �X�V�t�@�C�����Ĕz������
			for (int i = filesToUpdate.size() - 1; i >= 0; i--) {
				undeploy((DeployedFile) filesToUpdate.get(i));
			}
			for (int i = 0; i < filesToUpdate.size(); i++) {
				deploy((DeployedFile) filesToUpdate.get(i));
			}

			// �V�K�t�@�C����z������
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
	 * �z������
	 * 
	 * @param df �z���t�@�C���I�u�W�F�N�g
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
	 * ��z������
	 * 
	 * @param df �z���t�@�C���I�u�W�F�N�g
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
	 * �z���ς݂̃t�@�C���I�u�W�F�N�g��\���N���X�ł��B
	 */
	private class DeployedFile {
		/** �z�������y�[�W��`�t�@�C�� */
		public File file;
		/** �z�������ŏI���t */
		public long deployedLastModified;

		/**
		 * �R���X�g���N�^
		 * 
		 * @param file �y�[�W��`�t�@�C��
		 */
		public DeployedFile(final File file) {
			this.file = file;
		}

		/**
		 * ���̃I�u�W�F�N�g�̔z���������s���܂��B
		 */
		public void deployed() {
			deployedLastModified = getLastModified();
		}

		/**
		 * ���̃t�@�C���I�u�W�F�N�g���A�t�@�C����\���ꍇ�� true ��Ԃ��܂��B
		 * 
		 * @return ���̃t�@�C���I�u�W�F�N�g���A�t�@�C����\���ꍇ�� true ��Ԃ��܂��B
		 */
		public boolean isFile() {
			return file.isFile();
		}

		/**
		 * �t�@�C���I�u�W�F�N�g��Ԃ��܂��B
		 * 
		 * @return �t�@�C���I�u�W�F�N�g��Ԃ��܂��B
		 */
		public File getFile() {
			return file;
		}

		/**
		 * ���̔z���ς݃y�[�W��`�t�@�C�����폜����Ă���ꍇ�� true ��Ԃ��܂��B
		 * 
		 * @return ���̔z���ς݃y�[�W��`�t�@�C�����폜����Ă���ꍇ�� true ��Ԃ��܂��B
		 */
		public boolean isRemoved() {
			if (isFile()) {
				return !file.exists();
			}
			return false;
		}

		/**
		 * �t�@�C���̍ŏI�X�V���t��Ԃ��܂�
		 * 
		 * @return �t�@�C���̍ŏI�X�V���t��Ԃ��܂�
		 */
		public long getLastModified() {
			return file.lastModified();
		}

		/**
		 * �t�@�C�����X�V����Ă���ꍇ�� true ��Ԃ��܂�
		 * 
		 * @return �t�@�C�����X�V����Ă���ꍇ�� true ��Ԃ��܂�
		 */
		public boolean isModified() {
			long lastModified = getLastModified();
			if (lastModified == -1) {
				return false;
			}
			return deployedLastModified != lastModified;
		}

		/**
		 * �n�b�V���R�[�h��Ԃ��܂�
		 * 
		 * @return �n�b�V���R�[�h��Ԃ��܂�
		 */
		public int hashCode() {
			return file.hashCode();
		}

		/**
		 * ���̃I�u�W�F�N�g�Ƃ��̃I�u�W�F�N�g���r�������ʂ�Ԃ��܂��B
		 * 
		 * @param other ��r����I�u�W�F�N�g
		 */
		public boolean equals(final Object other) {
			if (other instanceof DeployedFile) {
				return ((DeployedFile) other).file.equals(this.file);
			}
			return false;
		}

		/**
		 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
		 */
		public String toString() {
			return super.toString() + "{ file=" + file
					+ ", deployedLastModified=" + deployedLastModified + " }";
		}
	}
}
