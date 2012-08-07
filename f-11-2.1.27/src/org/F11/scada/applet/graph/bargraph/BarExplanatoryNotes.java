/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/bargraph/BarExplanatoryNotes.java,v 1.12.2.6 2007/07/12 09:41:29 frdm Exp $
 * $Revision: 1.12.2.6 $
 * $Date: 2007/07/12 09:41:29 $
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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jp.gr.java_conf.skrb.gui.lattice.LatticeConstraints;
import jp.gr.java_conf.skrb.gui.lattice.LatticeLayout;
import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.applet.graph.GraphPropertyModel;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;

public class BarExplanatoryNotes extends JPanel {
	private static final long serialVersionUID = 3413417611062636699L;

	/** ロギングAPI */
	private static Logger logger;

	/** シリーズ */
	private int seriseId;
	/** グラフプロパティ */
	private GraphPropertyModel graphPropertyModel;
	/** グラフステップ */
	private BarGraphStep barGraphStep;

	/** 値表示パネル */
	private ValuePain valuePain[];

	public BarExplanatoryNotes(
			int seriseId,
			GraphPropertyModel graphPropertyModel,
			BarGraphStep barGraphStep) {
		super(new GridLayout(0, 2));
		logger = Logger.getLogger(getClass().getName());
		this.seriseId = seriseId;
		this.graphPropertyModel = graphPropertyModel;
		this.barGraphStep = barGraphStep;
		init();
		logger.debug("initialize.");
	}

	/**
	 * 初期処理
	 */
	private void init() {
		add(new JLabel(""));

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		valuePain = new ValuePain[graphPropertyModel.getFoldCount() + 1];
		for (int fold = 0; fold <= graphPropertyModel.getFoldCount(); fold++) {
			valuePain[fold] = new ValuePain(
					seriseId,
					fold,
					graphPropertyModel,
					barGraphStep);
			graphPropertyModel.addPropertyChangeListener(valuePain[fold]);
			graphPropertyModel.addPropertyChangeListener(
					GraphPropertyModel.GROUP_CHANGE_EVENT,
					valuePain[fold]);
			panel.add(valuePain[fold]);
		}
		add(panel);
	}

	/**
	 * 参照値を表示するクラスです。
	 */
	private static class ValuePain extends JPanel implements
			PropertyChangeListener {
		private static final long serialVersionUID = -398156645587077998L;
		/** シリーズ */
		private int seriseId;
		/** 折り返し */
		private int foldId;
		/** グラフプロパティ */
		private GraphPropertyModel graphPropertyModel;
		/** グラフステップ */
		private BarGraphStep barGraphStep;

		/** 色 */
		private JLabel colorBar;
		/** 参照時刻 */
		private JLabel logtime;
		/** 参照値 */
		private JLabel logdata;
		/** 単位 */
		private JLabel mark;

		public ValuePain(
				int seriseId,
				int foldId,
				GraphPropertyModel graphPropertyModel,
				BarGraphStep barGraphStep) {
			super(new LatticeLayout(15, 1));

			this.seriseId = seriseId;
			this.foldId = foldId;
			this.graphPropertyModel = graphPropertyModel;
			this.barGraphStep = barGraphStep;

			init();
		}

		public void init() {
			setBorder(BorderFactory.createLoweredBevelBorder());

			LatticeConstraints c = new LatticeConstraints();
			c.top = 3;
			c.bottom = 1;
			c.left = 3;
			c.right = 1;
			c.adjust = LatticeConstraints.BOTH;
			c.fill = LatticeConstraints.BOTH;

			createColorBar(c);
			createLogtime(c);
			createLogdata(c);
			createMark(c);

			setPreferredSize(new Dimension(500, 20));
		}

		private void createColorBar(LatticeConstraints c) {
			c.x = 0;
			c.y = 0;
			c.width = 1;

			colorBar = new JLabel("　");
			colorBar.setBackground(graphPropertyModel.getColors()[foldId]);
			colorBar.setOpaque(true);
			colorBar.setBorder(BorderFactory.createLoweredBevelBorder());
			add(colorBar, c);
		}

		private void createLogtime(LatticeConstraints c) {
			c.x = 1;
			c.y = 0;
			c.width = 8;

			logtime = new JLabel("                ");
			add(logtime, c);
		}

		private void createLogdata(LatticeConstraints c) {
			c.x = 9;
			c.y = 0;
			c.width = 4;

			logdata = new JLabel("     ");
			logdata.setHorizontalAlignment(SwingConstants.RIGHT);
			add(logdata, c);
		}

		private void createMark(LatticeConstraints c) {
			c.x = 13;
			c.y = 0;
			c.width = 2;

			mark = new JLabel(graphPropertyModel.getPointMark(seriseId));
			add(mark, c);
		}

		public void propertyChange(PropertyChangeEvent evt) {
			resetComponents();
		}

		private void resetComponents() {
			colorBar.setBackground(graphPropertyModel.getColors()[foldId]);
			mark.setText(graphPropertyModel.getPointMark(seriseId));

			String valuetext = "     ";
			String pName = graphPropertyModel.getDataProviderName(seriseId);
			if (pName != null && !"".equals(pName)) {
				DataProvider provider = Manager.getInstance().getDataProvider(
						pName);
				DataHolder holder = provider.getDataHolder(graphPropertyModel
						.getDataHolderName(seriseId));

				ConvertValue converter = (ConvertValue) holder
						.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
				double ref = converter.convertInputValue(graphPropertyModel
						.getReferenceValue(seriseId, foldId));
				valuetext = converter.convertStringValue(ref);
			}
			logdata.setText(valuetext);

			Timestamp reftime = graphPropertyModel.getReferenceTime(
					seriseId,
					foldId);
			if (reftime.getTime() != 0) {
				logtime.setText(barGraphStep.getReferenceTimeString(reftime
						.getTime()));
			} else {
				logtime.setText("                ");
			}
		}
	}
}
