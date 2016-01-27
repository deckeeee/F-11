/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/alarm/AlarmDefine.java,v 1.6.2.2 2005/07/05 09:17:11 hori Exp $
 * $Revision: 1.6.2.2 $
 * $Date: 2005/07/05 09:17:11 $
 * 
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
 */

package org.F11.scada.parser.alarm;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.xml.sax.SAXException;

/**
 * 設定ファイルより警報一覧のプロパティを生成するクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmDefine {
	/** 最新情報と警報・状態一覧の設定です */
	private AlarmConfig alarmConfig;

	/**
	 * "/resources/AlarmDefine.xml"(予めクラスパスに含める事)を解析して、各設定オブジェクトに値を保持します。
	 * @throws IOException
	 * @throws SAXException
	 */
	public AlarmDefine() {
		this("/resources/AlarmDefine.xml");
	}

	/**
	 * 引数のファイル(予めクラスパスに含める事)を解析して、各設定オブジェクトに値を保持します。
	 * @param file 設定ファイル名
	 * @throws IOException
	 * @throws SAXException
	 */
	public AlarmDefine(String file) {
		Digester digester = new Digester();
		digester.push(this);

		addPageRule(digester);

		URL xml = getClass().getResource(file);
		if (xml == null) {
			throw new IllegalStateException(file + " not found.");
		}
		InputStream is = null;
		try {
			is = xml.openStream();
			digester.parse(is);
			is.close();
		} catch (Exception e) {
		    throw new IllegalStateException();
		} finally {
			if (is != null) {
				try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
			}
		}
	}

	private void addPageRule(Digester digester) {
		digester.addObjectCreate("alarm", AlarmConfig.class);
		digester.addSetNext("alarm", "setAlarmConfig");

		digester.addObjectCreate("alarm/news", AlarmNewsConfig.class);
		digester.addSetNext("alarm/news", "setAlarmNewsConfig");
		digester.addSetProperties("alarm/news");
		digester.addObjectCreate("alarm/news/font", FontConfig.class);
		digester.addSetNext("alarm/news/font", "setFontConfig");
		digester.addSetProperties("alarm/news/font");
		digester.addObjectCreate("alarm/news/linecount", LineCountConfig.class);
		digester.addSetNext("alarm/news/linecount", "setLineCountConfig");
		digester.addSetProperties("alarm/news/linecount");

		digester.addObjectCreate("alarm/table", AlarmTableConfig.class);
		digester.addSetNext("alarm/table", "setAlarmTableConfig");
		digester.addSetProperties("alarm/table");
		digester.addObjectCreate("alarm/table/font", FontConfig.class);
		digester.addSetNext("alarm/table/font", "setFontConfig");
		digester.addSetProperties("alarm/table/font");
		digester.addObjectCreate("alarm/table/linecount", LineCountConfig.class);
		digester.addSetNext("alarm/table/linecount", "setLineCountConfig");
		digester.addSetProperties("alarm/table/linecount");

		digester.addObjectCreate("alarm/server", ServerErrorMessage.class);
		digester.addSetNext("alarm/server", "setServerErrorMessage");
		digester.addSetProperties("alarm/server");

		digester.addObjectCreate("alarm/page", Page.class);
		digester.addSetNext("alarm/page", "setPage");
		digester.addSetProperties("alarm/page");

		digester.addObjectCreate("alarm/toolbar", ToolBar.class);
		digester.addSetNext("alarm/toolbar", "setToolBar");
		digester.addSetProperties("alarm/toolbar");

		digester.addObjectCreate("alarm/init", InitConfig.class);
		digester.addSetNext("alarm/init", "setInitConfig");
		digester.addSetProperties("alarm/init");
		
		digester.addObjectCreate("alarm/title", TitleConfig.class);
		digester.addSetNext("alarm/title", "setTitleConfig");
		digester.addSetProperties("alarm/title");
	}

	/**
	 * 最新情報と警報・状態一覧の設定を返します
	 * @return 最新情報と警報・状態一覧の設定
	 */
	public AlarmConfig getAlarmConfig() {
		return alarmConfig;
	}

	/**
	 * 最新情報と警報・状態一覧の設定を設定します
	 * @param config 最新情報と警報・状態一覧の設定
	 */
	public void setAlarmConfig(AlarmConfig config) {
		alarmConfig = config;
	}

	/**
	 * このオブジェクトの文字列表現を返します
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
