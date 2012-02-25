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

package org.F11.scada.applet.schedule.point;

import java.rmi.RemoteException;

import javax.swing.table.TableModel;

import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.server.schedule.point.dto.SchedulePointDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;
import org.F11.scada.server.schedule.point.dto.ScheduleSearchDto;

/**
 * スケジュール機器一覧のテーブルモデル
 * 
 * @author maekawa
 * 
 */
public interface SchedulePointTableModel extends TableModel {
	/**
	 * 検索条件でテーブルモデルを生成しなおし、検索結果の「総レコード数(count)」「現在検索オフセット(offset)」「最大検索レコード数(limit)」を返します。
	 * 
	 * @param dto 検索条件
	 * @return 検索終了後の検索条件を返します。
	 */
	SchedulePointDto find(ScheduleSearchDto dto) throws RemoteException;

	/**
	 * 検索条件でテーブルモデルを生成しなおし、検索結果の「総レコード数(count)」「現在検索オフセット(offset)」「最大検索レコード数(limit)」を返します。
	 * 
	 * @param dto 検索条件
	 * @return 検索終了後の検索条件を返します。
	 */
	SchedulePointDto findByGroup(ScheduleSearchDto dto) throws RemoteException;

	/**
	 * AbstractTableModel#fireTableDataChangedをそのまま実行します。
	 * 
	 */
	void fireTableDataChanged();

	/**
	 * 行データを返します。
	 * 
	 * @param row 行
	 * @return 行データを返します。
	 */
	SchedulePointRowDto getSchedulePointRowDto(int row);

	/**
	 * 対象行のデータを更新します
	 * 
	 * @param dto 更新データ
	 * @param row 更新データ行
	 */
	void setSchedulePointRowDto(SchedulePointRowDto dto, int row);

	/**
	 * 対象行のスケジュール値を返します。
	 * @param row 対象の行
	 * @return 対象行のスケジュール値を返します。
	 */
	WifeDataSchedule getSeparateSchedule(int row);
}
