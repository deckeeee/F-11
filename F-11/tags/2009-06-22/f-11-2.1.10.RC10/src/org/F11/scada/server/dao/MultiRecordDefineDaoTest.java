/*
 * �쐬��: 2005/08/19 TODO ���̐������ꂽ�t�@�C���̃e���v���[�g��ύX����ɂ͎��փW�����v: �E�B���h�E - �ݒ� - Java -
 * �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
package org.F11.scada.server.dao;

import org.F11.scada.server.entity.MultiRecordDefine;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 * @author hori TODO ���̐������ꂽ�^�R�����g�̃e���v���[�g��ύX����ɂ͎��փW�����v: �E�B���h�E - �ݒ� - Java -
 *         �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
public class MultiRecordDefineDaoTest extends S2DaoTestCase {

	private MultiRecordDefineDao dao_;

	/**
	 * @param arg0
	 */
	public MultiRecordDefineDaoTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		include("MultiRecordDefineDao.dicon");
	}

	public void test001() throws Exception {
		MultiRecordDefine def = dao_.getMultiRecordDefine("log_table_sec");
		assertEquals("log_table_sec", def.getLoggingTableName());
		assertEquals("P1", def.getProvider());
		assertEquals(0, def.getComMemoryKinds());
		assertEquals(10000, def.getComMemoryAddress());
		assertEquals(880, def.getWordLength());
		assertEquals(20, def.getRecordCount());

		def = dao_.getMultiRecordDefine("hogehoge");
		assertNull(def);
	}

}
