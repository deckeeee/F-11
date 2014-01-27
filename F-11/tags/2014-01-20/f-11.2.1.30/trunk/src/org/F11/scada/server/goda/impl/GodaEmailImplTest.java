/*
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
 *
 */

package org.F11.scada.server.goda.impl;

import java.util.ArrayList;

import org.F11.scada.server.goda.GodaEmail;
import org.F11.scada.server.goda.GodaTaskProperty;
import org.seasar.extension.unit.S2TestCase;

public class GodaEmailImplTest extends S2TestCase {
	GodaEmail email;

	public GodaEmailImplTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		include("GodaEmailImplTest.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSend() throws Exception {
		GodaTaskProperty property = new GodaTaskProperty();
		property.setWatchPath(System.getProperty("java.io.tmpdir"));
		property.setFileFormat("yyyyMMdd'_C01_01.csv'");
		property.setRetryTime(3);
		property.setRetryWait(10000);
		property.setMailServer("mail.inside.frdm.co.jp");
		ArrayList list = new ArrayList();
		list.add("maekawa@frdm.co.jp");
		property.setToAddresses(list);
		property.setFromAddress("maekawa@frdm.co.jp");
		property.setSubject("GODA FILE");
		property.setBody("添付ファイルだす");
		email.send(property);
	}
}
