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
 * Wife�̊��ݒ��ێ�����N���X�ł��B
 * @author  maekawa
 * @version 1.0
 */
public class EnvironmentProperty implements Environment {
	/** �Ǘ��ԍ��̃v���p�e�B�L�[��\���萔�ł� */
	public static final String DEVICE_ID_KEY = "DEVICE_ID";
	/** �ʐM���@�̃v���p�e�B�L�[��\���萔�ł� */
	public static final String DEVICE_KIND_KEY = "DEVICE_KIND";

	/** PLC�|�[�g�i���o�[�̃v���p�e�B�L�[��\���萔�ł� */
	public static final String PLC_PORT_NO_KEY = "PLC_PORT_NO";
	/** PLC�R�}���h��ʂ̃v���p�e�B�L�[��\���萔�ł� */
	public static final String PLC_COMM_KIND_KEY = "PLC_COMM_KIND";
	/** PLC IP�A�h���X�̃v���p�e�B�L�[��\���萔�ł� */
	public static final String PLC_IP_ADDRESS_KEY = "PLC_IP_ADDRESS";
	/** PLC�l�b�g�i���o�[�̃v���p�e�B�L�[��\���萔�ł� */
	public static final String PLC_NET_NO_KEY = "PLC_NET_NO";
	/** PLC�m�[�h�i���o�[�̃v���p�e�B�L�[��\���萔�ł� */
	public static final String PLC_NODE_NO_KEY = "PLC_NODE_NO";
	/** PLC���j�b�g�i���o�[�̃v���p�e�B�L�[��\���萔�ł� */
	public static final String PLC_UNIT_NO_KEY = "PLC_UNIT_NO";
	/** PLCCPU�Ď����Ԃ̃v���p�e�B�L�[��\���萔�ł� */
	public static final String PLC_WATCH_WAIT_KEY = "PLC_WATCH_WAIT";
	/** PLC�^�C���A�E�g�̃v���p�e�B�L�[��\���萔�ł� */
	public static final String PLC_TIMEOUT_KEY = "PLC_TIMEOUT";
	/** PLC�^�C���A�E�g���g���C�񐔂̃v���p�e�B�L�[��\���萔�ł� */
	public static final String PLC_RETRY_COUNT_KEY = "PLC_RETRY_COUNT";
	/** PLC�ʐM�����҂����Ԃ̃v���p�e�B�L�[��\���萔�ł� */
	public static final String PLC_RECOVERY_WAIT_KEY = "PLC_RECOVERY_WAIT";

	/** �z�X�g�l�b�g�i���o�[�̃v���p�e�B�L�[��\���萔�ł� */
	public static final String HOST_NET_NO_KEY = "HOST_NET_NO";
	/** PLC�|�[�g�i���o�[�̃v���p�e�B�L�[��\���萔�ł� */
	public static final String HOST_PORT_NO_KEY = "HOST_PORT_NO";
	/** �z�X�gIP�A�h���X�̃v���p�e�B�L�[��\���萔�ł� */
	public static final String HOST_IP_ADDRESS_KEY = "HOST_IP_ADDRESS";

	/** �����L�[�ŕێ� */
	private java.util.Properties environments;

	/** Creates new WifePlcConfigure */
	public EnvironmentProperty() {
		environments = new java.util.Properties();
	}

	/** �v���p�e�B deviceID �̎擾���\�b�h�B
	 * @return �v���p�e�B plcID �̒l�B
	 */
	public String getDeviceID() {
		return environments.getProperty(DEVICE_ID_KEY);
	}
	/** �v���p�e�B deviceID �̐ݒ胁�\�b�h�B
	 * @param deviceID �v���p�e�B deviceID �̐V�����l�B
	 */
	public void setDeviceID(String deviceID) {
		environments.setProperty(DEVICE_ID_KEY, deviceID);
	}

	/** �v���p�e�B deviceKind �̎擾���\�b�h�B
	 * @return �v���p�e�B deviceKind �̒l�B
	 */
	public String getDeviceKind() {
		return environments.getProperty(DEVICE_KIND_KEY);
	}
	/** �v���p�e�B deviceKind �̐ݒ胁�\�b�h�B
	 * @param deviceKind �v���p�e�B deviceKind �̐V�����l�B
	 */
	public void setDeviceKind(String deviceKind) {
		environments.setProperty(DEVICE_KIND_KEY, deviceKind);
	}

	/** �v���p�e�B plcIpAddress �̎擾���\�b�h�B
	 * @return �v���p�e�B plcIpAddress �̒l�B
	 */
	public String getPlcIpAddress() {
		return environments.getProperty(PLC_IP_ADDRESS_KEY);
	}
	/** �v���p�e�B plcIpAddress �̐ݒ胁�\�b�h�B
	 * @param plcIpAddress �v���p�e�B plcIpAddress �̐V�����l�B
	 */
	public void setPlcIpAddress(String plcIpAddress) {
		environments.setProperty(PLC_IP_ADDRESS_KEY, plcIpAddress);
	}

	/** �v���p�e�B plcPortNo �̎擾���\�b�h�B
	 * @return �v���p�e�B plcPortNo �̒l�B
	 */
	public int getPlcPortNo() {
		return Integer.parseInt(environments.getProperty(PLC_PORT_NO_KEY));
	}
	/** �v���p�e�B plcPortNo �̐ݒ胁�\�b�h�B
	 * @param plcPortNo �v���p�e�B plcPortNo �̐V�����l�B
	 */
	public void setPlcPortNo(int plcPortNo) {
		environments.setProperty(PLC_PORT_NO_KEY, String.valueOf(plcPortNo));
	}

	/** �v���p�e�B plcCommKind �̎擾���\�b�h�B
	 * @return �v���p�e�B plcCommKind �̒l�B
	 */
	public String getPlcCommKind() {
		return environments.getProperty(PLC_COMM_KIND_KEY);
	}
	/** �v���p�e�B plcCommKind �̐ݒ胁�\�b�h�B
	 * @param plcCommKind �v���p�e�B plcCommKind �̐V�����l�B
	 */
	public void setPlcCommKind(String plcCommKind) {
		environments.setProperty(PLC_COMM_KIND_KEY, plcCommKind);
	}

	/** �v���p�e�B plcNetNo �̎擾���\�b�h�B
	 * @return �v���p�e�B plcNetNo �̒l�B
	 */
	public int getPlcNetNo() {
		return Integer.parseInt(environments.getProperty(PLC_NET_NO_KEY));
	}
	/** �v���p�e�B plcNetNo �̐ݒ胁�\�b�h�B
	 * @param plcNetNo �v���p�e�B plcNetNo �̐V�����l�B
	 */
	public void setPlcNetNo(int plcNetNo) {
		environments.setProperty(PLC_NET_NO_KEY, String.valueOf(plcNetNo));
	}

	/** �v���p�e�B plcNodeNo �̎擾���\�b�h�B
	 * @return �v���p�e�B plcNodeNo �̒l�B
	 */
	public int getPlcNodeNo() {
		return Integer.parseInt(environments.getProperty(PLC_NODE_NO_KEY));
	}
	/** �v���p�e�B plcNodeNo �̐ݒ胁�\�b�h�B
	 * @param plcNodeNo �v���p�e�B plcNodeNo �̐V�����l�B
	 */
	public void setPlcNodeNo(int plcNodeNo) {
		environments.setProperty(PLC_NODE_NO_KEY, String.valueOf(plcNodeNo));
	}

	/** �v���p�e�B plcUnitNo �̎擾���\�b�h�B
	 * @return �v���p�e�B plcUnitNo �̒l�B
	 */
	public int getPlcUnitNo() {
		return Integer.parseInt(environments.getProperty(PLC_UNIT_NO_KEY));
	}
	/** �v���p�e�B plcUnitNo �̐ݒ胁�\�b�h�B
	 * @param plcUnitNo �v���p�e�B plcUnitNo �̐V�����l�B
	 */
	public void setPlcUnitNo(int plcUnitNo) {
		environments.setProperty(PLC_UNIT_NO_KEY, String.valueOf(plcUnitNo));
	}

	/** �v���p�e�B plcWatchWait �̎擾���\�b�h�B
	 * @return �v���p�e�B plcWatchWait �̒l�B
	 */
	public int getPlcWatchWait() {
		return Integer.parseInt(environments.getProperty(PLC_WATCH_WAIT_KEY));
	}
	/** �v���p�e�B plcWatchWait �̐ݒ胁�\�b�h�B
	 * @param plcWatchWait �v���p�e�B plcWatchWait �̐V�����l�B
	 */
	public void setPlcWatchWait(int plcWatchWait) {
		environments.setProperty(PLC_WATCH_WAIT_KEY, String.valueOf(plcWatchWait));
	}

	/** �v���p�e�B plcTimeout �̎擾���\�b�h�B
	 * @return �v���p�e�B plcTimeout �̒l�B
	 */
	public int getPlcTimeout() {
		return Integer.parseInt(environments.getProperty(PLC_TIMEOUT_KEY));
	}
	/** �v���p�e�B plcTimeout �̐ݒ胁�\�b�h�B
	 * @param plcTimeout �v���p�e�B plcTimeout �̐V�����l�B
	 */
	public void setPlcTimeout(int plcTimeout) {
		environments.setProperty(PLC_TIMEOUT_KEY, String.valueOf(plcTimeout));
	}

	/** �v���p�e�B plcRetryTime �̎擾���\�b�h�B
	 * @return �v���p�e�B plcRetryTime �̒l�B
	 */
	public int getPlcRetryCount() {
		return Integer.parseInt(environments.getProperty(PLC_RETRY_COUNT_KEY));
	}
	/** �v���p�e�B retry �̐ݒ胁�\�b�h�B
	 * @param plcRetryTime �v���p�e�B retry �̐V�����l�B
	 */
	public void setPlcRetryCount(int plcRetryCount) {
		environments.setProperty(PLC_RETRY_COUNT_KEY, String.valueOf(plcRetryCount));
	}

	/** �v���p�e�B plcRecoveryWait �̎擾���\�b�h�B
	 * @return �v���p�e�B plcRecoveryWait �̒l�B
	 */
	public int getPlcRecoveryWait() {
		return Integer.parseInt(environments.getProperty(PLC_RECOVERY_WAIT_KEY));
	}
	/** �v���p�e�B plcRecoveryWait �̐ݒ胁�\�b�h�B
	 * @param plcRecoveryWait �v���p�e�B plcRecoveryWait �̐V�����l�B
	 */
	public void setPlcRecoveryWait(int plcRecoveryWait) {
		environments.setProperty(PLC_RECOVERY_WAIT_KEY, String.valueOf(plcRecoveryWait));
	}

	/** �v���p�e�B hostNetNo �̎擾���\�b�h�B
	 * @return �v���p�e�B hostNetNo �̒l�B
	 */
	public int getHostNetNo() {
		return Integer.parseInt(environments.getProperty(HOST_NET_NO_KEY));
	}
	/** �v���p�e�B hostNetNo �̐ݒ胁�\�b�h�B
	 * @param hostNetNo �v���p�e�B hostNetNo �̐V�����l�B
	 */
	public void setHostNetNo(int hostNetNo) {
		environments.setProperty(HOST_NET_NO_KEY, String.valueOf(hostNetNo));
	}

	/** �v���p�e�B hostPortNo �̎擾���\�b�h�B
	 * @return �v���p�e�B hostPortNo �̒l�B
	 */
	public int getHostPortNo() {
		return Integer.parseInt(environments.getProperty(HOST_PORT_NO_KEY));
	}
	/** �v���p�e�B hostPortNo �̐ݒ胁�\�b�h�B
	 * @param hostPortNo �v���p�e�B hostPortNo �̐V�����l�B
	 */
	public void setHostPortNo(int hostPortNo) {
		environments.setProperty(HOST_PORT_NO_KEY, String.valueOf(hostPortNo));
	}

	/** �v���p�e�B hostIpAddress �̎擾���\�b�h�B
	 * @return �v���p�e�B hostIpAddress �̒l�B
	 */
	public String getHostIpAddress() {
		return environments.getProperty(HOST_IP_ADDRESS_KEY);
	}
	/** �v���p�e�B hostIpAddress �̐ݒ胁�\�b�h�B
	 * @param hostIpAddress �v���p�e�B hostIpAddress �̐V�����l�B
	 */
	public void setHostIpAddress(String hostIpAddress) {
		environments.setProperty(HOST_IP_ADDRESS_KEY, hostIpAddress);
	}

	/** �v���p�e�B hostAddress �̎擾���\�b�h�B
	 * @return �v���p�e�B hostAddress �̒l�B
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

			// UDP�p�v���p�e�B
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

				// Option�ݒ�
				// host NetNo
				if (st.hasMoreTokens()) {
					setHostNetNo(Integer.parseInt(st.nextToken()));
				}
				else {
					// �w�肪�Ȃ��ꍇ�� 1
					setHostNetNo(1);
				}
				// host IpAddress
				if (st.hasMoreTokens()) {
					setHostIpAddress(st.nextToken());
				}
				else {
					// �w�肪�Ȃ��ꍇ�̓��[�J���A�h���X���擾
					setHostIpAddress(java.net.InetAddress.getLocalHost().getHostAddress());
				}
				// host PortNo
				if (st.hasMoreTokens()) {
					setHostPortNo(Integer.parseInt(st.nextToken()));
				}
				else {
					// �w�肪�Ȃ��ꍇ�� PlcPortNo �Ɠ���
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
