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
 * 警報音 再生・停止のインターフェイスです
 * @author hori
 */
public interface AlarmPlayer {
	/**
	 * 指定されたURLの音源をループ再生します
	 * @param path
	 */
	public void playAlarm(String path);

	/**
	 * 再生中の音を停止します
	 */
	public void stopAlarm();

	/**
	 * 設定されている再生禁止モードの状態を返します。
	 * @return 再生禁止モードなら true、そうでないなら false
	 */
	public boolean isAlarmSoundLock();
	
	/**
	 * 再生禁止モードを設定します。
	 * @param isDisplayLock 再生禁止する場合は true、そうでない場合は false
	 */
	public void setAlarmSoundLock(boolean isAlarmSoundLock);
}
