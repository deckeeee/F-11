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

import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.test.util.TestUtil;

/**
 * FixedDigitalSetterのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class FixedDigitalSetterTest extends TestCase {
    private DataProvider dp;

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(FixedDigitalSetterTest.class);
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
     * Constructor for FixedDigitalSetterTest.
     * @param name
     */
    public FixedDigitalSetterTest(String name) {
        super(name);
    }

    public void testFixedDigitalSetter() throws Exception {
        DataHolder dh = TestUtil.createDigitalHolder("H1");
        dp.addDataHolder(dh);
        assertEquals(WifeDataDigital.valueOfTrue(0), dh.getValue());
        
        FixedDigitalSetter s = new FixedDigitalSetter("P1", "H1", "false");
        s.writeValue(WifeDataDigital.valueOfFalse(0));
        
        assertEquals(WifeDataDigital.valueOfFalse(0), dh.getValue());
    }
}
