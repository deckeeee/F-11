package org.F11.scada.applet.dialog;

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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.applet.graph.JListUtil;
import org.F11.scada.applet.schedule.ScheduleModel;
import org.apache.log4j.Logger;

/**
 * スケジュール・グループ一覧ダイアログクラスです。
 */
public class ScheduleGroupListDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -3777297110635074258L;

	/** スケジュールのデータモデルです */
	private ScheduleModel scheduleModel;

	private JList list;
	private JButton okButton;
	private JButton cancelButton;

	private static Logger log = Logger.getLogger(ScheduleGroupListDialog.class);

	public ScheduleGroupListDialog(Frame frame, ScheduleModel scheduleModel) {
		super(frame);
		this.scheduleModel = scheduleModel;
		init();
	}

	public ScheduleGroupListDialog(Dialog dialog, ScheduleModel scheduleModel) {
		super(dialog);
		this.scheduleModel = scheduleModel;
		init();
	}

	private void init() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		int currentGroup = scheduleModel.getGroupNo();
		Object[] o = new Object[scheduleModel.getGroupNoMax()];
		for (int i = 0; i < o.length; i++) {
			scheduleModel.setGroupNo(i);
			o[i] = (i + 1) + " : " + scheduleModel.getGroupName();
		}
		scheduleModel.setGroupNo(currentGroup);

		list = new JList(o);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(currentGroup);
		JScrollPane scpane = new JScrollPane(list);
		JViewport p = scpane.getViewport();
		p.setViewPosition(new Point(0, scheduleModel.getGroupNo()
			* scpane.getVerticalScrollBar().getBlockIncrement(1)));

		okButton = new JButton("OK");
		cancelButton = new JButton("CANCEL");

		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		Container cont = getContentPane();
		cont.add(scpane, BorderLayout.CENTER);
		cont.add(buttonPanel, BorderLayout.SOUTH);

		list.addMouseListener(new ListListener(this));

		setTitle("グループ選択");

		setSize();

		Window window =
			(Window) SwingUtilities.getAncestorOfClass(Window.class, this);
		Dimension fDimension = window.getSize();
		Dimension dDimension = getSize();
		Point location = window.getLocation();
		location.translate(
			(fDimension.width - dDimension.width) / 2,
			(fDimension.height - dDimension.height) / 2);
		setLocation(location);
	}

	private void setSize() {
		ClientConfiguration configuration = new ClientConfiguration();
		setSize(configuration.getInt(
			"xwife.applet.Applet.schedule.dialog.width",
			157), configuration.getInt(
			"xwife.applet.Applet.schedule.dialog.height",
			217));
	}

	public void actionPerformed(ActionEvent evt) {
		JButton button = (JButton) evt.getSource();
		if (button == okButton) {
			pushOkButton(list.getSelectedIndex());
		} else if (button == cancelButton) {
			dispose();
		} else {
			log.error("想定外のアクションイベントです: source=" + button.getClass().getName());
		}
	}

	private void pushOkButton(int index) {
		if (JListUtil.hasSelected(this, index)) {
			scheduleModel.setGroupNo(index);
			dispose();
		}
	}

	/**
	 * リスト上のダブルクリックイベントを処理するクラスです。
	 */
	private static class ListListener extends MouseAdapter {
		private final Logger logger = Logger.getLogger(ListListener.class);
		private ScheduleGroupListDialog dialog;

		ListListener(ScheduleGroupListDialog dialog) {
			this.dialog = dialog;
		}

		public void mouseReleased(MouseEvent e) {
			if (e.getClickCount() == 2) {
				int index = dialog.list.locationToIndex(e.getPoint());
				dialog.pushOkButton(index);
			}
		}
	}
}
