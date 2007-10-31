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

package org.F11.scada.applet.symbol;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import junit.framework.TestCase;

/**
 * BasePaneのテストーケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class BasePaneTest extends TestCase {

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(BasePaneTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for BasePaneTest.
     * @param name
     */
    public BasePaneTest(String name) {
        super(name);
    }

    public void testBasePane() throws Exception {
        BasePane p = new BasePane();
        p.setPageName("Test Name");
        assertEquals("Test Name", p.getPageName());

        TestComponent tc = new TestComponent();
        assertFalse(tc.isDisConnect);
        p.addPageSymbol(tc);

        TestComponent tc1 = new TestComponent();
        assertFalse(tc1.isDisConnect);
        JScrollPane sp = new JScrollPane(tc1);
        p.addPageSymbol(sp);

        TestComponent tc2 = new TestComponent();
        assertFalse(tc2.isDisConnect);
        JPanel panel = new JPanel();
        panel.add(tc2);
        JPanel panel2 = new JPanel();
        panel2.add(panel);
        p.addPageSymbol(panel2);

        p.destroyPage();
        assertTrue(tc.isDisConnect);
        assertTrue(tc1.isDisConnect);
        assertTrue(tc2.isDisConnect);
    }

    static class TestComponent extends JLabel implements ReferencerOwnerSymbol {
        private static final long serialVersionUID = 7799987932599092651L;
		boolean isDisConnect;

        public void disConnect() {
            isDisConnect = true;
        }
    }
}
