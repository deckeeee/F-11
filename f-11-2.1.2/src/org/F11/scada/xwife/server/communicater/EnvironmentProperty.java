package org.F11.scada.xwife.server.communicater;

/*
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

import java.util.StringTokenizer;

import org.F11.scada.server.communicater.Environment;

/**
 * Wifeの環境設定を保持するクラスです。
 * @author  maekawa
 * @version 1.0
 */
public class EnvironmentProperty implements Environment {
	/** 管理番号のプロパティキーを表す定数です */
	public static final String DEVICE_ID_KEY = "DEVICE_ID";
	/** 通信方法のプロパティキーを表す定数です */
	public static final String DEVICE_KIND_KEY = "DEVICE_KIND";

	/** PLCポートナンバーのプロパティキーを表す定数です */
	public static final String PLC_PORT_NO_KEY = "PLC_PORT_NO";
	/** PLCコマンド種別のプロパティキーを表す定数です */
	public static final String PLC_COMM_KIND_KEY = "PLC_COMM_KIND";
	/** PLC IPアドレスのプロパティキーを表す定数です */
	public static final String PLC_IP_ADDRESS_KEY = "PLC_IP_ADDRESS";
	/** PLCネットナンバーのプロパティキーを表す定数です */
	public static final String PLC_NET_NO_KEY = "PLC_NET_NO";
	/** PLCノードナンバーのプロパティキーを表す定数です */
	public static final String PLC_NODE_NO_KEY = "PLC_NODE_NO";
	/** PLCユニットナンバーのプロパティキーを表す定数です */
	public static final String PLC_UNIT_NO_KEY = "PLC_UNIT_NO";
	/** PLCCPU監視時間のプロパティキーを表す定数です */
	public static final String PLC_WATCH_WAIT_KEY = "PLC_WATCH_WAIT";
	/** PLCタイムアウトのプロパティキーを表す定数です */
	public static final String PLC_TIMEOUT_KEY = "PLC_TIMEOUT";
	/** PLCタイムアウトリトライ回数のプロパティキーを表す定数です */
	public static final String PLC_RETRY_COUNT_KEY = "PLC_RETRY_COUNT";
	/** PLC通信復旧待ち時間のプロパティキーを表す定数です */
	public static final String PLC_RECOVERY_WAIT_KEY = "PLC_RECOVERY_WAIT";

	/** ホストネットナンバーのプロパティキーを表す定数です */
	public static final String HOST_NET_NO_KEY = "HOST_NET_NO";
	/** PLCポートナンバーのプロパティキーを表す定数です */
	public static final String HOST_PORT_NO_KEY = "HOST_PORT_NO";
	/** ホストIPアドレスのプロパティキーを表す定数です */
	public static final String HOST_IP_ADDRESS_KEY = "HOST_IP_ADDRESS";

	/** 環境をキーで保持 */
	private java.util.Properties environments;

	/** Creates new WifePlcConfigure */
	public EnvironmentProperty() {
		environments = new java.util.Properties();
	}

	/** プロパティ deviceID の取得メソッド。
	 * @return プロパティ plcID の値。
	 */
	public String getDeviceID() {
		return environments.getProperty(DEVICE_ID_KEY);
	}
	/** プロパティ deviceID の設定メソッド。
	 * @param deviceID プロパティ deviceID の新しい値。
	 */
	public void setDeviceID(String deviceID) {
		environments.setProperty(DEVICE_ID_KEY, deviceID);
	}

	/** プロパティ deviceKind の取得メソッド。
	 * @return プロパティ deviceKind の値。
	 */
	public String getDeviceKind() {
		return environments.getProperty(DEVICE_KIND_KEY);
	}
	/** プロパティ deviceKind の設定メソッド。
	 * @param deviceKind プロパティ deviceKind の新しい値。
	 */
	public void setDeviceKind(String deviceKind) {
		environments.setProperty(DEVICE_KIND_KEY, deviceKind);
	}

	/** プロパティ plcIpAddress の取得メソッド。
	 * @return プロパティ plcIpAddress の値。
	 */
	public String getPlcIpAddress() {
		return environments.getProperty(PLC_IP_ADDRESS_KEY);
	}
	/** プロパティ plcIpAddress の設定メソッド。
	 * @param plcIpAddress プロパティ plcIpAddress の新しい値。
	 */
	public void setPlcIpAddress(String plcIpAddress) {
		environments.setProperty(PLC_IP_ADDRESS_KEY, plcIpAddress);
	}

	/** プロパティ plcPortNo の取得メソッド。
	 * @return プロパティ plcPortNo の値。
	 */
	public int getPlcPortNo() {
		return Integer.parseInt(environments.getProperty(PLC_PORT_NO_KEY));
	}
	/** プロパティ plcPortNo の設定メソッド。
	 * @param plcPortNo プロパティ plcPortNo の新しい値。
	 */
	public void setPlcPortNo(int plcPortNo) {
		environments.setProperty(PLC_PORT_NO_KEY, String.valueOf(plcPortNo));
	}

	/** プロパティ plcCommKind の取得メソッド。
	 * @return プロパティ plcCommKind の値。
	 */
	public String getPlcCommKind() {
		return environments.getProperty(PLC_COMM_KIND_KEY);
	}
	/** プロパティ plcCommKind の設定メソッド。
	 * @param plcCommKind プロパティ plcCommKind の新しい値。
	 */
	public void setPlcCommKind(String plcCommKind) {
		environments.setProperty(PLC_COMM_KIND_KEY, plcCommKind);
	}

	/** プロパティ plcNetNo の取得メソッド。
	 * @return プロパティ plcNetNo の値。
	 */
	public int getPlcNetNo() {
		return Integer.parseInt(environments.getProperty(PLC_NET_NO_KEY));
	}
	/** プロパティ plcNetNo の設定メソッド。
	 * @param plcNetNo プロパティ plcNetNo の新しい値。
	 */
	public void setPlcNetNo(int plcNetNo) {
		environments.setProperty(PLC_NET_NO_KEY, String.valueOf(plcNetNo));
	}

	/** プロパティ plcNodeNo の取得メソッド。
	 * @return プロパティ plcNodeNo の値。
	 */
	public int getPlcNodeNo() {
		return Integer.parseInt(environments.getProperty(PLC_NODE_NO_KEY));
	}
	/** プロパティ plcNodeNo の設定メソッド。
	 * @param plcNodeNo プロパティ plcNodeNo の新しい値。
	 */
	public void setPlcNodeNo(int plcNodeNo) {
		environments.setProperty(PLC_NODE_NO_KEY, String.valueOf(plcNodeNo));
	}

	/** プロパティ plcUnitNo の取得メソッド。
	 * @return プロパティ plcUnitNo の値。
	 */
	public int getPlcUnitNo() {
		return Integer.parseInt(environments.getProperty(PLC_UNIT_NO_KEY));
	}
	/** プロパティ plcUnitNo の設定メソッド。
	 * @param plcUnitNo プロパティ plcUnitNo の新しい値。
	 */
	public void setPlcUnitNo(int plcUnitNo) {
		environments.setProperty(PLC_UNIT_NO_KEY, String.valueOf(plcUnitNo));
	}

	/** プロパティ plcWatchWait の取得メソッド。
	 * @return プロパティ plcWatchWait の値。
	 */
	public int getPlcWatchWait() {
		return Integer.parseInt(environments.getProperty(PLC_WATCH_WAIT_KEY));
	}
	/** プロパティ plcWatchWait の設定メソッド。
	 * @param plcWatchWait プロパティ plcWatchWait の新しい値。
	 */
	public void setPlcWatchWait(int plcWatchWait) {
		environments.setProperty(PLC_WATCH_WAIT_KEY, String.valueOf(plcWatchWait));
	}

	/** プロパティ plcTimeout の取得メソッド。
	 * @return プロパティ plcTimeout の値。
	 */
	public int getPlcTimeout() {
		return Integer.parseInt(environments.getProperty(PLC_TIMEOUT_KEY));
	}
	/** プロパティ plcTimeout の設定メソッド。
	 * @param plcTimeout プロパティ plcTimeout の新しい値。
	 */
	public void setPlcTimeout(int plcTimeout) {
		environments.setProperty(PLC_TIMEOUT_KEY, String.valueOf(plcTimeout));
	}

	/** プロパティ plcRetryTime の取得メソッド。
	 * @return プロパティ plcRetryTime の値。
	 */
	public int getPlcRetryCount() {
		return Integer.parseInt(environments.getProperty(PLC_RETRY_COUNT_KEY));
	}
	/** プロパティ retry の設定メソッド。
	 * @param plcRetryTime プロパティ retry の新しい値。
	 */
	public void setPlcRetryCount(int plcRetryCount) {
		environments.setProperty(PLC_RETRY_COUNT_KEY, String.valueOf(plcRetryCount));
	}

	/** プロパティ plcRecoveryWait の取得メソッド。
	 * @return プロパティ plcRecoveryWait の値。
	 */
	public int getPlcRecoveryWait() {
		return Integer.parseInt(environments.getProperty(PLC_RECOVERY_WAIT_KEY));
	}
	/** プロパティ plcRecoveryWait の設定メソッド。
	 * @param plcRecoveryWait プロパティ plcRecoveryWait の新しい値。
	 */
	public void setPlcRecoveryWait(int plcRecoveryWait) {
		environments.setProperty(PLC_RECOVERY_WAIT_KEY, String.valueOf(plcRecoveryWait));
	}

	/** プロパティ hostNetNo の取得メソッド。
	 * @return プロパティ hostNetNo の値。
	 */
	public int getHostNetNo() {
		return Integer.parseInt(environments.getProperty(HOST_NET_NO_KEY));
	}
	/** プロパティ hostNetNo の設定メソッド。
	 * @param hostNetNo プロパティ hostNetNo の新しい値。
	 */
	public void setHostNetNo(int hostNetNo) {
		environments.setProperty(HOST_NET_NO_KEY, String.valueOf(hostNetNo));
	}

	/** プロパティ hostPortNo の取得メソッド。
	 * @return プロパティ hostPortNo の値。
	 */
	public int getHostPortNo() {
		return Integer.parseInt(environments.getProperty(HOST_PORT_NO_KEY));
	}
	/** プロパティ hostPortNo の設定メソッド。
	 * @param hostPortNo プロパティ hostPortNo の新しい値。
	 */
	public void setHostPortNo(int hostPortNo) {
		environments.setProperty(HOST_PORT_NO_KEY, String.valueOf(hostPortNo));
	}

	/** プロパティ hostIpAddress の取得メソッド。
	 * @return プロパティ hostIpAddress の値。
	 */
	public String getHostIpAddress() {
		return environments.getProperty(HOST_IP_ADDRESS_KEY);
	}
	/** プロパティ hostIpAddress の設定メソッド。
	 * @param hostIpAddress プロパティ hostIpAddress の新しい値。
	 */
	public void setHostIpAddress(String hostIpAddress) {
		environments.setProperty(HOST_IP_ADDRESS_KEY, hostIpAddress);
	}

	/** プロパティ hostAddress の取得メソッド。
	 * @return プロパティ hostAddress の値。
	 */
	public int getHostAddress() {
		String s = getHostIpAddress();
		if (s == null) {
			return 0;
		}
		return Integer.parseInt(s.substring(s.lastIndexOf('.') + 1));
	}

	public void setAllPropertys(String line) throws Exception {
		try {
			StringTokenizer st = new StringTokenizer(line);

			// DeviceID
			setDeviceID(st.nextToken());
			// DeviceKing
			setDeviceKind(st.nextToken());

			// UDP用プロパティ
			if (getDeviceKind().equals("UDP")) {
//			if (getDeviceKind().getClass().getName().endsWith("UDP")) {
				// IpAddress
				setPlcIpAddress(st.nextToken());
				// PortNo
				setPlcPortNo(Integer.parseInt(st.nextToken()));
				// CommKind
				setPlcCommKind(st.nextToken());
				if (getPlcCommKind().equals("FINS")) {
					// NetNo
					setPlcNetNo(Integer.parseInt(st.nextToken()));
					// NodeNo
					setPlcNodeNo(Integer.parseInt(st.nextToken()));
					// UnitNo
					setPlcUnitNo(Integer.parseInt(st.nextToken()));
				}
				else if(getPlcCommKind().equals("MC3E")) {
					// NetNo
					setPlcNetNo(Integer.parseInt(st.nextToken()));
					// NodeNo
					setPlcNodeNo(Integer.parseInt(st.nextToken()));
					// Watch
					setPlcWatchWait(Integer.parseInt(st.nextToken()));
				}
				else {
					throw new Exception(" ERROR: not \"FINS\" \"MC3E\".");
				}
				// Timeout
				setPlcTimeout(Integer.parseInt(st.nextToken()));
				// RetryTime
				setPlcRetryCount(Integer.parseInt(st.nextToken()));
				// RecoveryWaitTime
				setPlcRecoveryWait(Integer.parseInt(st.nextToken()));

				// Option設定
				// host NetNo
				if (st.hasMoreTokens()) {
					setHostNetNo(Integer.parseInt(st.nextToken()));
				}
				else {
					// 指定がない場合は 1
					setHostNetNo(1);
				}
				// host IpAddress
				if (st.hasMoreTokens()) {
					setHostIpAddress(st.nextToken());
				}
				else {
					// 指定がない場合はローカルアドレスを取得
					setHostIpAddress(java.net.InetAddress.getLocalHost().getHostAddress());
				}
				// host PortNo
				if (st.hasMoreTokens()) {
					setHostPortNo(Integer.parseInt(st.nextToken()));
				}
				else {
					// 指定がない場合は PlcPortNo と同一
					setHostPortNo(getPlcPortNo());
				}
			}
			else if (getDeviceKind().equals("BACnet")) {
				// PortNo
				setPlcPortNo(0xBAC0);
			}
			else {
				throw new Exception(" ERROR: not \"UDP\".");
			}
		} catch(java.util.NoSuchElementException e) {
			throw new Exception(" ERROR: short of property.");
		}
	}
}
