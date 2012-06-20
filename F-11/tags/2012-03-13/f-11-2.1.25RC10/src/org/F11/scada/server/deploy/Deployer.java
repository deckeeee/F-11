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
 */

package org.F11.scada.server.deploy;

import java.io.File;

/**
 * 配備オブジェクトのインターフェイスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface Deployer {
	/**
	 * 配備処理を行います
	 * @param file 配備するファイル
	 * @exception DeploymentException 配備時に例外が発生した場合
	 */
	public void deploy(File file) throws DeploymentException;

	/**
	 * 非配備処理を行います
	 * @param file 配備するファイル
	 * @exception DeploymentException 配備時に例外が発生した場合
	 */
	public void undeploy(File file) throws DeploymentException;

	/**
	 * 引数のページ定義XMLが配備されているかを判定します
	 * @param pageName ページ名称
	 * @return 既に配備済みであれば true をそうでなければ false を返します
	 */
	public boolean isDeployed(String pageName);
}
