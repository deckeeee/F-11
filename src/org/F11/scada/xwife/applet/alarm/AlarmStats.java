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

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.parser.alarm.AlarmDefine;
import org.F11.scada.parser.alarm.AlarmNewsConfig;
import org.apache.commons.lang.time.FastDateFormat;

public class AlarmStats extends JLabel implements TableModelListener {
	private static final long serialVersionUID = -8893804481224965265L;
	private static final FastDateFormat format = FastDateFormat
			.getInstance("yyyy/MM/dd HH:mm:ss");
	private final AlarmNewsConfig alarmNewsConfig;
	private final boolean isUseNewInfoMode;

	/**
	 * コンストラクタ
	 * 
	 * @param table 最新情報元のテーブル
	 */
	public AlarmStats() {
		super();
		alarmNewsConfig = new AlarmDefine().getAlarmConfig()
				.getAlarmNewsConfig();
		setFont(alarmNewsConfig.getFontConfig().getFont());
		ClientConfiguration configuration = new ClientConfiguration();
		isUseNewInfoMode = configuration.getBoolean(
				"org.F11.scada.xwife.applet.alarm.AlarmStats.isUseNewInfoMode",
				false);
	}

	public void tableChanged(TableModelEvent e) {
		TableModel model = (TableModel) e.getSource();
		if (isSetText(model)) {
			final StringBuffer sb = new StringBuffer();
			sb.append(format.format(model.getValueAt(0, 12)) + "　");
			for (int i = 13; i < model.getColumnCount() - 2; i++) {
				Object value = model.getValueAt(0, i);
				if (null != value) {
					sb.append(value + "　");
				}
			}
			setText(sb.toString());
			setForeground(ColorFactory
					.getColor((String) model.getValueAt(0, 3)));
		}
	}

	private boolean isSetText(TableModel model) {
		if (isUseNewInfoMode) {
			Integer mode = (Integer) model.getValueAt(0, 18);
			Boolean value = (Boolean) model.getValueAt(0, 11);
			return (mode == 1 && !value) || (mode == 2 && value) || mode == 3
					|| mode == 4 || mode == 5 ? true : false;
		} else {
			return true;
		}
	}

	public Color getBackGroundColor() {
		return alarmNewsConfig.getBackGroundColor();
	}
}