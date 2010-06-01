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

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.test.util.TestUtil;

/**
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class VariableAnalogSetterTest extends TestCase {
    private DataProvider dp;

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(VariableAnalogSetterTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        dp = TestUtil.createDP();
        Manager.getInstance().addDataProvider(dp);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        TestUtil.crearJIM();
    }

    /**
     * Constructor for VariableAnalogSetterTest.
     * @param name
     */
    public VariableAnalogSetterTest(String name) {
        super(name);
    }

    public void testVariableAnalogSetter() throws Exception {
        DataHolder dh = TestUtil.createAnalogHolder("H1");
        dp.addDataHolder(dh);
        assertEquals(WifeDataAnalog.valueOfBcdSingle(0), dh.getValue());
        
        VariableAnalogSetter s = new VariableAnalogSetter("P1", "H1");
        s.writeValue("100");
        
        assertEquals(WifeDataAnalog.valueOfBcdSingle(100), dh.getValue());
    }
}
