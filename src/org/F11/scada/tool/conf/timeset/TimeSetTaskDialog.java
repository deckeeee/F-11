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

package org.F11.scada.tool.conf.timeset;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.F11.scada.tool.conf.TimeSetManager;
import org.F11.scada.tool.conf.io.TimeSetBean;
import org.F11.scada.tool.conf.io.TimeSetTaskBean;

class TimeSetTaskDialog extends JDialog {
	private static final long serialVersionUID = 9034439590614315391L;
	private JTextField nameField;
	private JComboBox schedulebox;
	private JFormattedTextField offsetField;
	private JComboBox millibox;
	private TimeSetTaskBean bean;
	private final TimeSetTaskTableModel model;
	private boolean beanInsert;

	TimeSetTaskDialog(
			Frame frame,
			TimeSetTaskTableModel model,
			int row,
			TimeSetManager stream) {
		super(frame, "タスク" + (0 > row ? "追加" : "変更"), true);
		this.model = model;
		createCenter(frame, model, row, stream);
		createSouth(model, row, frame);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(frame);
		setVisible(true);
	}

	private void createCenter(
			Frame frame,
			TimeSetTaskTableModel model,
			int row,
			TimeSetManager stream) {
		JPanel main = new JPanel(new BorderLayout());
		main.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(getTitle()));
		String taskName = getTaskName(model, row);
		setName(panel, taskName);
		setSchedule(panel, model, row);
		setOffset(panel, model, row);
		setMilliOffsetMode(panel, model, row);
		main.add(panel, BorderLayout.NORTH);
		TimeSetTab timeset = new TimeSetTab(frame, stream, row, this);
		main.add(timeset, BorderLayout.CENTER);
		add(main, BorderLayout.CENTER);
	}

	private String getTaskName(TimeSetTaskTableModel model, int row) {
		return 0 > row ? "" : model.get(row).get("name");
	}

	private void setName(JPanel panel, String taskName) {
		GridBagConstraints c = new GridBagConstraints();
		JLabel label = new JLabel("タスク名　：　");
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(label, c);
		nameField = new JTextField(taskName, 20);
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		panel.add(nameField, c);
	}

	private void setSchedule(JPanel panel, TimeSetTaskTableModel model, int row) {
		GridBagConstraints c = new GridBagConstraints();
		JLabel label = new JLabel("スケジュール　：　");
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 0;
		c.gridy = 1;
		panel.add(label, c);
		schedulebox =
			new JComboBox(new String[] {
				"分間隔",
				"10分間隔",
				"時間隔",
				"日間隔",
				"月間隔",
				"年間隔" });
		initComboBox(schedulebox, model, row, "schedule");
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		panel.add(schedulebox, c);
	}

	private void initComboBox(
			JComboBox box,
			TimeSetTaskTableModel model,
			int row,
			String property) {
		if (0 <= row) {
			String value = model.get(row).get(property);
			box.setSelectedItem(value);
		}
	}

	private void setOffset(JPanel panel, TimeSetTaskTableModel model, int row) {
		GridBagConstraints c = new GridBagConstraints();
		JLabel label = new JLabel("オフセット　：　");
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 0;
		c.gridy = 2;
		panel.add(label, c);
		DecimalFormat format = new DecimalFormat("####");
		offsetField = new JFormattedTextField(format);
		offsetField.setColumns(4);
		offsetField.setText(getOffset(model, row));
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		panel.add(offsetField, c);
	}

	private String getOffset(TimeSetTaskTableModel model, int row) {
		return 0 > row ? "0" : model.get(row).get("offset");
	}

	private void setMilliOffsetMode(
			JPanel panel,
			TimeSetTaskTableModel model,
			int row) {
		GridBagConstraints c = new GridBagConstraints();
		JLabel label = new JLabel("ミリ秒モード　：　");
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 0;
		c.gridy = 3;
		panel.add(label, c);
		millibox = new JComboBox(new String[] { "通常", "ミリ秒モード" });
		initComboBox(millibox, model, row, "milliOffsetMode");
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		panel.add(millibox, c);
	}

	private void createSouth(TimeSetTaskTableModel model, int row, Frame frame) {
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		box.add(Box.createGlue());
		box.add(getOk(model, row, frame));
		box.add(Box.createHorizontalStrut(5));
		box.add(getCancel());
		add(box, BorderLayout.SOUTH);
	}

	private JButton getOk(TimeSetTaskTableModel model, int row, Frame frame) {
		JButton okbutton = new JButton(getButtonText(row));
		okbutton.addActionListener(new ButtonAction(model, row, frame));
		return okbutton;
	}

	private String getButtonText(int row) {
		return 0 > row ? "追加" : "変更";
	}

	private JButton getCancel() {
		JButton button = new JButton("キャンセル");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		return button;
	}

	TimeSetTaskBean getBean() {
		return bean;
	}

	public void insert(String name) {
		TimeSetTaskBean bean = new TimeSetTaskBean();
		setProperties(name, bean);
		bean.setTimeList(new ArrayList<TimeSetBean>());
		model.insert(bean);
		beanInsert = true;
	}

	private void setProperties(String name, TimeSetTaskBean bean) {
		bean.put("name", name);
		bean.put("schedule", getSchedule());
		bean.put("offset", getOffset());
		bean.put("milliOffsetMode", getMilli());
	}

	private String getSchedule() {
		return (String) schedulebox.getSelectedItem();
	}

	private String getMilli() {
		return (String) millibox.getSelectedItem();
	}

	private String getOffset() {
		String offset = offsetField.getText();
		try {
			Integer.parseInt(offset);
			return offset;
		} catch (Exception e) {
			return "0";
		}
	}

	String getTaskName() {
		return nameField.getText();
	}

	void nameFocus() {
		nameField.requestFocus();
	}

	private class ButtonAction implements ActionListener {
		private final TimeSetTaskTableModel model;
		private final int row;
		private final Frame frame;

		public ButtonAction(TimeSetTaskTableModel model, int row, Frame frame) {
			this.model = model;
			this.row = row;
			this.frame = frame;
		}

		public void actionPerformed(ActionEvent e) {
			bean = createBean();
			if (null != bean || beanInsert) {
				dispose();
			} else {
				JOptionPane.showMessageDialog(frame, "タスク情報を入力してください");
				nameFocus();
			}
		}

		private TimeSetTaskBean createBean() {
			String name = getTaskName();
			if (0 > row) {
				if (!beanInsert) {
					if (null != name && !"".equals(name)) {
						TimeSetTaskBean bean = new TimeSetTaskBean();
						setProperties(name, bean);
						bean.setTimeList(new ArrayList<TimeSetBean>());
						return bean;
					}
				}
				return null;
			} else {
				TimeSetTaskBean bean = model.get(row);
				setProperties(name, bean);
				return bean;
			}
		}
	}
}
