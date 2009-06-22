/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2007 Freedom, Inc. All Rights Reserved.
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

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

import org.F11.scada.server.alarm.mail.SmtpEnvironments;
import org.apache.log4j.Logger;

public class SmtpAuthTransportCore implements TransportCore {
	private final Logger logger = Logger.getLogger(SmtpAuthTransportCore.class);
	private final SmtpEnvironments environments;
	private final Session session;

	public SmtpAuthTransportCore(Session session, SmtpEnvironments environments) {
		this.session = session;
		this.environments = environments;
	}

	public void sendMessage(Message msg, Address[] addresses)
			throws MessagingException {
		logger.info("TransportCore=" + SmtpAuthTransportCore.class.getName());
		Transport transport = session.getTransport("smtp");
		transport.connect(environments.getServername(), environments
				.getServerPort(), environments.getUser(), environments
				.getPassword());
		transport.sendMessage(msg, addresses);
	}
}
