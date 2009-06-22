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

package org.F11.scada.server.goda;

import java.util.ArrayList;

import org.apache.commons.lang.builder.ToStringBuilder;

public class GodaTaskProperty {
	private String watchPath;
	private String fileFormat;
	private int retryTime;
	private int retryWait;
	private String mailServer;
	private String fromAddress;
	private ArrayList toAddresses;
	private String subject;
	private String body;
	private boolean popBeforeSmtp;
	private String popServer;
	private String user;
	private String password;
	private boolean wether;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getMailServer() {
		return mailServer;
	}

	public void setMailServer(String mailServer) {
		this.mailServer = mailServer;
	}

	public int getRetryTime() {
		return retryTime;
	}

	public void setRetryTime(int retryTime) {
		this.retryTime = retryTime;
	}

	public int getRetryWait() {
		return retryWait;
	}

	public void setRetryWait(int retryWait) {
		this.retryWait = retryWait;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public ArrayList getToAddresses() {
		return toAddresses;
	}

	public void setToAddresses(ArrayList toAddresses) {
		this.toAddresses = toAddresses;
	}

	public String getWatchPath() {
		return watchPath;
	}

	public void setWatchPath(String watchPath) {
		this.watchPath = watchPath;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isPopBeforeSmtp() {
		return popBeforeSmtp;
	}

	public void setPopBeforeSmtp(boolean popBeforeSmtp) {
		this.popBeforeSmtp = popBeforeSmtp;
	}

	public String getPopServer() {
		return popServer;
	}

	public void setPopServer(String popServer) {
		this.popServer = popServer;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public boolean isWether() {
		return wether;
	}

	public void setWether(boolean wether) {
		this.wether = wether;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
