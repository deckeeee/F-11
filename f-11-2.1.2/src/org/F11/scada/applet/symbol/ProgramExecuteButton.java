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
package org.F11.scada.applet.symbol;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author hori
 */
public class ProgramExecuteButton extends AbstractButtonSymbol implements ActionListener {
	private static final long serialVersionUID = 3075226566674211599L;
	private List params;

	/**
	 * @param text
	 * @param icon
	 */
	public ProgramExecuteButton(SymbolProperty property) {
		super(property);

		params = new ArrayList();
		params.add(property.getProperty("command"));
		this.addActionListener(this);
	}

	public void addExecParam(String param) {
		params.add(param);
	}

	public void actionPerformed(ActionEvent e) {
		String[] execParams = new String[params.size()];
		int i = 0;
		for (Iterator it = params.iterator(); it.hasNext();i++) {
			execParams[i] = (String)it.next();
		}
		try {
			/* Process excel = */ Runtime.getRuntime().exec(execParams);
			//excel.waitFor();
		} catch (IOException ev) {
			ev.printStackTrace();
		//} catch (InterruptedException ev) {
		}
	}
}
