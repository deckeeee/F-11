/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/alarm/FontConfigTest.java,v 1.1 2003/10/23 09:58:34 frdm Exp $
 * $Revision: 1.1 $
 * $Date: 2003/10/23 09:58:34 $
 * 
 * =============================================================================
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

package org.F11.scada.parser.alarm;

import java.awt.Font;

import junit.framework.TestCase;

/**
 * FontConfigのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class FontConfigTest extends TestCase {

	/**
	 * Constructor for FontConfigTest.
	 * @param arg0
	 */
	public FontConfigTest(String arg0) {
		super(arg0);
	}

	public void testGetFontStyle() throws Exception {
		FontConfig fc = new FontConfig();
		fc.setType("Serif");
		fc.setPoint(48);
		fc.setStyle("plain");

		assertEquals(new Font("Serif", Font.PLAIN, 48), fc.getFont());
	}
}
