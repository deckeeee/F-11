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
package org.F11.scada.server.operationlog;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.F11.scada.server.operationlog.dto.FinderConditionDto;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public interface OperationLoggingFinderService extends Remote {
    /**
     * 操作ログレコードを検索します
     * @param finder 検索条件
     * @return 検索結果のレコード(OperationLoggingFinderDto)
     * @throws RemoteException リモート処理時例外発生
     */
    List getOperationLogging(FinderConditionDto finder) throws RemoteException;

    /**
     * 検索条件で返されるレコードの件数を返します
     * @param finder 検索条件
     * @return 検索条件で返されるレコードの件数
     * @throws RemoteException リモート処理時例外発生
     */
    int getCount(FinderConditionDto finder) throws RemoteException;

    /**
     * 操作ログ情報のポイントのプレフィックス有無
     * @return trueの場合はプレフィックスあり、falseの場合はプレフィックス無し
     * @throws RemoteException リモート処理時例外発生
     */
    boolean isPrefix() throws RemoteException;
}
