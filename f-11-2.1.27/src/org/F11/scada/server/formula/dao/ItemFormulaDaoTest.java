/*
 * 作成日: 2008/02/15 TODO この生成されたファイルのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 * コード・スタイル - コード・テンプレート
 */
package org.F11.scada.server.formula.dao;

import java.util.List;

import org.F11.scada.server.formula.dto.ItemFormulaDto;
import org.seasar.dao.unit.S2DaoTestCase;

public class ItemFormulaDaoTest extends S2DaoTestCase {
	private ItemFormulaDao dao;

	protected void setUp() throws Exception {
		include("ItemFormulaDao.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testFindAll() {
		List<ItemFormulaDto> list = dao.findAll();
		System.out.println(list);
	}

}
