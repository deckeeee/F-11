/*
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

package org.F11.scada.server.schedule;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.server.schedule.point.dto.DuplicateSeparateScheduleDto;
import org.F11.scada.server.schedule.point.dto.ScheduleGroupDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;

public interface SchedulePointCommunicator {
	/**
	 * グループ番号ホルダを更新します。
	 * 
	 * @param dto 行データ
	 */
	void setHolder(SchedulePointRowDto dto);

	/**
	 * 対象ホルダの通信データを返します。
	 * 
	 * @param dto 対象ホルダ
	 * @return 対象ホルダの通信データを返します。
	 */
	List getHolderData(List dto);

	/**
	 * マスタスケジュールから個別スケジュールへデータをコピーします。
	 * 
	 * @param src コピー元マスタスケジュール
	 * @param dest コピー先個別スケジュールホルダの配列
	 * @deprecated
	 */
	void duplicateSeparateSchedule(
			ScheduleGroupDto src,
			SchedulePointRowDto[] dest);

	/**
	 * マスタスケジュールから個別スケジュールへデータをコピーします。
	 * 
	 * @param dto スケジュールコピーオブジェクト
	 */
	void duplicateSeparateSchedule(DuplicateSeparateScheduleDto dto);

	/**
	 * 個別スケジュール(スケジュール)値を返します。
	 * 
	 * @param dto ホルダ名の設定されたオブジェクト
	 * @return 個別スケジュール(スケジュール)値を返します。
	 */
	WifeDataSchedule getSeparateSchedule(SchedulePointRowDto dto);

	/**
	 * 個別スケジュール値を更新します。
	 * 
	 * @param dto ホルダ名の設定された行データ
	 * @param date 更新日時
	 * @param data 更新データ値
	 * @throws RemoteException
	 */
	void updateSeperateSchedule(
			SchedulePointRowDto dto,
			Date date,
			WifeDataSchedule data);

}
