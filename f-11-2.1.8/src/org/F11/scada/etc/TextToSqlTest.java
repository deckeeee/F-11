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
 */
package org.F11.scada.etc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedHashMap;

import junit.framework.TestCase;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class TextToSqlTest extends TestCase {

    /**
     * Constructor for TextToSqlTest.
     * @param arg0
     */
    public TextToSqlTest(String arg0) {
        super(arg0);
    }

    public void testCreateString() throws Exception {
        TextToSql.SqlOutputInsert o = new TextToSql.SqlOutputInsert();
        assertEquals("a, b, c", o.createString("a\tb\tc"));
    }
    
    public void testSqkOutputUpdate() throws Exception {
        TextToSql.SqlOutputUpdate u = new TextToSql.SqlOutputUpdate();
        assertTrue(Arrays.equals(new String[]{"'a'", "b", "c"}, u.createArray("'a'\tb\tc")));
        LinkedHashMap map = new LinkedHashMap();
        map.put("a", "'a_data1'");
        map.put("b", "100");
        map.put("c", "1.01");
        assertEquals(map, u.createMap(new String[]{"a", "b", "c"}, "'a_data1'\t100\t1.01"));
        assertEquals("a='a_data1', b=100, c=1.01", u.createString(map));
        assertEquals("a='a_data1' AND b=100", u.createWhere(map, new String[]{"a", "b"}));
    }
    
    public void testWrite() throws Exception {
        TextToSql.SqlOutputInsert o = new TextToSql.SqlOutputInsert();
        o.write(new BufferedReader(new FileReader("src/org/F11/scada/etc/TextToSqlTest.txt")));
        TextToSql.SqlOutputUpdate u = new TextToSql.SqlOutputUpdate();
        u.write(new BufferedReader(new FileReader("src/org/F11/scada/etc/TextToSqlTest.txt")));
    }
}
