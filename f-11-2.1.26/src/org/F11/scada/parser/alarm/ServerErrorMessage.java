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

package org.F11.scada.parser.alarm;

/**
 * サーバーコネクションエラーメッセージクラス
 * メッセージ内容と音声ファイルのパスを保持します
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ServerErrorMessage {
	/** 表示メッセージ */
	private String message;
	/** 音声ファイルのパス */
	private String sound;
	
	/**
	 * 表示メッセージを返します
	 * @return 表示メッセージ
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * 表示メッセージを設定します
	 * @param string 表示メッセージ
	 */
	public void setMessage(String string) {
		message = string;
	}

	/**
	 * 音声ファイルのパスを返します
	 * @return 音声ファイルのパス
	 */
	public String getSound() {
		return sound;
	}

	/**
	 * 音声ファイルのパスを設定します
	 * @param string 音声ファイルのパス
	 */
	public void setSound(String string) {
		sound = string;
	}

}
