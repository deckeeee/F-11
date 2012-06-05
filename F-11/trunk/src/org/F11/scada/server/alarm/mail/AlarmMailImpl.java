/*
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2003 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.server.alarm.mail;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.alarm.mail.send.TransportService;
import org.apache.log4j.Logger;

/**
 * @author hori
 */
public class AlarmMailImpl implements AlarmMail {
	private final Logger logger = Logger.getLogger(AlarmMailImpl.class);
	private SmtpEnvironments environments;
	private TransportService transportService;

	/**
	 * @throws IOException
	 *
	 */
	public AlarmMailImpl() throws IOException {
		environments = new SmtpEnvironments();
	}

	public void setTransportService(TransportService transportService) {
		this.transportService = transportService;
	}

	public void send(ResultSet rs, DataValueChangeEventKey key) {
		if (environments.getServername().length() <= 0) {
			return;
		}

		boolean value = key.getValue().booleanValue();
		Collection<String> addresses = Collections.emptyList();
		MailUtil mailUtil = new MailUtil();
		try {
			int mode = rs.getInt("email_send_mode");
			if (mode == 0 || (mode == 1 && !value) || (mode == 2 && value))
				return;

			String holder = rs.getString("holder");
			addresses = mailUtil.getSendAddresses(
					rs.getInt("email_group_id"),
					rs.getString("provider"),
					holder);
			if (addresses.size() <= 0) {
				return;
			}

			if (mailUtil.isDisable(holder)) {
				return;
			}
		} catch (Exception e) {
			logger.error("メール送信処理 (DB読み取りエラー)", e);
		}

		// create some properties and get the default Session
		Properties props = new Properties();
		props.put("mail.smtp.host", environments.getServername());
		if (logger.isDebugEnabled())
			props.put("mail.debug", "true");

		Session session = Session.getDefaultInstance(props);
		session.setDebug(logger.isDebugEnabled());

		try {
			// create a message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(environments.getFrom()));
			InternetAddress[] address = new InternetAddress[addresses.size()];
			Iterator<String> it = addresses.iterator();
			for (int i = 0; it.hasNext(); i++) {
				address[i] = new InternetAddress(it.next());
			}
			mailUtil.setRecipients(msg, address);
			msg.setSubject(environments.getSubject(), "iso-2022-jp");

			if (mailUtil.isDateMode()) {
				msg.setSentDate(key.getTimeStamp());
			} else {
				msg.setSentDate(new Date());
			}
			// If the desired charset is known, you can use
			// setText(text, charset)
			StringBuffer sb = new StringBuffer();
			try {
				sb.append(rs.getString("unit"));
				sb.append(" ");
				sb.append(rs.getString("kikiname"));
				sb.append(" ");
				sb.append(rs.getString("attname"));
				sb.append(" ");
				sb.append(rs.getString("message"));
			} catch (SQLException e) {
				logger.error("メール送信処理 (DB読み取りエラー)", e);
				return;
			}

			sb.append(MailUtil.MAIL_CR);
			mailUtil.setPointComment(sb, key);
			msg.setText(sb.toString(), "iso-2022-jp");

			transportService.send(msg, addresses, key);
		} catch (MessagingException mex) {
			logger.error("メール送信処理 (メールサーバーエラー)", mex);
			mailUtil.printMessagingException(mex);
		}
	}
}
