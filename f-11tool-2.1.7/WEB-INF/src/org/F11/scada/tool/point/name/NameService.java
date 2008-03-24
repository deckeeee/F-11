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

package org.F11.scada.tool.point.name;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * ポイント名称更新サービス
 * 
 * @author maekawa
 * 
 */
public interface NameService extends Remote {
	/**
	 * 引数の値で更新します
	 * @param bean
	 * @throws RemoteException
	 */
	void setName(PointNameBean bean) throws RemoteException;

	/**
	 * ポイント名称を追加します
	 * @param bean 追加するポイント
	 * @return (重複等で)正常に追加できなかった場合
	 * @throws RemoteException
	 */
	boolean addName(PointNameBean bean) throws RemoteException;

	/**
	 * ポイント名称を削除します
	 * @param bean
	 * @throws RemoteException
	 */
	void removeName(PointNameBean bean) throws RemoteException;
}
