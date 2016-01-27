/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/TrendGraph.java,v 1.18.2.15 2006/09/28 02:53:38 frdm Exp $
 * $Revision: 1.18.2.15 $
 * $Date: 2006/09/28 02:53:38 $
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
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.F11.scada.applet.symbol.ColorFactory;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * �g�����h�O���t�R���|�[�l���g�N���X�ł��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TrendGraph extends JPanel {
	private static final long serialVersionUID = -8046608177604064271L;
	/** ���M���OAPI */
	private static Logger logger;
	/** �c�[���o�[ */
	private JToolBar toolBar;
	/** ���C���p�l�� */
	private JPanel mainPanel;
	/** �O���t�v���p�e�B */
	private GraphPropertyModel graphPropertyModel;
	/** �O���t�r���[ */
	private TrendGraphView view;

	/**
	 * �g�����h�O���t�R���|�[�l���g�𐶐����܂��B
	 * 
	 * @param graphModel �O���t���f���I�u�W�F�N�g
	 * @param graphPropertyModel �O���t�v���p�e�B���f���I�u�W�F�N�g
	 */
	public TrendGraph(GraphPropertyModel graphPropertyModel)
			throws IOException, SAXException {
		this(graphPropertyModel, null);
	}

	public TrendGraph(
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
		ModelModeLabel modelMode = new ModelModeLabel("�����X�V");
		modelMode.setBorder(BorderFactory.createRaisedBevelBorder());
		southPane.add(modelMode, BorderLayout.WEST);
		ReferenceTimestampLabel ref = new ReferenceTimestampLabel(
				graphPropertyModel);
		southPane.add(ref, BorderLayout.CENTER);
		notesPanel.add(southPane, BorderLayout.SOUTH);
		notesPanel.add(createExplanatoryNotes(), BorderLayout.CENTER);
		mainPanel.add(notesPanel, BorderLayout.NORTH);

		mainPanel.add(createLeftVerticallyScale(), BorderLayout.WEST);
		mainPanel.add(createRightVerticallyScale(), BorderLayout.EAST);

		view = new TrendGraphView(graphPropertyModel);
		view.addPropertyChangeListener(
				TrendGraphView.LineGraph.GRAPH_MODEL_SET,
				modelMode);
		mainPanel.add(view, BorderLayout.CENTER);

		add(mainPanel, BorderLayout.CENTER);

		ModelModeLabelListener labelListener =
			new ModelModeLabelListener(graphPropertyModel, view);
		modelMode.addActionListener(labelListener);
	}

	private JPanel createExplanatoryNotes() {
		JPanel panel = new JPanel(new GridLayout(3, 2));

		for (int i = 0; i < 6; i++) {
			panel.add(new ExplanatoryNotes(i, graphPropertyModel));
		}

		return panel;
	}

	private JPanel createLeftVerticallyScale() {
		JPanel panel = new JPanel();
		for (int i = 0; i < 3; i++) {
			panel.add(VerticallyScale.createLeftStringScale(
					graphPropertyModel,
					i));
		}

		panel.setBackground(ColorFactory.getColor("gray"));
		panel.setOpaque(true);

		return panel;
	}

	private JPanel createRightVerticallyScale() {
		JPanel panel = new JPanel();

		for (int i = 3; i < 6; i++) {
			panel.add(VerticallyScale.createRightStringScale(
					graphPropertyModel,
					i));
		}

		panel.setBackground(ColorFactory.getColor("gray"));
		panel.setOpaque(true);

		return panel;
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
}
