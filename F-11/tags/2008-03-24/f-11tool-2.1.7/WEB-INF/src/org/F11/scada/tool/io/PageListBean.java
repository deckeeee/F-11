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

package org.F11.scada.tool.io;

import java.io.Serializable;

/**
 * ページ定義XMLファイルパステーブルを保持するクラスです
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageListBean implements Serializable {
	private static final long serialVersionUID = -8253534711192885925L;
	/** ページ名 */
	private String pageName;
	/** ページ定義XMLファイルパス */
	private String pageXmlPath;

	/**
	 * ページ名を返します
	 * 
	 * @return ページ名
	 */
	public String getPageName() {
		return pageName;
	}

	/**
	 * ページ定義XMLファイルパスを返します
	 * 
	 * @return ページ定義XMLファイルパス
	 */
	public String getPageXmlPath() {
		return pageXmlPath;
	}

	/**
	 * ページ名を設定します
	 * 
	 * @param string ページ名
	 */
	public void setPageName(String string) {
		pageName = string;
	}

	/**
	 * ページ定義XMLファイルパスを設定します
	 * 
	 * @param string ページ定義XMLファイルパス
	 */
	public void setPageXmlPath(String string) {
		pageXmlPath = string;
	}
}
