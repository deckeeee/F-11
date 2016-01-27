/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/bargraph/BarGraph.java,v 1.16.2.14 2007/07/31 01:58:37 frdm Exp $
 * $Revision: 1.16.2.14 $
 * $Date: 2007/07/31 01:58:37 $
 * 
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

package org.F11.scada.applet.graph.bargraph;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.F11.scada.applet.graph.GraphModel;
import org.F11.scada.applet.graph.GraphPropertyModel;
import org.F11.scada.applet.graph.GroupButtonListener;
import org.F11.scada.applet.graph.VerticallyScale;
import org.F11.scada.applet.symbol.GraphicManager;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * 棒グラフを表示するコンポーネントクラスです。
 * 
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class BarGraph extends JPanel {
	private static final long serialVersionUID = 5857677859878598549L;
	/** ロギングAPI */
	private static Logger logger;
	/** ツールバー */
	private JToolBar toolBar;
	/** メインパネル */
	private JPanel mainPanel;
	/** グラフプロパティ */
	private GraphPropertyModel graphPropertyModel;

	public BarGraph(
			GraphModel graphModel,
			GraphPropertyModel graphPropertyModel,
			String barstep,
			int axismode,
			boolean isYear) throws IOException, SAXException {
		super(new BorderLayout());

		logger = Logger.getLogger(getClass().getName());

		logger.debug("create BarGraph BarGraphPropertyModel start");
		this.graphPropertyModel = graphPropertyModel;
		logger.debug("create BarGraph BarGraphPropertyModel end");

		createToolBar();

		mainPanel = new JPanel(new BorderLayout());
		logger.debug("create BarGraph createExplanatoryNotes start");
		mainPanel.add(createExplanatoryNotes(barstep), BorderLayout.NORTH);
		logger.debug("create BarGraph createExpBarGraph lanatoryNotes end");
		logger.debug("create BarGraph createLeftVerticallyScale start");
		mainPanel.add(createLeftVerticallyScale(), BorderLayout.WEST);
		logger.debug("create BarGraph createLeftVerticallyScale end");
		logger.debug("create BarGraphView start");
		if (isYear) {
			mainPanel.add(new YearBarGraphView(
					graphModel,
					graphPropertyModel,
					barstep,
					axismode), BorderLayout.CENTER);
		} else {
			mainPanel.add(new BarGraphView(
					graphModel,
					graphPropertyModel,
					barstep,
					axismode), BorderLayout.CENTER);
		}
		logger.debug("create BarGraphView end");

		add(mainPanel, BorderLayout.CENTER);
	}

	private void createToolBar() {
		logger.debug("create BarGraph Toolbar start");
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		addButton(
				new JButton(GraphicManager
						.get("/toolbarButtonGraphics/navigation/Back24.gif")),
				GroupButtonListener.createBackListener(graphPropertyModel));
		addButton(
				new JButton(GraphicManager
						.get("/toolbarButtonGraphics/navigation/Forward24.gif")),
				GroupButtonListener.createForwardListener(graphPropertyModel));
		addButton(
				new JButton(GraphicManager.get("/images/list.png")),
				GroupButtonListener.createDialogListener(graphPropertyModel));

		toolBar.addSeparator();

		GroupLabel groupName = new GroupLabel(graphPropertyModel);
		toolBar.add(groupName);
		add(toolBar, BorderLayout.NORTH);
		logger.debug("create BarGraph Toolbar end");
	}

	/**
	 * ボタンをツールバーに追加してリスナーを登録します。
	 * 
	 * @param button ボタン
	 * @param l リスナー
	 */
	private void addButton(JButton button, ActionListener l) {
		button.addActionListener(l);
		toolBar.add(button);
	}

	private JPanel createExplanatoryNotes(String barstep) {
		JPanel panel = new JPanel(new GridLayout(graphPropertyModel
				.getSeriesSize(), 1));

		for (int i = 0; i < graphPropertyModel.getSeriesSize(); i++) {
			BarGraphStep barGraphStep = BarGraphStep.createBarGraphStep(
					barstep,
					graphPropertyModel.getHorizontalScaleWidth());
			panel.add(new BarExplanatoryNotes(
					i,
					graphPropertyModel,
					barGraphStep));
		}

		return panel;
	}

	private JPanel createLeftVerticallyScale() {
		JPanel panel = new JPanel();

		panel.add(VerticallyScale.createLeftStringScale(graphPropertyModel, 0));

		return panel;
	}

	public JComponent getMainPanel() {
		return mainPanel;
	}

	public JComponent getToolBar() {
		return toolBar;
	}

	static class GroupLabel extends JLabel implements PropertyChangeListener {
		private static final long serialVersionUID = -5255929314423250266L;
		/** 表示するテキストのフォーマッタークラス */
		private static final MessageFormat format = new MessageFormat(
				"ポイント：{0}");

		public GroupLabel(GraphPropertyModel model) {
			super(format.format(new String[] { model.getGroupName() }));
			setFont(getFont().deriveFont((float) (getFont().getSize2D() * 1.4)));
			model.addPropertyChangeListener(
					GraphPropertyModel.GROUP_CHANGE_EVENT,
					this);
		}

		/**
		 * GraphModelPropertyの内容が変更された時に呼び出されます。
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			GraphPropertyModel model = (GraphPropertyModel) evt.getSource();

			final String[] msg = new String[] { model.getGroupName() };
			setText(format.format(msg));
		}

	}
}
