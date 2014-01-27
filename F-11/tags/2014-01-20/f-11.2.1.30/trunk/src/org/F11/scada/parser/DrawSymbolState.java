package org.F11.scada.parser;

import java.util.Stack;

import jp.gr.javacons.jim.DataReferencerOwner;

import org.F11.scada.applet.symbol.DrawSymbol;
import org.F11.scada.applet.symbol.SymbolProperty;
import org.F11.scada.parser.Util.DisplayState;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * @author user
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DrawSymbolState implements State {
	private static Logger logger;

	private Stack parentStack;
	private DataReferencerOwner referencerOwner;
	private DrawSymbol drawSymbol;

	/**
	 * Constructor for DrawSymbolStat.
	 */
	public DrawSymbolState(
		String tagName,
		Attributes atts,
		PageState pageState) {
		logger = Logger.getLogger(getClass());

		if (tagName.equals("drawsymbol")) {
			//			logger.debug("ImageSymbol : " + atts.getValue("value"));

			SymbolProperty property = new SymbolProperty(atts);
			drawSymbol = new DrawSymbol(property);
			drawSymbol.updateProperty();

			pageState.basePage.addPageSymbol(drawSymbol);

			parentStack = new Stack();
			parentStack.push(drawSymbol);
			referencerOwner = drawSymbol;
		} else {
			logger.debug("tagName:" + tagName);
		}

	}

	/**
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Push : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("if")) {
			stack.push(
				new IfState(tagName, atts, parentStack, referencerOwner));

		} else if (
			tagName.equals("drawline")
				|| tagName.equals("drawverticalfork")
				|| tagName.equals("drawhorizontalfork")) {
			stack.push(new DrawLineState(tagName, atts, drawSymbol));

		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/**
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (logger.isDebugEnabled()) {
			logger.debug("Pop : " + DisplayState.toString(tagName, stack));
		}
		if (tagName.equals("drawsymbol")) {
			drawSymbol.updateProperty();
			stack.pop();
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

}
