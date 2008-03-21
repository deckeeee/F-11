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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.applet.graph.LoggingData;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.server.event.LoggingDataEvent;
import org.F11.scada.server.event.LoggingDataListener;
import org.F11.scada.server.io.AutoPrintDataService;
import org.F11.scada.server.io.AutoPrintDataStore;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.io.ValueListHandler;
import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.F11.scada.server.logging.report.schedule.BMSSchedule;
import org.F11.scada.server.logging.report.schedule.CsvSchedule;
import org.F11.scada.server.logging.report.schedule.CsvScheduleFactory;
import org.F11.scada.server.logging.report.schedule.GODAMarker;
import org.F11.scada.server.register.HolderString;
import org.apache.log4j.Logger;
import org.seasar.framework.container.S2Container;

/**
 * @author hori
 */
public class CsvoutTask implements LoggingDataListener {
	/** ���M���O�e�[�u���� */
	private String logg_name;
	/** �n���h�� */
	private ValueListHandler handlerManager;
	/** �f�[�^�z���_�̃��X�g */
	private List dataHolders;
	/** CSV�t�@�C���̕ۑ��f�B���N�g���� */
	private String currDir;
	/** CSV�t�@�C�����̐擪���� */
	private String csv_head;
	/** CSV�t�@�C�����̓��t�t�H�[�}�b�g */
	private String csv_mid;
	/** CSV�t�@�C�����̖������� */
	private String csv_foot;
	/** CSV�t�@�C���̕ۑ����� */
	private int keepCount;
	/** ���R�[�h�w�b�_�̕t���t���O */
	private boolean data_head;
	/** ���R�[�h�������t���Z�o����w���p�N���X */
	private CsvSchedule csvSchedule;
	/** �t�@�C���o�͊J�n���Ԃ̎�� true = 0:0�`23:59:59 false = 0:0:1�`0:0:0 */
	private boolean dataMode;
	/** �A�C�e���f�[�^�擾���[�e�B���e�B�[ */
	private final ItemUtil util;
	/** �v�����g�f�[�^�X�V�N���X */
	private final AutoPrintDataService stor = new AutoPrintDataStore();

	/** ���M���OAPI */
	private static Logger logger;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param name ���M���O��
	 * @param dataHolders �f�[�^�z���_�[�̃��X�g
	 * @param factoryName �f�[�^�i���N���X��
	 * @exception SQLException DBMS�ɐڑ��ł��Ȃ������Ƃ�
	 */
	public CsvoutTask(
			String logg_name,
			ValueListHandler handlerManager,
			String schedule,
			List dataHolders,
			String currDir,
			String csv_head,
			String csv_mid,
			String csv_foot,
			int keepCount,
			boolean data_head,
			boolean dataMode) throws NoSuchFieldException,
			IllegalAccessException {
		super();
		logger = Logger.getLogger(getClass().getName());

		this.logg_name = logg_name;
		this.dataHolders = dataHolders;
		this.currDir = currDir;
		this.csv_head = csv_head;
		this.csv_mid = csv_mid;
		this.csv_foot = csv_foot;
		this.keepCount = keepCount;
		this.data_head = data_head;
		this.dataMode = dataMode;
		this.handlerManager = handlerManager;
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
						+ fileDf.format(startTime) + csv_foot);
				newFile.delete();
				tmpFile.renameTo(newFile);
			}
			// ���t�@�C���폜
			FilenameFilter filter = new CsvFilter(csv_head, csv_foot);
			removeOldFile(dir.listFiles(filter), keepCount);
		}
	}

	/**
	 * CSV�t�@�C���쐬
	 * 
	 * @param file �쐬����CSV�t�@�C��
	 * @return �擪���R�[�h�̓��t
	 */
	private Timestamp csvOut(File file) {
		logger.debug("csv out start!!");
		Timestamp startTime = null;
		BufferedWriter out = null;
		try {
			// csv �쐬
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file),
					"Windows-31J"));
			dataHeadWrite(out);
			Timestamp st = csvSchedule.startTime(
					System.currentTimeMillis(),
					dataMode);
			handlerManager.findRecord(logg_name, st);
			if (csvSchedule instanceof BMSSchedule) {
				startTime = bmsWrite(startTime, out);
			} else if (GODAMarker.class.isInstance(csvSchedule)) {
				startTime = nomalWrite(startTime, out, new SimpleDateFormat(
						"yyyy/MM/dd HH:mm"));
			} else if (dataMode) {
				startTime = nomalWrite(startTime, out, new SimpleDateFormat(
						"yyyy/MM/dd,HH:mm:ss"));
			} else {
				startTime = aAndAWrite(startTime, out);
			}
			out.flush();
		} catch (IOException e) {
			logger.fatal("CSV�t�@�C���o�� IO �G���[���� : ", e);
		} catch (Exception e) {
			logger.fatal("CSV�t�@�C���o�� �G���[���� : ", e);
		} finally {
			try {
				if (null != out) {
					out.close();
				}
			} catch (IOException e) {
				logger.fatal("CSV�t�@�C���o�� IO �G���[���� : ", e);
			}
		}
		return startTime;
	}

	/**
	 * @param out
	 * @throws IOException
	 */
	private void dataHeadWrite(BufferedWriter out) throws IOException {
		if (csvSchedule instanceof BMSSchedule) {
			out.write("���t,����");
			for (Iterator i = dataHolders.iterator(); i.hasNext();) {
				HolderString hs = (HolderString) i.next();
				out.write(",");
				out.write(hs.getHolder());
			}
			out.newLine();
		} else if (GODAMarker.class.isInstance(csvSchedule)) {
			out.write("");
			for (Iterator i = dataHolders.iterator(); i.hasNext();) {
				HolderString hs = (HolderString) i.next();
				out.write(",");
				out.write(hs.getHolder());
			}
			out.newLine();
			List hl = stor.getLoggingHeddarList(logg_name, dataHolders);
			out.write("");
			for (Iterator it = hl.iterator(); it.hasNext();) {
				Map rec = (Map) it.next();
				out.write(",");
				out.write((String) rec.get("name"));
			}
			out.newLine();
		} else if (data_head) {
			List hl = stor.getLoggingHeddarList(logg_name, dataHolders);
			out.write("���t,����");
			for (Iterator it = hl.iterator(); it.hasNext();) {
				Map rec = (Map) it.next();
				out.write(",");
				out.write((String) rec.get("unit"));
			}
			out.newLine();
			out.write("���t,����");
			for (Iterator it = hl.iterator(); it.hasNext();) {
				Map rec = (Map) it.next();
				out.write(",");
				out.write((String) rec.get("name"));
			}
			out.newLine();
			out.write("���t,����");
			for (Iterator it = hl.iterator(); it.hasNext();) {
				Map rec = (Map) it.next();
				out.write(",");
				out.write((String) rec.get("unit_mark"));
			}
			out.newLine();
		}
	}

	/**
	 * @param df
	 * @param startTime
	 * @param out
	 * @param st
	 * @return
	 * @throws IOException
	 */
	private Timestamp aAndAWrite(Timestamp startTime, BufferedWriter out)
			throws IOException {
		Timestamp st = csvSchedule.startTime(
				System.currentTimeMillis(),
				dataMode);
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss");

		while (handlerManager.hasNext(logg_name)) {
			LoggingData data = (LoggingData) handlerManager.next(logg_name);
			if (st.after(data.getTimestamp()))
				continue;
			out.write(df.format(data.getTimestamp()));
			data.first();
			ConvertValue[] convertValues = util.createConvertValue(
					dataHolders,
					logg_name);

			for (int i = 0; i < convertValues.length; i++) {
				ConvertValue conv = convertValues[i];
				double dd = data.next();

				out.write(',');
				out.write(conv.convertStringValue(conv.convertInputValue(dd)));
			}
			out.newLine();

			if (startTime == null)
				startTime = data.getTimestamp();
		}
		return startTime;
	}

	/**
	 * @param df
	 * @param startTime
	 * @param out
	 * @param st
	 * @return
	 * @throws IOException
	 */
	private Timestamp bmsWrite(Timestamp startTime, BufferedWriter out)
			throws IOException {
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd,HH:mm:ss");

		LoggingData data = (LoggingData) handlerManager.next(logg_name);
		out.write(df.format(data.getTimestamp()));
		data.first();
		ConvertValue[] convertValues = util.createConvertValue(
				dataHolders,
				logg_name);
		for (int i = 0; i < convertValues.length; i++) {
			ConvertValue conv = convertValues[i];
			double dd = data.next();

			out.write(',');
			out.write(conv.convertStringValue(conv.convertInputValue(dd)));
		}
		out.newLine();

		if (startTime == null)
			startTime = data.getTimestamp();
		return startTime;
	}

	/**
	 * @param df
	 * @param startTime
	 * @param out
	 * @param st
	 * @param endTime
	 * @return
	 * @throws IOException
	 */
	private Timestamp nomalWrite(
			Timestamp startTime,
			BufferedWriter out,
			DateFormat df) throws IOException {
		Timestamp st = csvSchedule.startTime(
				System.currentTimeMillis(),
				dataMode);
		Timestamp endTime = csvSchedule.endTime(
				System.currentTimeMillis(),
				dataMode);
		logger.debug(st + "�`" + endTime);
		while (handlerManager.hasNext(logg_name)) {
			LoggingData data = (LoggingData) handlerManager.next(logg_name);
			if (data.getTimestamp().equals(st)
					|| (data.getTimestamp().after(st) && data.getTimestamp()
							.before(endTime))) {
				out.write(df.format(data.getTimestamp()));
				data.first();
				ConvertValue[] convertValues = util.createConvertValue(
						dataHolders,
						logg_name);
				for (int i = 0; i < convertValues.length; i++) {
					ConvertValue conv = convertValues[i];
					double dd = data.next();
					out.write(',');
					out.write(conv.convertStringValue(conv
							.convertInputValue(dd)));
				}
				out.newLine();

				if (startTime == null)
					startTime = data.getTimestamp();
			}
		}
		return startTime;
	}

	/**
	 * �w�茏�����c���A�ҏW���t�̌Â����Ƀt�@�C�����폜����
	 * 
	 * @param files �t�@�C���̈ꗗ
	 * @param cnt �c������
	 */
	private void removeOldFile(File[] files, int cnt) {
		if (null == files || files.length <= cnt)
			return;

		java.util.List fileList = new ArrayList(files.length);
		for (int i = 0; i < files.length; i++) {
			fileList.add(files[i]);
		}
		while (cnt < fileList.size()) {
			long first = System.currentTimeMillis();
			File firstFile = null;
			for (Iterator it = fileList.iterator(); it.hasNext();) {
				File file = (File) it.next();
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
	 * To change this generated comment go to Window>Preferences>Java>Code
	 * Generation>Code Template
	 */
	private class CsvFilter implements FilenameFilter {
		private String head;
		private String foot;

		public CsvFilter(String head, String foot) {
			this.head = head;
			this.foot = foot;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		public boolean accept(File dir, String name) {
			if (name.startsWith(head) && name.endsWith(foot))
				return true;
			return false;
		}
	}
}