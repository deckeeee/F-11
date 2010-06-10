/*
 * $Header$
 * $Revision$
 * $Date$
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

package org.F11.scada.applet.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.applet.symbol.HandCursorListener;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * トレンドグラフコンポーネントクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TrendGraph2 extends JPanel {
	private static final long serialVersionUID = -32263345741884296L;
	/** ロギングAPI */
	private static Logger logger;
	/** ツールバー */
	private JToolBar toolBar;
	/** メインパネル */
	private JPanel mainPanel;
	/** グラフプロパティ */
	private GraphPropertyModel graphPropertyModel;
	/** グラフビュー */
	private TrendGraphView view;

	private static final Color SELECT_COLOR = ColorFactory.getColor("darkgray");

	public TrendGraph2(GraphPropertyModel graphPropertyModel)
			throws IOException, SAXException {
		this(graphPropertyModel, null);
	}

	/**
	 * トレンドグラフコンポーネントを生成します。
	 * 
	 * @param graphModel グラフモデルオブジェクト
	 * @param graphPropertyModel グラフプロパティモデルオブジェクト
	 */
	public TrendGraph2(
			GraphPropertyModel graphPropertyModel,
			String horizontalScaleFile) throws IOException, SAXException {
		super(new BorderLayout());
		this.graphPropertyModel = graphPropertyModel;

		logger = Logger.getLogger(getClass().getName());

		TrendGraphToolBar trendGraphToolBar = new TrendGraphToolBar();
		toolBar = trendGraphToolBar.createToolBar(
				graphPropertyModel,
				horizontalScaleFile);
		add(toolBar, BorderLayout.NORTH);

		mainPanel = new JPanel(new BorderLayout());

		JPanel notesPanel = new JPanel(new BorderLayout());
		JPanel southPane = new JPanel(new BorderLayout());
		ModelModeLabel modelMode = new ModelModeLabel("自動更新");
		modelMode.setBorder(BorderFactory.createRaisedBevelBorder());
		southPane.add(modelMode, BorderLayout.WEST);
		ReferenceTimestampLabel ref = new ReferenceTimestampLabel(
				graphPropertyModel);
		southPane.add(ref, BorderLayout.CENTER);
		notesPanel.add(southPane, BorderLayout.SOUTH);

		VerticallyScale[] scales = createVerticallyScales();
		JPanel scalePanel = createLeftVerticallyScale(scales[0]);
		mainPanel.add(scalePanel, BorderLayout.WEST);
		VerticallyScaleManager manager = getVerticallyScaleManager(
				scales,
				scalePanel);

		ExplanatoryNotes[] notes = createExplanatoryNotes();
		ExplanatoryNotesManager notesManager = getExplanatoryNotesManager(notes);
		notesPanel.add(
				createExplanatoryNotes(notes, manager, notesManager),
				BorderLayout.CENTER);
		mainPanel.add(notesPanel, BorderLayout.NORTH);

		view = new TrendGraphView(graphPropertyModel);
		view.addPropertyChangeListener(
				"org.F11.scada.applet.graph.LineGraph.GraphModelSet",
				modelMode);
		mainPanel.add(view, BorderLayout.CENTER);
		mainPanel.addPropertyChangeListener(view);

		add(mainPanel, BorderLayout.CENTER);

		ModelModeLabelListener labelListener = new ModelModeLabelListener(
				graphPropertyModel,
				view);
		modelMode.addActionListener(labelListener);
	}

	private ExplanatoryNotes[] createExplanatoryNotes() {
		ExplanatoryNotes[] notes = new ExplanatoryNotes[6];
		for (int i = 0; i < notes.length; i++) {
			ExplanatoryNotes en = new ExplanatoryNotes(i, graphPropertyModel);
			en.addMouseListener(new HandCursorListener());
			notes[i] = en;
		}
		return notes;
	}

	private ExplanatoryNotesManager getExplanatoryNotesManager(
			ExplanatoryNotes[] notes) {
		return new ExplanatoryNotesManager(notes);
	}

	private JPanel createExplanatoryNotes(
			ExplanatoryNotes[] notes,
			VerticallyScaleManager manager,
			ExplanatoryNotesManager notesManager) {
		JPanel panel = new JPanel(new GridLayout(3, 2));

		for (int i = 0; i < notes.length; i++) {
			notes[i].addMouseListener(new NotesMouseListener(
					manager,
					notesManager));
			panel.add(notes[i]);
		}
		notes[0].setBorder(BorderFactory.createLoweredBevelBorder());
		notes[0].setBackground(SELECT_COLOR);

		return panel;
	}

	private VerticallyScale[] createVerticallyScales() {
		VerticallyScale[] scales = new VerticallyScale[6];
		for (int i = 0; i < scales.length; i++) {
			scales[i] = VerticallyScale.createLeftStringScale(
					graphPropertyModel,
					i);
		}
		return scales;
	}

	private JPanel createLeftVerticallyScale(Component scale) {
		JPanel panel = new JPanel();

		panel.add(scale);
		panel.setBackground(graphPropertyModel.getVerticallyScaleProperty()
				.getBackGroundColor());
		panel.setOpaque(true);

		return panel;
	}

	private VerticallyScaleManager getVerticallyScaleManager(
			VerticallyScale[] scales,
			JPanel panel) {
		return new VerticallyScaleManager(scales, panel);
	}

	public JComponent getMainPanel() {
		return mainPanel;
	}

	public JComponent getToolBar() {
		return toolBar;
	}

	public void setStrokeWidth(float width) {
		view.setStrokeWidth(width);
	}

	private static class VerticallyScaleManager {
		private final VerticallyScale[] scales;
		private final JPanel panel;

		VerticallyScaleManager(VerticallyScale[] scales, JPanel panel) {
			this.scales = scales;
			this.panel = panel;
		}

		void setVerticallyScale(final ExplanatoryNotes notes) {
			panel.removeAll();
			panel.add(getVerticallyScale(notes));
			panel.revalidate();
			panel.repaint();
		}

		Component getVerticallyScale(ExplanatoryNotes notes) {
			return scales[notes.getSeriseId()];
		}
	}

	private static class ExplanatoryNotesManager {
		private final ExplanatoryNotes[] notes;
		private final Color defaultColor;

		ExplanatoryNotesManager(ExplanatoryNotes[] notes) {
			this.notes = notes;
			defaultColor = notes[0].getBackground();
		}

		void setBorder(final ExplanatoryNotes note) {
			for (int i = 0; i < notes.length; i++) {
				notes[i].setBorder(BorderFactory.createRaisedBevelBorder());
				notes[i].setBackground(defaultColor);
				notes[i].repaint();
			}
			notes[note.getSeriseId()].setBorder(BorderFactory
					.createLoweredBevelBorder());
			notes[note.getSeriseId()].setBackground(SELECT_COLOR);
			notes[note.getSeriseId()].repaint();
		}
	}

	private static class NotesMouseListener extends MouseAdapter {
		private final VerticallyScaleManager manager;
		private final ExplanatoryNotesManager notesManager;

		NotesMouseListener(
				VerticallyScaleManager manager,
				ExplanatoryNotesManager notesManager) {
			this.manager = manager;
			this.notesManager = notesManager;
		}

		public void mouseReleased(MouseEvent e) {
			ExplanatoryNotes notes = (ExplanatoryNotes) e.getSource();
			manager.setVerticallyScale(notes);
			notesManager.setBorder(notes);
		}
	}
}
