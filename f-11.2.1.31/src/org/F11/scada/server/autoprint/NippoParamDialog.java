/*
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
package org.F11.scada.server.autoprint;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * @author hori
 */
public class NippoParamDialog extends JDialog {
	private static final long serialVersionUID = -8282186174518608103L;

	private static final String dialogTitle = "ì˙ïÒé©ìÆàÛéöê›íË";

	private JRadioButton OnButton;
	private JRadioButton OffButton;
	private JTextField HourField;
	private JTextField MinuteField;

	private AutoPrintSchedule param;

	/**
	 * @param owner
	 * @throws java.awt.HeadlessException
	 */
	public NippoParamDialog(Frame owner, AutoPrintSchedule param, ParamDialogListener listener) {
		super(owner, dialogTitle, true);
		this.param = param;
		listener.setDlg(this);
		init(listener);
		setLocationCentor();
		show();
	}

	private void init(ActionListener listener) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		Container cont = getContentPane();

		cont.setLayout(new GridLayout(0, 2));
		cont.add(new JLabel("é©ìÆàÛéö : "));
		ButtonGroup group = new ButtonGroup();
		JPanel panel = new JPanel();
		OnButton = new JRadioButton("Ç∑ÇÈ");
		OffButton = new JRadioButton("ÇµÇ»Ç¢");
		panel.add(OnButton);
		panel.add(OffButton);
		group.add(OnButton);
		group.add(OffButton);
		if (param.isAutoOn())
			OnButton.setSelected(true);
		else
			OffButton.setSelected(true);
		cont.add(panel);

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(param.getTimestamp().getTime());
		cont.add(new JLabel("ì˙éû : "));
		panel = new JPanel();
		String no = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
		if (no.length() < 2)
			no = "0" + no;
		HourField = new JTextField(no);
		no = String.valueOf(cal.get(Calendar.MINUTE));
		if (no.length() < 2)
			no = "0" + no;
		MinuteField = new JTextField(no);
		panel.add(HourField);
		panel.add(new JLabel("éû"));
		panel.add(MinuteField);
		panel.add(new JLabel("ï™"));
		cont.add(panel);

		JButton ok = new JButton("OK");
		ok.addActionListener(listener);
		cont.add(ok);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new CancelListener(this));
		cont.add(cancel);
	}

	private void setLocationCentor() {
		pack();
		Dimension parentDim = getParent().getSize();
		Dimension screenSize = getToolkit().getScreenSize();
		Point location = getParent().getLocation();
		location.translate(
			(parentDim.width - getWidth()) / 2,
			(parentDim.height - getHeight()) / 2);
		location.x = Math.max(0, Math.min(location.x, screenSize.width - getSize().width));
		location.y = Math.max(0, Math.min(location.y, screenSize.height - getSize().height));
		setLocation(location.x, location.y);
	}

	public AutoPrintSchedule getParam() {
		return param;
	}

	private class CancelListener implements ActionListener {
		NippoParamDialog dlg;

		CancelListener(NippoParamDialog dlg) {
			this.dlg = dlg;
		}

		public void actionPerformed(ActionEvent evt) {
			dlg.dispose();
		}
	}

	public JRadioButton getOnButton() {
		return OnButton;
	}

	public void setParam(AutoPrintSchedule param) {
		this.param = param;
	}

	public JTextField getHourField() {
		return HourField;
	}

	public JTextField getMinuteField() {
		return MinuteField;
	}

}
