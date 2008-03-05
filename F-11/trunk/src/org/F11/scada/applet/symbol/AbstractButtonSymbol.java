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
package org.F11.scada.applet.symbol;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.JButton;

import org.F11.scada.util.AttributesUtil;
import org.apache.log4j.Logger;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public abstract class AbstractButtonSymbol extends JButton {
	protected static Logger logger =
		Logger.getLogger(AbstractButtonSymbol.class);

	protected AbstractButtonSymbol(SymbolProperty property) {
		super();

		String value = property.getProperty("value");
		if (value != null) {
			Icon icon = GraphicManager.get(value);
			if (icon != null)
				setIcon(icon);
			else
				setText(value);
		}

		String loc_x = property.getProperty("x");
		String loc_y = property.getProperty("y");
		if (loc_x != null && loc_y != null) {
			setLocation(Integer.parseInt(loc_x), Integer.parseInt(loc_y));
		}

		String toolTipText = property.getProperty("tooltiptext");
		if (toolTipText != null)
			setToolTipText(toolTipText);

		/** trueデフォルト */
		if ("false".equals(property.getProperty("opaque")))
			setOpaque(false);
		else
			setOpaque(true);

		Color color = ColorFactory.getColor(property.getProperty("foreground"));
		if (color != null)
			setForeground(color);

		color = ColorFactory.getColor(property.getProperty("background"));
		if (color != null)
			setBackground(color);

		String fontName = property.getProperty("font");
		String fontStyle = property.getProperty("font_style");
		String fontSize = property.getProperty("font_size");
		if (fontName != null && fontStyle != null && fontSize != null) {
			int style = Font.PLAIN;
			if ("BOLD".equals(fontStyle.toUpperCase()))
				style = Font.BOLD;
			else if ("ITALIC".equals(fontStyle.toUpperCase()))
				style = Font.ITALIC;
			Font font = new Font(fontName, style, Integer.parseInt(fontSize));
			setFont(font);
		}

		Point loc = this.getLocation();
		String width = property.getProperty("width");
		String height = property.getProperty("height");
		Insets insets = getInsets(property.getProperty("margin"));
		if (width != null && height != null) {
			setBounds(loc.x, loc.y, Integer.parseInt(width), Integer
				.parseInt(height));
			Dimension d =
				new Dimension(Integer.parseInt(width), Integer.parseInt(height));
			setPreferredSize(d);
			setMaximumSize(d);
			setMargin(insets);
		} else {
			Dimension dm = getPreferredSize();
			setBounds(loc.x, loc.y, dm.width, dm.height);
			setMargin(insets);
		}
	}

	private Insets getInsets(String insetStr) {
		if (!AttributesUtil.isSpaceOrNull(insetStr)) {
			String[] p = insetStr.split("\\,");
			try {
				return new Insets(
					getParam(p[0]),
					getParam(p[1]),
					getParam(p[2]),
					getParam(p[3]));
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	private int getParam(String p) {
		return Integer.parseInt(p.trim());
	}
}
