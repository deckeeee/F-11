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

import static org.F11.scada.applet.ngraph.model.GraphModel.GROUP_CHANGE;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

public class SeriesTableModel extends AbstractTableModel implements
		PropertyChangeListener {
	private final Logger logger = Logger.getLogger(SeriesTableModel.class);
	/** 表示の有無を表示している列 */
	public static final int VISIBLE_COLUMN = 0;
	/** 参照値を表示している列 */
	public static final int REFERENCE_VALUE_COLUMN = 5;
	private final String[] titles =
		new String[] { "表示", "色", "ｽﾊﾟﾝ", "記号", "機器名称", "参照値", "現在値", "単位" };
	private final GraphProperties graphProperties;

	public SeriesTableModel(GraphProperties graphProperties) {
		this.graphProperties = graphProperties;
		this.graphProperties.addPropertyChangeListener(GROUP_CHANGE, this);
	}

	public int getColumnCount() {
		return titles.length;
	}

	private boolean isNotEmpty() {
		SeriesGroup seriesGroup = graphProperties.getSeriesGroup();
		return null != seriesGroup.getSeriesProperties()
			&& !seriesGroup.getSeriesProperties().isEmpty();
	}

	public int getRowCount() {
		return isNotEmpty() ? graphProperties
			.getSeriesGroup()
			.getSeriesProperties()
			.size() : 0;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return isNotEmpty() ? getSeriesProperty(rowIndex, columnIndex) != null
			? getSeriesProperty(rowIndex, columnIndex)
			: "" : "";
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		SeriesProperties seriesProperties =
			graphProperties
				.getSeriesGroup()
				.getSeriesProperties()
				.get(rowIndex);
		switch (columnIndex) {
		case VISIBLE_COLUMN:
			boolean b = seriesProperties.isVisible();
			seriesProperties.setVisible(!b);
			fireTableChanged(getTableModelEvent(rowIndex, columnIndex));
			break;
		case REFERENCE_VALUE_COLUMN:
			if (null != value) {
				Number number = (Number) value;
				seriesProperties.setReferenceValue(number.floatValue());
			} else {
				seriesProperties.setReferenceValue(null);
			}
			fireTableChanged(getTableModelEvent(rowIndex, columnIndex));
		default:
			break;
		}
	}

	private TableModelEvent getTableModelEvent(int rowIndex, int columnIndex) {
		return new TableModelEvent(
			this,
			rowIndex,
			rowIndex,
			columnIndex,
			TableModelEvent.UPDATE);
	}

	private Object getSeriesProperty(int rowIndex, int columnIndex) {
		SeriesProperties seriesPropertie = getSeriesProperties(rowIndex);
		switch (columnIndex) {
		case VISIBLE_COLUMN:
			return seriesPropertie.isVisible();
		case 1:
			return seriesPropertie.getColor();
		case 2:
			return "変更";
		case 3:
			return seriesPropertie.getUnit();
		case 4:
			return seriesPropertie.getName();
		case REFERENCE_VALUE_COLUMN:
			return format(seriesPropertie.getVerticalFormat(), seriesPropertie
				.getReferenceValue());
		case 6:
			return format(seriesPropertie.getVerticalFormat(), seriesPropertie
				.getNowValue());
		case 7:
			return seriesPropertie.getUnitMark();
		default:
			throw new IllegalStateException();
		}
	}

	private String format(String format, Float value) {
		return value == null ? null : String.format(format, value);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (isNotEmpty()) {
			Object seriesProperty = getSeriesProperty(0, columnIndex);
			return seriesProperty != null
				? seriesProperty.getClass()
				: String.class;
		} else {
			return null;
		}
	}

	@Override
	public String getColumnName(int column) {
		return titles[column];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0 || columnIndex == 2;
	}

	public SeriesProperties getSeriesProperties(int rowIndex) {
		return graphProperties.getSeriesGroup().getSeriesProperties().get(
			rowIndex);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (GROUP_CHANGE.equals(evt.getPropertyName())) {
			for (int i = 0; i < graphProperties
				.getSeriesGroup()
				.getSeriesProperties()
				.size(); i++) {
				setValueAt(null, i, REFERENCE_VALUE_COLUMN);
			}
		}
	}
}
