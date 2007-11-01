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
package org.F11.scada.applet.schedule;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.dialog.schedule.DefaultScheduleDialog;
import org.F11.scada.applet.symbol.ScrollableBaseBox;
import org.F11.scada.data.DefaultSchedulePattern;
import org.apache.log4j.Logger;

/**
 * �O���t�B�b�N��ʎ��̃X�P�W���[���r���[�N���X�ł��B�i�Q���p�j
 * 
 * @todo bar �� button �̔z��2����̂́A�v���t�@�N�^�����O���ځB���ł�TAB�L�[�ړ����g�ݍ��߂�悤�ɁAEditable���g�ݍ��ށB
 */
public class GraphicTwoDayScheduleView implements ActionListener,
		PropertyChangeListener, GraphicScheduleViewCreator {
	/** �X�P�W���[�����f���̎Q�Ƃł� */
	private ScheduleModel scheduleModel;
	/** �O���t�B�b�N�o�[�̃��X�g�ł� */
	private JComponent[] bars;
	/** �����ύX�{�^���̃��X�g�ł� */
	private MatrixBarButton[] buttons;
	/** �����ύX�p�_�C�A���O�̎Q�Ƃł� */
	private JDialog dialog;
	/** �X�P�W���[�������f�[�^�̃\�[�g�L�� */
	private final boolean isSort;
	/** ���͎����召�`�F�b�N�̗L�� */
	private final boolean isLenient;
	/** ���M���O�I�u�W�F�N�g */
	private static Logger logger;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param alarmRef �����[�g�f�[�^�I�u�W�F�N�g
	 */
	public GraphicTwoDayScheduleView(
			ScheduleModel scheduleModel,
			boolean isSort,
			boolean isLenient) {
		this.scheduleModel = scheduleModel;
		this.isSort = isSort;
		this.isLenient = isLenient;
		logger = Logger.getLogger(getClass().getName());
		init();
	}

	/**
	 * �����������ł��B
	 */
	private void init() {
		bars = new JComponent[scheduleModel.getPatternSize()];
		buttons = new MatrixBarButton[scheduleModel.getPatternSize()];
		scheduleModel.addPropertyChangeListener(this);
	}

	/**
	 * �O���t�B�b�N��ʎ��̃X�P�W���[���r���[���X�N���[���y�C���ɂ̂��ĕԂ��܂��B
	 * 
	 * @return �X�N���[���y�C��
	 */
	public JComponent createView() {
		return new JScrollPane(createPanel());
	}

	private JComponent createPanel() {
		createRowComponent();
		ScrollableBaseBox mainBox = new ScrollableBaseBox(BoxLayout.Y_AXIS);
		mainBox.add(createRowPanel(0, 1));
		return mainBox;
	}

	private void createRowComponent() {
		ScheduleRowModel scheduleRowModel = getScheduleRowModel(DefaultSchedulePattern.TODAY);
		bars[scheduleModel.getDayIndex(DefaultSchedulePattern.TODAY)] = new BarMatrix(
				scheduleRowModel);
		buttons[scheduleModel.getDayIndex(DefaultSchedulePattern.TODAY)] = new MatrixBarButton(
				scheduleModel.getDayIndexName(DefaultSchedulePattern.TODAY),
				scheduleRowModel);

		scheduleRowModel = getScheduleRowModel(DefaultSchedulePattern.TOMORROW);
		bars[scheduleModel.getDayIndex(DefaultSchedulePattern.TOMORROW)] = new BarMatrix(
				scheduleRowModel);
		buttons[scheduleModel.getDayIndex(DefaultSchedulePattern.TOMORROW)] = new MatrixBarButton(
				scheduleModel.getDayIndexName(DefaultSchedulePattern.TOMORROW),
				scheduleRowModel);

		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i] == null) {
				logger.warn("button is null : index = " + i);
			} else {
				JButton b = (JButton) buttons[i];
				b.addActionListener(this);
				b.setPreferredSize(new Dimension(80, BarMatrix.BAR_HEIGHT));
			}
		}
	}

	private JComponent createRowPanel(int start, int end) {
		JPanel panel = new JPanel(new GridBagLayout());

		Border bevel = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		Border empty = BorderFactory.createEmptyBorder(5, 10, 15, 10);
		Border compound = BorderFactory.createCompoundBorder(bevel, empty);
		panel.setBorder(compound);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 1;
		c.gridheight = 1;

		GridBagConstraints bc = (GridBagConstraints) c.clone();
		bc.insets = new Insets(
				BarMatrix.SCALE_HEIGHT - 3,
				0,
				BarMatrix.MARGIN_BOTTOM,
				0);

		for (int i = start; i <= end; i++) {
			bc.gridx = 0;
			bc.gridy = i;
			panel.add(buttons[i], bc);

			c.gridx = 1;
			c.gridy = i;
			panel.add(bars[i], c);
		}

		return panel;
	}

	/**
	 * �����̗j���� ScheduleRowModel ��Ԃ��܂��B
	 * 
	 * @param selectRow �j���̍��ڃp�^�[���C���f�b�N�X
	 * @return �����̍��ڂŎw�肳�ꂽ ScheduleRowModel �I�u�W�F�N�g�B
	 */
	public ScheduleRowModel getScheduleRowModel(int selectRow) {
		return scheduleModel.getScheduleRowModel(selectRow);
	}

	/**
	 * �����ύX�{�^���̃A�N�V�����C�x���g���������܂��B
	 */
	public void actionPerformed(ActionEvent evt) {
		if (scheduleModel.isEditable()) {
			MatrixBarButton matrixBarButton = (MatrixBarButton) evt.getSource();
			Frame frame = WifeUtilities.getParentFrame(matrixBarButton);
			Dimension screenSize = frame.getToolkit().getScreenSize();

			if (dialog != null) {
				dialog.dispose();
			}

			dialog = new DefaultScheduleDialog(frame, matrixBarButton
					.getScheduleRowModel(), isSort, isLenient);
			dialog.pack();

			Rectangle dialogBounds = dialog.getBounds();

			dialogBounds.setLocation(matrixBarButton.getLocationOnScreen());
			dialogBounds.x += matrixBarButton.getWidth();
			dialogBounds.y += matrixBarButton.getHeight();
			dialog.setLocation(WifeUtilities.getInScreenPoint(
					screenSize,
					dialogBounds));
			dialog.show();
		}
	}

	/** �X�P�W���[�����f���̃o�E���Y�v���p�e�B���������܂� */
	public void propertyChange(PropertyChangeEvent evt) {
		for (int i = 0; i < bars.length; i++) {
			bars[i].repaint();
		}
	}
}
