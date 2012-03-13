/*
 * 作成日: 2008/02/15 TODO この生成されたファイルのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 * コード・スタイル - コード・テンプレート
 */
package org.F11.scada.server.formula.impl;

import java.util.List;

import org.F11.scada.server.formula.ItemFormulaService;
import org.F11.scada.server.formula.dao.ItemFormulaDao;
import org.F11.scada.server.formula.dto.ItemFormulaDto;

public class ItemFormulaServiceImpl implements ItemFormulaService {
	private final ItemFormulaDao itemFormulaDao;

	public ItemFormulaServiceImpl(ItemFormulaDao itemFormulaDao) {
		this.itemFormulaDao = itemFormulaDao;
	}

	public List<ItemFormulaDto> findAll() {
		return itemFormulaDao.findAll();
	}

}
