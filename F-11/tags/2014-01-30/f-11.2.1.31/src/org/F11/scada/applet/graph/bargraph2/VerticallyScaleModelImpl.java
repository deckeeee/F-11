/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.applet.graph.bargraph2;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.dialog.SelectedFieldNumberEditor;
import org.F11.scada.xwife.applet.alarm.LimitedAction;
import org.apache.log4j.Logger;

public class VerticallyScaleModelImpl implements VerticallyScaleModel {
	/** ロギングAPI */
	private static Logger logger = Logger
		.getLogger(VerticallyScaleModelImpl.class);
	private Container parent;
	private List<ChangePeriodButtonSymbol> buttonList =
		new ArrayList<ChangePeriodButtonSymbol>();
	private List<TextVerticalSymbol> textList =
		new ArrayList<TextVerticalSymbol>();
	private List<VerticallyScaleChangeListener> listeners =
		new ArrayList<VerticallyScaleChangeListener>();

	private PointProperty property;
	private String pattern;
	private boolean limiton;
	private double min;
	private double max;

	public void changePoint(PointProperty prop, String pattern) {
		this.property = prop;
		this.pattern = pattern;

		fireScaleChange();
	}

	public void addScaleChangeButtonSymbol(ChangePeriodButtonSymbol symbol) {
		symbol.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showDlg(e);
			}
		});
		buttonList.add(symbol);
	}

	public void addTextVerticalSymbol(TextVerticalSymbol symbol) {
		textList.add(symbol);
	}

	public void setParent(Container parent) {
		this.parent = parent;
		for (Component comp : buttonList)
			parent.add(comp);
		for (Component comp : textList)
			parent.add(comp);
	}

	public void addScaleChangeListener(VerticallyScaleChangeListener l) {
		listeners.add(l);
	}

	private void fireScaleChange() {
		for (TextVerticalSymbol symbol : textList) {
			symbol.setPattern(
				pattern,
				property.getMinimums(),
				property.getMaximums());
			symbol.updateProperty();
		}
		for (VerticallyScaleChangeListener l : listeners) {
			l.changeScale(property.getMinimums(), property.getMaximums());
		}
	}

	private void showDlg(ActionEvent e) {

		Frame frame = WifeUtilities.getParentFrame(parent);
		JDialog dialog = new ScaleChangeDialog(frame, this);

		JComponent comp = (JComponent) e.getSource();
		Rectangle size = comp.getBounds();
		Point p = comp.getLocationOnScreen();
		dialog.setLocation(new Point(p.x, p.y - size.height));
		Rectangle bounds = dialog.getBounds();
		bounds.setLocation(p);
		dialog.setLocation(WifeUtilities.getInScreenPoint(frame
			.getToolkit()
			.getScreenSize(), bounds));
		dialog.setVisible(true);
	}

	public void setMin(double min) {
		this.min = min;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public void setLimiton(boolean limiton) {
		this.limiton = limiton;
	}

	/**
	 * スケール変更の入力ダイアログです。
	 */
	private static class ScaleChangeDialog extends JDialog {
		private static final long serialVersionUID = 7172111920974254348L;
		private VerticallyScaleModelImpl scalemodel;
		private JButton okButton;
		private JSpinner maxSpinner;
		private JSpinner minSpinner;
		private JButton cancelButton;

		ScaleChangeDialog(Frame frame, VerticallyScaleModelImpl model) {
			super(frame, true);
			this.scalemodel = model;
			init();
			pack();
		}

		private void init() {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			JPanel mainPanel = new JPanel(new GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(10, 10, 10, 10);
			c.gridx = 0;
			c.gridy = 0;
			JLabel maxLabel = new JLabel("最大");
			mainPanel.add(maxLabel, c);

			c.gridy = 1;
			JLabel minLabel = new JLabel("最小");
			mainPanel.add(minLabel, c);

			c.gridy = 2;
			okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					push_ok();
				}
			});
			mainPanel.add(okButton, c);

			c.gridx = 1;
			c.gridy = 1;
			setMinSpinner(mainPanel, c);

			c.gridy = 0;
			setMaxSpinner(mainPanel, c);

			c.gridy = 2;
			cancelButton = new JButton("CANCEL");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					push_cancel();
				}
			});
			mainPanel.add(cancelButton, c);
			getContentPane().add(mainPanel);
		}

		private void setMaxSpinner(JPanel mainPanel, GridBagConstraints c) {
			if (scalemodel.limiton) {
				SpinnerNumberModel maxModel =
					new SpinnerNumberModel(
						scalemodel.property.getMaximums(),
						scalemodel.min,
						scalemodel.max,
						1);
				maxSpinner = new JSpinner(maxModel);
			} else {
				SpinnerNumberModel maxModel =
					new SpinnerNumberModel(
						scalemodel.property.getMaximums(),
						Long.MIN_VALUE,
						Long.MAX_VALUE,
						1);
				maxSpinner = new JSpinner(maxModel);
			}
			setEditor(maxSpinner);

			Dimension d = maxSpinner.getPreferredSize();
			d.width = 80;
			maxSpinner.setPreferredSize(d);
			mainPanel.add(maxSpinner, c);
		}

		private void setEditor(JSpinner spinner) {
			SelectedFieldNumberEditor editer =
				new SelectedFieldNumberEditor(spinner);
			JFormattedTextField text = editer.getTextField();
			text.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					if (e.getSource() instanceof JTextComponent) {
						final JTextComponent textComp =
							((JTextComponent) e.getSource());
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								textComp.selectAll();
							}
						});
					}
				}
			});
			spinner.setEditor(editer);
		}

		private void setMinSpinner(JPanel mainPanel, GridBagConstraints c) {
			if (scalemodel.limiton) {
				SpinnerNumberModel minModel =
					new SpinnerNumberModel(
						scalemodel.property.getMinimums(),
						scalemodel.min,
						scalemodel.max,
						1);
				minSpinner = new JSpinner(minModel);
			} else {
				SpinnerNumberModel minModel =
					new SpinnerNumberModel(
						scalemodel.property.getMinimums(),
						Long.MIN_VALUE,
						Long.MAX_VALUE,
						1);
				minSpinner = new JSpinner(minModel);
			}
			setEditor(minSpinner);

			Dimension d = minSpinner.getPreferredSize();
			d.width = 80;
			minSpinner.setPreferredSize(d);
			mainPanel.add(minSpinner, c);
		}

		// OK
		private void push_ok() {
			logger.debug("push_ok -> fireScaleChange()");
			double max = getSpinnerValue(maxSpinner);
			double min = getSpinnerValue(minSpinner);
			scalemodel.property.setMaximums(max);
			scalemodel.property.setMinimums(min);
			scalemodel.fireScaleChange();
			dispose();
		}

		private double getSpinnerValue(JSpinner spinner) {
			Number number = (Number) spinner.getValue();
			return number.doubleValue();
		}

		// Cancel
		private void push_cancel() {
			dispose();
		}
	}

}
