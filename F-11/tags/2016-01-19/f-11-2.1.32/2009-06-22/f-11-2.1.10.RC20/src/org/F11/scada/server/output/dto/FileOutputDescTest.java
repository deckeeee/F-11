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
package org.F11.scada.server.output.dto;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class FileOutputDescTest extends TestCase {

    /**
     * Constructor for FileOutputDescTest.
     * @param arg0
     */
    public FileOutputDescTest(String arg0) {
        super(arg0);
    }

    public void testFileOutputDesc() {
        FileOutputDesc fod = new FileOutputDesc();
        fod.addHolder("H1");
        List datas = fod.getHolders();
        for (Iterator i = datas.iterator(); i.hasNext();) {
            String h = (String) i.next();
            assertSame("H1", h);
        }

        try {
            datas.add(new Object());
            fail();
        } catch (UnsupportedOperationException e) {}
    }

}
