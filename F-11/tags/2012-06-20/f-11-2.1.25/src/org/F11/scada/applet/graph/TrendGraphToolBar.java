/*
 * =============================================================================
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

package org.F11.scada.applet.graph;

import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.F11.scada.applet.symbol.GraphicManager;
import org.apache.log4j.Logger;

public class TrendGraphToolBar {
	/** ロギングAPI */
	private static Logger logger = Logger.getLogger(TrendGraphToolBar.class);

	JToolBar createToolBar(
			GraphPropertyModel graphPropertyModel,
			String horizontalScaleFile) {
		logger.debug("create Toolbar start");
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		toolBar.addSeparator();

		HorizontalScaleButtonBuilder builder = new HorizontalScaleButtonBuilder(
				horizontalScaleFile);
		HorizontalScaleButtonFactory factory = builder.create();

		Collection buttons = factory
				.getHorizontalScaleButtons(graphPropertyModel);
		for (Iterator i = buttons.iterator(); i.hasNext();) {
			JButton button = (JButton) i.next();
			toolBar.add(button);
		}

		toolBar.addSeparator();

		addButton(
				new JButton(GraphicManager
						.get("/toolbarButtonGraphics/navigation/Back24.gif")),
				GroupButtonListener.createBackListener(graphPropertyModel),
				toolBar);
		addButton(
				new JButton(GraphicManager
						.get("/toolbarButtonGraphics/navigation/Forward24.gif")),
				GroupButtonListener.createForwardListener(graphPropertyModel),
				toolBar);
		addButton(
				new JButton(GraphicManager.get("/images/list.png")),
				GroupButtonListener.createDialogListener(graphPropertyModel),
				toolBar);

		toolBar.addSeparator();

		GroupLabel groupName = new GroupLabel(graphPropertyModel);
		toolBar.add(groupName);

		logger.debug("create Toolbar end");
		return toolBar;
	}

	/**
	 * ボタンをツールバーに追加してリスナーを登録します。
	 * 
	 * @param button ボタン
	 * @param l リスナー
	 */
	private void addButton(JButton button, ActionListener l, JToolBar toolBar) {
		button.addActionListener(l);
		toolBar.add(button);
	}
}
