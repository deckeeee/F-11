/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/server/alarm/table/StrategyFactory.java,v 1.1.6.2 2006/08/11 02:24:33 frdm Exp $
 * $Revision: 1.1.6.2 $
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
package org.F11.scada.server.alarm.table;


/**
 * 行操作アルゴリズムオブジェクトの生成ファクトリークラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public abstract class StrategyFactory {

	/**
	 * 行操作アルゴリズムオブジェクトの生成ファクトリーを返します。
	 * @param className 行操作アルゴリズム実装クラス
	 * @return 行操作アルゴリズムオブジェクトの生成ファクトリー
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static StrategyFactory createStrategyFactory(Class className)
			throws InstantiationException, IllegalAccessException {
		return (StrategyFactory) className.newInstance();
	}

	/**
	 * 履歴テーブルモデル操作アルゴリズムを返します。
	 * @param model テーブルモデル
	 * @return 操作アルゴリズムオブジェクト
	 */
	abstract public RowDataStrategy createCareerStrategy(AlarmTableModel model);

	/**
	 * ヒストリーテーブルモデル操作アルゴリズムを返します。
	 * @param model テーブルモデル
	 * @return 操作アルゴリズムオブジェクト
	 */
	abstract public RowDataStrategy createHistoryStrategy(AlarmTableModel model);

	/**
	 * サマリーテーブルモデル操作アルゴリズムを返します。
	 * @param model テーブルモデル
	 * @return 操作アルゴリズムオブジェクト
	 */
	abstract public RowDataStrategy createSummaryStrategy(AlarmTableModel model);

	/**
	 * 未復旧テーブルモデル操作アルゴリズムを返します。
	 * @param model テーブルモデル
	 * @return 操作アルゴリズムオブジェクト
	 */
	abstract public RowDataStrategy createOccurrenceStrategy(AlarmTableModel model);

	/**
	 * 未確認テーブルモデル操作アルゴリズムを返します。
	 * @param model テーブルモデル
	 * @return 操作アルゴリズムオブジェクト
	 */
	abstract public RowDataStrategy createNoncheckStrategy(AlarmTableModel model);
}
