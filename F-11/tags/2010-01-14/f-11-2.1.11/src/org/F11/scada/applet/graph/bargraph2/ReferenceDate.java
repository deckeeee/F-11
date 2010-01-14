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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class ReferenceDate {
	private Date date;
	private BarGraphModel model;
	private TextDateTimeSymbol textSymbol;

	public ReferenceDate(BarGraphModel model) {
		this.model = model;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
		textSymbol.setDate(this.date);
		textSymbol.updateProperty();
	}

	public void setDateTextSymbol(TextDateTimeSymbol textSymbol) {
		model.getJComponent().add(textSymbol);
		this.textSymbol = textSymbol;
	}

	public void addChangePeriodButtonSymbol(
			final ChangePeriodButtonSymbol button) {
		model.getJComponent().add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.changePeriod(button.getPeriodOffset());
			}
		});
	}

}
