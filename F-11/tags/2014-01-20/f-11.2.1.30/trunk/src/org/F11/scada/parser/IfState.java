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
package org.F11.scada.parser;

import java.util.Stack;

import jp.gr.javacons.jim.DataReferencerOwner;

import org.F11.scada.applet.symbol.BitRefer;
import org.F11.scada.applet.symbol.CompositeProperty;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/SYMBOL/if 状態を表すクラスです。
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class IfState implements State {
	private static Logger logger;

	private Stack parentStack;
	private DataReferencerOwner referencerOwner;

	/**
	 * 状態を表すオブジェクトを生成します。
	 */
	public IfState(
		String tagName,
		Attributes atts,
		Stack parentStack,
		DataReferencerOwner referencerOwner) {

		logger = Logger.getLogger(getClass());

//		logger.debug(
//			"If : "
//				+ atts.getValue("provider")
//				+ " & "
//				+ atts.getValue("holder"));

//		BitRefer bitRefer =
//			new BitRefer(atts.getValue("provider"), atts.getValue("holder"));
//		bitRefer.connectReferencer(referencerOwner);
		BitRefer bitRefer =
			new BitRefer(atts.getValue("value"), referencerOwner);

		CompositeProperty parent = (CompositeProperty) parentStack.peek();
		parent.addCompositeProperty(bitRefer);
		parentStack.push(bitRefer);

		this.parentStack = parentStack;
		this.referencerOwner = referencerOwner;
	}

	/*
	 * @see orgF11.scada.parser.State#add(String, Attributes)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("if")) {
			stack.push(
				new IfState(tagName, atts, parentStack, referencerOwner));
		} else if (tagName.equals("property")) {
			stack.push(
				new PropertyState(tagName, atts, parentStack, referencerOwner));
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/*
	 * 終了タグイベントの処理
	 */
	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("if")) {
			stack.pop();
			parentStack.pop();
		} else {
			logger.debug("tagName:" + tagName);
		}
	}
}
