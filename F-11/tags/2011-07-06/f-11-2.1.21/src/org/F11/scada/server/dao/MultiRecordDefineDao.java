/*
 * �쐬��: 2005/08/19
 *
 * TODO ���̐������ꂽ�t�@�C���̃e���v���[�g��ύX����ɂ͎��փW�����v:
 * �E�B���h�E - �ݒ� - Java - �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
package org.F11.scada.server.dao;

import org.F11.scada.server.entity.MultiRecordDefine;

/**
 * @author hori
 *
 * TODO ���̐������ꂽ�^�R�����g�̃e���v���[�g��ύX����ɂ͎��փW�����v:
 * �E�B���h�E - �ݒ� - Java - �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
public interface MultiRecordDefineDao {
	public Class BEAN = MultiRecordDefine.class;
	public MultiRecordDefine getMultiRecordDefine(String loggingTableName);
    public static final String getMultiRecordDefine_QUERY = "logging_table_name = ?";
}
