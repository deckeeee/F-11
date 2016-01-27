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

package org.F11.scada.server.register;

import java.util.StringTokenizer;

import org.F11.scada.data.SevenDaysMainSchedulePattern;
import org.F11.scada.data.SevenDaysSchedulePattern;
import org.F11.scada.data.StringData;
import org.F11.scada.data.TwoDaysSchedulePattern;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataAnalog4;
import org.F11.scada.data.WifeDataCalendar;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.data.WifeDataTimestamp;
import org.F11.scada.server.entity.Item;

/**
 * Item オブジェクトと WifeData の変換ユーティリティークラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class WifeDataUtil {
	private WifeDataUtil() {
	}

	/**
	 * Item オブジェクトより、使用する WifeData のインスタンスを返します。
	 * 
	 * @param item Itemオブジェクト
	 * @return 引数のItemオブジェクトが使用するWifeDataのインスタンス。
	 */
	public static WifeData getWifeData(Item item) {
		switch (item.getDataType()) {
		case 0:
			// Wi-Fi 用デジタルタグ
		case 21:
			int bitNo = Integer.parseInt(item.getDataArgv());
			if (item.getSummary() != null) {
				return WifeDataDigital.valueOf(
						item.getSummary().isBitValue(),
						bitNo);
			} else {
				return WifeDataDigital.valueOfFalse(bitNo);
			}
		case 1:
			return WifeDataAnalog.valueOfBcdZeroHalf(0);
		case 2:
			return WifeDataAnalog.valueOfBcdOneHalf(0);
		case 3:
			return WifeDataAnalog.valueOfBcdSingle(0);
		case 4:
			return WifeDataAnalog.valueOfBcdDouble(0);
		case 5:
			return WifeDataAnalog.valueOfHexZeroHalf(0);
		case 6:
			return WifeDataAnalog.valueOfHexOneHalf(0);
		case 7:
			return WifeDataAnalog.valueOfHexSingle(0);
		case 8:
			return WifeDataAnalog.valueOfHexDouble(0);
		case 9:
			return WifeDataAnalog.valueOfShxZeroHalf(0);
		case 10:
			return WifeDataAnalog.valueOfShxOneHalf(0);
		case 11:
			return WifeDataAnalog.valueOfShxSingle(0);
		case 12:
			return WifeDataAnalog.valueOfShxDouble(0);
		case 13:
			return WifeDataAnalog.valueOfFloat(0);
		case 14:
			return WifeDataAnalog.valueOfDouble(0);
		case 15:
			return WifeDataCalendar.valueOf(Integer
					.parseInt(item.getDataArgv()));
		case 16:
			return getScheduleData(item);
		case 17:
			return WifeDataTimestamp.valueOfType1(0);
		case 18:
			return WifeDataAnalog4
					.valueOfBcdSingle(new double[] { 0, 0, 0, 0 });
		case 19:
			return WifeDataAnalog4
					.valueOfHexSingle(new double[] { 0, 0, 0, 0 });
		case 20:
			return WifeDataAnalog4
					.valueOfShxSingle(new double[] { 0, 0, 0, 0 });
		case 22:
			return StringData.valueOf(Integer.parseInt(item.getDataArgv()));
		default:
			throw new IllegalArgumentException("datatype : "
					+ item.getDataType());
		}
	}

	private static WifeData getScheduleData(Item item) {
		String argv = item.getDataArgv();
		checkArgv(argv);
		StringTokenizer tokenizer = new StringTokenizer(argv, ",");
		String firstToken = tokenizer.nextToken().trim();
		WifeData wd;
		if ("TwoDays".equalsIgnoreCase(firstToken)) {
			int arg = Integer.parseInt(tokenizer.nextToken().trim());
			wd = WifeDataSchedule.valueOf(new TwoDaysSchedulePattern(), arg);
		} else if ("SevenDays".equalsIgnoreCase(firstToken)) {
			int arg = Integer.parseInt(tokenizer.nextToken().trim());
			wd = WifeDataSchedule.valueOf(new SevenDaysSchedulePattern(), arg);
		} else if ("SevenMain".equalsIgnoreCase(firstToken)) {
			int spacialDayCount = Integer
					.parseInt(tokenizer.nextToken().trim());
			int number = Integer.parseInt(tokenizer.nextToken().trim());
			String groupName = item.get_point().getName();
			wd = WifeDataSchedule.valueOf(new SevenDaysMainSchedulePattern(
					spacialDayCount), number, groupName);
		} else {
			int special = Integer.parseInt(firstToken);
			int number = Integer.parseInt(tokenizer.nextToken().trim());
			String groupName = item.get_point().getName();
			wd = WifeDataSchedule.valueOf(special, number, groupName);
		}
		return wd;
	}

	private static void checkArgv(String argv) {
		StringTokenizer tokenizer = new StringTokenizer(argv, ",");
		String firstToken = tokenizer.nextToken().trim();
		if ("SevenMain".equalsIgnoreCase(firstToken)) {
			if (tokenizer.countTokens() != 2) {
				throw new IllegalArgumentException(
						"data_argv field need 'SevenMain, <int>, <int>");
			} else {
				return;
			}
		}

		tokenizer = new StringTokenizer(argv, ",");
		if (tokenizer.countTokens() != 2) {
			throw new IllegalArgumentException(
					"data_argv field need `<int>, <int>' or 'TwoDays, <int> or 'SevenDays, <int>'.");
		}
	}
}
