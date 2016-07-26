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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * @author hori
 */
public class NenpoParamDialog extends JDialog {
	private static final long serialVersionUID = 5381286915187754493L;

	private static final String dialogTitle = "�N�񎩓��󎚐ݒ�";

	private JRadioButton OnButton;
	private JRadioButton OffButton;
	private JTextField MonthField;
	private JTextField DayField;
	private JTextField HourField;
	private JTextField MinuteField;

	private AutoPrintSchedule param;

	/**
	 * @param owner
	 * @throws java.awt.HeadlessException
	 */
	public NenpoParamDialog(Frame owner, AutoPrintSchedule param) {
		super(owner, dialogTitle, true);
		this.param = param;
		init();
		setLocationCentor();
		show();
	}

	private void init() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		Container cont = getContentPane();

		cont.setLayout(new GridLayout(0, 2));
		cont.add(new JLabel("������ : "));
		ButtonGroup group = new ButtonGroup();
		JPanel panel = new JPanel();
		OnButton = new JRadioButton("����");
		OffButton = new JRadioButton("���Ȃ�");
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
		cont.add(new JLabel("���� : "));
		panel = new JPanel();
		String no = String.valueOf(cal.get(Calendar.MONTH) + 1);
		if (no.length() < 2)
			no = "0" + no;
		MonthField = new JTextField(no);
		no = String.valueOf(cal.get(Calendar.DATE));
		if (no.length() < 2)
			no = "0" + no;
		DayField = new JTextField(no);
		no = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
		if (no.length() < 2)
			no = "0" + no;
		HourField = new JTextField(no);
		no = String.valueOf(cal.get(Calendar.MINUTE));
		if (no.length() < 2)
			no = "0" + no;
		MinuteField = new JTextField(no);
		panel.add(MonthField);
		panel.add(new JLabel("��"));
		panel.add(DayField);
		panel.add(new JLabel("��"));
		panel.add(HourField);
		panel.add(new JLabel("��"));
		panel.add(MinuteField);
		panel.add(new JLabel("��"));
		cont.add(panel);

		JButton ok = new JButton("OK");
		ok.addActionListener(new OkActionListener(this));
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

	/**
	 * <p>OK �{�^�����������ꂽ���Ɏ��s����郊�X�i�[�N���X�ł��B
	 * <p>�F�؃��W�b�N�� checkAuthentication ���\�b�h�ɁA���[�U�[���ƃp�X���[�h��
	 * �����œn���܂��B
	 */
	private class OkActionListener implements ActionListener {
		NenpoParamDialog dlg;

		OkActionListener(NenpoParamDialog dlg) {
			this.dlg = dlg;
		}

		public void actionPerformed(ActionEvent evt) {
			boolean autoOn = false;
			if (OnButton.isSelected())
				autoOn = true;
			int month = 1;
			int dd = 1;
			int hh = 0;
			int mm = 0;
			try {
				month = Integer.parseInt(MonthField.getText());
				dd = Integer.parseInt(DayField.getText());
				hh = Integer.parseInt(HourField.getText());
				mm = Integer.parseInt(MinuteField.getText());
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(
					null,
					"���l����͂��Ă��������B",
					"�G���[",
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (month < 1
				|| 12 < month
				|| dd < 1
				|| 31 < dd
				|| hh < 0
				|| 23 < hh
				|| mm < 0
				|| 59 < mm) {
				JOptionPane.showMessageDialog(
					null,
					"���l���͈͓��ɂ���܂���B",
					"�G���[",
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			dlg.param = new AutoPrintSchedule.Yearly(autoOn, month, dd, hh, mm);
			dlg.dispose();
		}
	}

	private class CancelListener implements ActionListener {
		NenpoParamDialog dlg;

		CancelListener(NenpoParamDialog dlg) {
			this.dlg = dlg;
		}

		public void actionPerformed(ActionEvent evt) {
			dlg.dispose();
		}
	}

	/**
	* �e�X�g�p���C��
	*/
	public static void main(String args[]) {
		AutoPrintSchedule param = new AutoPrintSchedule.Yearly(true, 1, 1, 8, 40);
		new NenpoParamDialog((Frame) null, param);
	}

}
