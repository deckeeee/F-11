package org.F11.scada.xwife.server.communicater;

/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.F11.scada.server.communicater.Environment;
import org.F11.scada.util.ConnectionUtil;
import org.apache.log4j.Logger;

public class EnvironmentPostgreSQL implements Environment {
	private static Logger logger;

	private String deviceID;
	private String deviceKind;
	private String plcIpAddress;
	private int plcPortNo;
	private String plcCommKind;
	private int plcNetNo;
	private int plcNodeNo;
	private int plcUnitNo;
	private int plcWatchWait;
	private int plcTimeout;
	private int plcRetryCount;
	private int plcRecoveryWait;
	private int hostNetNo;
	private int hostPortNo;
	private String hostIpAddress;

	private EnvironmentPostgreSQL(ResultSet result) throws SQLException,
			UnknownHostException {
		deviceID = result.getString(1);
		deviceKind = result.getString(2);
		plcIpAddress = result.getString(3);
		plcPortNo = result.getInt(4);
		plcCommKind = result.getString(5);
		plcNetNo = result.getInt(6);
		plcNodeNo = result.getInt(7);
		plcUnitNo = result.getInt(8);
		plcWatchWait = result.getInt(9);
		plcTimeout = result.getInt(10);
		plcRetryCount = result.getInt(11);
		plcRecoveryWait = result.getInt(12);
		hostNetNo = result.getInt(13);
		hostPortNo = result.getInt(14);
		if (result.wasNull()) {
			hostPortNo = plcPortNo;
		}
		hostIpAddress = result.getString(15);
		if (result.wasNull()) {
			hostIpAddress = InetAddress.getLocalHost().getHostAddress();
		}
	}

	/**
	 * デバイスIDを返します。
	 * @return デバイスID
	 */
	public String getDeviceID() {
		return deviceID;
	}

	/**
	 * デバイスの種類を返します。
	 * @return デバイスの種類
	 */
	public String getDeviceKind() {
		return deviceKind;
	}

	/**
	 * デバイスのIPアドレスを返します。
	 * @return デバイスのIPアドレス
	 */
	public String getPlcIpAddress() {
		return plcIpAddress;
	}

	/**
	 * デバイスの通信ポートを返します。
	 * @return デバイスの通信ポート
	 */
	public int getPlcPortNo() {
		return plcPortNo;
	}

	/**
	 * デバイスのコマンド形態を返します。
	 * @return デバイスのコマンド形態
	 */
	public String getPlcCommKind() {
		return plcCommKind;
	}

	/**
	 * デバイスのネット番号を返します。
	 * @return デバイスのネット番号
	 */
	public int getPlcNetNo() {
		return plcNetNo;
	}

	/**
	 * デバイスのノード番号を返します。
	 * @return デバイスのノード番号
	 */
	public int getPlcNodeNo() {
		return plcNodeNo;
	}

	/**
	 * デバイスのユニット番号を返します。
	 * @return デバイスのユニット番号
	 */
	public int getPlcUnitNo() {
		return plcUnitNo;
	}

	/**
	 * デバイスの通信待ち時間を返します。
	 * @return デバイスの通信待ち時間
	 */
	public int getPlcWatchWait() {
		return plcWatchWait;
	}

	/**
	 * デバイスのタイムアウト時間を返します。
	 * @return デバイスのタイムアウト時間
	 */
	public int getPlcTimeout() {
		return plcTimeout;
	}

	/**
	 * デバイスのエラーリトライ回数を返します。
	 * @return デバイスのエラーリトライ回数
	 */
	public int getPlcRetryCount() {
		return plcRetryCount;
	}

	/**
	 * デバイスの通信復旧待ち時間を返します。
	 * @return デバイスの通信復旧待ち時間
	 */
	public int getPlcRecoveryWait() {
		return plcRecoveryWait;
	}

	/**
	 * ホストのネットアドレスを返します。
	 * @return ホストのネットアドレス
	 */
	public int getHostNetNo() {
		return hostNetNo;
	}

	/**
	 * ホストの通信ポートを返します。
	 * @return ホストの通信ポート
	 */
	public int getHostPortNo() {
		return hostPortNo;
	}

	/**
	 * ホストのIPアドレスを返します。
	 * @return ホストのIPアドレス
	 */
	public String getHostIpAddress() {
		return hostIpAddress;
	}

	/**
	 * ホストのホストアドレスを返します。
	 * @return ホストのホストアドレス
	 */
	public int getHostAddress() {
		String s = getHostIpAddress();
		if (s == null) {
			return 0;
		}
		return Integer.parseInt(s.substring(s.lastIndexOf('.') + 1));
	}

	/**
	 * デバイスのIPアドレス(二重化用)を返します。
	 * @return デバイスのIPアドレス
	 */
	public String getPlcIpAddress2() {
		return null;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(deviceID + ", ");
		buffer.append(deviceKind + ", ");
		buffer.append(plcIpAddress + ", ");
		buffer.append(plcPortNo + ", ");
		buffer.append(plcCommKind + ", ");
		buffer.append(plcNetNo + ", ");
		buffer.append(plcNodeNo + ", ");
		buffer.append(plcUnitNo + ", ");
		buffer.append(plcWatchWait + ", ");
		buffer.append(plcTimeout + ", ");
		buffer.append(plcRetryCount + ", ");
		buffer.append(plcRecoveryWait + ", ");
		buffer.append(hostNetNo + ", ");
		buffer.append(hostPortNo + ", ");
		buffer.append(hostIpAddress);

		return buffer.toString();
	}

	public static Environment[] getEnvironments() throws SQLException,
			UnknownHostException {
		// Class.forName("org.postgresql.Driver");
		if (logger == null) {
			logger = Logger.getLogger(EnvironmentPostgreSQL.class.getClass());
		}

		Connection con = null;
		Statement stmt = null;
		ResultSet result = null;

		try {
			con = ConnectionUtil.getConnection();
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT ");
			buffer.append("id, kind, ip, port, command, net, node, unit, watch_wait, timeout, retry_count, ");
			buffer.append("recovery_wait, host_net, host_port, host_ip ");
			buffer.append("from device_properties_table");

			logger.debug(buffer.toString());

			result = stmt.executeQuery(buffer.toString());
			result.last();
			EnvironmentPostgreSQL[] environments = new EnvironmentPostgreSQL[result.getRow()];
			result.beforeFirst();
			for (int i = 0; result.next() && i < environments.length; i++) {
				environments[i] = new EnvironmentPostgreSQL(result);
			}
			result.close();
			stmt.close();
			con.close();

			return environments;
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					result = null;
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
	}
}
