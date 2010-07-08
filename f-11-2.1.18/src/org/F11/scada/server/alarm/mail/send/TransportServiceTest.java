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

package org.F11.scada.server.alarm.mail.send;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.test.util.TestUtil;
import org.seasar.extension.unit.S2TestCase;

public class TransportServiceTest extends S2TestCase {
	TransportService service;

	public TransportServiceTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		include("TransportServiceTest.dicon");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'org.F11.scada.server.alarm.mail.send.TransportCore.send(MimeMessage)'
	 */
	public void testSendError() throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.host", "mail.example.co.jp");
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage msg = new MimeMessage(session);
		service.send(msg, Collections.EMPTY_LIST, new DataValueChangeEventKey(
				0, "", "", Boolean.TRUE, new Timestamp(System
						.currentTimeMillis())));
		TestUtil.sleep(10000L);
	}
}
