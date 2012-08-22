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

package org.F11.scada.server.command;

import static org.F11.scada.util.AttributesUtil.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.logging.report.CsvFilter;
import org.F11.scada.util.FileUtil;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.commons.digester.Digester;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * �C�ӂ̌`���Ńe�L�X�g�t�@�C�����o�͂ł���A�T�[�o�[�R�}���h�ł��B
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TextOutputCommand implements Command {
	/** �o�͒�`�t�@�C���p�X�� */
	private String defPath;
	/** �o�̓t�@�C���t�H���_ */
	private String outDir;
	/** �o�̓t�@�C���w�b�_ */
	private String outHead;
	/** �o�̓t�@�C�������t�H�[�}�b�g */
	private String outMid = "";
	/** �o�̓t�@�C���g���q */
	private String outFoot;
	/** �o�̓t�@�C���ێ��t�@�C���� */
	private int keep;
	/** true �̏ꍇ�A�f�[�^�̓t�@�C���̐擪�ł͂Ȃ��Ō�ɏ������܂�� */
	private boolean isAppend;
	/** �o�̓t�@�C���̕����G���R�[�f�B���O */
	private String csn = "Windows-31J";
	/** �������݃G���[���g���C�Ԋu */
	private long errorRetryTime = 10000;
	/** �������݃G���[���g���C�� */
	private int errorRetryCount = 10;
	/** �o�̓t�@�C�������t�H�[�}�b�g�̃I�t�Z�b�g(�b) */
	private long outMidOffset;
	/** �f�W�^���z���_�̏o�͌`��(true�Ȃ�boolean�`���Afalse�Ȃ�1 or 0�ŏo��) */
	private boolean isDigitalMode;
	/** Logging API */
	private static Logger log = Logger.getLogger(TextOutputCommand.class);
	/** �X���b�h�v�[�����s�N���X */
	private static Executor executor = Executors.newCachedThreadPool();
	/** �o�̓t�@�C����`�v���p�e�B */
	private List<Property> properties = new ArrayList<Property>();
	/** �e�X�g���[�h�̏ꍇ�o�͒�`�v���p�e�B�𖈉�ǂݒ��� */
	private boolean isTestMode;

	/**
	 * �R�}���h�����s���܂�
	 *
	 * @param evt �f�[�^�ύX�C�x���g
	 */
	public void execute(DataValueChangeEventKey evt) {
		checkPath();
		if (isTestMode) {
			properties.clear(); // �e�X�g�p
		}
		if (properties.isEmpty()) {
			perseXml();
		}
		executeTask(evt);
	}

	private void checkPath() {
		if (isSpaceOrNull(defPath)) {
			throw new IllegalStateException("�o�͒�`�t�@�C���p�X��(defPath)���ݒ肳��Ă��܂���B");
		}
		if (isSpaceOrNull(outDir)) {
			throw new IllegalStateException("�o�̓t�@�C���t�H���_(outDir)���ݒ肳��Ă��܂���B ");
		}
		if (isSpaceOrNull(outHead)) {
			throw new IllegalStateException("�o�̓t�@�C���w�b�_(outHead)���ݒ肳��Ă��܂���B ");
		}
		if (isSpaceOrNull(outFoot)) {
			throw new IllegalStateException("�o�̓t�@�C���g���q(outFoot)���ݒ肳��Ă��܂���B ");
		}
		if (!Charset.isSupported(csn)) {
			throw new IllegalStateException("�T�|�[�g����Ȃ������G���R�[�h���w�肳��܂����Bcsn = "
				+ csn);
		}
	}

	private void executeTask(DataValueChangeEventKey evt) {
		if (evt.getValue()) {
			try {
				executor.execute(new TextOutputCommandTask(evt));
			} catch (RejectedExecutionException e) {
				log.fatal("TextOutputCommandTask���s���ɃG���[������", e);
			}
		}
	}

	private void perseXml() {
		URL xml = getClass().getResource(defPath);
		if (xml == null) {
			IllegalStateException e =
				new IllegalStateException("�o�͒�`�t�@�C�� " + defPath + " �����݂��܂���B");
			log.error("�o�͒�`�t�@�C�� " + defPath + " �����݂��܂���B", e);
			throw e;
		}

		Digester digester = getDigester();
		InputStream is = null;
		try {
			is = xml.openStream();
			digester.parse(is);
		} catch (IOException e) {
			log.error("�o�͒�`�t�@�C���ǂݍ��ݒ���I/O�G���[������", e);
		} catch (SAXException e) {
			log.error("�o�͒�`�t�@�C����͒��ɕ��@�G���[������", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Digester getDigester() {
		Digester digester = new Digester();
		URL url = getClass().getResource("/resources/textoutput10.dtd");
		if (null == url) {
			throw new IllegalStateException(
				"/resources/textoutput10.dtd ���N���X�p�X��ɑ��݂��܂���");
		}
		digester.register(
			"-//F-11 2.0//DTD F11 Textout Configuration//EN",
			url.toString());
		digester.setValidating(true);
		digester.push(this);
		addPageRule(digester);
		return digester;
	}

	private void addPageRule(Digester digester) {
		digester.addCallMethod("textout/property", "addProperty", 2);
		digester.addCallParam("textout/property", 0, "name");
		digester.addCallParam("textout/property", 1, "value");
	}

	/**
	 * �e�L�X�g�o�͒�`�t�H�[�}�b�g���v���p�e�B�ɒǉ����܂�
	 *
	 * @param name �v���p�e�B��
	 * @param value �l
	 */
	public void addProperty(String name, String value) {
		Property o = new Property();
		o.setName(name);
		o.setValue(value);
		properties.add(o);
	}

	/**
	 * ��`�p�X����ݒ肵�܂�
	 *
	 * @param string ��`�p�X��
	 */
	public void setDefPath(String string) {
		defPath = string;
	}

	/**
	 * �o�̓f�B���N�g���[��ݒ肵�܂��B������"/"�łȂ���Βǉ����܂��B
	 *
	 * @param outDir �o�̓f�B���N�g���[��ݒ肵�܂��B������"/"�łȂ���Βǉ����܂��B
	 */
	public void setOutDir(String outDir) {
		if (outDir.endsWith("/") || outDir.endsWith("\\")) {
			this.outDir = outDir;
		} else {
			this.outDir = outDir + "/";
		}
	}

	public void setOutHead(String outHead) {
		this.outHead = outHead;
	}

	public void setOutMid(String outMid) {
		this.outMid = outMid;
	}

	public void setOutFoot(String outFoot) {
		this.outFoot = outFoot;
	}

	public void setKeep(int keep) {
		this.keep = keep;
	}

	public void setCsn(String csn) {
		this.csn = csn;
	}

	/**
	 * �������݃G���[���g���C�񐔂�ݒ肵�܂�
	 *
	 * @param i �������݃G���[���g���C��
	 */
	public void setErrorRetryCount(int i) {
		errorRetryCount = i;
	}

	/**
	 * �������݃G���[���g���C�Ԋu��ݒ肵�܂��B
	 *
	 * @param i �������݃G���[���g���C�Ԋu
	 */
	public void setErrorRetryTime(int i) {
		errorRetryTime = i;
	}

	public void setOutMidOffset(long outMidOffset) {
		this.outMidOffset = outMidOffset;
	}

	/**
	 * �e�X�g���[�h��ݒ肵�܂��B�e�X�g���[�h�͖���o�͒�`�v���p�e�B��ǂݒ����܂��B
	 *
	 * @param isTestMode true�̏ꍇ�e�X�g���[�h�B
	 */
	public void setTestMode(boolean isTestMode) {
		this.isTestMode = isTestMode;
	}

	/**
	 * �A�E�g�v�b�g�t�@�C���̏������[�h��ݒ肵�܂��Btrue�Ȃ�ǋL���������܂��B
	 *
	 * @param isAppend �A�E�g�v�b�g�t�@�C���̏������[�h��ݒ肵�܂��Btrue�Ȃ�ǋL���������܂��B
	 */
	public void setAppend(boolean isAppend) {
		this.isAppend = isAppend;
	}

	public void setDigitalMode(boolean isDigital) {
		this.isDigitalMode = isDigital;
	}

	/**
	 * Executor �Ŏ��s�����^�X�N�̃N���X�ł��B
	 *
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	private class TextOutputCommandTask implements Runnable {
		private final DataValueChangeEventKey evt;

		public TextOutputCommandTask(DataValueChangeEventKey evt) {
			this.evt = evt;
		}

		/**
		 * Executor �ɂ����s����郁�\�b�h�ł��B
		 */
		public void run() {
			PrintWriter out = null;
			try {
				for (int i = 1; i <= errorRetryCount; i++) {
					try {
						out =
							FileUtil
								.getPrintWriter(getOutPath(), isAppend, csn);
						writeFile(out);
						log.info("TextOutputCommandTask �o�͂��܂����B file = "
							+ getOutPath());
						break;
					} catch (UnsupportedEncodingException e) {
						log.info("�T�|�[�g����Ȃ������G���R�[�h���w�肳��܂����B csn = " + csn, e);
					} catch (IOException e) {
						logWrite(i);
						sleep();
						continue;
					}
				}
			} finally {
				if (out != null) {
					out.close();
				}
			}
			removeOldFile();
		}

		private String getOutPath() {
			Format f = new SimpleDateFormat(outMid);
			return outDir
				+ outHead
				+ f.format(new Date(evt.getTimeStamp().getTime()
					- outMidOffset
					* 1000))
				+ outFoot;
		}

		private void sleep() {
			try {
				Thread.sleep(errorRetryTime);
			} catch (InterruptedException e1) {
			}
		}

		private void removeOldFile() {
			FilenameFilter filter = new CsvFilter(outHead, outFoot);
			File dir = new File(outDir);
			FileUtil.removeOldFile(dir.listFiles(filter), keep);
		}

		private void logWrite(int i) {
			log.error("�������݃G���[���������܂����B"
				+ errorRetryTime
				+ "�~���b��Ƀ��g���C���܂��B ("
				+ i
				+ "/"
				+ errorRetryCount
				+ ")");
		}

		private void writeFile(PrintWriter out) {
			for (Property p : properties) {
				if (p.isData()) {
					DataHolder dh =
						Manager.getInstance().findDataHolder(p.getValue());
					if (dh == null) {
						IllegalStateException e =
							new IllegalStateException(p.getValue()
								+ " �����݂��܂���B");
						log.error("", e);
						throw e;
					} else {
						WifeData wa = (WifeData) dh.getValue();
						if (wa instanceof WifeDataAnalog) {
							WifeDataAnalog da = (WifeDataAnalog) dh.getValue();
							ConvertValue conv =
								(ConvertValue) dh
									.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
							out
								.print(conv.convertStringValue(da.doubleValue()));
						} else if (wa instanceof WifeDataDigital) {
							WifeDataDigital da =
								(WifeDataDigital) dh.getValue();
							boolean bit = da.isOnOff(true);
							out.print(getDigitalValue(bit));
						} else {
							IllegalStateException e =
								new IllegalStateException(p.getValue()
									+ " ���f�W�^�����̓A�i���O�ł͂���܂���B");
							log.error("", e);
							throw e;
						}
					}
				} else if ("text".equalsIgnoreCase(p.getName())) {
					out.print(p.getValue());
				} else {
					IllegalArgumentException e =
						new IllegalArgumentException(
							"name������text����data�ł͂���܂���B");
					log.error("", e);
					throw e;
				}
			}
		}

		private String getDigitalValue(boolean bit) {
			if (isDigitalMode) {
				return bit ? "true" : "false";
			} else {
				return bit ? "1" : "0";
			}
		}
	}

	/**
	 * �e�L�X�g�o�͂̃t�H�[�}�b�g��`�v���p�e�B name:text �� data��ݒ�B
	 * value:name������text�Ȃ�value�l�����̂܂܃e�L�X�g�o�́Bdata�Ȃ�z���_�̓��e���o�́B
	 *
	 * @author maekawa
	 *
	 */
	private static class Property {
		private String name;
		private String value;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public boolean isData() {
			return "data".equalsIgnoreCase(name);
		}

		@Override
		public String toString() {
			return name + " " + value;
		}
	}
}
