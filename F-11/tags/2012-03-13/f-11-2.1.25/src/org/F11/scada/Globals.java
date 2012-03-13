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

package org.F11.scada;

import java.awt.Dimension;
import java.sql.Timestamp;

import org.F11.scada.applet.ClientConfiguration;

/**
 * グローバル変数を保持するクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class Globals {
	private Globals() {}

	/**
	 * RMIコネクションの再接続までの待ち時間です。
	 */
	public static final long RMI_CONNECTION_RETRY_WAIT_TIME = 10000L;
	
	/**
	 * RMIコネクションの再接続リトライ回数です。
	 */
	public static final int RMI_CONNECTION_RETRY_COUNT = 10;
	
	/**
	 * RMIメソッドのリトライ回数です。
	 */
	public static final int RMI_METHOD_RETRY_COUNT = 5;

	/**
	 * EPOCH タイムスタンプオブジェクトです。
	 */	
	public static final Timestamp EPOCH = new Timestamp(0);
	
	/**
	 * 空文字列の定数です。
	 */
	public static final String NULL_STRING = "";

	/**
	 * カレンダーのデータタイプIDです
	 */
	public static final int DATA_TYPE_CALENDAR = 15;

	/**
	 * スケジュールのデータタイプIDです
	 */
	public static final int DATA_TYPE_SCHEDULE = 16;
	
	/**
	 * タイムスタンプのデータタイプIDです
	 */
	public static final int DATA_TYPE_TIMESTAMP = 17;

	/**
	 * Seasar2コンポーネント定義ファイル
	 */
    public static final String APP_DICON = "app.dicon";
    
    /**
     * エラーホルダ名称
     */
    public static final String ERR_HOLDER = "ERR_HOLDER";
    
    /**
     * シンボルの初期表示リトライ回数
     */
    public static final int SYMBOL_REPAINT_COUNT;
	static {
    	ClientConfiguration configuration = new ClientConfiguration();
    	SYMBOL_REPAINT_COUNT = configuration.getInt("symbol.repaint.count", 3);
    }

	/**
	 * 0, 0のDimensionオブジェクトです。
	 */
	public static final Dimension ZERO_DIMENSION = new Dimension(0, 0);
}
