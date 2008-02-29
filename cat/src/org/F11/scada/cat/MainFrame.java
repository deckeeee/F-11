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

package org.F11.scada.cat;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.F11.scada.cat.component.CheckList;
import org.F11.scada.cat.logic.CheckLogic;
import org.F11.scada.cat.logic.CheckLogicFactory;
import org.F11.scada.cat.logic.ExecuteTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.jdesktop.application.Task.BlockingScope;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.S2ContainerFactory;

/**
 * 定義ファイルチェックプログラム
 * 
 * @author maekawa
 * 
 */
public class MainFrame extends SingleFrameApplication {
	/** ロギング */
	private final Log log = LogFactory.getLog(MainFrame.class);
	/** チェック処理ロジックのリスト */
	private List<CheckLogic> checkLogics;

	@Override
	protected void startup() {
		getCheckLogics();
		JFrame mainFrame = getMainFrame();
		mainFrame.setJMenuBar(createMenuBar());
		mainFrame.add(createMain(), BorderLayout.CENTER);
		show(mainFrame);
	}

	private void getCheckLogics() {
		// cat用のコンテナを生成
		S2Container container = S2ContainerFactory.create("app.dicon");
		CheckLogicFactory factory =
			(CheckLogicFactory) container.getComponent(CheckLogicFactory.class);
		checkLogics = factory.getCheckLogics();
	}

	private JMenuBar createMenuBar() {
		String[] demoMenuActionNames = { "executeCheckLogic", "quit" };
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createMenu("demoMenu", demoMenuActionNames));
		return menuBar;
	}

	private JMenu createMenu(String menuName, String[] actionNames) {
		JMenu menu = new JMenu();
		menu.setName(menuName);
		for (String actionName : actionNames) {
			JMenuItem menuItem = new JMenuItem();
			menuItem.setAction(actionMap().get(actionName));
			menu.add(menuItem);
		}
		return menu;
	}

	private ActionMap actionMap() {
		return getContext().getActionMap();
	}

	private JComponent createMain() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(createCenter(), BorderLayout.CENTER);
		panel.add(createSouth(), BorderLayout.SOUTH);
		return panel;
	}

	private Component createCenter() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setName("checkPanel");
		String text = getContext().getResourceMap().getString("checkPanel");
		panel.setBorder(BorderFactory.createTitledBorder(text));
		panel.add(createCheckBoxes());
		return panel;
	}

	private Component createCheckBoxes() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
		CheckList list = new CheckList(checkLogics);
		panel.add(new JScrollPane(list));
		return panel;
	}

	private Component createSouth() {
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		box.add(Box.createHorizontalGlue());
		JButton button = new JButton();
		button.setName("checkButton");
		ActionMap map = getContext().getActionMap();
		button.setAction(map.get("executeCheckLogic"));
		box.add(button);
		return box;
	}

	@Action(block = BlockingScope.APPLICATION)
	public Task<Void, Void> executeCheckLogic() {
		return new ExecuteTask(this, checkLogics, new File("")
			.getAbsolutePath());
	}

	public static void main(String[] args) {
		launch(MainFrame.class, args);
	}
}
