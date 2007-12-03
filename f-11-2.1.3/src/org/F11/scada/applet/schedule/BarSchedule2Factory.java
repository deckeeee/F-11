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
 * �O���t�B�b�N���̃X�P�W���[���N���X�𐶐������ۃt�@�N�g���[�N���X�ł��B ScheduleFactory
 * �N���X����A�C���X�^���X�����̍ۃ��t�@�����X�@�\��ʂ��āA�Ăяo����܂��B
 */
public class BarSchedule2Factory extends ScheduleFactory {
	private GraphicScheduleView view;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param alarmRef �����[�g�f�[�^�I�u�W�F�N�g
	 */
	public BarSchedule2Factory(
			ScheduleModel scheduleModel,
			boolean isSort,
			boolean isNonTandT,
			String pageId,
			boolean isLenient) {
		super(scheduleModel);
		// �o�[�^�C�v�͓��͎����`�F�b�N�L��
		init(isSort, isNonTandT, pageId, false);
	}

	/**
	 * ���������ł��B
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
