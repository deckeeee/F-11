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
package org.F11.scada.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.BitSet;

import org.F11.scada.WifeUtilities;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class BooleanUtil {
	private BooleanUtil() {
	}

	public static boolean isBoolean(ResultSet rs) throws SQLException {
		ResultSetMetaData rsMeta = rs.getMetaData();
		int type = rsMeta.getColumnType(1);
		String flagStr = rs.getString("value");
		switch (type) {
		case Types.CHAR:
		case Types.INTEGER:
		case Types.VARCHAR:
			return "1".equals(flagStr);
		case Types.BOOLEAN:
		case Types.BIT:
			return Boolean.valueOf(flagStr).booleanValue();
		default:
			throw new IllegalStateException();
		}
	}

	public static boolean isBoolean(ResultSet rs, String columnName)
			throws SQLException {
		int type = getColumnType(rs, columnName);
		String flagStr = rs.getString(columnName);
		System.out.println(flagStr);
		switch (type) {
		case Types.CHAR:
		case Types.INTEGER:
		case Types.VARCHAR:
			return "1".equals(flagStr);
		case Types.BOOLEAN:
		case Types.BIT:
			return Boolean.valueOf(flagStr).booleanValue();
		default:
			throw new IllegalStateException();
		}
	}

	private static int getColumnType(ResultSet rs, String name)
			throws SQLException {
		ResultSetMetaData rsMeta = rs.getMetaData();
		int column = 1;
		for (int maxColumn = rsMeta.getColumnCount(); column <= maxColumn; ++column) {
			String columnName = rsMeta.getColumnName(column);
			if (columnName.equals(name))
				break;
		}

		return rsMeta.getColumnType(column);
	}

	public static Object getDigitalValue(boolean b) {
		return "mysql".equals(WifeUtilities.getDBMSName()) ? b
			? (Object) new Double(1)
			: (Object) new Double(0) : b ? (Object) "true" : (Object) "false";
	}

	/**
	 * ビットを表す2進数文字列表現のBitSetを返します。"001"は0ビットが1その他のビットが0を表す文字列です。1以外の文字列は0として扱います
	 * 
	 * @param b ビットを表す2進数文字列表現
	 * @return ビットを表す2進数文字列表現のBitSetを返します。
	 */
	public static BitSet getBitSet(String b) {
		if (null == b || "".equals(b)) {
			return new BitSet(0);
		} else {
			BitSet set = new BitSet(b.length());
			for (int i = 0; i < b.length(); i++) {
				char c = b.charAt(i);
				if ('1' == c) {
					set.set(i);
				}
			}
			return set;
		}
	}
}
