package org.F11.scada.applet.symbol;

import java.awt.Point;

import junit.framework.TestCase;

/**
 * @author user
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DrawSymbolTest extends TestCase {

	/**
	 * Constructor for DrawSymbolTest.
	 * @param arg0
	 */
	public DrawSymbolTest(String arg0) {
		super(arg0);
	}

	public void testUpdateProperty() {
		SymbolProperty prop = new SymbolProperty();
		prop.setProperty("visible","true");
		prop.setProperty("blink","false");
		prop.setProperty("line_width","5");
		prop.setProperty("line_cap","SQUARE");
		prop.setProperty("foreground","red");
		DrawSymbol fix = new DrawSymbol(prop);
		
		Point[] pointlist = new Point[10];
		pointlist[0] = new Point(10,10);
		pointlist[1] = new Point(200,50);
		pointlist[2] = new Point(150,130);
		pointlist[3] = new Point(200,200);
		fix.addLine(pointlist, 4);
		pointlist[0] = new Point(200,10);
		pointlist[1] = new Point(100,50);
		fix.addLine(pointlist, 2);
		SymbolsDlg_Test dlg = new SymbolsDlg_Test("DrawSymbolTest", "•\Ž¦");
		dlg.addSymbol(fix);
		dlg.show();
		if (!dlg.getStat())
			fail();
	}

}
