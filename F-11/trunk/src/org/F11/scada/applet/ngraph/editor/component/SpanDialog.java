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

package org.F11.scada.applet.ngraph.editor.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import org.F11.scada.applet.ngraph.SelectedFieldNumberEditor;
import org.F11.scada.applet.ngraph.editor.UnitData;
import org.F11.scada.util.ComponentUtil;

public class SpanDialog extends JDialog {
	private final UnitData unitData;
	private JSpinner minSpinner;
	private JSpinner maxSpinner;

	public SpanDialog(JDialog dialog, UnitData unitData) {
		super(dialog, unitData.getUnitName(), true);
		this.unitData = unitData;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		add(getCenter(), BorderLayout.CENTER);
		add(getSouth(), BorderLayout.SOUTH);
		setSize(220, 150);
		ComponentUtil.setCenter(JDialog.class, this);
	}

	private Component getCenter() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		JPanel panel2 = new JPanel(new GridBagLayout());
		panel2.setBorder(BorderFactory.createTitledBorder("ｽﾊﾟﾝ入力"));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		panel2.add(new JLabel("最小値："), c);
		setMinSpinner();
		panel2.add(minSpinner, c);
		c.gridy = 1;
		panel2.add(new JLabel("最大値："), c);
		setMaxSpinner();
		panel2.add(maxSpinner, c);
		panel.add(panel2);
		return panel;
	}

	private void setMinSpinner() {
		minSpinner = new JSpinner();
		minSpinner.setPreferredSize(new Dimension(80, 20));
		minSpinner.setMinimumSize(new Dimension(80, 20));
		minSpinner.setMaximumSize(new Dimension(80, 20));
		SelectedFieldNumberEditor editor =
			new SelectedFieldNumberEditor(minSpinner, "0.0");
		minSpinner.setValue(unitData.getMin());
		minSpinner.setEditor(editor);
	}

	private void setMaxSpinner() {
		maxSpinner = new JSpinner();
		SelectedFieldNumberEditor editor =
			new SelectedFieldNumberEditor(maxSpinner, "0.0");
		maxSpinner.setValue(unitData.getMax());
		maxSpinner.setEditor(editor);
	}

	private Component getSouth() {
		Box box = Box.createHorizontalBox();
		box.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		box.add(Box.createHorizontalGlue());
		box.add(getOk());
		box.add(Box.createHorizontalStrut(3));
		box.add(getCancel());
		return box;
	}

	private JButton getOk() {
		JButton button = new JButton("了解");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				unitData.setMin(getValue(minSpinner.getValue()));
				unitData.setMax(getValue(maxSpinner.getValue()));
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
		JButton button = new JButton("取消し");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		return button;
	}
}
