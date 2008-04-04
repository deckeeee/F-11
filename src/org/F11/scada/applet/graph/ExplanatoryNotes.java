/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/ExplanatoryNotes.java,v 1.18.2.7 2007/07/12 09:41:51 frdm Exp $
 * $Revision: 1.18.2.7 $
 * $Date: 2007/07/12 09:41:51 $
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

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jp.gr.java_conf.skrb.gui.lattice.LatticeConstraints;
import jp.gr.java_conf.skrb.gui.lattice.LatticeLayout;
import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;

/**
 * 凡例コンポーネントクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ExplanatoryNotes extends JPanel implements PropertyChangeListener {
	private static final long serialVersionUID = 4230218396121133190L;
	/** ロギングAPI */
	private static Logger logger;
	/** シリーズ */
	private int seriseId;
	/** グラフプロパティ */
	private GraphPropertyModel graphPropertyModel;

	/** 色 */
	private JLabel colorBar;
	/** データ名 */
	private JLabel dataName;
	/** 参照値ラベル */
	private JLabel logdataLabel;
	/** 参照値 */
	private JLabel logdata;
	/** 単位記号 */
	private JLabel mark;
	/** 現在値 */
	private JLabel cullentDataLabel;
	/** 現在値 */
	private ExplanatoryNotesText cullentData;

	/**
	 * コンストラクタ
	 * 
	 * @param seriseId シリーズ
	 * @param graphPropertyModel グラフプロパティ
	 */
	public ExplanatoryNotes(int seriseId, GraphPropertyModel graphPropertyModel) {
		super(new LatticeLayout(20, 2));
		this.seriseId = seriseId;
		this.graphPropertyModel = graphPropertyModel;
		this.graphPropertyModel.addPropertyChangeListener(this);
		this.graphPropertyModel.addPropertyChangeListener(
			GraphPropertyModel.GROUP_CHANGE_EVENT,
			this);
		logger = Logger.getLogger(getClass().getName());

		init();
	}

	/**
	 * 初期処理
	 */
	private void init() {
		setBorder(BorderFactory.createRaisedBevelBorder());

		LatticeConstraints c = new LatticeConstraints();
		initLatticeConst(c);

		createColorBar(c);
		createDataName(c);
		createLogdataLabel(c);
		createLogdata(c);
		createMark(c);
		createCurrentDataLabel(c);
		createCurrentData(c);

		setPreferredSize(new Dimension(520, 50));
	}

	private void initLatticeConst(LatticeConstraints c) {
		c.top = 3;
		c.bottom = 1;
		c.left = 3;
		c.right = 1;
		c.adjust = LatticeConstraints.BOTH;
		c.fill = LatticeConstraints.BOTH;
	}

	private void createColorBar(LatticeConstraints c) {
		c.x = 0;
		c.y = 0;
		c.width = 1;

		colorBar = new JLabel("　");
		colorBar.setBackground(graphPropertyModel.getColors()[seriseId]);
		colorBar.setOpaque(true);
		colorBar.setBorder(BorderFactory.createLoweredBevelBorder());
		setTextProperty(colorBar);
		add(colorBar, c);
	}

	private void createDataName(LatticeConstraints c) {
		c.x = 1;
		c.y = 0;
		c.width = 12;

		dataName = new JLabel(graphPropertyModel.getPointName(seriseId));
		setTextProperty(dataName);
		add(dataName, c);
	}

	private void createLogdataLabel(LatticeConstraints c) {
		c.x = 13;
		c.y = 0;
		c.width = 2;

		logdataLabel = new JLabel("参照値");
		setTextProperty(logdataLabel);
		add(logdataLabel, c);
	}

	private void createLogdata(LatticeConstraints c) {
		c.x = 15;
		c.y = 0;
		c.width = 3;

		logdata = new JLabel();
		logdata.setHorizontalAlignment(SwingConstants.RIGHT);
		setTextProperty(logdata);
		add(logdata, c);
	}

	private void createMark(LatticeConstraints c) {
		c.x = 18;
		c.y = 0;
		c.width = 2;

		String markStr = graphPropertyModel.getPointMark(seriseId);
		if (markStr == null) {
			markStr = "";
		}
		mark = new JLabel(markStr);
		setTextProperty(mark);
		add(mark, c);
	}

	private void createCurrentDataLabel(LatticeConstraints c) {
		c.x = 13;
		c.y = 1;
		c.width = 2;

		cullentDataLabel = new JLabel("現在値");
		setTextProperty(cullentDataLabel);
		add(cullentDataLabel, c);
	}

	private void createCurrentData(LatticeConstraints c) {
		setCurrentDataConst(c);

		cullentData = graphPropertyModel.getSymbol(seriseId);
		if (cullentData != null) {
			setTextProperty(cullentData);
			add(cullentData, c);
		}
	}

	private void setCurrentDataConst(LatticeConstraints c) {
		c.x = 15;
		c.y = 1;
		c.width = 3;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		resetComponents(evt);
		revalidate();
		repaint();
	}

	private void resetComponents(PropertyChangeEvent evt) {
		colorBar.setBackground(graphPropertyModel.getColors()[seriseId]);
		dataName.setText(graphPropertyModel.getPointName(seriseId));
		mark.setText(graphPropertyModel.getPointMark(seriseId));

		String pName = graphPropertyModel.getDataProviderName(seriseId);

		if (isChangeReference(evt, pName)) {
			DataProvider provider =
				Manager.getInstance().getDataProvider(pName);
			DataHolder holder =
				provider.getDataHolder(graphPropertyModel
					.getDataHolderName(seriseId));

			ConvertValue converter =
				(ConvertValue) holder
					.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
			double ref =
				converter.convertInputValue(graphPropertyModel
					.getReferenceValue(seriseId));

			logdata.setText(converter.convertStringValue(ref));
		} else {
			logdata.setText(null);
		}

		if (cullentData != null) {
			remove(cullentData);
		}

		cullentData = graphPropertyModel.getSymbol(seriseId);
		LatticeConstraints c = new LatticeConstraints();
		initLatticeConst(c);
		setCurrentDataConst(c);

		if (cullentData != null) {
			setTextProperty(cullentData);
			add(cullentData, c);
		}
	}

	private boolean isChangeReference(PropertyChangeEvent evt, String pName) {
		return !GraphPropertyModel.GROUP_CHANGE_EVENT.equals(evt
			.getPropertyName())
			&& pName != null
			&& !"".equals(pName);
	}

	private void setTextProperty(Component c) {
		c.setFont(graphPropertyModel.getExplanatoryFont());
		c.setForeground(graphPropertyModel.getExplanatoryColor());
		if (seriseId == 0) {
			logger.debug("dataName : " + dataName);
		}
	}

	/**
	 * シリーズNoを返します。
	 * 
	 * @return シリーズNoを返します
	 */
	public int getSeriseId() {
		return seriseId;
	}
}
