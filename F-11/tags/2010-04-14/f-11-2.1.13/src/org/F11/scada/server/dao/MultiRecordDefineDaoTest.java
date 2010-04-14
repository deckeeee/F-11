/*
 * 作成日: 2005/08/19 TODO この生成されたファイルのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 * コード・スタイル - コード・テンプレート
 */
package org.F11.scada.server.dao;

import org.F11.scada.server.entity.MultiRecordDefine;
import org.seasar.dao.unit.S2DaoTestCase;

/**
 * @author hori TODO この生成された型コメントのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 *         コード・スタイル - コード・テンプレート
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
