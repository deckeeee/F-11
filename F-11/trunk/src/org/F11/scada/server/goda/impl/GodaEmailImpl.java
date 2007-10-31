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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.F11.scada.server.goda.GodaEmail;
import org.F11.scada.server.goda.GodaFileSearch;
import org.F11.scada.server.goda.GodaTaskProperty;
import org.F11.scada.util.ThreadUtil;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.log4j.Logger;

public class GodaEmailImpl implements GodaEmail {
	private final Logger logger = Logger.getLogger(GodaEmailImpl.class);
	private GodaFileSearch search;

	public void setSearch(GodaFileSearch search) {
		this.search = search;
	}

	public void send(GodaTaskProperty property) {
		int i = 0;
		for (; i < property.getRetryTime(); i++) {
			try {
				MultiPartEmail email = createMultiPartEmail(property);
				if (setAttach(email, property)) {
					email.send();
					search.setLastFile(property);
				}
				break;
			} catch (EmailException e) {
				logger.error("email送信エラー", e);
				ThreadUtil.sleep(property.getRetryWait());
				continue;
			} catch (IOException e) {
				logger.error("email送信エラー", e);
				ThreadUtil.sleep(property.getRetryWait());
				continue;
			}
		}
		if (i >= property.getRetryTime()) {
			JOptionPane.showMessageDialog(null, "email送信エラー");
		}
	}

	private boolean setAttach(MultiPartEmail email, GodaTaskProperty property)
			throws IOException,
			EmailException {
		File[] files = search.getFiles(property);
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			EmailAttachment attachment = new EmailAttachment();
			attachment.setPath(file.getAbsolutePath());
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription(file.getName());
			attachment.setName(file.getName());
			email.attach(attachment);
		}
		return files.length > 0;
	}

	private MultiPartEmail createMultiPartEmail(GodaTaskProperty property)
			throws EmailException {
		MultiPartEmail email = new MultiPartEmail();
		email.setCharset("ISO-2022-JP");
		email.addHeader("Content-Transfer-Encoding", "7bit");
		email.setHostName(property.getMailServer());
		for (Iterator i = property.getToAddresses().iterator(); i.hasNext();) {
			String address = (String) i.next();
			email.addTo(address);
		}
		email.setFrom(property.getFromAddress());
		email.setSubject(property.getSubject());
		email.setMsg(property.getBody());
		if (property.isPopBeforeSmtp()) {
			email.setPopBeforeSmtp(
					property.isWether(),
					property.getPopServer(),
					property.getUser(),
					property.getPassword());
		}
		return email;
	}
}
