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
import org.F11.scada.applet.ngraph.HorizontalScaleButtonProperty;
import org.F11.scada.applet.ngraph.SeriesGroup;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.parser.PageState;
import org.F11.scada.parser.State;
import org.F11.scada.util.AttributesUtil;
import org.F11.scada.util.FontUtil;
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
	/** ���t�\���t�H�[�}�b�g */
	private String dateFormat;
	/** ���ԕ\���t�H�[�}�b�g */
	private String timeFormat;
	/** �c�ڐ�1���̃s�N�Z���� */
	private int verticalScale;
	/** �c�ڐ��̐� */
	private int verticalCount;
	/** �������[�h�̏c�ڐ��̐� */
	private int compositionVerticalCount;
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
	/** �y�[�W�t�@�C���� */
	private String pagefile;
	/** ���X�P�[���{�^���v���p�e�B�[�̃��X�g */
	List<HorizontalScaleButtonProperty> scaleButtonProperties;
	/** �g�����h�O���t�ő�\�����R�[�h */
	private int maxRecord;
	/** �c�[���o�[�\���̗L�� */
	private boolean isVisibleToolbar;
	/** �V���[�Y�\���̗L�� */
	private boolean isVisibleSeries;
	/** �X�e�[�^�X�\���̗L�� */
	private boolean isVisibleStatus;
	/** �X�N���[���o�[�\���̗L�� */
	private boolean isVisibleScroolbar;
	/** �Q�ƈʒu���\���̗L�� */
	private boolean isVisibleReferenceLine;
	/** �c�X�P�[�������\���̗L�� */
	private boolean isVisibleVerticalString;
	/** ���݂̍����E�����\�����[�h */
	private boolean isCompositionMode;
	/** ���݂̃X�p���\�����[�h */
	private boolean isAllSpanDisplayMode;

	/**
	 * ��Ԃ�\���I�u�W�F�N�g�𐶐����܂��B
	 */
	public TrendGraph3State(String tagName, Attributes atts, PageState pageState) {
		seriesGroups = new ArrayList<SeriesGroup>();
		this.pageState = pageState;
		x = getValue(atts, "x", "0");
		y = getValue(atts, "y", "0");
		width = getValue(atts, "width", "1028");
		height = getValue(atts, "height", "800");

		horizontalForAllSpanMode =
			Integer.parseInt(getValue(atts, "horizontalForAllSpanMode", "112"));
		horizontalForSelectSpanMode =
			Integer.parseInt(getValue(
				atts,
				"horizontalForSelectSpanMode",
				"168"));
		dateFormat = getValue(atts, "dateFormat", "MM/dd");
		timeFormat = getValue(atts, "timeFormat", "HH:mm");
		verticalScale = Integer.parseInt(getValue(atts, "verticalScale", "48"));
		verticalCount = Integer.parseInt(getValue(atts, "verticalCount", "10"));
		compositionVerticalCount =
			Integer.parseInt(getValue(atts, "compositionVerticalCount", "10"));
		scalePixcelSize =
			Integer.parseInt(getValue(atts, "scalePixcelSize", "5"));
		insets = AttributesUtil.getInsets(atts.getValue("insets"));
		font = FontUtil.getFont(atts.getValue("font"));

		lineColor = ColorFactory.getColor(getValue(atts, "lineColor", "white"));
		backGround =
			ColorFactory.getColor(getValue(atts, "backGround", "navy"));
		verticalScaleColor =
			ColorFactory.getColor(getValue(
				atts,
				"verticalScaleColor",
				"cornflowerblue"));
		pagefile = atts.getValue("pagefile");
		maxRecord = Integer.parseInt(getValue(atts, "maxRecord", "5000"));
		isVisibleToolbar =
			Boolean.parseBoolean(getValue(atts, "visibleToolbar", "true"));
		isVisibleSeries =
			Boolean.parseBoolean(getValue(atts, "visibleSeries", "true"));
		isVisibleStatus =
			Boolean.parseBoolean(getValue(atts, "visibleStatus", "true"));
		isVisibleScroolbar =
			Boolean.parseBoolean(getValue(atts, "visibleScroolbar", "true"));
		isVisibleReferenceLine =
			Boolean
				.parseBoolean(getValue(atts, "visibleReferenceLine", "true"));
		isVisibleVerticalString =
			Boolean
				.parseBoolean(getValue(atts, "visibleVerticalString", "true"));
		isCompositionMode =
			Boolean.parseBoolean(getValue(atts, "compositionMode", "true"));
		isAllSpanDisplayMode =
			Boolean.parseBoolean(getValue(atts, "allSpanDisplayMode", "false"));
	}

	private String getValue(Attributes atts, String name, String def) {
		String value = atts.getValue(name);
		return value != null ? value : def;
	}

	/*
	 * @see org.F11.scada.parser.State#add(String, Attributes, Stack)
	 */
	public void add(String tagName, Attributes atts, Stack stack) {
		if (tagName.equals("series")) {
			stack.push(new SeriesState(tagName, atts, this));
		} else if (tagName.equals("horizontalScaleButton")) {
			stack.push(new HorizontalScaleButtonState(tagName, atts, this));
		} else {
			logger.debug("tagName : " + tagName);
		}
	}

	public void end(String tagName, Stack stack) {
		if (tagName.equals("trendgraph3")) {
			GraphProperties p = getGraphProperties();
			p.setSeriesGroup(seriesGroups);
			p.setHorizontalScaleButtonProperty(scaleButtonProperties);
			GraphMainPanel mainPanel = new GraphMainPanel(p);
			if (x != null && y != null) {
				mainPanel.setLocation(getNumber(x), getNumber(y));
			}
			if (width != null && height != null) {
				mainPanel.setSize(getNumber(width), getNumber(height));
			}
			pageState.addPageSymbol(mainPanel);
			if (p.isVisibleToolbar()) {
				pageState.setToolBar(mainPanel.getToolBar());
			}
			stack.pop();
		}
	}

	private GraphProperties getGraphProperties() {
		GraphProperties p = new GraphProperties();
		p.setHorizontalForAllSpanMode(horizontalForAllSpanMode);
		p.setHorizontalForSelectSpanMode(horizontalForSelectSpanMode);
		p.setHorizontalCount(getHorizontalCount());
		p.setHorizontalLineSpan(getHorizontalSpan());
		p.setDateFormat(dateFormat);
		p.setTimeFormat(timeFormat);
		p.setVerticalScale(verticalScale);
		p.setVerticalCount(verticalCount);
		p.setCompositionVerticalCount(compositionVerticalCount);
		p.setScalePixcelSize(scalePixcelSize);
		p.setInsets(insets);
		p.setFont(font);
		p.setLineColor(lineColor);
		p.setBackGround(backGround);
		p.setVerticalScaleColor(verticalScaleColor);
		p.setPagefile(pagefile);
		p.setMaxRecord(maxRecord);
		p.setVisibleToolbar(isVisibleToolbar);
		p.setVisibleSeries(isVisibleSeries);
		p.setVisibleStatus(isVisibleStatus);
		p.setVisibleScroolbar(isVisibleScroolbar);
		p.setVisibleReferenceLine(isVisibleReferenceLine);
		p.setVisibleVerticalString(isVisibleVerticalString);
		p.setCompositionMode(isCompositionMode);
		p.setAllSpanDisplayMode(isAllSpanDisplayMode);
		return p;
	}

	private long getHorizontalSpan() {
		return scaleButtonProperties.get(0).getHorizontalLineSpan();
	}

	private int getHorizontalCount() {
		return scaleButtonProperties.get(0).getHorizontalCount();
	}

	private int getNumber(String string) {
		return Integer.parseInt(string);
	}

}
