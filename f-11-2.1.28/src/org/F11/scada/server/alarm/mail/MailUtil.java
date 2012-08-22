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

package org.F11.scada.server.alarm.mail;

import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Globals;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.comment.PointCommentDto;
import org.F11.scada.server.comment.PointCommentService;
import org.F11.scada.server.io.StrategyUtility;
import org.F11.scada.util.ConnectionUtil;
import org.F11.scada.util.RmiUtil;
import org.apache.log4j.Logger;

/**
 * メール送信時の作業をまとめたユーティリティークラスです。
 *
 * @author maekawa
 *
 */
public class MailUtil {
	private final Logger logger = Logger.getLogger(MailUtil.class);
	public static final String MAIL_CR = "\r\n";
	private StrategyUtility utility;
	private PointCommentService service;

	public MailUtil() {
		utility = new StrategyUtility();
	}

	public Collection<String> getSendAddresses(
			int group_id,
			String provider,
			String holder) throws SQLException {
		Collection<String> addresses = new ArrayList<String>();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = ConnectionUtil.getConnection();
			getMasterAddresses(addresses, con, provider, holder);
			stmt = con.prepareStatement(utility
					.getPrepareStatement("/email/individual/tosql"));
			stmt.setString(1, provider);
			stmt.setString(2, holder);
			rs = stmt.executeQuery();
			if (rs.next()) {
				StringTokenizer st = new StringTokenizer(rs
						.getString("email_address"), ",");
				while (st.hasMoreTokens()) {
					String mailaddress = st.nextToken().trim();
					addresses.add(mailaddress);
				}
			}
			rs.close();
			stmt.close();
			// 旧メール送信機構 start
			if (addresses.size() <= 0) {
				stmt = con.prepareStatement(utility
						.getPrepareStatement("/email/tosql"));
				stmt.setInt(1, group_id);
				rs = stmt.executeQuery();
				while (rs.next()) {
					StringTokenizer st = new StringTokenizer(rs
							.getString("email_address"), ",");
					while (st.hasMoreTokens()) {
						addresses.add(st.nextToken().trim());
					}
				}
				rs.close();
				stmt.close();
			}
			// 旧メール送信機構 end
			con.close();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					stmt = null;
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					con = null;
				}
			}
		}
		return addresses;
	}

	private void getMasterAddresses(
			Collection<String> addresses,
			Connection con,
			String provider,
			String holder) throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;
		st = con.prepareStatement(utility
				.getPrepareStatement("/email/individual/getgroupid"));
		st.setString(1, provider);
		st.setString(2, holder);
		rs = st.executeQuery();
		while (rs.next()) {
			int master_id = rs.getInt("email_group_id");
			addresses.addAll(getMasterAddresses(master_id, con));
		}
		rs.close();
		st.close();
	}

	private Collection<String> getMasterAddresses(int master_id, Connection con)
			throws SQLException {
		Collection<String> addresses = new ArrayList<String>();

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(utility
					.getPrepareStatement("/email/master/tosql"));
			stmt.setInt(1, master_id);
			rs = stmt.executeQuery();
			while (rs.next()) {
				addresses.add(rs.getString("email_address").trim());
			}
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					rs = null;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					stmt = null;
				}
			}
		}
		return addresses;
	}

	public void setPointComment(StringBuffer sb, DataValueChangeEventKey key) {
		PointCommentDto dto = createPointCommentDto(key);
		if (service == null) {
			service = lookup();
		}
		try {
			setPointComment(sb, dto);
		} catch (RemoteException e) {
			try {
				service = lookup();
				setPointComment(sb, dto);
			} catch (RemoteException e1) {
				logger.error("ポイントコメント (ネットワークエラー)", e1);
			}
		}
	}

	private PointCommentDto createPointCommentDto(DataValueChangeEventKey key) {
		PointCommentDto dto = new PointCommentDto();
		dto.setProvider(key.getProvider());
		dto.setHolder(key.getHolder());
		return dto;
	}

	private PointCommentService lookup() {
		return (PointCommentService) RmiUtil
				.lookupServer(PointCommentService.class);
	}

	private void setPointComment(StringBuffer sb, PointCommentDto dto)
			throws RemoteException {
		PointCommentDto data = service.getPointCommentDto(dto);
		if (data != null) {
			sb.append(MAIL_CR).append(data.getComment()).append(MAIL_CR);
		}
	}

	public boolean isDateMode() {
		String b = EnvironmentManager.get(
				"/server/mail/message/datemode",
				"false");
		return Boolean.valueOf(b).booleanValue();
	}

	public void setRecipients(MimeMessage msg, InternetAddress[] address) {
		String fieldType = EnvironmentManager.get(
				"/server/mail/message/address/field",
				"TO").toUpperCase();
		try {
			Field field = Message.RecipientType.class
					.getDeclaredField(fieldType);
			Message.RecipientType type = (Message.RecipientType) field
					.get(null);
			msg.setRecipients(type, address);
		} catch (Exception e) {
			// TOを設定する
			logger.error("Illegal address field type : " + fieldType
					+ ". Address set TO field.");
			try {
				msg.setRecipients(Message.RecipientType.TO, address);
			} catch (MessagingException e1) {
				printMessagingException(e1);
			}
		}
	}

	public void printMessagingException(Exception ex) {
		ex.printStackTrace();
		do {
			if (ex instanceof SendFailedException) {
				SendFailedException sfex = (SendFailedException) ex;
				Address[] invalid = sfex.getInvalidAddresses();
				if (invalid != null) {
					System.out.println("    ** Invalid Addresses");
					if (invalid != null) {
						for (int i = 0; i < invalid.length; i++)
							System.out.println("         " + invalid[i]);
					}
				}
				Address[] validUnsent = sfex.getValidUnsentAddresses();
				if (validUnsent != null) {
					System.out.println("    ** ValidUnsent Addresses");
					if (validUnsent != null) {
						for (int i = 0; i < validUnsent.length; i++)
							System.out.println("         " + validUnsent[i]);
					}
				}
				Address[] validSent = sfex.getValidSentAddresses();
				if (validSent != null) {
					System.out.println("    ** ValidSent Addresses");
					if (validSent != null) {
						for (int i = 0; i < validSent.length; i++)
							System.out.println("         " + validSent[i]);
					}
				}
			}
			System.out.println();
			if (ex instanceof MessagingException)
				ex = ((MessagingException) ex).getNextException();
			else
				ex = null;
		} while (ex != null);
	}

	public boolean isDisable(String holder) {
		SmtpEnvironments environments = new SmtpEnvironments();
		if (0 < environments.getDisableProvider().length()
				&& 0 < environments.getDisableHolder().length()) {
			// EMail禁止フラグの設定が有効で、値がtrue時にメール送信禁止
			DataHolder dh = Manager.getInstance().findDataHolder(
					environments.getDisableProvider(),
					environments.getDisableHolder());
			if (dh != null && dh.getValue() instanceof WifeDataDigital) {
				WifeDataDigital wd = (WifeDataDigital) dh.getValue();
				return wd.isOnOff(true);
			}
		}
		return false;
	}
}
