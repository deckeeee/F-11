/*
 * �쐬��: 2008/02/15 TODO ���̐������ꂽ�t�@�C���̃e���v���[�g��ύX����ɂ͎��փW�����v: �E�B���h�E - �ݒ� - Java -
 * �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
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
