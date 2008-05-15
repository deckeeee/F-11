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

package org.F11.scada.xwife.applet;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.DataValueChangeListener;

import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.parser.alarm.AlarmNewsConfig;
import org.F11.scada.xwife.applet.alarm.PriorityController;
import org.apache.commons.lang.time.FastDateFormat;

class AlarmNewLines extends Box implements TableModelListener,
		DataReferencerOwner, DataValueChangeListener {
	private static final long serialVersionUID = -4880754153203969009L;
	// private static Logger logger = Logger.getLogger(AlarmNewLines.class);
	private static final FastDateFormat format = FastDateFormat
			.getInstance("yyyy/MM/dd HH:mm:ss");
	private final AlarmNewsConfig alarmNewsConfig;
	private final JPanel[] panels;

	private DataReferencer referencer;

	/** DataHolderタイプ情報です。 */
	private static final Class[][] WIFE_TYPE_INFO = new Class[][] { {
			DataHolder.class, TableModel.class } };

	/**
	 * コンストラクタ
	 * 
	 * @param table 最新情報元のテーブル
	 */
	public AlarmNewLines(
			DataHolder dataHolder,
			AbstractWifeApplet wifeApplet,
			AlarmNewsConfig alarmNewsConfig) {
		super(BoxLayout.Y_AXIS);
		this.alarmNewsConfig = alarmNewsConfig;

		final TableModel model = (TableModel) dataHolder.getValue();

		int count = alarmNewsConfig.getLineCountConfig().getValue();
		panels = new JPanel[count];
		for (int i = 0; i < count; i++) {
			panels[i] = new JPanel(new GridLayout());
			JLabel label = new JLabel("");
			label.setFont(alarmNewsConfig.getFontConfig().getFont());
			label.addMouseListener(new PageJumpNewLines(model, wifeApplet, i));
			panels[i].add(label, 0);
			panels[i].setBorder(BorderFactory
					.createBevelBorder(BevelBorder.LOWERED));
			panels[i].setBackground(alarmNewsConfig.getBackGroundColor());
			panels[i].setOpaque(true);
			this.add(panels[i]);
		}

		model.addTableModelListener(this);
		updateNewLines(model);
		model.addTableModelListener(new PriorityController(wifeApplet));

		referencer = new DataReferencer(dataHolder.getDataProvider()
				.getDataProviderName(), dataHolder.getDataHolderName());
		referencer.connect(this);
	}

	/**
	 * 最新警報欄の更新
	 * 
	 * @param model
	 */
	private void updateNewLines(final TableModel model) {
		for (int i = 0; i < panels.length && i < model.getRowCount(); i++) {
			final JLabel label = (JLabel) panels[i].getComponent(0);
			if (i < model.getRowCount()) {
				final StringBuffer sb = new StringBuffer();
				sb.append(format.format(model.getValueAt(i, 12)) + "　");
				for (int j = 13; j < model.getColumnCount() - 1; j++) {
					sb.append(model.getValueAt(i, j) + "　");
				}
				final String colorStr = (String) model.getValueAt(i, 3);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						label.setText(sb.toString());
						label.setForeground(ColorFactory.getColor(colorStr));
					};
				});
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						label.setText(" ");
					};
				});
			}
		}
	}

	public Color getBackGroundColor() {
		return alarmNewsConfig.getBackGroundColor();
	}

	/**
	 * データ変更イベント処理
	 */
	public void dataValueChanged(DataValueChangeEvent evt) {
	}

	/**
	 * DataHolderタイプ情報を返します。
	 */
	public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
		return WIFE_TYPE_INFO;
	}

	public void tableChanged(TableModelEvent e) {
		updateNewLines((TableModel) e.getSource());
	}
}