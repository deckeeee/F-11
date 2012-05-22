/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/xwife/server/WifeDataProvider.java,v 1.51.2.11 2007/10/18 09:48:42 frdm Exp $
 * $Revision: 1.51.2.11 $
 * $Date: 2007/10/18 09:48:42 $
 *
 * =============================================================================
 * Projrct    F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All
 * Rights Reserved.
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

package org.F11.scada.xwife.server;

import java.util.List;

import jp.gr.javacons.jim.DataProvider;

import org.F11.scada.Service;
import org.F11.scada.data.WifeData;
import org.F11.scada.server.frame.SendRequestSupport;
import org.F11.scada.xwife.applet.Session;

/**
 * データプロバイダクラスです。通信でPLCからデータを取得して、データホルダーに値を設定していきます。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface WifeDataProvider extends DataProvider, Runnable, Service {
	/** 変換オブジェクト取得のパラメータ名です */
	public static final String PARA_NAME_CONVERT = "convert";
	/** 通信コマンドオブジェクト取得のパラメータ名です */
	public static final String PARA_NAME_COMAND = "command";
	/** ポイント名取得のパラメータ名です */
	public static final String PARA_NAME_POINT = "point";
	/** 常時読み読みフラグのパラメータ名です */
	public static final String PARA_NAME_CYCLEREAD = "cycleread";
	/** 常時読み読み周期です */
	public static final String PARA_NAME_CYCLEREADTIME = "cyclereadtime";
	/** チャタリング防止タイマ値のパラメータ名です */
	public static final String PARA_NAME_OFFDELAY = "off_delay";
	/**	警報リファレンサーのパラメータ名です */
	public static final String PARA_NAME_ALARM = "alarmReferencer";
	/**	デマンド警報リファレンサーのパラメータ名です */
	public static final String PARA_NAME_DEMAND = "demandReferencer";
	/** INITIALからGOODになった場合のフラグ(警報・状態では無視する必要がある為) */
	public static final String PARA_NAME_INIT2GOOD = "org.F11.scada.xwife.server.init2good";
	/** 計算式のパラメータ名です */
	public static final String PARA_NAME_EXPRESSION = "org.F11.scada.xwife.server.expression";

	/** プロバイダー・ホルダー間セパレーター文字列 */
	public static final String SEPARATER = "_";

	/**
	 * 引数のlong値(更新日付のlong値)より上のHolderDataを返します。
	 * @param t
	 * @return HolderData[]
	 */
	public List getHoldersData(long t, Session session);

	/**
	 * フレームマネージャーを設定します。
	 * @param frameDefineManager フレームマネージャー
	 */
    public void setSendRequestSupport(SendRequestSupport sendRequestSupport);

	/**
	 * プロバイダのロックを開始します。ロックを開始する際、通信スレッドに割り込みをかけます。
	 */
    void lock();

	/**
	 * スレッドのロックを外します。
	 */
    void unlock();

    /**
     *
     * @param entryDate
     * @param value
     */
    void addJurnal(long entryDate, WifeData value);
}
