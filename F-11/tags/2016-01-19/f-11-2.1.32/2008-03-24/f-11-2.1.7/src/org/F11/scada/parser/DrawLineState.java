package org.F11.scada.parser;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;

import org.F11.scada.applet.symbol.DrawSymbol;
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
public class DrawLineState implements State {
	private static Logger logger;

	private DrawSymbol drawSymbol;
	private java.util.List lines = new ArrayList();

	/**
	 * Constructor for DrawLineStat.
	 */
	public DrawLineState(
		String tagName,
		Attributes atts,
		DrawSymbol drawSymbol) {
		logger = Logger.getLogger(getClass());

		if (tagName.equals("drawline")) {
			//logger.debug("drawline : " + atts.getValue("value"));

			int x = Integer.parseInt(atts.getValue("start_x"));
			int y = Integer.parseInt(atts.getValue("start_y"));
			lines.add(new Point(x, y));
			this.drawSymbol = drawSymbol;
		} else if (
			tagName.equals("drawverticalfork")
				|| tagName.equals("drawhorizontalfork")) {
			//logger.debug("drawverticalfork : " + atts.getValue("value"));

			int x = Integer.parseInt(atts.getValue("center_x"));
			int y = Integer.parseInt(atts.getValue("center_y"));
			lines.add(new Point(x, y));
			this.drawSymbol = drawSymbol;
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/**
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (tagName.equals("lineto")) {
			stack.push(new LineToState(tagName, atts, lines));

		} else {
			logger.debug("tagName:" + tagName);
		}
	}

	/**
	 * @see org.F11.scada.parser.State#end(String, Stack)
	 */
	public void end(String tagName, Stack stack) {
		if (tagName.equals("drawline")) {
			if (1 < lines.size()) {
				Point[] plist = new Point[lines.size()];
				System.arraycopy(lines.toArray(), 0, plist, 0, lines.size());
				drawSymbol.addLine(plist, plist.length);
			}
			stack.pop();
		} else if (tagName.equals("drawverticalfork")) {
			for (int i = 1; i < lines.size(); i++) {
				Point[] plist = new Point[3];
				plist[0] = (Point) lines.get(0);
				plist[2] = (Point) lines.get(i);
				plist[1] = new Point(plist[2].x, plist[0].y);
				drawSymbol.addLine(plist, plist.length);
			}
			stack.pop();
		} else if (tagName.equals("drawhorizontalfork")) {
			for (int i = 1; i < lines.size(); i++) {
				Point[] plist = new Point[3];
				plist[0] = (Point) lines.get(0);
				plist[2] = (Point) lines.get(i);
				plist[1] = new Point(plist[0].x, plist[2].y);
				drawSymbol.addLine(plist, plist.length);
			}
			stack.pop();
		} else {
			logger.debug("tagName:" + tagName);
		}
	}

}
