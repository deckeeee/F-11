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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import jp.gr.javacons.jim.DataReferencer;

import org.F11.scada.WifeException;
import org.F11.scada.applet.expression.Expression;

/**
 * 折れ線グラフのシンボルです。
 * @author hori
 */
public class LineGraphSymbol extends Symbol {
	private static final long serialVersionUID = -6346525108221035494L;
	/** 参照するリファレンサのリストです。 */
	protected List dataReferencers = new ArrayList();
	private List lines = new ArrayList();
	private double x_min;
	private double x_max;
	private double y_min;
	private double y_max;

	/**
	 * @param property
	 */
	public LineGraphSymbol(SymbolProperty property) {
		super(property);
		x_min = Double.parseDouble(getProperty("x_min_scale"));
		x_max = Double.parseDouble(getProperty("x_max_scale"));
		y_min = Double.parseDouble(getProperty("y_min_scale"));
		y_max = Double.parseDouble(getProperty("y_max_scale"));
	}

	/**
	 * 
	 */
	public LineGraphSymbol() {
		this(null);
	}

	public void addLinePath(LinePath linePath) {
		lines.add(linePath);
		dataReferencers.addAll(linePath.getReferencers());
		connectReferencer();
	}

	/* (non-Javadoc)
	 * @see org.F11.scada.applet.symbol.Symbol#updatePropertyImpl()
	 */
	protected void updatePropertyImpl() {
		Point loc = this.getLocation();
		String width = getProperty("width");
		String height = getProperty("height");
		if (width != null && height != null) {
			this.setBounds(loc.x, loc.y, Integer.parseInt(width), Integer.parseInt(height));
		} else {
			Dimension dm = this.getPreferredSize();
			this.setBounds(loc.x, loc.y, dm.width, dm.height);
		}
		this.repaint();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		for (Iterator it = lines.iterator(); it.hasNext();) {
			LinePath linePath = (LinePath) it.next();
			try {
				linePath.paintLine(g2, this);
			} catch (WifeException ex) {
				if (ex.getConditionCode() == WifeException.WIFE_ERROR)
					ex.printStackTrace();
			}

		}
	}

	/**
	 * 自分自身をManagerに登録します。
	 */
	public void connectReferencer() {
		Iterator it = dataReferencers.iterator();
		while (it.hasNext()) {
			DataReferencer dataReferencer = (DataReferencer) it.next();
			dataReferencer.connect(this);
		}
	}

	/**
	 * 親シンボルをManagerから登録解除します。
	 */
	public void disConnect() {
		for (Iterator it = dataReferencers.iterator(); it.hasNext();) {
			DataReferencer dataReferencer = (DataReferencer) it.next();
			dataReferencer.disconnect(this);
		}
		dataReferencers.clear();
		lines.clear();
		
		super.disConnect();
	}

	public int calcXPos(double dx) {
		return (int) ((dx - x_min) / (x_max - x_min) * this.getWidth());
	}

	public int calcYPos(double dy) {
		return (int) ((dy - y_min) / (y_max - y_min) * this.getHeight());
	}

	public static class LinePath implements CompositeProperty {
		/** プロパティのセットです。 */
		private List propertys = new ArrayList();
		private List pointExpr = new ArrayList();

		public LinePath(SymbolProperty parentProperty, SymbolProperty localProperty) {
			addCompositeProperty(parentProperty);
			addCompositeProperty(localProperty);
		}
		public void addCompositeProperty(CompositeProperty property) {
			if (property != null) {
				propertys.add(property);
			}
		}

		public String getProperty(String key) {
			for (ListIterator li = propertys.listIterator(propertys.size()); li.hasPrevious();) {
				CompositeProperty prop = (CompositeProperty) li.previous();
				if (prop != null && prop.getProperty(key) != null)
					return prop.getProperty(key);
			}
			return null;
		}

		public void addLine(String x, String y) {
			Expression[] expr = new Expression[2];
			expr[0] = new Expression();
			expr[1] = new Expression();
			expr[0].toPostfix(x);
			expr[1].toPostfix(y);
			pointExpr.add(expr);
		}

		public void paintLine(Graphics2D g2, LineGraphSymbol lineGraph) throws WifeException {
			Color color = ColorFactory.getColor(getProperty("foreground"));
			if (color != null)
				g2.setColor(color);
			else
				g2.setColor(Color.black);
			float width = 1.0f;
			String strw = getProperty("line_width");
			if (strw != null)
				width = Float.parseFloat(strw);
			BasicStroke stroke =
				new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			g2.setStroke(stroke);

			int[] xPos = new int[pointExpr.size()];
			int[] yPos = new int[pointExpr.size()];
			int posCnt = 0;
			for (Iterator it = pointExpr.iterator(); it.hasNext();) {
				Expression[] expr = (Expression[]) it.next();
				xPos[posCnt] = lineGraph.calcXPos(expr[0].doubleValue());
				yPos[posCnt] = lineGraph.calcYPos(expr[1].doubleValue());
				posCnt++;
			}
			g2.drawPolyline(xPos, yPos, posCnt);
		}

		public Collection getReferencers() {
			List refList = new ArrayList();
			for (Iterator it = pointExpr.iterator(); it.hasNext();) {
				Expression[] expr = (Expression[]) it.next();
				for (int i = 0; i < expr.length; i++) {
					refList.addAll(phNamesToReferencers(expr[i].getProviderHolderNames()));
				}
			}
			return refList;
		}

		private Collection phNamesToReferencers(Collection phNames) {
			List refList = new ArrayList();
			for (Iterator ite = phNames.iterator(); ite.hasNext();) {
				String phName = (String) ite.next();
				int p = phName.indexOf('_');
				if (0 < p) {
					refList.add(
						new DataReferencer(phName.substring(0, p), phName.substring(p + 1)));
				}
			}
			return refList;
		}

	}
}
