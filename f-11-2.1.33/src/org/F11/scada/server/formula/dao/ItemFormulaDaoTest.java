/*
 * �쐬��: 2008/02/15 TODO ���̐������ꂽ�t�@�C���̃e���v���[�g��ύX����ɂ͎��փW�����v: �E�B���h�E - �ݒ� - Java -
 * �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
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
