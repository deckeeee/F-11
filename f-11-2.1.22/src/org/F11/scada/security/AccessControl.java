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
 * WIFE のアクセス制御を判定するクラスです。
 * <p>
 * デフォルトでは postgreSQL のテーブルに設定された、Policy 定義を使用して、 Subject
 * に関連づけられたプリンシパルが、指定されたリソースの権限を持つかを判定します。
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
	 * アクセス制御サーバーを初期化して、Rmiregistory に登録します。
	 * 
	 * @throws RemoteException RMI エラーが発生した場合
	 * @throws MalformedURLException 名前が適切な形式の URL でない場合
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
	 * 全ての編集可能シンボルリソースのアクセス権承認を行います。 指定した Subject が destinations
	 * で指定されたアクセスを、許可されているか判定します。
	 * 
	 * @param subject Subject
	 * @param destinations 編集可能シンボルが保持しているデータホルダー名の配列
	 * @return 許可されている場合は true、そうでない場合は false の Boolean配列のリストを返します。
	 * @throws RemoteException RMI 呼び出しに失敗した場合
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
