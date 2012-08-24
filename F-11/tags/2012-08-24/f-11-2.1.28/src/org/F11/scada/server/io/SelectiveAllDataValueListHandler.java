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
package org.F11.scada.server.io;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;
import java.util.SortedMap;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public interface SelectiveAllDataValueListHandler extends SelectiveValueListHandler {

    /**
     * 一番古いデータのタイムスタンプを返します。
     * @param name ハンドラ名
	 * @param holderStrings 抽出するデータホルダーのリスト
     * @return 一番古いデータのタイムスタンプを返します。
     * @throws RemoteException
     */
	Timestamp firstTime(String name, List holderStrings) throws RemoteException;

    /**
     * 一番新しいデータのタイムスタンプを返します。
     * @param name ハンドラ名
	 * @param holderStrings 抽出するデータホルダーのリスト
     * @return 一番新しいデータのタイムスタンプを返します。
     * @throws RemoteException
     */
	Timestamp lastTime(String name, List holderStrings) throws RemoteException;
	
	
	/**
	 * 指定範囲のロギングデータを返します。
	 * @param name ハンドラ名
	 * @param holderStrings 抽出するデータホルダーのリスト
	 * @param start 抽出開始日時
	 * @param limit 最大レコード件数
	 * @return 指定範囲のロギングデータを返します。
     * @throws RemoteException
	 */
	SortedMap getLoggingData(String name, List holderStrings, Timestamp start, int limit) throws RemoteException;
}
