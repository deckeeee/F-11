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

package org.F11.scada.server.autoprint.jasper.exportor;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

/**
 * PdfExportorのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PdfExportorTest extends TestCase {

	/**
	 * Constructor for PdfExportorTest.
	 * @param arg0
	 */
	public PdfExportorTest(String arg0) {
		super(arg0);
	}

	public void testCanonicalFile() throws Exception {
		PdfExportor e = new PdfExportor();
		e.setProperty(PdfExportor.PROPERTY_OUT, "daily%yyyyMMdd%.pdf");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		String now = fmt.format(new Date());
		assertEquals("daily" + now + ".pdf", e.getOut());
	}
}
