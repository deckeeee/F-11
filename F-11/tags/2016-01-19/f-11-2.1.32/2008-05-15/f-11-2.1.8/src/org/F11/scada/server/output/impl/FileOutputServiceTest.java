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
package org.F11.scada.server.output.impl;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

import org.F11.scada.server.output.OutputService;
import org.F11.scada.server.output.dto.FileOutputDesc;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class FileOutputServiceTest extends S2TestCase {
    private OutputService outputService;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        include("FileOutputServiceTest.dicon");
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for FileOutputServiceTest.
     * @param arg0
     */
    public FileOutputServiceTest(String arg0) {
        super(arg0);
    }

    public void testFileOutputService() throws Exception {
        outputService.write();
        FileOutputDesc desc = (FileOutputDesc) getComponent("analog1");
        File file = new File(desc.getFilePath());
        file.deleteOnExit();
        assertTrue(file.exists());
        LineNumberReader in = null;
        try {
            in = new LineNumberReader(new FileReader(file));
            assertEquals("100,100", in.readLine());
            assertNull(in.readLine());
        } finally {
            if (in != null) 
                in.close();
        }

        try {
            in = new LineNumberReader(new FileReader(file));
            outputService.write();
            assertEquals("100,100", in.readLine());
            assertNull(in.readLine());
        } finally {
            if (in != null) 
                in.close();
            file.delete();
        }
    }

}
