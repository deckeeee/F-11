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

import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.dialog.DialogFactory;
import org.F11.scada.applet.dialog.WifeDialog;
import org.F11.scada.applet.symbol.VariableAnalog4Setter.Analog4Data;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog4;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.util.ComponentUtil;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.xml.sax.Attributes;

/**
 * @author hori
 */
public class TextAnalog4SymbolEditable extends TextAnalog4Symbol implements
		Analog4Editable {
	private static final long serialVersionUID = 82128533692496767L;
	/** ダイアログ表示位置 */
	private Point dialogPoint;
	/** 編集可能フラグ */
	private boolean editable;
	/** アクションのリスト */
	private List actions;
	/** 書込み対象ホルダ指定 */
	private String providerName;
	private String holderName;
	/** 値を編集するためのダイアログ名称 */
	private String secondDialogName;
	/** ダイアログタイトル */
	private String dlgTitle;
	private Authenticationable authentication;

	/**
	 * Constructor for TextAnalog4Symbol.
	 * 
	 * @param property SymbolProperty オブジェクト
	 */
	public TextAnalog4SymbolEditable(
			SymbolProperty property,
			Authenticationable authentication) {
		this(property);
		this.authentication = authentication;
		this.authentication.addEditable(this);
	}

	/**
	 * Constructor for TextAnalog4Symbol.
	 * 
	 * @param property SymbolProperty オブジェクト
	 */
	public TextAnalog4SymbolEditable(SymbolProperty property) {
		super(property);
		addMouseListener();
		actions = new ArrayList();
		dlgTitle = getProperty("dlgtitle");
	}

	/**
	 * デフォルトのコンストラクタです。
	 */
	public TextAnalog4SymbolEditable() {
		this(null);
	}

	/**
	 * マウスイベントの登録
	 */
	private void addMouseListener() {
		final Component myComp = this;
		this.addMouseListener(new MouseAdapter() {
			private Rectangle rectangle;

			public void mousePressed(MouseEvent e) {
				rectangle = myComp.getBounds();
			}

			public void mouseReleased(MouseEvent e) {
				if (ComponentUtil.contains(rectangle, e.getPoint())) {
					this_mouseClicked(e);
				}
			}
		});
		addMouseListener(new HandCursorListener(this));
	}

	/**
	 * マウスクリックイベント
	 * 
	 * @param e マウスイベントオブジェクト
	 */
	public void this_mouseClicked(java.awt.event.MouseEvent e) {
		if (!isEditable()) {
			return;
		}
		final Frame frame = WifeUtilities.getParentFrame(this);
		final ArrayList para = new ArrayList();
		para.add(this.getClass());
		para.add(this);
		WifeDialog dlg = getDialog(frame, (SymbolCollection) getParent(), para);
//		dlg.selectAll();
		dlg.show();
	}

	/**
	 * ConvertValueを返します
	 */
	public ConvertValue getConvertValue() {
		DataHolder dh = Manager.getInstance().findDataHolder(
				providerName,
				holderName);
		WifeData wd = (WifeData) dh.getValue();
		if (!(wd instanceof WifeDataAnalog4))
			return null;

		return (ConvertValue) dh
				.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
	}

	/**
	 * 数値表示フォーマット文字列を返します
	 */
	public String getFormatString() {
		if (pattern != null)
			return pattern;

		DataHolder dh = Manager.getInstance().findDataHolder(
				providerName,
				holderName);
		WifeData wd = (WifeData) dh.getValue();
		if (!(wd instanceof WifeDataAnalog4))
			return "0.0";

		ConvertValue conv = (ConvertValue) dh
				.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
		return conv.getPattern();
	}

	/**
	 * シンボルの値を返します
	 */
	public String[] getValues() {
		DataHolder dh = Manager.getInstance().findDataHolder(
				providerName,
				holderName);
		WifeData wd = (WifeData) dh.getValue();
		if (!(wd instanceof WifeDataAnalog4))
			return null;

		WifeDataAnalog4 wa = (WifeDataAnalog4) wd;
		ConvertValue conv = (ConvertValue) dh
				.getParameter(WifeDataProvider.PARA_NAME_CONVERT);

		double[] data = wa.doubleValues();
		String[] ret = new String[data.length];
		for (int i = 0; i < data.length; i++) {
			ret[i] = conv.convertStringValue(data[i], getFormatString());
		}
		return ret;
	}

	/**
	 * シンボルに値を設定します
	 */
	public void setValue(String[] values) {
		Analog4Data value = Analog4Data.valueOf(values);
		for (Iterator it = actions.iterator(); it.hasNext();) {
			((ValueSetter) it.next()).writeValue(value);
		}
		/** 再読込み */
		updateProperty();
	}

	/**
	 * 書き込み先の追加はしない。
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#addDestination(Attributes)
	 */
	public void addDestination(Map params) {
	}

	/**
	 * 書き込み先を設定します。
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#addElement(Attributes)
	 */
	public void addValueSetter(ValueSetter setter) {
		if (holderName == null) {
			String phname = setter.getDestination();
			int p = phname.indexOf('_');
			this.providerName = phname.substring(0, p);
			this.holderName = phname.substring(p + 1);
		}
		actions.add(setter);
	}

	/**
	 * 書き込み先のプロバイダ＋ホルダ名を返します。
	 */
	public String[] getDestinations() {
		String[] ret = new String[actions.size()];
		int i = 0;
		for (Iterator it = actions.iterator(); it.hasNext(); i++) {
			ret[i] = ((ValueSetter) it.next()).getDestination();
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#getDialog(java.awt.Window,
	 *      org.F11.scada.applet.symbol.SymbolCollection, java.util.List)
	 */
	public WifeDialog getDialog(
			Window window,
			SymbolCollection collection,
			List para) {
		String dialogId = getProperty("dlgname");
		if (dialogId == null) {
			System.out.println("dialogId null");
			dialogId = "4";
		}

		WifeDialog d = DialogFactory.get(window, dialogId);
		if (d == null)
			System.out.println(this.getClass().getName() + "dialog null");
		d.setListIterator(collection.listIterator(para));
		d.setTitle(dlgTitle);
		return d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#getPoint()
	 */
	public Point getPoint() {
		return dialogPoint;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#isEditable()
	 */
	public boolean isEditable() {
		return editable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#setEditable(boolean[])
	 */
	public void setEditable(boolean[] editable) {
		this.editable = true;
		for (int i = 0; i < editable.length; i++) {
			if (!editable[i]) {
				this.editable = false;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#setPoint(java.awt.Point)
	 */
	public void setPoint(Point point) {
		dialogPoint = point;
	}

	/**
	 * 値を編集する為のダイアログ名を返します。
	 */
	public String getSecondDialogName() {
		return secondDialogName;
	}

	/**
	 * 値を編集する為のダイアログ名を設定します。
	 */
	public void setSecondDialogName(String secondDialogName) {
		this.secondDialogName = secondDialogName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#isTabkeyMove()
	 */
	public boolean isTabkeyMove() {
		return false;
	}

	public String getDialogName() {
		return dlgTitle;
	}

	public void disConnect() {
		actions.clear();
		if (null != authentication) {
			authentication.removeEditable(this);
		}
		super.disConnect();
	}
}
