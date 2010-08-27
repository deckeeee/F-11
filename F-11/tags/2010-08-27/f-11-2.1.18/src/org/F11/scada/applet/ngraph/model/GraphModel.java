/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.applet.ngraph.model;


import java.beans.PropertyChangeListener;
import java.util.List;

import org.F11.scada.applet.ngraph.SeriesGroup;


/**
 * グラフモデル
 *
 * @author maekawa
 *
 */
public interface GraphModel {
	/** モデルが初期化されたときに通知します */
	final static String INITIALIZE = GraphModel.class.getName() + ".initialize";
	/** モデル内のデータ値が変更されたときに通知します */
	final static String VALUE_CHANGE =
		GraphModel.class.getName() + ".value.change";
	/** カレントグループが変更されたときに通知します */
	final static String GROUP_CHANGE =
		GraphModel.class.getName() + ".group.change";

	/**
	 * モデルのプロパティ変更リスナーを登録します
	 *
	 * @param listener プロパティ変更リスナー
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * モデルのプロパティ変更リスナーを削除します
	 *
	 * @param listener プロパティ変更リスナー
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * モデルを初期化します。
	 */
	void initialize();

	/**
	 * モデルを終了します。
	 */
	void shutdown();

	/**
	 * モデルが保持するデータレコード数を返します
	 *
	 * @return モデルが保持するデータレコード数を返します
	 */
	int getMaxRecord();

	/**
	 * グループを設定し、モデルの内容を対象グループに変更します。
	 *
	 * @param group 対象グループ
	 */
	void setGroupNo(int group);

	/**
	 * カレントグループNo.を返します。
	 *
	 * @return グループNoを返します。
	 */
	int getGroupNo();

	/**
	 * カレントグループ名を返します。
	 *
	 * @return カレントグループ名を返します
	 */
	String getGroupName();

	/**
	 * グループのリストを返します。
	 *
	 * @return グループのリストを返します。
	 */
	List<SeriesGroup> getSeriesGroups();

	/**
	 * データ取得対象ログ名を返します
	 *
	 * @return データ取得対象ログ名を返します
	 */
	String getLogName();

	/**
	 * データ取得対象ログ名を設定します
	 *
	 * @param logName データ取得対象ログ名を設定します
	 */
	void setLogName(String logName);

	/**
	 * スクロールバーが最小値になった場合通知します。
	 * @return TODO
	 */
	boolean reachedMinimum();

	/**
	 * スクロールバーが最大値になった場合通知します。
	 * @return TODO
	 */
	boolean reachedMaximum();

	/**
	 * プロパティ変更リスナーを全て消去します。
	 */
	void removePropertyChangeListeners();

}