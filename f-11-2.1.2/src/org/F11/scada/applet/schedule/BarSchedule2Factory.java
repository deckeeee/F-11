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

import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JDialog;

import org.F11.scada.applet.dialog.schedule.BarScheduleDialog;
import org.F11.scada.applet.dialog.schedule.ScheduleDialogFactory;

/**
 * グラフィック式のスケジュールクラスを生成する具象ファクトリークラスです。 ScheduleFactory
 * クラスから、インスタンス生成の際リファレンス機構を通じて、呼び出されます。
 */
public class BarSchedule2Factory extends ScheduleFactory {
	private GraphicScheduleView view;

	/**
	 * コンストラクタ
	 * 
	 * @param alarmRef リモートデータオブジェクト
	 */
	public BarSchedule2Factory(
			ScheduleModel scheduleModel,
			boolean isSort,
			boolean isNonTandT,
			String pageId,
			boolean isLenient) {
		super(scheduleModel);
		// バータイプは入力時刻チェック有り
		init(isSort, isNonTandT, pageId, false);
	}

	/**
	 * 初期処理です。
	 * 
	 * @param isSort
	 * @param isNonTandT
	 * @param pageId
	 */
	private void init(boolean isSort, boolean isNonTandT, String pageId, boolean isLenient) {
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
			return new BarMatrix3(model);
		}
	}

	private class ScheduleDialogFactoryImpl implements ScheduleDialogFactory {
		private final boolean isSort;
		private final boolean isLenient;

		public ScheduleDialogFactoryImpl(boolean isSort, boolean isLenient) {
			this.isSort = isSort;
			this.isLenient = isLenient;
		}

		public JDialog getScheduleDialog(Frame frame, ScheduleRowModel rowModel) {
			return new BarScheduleDialog(frame, rowModel, isSort, isLenient);
		}
	}
}
