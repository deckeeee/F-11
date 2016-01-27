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

import java.awt.Point;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.event.SwingPropertyChangeSupport;

import org.F11.scada.applet.dialog.WifeDialog;
import org.F11.scada.applet.schedule.GroupElement;
import org.F11.scada.applet.schedule.ScheduleModel;
import org.F11.scada.applet.schedule.ScheduleRowModel;
import org.F11.scada.applet.schedule.ScheduleRowModelImpl;
import org.F11.scada.applet.symbol.SymbolCollection;
import org.F11.scada.applet.symbol.ValueSetter;
import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.server.schedule.SchedulePointService;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;
import org.F11.scada.util.RmiErrorUtil;
import org.F11.scada.util.RmiUtil;
import org.apache.log4j.Logger;

public class SeparateScheduleModel implements ScheduleModel {
	private final Logger logger = Logger.getLogger(SeparateScheduleModel.class);
	/** バウンズプロパティです。 */
	private SwingPropertyChangeSupport changeSupport;
	/** スケジュールデータです */
	private WifeDataSchedule dataSchedule;
	/** スケジュールポイントサービスの参照 */
	private final SchedulePointService schedulePointService;
	/** 対象行データ */
	private final SchedulePointRowDto rowDto;
	/** 親コンポーネント */
	private final JDialog dialog;

	/** スケジュールモデル変更イベント名 */
	private static final String SCHEDULE_MODEL_CHANGE = "org.F11.scada.applet.schedule.point.SeparateScheduleModel.SCHEDULE_MODEL_CHANGE";

	public SeparateScheduleModel(SchedulePointRowDto rowDto, JDialog dialog)
			throws RemoteException {
		this((SchedulePointService) RmiUtil
				.lookupServer(SchedulePointService.class), rowDto, dialog);
	}

	public SeparateScheduleModel(
			SchedulePointService schedulePointService,
			SchedulePointRowDto rowDto,
			JDialog dialog) throws RemoteException {
		this.schedulePointService = schedulePointService;
		this.rowDto = rowDto;
		this.dialog = dialog;
		dataSchedule = this.schedulePointService.getSeparateSchedule(rowDto);
	}

	/**
	 * オブジェクトに値を設定します。
	 * 
	 * @exception RemoteException
	 */
	public void setValue() {
		try {
			SchedulePointUtil.setLoggingField(rowDto);
			schedulePointService.updateSeperateSchedule(
					rowDto,
					new Date(),
					dataSchedule);
		} catch (RemoteException e) {
			RmiErrorUtil.error(logger, e, dialog);
		}
	}

	/**
	 * データが編集された場合に true を返します。
	 */
	public boolean isEditing() {
		return false;
	}

	/**
	 * 保持しているスケジュールデータを更新します。
	 */
	public void writeData() {
	}

	/**
	 * 保持しているスケジュールデータをアンドゥします。
	 */
	public void undoData() {
	}

	/**
	 * スケジュールデータのグループを指定したインデックスに変更します。
	 * 
	 * @param index グループインデックス
	 */
	public void setGroupNo(int index) {
	}

	/**
	 * スケジュールパターンのインデックスを返します。
	 * 
	 * @param index 項目（曜日）
	 * @return 項目表すインデックス
	 */
	public int getDayIndex(int index) {
		return dataSchedule.getDayIndex(index);
	}

	/**
	 * グループNo の最大数を返します
	 * 
	 * @return グループNo の最大数
	 */
	public int getGroupNoMax() {
		return 1;
	}

	/**
	 * スケジュールパターンのインデックス名を返します。
	 * 
	 * @param index
	 */
	public String getDayIndexName(int index) {
		return dataSchedule.getDayIndexName(index);
	}

	/**
	 * 特殊日のインデックスを返します。
	 * 
	 * @param index
	 */
	public int getSpecialDayOfIndex(int index) {
		return dataSchedule.getSpecialDayOfIndex(index);
	}

	/**
	 * 項目パターンのサイズを返します。
	 * 
	 * @return 項目パターンのサイズ
	 */
	public int getPatternSize() {
		if (dataSchedule == null) {
			return 0;
		}
		return dataSchedule.getPatternSize();
	}

	/**
	 * スケジュール On/Off の最大回数を返します。
	 * 
	 * @return スケジュール On/Off の最大回数
	 */
	public int getNumberSize() {
		return dataSchedule.getNumberSize();
	}

	/**
	 * スケジュール行モデルを返します。
	 * 
	 * @param index 返す行
	 * @return スケジュール行モデルを返します。
	 */
	public ScheduleRowModel getScheduleRowModel(int index) {
		return new ScheduleRowModelImpl(this, index);
	}

	/**
	 * データモデルが変更されるたびに通知されるリストにリスナーを追加します。
	 * 
	 * @param listener PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (changeSupport == null)
			changeSupport = new SwingPropertyChangeSupport(this);
		changeSupport
				.addPropertyChangeListener(SCHEDULE_MODEL_CHANGE, listener);
	}

	/**
	 * データモデルが変更されるたびに通知されるリストからリスナーを削除します。
	 * 
	 * @param listener PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (changeSupport == null)
			return;
		changeSupport.removePropertyChangeListener(
				SCHEDULE_MODEL_CHANGE,
				listener);
	}

	/**
	 * リスナーにバウンズプロパティ変更イベントを通知します。
	 * 
	 * @param oldValue 変更前の値
	 * @param newValue 変更後の値
	 */
	public void firePropertyChange(Object oldValue, Object newValue) {
		if (changeSupport == null)
			return;
		changeSupport.firePropertyChange(
				SCHEDULE_MODEL_CHANGE,
				oldValue,
				newValue);
	}

	/**
	 * グループNo を返します
	 */
	public int getGroupNo() {
		return 0;
	}

	/**
	 * グループを追加します。
	 * 
	 * @param dataProvider
	 * @param dataHolder
	 * @param flagHolder
	 */
	public void addGroup(
			String dataProvider,
			String dataHolder,
			String flagHolder) {
	}

	/**
	 * 編集する為のダイアログを返します。
	 * 
	 * @param window 親ウィンドウ
	 * @param collection ベースクラスのインスタンス
	 * @param 任意のパラメータリスト
	 * @todo 任意のパラメータはもう少し、型を強制するべきかも。
	 */
	public WifeDialog getDialog(
			Window window,
			SymbolCollection collection,
			List para) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 設定ダイアログの左上の Point オブジェクトを返します。
	 */
	public Point getPoint() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 設定ダイアログの左上の Point オブジェクトを設定します。
	 */
	public void setPoint(Point point) {
		throw new UnsupportedOperationException();
	}

	/**
	 * このシンボルの編集可能フラグを設定します。
	 * 
	 * @param editable 編集可能にするなら true をそうでなければ false を設定します。
	 */
	public void setEditable(boolean[] editable) {
	}

	/**
	 * このシンボルが編集可能かどうかを返します。
	 * 
	 * @return 編集可能な場合は true をそうでなければ false を返します。
	 */
	public boolean isEditable() {
		// TODO AccessControlable#checkPermissionで権限判定
		return true;
	}

	/*
	 * @see org.F11.scada.applet.symbol.Editable#getDestinations()
	 */
	public String[] getDestinations() {
		return new String[0];
	}

	/**
	 * 書き込み先の追加はしない。
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#addDestination(Map)
	 */
	public void addDestination(Map atts) {
	}

	/**
	 * 書き込み先の追加はしない。
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#addElement(Map)
	 */
	public void addValueSetter(ValueSetter setter) {
	}

	/**
	 * グループ名称を返します
	 * 
	 * @return グループ名称を返します
	 */
	public String getGroupName() {
		return dataSchedule.getGroupName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#isTabkeyMove()
	 */
	public boolean isTabkeyMove() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.schedule.ScheduleModel#disConnect()
	 */
	public void disConnect() {
	}

	public void duplicateGroup(int[] dest) {
	}

	public void duplicateWeekOfDay(int src, int[] dest) {
	}

	public GroupElement[] getGroupNames() {
		return new GroupElement[0];
	}

	public int getTopSize() {
		return dataSchedule.getTopSize();
	}

	public WifeDataSchedule getDataSchedule() {
		return dataSchedule;
	}

	public void setDataSchedule(WifeDataSchedule schedule) {
		dataSchedule = schedule;
	}
}
