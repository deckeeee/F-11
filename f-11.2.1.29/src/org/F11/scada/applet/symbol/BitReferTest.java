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

import java.util.Date;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;
import jp.gr.javacons.jim.Manager;
import junit.framework.TestCase;

import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.test.util.TestUtil;

/**
 * BitReferのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class BitReferTest extends TestCase implements DataReferencerOwner {
    private static Class[][] OWNERED_CLASS = {
   		{ DataHolder.class, WifeData.class }
    };
    private Manager manager = Manager.getInstance();
    private DataProvider dp;

    public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
        return OWNERED_CLASS;
    }

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(BitReferTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        dp = TestUtil.createDP();
        manager.addDataProvider(dp);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        TestUtil.crearJIM();
    }

    /**
     * Constructor for BitReferTest.
     * @param name
     */
    public BitReferTest(String name) {
        super(name);
    }

    public void testBitRefer() throws Exception {
        DataHolder dh = TestUtil.createDigitalHolder("H1");
        dp.addDataHolder(dh);
        BitRefer bf = new BitRefer("P1_H1", this);
        SymbolProperty tp = createSymbolProperty("value", "true");
        SymbolProperty fp = createSymbolProperty("value", "false");
        
        bf.addCompositeProperty(tp);
        bf.addCompositeProperty(fp);
        
        setValue(dh, WifeDataDigital.valueOfTrue(0));
        assertEquals("true", bf.getProperty("value"));

        setValue(dh, WifeDataDigital.valueOfFalse(0));
        assertEquals("false", bf.getProperty("value"));

        assertEquals(1, bf.dataReferencers.size());
        bf.disconnectReferencer(this);
        assertEquals(0, bf.dataReferencers.size());
    }
    
    public void testBitReferMulti() throws Exception {
        DataHolder dh = TestUtil.createDigitalHolder("H1");
        dp.addDataHolder(dh);
        BitRefer bf = new BitRefer("P1_H1", this);
        SymbolProperty fp = createSymbolProperty("value", "false");

        DataHolder dh2 = TestUtil.createDigitalHolder("H2");
        dp.addDataHolder(dh2);
        BitRefer bf2 = new BitRefer("P1_H2", this);
        SymbolProperty tp2 = createSymbolProperty("value", "true");
        SymbolProperty fp2 = createSymbolProperty("value", "false");
        
        bf2.addCompositeProperty(tp2);
        bf2.addCompositeProperty(fp2);
        bf.addCompositeProperty(bf2);
        bf.addCompositeProperty(fp);
        
        setValue(dh, WifeDataDigital.valueOfTrue(0));
        setValue(dh2, WifeDataDigital.valueOfTrue(0));
        assertEquals("true", bf.getProperty("value"));

        setValue(dh2, WifeDataDigital.valueOfFalse(0));
        assertEquals("false", bf.getProperty("value"));

        setValue(dh, WifeDataDigital.valueOfFalse(0));
        assertEquals("false", bf.getProperty("value"));

        assertEquals(1, bf.dataReferencers.size());
        bf.disconnectReferencer(this);
        assertEquals(0, bf.dataReferencers.size());
    }

    public void testDisConnect() throws Exception {
        DataHolder dh = TestUtil.createDigitalHolder("H1");
        dp.addDataHolder(dh);
        dh = TestUtil.createDigitalHolder("H2");
        dp.addDataHolder(dh);
        dh = TestUtil.createDigitalHolder("H3");
        dp.addDataHolder(dh);
        
        BitRefer br = new BitRefer("P1_H1 && P1_H2 && P1_H3", this);
        assertEquals(3, br.dataReferencers.size());
        br.disconnectReferencer(this);
        assertEquals(0, br.dataReferencers.size());
    }

    /**
     * @param dh
     */
    private void setValue(DataHolder dh, WifeData value) {
        dh.setValue(value, new Date(), WifeQualityFlag.GOOD);
    }

    /**
     * @return
     */
    private SymbolProperty createSymbolProperty(String key, String value) {
        SymbolProperty tp = new SymbolProperty();
        tp.setProperty(key, value);
        return tp;
    }
}
