/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/table/postgresql/PostgreSQLInitialTableFactoryTest.java,v 1.3.2.3 2007/02/26 00:44:14 frdm Exp $
 * $Revision: 1.3.2.3 $
 * $Date: 2007/02/26 00:44:14 $
 * 
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
package org.F11.scada.server.alarm.table.postgresql;

import junit.framework.TestCase;

import org.F11.scada.WifeUtilities;
import org.F11.scada.server.alarm.table.AlarmTableModel;
import org.F11.scada.server.alarm.table.InitialTableFactory;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PostgreSQLInitialTableFactoryTest extends TestCase {
	InitialTableFactory factory;

	/**
	 * Constructor for PostgreSQLInitialCreatorTest.
	 * @param arg0
	 */
	public PostgreSQLInitialTableFactoryTest(String arg0) {
		super(arg0);
		try {
			Class.forName(WifeUtilities.getJdbcDriver());
		} catch (ClassNotFoundException e) {
		}
	}
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		factory =
			InitialTableFactory.createInitialTableFactory(PostgreSQLInitialTableFactory.class);
	}


	public void testCreateCareerInitial() throws Exception {
		AlarmTableModel model = factory.createCareer();
		assertEquals(17, model.getColumnCount());
		String[] title = {
			"ジャンプパス", "自動ジャンプ", "優先順位", "表示色"
			,"point", "provider", "holder", "サウンドタイプ", "サウンドパス", "Emailグループ", "Emailモード"
			,"onoff", "日時",	"記号", "名称", "警報・状態"
		};
		columnName(title, model);
	}

	public void testCreateHistoryInitial() throws Exception {
		AlarmTableModel model = factory.createHistory();
		assertEquals(13, model.getColumnCount());

		String[] title = {
			"ジャンプパス", "自動ジャンプ", "優先順位", "表示色", "point", "provider", "holder",
			"発生・運転", "復旧・停止", "記号", "名称", "種別", "確認"
		};
		columnName(title, model);
	}

	public void testCreateSummaryInitial() throws Exception {
		AlarmTableModel model = factory.createSummary();
		assertEquals(13, model.getColumnCount());

		String[] title = {
			"ジャンプパス", "自動ジャンプ", "優先順位", "表示色", "point", "provider", "holder",
			"発生・運転", "復旧・停止", "記号", "名称", "警報・状態"
		};
		columnName(title, model);
	}

	private void columnName(String[] title, AlarmTableModel model) {
		for (int i = 0; i < title.length; i++) {
//			System.out.println(title[i] + " " + model.getColumnName(i));
			assertEquals(title[i], model.getColumnName(i));
		}
	}
}
