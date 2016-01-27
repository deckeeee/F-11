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
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.JButton;

import org.apache.log4j.Logger;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public abstract class AbstractButtonSymbol extends JButton {
	protected static Logger logger = Logger.getLogger(AbstractButtonSymbol.class);

	protected AbstractButtonSymbol(SymbolProperty property) {
		super();
		
		String value = property.getProperty("value");
		if (value != null) {
			Icon icon = GraphicManager.get(value);
			if (icon != null)
				this.setIcon(icon);
			else
				this.setText(value);
		}

		String loc_x = property.getProperty("x");
		String loc_y = property.getProperty("y");
		if (loc_x != null && loc_y != null) {
			this.setLocation(Integer.parseInt(loc_x), Integer.parseInt(loc_y));
		}

		String toolTipText = property.getProperty("tooltiptext");
		if (toolTipText != null)
			this.setToolTipText(toolTipText);

		/** trueデフォルト */
		if ("false".equals(property.getProperty("opaque")))
			this.setOpaque(false);
		else
			this.setOpaque(true);

		Color color = ColorFactory.getColor(property.getProperty("foreground"));
		if (color != null)
			this.setForeground(color);

		color = ColorFactory.getColor(property.getProperty("background"));
		if (color != null)
			this.setBackground(color);

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
			this.setFont(font);
		}

		Point loc = this.getLocation();
		String width = property.getProperty("width");
		String height = property.getProperty("height");
		if (width != null && height != null) {
			this.setBounds(loc.x, loc.y, Integer.parseInt(width), Integer.parseInt(height));
		} else {
			Dimension dm = this.getPreferredSize();
			this.setBounds(loc.x, loc.y, dm.width, dm.height);
		}
	}
}
