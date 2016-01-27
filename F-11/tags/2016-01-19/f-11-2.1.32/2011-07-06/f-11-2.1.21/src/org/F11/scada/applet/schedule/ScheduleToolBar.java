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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.dialog.ScheduleGroupListDialog;
import org.F11.scada.applet.schedule.point.SchedulePointFinder;
import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.util.ComponentUtil;
import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.apache.log4j.Logger;

/**
 * �X�P�W���[���o�^�E����̃{�^����ێ�����c�[���o�[�ł��B
 */
public class ScheduleToolBar extends JToolBar implements ActionListener,
		PropertyChangeListener {
	private static final long serialVersionUID = 371500372873608373L;
	/** �c�[���o�[�̃f�[�^���f���ł� */
	private ScheduleModel scheduleModel;
	/** ���O���[�v�{�^�� */
	private JButton nextGroupButton;
	/** �O�O���[�v�{�^�� */
	private JButton previousGroupButton;
	/** �ꗗ�\���{�^�� */
	private JButton listButton;
	/** �O���[�vNo ���x�� */
	private JLabel groupNoLabel;
	/** �����E�����\���̗L�� */
	private final boolean isNonTandT;
	/** �y�[�WID */
	private final String pageId;

	private static Logger logger;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param pageId
	 * 
	 * @aparam scheduleModel �c�[���o�[�̃f�[�^���f��
	 */
	public ScheduleToolBar(
			ScheduleModel scheduleModel,
			boolean isNonTandT,
			String pageId) {
		this.scheduleModel = scheduleModel;
		this.isNonTandT = isNonTandT;
		this.pageId = pageId;
		logger = Logger.getLogger(getClass().getName());
		init();
	}

	/**
	 * ���������ł��B
	 */
	private void init() {
		nextGroupButton =
			new JButton(GraphicManager
				.get("/toolbarButtonGraphics/navigation/Forward24.gif"));
		previousGroupButton =
			new JButton(GraphicManager
				.get("/toolbarButtonGraphics/navigation/Back24.gif"));
		listButton = new JButton(GraphicManager.get("/images/list.png"));
		listButton.setPreferredSize(new Dimension(36, 36));
		listButton.setMinimumSize(listButton.getPreferredSize());
		listButton.setMaximumSize(listButton.getPreferredSize());
		groupNoLabel = new JLabel(getDisplayGroupNo());
		WifeUtilities.setFontSize(groupNoLabel, 1.4);

		listButton.addActionListener(this);
		nextGroupButton.addActionListener(this);
		previousGroupButton.addActionListener(this);

		listButton.setToolTipText("�O���[�v�ꗗ�_�C�A���O��\�����܂��B");
		nextGroupButton.setToolTipText("���̃O���[�v��\�����܂�");
		previousGroupButton.setToolTipText("�O�̃O���[�v��\�����܂�");

		JButton groupDuplicate =
			new JButton(GraphicManager.get("/images/gdup.png"));
		groupDuplicate.setToolTipText("�O���[�v����");
		groupDuplicate.setPreferredSize(new Dimension(36, 36));
		groupDuplicate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (scheduleModel.isEditable()) {
					GroupDuplicateDialog dialog =
						new GroupDuplicateDialog((Frame) SwingUtilities
							.getAncestorOfClass(
								Frame.class,
								ScheduleToolBar.this), scheduleModel);
					dialog.show();
				}
			}
		});

		JButton weekOfDayDuplicate =
			new JButton(GraphicManager.get("/images/wdup.png"));
		weekOfDayDuplicate.setToolTipText("�j���ԕ���");
		weekOfDayDuplicate.setPreferredSize(new Dimension(36, 36));
		weekOfDayDuplicate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (scheduleModel.isEditable()) {
					WeekOfDayDuplicateDialog dialog =
						new WeekOfDayDuplicateDialog(
							(Frame) SwingUtilities.getAncestorOfClass(
								Frame.class,
								ScheduleToolBar.this),
							scheduleModel,
							isNonTandT);
					dialog.show();
				}
			}
		});

		JButton kikiTable = new JButton(GraphicManager.get("/images/kiki.png"));
		kikiTable.setToolTipText("�X�P�W���[���@�푀��");
		kikiTable.setPreferredSize(new Dimension(36, 36));
		kikiTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (scheduleModel.isEditable()) {
					AbstractWifeApplet wifeApplet = ComponentUtil.getAncestorOfClass(
						AbstractWifeApplet.class,
						ScheduleToolBar.this);
					new SchedulePointFinder(
						(Frame) SwingUtilities.getAncestorOfClass(
							Frame.class,
							ScheduleToolBar.this),
						pageId,
						wifeApplet);
				}
			}
		});

		add(previousGroupButton);
		add(nextGroupButton);
		add(listButton);
		addSeparator();
		add(groupDuplicate);
		add(weekOfDayDuplicate);
		if (WifeUtilities.isSchedulePoint()) {
			add(kikiTable);
		}
		addSeparator();
		add(groupNoLabel);

		setFloatable(false);

		scheduleModel.addPropertyChangeListener(this);
	}

	/**
	 * �{�^���̃A�N�V�����C�x���g���������܂��B
	 */
	public void actionPerformed(ActionEvent evt) {
		JComponent comp = (JComponent) evt.getSource();
		Frame frame = WifeUtilities.getParentFrame(comp);
		if (comp == listButton) {
			JDialog dialog = new ScheduleGroupListDialog(frame, scheduleModel);
			dialog.show();
		} else if (comp == nextGroupButton) {
			if (scheduleModel.getGroupNo() < scheduleModel.getGroupNoMax() - 1) {
				scheduleModel.setGroupNo(scheduleModel.getGroupNo() + 1);
			}
		} else if (comp == previousGroupButton) {
			if (scheduleModel.getGroupNo() > 0) {
				scheduleModel.setGroupNo(scheduleModel.getGroupNo() - 1);
			}
		} else {
			logger.warn("�C�x���g���ςł��B");
		}
	}

	/**
	 * �X�P�W���[���f�[�^���f���́A�o�E���Y�v���p�e�B�ύX�C�x���g���������܂��B �O���[�vNo �̕ύX�������B
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		logger.debug("�X�P�W���[���f�[�^���f���ύX");
		groupNoLabel.setText(getDisplayGroupNo());
		groupNoLabel.revalidate();
	}

	/**
	 * ��ʂɕ\������O���[�vNO��Ԃ��܂��B
	 * 
	 * @return
	 */
	private String getDisplayGroupNo() {
		return (scheduleModel.getGroupNo() + 1)
			+ " : "
			+ scheduleModel.getGroupName();
	}
}
