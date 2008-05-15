package org.F11.scada.parser;

import java.awt.Point;
import java.util.Stack;

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
public class LineToState implements State {
	private static Logger logger;

	/**
	 * Constructor for LineToState.
	 */
	public LineToState(String tagName, Attributes atts, java.util.List lines) {
		logger = Logger.getLogger(getClass());

		if (tagName.equals("lineto")) {
			int x = Integer.parseInt(atts.getValue("x"));
			int y = Integer.parseInt(atts.getValue("y"));
			lines.add(new Point(x, y));
			
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/**
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
	}

	/**
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (tagName.equals("lineto")) {
			stack.pop();
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

}
