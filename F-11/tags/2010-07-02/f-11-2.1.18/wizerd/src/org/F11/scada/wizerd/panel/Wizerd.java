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

package org.F11.scada.wizerd.panel;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.F11.scada.wizerd.Main;
import org.jdesktop.application.Task;

/**
 * 各ウィザードの規定クラス
 * 
 * @author maekawa
 * 
 */
public abstract class Wizerd extends JPanel {
	protected boolean isNext;

	public Wizerd(LayoutManager layout) {
		super(layout);
	}

	/**
	 * 初期処理
	 */
	public abstract void init();

	/**
	 * 実行ボタンの処理。必ずTaskオブジェクトを返し、バックグラウンドスレッドで実行します。
	 * 
	 * @param main
	 * @return Taskオブジェクト
	 */
	public abstract Task<Void, Void> execute(Main main);

	/**
	 * 基本(物件)ディレクトリを返します。
	 * 
	 * @return 基本(物件)ディレクトリを返します。
	 */
	public abstract String getBaseDirectory();
}
