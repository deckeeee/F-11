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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.server.schedule.point.dto.DuplicateSeparateScheduleDto;
import org.F11.scada.server.schedule.point.dto.ScheduleGroupDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;
import org.F11.scada.server.schedule.point.dto.ScheduleSearchDto;

/**
 * スケジュール操作のサーバーサービス
 * 
 * @author maekawa
 * 
 */
public interface SchedulePointService extends Remote {
	/**
	 * 引数のDTO条件でスケジュール機器一覧のレコードを返します。
	 * 
	 * @param dto 検索条件
	 * @return 引数のDTO条件でスケジュール機器一覧のレコードを返します。
	 * @throws RemoteException
	 */
	SchedulePointDto getSchedulePoint(ScheduleSearchDto dto)
			throws RemoteException;

	/**
	 * 引数のDTO条件でスケジュール機器一覧のレコードを返します。
	 * 
	 * @param dto 検索条件
	 * @return 引数のDTO条件でスケジュール機器一覧のレコードを返します。
	 * @throws RemoteException
	 */
	SchedulePointDto getSchedulePointByGroup(ScheduleSearchDto dto)
			throws RemoteException;

	/**
	 * サービスを初期化します。
	 * 
	 * @throws RemoteException
	 */
	void init() throws RemoteException;

	/**
	 * スケジュールグループのレコードを返します。
	 * 
	 * @return スケジュールグループのレコードを返します。
	 * @throws RemoteException
	 */
	List getScheduleGroup(ScheduleGroupDto dto) throws RemoteException;

	/**
	 * 対象スケジュールグループNo.を更新し、PLCの書き込みを行います。
	 * 
	 * @param dto
	 * @return
	 * @throws RemoteException
	 */
	int updateSchedulePoint(SchedulePointRowDto dto) throws RemoteException;

	/**
	 * マスタスケジュールから個別スケジュールへデータをコピーします。
	 * 
	 * @param src コピー元マスタスケジュールホルダ名
	 * @param dest コピー先個別スケジュールホルダ名の配列
	 * @throws RemoteException
	 * @deprecated {@link duplicateSeparateSchedule(DuplicateSeparateScheduleDto)}を使用してください。
	 */
	void duplicateSeparateSchedule(
			ScheduleGroupDto src,
			SchedulePointRowDto[] dest) throws RemoteException;

	/**
	 * マスタスケジュールから個別スケジュールへデータをコピーします。
	 * 
	 * @param dto スケジュールコピーオブジェクト
	 * @throws RemoteException
	 */
	void duplicateSeparateSchedule(DuplicateSeparateScheduleDto dto)
			throws RemoteException;

	/**
	 * 個別スケジュール(スケジュール)値を返します。
	 * 
	 * @param dto ホルダ名の設定された行データ
	 * @return 個別スケジュール(スケジュール)値を返します。
	 */
	WifeDataSchedule getSeparateSchedule(SchedulePointRowDto dto)
			throws RemoteException;

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
			WifeDataSchedule data) throws RemoteException;
}
