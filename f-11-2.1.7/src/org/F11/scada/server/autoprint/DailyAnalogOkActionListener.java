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

package org.F11.scada.server.autoprint;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

class DailyAnalogOkActionListener  implements ParamDialogListener {
	NippoParamDialog dlg;

	DailyAnalogOkActionListener() {
	}

	public void setDlg(NippoParamDialog dlg) {
		this.dlg = dlg;
	}

	public void actionPerformed(ActionEvent evt) {
		boolean autoOn = false;
		if (dlg.getOnButton().isSelected())
			autoOn = true;
		int hh = 0;
		int mm = 0;
		try {
			hh = Integer.parseInt(dlg.getHourField().getText());
			mm = Integer.parseInt(dlg.getMinuteField().getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(
				null,
				"数値を入力してください。",
				"エラー",
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (hh < 0 || 23 < hh || mm < 0 || 59 < mm) {
			JOptionPane.showMessageDialog(
				null,
				"数値が範囲内にありません。",
				"エラー",
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		dlg.setParam(new AutoPrintSchedule.DailyAnalog(autoOn, hh, mm));
		dlg.dispose();
	}
}