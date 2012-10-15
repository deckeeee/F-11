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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 活線シンボルクラスです。
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class DrawSymbol extends Symbol {
	private static final long serialVersionUID = -6262805787396199210L;
	private List lines = new ArrayList();
	private BasicStroke stroke;
	private Color color = Color.black;
	private int x_max;
	private int y_max;
	

	/**
	 * Constructor for DrawSymbol.
	 * @param property
	 */
	public DrawSymbol(SymbolProperty property) {
		super(property);
		property.setProperty("x", "0");
		property.setProperty("y", "0");
		
		float width = 1.0f;
		String strw = getProperty("line_width");
		if (strw != null)
			width = Float.parseFloat(strw);

		int cap = BasicStroke.CAP_ROUND;
		String strcap = getProperty("line_cap");
		if ("BUTT".equals(strcap))
			cap = BasicStroke.CAP_BUTT;
		else if ("SQUARE".equals(strcap))
			cap = BasicStroke.CAP_SQUARE;
			
		stroke = new BasicStroke(width, cap, BasicStroke.JOIN_ROUND);
	}

	/**
	 * Constructor for DrawSymbol.
	 */
	public DrawSymbol() {
		super();
	}

	protected void updatePropertyImpl() {
		color = ColorFactory.getColor(getProperty("foreground"));

		Point loc = this.getLocation();
		float width = stroke.getLineWidth();
		this.setBounds(loc.x, loc.y, x_max+(int)width, y_max+(int)width);
		this.repaint();
	}

	public void addLine(Point[] pl, int pllen) {
		if (pllen < 2)
			return;
		
		for (int i = 0; i < pllen; i++) {
			if (i < pllen - 1) {
				Line2D l2 = new Line2D.Float(pl[i].x, pl[i].y, pl[i+1].x, pl[i+1].y);
				lines.add(l2);
			}
			if (x_max < pl[i].x)	x_max = pl[i].x;
			if (y_max < pl[i].y)	y_max = pl[i].y;
		}
	}
	/**
	 * @see javax.swing.JComponent#paintComponent(Graphics)
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		if (color != null)
			g2.setColor(color);
		g2.setStroke(stroke);
		for (Iterator it = lines.iterator(); it.hasNext(); ) {
			g2.draw((Line2D)it.next());
		}
	}

	public void disConnect() {
		lines.clear();
		super.disConnect();
	}
}
