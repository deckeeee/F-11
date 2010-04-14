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

package org.F11.scada.applet.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import jp.gr.javacons.jim.DataReferencer;

import org.F11.scada.WifeException;
import org.F11.scada.applet.expression.Expression;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.applet.symbol.SymbolProperty;
import org.F11.scada.applet.symbol.TextSymbol;

/**
 * アナログ値を表示するクラスです。
 * 表示フォーマット変換、スパン変換、最大・最小値チェック等を行いラベルに表示します。
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class ExplanatoryNotesText extends TextSymbol {
	private static final long serialVersionUID = -5225388690479357956L;
	/** 参照するリファレンサのリストです。 */
	protected List dataReferencers = new ArrayList();
	/** 値の表示に使う計算式です。 */
	protected Expression expression;
	/** ローカルで保持する表示パターン */
	protected String pattern;
	
//	private static Logger logger = Logger.getLogger(ExplanatoryNotesText.class);
	
	/**
	 * Constructor for TextAnalogSymbol.
	 * @param property SymbolProperty オブジェクト
	 */
	public ExplanatoryNotesText(SymbolProperty property) {
		super(property);
		init(getProperty("value"));
		setAlign();
	}

	/**
	 * Constructor for TextAnalogSymbol.
	 * @param dataProviderName プロバイダ名
	 * @param dataHolderName ホルダ名
	 */
	public ExplanatoryNotesText(String dataProviderName, String dataHolderName) {
		init(dataProviderName + "_" + dataHolderName);
	}

	/**
	 * デフォルトのコンストラクタです。
	 */
	public ExplanatoryNotesText() {
		this(null);
	}

	protected void init(String value) {
		expression = new Expression();
		expression.toPostfix(value);
		Iterator it = expression.getProviderHolderNames().iterator();
		while (it.hasNext()) {
			String tag = (String) it.next();
			int p = tag.indexOf('_');
			if (0 < p) {
				dataReferencers.add(
					new DataReferencer(
						tag.substring(0, p),
						tag.substring(p + 1)));
			}
		}
		connectReferencer();
	}

	/**
	 * 文字列を設定します。
	 * @see org.F11.scada.applet.symbol.TextSymbol#setFormatedString()
	 */
	protected void setFormatedString() {
		try {
			double value = expression.doubleValue();
			setText(expression.format(value, getProperty("format")));
		} catch (WifeException we) {
			if (we.getDetailCode() == WifeException.WIFE_INITIALDATA_WARNING) {
				setText(message.getInitText());
			} else if (we.getDetailCode() == WifeException.WIFE_BAD_DATA_WARNING) {
				setText(message.getErrorText());
			} else {
				setText(message.getErrorText());
			}
		}

		setAlign();
	}

	/**
	 * 自分自身をManagerに登録します。
	 */
	public void connectReferencer() {
		for (Iterator it = dataReferencers.iterator(); it.hasNext();) {
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

		super.disConnect();
	}

	protected void updatePropertyImpl() {
		if (SwingUtilities.isEventDispatchThread()) {
			propertyUpdate();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					propertyUpdate();
				}
			});
		}
	}
	
	private void propertyUpdate() {
		/** falseデフォルト */
		if ("true".equals(getProperty("opaque")))
			setOpaque(true);
		else
			setOpaque(false);

		Color color = ColorFactory.getColor(getProperty("foreground"));
		if (color != null)
			setForeground(color);

		color = ColorFactory.getColor(getProperty("background"));
		if (color != null)
			setBackground(color);

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
			setFont(font);
		}

		setFormatedString();
		
		Point loc = getLocation();
		String width = getProperty("width");
		String height = getProperty("height");
		if (width != null && height != null) {
			setBounds(
				loc.x,
				loc.y,
				Integer.parseInt(width),
				Integer.parseInt(height));
		}
	}
}
