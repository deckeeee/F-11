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

package org.F11.scada.applet.schedule;

import java.awt.Point;
import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.SwingPropertyChangeSupport;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.DataValueChangeListener;

import org.F11.scada.applet.dialog.WifeDialog;
import org.F11.scada.applet.symbol.SymbolCollection;
import org.F11.scada.applet.symbol.ValueSetter;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.util.AttributesUtil;
import org.apache.log4j.Logger;

/**
 * スケジュールモデルの実装クラスです。
 */
public class DefaultScheduleModel implements ScheduleModel,
		DataReferencerOwner, DataValueChangeListener {
	/** DataHolderタイプ情報です。 */
	private static final Class[][] WIFE_TYPE_INFO =
		new Class[][] { { DataHolder.class, WifeData.class } };
	/** バウンズプロパティです。 */
	private SwingPropertyChangeSupport changeSupport;
	/** スケジュールデータです */
	private WifeDataSchedule dataSchedule;
	/** 取消処理用スケジュールデータです */
	private WifeDataSchedule undoDataSchedule;
	/** データリファレンサのリストです */
	private List dataReferencerList;
	/** 参照中のリファレンサインデックスです */
	private int referencerIndex;
	/** 編集可能フラグの配列です */
	private boolean[] isEditable;
	/** 認証コントロールの参照 */
	private Authenticationable authentication;
	/** 変更フラグデータリファレンサのリストです */
	private List flagReferencerList;

	/** スケジュールモデル変更イベント名 */
	private static final String SCHEDULE_MODEL_CHANGE = "SCHEDULE_MODEL_CHANGE";
	/** 変更フラグが不要な場合のダミーリファレンサ */
	private static final DataReferencer NON_FLAG = new DataReferencer("", "");
	/** Logging API */
	private final Logger logger = Logger.getLogger(DefaultScheduleModel.class);

	/**
	 * コンストラクタ
	 */
	public DefaultScheduleModel() {
		this(null);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param authentication 認証コントロールの参照
	 */
	public DefaultScheduleModel(Authenticationable authentication) {
		this.authentication = authentication;
		init();
	}

	private void init() {
		dataReferencerList = new ArrayList();
		flagReferencerList = new ArrayList();
		referencerIndex = -1;
		if (authentication != null) {
			authentication.addEditable(this);
		}
	}

	/**
	 * データ変更イベント処理
	 */
	public void dataValueChanged(DataValueChangeEvent evt) {
		Object o = evt.getSource();
		if (!(o instanceof DataHolder)) {
			return;
		}

		if (0 <= referencerIndex) {
			if (!isEditing()) {
				setGroupNo(referencerIndex);
			}
		}
	}

	/**
	 * DataHolderタイプ情報を返します。
	 */
	public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
		return WIFE_TYPE_INFO;
	}

	/**
	 * オブジェクトに値を設定します。
	 * 
	 * @exception RemoteException
	 */
	public void setValue() {
		writeSchedule();
		writeFlag();
	}

	private void writeSchedule() {
		DataReferencer rf =
			(DataReferencer) dataReferencerList.get(referencerIndex);
		DataHolder dh = rf.getDataHolder();
		dh.setValue(dataSchedule, new Date(), WifeQualityFlag.GOOD);
		try {
			dh.syncWrite();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void writeFlag() {
		DataReferencer rf =
			(DataReferencer) flagReferencerList.get(referencerIndex);
		if (rf != NON_FLAG) {
			DataHolder dh = rf.getDataHolder();
			WifeDataDigital d = (WifeDataDigital) dh.getValue();
			dh.setValue(d.valueOf(true), new Date(), WifeQualityFlag.GOOD);
			try {
				dh.syncWrite();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * データが編集された場合に true を返します。
	 */
	public boolean isEditing() {
		return !dataSchedule.equals(undoDataSchedule);
	}

	/**
	 * 保持しているスケジュールデータを更新します。
	 */
	public void writeData() {
		undoDataSchedule =
			(WifeDataSchedule) dataSchedule.valueOf(dataSchedule.toByteArray());
	}

	/**
	 * 保持しているスケジュールデータをアンドゥします。
	 */
	public void undoData() {
		dataSchedule =
			(WifeDataSchedule) undoDataSchedule.valueOf(undoDataSchedule
				.toByteArray());
		firePropertyChange(null, null);
	}

	/**
	 * スケジュールデータのグループを指定したインデックスに変更します。
	 * 
	 * @param index グループインデックス
	 */
	public void setGroupNo(int index) {
		DataReferencer rf = (DataReferencer) dataReferencerList.get(index);
		DataHolder dh = rf.getDataHolder();
		if (dh == null) {
			logger.error(rf.getDataProviderName()
				+ "_"
				+ rf.getDataHolderName()
				+ "が登録されていません。");
		}
		WifeDataSchedule schedule = (WifeDataSchedule) dh.getValue();
		undoDataSchedule =
			(WifeDataSchedule) schedule.valueOf(schedule.toByteArray());
		dataSchedule =
			(WifeDataSchedule) schedule.valueOf(schedule.toByteArray());
		referencerIndex = index;
		firePropertyChange(null, null);
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
		return dataReferencerList.size();
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
		// return dataSchedule.getGroupNo();
		return referencerIndex;
	}

	/**
	 * グループを追加します。
	 * 
	 * @param dataProvider
	 * @param dataHolder
	 */
	private void addGroup(String dataProvider, String dataHolder) {
		DataReferencer dr = new DataReferencer(dataProvider, dataHolder);
		dataReferencerList.add(dr);
		dr.connect(this);
		if (referencerIndex < 0) {
			setGroupNo(0);
		}
	}

	public void addGroup(
			String dataProvider,
			String dataHolder,
			String flagHolder) {
		addGroup(dataProvider, dataHolder);
		if (AttributesUtil.isSpaceOrNull(flagHolder)) {
			flagReferencerList.add(NON_FLAG);
		} else {
			int index = flagHolder.indexOf("_");
			String provider = flagHolder.substring(0, index);
			String holder = flagHolder.substring(index + 1);
			DataReferencer dr = new DataReferencer(provider, holder);
			flagReferencerList.add(dr);
			dr.connect(this);
		}
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
			java.util.List para) {
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
		isEditable = editable;
	}

	/**
	 * このシンボルが編集可能かどうかを返します。
	 * 
	 * @return 編集可能な場合は true をそうでなければ false を返します。
	 */
	public boolean isEditable() {
		if (isEditable != null) {
			return isEditable[referencerIndex];
		}
		return false;
	}

	/*
	 * @see org.F11.scada.applet.symbol.Editable#getDestinations()
	 */
	public String[] getDestinations() {
		String[] ret = new String[dataReferencerList.size()];
		for (int i = 0; i < ret.length; i++) {
			DataReferencer dr = (DataReferencer) dataReferencerList.get(i);
			StringBuffer buffer = new StringBuffer();
			buffer.append(dr.getDataProviderName());
			buffer.append("_");
			buffer.append(dr.getDataHolderName());
			ret[i] = buffer.toString();
		}
		return ret;
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
		for (Iterator i = dataReferencerList.iterator(); i.hasNext();) {
			DataReferencer dr = (DataReferencer) i.next();
			dr.disconnect(this);
		}
		dataReferencerList.clear();

		for (Iterator i = flagReferencerList.iterator(); i.hasNext();) {
			DataReferencer dr = (DataReferencer) i.next();
			dr.disconnect(this);
		}
		flagReferencerList.clear();

		removePropertyChangeListeners();
		if (null != authentication) {
			authentication.removeEditable(this);
		}
	}

	public void removePropertyChangeListeners() {
		if (changeSupport != null) {
			PropertyChangeListener[] listeners =
				changeSupport.getPropertyChangeListeners();
			for (int i = 0; i < listeners.length; i++) {
				changeSupport.removePropertyChangeListener(listeners[i]);
			}
		}
	}

	public void duplicateGroup(int[] dest) {
		try {
			for (int i = 0; i < dest.length; i++) {
				DataReferencer ref =
					(DataReferencer) dataReferencerList.get(dest[i]);
				DataHolder dh = ref.getDataHolder();
				if (dh == null) {
					throw new NullPointerException(
						"not registered dataholder : "
							+ ref.getDataProviderName()
							+ "_"
							+ ref.getDataHolderName());
				}
				WifeDataSchedule src = (WifeDataSchedule) dh.getValue();
				dh.setValue(
					dataSchedule.duplicate(src),
					new Date(),
					WifeQualityFlag.GOOD);
				dh.syncWrite();
			}
		} catch (DataProviderDoesNotSupportException e) {
			e.printStackTrace();
		}
	}

	public void duplicateWeekOfDay(int src, int[] dest) {
		int maxNumber = dataSchedule.getNumberSize();
		int[] onTimes = new int[maxNumber];
		int[] offTimes = new int[maxNumber];
		for (int i = 0; i < maxNumber; i++) {
			onTimes[i] = dataSchedule.getOnTime(src, i);
			offTimes[i] = dataSchedule.getOffTime(src, i);
		}
		for (int i = 0; i < dest.length; i++) {
			for (int j = 0; j < maxNumber; j++) {
				dataSchedule = dataSchedule.setOnTime(dest[i], j, onTimes[j]);
				dataSchedule = dataSchedule.setOffTime(dest[i], j, offTimes[j]);
			}
		}
		setValue();
		firePropertyChange(null, null);
	}

	public GroupElement[] getGroupNames() {
		ArrayList ret = new ArrayList(dataReferencerList.size());
		for (int i = 0, size = dataReferencerList.size(); i < size; i++) {
			if (isEditable[i] && referencerIndex != i) {
				DataReferencer dr = (DataReferencer) dataReferencerList.get(i);
				DataHolder dh = dr.getDataHolder();
				WifeDataSchedule data = (WifeDataSchedule) dh.getValue();
				ret.add(new GroupElement(i, data.getGroupName()));
			}
		}
		return (GroupElement[]) ret.toArray(new GroupElement[0]);
	}

	public String toString() {
		return "referencerIndex="
			+ referencerIndex
			+ " ,dataSchedule="
			+ dataSchedule.toString();
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
	
	public boolean isVisible() {
		return true;
	}
}
