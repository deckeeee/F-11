/*
 * �쐬��: 2005/08/16 TODO ���̐������ꂽ�t�@�C���̃e���v���[�g��ύX����ɂ͎��փW�����v: �E�B���h�E - �ݒ� - Java -
 * �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
package org.F11.scada.server.entity;

import java.io.Serializable;

/**
 * @author hori TODO ���̐������ꂽ�^�R�����g�̃e���v���[�g��ύX����ɂ͎��փW�����v: �E�B���h�E - �ݒ� - Java -
 *         �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
public class MultiRecordDefine implements Serializable {

	private static final long serialVersionUID = -1237946957029180270L;
	public static final String TABLE = "multi_record_define_table";
	public static final String loggingTableName_COLUMN = "logging_table_name";
	public static final String provider_COLUMN = "provider";
	public static final String comMemoryKinds_COLUMN = "com_memory_kinds";
	public static final String comMemoryAddress_COLUMN = "com_memory_address";
	public static final String wordLength_COLUMN = "word_length";
	public static final String recordCount_COLUMN = "record_count";

	private String loggingTableName;
	private String provider;
	private int comMemoryKinds;
	private long comMemoryAddress;
	private int wordLength;
	private int recordCount;

	public long getComMemoryAddress() {
		return comMemoryAddress;
	}
	public void setComMemoryAddress(long comMemoryAddress) {
		this.comMemoryAddress = comMemoryAddress;
	}
	public int getComMemoryKinds() {
		return comMemoryKinds;
	}
	public void setComMemoryKinds(int comMemoryKinds) {
		this.comMemoryKinds = comMemoryKinds;
	}
	public String getLoggingTableName() {
		return loggingTableName;
	}
	public void setLoggingTableName(String loggingTableName) {
		this.loggingTableName = loggingTableName;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public int getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
	public int getWordLength() {
		return wordLength;
	}
	public void setWordLength(int wordLength) {
		this.wordLength = wordLength;
	}

	public String toString() {
		return loggingTableName + " " + provider + " " + comMemoryKinds + " "
				+ comMemoryAddress + " " + wordLength + " " + recordCount;
	}
}
