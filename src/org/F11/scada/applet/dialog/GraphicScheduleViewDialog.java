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
package org.F11.scada.applet.dialog;

import java.awt.Dialog;
import java.awt.Frame;

import org.F11.scada.applet.schedule.GraphicScheduleViewCreator;
import org.F11.scada.applet.schedule.GraphicSevenDayScheduleView;
import org.F11.scada.applet.schedule.GraphicTwoDayScheduleView;
import org.F11.scada.applet.schedule.ScheduleModel;
import org.F11.scada.xwife.applet.PageChanger;
import org.apache.log4j.Logger;

/**
 * @author hori
 */
public class GraphicScheduleViewDialog extends AbstractScheduleDialog {
	private static final long serialVersionUID = 2203009472569722346L;
	private static final Logger logger =
		Logger.getLogger(GraphicScheduleViewDialog.class);
	private final int viewMode;

	/**
	 * コンストラクタ
	 * 
	 * @param frame 親のフレームです
	 */
	public GraphicScheduleViewDialog(
			Frame frame,
			boolean isSort,
			int viewMode,
			boolean isLenient,
			PageChanger changer) {
		super(frame, isSort, isLenient, changer);
		this.viewMode = viewMode;
		checkViewMode();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param dialog 親のダイアログです
	 */
	public GraphicScheduleViewDialog(
			Dialog dialog,
			boolean isSort,
			int viewMode,
			boolean isLenient,
			PageChanger changer) {
		super(dialog, isSort, isLenient, changer);
		this.viewMode = viewMode;
		checkViewMode();
	}

	private void checkViewMode() {
		if (2 != viewMode && 7 != viewMode) {
			logger.error("viewMode は 2 または 7を指定してください。初期値を2に設定します。");
		}
	}

	public GraphicScheduleViewCreator createView(
			ScheduleModel scheduleModel,
			boolean isSort,
			boolean isLenient,
			PageChanger changer) {
		// TODO viewModeは廃止してScheduleModel#getTopSizeを使用すべき?
		switch (viewMode) {
		case 2:
			return new GraphicTwoDayScheduleView(
				scheduleModel,
				isSort,
				isLenient,
				changer);
		case 7:
			return new GraphicSevenDayScheduleView(
				scheduleModel,
				isSort,
				isLenient,
				changer);
		default:
			return new GraphicTwoDayScheduleView(
				scheduleModel,
				isSort,
				isLenient,
				changer);
		}
	}
}
