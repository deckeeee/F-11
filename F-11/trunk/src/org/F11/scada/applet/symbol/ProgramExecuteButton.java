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
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 指定されたコマンド文字列を実行するボタンです。
 *
 * @author hori
 */
public class ProgramExecuteButton extends AbstractButtonSymbol implements
		ActionListener {
	private static final long serialVersionUID = 3075226566674211599L;
	private final Logger logger = Logger.getLogger(ProgramExecuteButton.class);
	private final List<String> params;

	/**
	 * 定義文字列を実行コマンドとして設定します。
	 *
	 * @param property プロパティ
	 */
	public ProgramExecuteButton(SymbolProperty property) {
		super(property);
		params = new ArrayList<String>();
		params.add(property.getProperty("command"));
		addActionListener(this);
	}

	public void addExecParam(String param) {
		params.add(param);
	}

	public void actionPerformed(ActionEvent e) {
		String[] execParams = params.toArray(new String[0]);
		try {
			Runtime.getRuntime().exec(execParams);
		} catch (IOException ex) {
			logger.error("プログラム実行時にエラーが発生:", ex);
		}
	}
}
