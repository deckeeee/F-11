package org.F11.scada.applet.symbol;

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

import java.awt.Point;
import java.awt.Window;
import java.util.Map;

import org.F11.scada.applet.dialog.WifeDialog;

/**
 * 編集可能なシンボルはこのインターフェイスを実装し、シンボル値を編集するダイアログを返す必要があります。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface Editable {
	/**
	 * 編集する為のダイアログを返します。
	 * @param window 親ウィンドウ
	 * @param collection ベースクラスのインスタンス
	 * @param 任意のパラメータリスト
	 * @todo 任意のパラメータはもう少し、型を強制するべきかも。
	 */
	public WifeDialog getDialog(Window window, SymbolCollection collection, java.util.List para);
	/**
	 * 設定ダイアログの左上の Point オブジェクトを返します。
	 */
	public Point getPoint();
	/**
	 * 設定ダイアログの左上の Point オブジェクトを設定します。
	 */
	public void setPoint(Point point);
	/**
	 * このシンボルの編集可能フラグを設定します。
	 * @param editable 編集可能にするなら true をそうでなければ false を設定します。
	 */
	public void setEditable(boolean[] editable);
	/**
	 * このシンボルが編集可能かどうかを返します。
	 * @return 編集可能な場合は true をそうでなければ false を返します。
	 */
	public boolean isEditable();

	/**
	 * ベースシステムのユーザー認証により Subject が変更されたときにディスパッチされます。
	 * 編集可能なシンボルが保持している、データプロバイダ名＋データホルダー名をアンダーバー
	 * で結合した文字列配列を返します。
	 * @return データプロバイダ名＋データホルダー名をアンダーバーで結合した文字列配列
	 */
	public String[] getDestinations();
	
	/**
	 * 書き込み先を追加します
	 * @param params 
	 */
	public void addDestination(Map params);
	
	/**
	 * 書き込み先を設定します。
	 * @param setter
	 */
	public void addValueSetter(ValueSetter setter);
	
	/**
	 * タブキーダイアログ移動使用の有無
	 * @return タブキー移動をするなら true を そうでないなら false を返します
	 */
	public boolean isTabkeyMove();
	
	/**
	 * 表示中なら true を そうでないなら false を返します。
	 * 
	 * @return 表示中なら true を そうでないなら false を返します。
	 */
	boolean isVisible();
}
