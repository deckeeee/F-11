package org.F11.scada.security;

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

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import org.F11.scada.WifeUtilities;
import org.F11.scada.parser.client.ClientsDefine;
import org.F11.scada.security.auth.Authentication;
import org.F11.scada.security.auth.AuthenticationFactory;
import org.F11.scada.security.auth.Subject;
import org.apache.log4j.Logger;

/**
 * <p>
 * WIFE �̃A�N�Z�X����𔻒肷��N���X�ł��B
 * <p>
 * �f�t�H���g�ł� postgreSQL �̃e�[�u���ɐݒ肳�ꂽ�APolicy ��`���g�p���āA Subject
 * �Ɋ֘A�Â���ꂽ�v�����V�p�����A�w�肳�ꂽ���\�[�X�̌����������𔻒肵�܂��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AccessControl extends UnicastRemoteObject
		implements
			AccessControlable {
	private static final long serialVersionUID = 70984571909046404L;
	private static Logger logger;
	private ClientsDefine clientsDefine;

	/**
	 * �A�N�Z�X����T�[�o�[�����������āARmiregistory �ɓo�^���܂��B
	 * 
	 * @throws RemoteException RMI �G���[�����������ꍇ
	 * @throws MalformedURLException ���O���K�؂Ȍ`���� URL �łȂ��ꍇ
	 */
	public AccessControl(int recvPort) throws RemoteException,
			MalformedURLException {
		super(recvPort);
		logger = Logger.getLogger(getClass().getName());
		Naming.rebind(WifeUtilities.createRmiActionControl(), this);
		logger.info("AccessControl bound in registry");

		clientsDefine = new ClientsDefine();
	}

	boolean checkPermission(Subject subject, WifePermission permission) {
		//		logger.debug("principals: " + subject.getPrincipals());
		//		logger.debug("Permission: " + permission);
		WifePolicy wp = (WifePolicy) WifePolicy.getPolicy();
		//		logger.debug("WifePolicy: " + wp.toString());
		return wp.implies(subject, permission);
	}

	/**
	 * �S�Ă̕ҏW�\�V���{�����\�[�X�̃A�N�Z�X�����F���s���܂��B �w�肵�� Subject �� destinations
	 * �Ŏw�肳�ꂽ�A�N�Z�X���A������Ă��邩���肵�܂��B
	 * 
	 * @param subject Subject
	 * @param destinations �ҏW�\�V���{�����ێ����Ă���f�[�^�z���_�[���̔z��
	 * @return ������Ă���ꍇ�� true�A�����łȂ��ꍇ�� false �� Boolean�z��̃��X�g��Ԃ��܂��B
	 * @throws RemoteException RMI �Ăяo���Ɏ��s�����ꍇ
	 */
	public List checkPermission(Subject subject, String[][] destinations)
			throws RemoteException {

		List retList = new ArrayList(destinations.length);
		for (int i = 0; i < destinations.length; i++) {
			retList.add(checkPermission(subject, destinations[i]));
		}

		return retList;
	}

	private Boolean[] checkPermission(Subject subject, String[] destinations) {
		Boolean[] ret = new Boolean[destinations.length];
		for (int i = 0; i < destinations.length; i++) {
			ret[i] = Boolean.valueOf(checkPermission(subject,
					new WifePermission(destinations[i], "write")));
		}
		return ret;
	}

	public Subject checkAuthentication(String user, String password) {
		try {
			Authentication at = AuthenticationFactory.createAuthentication();
			return at.checkAuthentication(user, password);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public String getLogoutUser(InetAddress local) throws RemoteException {
		return clientsDefine.getClientConfig(local.getHostAddress()).getName();
	}

	public static void main(String[] argv) {
		try {
			new AccessControl(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
