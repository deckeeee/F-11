/*
 * =============================================================================
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

package org.F11.scada.applet.schedule;

import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.test.util.TestUtil;

public class DefaultScheduleModelTest extends TestCase {
	private DataProvider dp;
	private ScheduleModel model;

	public DefaultScheduleModelTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		// �X�P�W���[���f�[�^�̗p��
		dp = TestUtil.createDP();
		dp.addDataHolder(TestUtil.createScheduleHolder("SC1", "Group1"));
		dp.addDataHolder(TestUtil.createScheduleHolder("SC2", "Group2"));
		dp.addDataHolder(TestUtil.createScheduleHolder("SC3", "Group3"));
		Manager.getInstance().addDataProvider(dp);
		// �X�P�W���[�����f���ւ̓o�^
		model = new DefaultScheduleModel();
		model.addGroup("P1", "SC1", "");
		model.addGroup("P1", "SC2", "");
		model.addGroup("P1", "SC3", "");
	}

	protected void tearDown() throws Exception {
		// �X�P�W���[�����f���I��
		model.disConnect();
		// JIM�t���[�����[�N�N���A
		TestUtil.crearJIM();
	}
	
	// �O���[�v�����̃e�X�g
	public void testDuplicateGroup() throws Exception {
		// �{���E1��ڂɎ�����ݒ�
		ScheduleRowModel rowModel = model.getScheduleRowModel(0);
		rowModel.setOnTime(0, 1234);
		rowModel.setOffTime(0, 2345);
		model.setValue();
		
		rowModel = model.getScheduleRowModel(0);
		assertEquals(1234, rowModel.getOnTime(0));
		assertEquals(2345, rowModel.getOffTime(0));
		assertEquals("Group1", model.getGroupName());
		
		// �O���[�v3�̖{���E1��ڎ������e�X�g
		model.setGroupNo(2);
		rowModel = model.getScheduleRowModel(0);
		assertEquals(0, rowModel.getOnTime(0));
		assertEquals(0, rowModel.getOffTime(0));
		assertEquals("Group3", model.getGroupName());

		// �O���[�v1���O���[�v3�ɕ���
		model.setGroupNo(0);
		model.duplicateGroup(new int[]{2});

		// �O���[�v2���e�X�g
		model.setGroupNo(1);
		rowModel = model.getScheduleRowModel(0);
		assertEquals(0, rowModel.getOnTime(0));
		assertEquals(0, rowModel.getOffTime(0));
		assertEquals("Group2", model.getGroupName());
		
		// �O���[�v3���O���[�v1�̕������ǂ������e�X�g
		model.setGroupNo(2);
		rowModel = model.getScheduleRowModel(0);
		assertEquals(1234, rowModel.getOnTime(0));
		assertEquals(2345, rowModel.getOffTime(0));
		assertEquals("Group3", model.getGroupName());
	}
	
	// �j���R�s�[�̃e�X�g
	public void testDuplicateWeekOfDay() throws Exception {
		// �{���E1��ڂɎ�����ݒ�
		ScheduleRowModel rowModel = model.getScheduleRowModel(0);
		rowModel.setOnTime(0, 1234);
		rowModel.setOffTime(0, 2345);
		model.setValue();

		rowModel = model.getScheduleRowModel(2);
		assertEquals(0, rowModel.getOnTime(0));
		assertEquals(0, rowModel.getOffTime(0));

		rowModel = model.getScheduleRowModel(3);
		assertEquals(0, rowModel.getOnTime(0));
		assertEquals(0, rowModel.getOffTime(0));

		rowModel = model.getScheduleRowModel(model.getPatternSize() - 1);
		assertEquals(0, rowModel.getOnTime(0));
		assertEquals(0, rowModel.getOffTime(0));

		// �{���̃X�P�W���[������j�A���j�A�����1�ɕ���
		model.duplicateWeekOfDay(0, new int[]{2, 3, (model.getPatternSize() - 1)});

		// ���j���e�X�g
		rowModel = model.getScheduleRowModel(2);
		assertEquals(1234, rowModel.getOnTime(0));
		assertEquals(2345, rowModel.getOffTime(0));
		// ���j���e�X�g
		rowModel = model.getScheduleRowModel(3);
		assertEquals(1234, rowModel.getOnTime(0));
		assertEquals(2345, rowModel.getOffTime(0));
		// �����1���e�X�g
		rowModel = model.getScheduleRowModel(model.getPatternSize() - 1);
		assertEquals(1234, rowModel.getOnTime(0));
		assertEquals(2345, rowModel.getOffTime(0));
	}
	
	public void testGetGroupNames() throws Exception {
		model.setEditable(new boolean[]{true, true, true});
		GroupElement[] groups = model.getGroupNames();
		assertEquals(2, groups.length);
		assertEquals(1, groups[0].getIndex());
		assertEquals("Group2", groups[0].getGroupName());
		assertEquals(2, groups[1].getIndex());
		assertEquals("Group3", groups[1].getGroupName());
		
		model.setGroupNo(1);
		groups = model.getGroupNames();
		assertEquals(2, groups.length);
		assertEquals(0, groups[0].getIndex());
		assertEquals("Group1", groups[0].getGroupName());
		assertEquals(2, groups[1].getIndex());
		assertEquals("Group3", groups[1].getGroupName());
	}
}
