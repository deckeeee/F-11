/*
 * $Header$
 * $Revision$
 * $Date$
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
 *
 */
package org.F11.scada.parser.ngraph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.F11.scada.applet.ngraph.GraphMainPanel;
import org.F11.scada.applet.ngraph.GraphProperties;
import org.F11.scada.applet.ngraph.SeriesGroup;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.parser.PageState;
import org.F11.scada.parser.State;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * XPath=/page_map/page/trendgraph ��Ԃ�\���N���X�ł��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class TrendGraph3State implements State {
	protected static Logger logger = Logger.getLogger(TrendGraph3State.class);
	private PageState pageState;
	private String x;
	private String y;
	private String width;
	private String height;
	/** ���ݑS�\���̉����s�N�Z���� */
	private int horizontalForAllSpanMode;
	/** ���ݗ��\���̉����s�N�Z���� */
	private int horizontalForSelectSpanMode;
	/** ���ڐ��̐� */
	private int horizontalCount;
	/** ���ڐ��̎��ԃX�P�[���� */
	private long horizontalLineSpan;
	/** ���t�\���t�H�[�}�b�g */
	private String dateFormat;
	/** ���ԕ\���t�H�[�}�b�g */
	private String timeFormat;
	/** �c�ڐ�1���̃s�N�Z���� */
	private int verticalScale;
	/** �c�ڐ��̐� */
	private int verticalCount;
	/** �ڐ����̃s�N�Z���� */
	private int scalePixcelSize;
	/** �O���t�G���A�̗]�� */
	private Insets insets;
	/** �g�p�t�H���g */
	private Font font;
	/** ���̐F */
	private Color lineColor;
	/** �w�i�F */
	private Color backGround;
	/** �O���t�G���A�̃O���b�h�F */
	private Color verticalScaleColor;
	/** �V���[�Y�O���[�v�̃��X�g */
	List<SeriesGroup> seriesGroups;

	/**
	 * ��Ԃ�\���I�u�W�F�N�g�𐶐����܂��B
	 */
	public TrendGraph3State(String tagName, Attributes atts, PageState pageState) {
		seriesGroups = new ArrayList<SeriesGroup>();
		this.pageState = pageState;
		x = atts.getValue("x");
		y = atts.getValue("y");
		width = atts.getValue("width");
		height = atts.getValue("height");

		horizontalForAllSpanMode =
			Integer.parseInt(getValue(atts, "horizontalForAllSpanMode", "112"));
		horizontalForSelectSpanMode =
			Integer.parseInt(getValue(
				atts,
				"horizontalForSelectSpanMode",
				"168"));
		horizontalCount =
			Integer.parseInt(getValue(atts, "horizontalCount", "5"));
		horizontalLineSpan =
			Long.parseLong(getValue(atts, "horizontalLineSpan", "18000000"));
		dateFormat = getValue(atts, "dateFormat", "%1$tm/%1$td");
		timeFormat = getValue(atts, "timeFormat", "%1$tH:%1$tM");
		verticalScale = Integer.parseInt(getValue(atts, "verticalScale", "48"));
		verticalCount = Integer.parseInt(getValue(atts, "verticalCount", "10"));
		scalePixcelSize =
			Integer.parseInt(getValue(atts, "scalePixcelSize", "5"));
		insets = getInsets(atts);
		font = getFont(atts);

		lineColor = ColorFactory.getColor(getValue(atts, "lineColor", "white"));
		backGround =
			ColorFactory.getColor(getValue(atts, "backGround", "navy"));
		verticalScaleColor =
			ColorFactory.getColor(getValue(
				atts,
				"verticalScaleColor",
				"cornflowerblue"));
	}

	private String getValue(Attributes atts, String name, String def) {
		String value = atts.getValue(name);
		return value != null ? value : def;
	}

	private Insets getInsets(Attributes atts) {
		String is = atts.getValue("insets");
		if (is != null) {
			String[] s = is.split(",");
			return new Insets(
				getNumber(s[0].trim()),
				getNumber(s[1].trim()),
				getNumber(s[2].trim()),
				getNumber(s[3].trim()));
		} else {
			return new Insets(50, 80, 60, 50);
		}
	}

	private int getNumber(String string) {
		return Integer.parseInt(string);
	}

	private Font getFont(Attributes atts) {
		String fontStr = atts.getValue("font");
		if (fontStr != null) {
			String[] s = fontStr.split("-");
			return new Font(s[0], getFontStyle(s[1]), getNumber(s[2]));
		} else {
			return new Font("Monospaced", Font.PLAIN, 18);
		}
	}

	private int getFontStyle(String s) {
		if ("PLAIN".equalsIgnoreCase(s)) {
			return Font.PLAIN;
		} else if ("BOLD".equalsIgnoreCase(s)) {
			return Font.BOLD;
		} else {
			return Font.ITALIC;
		}
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (tagName.equals("series")) {
			stack.push(new SeriesState(tagName, atts, this));
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	public void end(String tagName, Stack stack) {
		if (tagName.equals("trendgraph3")) {
			GraphProperties p = getGraphProperties();
			p.setSeriesGroup(seriesGroups);
			GraphMainPanel mainPanel = new GraphMainPanel(p);
			if (x != null && y != null) {
				mainPanel.setLocation(getNumber(x), getNumber(y));
			}
			if (width != null && height != null) {
				mainPanel.setSize(getNumber(width), getNumber(height));
			}
			pageState.addPageSymbol(mainPanel);
			stack.pop();
		}
	}

	private GraphProperties getGraphProperties() {
		GraphProperties p = new GraphProperties();
		p.setHorizontalForAllSpanMode(horizontalForAllSpanMode);
		p.setHorizontalForSelectSpanMode(horizontalForSelectSpanMode);
		p.setHorizontalCount(horizontalCount);
		p.setHorizontalLineSpan(horizontalLineSpan);
		p.setDateFormat(dateFormat);
		p.setTimeFormat(timeFormat);
		p.setVerticalScale(verticalScale);
		p.setVerticalCount(verticalCount);
		p.setScalePixcelSize(scalePixcelSize);
		p.setInsets(insets);
		p.setFont(font);
		p.setLineColor(lineColor);
		p.setBackGround(backGround);
		p.setVerticalScaleColor(verticalScaleColor);
		return p;
	}
}
