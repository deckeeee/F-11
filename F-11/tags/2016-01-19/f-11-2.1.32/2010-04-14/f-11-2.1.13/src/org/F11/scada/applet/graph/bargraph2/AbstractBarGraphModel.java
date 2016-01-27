/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.applet.graph.bargraph2;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.server.io.BarGraph2ValueListHandler;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.util.ThreadUtil;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;

/**
 * BarGraphModelの基底クラスです
 */
public abstract class AbstractBarGraphModel implements BarGraphModel, Runnable {
	/** ロギングAPI */
	private static Logger logger = Logger.getLogger(AbstractBarGraphModel.class);
	private static final PointProperty nullPoint = new PointProperty();
	/** 内部データ更新周期 */
	private static final long SLEEP_TIME = 30000L;
	/** スレッドオブジェクト */
	private Thread thread;

	/** バーグラフのベースとなるコンポーネント */
	private JLabel label = new JLabel();
	/** toString()の返り値 */
	private String labelString;
	/** データハンドラマネージャー */
	private BarGraph2ValueListHandler valueListHandler;

	/** 自身のインデックス */
	private int barGraphModelIndex;
	/** 横スケール１単位中のバーの本数 */
	private int barCount = 1;
	/** 参照するテーブル名の配列 */
	private String handlerName;
	/** バーを描画するコンポーネント */
	protected BarGraph2View view;
	/** 縦スケールを管理するクラス */
	private VerticallyScaleModel vertical;
	/** 単位描画シンボルのリスト */
	private List<TextUnitsSymbol> unitsList = new ArrayList<TextUnitsSymbol>();
	/** 参照中のポイント情報 */
	protected PointProperty refPoint;
	/** データ先頭日時 */
	protected Date firstLoggingDate = new Date();
	/** データ末尾日時 */
	private Date lastLoggingDate = new Date();
	/** グラフデータマトリックス */
	private BarData[][] dataMatrix;

	protected AbstractBarGraphModel() throws RemoteException {
		Exception serverError = null;
		for (int i = 0; i < Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				lookup();
				serverError = null;
				break;
			} catch (Exception e) {
				serverError = e;
				ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				continue;
			}
		}
		if (serverError != null) {
			throw ServerErrorUtil.createException(serverError);
		}
	}

	/**
	 * ロギングデータ読み込み
	 */
	abstract protected BarData[][] loadLoggingData() throws RemoteException;
	/**
	 * 現在値を設定
	 */
	abstract protected void setNowValue(BarData[][] dataMatrix);
	/**
	 * 最新描画モードを取得
	 */
	abstract protected boolean isRealTimeMode();

	/**
	 * データを読み込んでviewに通知する。
	 */
	protected synchronized void fireViewChangeData() throws RemoteException {
		dataMatrix = loadLoggingData();
		setNowValue(dataMatrix);
		view.changeData(dataMatrix);
	}

	/**
	 * バーグラフコンポーネントを返します。
	 * @return
	 */
	public JComponent getJComponent() {
		return label;
	}

	/**
	 * 表示ポイントを変更します。
	 * @param series
	 */
	public synchronized void changePoint(BarSeries series)
			throws RemoteException {
		// 参照中のポイントの設定
		refPoint = series.getPointProperty(barGraphModelIndex);

		// ホルダ検索
		DataProvider provider = Manager.getInstance().getDataProvider(
				refPoint.getProviderName());
		DataHolder holder = provider.getDataHolder(refPoint.getHolderName());
		if (holder == null) {
			logger.error("holder is null [" + refPoint.getHolderName() + "]");
			// スケールクリア
			vertical.changePoint(nullPoint, "0.0");
			// viewクリア
			view.changeData(null);
			// 単位クリア
			for (TextUnitsSymbol symbol : unitsList) {
				symbol.setUnits("");
				symbol.updateProperty();
			}
			return;
		}
		ConvertValue converter = (ConvertValue) holder.getParameter(WifeDataProvider.PARA_NAME_CONVERT);

		// スケール設定
		vertical.changePoint(refPoint, converter.getPattern());

		// viewへ設定
		view.changePattern(converter.getPattern());
		fireViewChangeData();

		// 単位
		for (TextUnitsSymbol symbol : unitsList) {
			symbol.setUnits(series.getUnit_mark());
			symbol.updateProperty();
		}
	}
	/**
	 * 横スケール１単位中のバーの本数を返します。
	 * @return
	 */
	public int getBarCount() {
		return barCount;
	}
	/**
	 * 表示データのマトリックスを返します。
	 * @return
	 */
	public BarData[][] getDataMatrix() {
		return dataMatrix;
	}

	/**
	 * 自身のインデックスを設定します。
	 * @param barIndex
	 */
	public void setBarGraphModelIndex(int barIndex) {
		barGraphModelIndex = barIndex;
	}
	/**
	 * toString()の返り値を設定します。
	 * @param text
	 */
	public void setText(String text) {
		this.labelString = text;
	}
	/**
	 * コンポーネントのバックグラウンドイメージを設定します。
	 * @param icon
	 */
	public void setIcon(Icon icon) {
		this.label.setIcon(icon);
	}
	/**
	 * 参照するテーブル名を設定します。
	 * @param handlerNames
	 */
	public void setHandlerNames(String[] handlerNames) {
		this.handlerName = handlerNames[0];
	}
	/**
	 * 横スケール１単位中のバーの本数を設定します。
	 * @param count
	 */
	public void setBarCount(int count) {
		this.barCount = count;
	}
	/**
	 * バーを描画するコンポーネントを設定します。
	 * @param view
	 */
	public void setBarGraph2View(BarGraph2View view) {
		this.view = view;
		label.add(view);
		// どっちが後でもリスナー登録
		if (this.view != null && this.vertical != null)
			this.vertical.addScaleChangeListener(this.view);
	}
	/**
	 * 縦スケールを管理するクラスを追加します。
	 * @param symbol
	 */
	public void setVerticallyScaleModel(VerticallyScaleModel vertical) {
		this.vertical = vertical;
		this.vertical.setParent(label);
		// どっちが後でもリスナー登録
		if (this.view != null && this.vertical != null)
			this.vertical.addScaleChangeListener(this.view);
	}
	/**
	 * コンポーネントに単位表示用テキストシンボルを追加します。
	 * @param symbol
	 */
	public void addUnitsSymbol(TextUnitsSymbol symbol) {
		unitsList.add(symbol);
		label.add(symbol);
	}

	/**
	 * 
	 */
	public String toString() {
		return labelString;
	}

	protected SortedMap<Timestamp, DoubleList> loadRmi(
			List<HolderString> holderStrings, Date first, Date last)
			throws RemoteException {
		SortedMap<Timestamp, DoubleList> result = new TreeMap<Timestamp, DoubleList>();
		Exception serverError = null;
		for (int i = 0; i < Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				result = valueListHandler.getLoggingData(handlerName,
						holderStrings, first, last);
				firstLoggingDate = valueListHandler.getFirstDateTime(
						handlerName, holderStrings);
				lastLoggingDate = valueListHandler.getLastDateTime(handlerName,
						holderStrings);
				serverError = null;
				break;
			} catch (Exception e) {
				try {
					ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				serverError = e;
				continue;
			}
		}
		if (serverError != null) {
			throw ServerErrorUtil.createException(serverError);
		}
		return result;
	}

	protected Date loadRmiLastDateTime(List<HolderString> holderStrings)
			throws RemoteException {
		Date result = new Date(0);
		Exception serverError = null;
		for (int i = 0; i < Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				result = valueListHandler.getLastDateTime(handlerName,
						holderStrings);
				serverError = null;
				break;
			} catch (Exception e) {
				try {
					ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				serverError = e;
				continue;
			}
		}
		if (serverError != null) {
			throw ServerErrorUtil.createException(serverError);
		}
		return result;
	}

	private void lookup() throws Exception {
		logger.info("lookup ValueListHandler:"
				+ WifeUtilities.createRmiBarGraph2DataValueListHandlerManager());
		valueListHandler = (BarGraph2ValueListHandler) Naming.lookup(WifeUtilities.createRmiBarGraph2DataValueListHandlerManager());
	}

	public void run() {
		Thread ct = Thread.currentThread();

		while (ct == thread) {
			try {
				synchronized (this) {
					if (isRealTimeMode()) {
						List<HolderString> holderStrings = new ArrayList<HolderString>();
						holderStrings.add(new HolderString(
								refPoint.getProviderName(),
								refPoint.getHolderName()));
						Date lastTime = loadRmiLastDateTime(holderStrings);
						if (logger.isDebugEnabled()) {
							SimpleDateFormat sf = new SimpleDateFormat(
									"yyyy/MM/dd HH:mm");
							logger.debug("check "
									+ sf.format(lastLoggingDate.getTime())
									+ " < " + sf.format(lastTime.getTime()));
						}
						if (lastLoggingDate.getTime() < lastTime.getTime()) {
							changePeriod(0);
						} else {
							setNowValue(dataMatrix);
							view.changeData(dataMatrix);
						}
					}
				}
			} catch (RemoteException e) {
			}
			ThreadUtil.sleep(SLEEP_TIME);
		}
	}
	public synchronized void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.setName(getClass().getSimpleName());
			thread.start();
		}
	}

	public synchronized void stop() {
		if (thread != null) {
			Thread ot = thread;
			thread = null;
			ot.interrupt();
		}
	}

}
