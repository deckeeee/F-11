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

package org.F11.scada.applet.ngraph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.F11.scada.applet.ngraph.event.GraphChangeEvent;
import org.F11.scada.applet.ngraph.model.DefaultAllGraphModel;
import org.F11.scada.applet.ngraph.model.DefaultGraphModel;
import org.F11.scada.applet.ngraph.model.GraphModel;
import org.apache.commons.collections.primitives.DoubleIterator;
import org.apache.log4j.Logger;

/**
 * トレンドグラフのメインパネル
 * 各コンポーネントのメデエイターにもなっている
 * 
 * @author maekawa
 *
 */
public class GraphMainPanel extends JPanel implements Mediator {
	private final Logger logger = Logger.getLogger(GraphMainPanel.class);
	private GraphStatusBar statusBar;
	private GraphProperties graphProperties;
	private GraphModel graphModel;
	private GraphView graphView;
	private GraphScrollBar scrollBar;
	private SeriesTableModel seriesModel;
	private SeriesTable seriesTable;
	private GraphToolBar graphToolBar;

	public GraphMainPanel(GraphProperties graphProperties) {
		super(new BorderLayout());
		this.graphProperties = graphProperties;
		createColleagus();
	}

	private void createColleagus() {
		graphModel = new DefaultGraphModel(graphProperties);
		graphView = new GraphView(this, graphModel, graphProperties);
		scrollBar = new GraphScrollBar(graphView);
		statusBar = new GraphStatusBar(this);
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(statusBar, BorderLayout.NORTH);
		centerPanel.add(graphView, BorderLayout.CENTER);
		seriesModel = new SeriesTableModel(graphProperties);
		graphView.setSeriesMaxCount(seriesModel.getRowCount());
		seriesModel.addTableModelListener(new ChangeTableModelListener(
			graphView));
		graphView.addPropertyChangeListener(
			GraphView.GRAPH_CLICKED_CHANGE,
			new GraphClickedListener(seriesModel));
		seriesTable = new SeriesTable(seriesModel, this, graphProperties);
		JScrollPane spane = new JScrollPane(seriesTable);
		spane.setPreferredSize(new Dimension(
			graphView.getPreferredSize().width,
			Math.round(seriesTable.getPreferredSize().height
				+ seriesTable.getRowHeight()
				+ seriesTable.getRowMargin()
				* 2.5F)));
		JPanel northPanel = new JPanel(new BorderLayout());
		graphToolBar = new GraphToolBar(this, graphProperties);
//		northPanel.add(graphToolBar, BorderLayout.NORTH);
		northPanel.add(spane, BorderLayout.CENTER);
		add(northPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(scrollBar, BorderLayout.SOUTH);
		graphModel.setLogName("log_table_minute");
	}

	public void colleaguChanged(Colleague colleague) {
		if (statusBar == colleague) {
			if (statusBar.isDataAreaListener()) {
				String logName = graphModel.getLogName();
				if (graphView.isAllDataDisplayMode()) {
					logger.info("全データモデルモード");
					GraphModel model =
						new DefaultAllGraphModel(graphProperties);
					graphView.setModel(model);
					graphModel = model;
				} else {
					logger.info("自動更新データモデルモード");
					GraphModel model = new DefaultGraphModel(graphProperties);
					graphView.setModel(model);
					graphModel = model;
				}
				graphModel.setLogName(logName);
			}
			statusBar.performColleagueChange(getGraphChangeEvent());
		} else if (graphView == colleague) {
			graphView.performColleagueChange(getGraphChangeEvent());
			statusBar.performColleagueChange(getGraphChangeEvent());
		} else if (graphToolBar == colleague) {
			graphToolBar.performColleagueChange(getGraphChangeEvent());
			seriesModel.fireTableDataChanged();
		} else if (seriesTable == colleague) {
			seriesTable.performColleagueChange(getGraphChangeEvent());
		}
	}

	public GraphChangeEvent getGraphChangeEvent() {
		return new GraphChangeEvent(
			this,
			graphView,
			graphModel,
			graphProperties,
			scrollBar,
			statusBar,
			seriesTable,
			seriesModel);
	}

	public JComponent getToolBar() {
		return graphToolBar;
	}

	private static class ChangeTableModelListener implements TableModelListener {
		private final GraphView graph;

		public ChangeTableModelListener(GraphView graph) {
			this.graph = graph;
		}

		public void tableChanged(TableModelEvent e) {
			graph.repaint();
		}
	}

	private static class GraphClickedListener implements PropertyChangeListener {
		private final TableModel model;

		public GraphClickedListener(TableModel model) {
			this.model = model;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			LogData data = (LogData) evt.getNewValue();
			int i = 0;
			for (DoubleIterator it = data.getValues().iterator(); it.hasNext(); i++) {
				double d = it.next();
				model.setValueAt(d, i, SeriesTableModel.REFERENCE_VALUE_COLUMN);
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame f = new JFrame("トレンドグラフテスト");
				f.add(
					new GraphMainPanel(new GraphProperties()),
					BorderLayout.CENTER);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.pack();
				f.setVisible(true);
			}
		});
	}
}
