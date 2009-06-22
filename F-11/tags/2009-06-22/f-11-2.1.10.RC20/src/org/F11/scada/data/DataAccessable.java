/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/DataAccessable.java,v 1.9.2.6 2006/08/11 02:24:33 frdm Exp $
 * $Revision: 1.9.2.6 $
 * $Date: 2006/08/11 02:24:33 $
 * 
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

package org.F11.scada.data;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.QualityFlag;

import org.F11.scada.xwife.applet.Session;

/**
 * データプロバイダ・データプロバイダプロクシ間のリモートインターフェイスです。
 * アプレット経由でデータプロバイダにアクセスする為のインターフェイスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface DataAccessable extends Remote {
	/**
	 * 指定されたデータを返します。
	 * @param dpname データプロバイダ名
	 * @param dhname データホルダ名
	 * @return 指定したデータホルダのデータオブジェクト
	 * @exception RemoteException RMI エラーが発生したとき
	 */
	public Object getValue(String dpname, String dhname)
		throws RemoteException;

	/**
	 * 指定されたクオリティフラグを返します。
	 * @param dpname データプロバイダ名
	 * @param dhname データホルダ名
	 * @return 指定したデータホルダのデータオブジェクト
	 * @exception RemoteException RMI エラーが発生したとき
	 */
	public QualityFlag getQualityFlag(String dpname, String dhname)
		throws RemoteException;

	/**
	 * データホルダに値オブジェクトを設定します。
	 * @param dpname データプロバイダ名
	 * @param dhname データホルダ名
	 * @param dataValue 値オブジェクト
	 * @exception RemoteException RMI エラーが発生したとき
	 */
	public void setValue(String dpname, String dhname, Object dataValue)
		throws RemoteException;

	/**
	 * データホルダに値オブジェクトを設定します。
	 * @param dpname データプロバイダ名
	 * @param dhname データホルダ名
	 * @param dataValue 値オブジェクト
	 * @param user ユーザー
	 * @param ip 端末IP
	 * @exception RemoteException RMI エラーが発生したとき
	 */
	public void setValue(String dpname, String dhname, Object dataValue, String user, String ip)
		throws RemoteException;

	/**
	 * ヒストリテーブルの確認フラグを設定します。
	 * @param point ポイント番号
	 * @param dpname データプロバイダ名
	 * @param dhname データホルダ名
	 * @param date 更新日付
	 * @param row マウスクリック（変更）行
	 * @throws RemoteException RemoteException RMI エラーが発生したとき
	 */
	public void setHistoryTable(
		Integer point,
		String dpname,
		String dhname,
		Timestamp date,
		Integer row)
		throws RemoteException;

	/**
	 * 指定されたデータホルダを返します。
	 * @param dpname データプロバイダ名
	 * @param dhname データホルダ名
	 * @return 存在した場合は DataHolder オブジェクト、無い場合は null を返します。
	 * @throws RemoteException RemoteException RMI エラーが発生したとき
	 */
	public DataHolder findDataHolder(String dpname, String dhname)
		throws RemoteException;

	/**
	 * 指定されたデータホルダのパラメタを返します。
	 * @param dpname データプロバイダ名
	 * @param dhname データホルダ名
	 * @param paraName パラメタ名
	 * @return パラメタオブジェクト
	 * @throws RemoteException RemoteException RMI エラーが発生したとき
	 */
	public Object getParameta(String dpname, String dhname, String paraName)
		throws RemoteException;

	/**
	 * データホルダ更新データを返します。
	 * @param provider データプロバイダ名
	 * @return データホルダ更新データのListオブジェクト 
	 */
	public List getHoldersData(String provider) throws RemoteException;

	/**
	 * データホルダ更新データを返します。
	 * @param provider データプロバイダ名
	 * @param t 保持データの最新日付の long 値
	 * @return データホルダ更新データのListオブジェクト 
	 */
	public List getHoldersData(String provider, long t, Session session) throws RemoteException;

	/**
	 * サーバーで実行されている、データプロバイダ名の配列を返します。
	 * @return データプロバイダ名のリスト
	 */
	public List getDataProviders() throws RemoteException;


	/**
	 * データホルダー生成オブジェクトの配列を返します。
	 * @param dataProvider データプロバイダ名
	 * @return CreateHolderData[] データホルダー生成オブジェクトの配列 
	 */
	public List getCreateHolderDatas(String dataProvider)
		throws RemoteException;

	/**
	 * タイムスタンプで指定された以上のジャーナルデータを返します。
	 * @param t タイムスタンプのLong値
	 * @param provider データプロバイダー名
	 * @param holder データホルダー名
	 * @return SortedMap ジャーナルデータのリスト
	 */
	public SortedMap getAlarmJournal(long t, String provider, String holder)
		throws RemoteException;

	/**
	 * タイムスタンプで指定された以上のジャーナルデータを返します。
	 * @param t タイムスタンプのLong値
	 * @return SortedMap 更新日時と PointTableBean オブジェクトのマップ
	 * @since 1.0.3
	 */
	public SortedMap getPointJournal(long t) throws RemoteException;

	/**
	 * データホルダー生成オブジェクトの配列を返します。
	 * @param holderStrings データホルダ情報
	 * @return CreateHolderData[] データホルダー生成オブジェクトの配列 
	 */
	public List getCreateHolderDatas(Collection holderStrings)
		throws RemoteException;

	/**
	 * サーバーのコマンドを呼び出します。
	 * @param command コマンド名
	 * @param args 引数
	 * @return 戻り値があるばあいはオブジェクトが返される、そうでない場合は null が返される。
	 */
	Object invoke(String command, Object[] args) throws RemoteException;
}
