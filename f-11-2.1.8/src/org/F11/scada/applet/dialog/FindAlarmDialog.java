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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.server.alarm.table.AttributeRecord;
import org.F11.scada.server.alarm.table.FindAlarmCondition;
import org.F11.scada.server.alarm.table.FindAlarmCondition.RadioStat;
import org.F11.scada.xwife.applet.SortColumnUtil;

/**
 * 警報一覧 検索条件設定ダイアログです。
 * 
 * @author hori <hoti@users.sourceforge.jp>
 */
public class FindAlarmDialog extends JDialog {
	private static final long serialVersionUID = -5361146091289476425L;
	private final AttributeRecord[] attris;
	private DateTimePanel st_panel;
	private DateTimePanel ed_panel;
	private JList kindList;
	private RadioStat bitvalSelect;
	private RadioStat histckSelect;

	private FindAlarmCondition ret_cond;
	private boolean ret_ok = false;
	private JTextField unitField;
	private JTextField nameField;
	private JList prioritySelectList;
	private final List priorityList;
	private final boolean isShowSortColumn;

	/**
	 * 
	 */
	private FindAlarmDialog(
			Frame frame,
			FindAlarmCondition cond,
			AttributeRecord[] attris,
			List priorityList) {
		super(frame, "状態・警報検索", true);
		this.attris = attris;
		this.priorityList = priorityList;
		createPriorityList(cond);
		isShowSortColumn = getShowSortColumn();
		init(cond);
	}

	private boolean getShowSortColumn() {
		ClientConfiguration configuration = new ClientConfiguration();
		return SortColumnUtil.getShowSortColumn(configuration);
	}

	public static FindAlarmCondition showFindAlarmDialog(
			Frame frame,
			FindAlarmCondition cond,
			AttributeRecord[] attris,
			List priorityList) {
		FindAlarmDialog dlg = new FindAlarmDialog(
				frame,
				cond,
				attris,
				priorityList);
		dlg.show();
		return dlg.ret_ok ? dlg.ret_cond : cond;
	}

	private void init(FindAlarmCondition cond) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JComponent mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(getDateComponents(cond), BorderLayout.NORTH);
		mainPanel.add(getCenter(cond), BorderLayout.CENTER);
		mainPanel.add(getSouthComponents(cond), BorderLayout.SOUTH);
		setContentPane(mainPanel);
		pack();
		WifeUtilities.setCenter(this);
	}

	private JComponent getDateComponents(FindAlarmCondition cond) {
		// 開始日時
		Box mainPanel = Box.createVerticalBox();
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("開始日時："));
		JCheckBox chack = new JCheckBox();
		chack.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				st_panel.setEnabled(((JCheckBox) evt.getSource()).isSelected());
			}
		});
		panel.add(chack);
		st_panel = new DateTimePanel(cond.getSt_calendar());
		panel.add(st_panel);
		mainPanel.add(panel);
		// 状態反映
		chack.setSelected(cond.isSt_enable());
		st_panel.setEnabled(cond.isSt_enable());
		// 終了日時
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("終了日時："));
		chack = new JCheckBox();
		chack.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				ed_panel.setEnabled(((JCheckBox) evt.getSource()).isSelected());
			}
		});
		panel.add(chack);
		ed_panel = new DateTimePanel(cond.getEd_calendar());
		panel.add(ed_panel);
		mainPanel.add(panel);
		// 状態反映
		chack.setSelected(cond.isEd_enable());
		ed_panel.setEnabled(cond.isEd_enable());
		return mainPanel;
	}

	private JComponent getCenter(FindAlarmCondition cond) {
		Box box = Box.createVerticalBox();
		box.add(getKindList(cond));
		if (isShowSortColumn) {
			box.add(getPriortyList(cond));
		}
		return box;
	}

	private JComponent getKindList(FindAlarmCondition cond) {
		// 種別
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		panel.add(getSyubetu(), BorderLayout.WEST);
		JPanel box = new JPanel(new BorderLayout());
		DefaultListModel model = new DefaultListModel();
		kindList = new JList(model);
		JScrollPane sc = new JScrollPane(kindList);
		box.add(sc, BorderLayout.CENTER);
		box.add(getButtonComponents(kindList), BorderLayout.SOUTH);
		panel.add(box);
		// 状態反映
		for (int i = 0; i < attris.length; i++) {
			model.add(i, attris[i].getName());
		}
		int[] sels = cond.getSelectKind();
		if (0 < sels.length) {
			int[] n_indices = new int[sels.length];
			for (int s = 0; s < sels.length; s++) {
				for (int i = 0; i < attris.length; i++) {
					if (sels[s] == attris[i].getAttribute()) {
						n_indices[s] = i;
					}
				}
			}
			kindList.setSelectedIndices(n_indices);
		} else {
			int[] n_indices = new int[attris.length];
			for (int i = 0; i < n_indices.length; i++) {
				n_indices[i] = i;
			}
			kindList.setSelectedIndices(n_indices);
		}
		return panel;
	}

	private Component getSyubetu() {
		Box box = Box.createVerticalBox();
		box.add(new JLabel("属性："));
		return box;
	}

	private Component getButtonComponents(final JList list) {
		JPanel panel = new JPanel();
		JButton clearButton = new JButton("クリア");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				list.clearSelection();
			}
		});
		panel.add(clearButton);

		JButton selectButton = new JButton("選択反転");
		selectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int listlen = list.getModel().getSize();
				int[] s_indices = list.getSelectedIndices();
				int[] n_indices = new int[listlen - s_indices.length];
				for (int sp = 0, np = 0, pos = 0; pos < listlen; pos++) {
					if (s_indices.length <= sp || s_indices[sp] != pos) {
						n_indices[np] = pos;
						np++;
					} else {
						sp++;
					}
				}
				list.setSelectedIndices(n_indices);
			}
		});
		panel.add(selectButton);
		return panel;
	}

	private Component getPriortyList(FindAlarmCondition cond) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		JPanel subPanel = new JPanel(new BorderLayout());
		subPanel.add(getPriorityLabel(), BorderLayout.WEST);
		createPriorityList(cond);
		subPanel.add(new JScrollPane(prioritySelectList), BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(getButtonComponents(prioritySelectList));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 35, 0, 0));
		subPanel.add(buttonPanel, BorderLayout.SOUTH);
		panel.add(subPanel, BorderLayout.CENTER);
		return panel;
	}

	private void createPriorityList(FindAlarmCondition cond) {
		DefaultListModel model = new DefaultListModel();
		prioritySelectList = new JList(model);
		// 状態反映
		for (int i = 0; i < priorityList.size(); i++) {
			model.add(i, priorityList.get(i));
		}
		List sels = cond.getPriorities();
		if (!sels.isEmpty()) {
			int[] n_indices = new int[sels.size()];
			for (int s = 0; s < sels.size(); s++) {
				for (int i = 0; i < priorityList.size(); i++) {
					if (sels.get(s).equals(priorityList.get(i))) {
						n_indices[s] = i;
					}
				}
			}
			prioritySelectList.setSelectedIndices(n_indices);
		} else {
			int[] n_indices = new int[priorityList.size()];
			for (int i = 0; i < n_indices.length; i++) {
				n_indices[i] = i;
			}
			prioritySelectList.setSelectedIndices(n_indices);
		}
	}

	private Component getPriorityLabel() {
		Box box = Box.createVerticalBox();
		box.add(new JLabel("種別："));
		return box;
	}

	private JComponent getSouthComponents(FindAlarmCondition cond) {
		Box mainPanel = Box.createVerticalBox();
		setMessageRadioButtons(cond, mainPanel);
		setCheckRadioButtons(cond, mainPanel);
		setUnitFields(cond, mainPanel);
		setNameFields(cond, mainPanel);
		setOkCancel(mainPanel);
		return mainPanel;
	}

	private void setMessageRadioButtons(
			FindAlarmCondition cond,
			JComponent mainPanel) {
		// 条件
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		ButtonGroup group = new ButtonGroup();
		panel.add(new JLabel("条件："));
		JRadioButton radio_all = new JRadioButton("全て");
		radio_all.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					bitvalSelect = RadioStat.SELECTALL;
				}
			}
		});
		panel.add(radio_all);
		group.add(radio_all);
		JRadioButton radio_true = new JRadioButton("発生・運転のみ");
		radio_true.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					bitvalSelect = RadioStat.SELECTTRUE;
				}
			}
		});
		panel.add(radio_true);
		group.add(radio_true);
		JRadioButton radio_false = new JRadioButton("復旧・停止のみ");
		radio_false.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					bitvalSelect = RadioStat.SELECTFALSE;
				}
			}
		});
		panel.add(radio_false);
		group.add(radio_false);
		mainPanel.add(panel);
		// 状態反映
		bitvalSelect = cond.getBitvalSelect();
		if (bitvalSelect == RadioStat.SELECTTRUE) {
			radio_true.setSelected(true);
		} else if (bitvalSelect == RadioStat.SELECTFALSE) {
			radio_false.setSelected(true);
		} else if (bitvalSelect == RadioStat.SELECTALL) {
			radio_all.setSelected(true);
		}
	}

	private void setCheckRadioButtons(
			FindAlarmCondition cond,
			JComponent mainPanel) {
		// 確認
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		ButtonGroup group = new ButtonGroup();
		panel.add(new JLabel("確認：(ヒストリのみ)"));
		JRadioButton radio_all = new JRadioButton("全て");
		radio_all.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					histckSelect = RadioStat.SELECTALL;
				}
			}
		});
		panel.add(radio_all);
		group.add(radio_all);
		JRadioButton radio_true = new JRadioButton("確認済み");
		radio_true.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					histckSelect = RadioStat.SELECTTRUE;
				}
			}
		});
		panel.add(radio_true);
		group.add(radio_true);
		JRadioButton radio_false = new JRadioButton("未確認");
		radio_false.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (((JRadioButton) e.getSource()).isSelected()) {
					histckSelect = RadioStat.SELECTFALSE;
				}
			}
		});
		panel.add(radio_false);
		group.add(radio_false);
		mainPanel.add(panel);
		// 状態反映
		histckSelect = cond.getHistckSelect();
		if (histckSelect == RadioStat.SELECTTRUE) {
			radio_true.setSelected(true);
		} else if (histckSelect == RadioStat.SELECTFALSE) {
			radio_false.setSelected(true);
		} else if (histckSelect == RadioStat.SELECTALL) {
			radio_all.setSelected(true);
		}
	}

	private void setUnitFields(FindAlarmCondition cond, Box mainPanel) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("記号："));
		unitField = new JTextField(40);
		unitField.setText(cond.getUnit());
		panel.add(unitField);
		mainPanel.add(panel);
	}

	private void setNameFields(FindAlarmCondition cond, Box mainPanel) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.add(new JLabel("名称："));
		nameField = new JTextField(40);
		nameField.setText(cond.getName());
		panel.add(nameField);
		mainPanel.add(panel);
	}

	private void setOkCancel(JComponent mainPanel) {
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
		box.add(Box.createHorizontalGlue());
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (createFindAlarmCondition()) {
					ret_ok = true;
					dispose();
				}
			}
		});
		box.add(okButton);
		box.add(Box.createHorizontalStrut(5));
		JButton cancelButton = new JButton("キャンセル");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ret_ok = false;
				dispose();
			}
		});
		box.add(cancelButton);
		mainPanel.add(box);
	}

	private boolean createFindAlarmCondition() {
		int[] sels = kindList.getSelectedIndices();
		if (sels.length <= 0) {
			JOptionPane.showMessageDialog(
					this,
					"種別を１つ以上選択してください。",
					"F-11 client error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		int[] atid = new int[sels.length];
		String[] atnm = new String[sels.length];
		for (int s = 0; s < sels.length; s++) {
			atid[s] = attris[sels[s]].getAttribute();
			atnm[s] = attris[sels[s]].getName();
		}
		Object[] prioSels = prioritySelectList.getSelectedValues();
		if (prioSels.length <= 0) {
			JOptionPane.showMessageDialog(
					this,
					"種別を１つ以上選択してください。",
					"F-11 client error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		ret_cond = new FindAlarmCondition(
				st_panel.isEnabled(),
				st_panel.getCalendar(),
				ed_panel.isEnabled(),
				ed_panel.getCalendar(),
				atid,
				bitvalSelect,
				histckSelect,
				atnm,
				unitField.getText(),
				nameField.getText(),
				Arrays.asList(prioSels));
		return true;
	}
}
