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

import org.F11.scada.applet.dialog.schedule.ScheduleDialogFactoryImpl;

/**
 * グラフィック式のスケジュールクラスを生成する具象ファクトリークラスです。 ScheduleFactory
 * クラスから、インスタンス生成の際リファレンス機構を通じて、呼び出されます。
 */
public class GraphicScheduleFactory extends ScheduleFactory {
	private GraphicScheduleView view;

	/**
	 * コンストラクタ
	 * 
	 * @param alarmRef リモートデータオブジェクト
	 */
	public GraphicScheduleFactory(
			ScheduleModel scheduleModel,
			boolean isSort,
			boolean isNonTandT,
			String pageId,
			boolean isLenient) {
		super(scheduleModel);
		init(isSort, isNonTandT, pageId, isLenient);
	}

	/**
	 * 初期処理です。
	 * 
	 * @param isSort
	 * @param isNonTandT
	 */
	private void init(
			boolean isSort,
			boolean isNonTandT,
			String pageId,
			boolean isLenient) {
		view = new GraphicScheduleView(
				scheduleModel,
				new BarMatrixFactoryImpl(),
				new ScheduleDialogFactoryImpl(isSort, isLenient),
				isNonTandT,
				pageId);
	}

	public JComponent createView() {
		return view.createView();
	}

	public JComponent createToolBar() {
		return view.createToolBar();
	}

	private class BarMatrixFactoryImpl implements BarMatrixFactory {
		public JComponent getBarMatrix(ScheduleRowModel model) {
			return new BarMatrix(model);
		}
	}
}
