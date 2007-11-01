package org.F11.scada.applet.schedule;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

/**
 * スケジュールクラスを生成する抽象クラスです。 スケジュールビューのバリエーションは、このクラスを継承したファクトリクラスを作成してください。
 * AbstractFactory Pattern を使用しています。
 * 
 * @todo スケジュールのグループ化と、機器別スケジュールの対応。
 */
public abstract class ScheduleFactory {
	/** スケジュールモデルの参照です */
	protected ScheduleModel scheduleModel;

	private static Logger logger = Logger.getLogger(ScheduleFactory.class);

	/**
	 * コンストラクタ
	 * 
	 * @see getFactory
	 * @param alarmRef リモートデータオブジェクト
	 */
	public ScheduleFactory(ScheduleModel scheduleModel) {
		this.scheduleModel = scheduleModel;
	}

	/**
	 * スタティック・ファクトリメソッドです。 このメソッドを使用して、スケジュールオブジェクトのインスタンスを生成します。
	 * 
	 * @param alarmRef リモートデータオブジェクト
	 * @param viewClass 使用するビュークラス名
	 * @param isSort 時間ソートの有無
	 * @param isNonTandT 今日・明日ビューの有無
	 * 
	 */
	public static ScheduleFactory getFactory(
			ScheduleModel scheduleModel,
			String viewClass,
			boolean isSort,
			boolean isNonTandT,
			String pageId,
			boolean isLenient) {
		ScheduleFactory factory = null;
		String realClassName = "org.F11.scada.applet.schedule." + viewClass;
		logger.debug("Class Name : " + realClassName);

		try {
			Class factoryClass = Class.forName(realClassName);
			Class[] param = { ScheduleModel.class, Boolean.TYPE, Boolean.TYPE,
					String.class, Boolean.TYPE };
			Constructor constructor = factoryClass.getConstructor(param);
			factory = (ScheduleFactory) constructor.newInstance(new Object[] {
					scheduleModel, Boolean.valueOf(isSort),
					Boolean.valueOf(isNonTandT), pageId,
					Boolean.valueOf(isLenient) });
		} catch (ClassNotFoundException ex) {
			logger.warn("class not found = " + viewClass);
		} catch (InvocationTargetException ex2) {
			ex2.getTargetException().printStackTrace();
			// ex2.printStackTrace();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return factory;
	}

	/**
	 * ビューを含んだコンポーネントを返す抽象メソッドです。 サブクラスで実装します。
	 * 
	 * @return ビューを含んだコンポーネント(通常はスクロールペインが返されます)
	 */
	public abstract JComponent createView();

	/**
	 * ツールバーを含んだコンポーネントを返す抽象メソッドです。 サブクラスで実装します。
	 * 
	 * @return ツールバーを含んだコンポーネント
	 */
	public abstract JComponent createToolBar();
}
