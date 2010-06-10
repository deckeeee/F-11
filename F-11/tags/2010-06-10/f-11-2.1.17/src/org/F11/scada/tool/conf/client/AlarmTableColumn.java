/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.tool.conf.client;

import static org.F11.scada.util.ComponentUtil.addLabel;
import static org.F11.scada.util.ComponentUtil.addTextArea;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.F11.scada.tool.conf.StreamManager;
import org.F11.scada.xwife.applet.alarm.AlarmColumn;

/**
 * �x��ꗗ�̗񕝐ݒ�_�C�A���O
 *
 * @author maekawa
 *
 */
public class AlarmTableColumn extends JDialog {
	private static final long serialVersionUID = -3024378393789154566L;
	private final StreamManager manager;
	private final JTextField rowHeaderWidth = new JTextField();
	private final JTextField rowHeaderFormat = new JTextField(6);
	private final JTextField unit = new JTextField();
	private final JTextField attribute = new JTextField();
	private final JTextField status = new JTextField();
	private final JTextField sort = new JTextField();
	private final JTextField date = new JTextField();
	private final JTextField attributen = new JTextField();

	public AlarmTableColumn(Frame frameParent, StreamManager manager) {
		super(frameParent, "�x��ꗗ", true);
		this.manager = manager;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel panel = getCenter();
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		mainPanel.add(panel, BorderLayout.CENTER);
		mainPanel.add(getSouth(), BorderLayout.SOUTH);
		add(mainPanel);
		pack();
		setLocationRelativeTo(frameParent);
	}

	private JPanel getCenter() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder("�� (�߸��)"));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		setRowHeaderWidth(c, panel);
		setRowHeaderFormat(c, panel);
		setUnit(c, panel);
		setAttribute(c, panel);
		setStatus(c, panel);
		setSort(c, panel);
		setDate(c, panel);
		setAttributeN(c, panel);
		return panel;
	}

	private void setRowHeaderWidth(GridBagConstraints c, JPanel panel) {
		JLabel label = new JLabel("�s�w�b�_�[���F");
		addLabel(c, panel, label);
		addTextArea(c, panel, rowHeaderWidth, manager.getClientConf(
			"org.F11.scada.xwife.applet.alarm.rowheader.width",
			"" + AlarmColumn.ROW_HEADER_SIZE));
	}

	private void setRowHeaderFormat(GridBagConstraints c, JPanel panel) {
		JLabel label = new JLabel("�s�w�b�_�[�t�H�[�}�b�g�F");
		addLabel(c, panel, label);
		addTextArea(c, panel, rowHeaderFormat, manager.getClientConf(
			"org.F11.scada.xwife.applet.alarm.rowheader.format",
			"" + AlarmColumn.ROW_HEADER_FORMAT));
	}

	private void setUnit(GridBagConstraints c, JPanel panel) {
		JLabel label = new JLabel("�@��L���F");
		addLabel(c, panel, label);
		addTextArea(c, panel, unit, manager.getClientConf(
			"org.F11.scada.xwife.applet.alarm.unitSize",
			"" + AlarmColumn.UNIT_SIZE));
	}

	private void setAttribute(GridBagConstraints c, JPanel panel) {
		JLabel label = new JLabel("�����F");
		addLabel(c, panel, label);
		addTextArea(c, panel, attribute, manager.getClientConf(
			"org.F11.scada.xwife.applet.alarm.attributeSize",
			"" + AlarmColumn.DEFAULT_SIZE));
	}

	private void setStatus(GridBagConstraints c, JPanel panel) {
		JLabel label = new JLabel("��ԁF");
		addLabel(c, panel, label);
		addTextArea(c, panel, status, manager.getClientConf(
			"org.F11.scada.xwife.applet.alarm.statusSize",
			"" + AlarmColumn.DEFAULT_SIZE));
	}

	private void setSort(GridBagConstraints c, JPanel panel) {
		JLabel label = new JLabel("��ʁF");
		addLabel(c, panel, label);
		addTextArea(c, panel, sort, manager.getClientConf(
			"org.F11.scada.xwife.applet.alarm.sortSize",
			"" + AlarmColumn.DEFAULT_SIZE));
	}

	private void setDate(GridBagConstraints c, JPanel panel) {
		JLabel label = new JLabel("�����F");
		addLabel(c, panel, label);
		addTextArea(c, panel, date, manager.getClientConf(
			"org.F11.scada.xwife.applet.alarm.dateSize",
			"" + AlarmColumn.DATE_SIZE));
	}

	private void setAttributeN(GridBagConstraints c, JPanel panel) {
		JLabel label = new JLabel("����1,2,3�F");
		addLabel(c, panel, label);
		addTextArea(c, panel, attributen, manager.getClientConf(
			"org.F11.scada.xwife.applet.alarm.attributeNSize",
			"" + AlarmColumn.DEFAULT_SIZE));
	}

	private Component getSouth() {
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		box.add(Box.createHorizontalGlue());
		box.add(getOk());
		box.add(Box.createHorizontalStrut(5));
		box.add(getCancel());
		return box;
	}

	private JButton getOk() {
		JButton button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				manager.setClientConf(
					"org.F11.scada.xwife.applet.alarm.rowheader.width",
					rowHeaderWidth.getText());
				manager.setClientConf(
					"org.F11.scada.xwife.applet.alarm.rowheader.format",
					rowHeaderFormat.getText());
				manager.setClientConf(
					"org.F11.scada.xwife.applet.alarm.unitSize",
					unit.getText());
				manager.setClientConf(
					"org.F11.scada.xwife.applet.alarm.attributeSize",
					attribute.getText());
				manager.setClientConf(
					"org.F11.scada.xwife.applet.alarm.statusSize",
					status.getText());
				manager.setClientConf(
					"org.F11.scada.xwife.applet.alarm.sortSize",
					sort.getText());
				manager.setClientConf(
					"org.F11.scada.xwife.applet.alarm.dateSize",
					date.getText());
				manager.setClientConf(
					"org.F11.scada.xwife.applet.alarm.attributeNSize",
					attributen.getText());
				dispose();
			}
		});
		return button;
	}

	private JButton getCancel() {
		JButton button = new JButton("CANCEL");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		return button;
	}
}
