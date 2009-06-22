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

package org.F11.scada.server.autoprint.jasper.data;

import java.sql.ResultSet;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.xwife.server.WifeDataProvider;

import dori.jasper.engine.JRDataSource;
import dori.jasper.engine.JRException;
import dori.jasper.engine.JRField;
import dori.jasper.engine.JRResultSetDataSource;

/**
 * JRResultSetDataSource をコンポジションし、ロギングデータソース
 * を生成するクラスです。
 * 
 * データソースの元になるResultSetオブジェクトを明示的に close する
 * 必要があります。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class JRLogingDataSource implements JRDataSource {
	/** JRResultSetDataSourceの参照 */
	private final JRResultSetDataSource resultSetDataSource;

	/**
	 * JRResultSetDataSourceを引数にして、データソースを生成します。
	 * @param dataSource JRResultSetDataSourceオブジェクト
	 */
	public JRLogingDataSource(JRResultSetDataSource dataSource) {
		resultSetDataSource = dataSource;
	}

	/**
	 * ResultSet オブジェクトを引数にして、データソースを生成します。
	 * @param rs ResultSetオブジェクト
	 */
	public JRLogingDataSource(ResultSet rs) {
		this(new JRResultSetDataSource(rs));
	}

	/**
	 * @see dori.jasper.engine.JRDataSource#next()
	 */
	public boolean next() throws JRException {
		return resultSetDataSource.next();
	}

	/**
	 * @see dori.jasper.engine.JRDataSource#getFieldValue(dori.jasper.engine.JRField)
	 */
	public Object getFieldValue(JRField jrField) throws JRException {
		return convertValue(jrField);
	}

	private Object convertValue(JRField jrField) throws JRException {
		Object obj = resultSetDataSource.getFieldValue(jrField);
		if (obj instanceof Double) {
			Double d = (Double) obj;
			DataHolder dh = getDataHolder(jrField);
			ConvertValue convertValue =
				(ConvertValue) dh.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
			return new Double(convertValue.convertDoubleValue(d.doubleValue()));
		} else {
			return obj;
		}
	}

	private DataHolder getDataHolder(JRField field) {
		String sufix = "f_";
		String name = field.getName();
		int index = name.indexOf(sufix);
		DataHolder dh = null;
		if (index < 0) {
			throw new IllegalArgumentException();
		} else {
			String phname = name.substring(index + sufix.length());
			int pindex = phname.indexOf("_");
			if (pindex < 0) {
				throw new IllegalArgumentException();
			} else {
				String pname = phname.substring(0, pindex);
				String hname = phname.substring(pindex + 1);
				dh =
					Manager.getInstance().getDataProvider(pname).getDataHolder(
						hname);
			}
		}
		return dh;
	}
}
