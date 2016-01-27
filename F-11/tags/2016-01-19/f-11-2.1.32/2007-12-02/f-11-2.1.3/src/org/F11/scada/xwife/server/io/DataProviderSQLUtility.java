package org.F11.scada.xwife.server.io;

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

import org.apache.log4j.Logger;

/**
 * データプロバイダを生成する際に使用する SQL ユーティリティークラスです。
 */
public final class DataProviderSQLUtility {
	private static Logger logger = Logger.getLogger(DataProviderSQLUtility.class);

	private DataProviderSQLUtility() {}

	/**
	 * 指定プロバイダの全サマリーを返す SQL を返します。
	 * @param deviceID プロバイダ名
	 * @return SQL 文字列
	 */
	public static String createAllAlarmSQL(String deviceID) {
		StringBuffer buffer = new StringBuffer();

		buffer.append("SELECT i.point, i.provider, i.holder, i.com_cycle, i.com_cycle_mode, i.com_memory_kinds,");
		buffer.append(" i.com_memory_address, s.bit_value, i.data_argv, i.off_delay");
		buffer.append(" FROM item_table as i, summary_table as s");
		buffer.append(" WHERE i.point=s.point AND i.provider=s.provider AND i.holder=s.holder AND");
		buffer.append(" i.provider='");
		buffer.append(deviceID).append("'");
		buffer.append(" AND i.data_type=0");

		logger.debug(buffer.toString());

		return buffer.toString();
	}

	/**
	 * サマリーに登録されていないアイテムを返す SQL を返します。
	 * @param deviceID プロバイダ名
	 * @return SQL 文字列
	 */
	public static String createAlarmSQL(String deviceID) {
		StringBuffer buffer = new StringBuffer();
/*
		buffer.append("SELECT point, provider, holder, com_cycle, com_cycle_mode, com_memory_kinds, com_memory_address, data_argv");
		buffer.append(" FROM item_table WHERE provider='").append(deviceID).append("'");
		buffer.append(" AND data_type=0");
		buffer.append(" EXCEPT");
		buffer.append(" SELECT i.point, i.provider, i.holder, i.com_cycle, i.com_cycle_mode, i.com_memory_kinds, i.com_memory_address, i.data_argv");
		buffer.append(" FROM item_table as i, summary_table as s WHERE i.point=s.point AND i.provider=s.provider AND i.holder=s.holder AND i.provider='");
		buffer.append(deviceID).append("'");
		buffer.append(" AND i.data_type=0");
*/

		buffer.append("SELECT i.point, i.provider, i.holder, i.com_cycle, i.com_cycle_mode, ");
		buffer.append("i.com_memory_kinds, i.com_memory_address, i.data_argv, i.off_delay ");
		buffer.append("FROM item_table as i LEFT OUTER JOIN summary_table as s ON ");
		buffer.append("(i.point = s.point AND i.provider = s.provider AND i.holder = s.holder) ");
		buffer.append("WHERE i.provider = '");
		buffer.append(deviceID);
		buffer.append("' AND i.data_type = 0 AND s.point IS NULL AND ");
		buffer.append("s.provider IS NULL AND s.holder IS NULL");
		logger.debug(buffer.toString());

		return buffer.toString();
	}

	/**
	 * アナログホルダ生成時に使用するSQLを返します。
	 * @param deviceID プロバイダ名
	 * @return SQL 文字列
	 */
	public static String createAnalogSQL(String deviceID) {
		StringBuffer buffer = new StringBuffer();

		buffer.append("SELECT i.point, i.provider, i.holder, i.com_cycle, i.com_cycle_mode, ");
		buffer.append("i.com_memory_kinds, i.com_memory_address, i.data_type, i.data_argv, ");
		buffer.append("t.convert_min, t.convert_max, t.input_min, t.input_max, t.format, t.convert_type ");
		buffer.append("FROM item_table AS i, analog_type_table AS t ");
		buffer.append("WHERE i.provider='").append(deviceID).append("' AND i.data_type BETWEEN 1 AND 14 ");
		buffer.append("AND i.analog_type_id = t.analog_type_id");

		logger.debug(buffer.toString());

		return buffer.toString();
	}

	public static String createSQL() {
		String sql = "SELECT i.point, i.provider, i.holder, i.com_cycle, i.com_cycle_mode, " +
		"i.com_memory_kinds, i.com_memory_address, i.data_type, i.data_argv, " +
		"t.convert_min, t.convert_max, t.input_min, t.input_max, t.format, " +
		"p.unit, p.name, p.unit_mark " +
		"FROM item_table AS i, analog_type_table AS t, point_table AS p " +
		"WHERE i.provider = ? AND i.data_type = ? "+	
		"AND i.analog_type_id = t.analog_type_id AND p.point = i.point";

		logger.debug(sql);
		
		return sql;
	}

	/**
	 * アナログ４ホルダ生成時に使用するSQLを返します。
	 * @param deviceID プロバイダ名
	 * @return SQL 文字列
	 */
	public static String createAnalog4SQL(String deviceID) {
		StringBuffer buffer = new StringBuffer();

		buffer.append("SELECT i.point, i.provider, i.holder, i.com_cycle, i.com_cycle_mode, ");
		buffer.append("i.com_memory_kinds, i.com_memory_address, i.data_type, i.data_argv, ");
		buffer.append("t.convert_min, t.convert_max, t.input_min, t.input_max, t.format, t.convert_type ");
		buffer.append("FROM item_table AS i, analog_type_table AS t ");
		buffer.append("WHERE i.provider='").append(deviceID).append("' AND i.data_type BETWEEN 18 AND 20 ");
		buffer.append("AND i.analog_type_id = t.analog_type_id");

		logger.debug(buffer.toString());

		return buffer.toString();
	}
}
