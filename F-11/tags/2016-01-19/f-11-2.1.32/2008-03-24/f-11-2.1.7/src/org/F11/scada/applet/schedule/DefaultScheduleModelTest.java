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
		// スケジュールデータの用意
		dp = TestUtil.createDP();
		dp.addDataHolder(TestUtil.createScheduleHolder("SC1", "Group1"));
		dp.addDataHolder(TestUtil.createScheduleHolder("SC2", "Group2"));
		dp.addDataHolder(TestUtil.createScheduleHolder("SC3", "Group3"));
		Manager.getInstance().addDataProvider(dp);
		// スケジュールモデルへの登録
		model = new DefaultScheduleModel();
		model.addGroup("P1", "SC1", "");
		model.addGroup("P1", "SC2", "");
		model.addGroup("P1", "SC3", "");
	}

	protected void tearDown() throws Exception {
		// スケジュールモデル終了
		model.disConnect();
		// JIMフレームワーククリア
		TestUtil.crearJIM();
	}
	
	// グループ複製のテスト
	public void testDuplicateGroup() throws Exception {
		// 本日・1回目に時刻を設定
		ScheduleRowModel rowModel = model.getScheduleRowModel(0);
		rowModel.setOnTime(0, 1234);
		rowModel.setOffTime(0, 2345);
		model.setValue();
		
		rowModel = model.getScheduleRowModel(0);
		assertEquals(1234, rowModel.getOnTime(0));
		assertEquals(2345, rowModel.getOffTime(0));
		assertEquals("Group1", model.getGroupName());
		
		// グループ3の本日・1回目時刻をテスト
		model.setGroupNo(2);
		rowModel = model.getScheduleRowModel(0);
		assertEquals(0, rowModel.getOnTime(0));
		assertEquals(0, rowModel.getOffTime(0));
		assertEquals("Group3", model.getGroupName());

		// グループ1をグループ3に複製
		model.setGroupNo(0);
		model.duplicateGroup(new int[]{2});

		// グループ2をテスト
		model.setGroupNo(1);
		rowModel = model.getScheduleRowModel(0);
		assertEquals(0, rowModel.getOnTime(0));
		assertEquals(0, rowModel.getOffTime(0));
		assertEquals("Group2", model.getGroupName());
		
		// グループ3がグループ1の複製かどうかをテスト
		model.setGroupNo(2);
		rowModel = model.getScheduleRowModel(0);
		assertEquals(1234, rowModel.getOnTime(0));
		assertEquals(2345, rowModel.getOffTime(0));
		assertEquals("Group3", model.getGroupName());
	}
	
	// 曜日コピーのテスト
	public void testDuplicateWeekOfDay() throws Exception {
		// 本日・1回目に時刻を設定
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

		// 本日のスケジュールを日曜、月曜、特殊日1に複製
		model.duplicateWeekOfDay(0, new int[]{2, 3, (model.getPatternSize() - 1)});

		// 日曜をテスト
		rowModel = model.getScheduleRowModel(2);
		assertEquals(1234, rowModel.getOnTime(0));
		assertEquals(2345, rowModel.getOffTime(0));
		// 月曜をテスト
		rowModel = model.getScheduleRowModel(3);
		assertEquals(1234, rowModel.getOnTime(0));
		assertEquals(2345, rowModel.getOffTime(0));
		// 特殊日1をテスト
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
