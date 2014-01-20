/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/parser/dialog/TenkeyDialogNoTabState.java,v 1.1.2.2 2006/06/08 02:58:42 frdm Exp $
 * $Revision: 1.1.2.2 $
 * $Date: 2006/06/08 02:58:42 $
 * 
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
package org.F11.scada.applet.parser.dialog;

import java.awt.Dialog;
import java.awt.Frame;
import java.util.Stack;

import org.F11.scada.applet.dialog.TenkeyDialogNoTab;
import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * xpath /dialogmap/tenkeydialog 状態を表すクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TenkeyDialogNoTabState implements State {
	DialogMapState state;
	TenkeyDialogNoTab dialog;

	private String dialogName;

	private static Logger logger;

	/**
	 * 状態オブジェクトを生成します。
	 * 
	 * @param tagName タグ名称
	 * @param atts タグ属性
	 * @param 親の状態オブジェクト
	 */
	public TenkeyDialogNoTabState(
			String tagName,
			Attributes atts,
			DialogMapState state) {
		this.state = state;
		logger = Logger.getLogger(getClass().getName());

		if (atts.getValue("name") == null) {
			throw new IllegalArgumentException("name is null");
		}
		if (atts.getValue("width") == null) {
			throw new IllegalArgumentException("width is null");
		}
		if (atts.getValue("height") == null) {
			throw new IllegalArgumentException("height is null");
		}

		dialogName = atts.getValue("name");
		int width = Integer.parseInt(atts.getValue("width"));
		int height = Integer.parseInt(atts.getValue("height"));

		if (state.handler.window instanceof Frame) {
			dialog =
				new TenkeyDialogNoTab(
					(Frame) state.handler.window,
					state.handler.changer);
		} else if (state.handler.window instanceof Dialog) {
			dialog =
				new TenkeyDialogNoTab(
					(Dialog) state.handler.window,
					state.handler.changer);
		}
		if (null != atts.getValue("dialogMin")) {
			dialog.setDialogMin(atts.getValue("dialogMin"));
		}
		if (null != atts.getValue("dialogMax")) {
			dialog.setDialogMax(atts.getValue("dialogMax"));
		}
		dialog.setSize(width, height);
		// dialog.setModal(true);
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
	}

	/*
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		state.handler.dialogs.put(dialogName, dialog);
		stack.pop();
	}

}
