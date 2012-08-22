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

package org.F11.scada.xwife.applet.alarm;

import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.DataValueChangeListener;

import org.F11.scada.parser.alarm.AlarmTableConfig;
import org.F11.scada.parser.alarm.FontConfig;
import org.F11.scada.xwife.applet.AbstractWifeApplet;

public class AlarmTable extends JTable implements DataReferencerOwner,
		DataValueChangeListener {

	private static final long serialVersionUID = 4629690279812820658L;
	/** DataHolderタイプ情報です。 */
	private static final Class[][] WIFE_TYPE_INFO = new Class[][] { {
			DataHolder.class, TableModel.class } };
	private DataReferencer referencer;

	/**
	 * コンストラクタ
	 * 
	 * @param dataHolder データホルダー
	 * @param wifeApplet メインアプレットの参照
	 */
	public AlarmTable(
			DataHolder dataHolder,
			AbstractWifeApplet wifeApplet,
			AlarmTableConfig alarmTableConfig) {

		this(dataHolder, wifeApplet, alarmTableConfig, new int[0]);
	}

	public AlarmTable(
			DataHolder dataHolder,
			AbstractWifeApplet wifeApplet,
			AlarmTableConfig alarmTableConfig,
			int[] nonCheckColumn) {
		super((TableModel) dataHolder.getValue());
		new PageJump(this, wifeApplet, nonCheckColumn);
		referencer = new DataReferencer(dataHolder.getDataProvider()
				.getDataProviderName(), dataHolder.getDataHolderName());
		referencer.connect(this);

		FontConfig fc = alarmTableConfig.getFontConfig();

		Font font = fc.getFont();

		setFont(font);
		FontMetrics metrics = getFontMetrics(font);
		int height = metrics.getHeight();
		setRowHeight(height);

		JTableHeader header = getTableHeader();
		header.setFont(font);
	}

	/**
	 * データ変更イベント処理
	 */
	public void dataValueChanged(DataValueChangeEvent evt) {
		Object o = evt.getSource();
		if (!(o instanceof DataHolder)) {
			return;
		}

		DataHolder dh = (DataHolder) o;
		TableModel model = (TableModel) dh.getValue();
		setModel(model);
	}

	/**
	 * DataHolderタイプ情報を返します。
	 */
	public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
		return WIFE_TYPE_INFO;
	}
}