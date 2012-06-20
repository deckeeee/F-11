/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/parser/dialog/DialogMapState.java,v 1.5.2.3 2006/02/16 04:59:00 frdm Exp $
 * $Revision: 1.5.2.3 $
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

import java.util.Stack;

import org.F11.scada.parser.State;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * xpath /dialogmap 状態を表すクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DialogMapState implements State {
	F11DialogHandler handler;
	
	private static Logger logger;

	/**
	 * 状態オブジェクトを生成します。
	 */
	public DialogMapState(F11DialogHandler dialogHandler) {
		this.handler = dialogHandler;
		logger = Logger.getLogger(getClass().getName());
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("dialog")) {
			stack.push(new DialogState(tagName, atts, this));
		} else if (tagName.equals("tenkeydialog")) {
			stack.push(new TenkeyDialogState(tagName, atts, this));
		} else if (tagName.equals("pftenkeydialog")) {
			stack.push(new PfTenkeyDialogState(tagName, atts, this));
		} else if (tagName.equals("graphicscheduleviewdialog")) {
			stack.push(new GraphicScheduleViewDialogState(tagName, atts, this));
		} else if (tagName.equals("udlimitdialog")) {
			stack.push(new UDLimitDialogState(tagName, atts, this));
		} else if (tagName.equals("tenkeydialognotab")) {
			stack.push(new TenkeyDialogNoTabState(tagName, atts, this));
		} else if (tagName.equals("pflelaudlimitdialog")) {
			stack.push(new PfLeLaUDLimitDialogState(tagName, atts, this));
		} else if (tagName.equals("pflaleudlimitdialog")) {
			stack.push(new PfLaLeUDLimitDialogState(tagName, atts, this));
		} else if (tagName.equals("pfletenkeydialog")) {
			stack.push(new PfLeTenkeyDialogState(tagName, atts, this));
		} else if (tagName.equals("pflatenkeydialog")) {
			stack.push(new PfLaTenkeyDialogState(tagName, atts, this));
		} else if (tagName.equals("pointcommentdialog")) {
			stack.push(new PointCommentDialogState(tagName, atts, this));
		} else if (tagName.equals("pinpointdialog")) {
			stack.push(new PinpointDialogState(tagName, atts, this));
		} else {
			logger.debug("tagName:" + tagName);
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
