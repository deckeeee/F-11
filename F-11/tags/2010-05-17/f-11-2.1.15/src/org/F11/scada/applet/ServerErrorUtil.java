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

package org.F11.scada.applet;

import java.rmi.RemoteException;
import java.sql.Timestamp;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.parser.alarm.AlarmDefine;
import org.F11.scada.server.alarm.table.AlarmTableModel;
import org.F11.scada.xwife.server.AlarmDataProvider;
import org.apache.log4j.Logger;

/**
 * サーバーエラー処理を記述したユーティリティークラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class ServerErrorUtil {
	/** commons logging API */
	private static Logger log = Logger.getLogger(ServerErrorUtil.class);
	/** サーバーエラー処理 */
	private static final String EXCEPTION_MESSAGE = "Server error.";
	/** サーバーコネクションエラーメッセージ */
	public static String ERROR_MESSAGE = "サーバーコネクションエラー";
	/** サーバーコネクションエラー 音 */
	public static String ERROR_SOUND = "";

	static {
		try {
			AlarmDefine ad = new AlarmDefine("/resources/AlarmDefine.xml");
			String msg = ad.getAlarmConfig().getServerErrorMessage().getMessage();
			String sound = ad.getAlarmConfig().getServerErrorMessage().getSound();
			if (msg != null) {
				ERROR_MESSAGE = msg;
				if (sound != null) {
					ERROR_SOUND = sound;
				}

			} else {
				log.info("サーバーコネクションエラーが定義されていません、「" + ERROR_MESSAGE + "」を使用します");
			}
		} catch (Exception e) {
			log.info("サーバーコネクションエラーが定義されていません、「" + ERROR_MESSAGE + "」を使用します");
            log.info("Exception caught: ", e);
		}
	}

	private static Object[] CAREER_MESSAGE =
		{"", Boolean.FALSE, "0", "RED", "", "", "", new Integer(0), "", "", "", Boolean.TRUE, "TIMESTAMP", "", ERROR_MESSAGE, "", "発生"};
	private static Object[] HISTORY_MESSAGE =
		{"", Boolean.FALSE, "0", "RED", "", "", "", "TIMESTAMP", null, "", ERROR_MESSAGE, "", "", "", "", "", "────"};
	private static Object[] SUMMARY_MESSAGE =
		{"", Boolean.FALSE, "0", "RED", "", "", "", "TIMESTAMP", null, "", ERROR_MESSAGE, "", "発生"};

	private static Object[] CAREER_MESSAGE_OFF =
		{"", Boolean.FALSE, "0", "BLACK", "", "", "", new Integer(0), "", "", "",  Boolean.FALSE, "TIMESTAMP", "", ERROR_MESSAGE, "", "復旧"};
	//ジャンプパス,自動ジャンプ,優先順位,表示色,point,provider,holder,サウンドタイプ,サウンドパス,Emailグループ,Emailモード,onoff,日時,記号,名称,警報・状態
//	private static Object[] HISTORY_MESSAGE_OFF =
//		{"", Boolean.FALSE, "0", "BLACK", "", "", "", "TIMESTAMP", null, "", ERROR_MESSAGE, "────"};
//	private static Object[] SUMMARY_MESSAGE_OFF =
//		{"", Boolean.FALSE, "0", "BLACK", "", "", "", "TIMESTAMP", null, "", ERROR_MESSAGE, "復旧"};

	/**
	 * サーバーコネクションエラーを発生します。
	 */
	public static void invokeServerError() {
		Timestamp now = new Timestamp(System.currentTimeMillis());

		AlarmTableModel model = getAlarmTableModel(AlarmDataProvider.CAREER);
		CAREER_MESSAGE[12] = now;
		if (0 < ERROR_SOUND.length()) {
			CAREER_MESSAGE[7] = new Integer(1);
			CAREER_MESSAGE[8] = ERROR_SOUND;
		}
		model.insertRow(0, CAREER_MESSAGE);

		model = getAlarmTableModel(AlarmDataProvider.HISTORY);
		HISTORY_MESSAGE[7] = now;
		model.insertRow(0, HISTORY_MESSAGE);

		model = getAlarmTableModel(AlarmDataProvider.SUMMARY);
		SUMMARY_MESSAGE[7] = now;
		int row = searchRow(model, ERROR_MESSAGE, 10, false);
		if (row < 0) {
			model.insertRow(0, SUMMARY_MESSAGE);
		} else {
			model.setValueAt(now, row, 7);
			model.setValueAt(null, row, 8);
			model.setValueAt("発生", row, 11);
			model.setValueAt("RED", row, 3);
		}
	}

	private static AlarmTableModel getAlarmTableModel(String tableName) {
		DataProvider dataProvider =
			Manager.getInstance().getDataProvider(AlarmDataProvider.PROVIDER_NAME);
		DataHolder dh = dataProvider.getDataHolder(tableName);
		AlarmTableModel model = (AlarmTableModel) dh.getValue();
		return model;
	}

	public static void display() {
		JTabbedPane tab = new JTabbedPane();
		tab.addTab("履歴", new JScrollPane(new JTable(getAlarmTableModel(AlarmDataProvider.CAREER))));
		tab.addTab("ヒストリ", new JScrollPane(new JTable(getAlarmTableModel(AlarmDataProvider.HISTORY))));
		tab.addTab("サマリー", new JScrollPane(new JTable(getAlarmTableModel(AlarmDataProvider.SUMMARY))));
		JFrame frame = new JFrame();
		frame.getContentPane().add(tab);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * RemoteException 例外を生成します。
	 * @param curse 元になる例外
	 * @return RemoteException
	 */
	public static RemoteException createException(Exception curse) {
		return new RemoteException(EXCEPTION_MESSAGE, curse);
	}

	/**
	 * サーバーコネクションエラーを復旧します。
	 */
	public static void invokeServerRepair() {
		Timestamp now = new Timestamp(System.currentTimeMillis());

		AlarmTableModel model = getAlarmTableModel(AlarmDataProvider.CAREER);
		CAREER_MESSAGE_OFF[12] = now;
		model.insertRow(0, CAREER_MESSAGE_OFF);

		model = getAlarmTableModel(AlarmDataProvider.HISTORY);
		int row = searchRow(model, ERROR_MESSAGE, 10, true);
		if (row >= 0) {
			model.setValueAt(now, row, 8);
			model.setValueAt("BLACK", row, 3);
		}

		model = getAlarmTableModel(AlarmDataProvider.SUMMARY);
		row = searchRow(model, ERROR_MESSAGE, 10, true);
		if (row >= 0) {
			model.setValueAt(now, row, 8);
			model.setValueAt("復旧", row, 12);
			model.setValueAt("BLACK", row, 3);
		}
	}

	private static int searchRow(AlarmTableModel model, String msg, int column, boolean repair) {
		for (int i = (model.getRowCount() - 1); i >= 0; i--) {
			String s = (String) model.getValueAt(i, column);
			Timestamp t = (Timestamp) model.getValueAt(i, 8);
			if (repair) {
				if (ERROR_MESSAGE.equals(s) && t == null) {
					return i;
				}
			} else {
				if (ERROR_MESSAGE.equals(s) && t != null) {
					return i;
				}
			}
		}
		return -1;
	}
}
