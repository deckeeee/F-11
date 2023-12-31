package org.F11.scada.applet.schedule;

/*
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

import javax.swing.JComponent;

import org.F11.scada.xwife.applet.PageChanger;

/**
 * スケジュール画面を作成するクラス。実装はファクトリークラスに委譲します。
 */
public class WifeSchedule {
	/** スケジュールファクトリークラス */
	private ScheduleFactory factory;

	public WifeSchedule(
			ScheduleModel scheduleModel,
			String viewClass,
			boolean isSort,
			boolean isNonTandT,
			String pageId,
			boolean isLenient,
			PageChanger changer) {
		factory = ScheduleFactory.getFactory(
				scheduleModel,
				viewClass,
				isSort,
				isNonTandT,
				pageId,
				isLenient,
				changer);
	}

	public JComponent getMainPanel() {
		return factory.createView();
	}

	public JComponent getToolBar() {
		return factory.createToolBar();
	}
}
