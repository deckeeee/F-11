/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/parser/dialog/ButtonState.java,v 1.1.6.2 2006/02/16 04:59:00 frdm Exp $
 * $Revision: 1.1.6.2 $
 * $Date: 2006/02/16 04:59:00 $
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

import java.awt.Rectangle;
import java.util.Stack;

import org.F11.scada.applet.dialog.DigitalDialog;
import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * xpath /dialogmap/dialog/button 状態を表すクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ButtonState implements State {
	private static Logger logger;

	/**
	 * Constructor for ButtonState.
	 */
	public ButtonState(String tagName, Attributes atts, DialogState state) {
		logger = Logger.getLogger(getClass().getName());

		if (atts.getValue("text") == null) {
			throw new IllegalArgumentException("text is null");
		}
		if (atts.getValue("x") == null) {
			throw new IllegalArgumentException("x is null");
		}
		if (atts.getValue("y") == null) {
			throw new IllegalArgumentException("y is null");
		}
		if (atts.getValue("width") == null) {
			throw new IllegalArgumentException("width is null");
		}
		if (atts.getValue("height") == null) {
			throw new IllegalArgumentException("height is null");
		}
		if (atts.getValue("action") == null) {
			throw new IllegalArgumentException("action is null");
		}

		String text = atts.getValue("text");
		int x = Integer.parseInt(atts.getValue("x"));
		int y = Integer.parseInt(atts.getValue("y"));
		int width = Integer.parseInt(atts.getValue("width"));
		int height = Integer.parseInt(atts.getValue("height"));
		int action = Integer.parseInt(atts.getValue("action"));

		String foreground = atts.getValue("foreground");
		String background = atts.getValue("background");

		String font = atts.getValue("font");
		String fontStyle = atts.getValue("font_style");
		String fontSize = atts.getValue("font_size");

		String schedule = atts.getValue("schedule");

		if (state.dialog instanceof DigitalDialog) {
			((DigitalDialog) state.dialog).add(
				text,
				action,
				new Rectangle(x, y, width, height),
				foreground,
				background,
				font,
				fontStyle,
				fontSize,
				schedule);
		} else {
			logger.warn("Not DigitalDialog:"
				+ state.dialog.getClass().getName());
		}
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
		stack.pop();
	}

}
