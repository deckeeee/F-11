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
 *
 */

package org.F11.scada.server.logging.report;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.F11.scada.server.event.LoggingDataEvent;
import org.F11.scada.server.event.LoggingDataListener;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.io.ValueListHandler;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.F11.scada.server.logging.report.schedule.CsvSchedule;
import org.F11.scada.server.logging.report.schedule.CsvScheduleFactory;
import org.F11.scada.server.register.HolderString;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

/**
 * @author maekawa
 */
public abstract class AbstractCsvoutTask implements LoggingDataListener {
	/** ���M���O�e�[�u���� */
	protected String logg_name;
	/** �n���h�� */
	protected ValueListHandler handlerManager;
	/** �f�[�^�z���_�̃��X�g */
	protected List<HolderString> dataHolders;
	/** CSV�t�@�C���̕ۑ��f�B���N�g���� */
	protected String currDir;
	/** CSV�t�@�C�����̐擪���� */
	protected String csv_head;
	/** CSV�t�@�C�����̓��t�t�H�[�}�b�g */
	protected String csv_mid;
	/** CSV�t�@�C�����̖������� */
	protected String csv_foot;
	/** CSV�t�@�C���̕ۑ����� */
	protected int keepCount;
	/** ���R�[�h�w�b�_�̕t���t���O */
	protected boolean data_head = true;
	/** ���R�[�h�������t���Z�o����w���p�N���X */
	protected CsvSchedule csvSchedule;
	/** �t�@�C���o�͊J�n���Ԃ̎�� true = 0:0�`23:59:59 false = 0:0:1�`0:0:0 */
	protected boolean dataMode = true;
	/** �A�C�e���f�[�^�擾���[�e�B���e�B�[ */
	protected final ItemUtil util;
	/** �t�@�C���������I�t�Z�b�g(�~���b) */
	protected final long midOffset;
	/** ��������e�[�u�� */
	protected final List<String> tables;

	/** ���M���OAPI */
	protected static Logger logger = Logger.getLogger(AbstractCsvoutTask.class);

	/**
	 * �R���X�g���N�^
	 * 
	 * @param name ���M���O��
	 * @param dataHolders �f�[�^�z���_�[�̃��X�g
	 * @param midOffset
	 * @param factoryName �f�[�^�i���N���X��
	 * @exception SQLException DBMS�ɐڑ��ł��Ȃ������Ƃ�
	 */
	public AbstractCsvoutTask(
			String logg_name,
			ValueListHandler handlerManager,
			String schedule,
			List<HolderString> dataHolders,
			String currDir,
			String csv_head,
			String csv_mid,
			String csv_foot,
			int keepCount,
			long midOffset,
			List<String> tables) throws NoSuchFieldException, IllegalAccessException {
		super();

		this.logg_name = logg_name;
		this.dataHolders = dataHolders;
		this.currDir = currDir;
		this.csv_head = csv_head;
		this.csv_mid = csv_mid;
		this.csv_foot = csv_foot;
		this.keepCount = keepCount;
		this.handlerManager = handlerManager;
		this.midOffset = midOffset;
		this.tables = tables;
		CsvScheduleFactory factory = new CsvScheduleFactory();
		csvSchedule = factory.getCsvSchedule(schedule);
		S2Container container = S2ContainerUtil.getS2Container();
		util = (ItemUtil) container.getComponent("itemutil");
	}

	public void changeLoggingData(LoggingDataEvent event) {
		if (csvSchedule.isOutput()) {
			File dir = new File(currDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// �ꎞ�t�@�C���쐬
			File tmpFile = new File(currDir + logg_name + csv_foot);
			Timestamp startTime = csvOut(tmpFile);
			if (startTime != null) {
				// �t�@�C�����ύX
				DateFormat fileDf = new SimpleDateFormat(csv_mid);
				File newFile = new File(currDir + csv_head
						+ fileDf.format(getMidTime(startTime)) + csv_foot);
				newFile.delete();
				tmpFile.renameTo(newFile);
			}
			// ���t�@�C���폜
			FilenameFilter filter = new CsvFilter(csv_head, csv_foot);
			removeOldFile(dir.listFiles(filter), keepCount);
		}
	}

	private Timestamp getMidTime(Timestamp startTime) {
		return new Timestamp(startTime.getTime() - midOffset);
	}

	/**
	 * CSV�t�@�C���쐬
	 * 
	 * @param file �쐬����CSV�t�@�C��
	 * @return �擪���R�[�h�̓��t
	 */
	abstract protected Timestamp csvOut(File file);

	/**
	 * �w�茏�����c���A�ҏW���t�̌Â����Ƀt�@�C�����폜����
	 * 
	 * @param files �t�@�C���̈ꗗ
	 * @param cnt �c������
	 */
	protected void removeOldFile(File[] files, int cnt) {
		if (null == files || files.length <= cnt)
			return;

		ArrayList<File> fileList = new ArrayList<File>(files.length);
		for (int i = 0; i < files.length; i++) {
			fileList.add(files[i]);
		}
		while (cnt < fileList.size()) {
			long first = System.currentTimeMillis();
			File firstFile = null;
			for (Iterator<File> it = fileList.iterator(); it.hasNext();) {
				File file = it.next();
				if (file.lastModified() < first) {
					first = file.lastModified();
					firstFile = file;
				}
			}
			firstFile.delete();
			fileList.remove(firstFile);
		}
	}

	/**
	 * �擪�����Ɩ����������w�肷��t�@�C���t�B���^�[�N���X
	 * 
	 * @author hori
	 * 
	 */
	protected class CsvFilter implements FilenameFilter {
		private String head;
		private String foot;

		public CsvFilter(String head, String foot) {
			this.head = head;
			this.foot = foot;
		}

		public boolean accept(File dir, String name) {
			return name.startsWith(head) && name.endsWith(foot);
		}
	}
}
