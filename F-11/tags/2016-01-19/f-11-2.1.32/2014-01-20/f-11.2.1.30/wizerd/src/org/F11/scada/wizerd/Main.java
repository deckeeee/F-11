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

package org.F11.scada.wizerd;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.F11.scada.wizerd.panel.CopyWizerd;
import org.F11.scada.wizerd.panel.PreferenceWizerd;
import org.F11.scada.wizerd.panel.Wizerd;
import org.apache.log4j.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;
import org.jdesktop.application.Task.BlockingScope;

/**
 * MySQLインストールウィザードアプリケーション
 * 
 * @author maekawa
 * 
 */
public class Main extends SingleFrameApplication {
	private final Logger logger = Logger.getLogger(Main.class);
	private Wizerd wizerdPanel;
	private JButton nextButton;
	private JPanel mainPanel;
	private JButton executeButton;
	private Box southBox;

	@Override
	protected void startup() {
		mainPanel = new JPanel(new BorderLayout());
		wizerdPanel = new CopyWizerd();
		mainPanel.add(wizerdPanel, BorderLayout.CENTER);
		mainPanel.add(getSouth(), BorderLayout.SOUTH);
		getContext().getResourceMap().injectComponents(mainPanel);
		JFrame frame = getMainFrame();
		frame.add(mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(740, 370);
		frame.setVisible(true);
		wizerdPanel.init();
	}

	private Component getSouth() {
		southBox = Box.createHorizontalBox();
		southBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		southBox.add(Box.createHorizontalGlue());
		ActionMap map = getContext().getActionMap();

		executeButton = new JButton();
		executeButton.setName("executeButton");
		executeButton.setAction(map.get("executeButton"));
		southBox.add(executeButton);

		southBox.add(Box.createHorizontalStrut(5));

		nextButton = new JButton();
		nextButton.setName("nextButton");
		nextButton.setAction(map.get("nextButton"));
		southBox.add(nextButton);
		return southBox;
	}

	@Action(block = BlockingScope.APPLICATION)
	public Task<Void, Void> executeButton() {
		return wizerdPanel.execute(this);
	}

	@Action
	public void nextButton() {
		String base = wizerdPanel.getBaseDirectory();
		mainPanel.remove(wizerdPanel);
		wizerdPanel = new PreferenceWizerd(base);
		mainPanel.add(wizerdPanel, BorderLayout.CENTER);
		mainPanel.revalidate();
		mainPanel.repaint();
		executeButton.setText("設定");
		nextButton.setEnabled(false);
		wizerdPanel.init();
	}

	public void setNextEnabled() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				nextButton.setEnabled(true);
			}
		});
	}

	public void setEndButton() {
		southBox.removeAll();
		executeButton = new JButton("終了");
		executeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		southBox.add(Box.createHorizontalGlue());
		southBox.add(executeButton);
		southBox.revalidate();
		southBox.repaint();
	}

	public TaskListener<Void, Void> getTaskListener() {
		return new CopyTaskListener(this);
	}

	public TaskListener<Void, Void> getPreferenceTaskListener() {
		return new PreferenceTaskListener(this);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(Main.class, args);
	}

	/**
	 * コピータスクリスナー
	 * 
	 * @author maekawa
	 * 
	 */
	private static class CopyTaskListener extends
			TaskListener.Adapter<Void, Void> {
		private final Main main;

		public CopyTaskListener(Main main) {
			this.main = main;
		}

		@Override
		public void finished(TaskEvent<Void> event) {
			Task<Void, Void> task = (Task<Void, Void>) event.getSource();
			if (!task.isCancelled()) {
				main.setNextEnabled();
			}
		}
	}

	/**
	 * 設定タスクリスナー
	 * 
	 * @author maekawa
	 * 
	 */
	private static class PreferenceTaskListener extends
			TaskListener.Adapter<Void, Void> {
		private final Main main;

		public PreferenceTaskListener(Main main) {
			this.main = main;
		}

		@Override
		public void finished(TaskEvent<Void> event) {
			Task<Void, Void> task = (Task<Void, Void>) event.getSource();
			if (!task.isCancelled()) {
				main.setEndButton();
			}
		}
	}
}
