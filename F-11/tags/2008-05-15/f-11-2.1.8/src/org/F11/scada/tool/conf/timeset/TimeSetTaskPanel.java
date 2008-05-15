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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.F11.scada.tool.conf.TimeSetManager;
import org.F11.scada.tool.conf.io.TimeSetBean;
import org.F11.scada.tool.conf.io.TimeSetTaskBean;
import org.F11.scada.util.ComponentUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TimeSetTaskPanel extends JPanel {
	private static final long serialVersionUID = -8257220545924970488L;
	private final TimeSetTaskTableModel model;
	private final JTable table;
	private final TimeSetManager manager;

	public TimeSetTaskPanel(TimeSetManager manager) {
		super(new BorderLayout());
		this.manager = manager;
		model = new TimeSetTaskTableModel(manager);
		table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		createEast();
		createCenter();
	}

	private void createEast() {
		Box box = Box.createVerticalBox();
		box.add(getAddButton());
		box.add(getRemoveButton());
		box.add(getUpdateButton());
		add(box, BorderLayout.EAST);
	}

	private JButton getUpdateButton() {
		final JButton button = new JButton("変更");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Frame frame =
					ComponentUtil.getAncestorOfClass(Frame.class, button);
				int row = table.getSelectedRow();
				if (0 <= row) {
					TimeSetTaskDialog dialog =
						new TimeSetTaskDialog(frame, model, row, manager);
					TimeSetTaskBean bean = dialog.getBean();
					if (null != bean) {
						model.update(bean, row);
					}
				}
			}
		});
		return button;
	}

	private JButton getRemoveButton() {
		final JButton button = new JButton("削除");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Frame frame =
					ComponentUtil.getAncestorOfClass(Frame.class, button);
				int row = table.getSelectedRow();
				if (0 <= row && openConfirm(frame)) {
					model.remove(row);
				}
			}

			private boolean openConfirm(Frame frame) {
				return JOptionPane.showConfirmDialog(
					frame,
					"削除します。よろしいですか？",
					"アイテム削除",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
			}
		});
		return button;
	}

	private JButton getAddButton() {
		final JButton button = new JButton("追加");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Frame frame =
					ComponentUtil.getAncestorOfClass(Frame.class, button);
				TimeSetTaskDialog dialog =
					new TimeSetTaskDialog(frame, model, -1, manager);
				TimeSetTaskBean bean = dialog.getBean();
				if (null != bean) {
					model.insert(bean);
				}
			}
		});
		return button;
	}

	private void createCenter() {
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.add(new TimeSetTaskPanel(new TimeSetManager() {
					private Log log = LogFactory.getLog(this.getClass());

					public String getTimeSet(String name, String key, String def) {
						log.info(name + " " + key + " " + def);
						return "";
					}

					public List<TimeSetBean> getTimeSetBeansList(String name) {
						log.info(name);
						return new ArrayList<TimeSetBean>();
					}

					public void setTimeSet(String name, String key, String value) {
						log.info(name + " " + key + " " + value);
					}

					public void setTimeSetBeansList(
							String name,
							List<TimeSetBean> list) {
						log.info(name + " " + list);
					}

					public void setTimeSetTask(TimeSetTaskBean bean) {
						log.info(bean);
					}

					public TimeSetTaskBean removeTimeSetTask(
							TimeSetTaskBean bean) {
						log.info(bean);
						TimeSetTaskBean timeSetTaskBean = new TimeSetTaskBean();
						ArrayList<TimeSetBean> l = new ArrayList<TimeSetBean>();
						timeSetTaskBean.setTimeList(l);
						return timeSetTaskBean;
					}

					public List<TimeSetTaskBean> getTimeSetTask() {
						return new ArrayList<TimeSetTaskBean>();
					}

					public TimeSetTaskBean getTimeSetTask(String name) {
						return new TimeSetTaskBean();
					}
				}));
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
}
