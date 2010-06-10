package org.F11.scada.applet.symbol;

import junit.framework.TestCase;

/**
 * @author user
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TextSymbolTest extends TestCase {

	/**
	 * Constructor for TextSymbolTest.
	 * @param arg0
	 */
	public TextSymbolTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testUpdateProperty() {
		SymbolProperty prop = new SymbolProperty();
		prop.setProperty("visible","true");
		prop.setProperty("blink","false");
		prop.setProperty("x","30");
		prop.setProperty("y","20");
		prop.setProperty("h_aligin", "CENTER");
		prop.setProperty("opaque", "false");
		prop.setProperty("foreground", "blue");
		prop.setProperty("background", "red");
		prop.setProperty("font", "SansSerif");
		prop.setProperty("font_style", "PLAIN");
		prop.setProperty("font_size", "20");
		prop.setProperty("value","test test string test");
		prop.setProperty("width", "300");
		prop.setProperty("height", "30");
		TextSymbol fix = new TextSymbol(prop);
		SymbolsDlg_Test dlg = new SymbolsDlg_Test("TextSymbolTest", "•\Ž¦");
		dlg.addSymbol(fix);
		dlg.show();
		if (!dlg.getStat())
			fail();
	}
}
