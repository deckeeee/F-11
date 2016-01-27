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
package org.F11.scada.xwife.applet;

/**
 * ページ切換のインターフェイスです。
 * @author hori <hori@users.sourceforge.jp>
 */
public interface PageChanger {
	
	/**
	 * 指定キーのページを表示します。
	 * @param pageChange ページ切換イベント
	 */
	public void changePage(PageChangeEvent pageChange);

	/**
	 * 設定されている画面ロックモードの状態を返します。
	 * @return ロックモードなら true、そうでないなら false
	 */
	public boolean isDisplayLock();
	
	/**
	 * 画面ロックモードを設定します。
	 * @param isDisplayLock 画面ロックする場合は true、そうでない場合は false
	 */
	public void setDisplayLock(boolean isDisplayLock);

	/**
	 * Robotクラスでシフトキーを押下する。
	 *
	 */
	void pressShiftKey();

	/**
	 * 警報音を鳴らします。
	 * @param soundPath 音源ファイル
	 */
	void playAlarm(String soundPath);
	
	/**
	 * 警報音を停止します。
	 */
	void stopAlarm();
}
