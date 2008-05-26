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

package org.F11.scada.applet.symbol;

import javax.swing.Icon;

import org.apache.log4j.Logger;


/**
 * イメージグラフィックを表示するシンボルクラスです。
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class ImageSymbol extends Symbol {
	private static final long serialVersionUID = 3573143713895769846L;
	private final Logger log = Logger.getLogger(ImageSymbol.class);

	/**
	 * Constructor for ImageSymbol.
	 * @see org.F11.scada.applet.symbol.Symbol#Symbol()
	 * @param property SymbolProperty オブジェクト
	 */
	public ImageSymbol(SymbolProperty property) {
		super(property);
	}

	/**
	 * Constructor for ImageSymbol.
	 * @see org.F11.scada.applet.symbol.Symbol#Symbol()
	 */
	public ImageSymbol() {
		super();
	}

	/*
	 * プロパティを変更します。
	 * @see org.F11.scada.applet.symbol.Symbol#updateProperty()
	 */
	protected void updatePropertyImpl() {
		String path = getProperty("value");
		Icon icon = GraphicManager.get(path);
		if (icon != null) {
			setIcon(icon);
			setSize(icon.getIconWidth(), icon.getIconHeight());
		} else {
			if (null != path) {
				log.error("icon file not found = " + path);
			}
		}
	}
}
