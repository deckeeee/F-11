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

package org.F11.scada.server.autoprint.jasper.exportor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import dori.jasper.engine.JRException;
import dori.jasper.engine.JasperExportManager;
import dori.jasper.engine.JasperPrint;

/**
 * PDFファイルを出力するクラスです
 * 
 * 出力クラスプロパティー out には、ファイル名を指定します。
 * ファイル名には'%'で囲むことで {@link java.text.SimpleDateFormat}クラスの日付/時刻パターンを使用することができます。
 * 
 * 例.
 * daily%yyyyMMdd%.pdfは、2004年4月1日に実行することでdaily20040101.pdfと展開されます。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PdfExportor implements Exportor {
	/** 出力先パスのプロパティ名 */
	public static final String PROPERTY_OUT = "out";
	/** このクラスのプロパティー */
	private final Properties properties;

	/**
	 * デフォルトコンストラクタ
	 */
	public PdfExportor() {
		this.properties = new Properties();
	}

	/**
	 * プロパティーを設定します。
	 * @param key プロパティー名
	 * @param value プロパティー値
	 */
	public void setProperty(String key, String value) {
		this.properties.setProperty(key, value);
	}

	// Only in a test, it is used.
	String getOut() {
		String outPath = this.properties.getProperty(PROPERTY_OUT, "");
		return canonicalFile(outPath);
	}

	/**
	 * 印刷オブジェクトを PDF ファイルに出力します
	 * @param print 印刷オブジェクト
	 */
	public void export(JasperPrint print) throws JRException {
		JasperExportManager.exportReportToPdfFile(print, getOut());
	}
	
	private String canonicalFile(String out) {
		if (out.indexOf("%") < 0) {
			return out;
		}

		String[] s = out.split("\\%");
		String dateStr = new SimpleDateFormat(s[1]).format(new Date());
		return s[0] + dateStr + s[2];
	}

	/**
	 * このオブジェクトの文字列表現を返します
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "properties=[" + properties + "]";
	}

}
