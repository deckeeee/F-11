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

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JLabel;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.DataValueChangeListener;

import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.data.WifeData;
import org.apache.log4j.Logger;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * シンボルオブジェクトの基底クラスです。
 * 
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public abstract class Symbol extends JLabel implements CompositeProperty,
		DataReferencerOwner, DataValueChangeListener, ReferencerOwnerSymbol {
	/** DataHolderタイプ情報です。 */
	private static final Class[][] WIFE_TYPE_INFO = new Class[][] { {
			DataHolder.class, WifeData.class } };

	/** 点滅用のタイマーです */
	private WifeTimer timer = WifeTimer.getInstance();
	/** 点滅フラグです。 */
	private boolean isBlink = false;
	/** プロパティのセットです。 */
	private List propertys = new CopyOnWriteArrayList();
	/** 点滅のリスナークラス */
	private ActionListener listener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (isBlink) {
				setVisible(timer.isShowTime());
			}
		}
	};

	private static boolean isCustomTipLocation;
	static {
		ClientConfiguration configuration = new ClientConfiguration();
		isCustomTipLocation = configuration.getBoolean(
				"xwife.applet.Applet.customTipLocation",
				false);
	}

	private static Logger logger = Logger.getLogger(Symbol.class);

	/**
	 * Constructor for Symbol.
	 * 
	 * @param property SymbolProperty オブジェクト
	 */
	public Symbol(SymbolProperty property) {
		super();

		/* 点滅の実装 */
		timer.addActionListener(listener);

		addCompositeProperty(property);
	}

	/**
	 * Constructor for Symbol.
	 */
	public Symbol() {
		this(null);
	}

	/**
	 * プロパティを変更します。 サブクラスはupdatePropertyImplメソッドを実装し、サブクラス固有のプロパティー
	 * の更新処理を行います。このメソッドはオーバーライドできないようにfinal宣言されています。 <!-- Template Method
	 * Pattern -->
	 */
	public final void updateProperty() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				/** falseデフォルト */
				if ("true".equals(getProperty("blink"))) {
					isBlink = true;
				} else {
					isBlink = false;
				}

				String loc_x = getProperty("x");
				String loc_y = getProperty("y");
				if (loc_x != null && loc_y != null) {
					setLocation(Integer.parseInt(loc_x), Integer
							.parseInt(loc_y));
				}

				String toolTipText = getProperty("tooltiptext");
				if (toolTipText != null) {
					setToolTipText(toolTipText);
				}

				updatePropertyImpl();

				/** trueデフォルト */
				if ("false".equals(getProperty("visible"))) {
					setVisible(false);
				} else {
					setVisible(true);
				}
				revalidate();
			}
		});
	}

	/**
	 * サブクラス固有のプロパティーの更新処理を行います。
	 */
	protected abstract void updatePropertyImpl();

	/**
	 * プロパティを設定します。
	 * 
	 * @see org.F11.scada.applet.symbol.CompositeProperty#addCompositeProperty(CompositeProperty)
	 * @param property コンポジットパターン
	 */
	public void addCompositeProperty(CompositeProperty property) {
		if (property != null) {
			propertys.add(property);
		}
	}

	/**
	 * プロパティのセットから値を取得します。
	 * 
	 * @see org.F11.scada.applet.symbol.CompositeProperty#getProperty(String)
	 * @param key プロパティのキー文字列
	 */
	public String getProperty(String key) {
		ListIterator li = propertys.listIterator(propertys.size());
		while (li.hasPrevious()) {
			CompositeProperty prop = (CompositeProperty) li.previous();
			if (prop != null && prop.getProperty(key) != null)
				return prop.getProperty(key);
		}
		return null;
	}

	/**
	 * DataHolderタイプ情報を返します。
	 * 
	 * @see jp.gr.javacons.jim.DataReferencerOwner#getReferableDataHolderTypeInfo(DataReferencer)
	 */
	public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
		return WIFE_TYPE_INFO;
	}

	/**
	 * データ変更イベント処理
	 * 
	 * @see jp.gr.javacons.jim.DataValueChangeListener#dataValueChanged(DataValueChangeEvent)
	 */
	public void dataValueChanged(DataValueChangeEvent evt) {
		Object o = evt.getSource();
		if (!(o instanceof DataHolder)) {
			return;
		}

		updateProperty();
	}

	/**
	 * 親シンボルをManagerから登録解除します。
	 * 
	 * @since 1.1.1
	 */
	public void disConnect() {
		for (Iterator it = propertys.iterator(); it.hasNext();) {
			CompositeProperty cp = (CompositeProperty) it.next();
			if (cp instanceof BitRefer) {
				BitRefer br = (BitRefer) cp;
				br.disconnectReferencer(this);
			}
		}
		propertys.clear();
		timer.removeActionListener(listener);
	}

	/**
	 * 点滅状態を返します。
	 * 
	 * @return 点滅状態を返します
	 */
	public boolean isBlink() {
		return isBlink;
	}

	/**
	 * 
	 * @see javax.swing.JComponent#getToolTipLocation(java.awt.event.MouseEvent)
	 */
	public Point getToolTipLocation(MouseEvent event) {
		if (isCustomTipLocation) {
			Symbol symbol = (Symbol) event.getSource();
			Point p = new Point(event.getX() + 8, symbol.getHeight() / 2 + 32);
			return p;
		} else {
			return super.getToolTipLocation(event);
		}
	}

	public JToolTip createToolTip() {
		return new YellowToolTip(this);
	}
}
