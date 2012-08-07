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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.F11.scada.server.goda.GodaTaskProperty;
import org.F11.scada.util.ThreadUtil;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.log4j.Logger;

/**
 * ファイル添付をまとめずに1ファイル/1通でメール送信します。
 * 
 * @author maekawa
 *
 */
public class GodaEmailNonMultipartImpl extends AbstractGodaEmailImpl {
	private final Logger logger =
		Logger.getLogger(GodaEmailNonMultipartImpl.class);

	public void send(GodaTaskProperty property) {
		int i = 0;
		for (; i < property.getRetryTime(); i++) {
			try {
				File[] files = search.getFiles(property);
				for (int j = 0; j < files.length; j++) {
					File file = files[j];
					MultiPartEmail email = createMultiPartEmail(property);
					EmailAttachment attachment = new EmailAttachment();
					attachment.setPath(file.getAbsolutePath());
					attachment.setDisposition(EmailAttachment.ATTACHMENT);
					attachment.setDescription(file.getName());
					attachment.setName(file.getName());
					email.attach(attachment);
					email.send();
					search.setLastFile(
						property,
						getLastFileDate(file, property));
				}
				break;
			} catch (Exception e) {
				logger.error("email送信エラー", e);
				ThreadUtil.sleep(property.getRetryWait());
				continue;
			}
		}
	}

	private Date getLastFileDate(File file, GodaTaskProperty property)
			throws ParseException {
		SimpleDateFormat f = new SimpleDateFormat(property.getFileFormat());
		return f.parse(file.getName());
	}
}
