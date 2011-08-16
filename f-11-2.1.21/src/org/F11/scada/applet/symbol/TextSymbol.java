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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JLabel;

/**
 * テキストを表示するシンボルクラスです。
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class TextSymbol extends Symbol {
	private static final long serialVersionUID = -2319682910862006833L;
	/** エラー表示ーメッセージ */
	protected static final TextSymbolMessage message = new TextSymbolMessage();

	/**
	 * Constructor for StringSymbol.
	 * @param property SymbolProperty オブジェクト
	 */
	public TextSymbol(SymbolProperty property) {
		super(property);
		setAlign();
	}

	/**
	 * Constructor for StringSymbol.
	 */
	public TextSymbol() {
		this(null);
	}

	/*
	 * プロパティを変更します。
	 * @see org.F11.scada.applet.symbol.Symbol#updateProperty()
	 */
	protected void updatePropertyImpl() {

		/** falseデフォルト */
		if ("true".equals(getProperty("opaque")))
			this.setOpaque(true);
		else
			this.setOpaque(false);

		Color color = ColorFactory.getColor(getProperty("foreground"));
		if (color != null)
			this.setForeground(color);

		color = ColorFactory.getColor(getProperty("background"));
		if (color != null)
			this.setBackground(color);

		String fontName = getProperty("font");
		String fontStyle = getProperty("font_style");
		String fontSize = getProperty("font_size");
		if (fontName != null && fontStyle != null && fontSize != null) {
			int style = Font.PLAIN;
			if ("BOLD".equals(fontStyle.toUpperCase()))
				style = Font.BOLD;
			else if ("ITALIC".equals(fontStyle.toUpperCase()))
				style = Font.ITALIC;
			Font font = new Font(fontName, style, Integer.parseInt(fontSize));
			this.setFont(font);
		}

		setFormatedString();
		
		Point loc = this.getLocation();
		String width = getProperty("width");
		String height = getProperty("height");
		if (width != null && height != null) {
			this.setBounds(
				loc.x,
				loc.y,
				Integer.parseInt(width),
				Integer.parseInt(height));
		} else {
			Dimension dm = this.getPreferredSize();
			this.setBounds(loc.x, loc.y, dm.width, dm.height);
		}
	}
	
	/**
	 * 文字列を設定します。
	 */
	protected void setFormatedString() {
		String text = getProperty("value");
		if (text != null) {
			this.setText(text);
		}
		setAlign();
	}
	
	protected void setAlign() {
		String halg = getProperty("h_aligin");
		if (halg != null) {
			int alignment = JLabel.LEADING;
			if ("LEFT".equals(halg))
				alignment = JLabel.LEFT;
			else if ("CENTER".equals(halg))
				alignment = JLabel.CENTER;
			else if ("RIGHT".equals(halg))
				alignment = JLabel.RIGHT;
			else if ("LEADING".equals(halg))
				alignment = JLabel.LEADING;
			else if ("TRAILING ".equals(halg))
				alignment = JLabel.TRAILING;

			int currentAlign = getHorizontalAlignment();
			if (currentAlign != alignment) {
				this.setHorizontalAlignment(alignment);
			}
		}
	}
}
