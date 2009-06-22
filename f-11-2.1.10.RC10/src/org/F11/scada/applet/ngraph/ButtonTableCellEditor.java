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
import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;

import org.F11.scada.util.TableUtil;

/**
 * ボタンを表示してクリックをボタンに伝えるテーブルセルエディタ
 * 
 * @author maekawa
 * 
 */
public class ButtonTableCellEditor extends DefaultCellEditor {
	private static final long serialVersionUID = 3968400001982047137L;
	protected JButton displayButton;
	private String displayLabel;
	private boolean isPushed;
	private JTable table;
	private SeriesTableModel model;
	private Point cellPoint;

	public ButtonTableCellEditor(JCheckBox box) {
		super(box);
		displayButton = new JButton();
		displayButton.setOpaque(true);
		displayButton.setMargin(new Insets(0, 0, 0, 0));
		displayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}
		});
	}

	@Override
	public Component getTableCellEditorComponent(
			JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column) {
		if (isSelected) {
			displayButton.setForeground(table.getSelectionForeground());
			displayButton.setBackground(table.getSelectionBackground());
		} else {
			displayButton.setForeground(table.getForeground());
			displayButton.setBackground(table.getBackground());
		}
		displayLabel = (value == null) ? "" : value.toString();
		displayButton.setText(displayLabel);
		isPushed = true;
		this.table = table;
		model = (SeriesTableModel) table.getModel();
		cellPoint = new Point(column, row);
		return displayButton;
	}

	@Override
	public Object getCellEditorValue() {
		if (isPushed) {
			SpanSetDialog d = new SpanSetDialog();
			d.setVisible(true);
		}
		isPushed = false;
		return new String(displayLabel);
	}

	@Override
	public boolean stopCellEditing() {
		isPushed = false;
		return super.stopCellEditing();
	}

	/**
	 * スパン変更のダイアログ
	 * 
	 * @author maekawa
	 * 
	 */
	private class SpanSetDialog extends JDialog {
		private static final long serialVersionUID = -6379477132804798664L;
		private static final String SPAN_LABEL = "ｽﾊﾟﾝ変更";
		private JSpinner minSpinner;
		private JSpinner maxSpinner;

		public SpanSetDialog() {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setModal(true);
			setTitle(SPAN_LABEL);
			setResizable(false);
			add(getCenter(), BorderLayout.CENTER);
			add(getSouth(), BorderLayout.SOUTH);
			setSize(250, 170);
			setLocation(TableUtil.getDialogPoint(
				table,
				cellPoint.y,
				cellPoint.x));
		}

		private Component getCenter() {
			JPanel mainPanel = new JPanel(new BorderLayout());
			mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			mainPanel.add(getLabel(), BorderLayout.NORTH);
			JPanel panel = new JPanel(new BorderLayout());
			panel.setBorder(BorderFactory.createTitledBorder(SPAN_LABEL));
			Box box = Box.createVerticalBox();
			box.setBorder(BorderFactory.createEmptyBorder(0, 30, 5, 30));
			box.add(getMinSpinner());
			box.add(Box.createVerticalStrut(5));
			box.add(getMaxSpinner());
			panel.add(box, BorderLayout.CENTER);
			mainPanel.add(panel, BorderLayout.CENTER);
			return mainPanel;
		}

		private Component getMinSpinner() {
			Box box = Box.createHorizontalBox();
			minSpinner = new JSpinner();
			SelectedFieldNumberEditor editor =
				new SelectedFieldNumberEditor(minSpinner, "0.0");
			minSpinner.setEditor(editor);
			minSpinner
				.setValue(model.getSeriesProperties(cellPoint.y).getMin());
			box.add(new JLabel("最小値："));
			box.add(minSpinner);
			return box;
		}

		private Component getMaxSpinner() {
			Box box = Box.createHorizontalBox();
			maxSpinner = new JSpinner();
			SelectedFieldNumberEditor editor =
				new SelectedFieldNumberEditor(maxSpinner, "0.0");
			maxSpinner.setEditor(editor);
			maxSpinner
				.setValue(model.getSeriesProperties(cellPoint.y).getMax());
			box.add(new JLabel("最大値："));
			box.add(maxSpinner);
			return box;
		}

		private Component getLabel() {
			Box box = Box.createHorizontalBox();
			box.add(new JLabel("機器名称："));
			JLabel label =
				new JLabel(model.getSeriesProperties(cellPoint.y).getName());
			label.setOpaque(true);
			label.setBackground(model
				.getSeriesProperties(cellPoint.y)
				.getColor());
			box.add(label);
			return box;
		}

		private Component getSouth() {
			Box box = Box.createHorizontalBox();
			box.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
			box.add(Box.createHorizontalGlue());
			box.add(getOkButton());
			box.add(Box.createHorizontalStrut(5));
			box.add(getCancel());
			return box;
		}

		private JButton getOkButton() {
			JButton button = new JButton("OK");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SeriesProperties seriesProperties =
						model.getSeriesProperties(cellPoint.y);
					seriesProperties.setMin(getValue(minSpinner.getValue()));
					seriesProperties.setMax(getValue(maxSpinner.getValue()));
					model.fireTableRowsUpdated(cellPoint.y, cellPoint.y);
					dispose();
				}

				private Float getValue(Object obj) {
					Number num = (Number) obj;
					return num.floatValue();
				}
			});
			return button;
		}

		private JButton getCancel() {
			JButton button = new JButton("CANCEL");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			return button;
		}
	}
}
