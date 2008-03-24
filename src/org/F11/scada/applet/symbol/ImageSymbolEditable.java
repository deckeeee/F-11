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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.dialog.DialogFactory;
import org.F11.scada.applet.dialog.WifeDialog;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.util.ComponentUtil;
import org.xml.sax.Attributes;

/**
 * 編集可能なイメージグラフィックを表示するシンボルクラスです。
 * 
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class ImageSymbolEditable extends ImageSymbol implements DigitalEditable {

	private static final long serialVersionUID = 4694682226718183275L;
	/** ダイアログ表示位置 */
	private Point dialogPoint;
	/** 編集可能フラグ */
	private boolean editable;
	/** ダイアログ名称 */
	private String dlgName;
	/** ダイアログタイトル */
	private String dlgTitle;
	/** ボタン名称のリスト */
	private List buttonTexts;
	/** アクションのリスト */
	private List actions;
	private Authenticationable authentication;

	/**
	 * Constructor for ImageSymbolEditable.
	 * 
	 * @param property
	 * @param authentication
	 */
	public ImageSymbolEditable(
			SymbolProperty property,
			Authenticationable authentication) {
		this(property);
		this.authentication = authentication;
		this.authentication.addEditable(this);
	}

	/**
	 * Constructor for ImageSymbolEditable.
	 * 
	 * @param property
	 */
	public ImageSymbolEditable(SymbolProperty property) {
		super(property);

		dlgName = getProperty("dlgname");
		dlgTitle = getProperty("dlgtitle");

		/* マウスクリックイベントの登録 */
		final Component myComp = this;
		addMouseListener(new MouseAdapter() {
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
		
		buttonTexts = new ArrayList();
		actions = new ArrayList();
	}

	/**
	 * Constructor for ImageSymbolEditable.
	 */
	public ImageSymbolEditable() {
		this(null);
	}

	/**
	 * マウスクリックイベント
	 */
	public void this_mouseClicked(java.awt.event.MouseEvent e) {
		if (!isEditable()) {
			return;
		}

		final Window wd = WifeUtilities.getDialogParent(this);
		final ArrayList para = new ArrayList();
		para.add(this.getClass());
		para.add(this);
		JDialog dlg = getDialog(wd, (SymbolCollection) getParent(), para);
		dlg.show();
	}

	/**
	 * @see org.F11.scada.applet.symbol.DigitalEditable#pushButton(int)
	 */
	public void pushButton(int n) {
		int actionNo = n - 21;
		if (actions.size() < actionNo)
			return;
		List acs = (List) actions.get(actionNo);
		for (Iterator it = acs.iterator(); it.hasNext();) {
			((ValueSetter) it.next()).writeValue(null);
		}
	}

	/**
	 * @see org.F11.scada.applet.symbol.DigitalEditable#getButtonString(int)
	 */
	public String getButtonString(int n) {
		if (0 <= n && n < buttonTexts.size())
			return (String) buttonTexts.get(n);
		return null;
	}

	/**
	 * @see org.F11.scada.applet.symbol.Editable#getDialog(Window,
	 *      SymbolCollection, List)
	 */
	public WifeDialog getDialog(
			Window window,
			SymbolCollection collection,
			List para) {
		WifeDialog d = DialogFactory.get(window, dlgName);
		d.setListIterator(collection.listIterator(para));
		d.setTitle(dlgTitle);
		return d;
	}

	/**
	 * @see org.F11.scada.applet.symbol.Editable#getPoint()
	 */
	public Point getPoint() {
		return dialogPoint;
	}

	/**
	 * @see org.F11.scada.applet.symbol.Editable#setPoint(Point)
	 */
	public void setPoint(Point point) {
		dialogPoint = point;
	}

	/**
	 * @see org.F11.scada.applet.symbol.Editable#setEditable(boolean)
	 */
	public void setEditable(boolean[] editable) {
		this.editable = true;
		for (int i = 0; i < editable.length; i++) {
			if (!editable[i]) {
				this.editable = false;
			}
		}
	}

	/**
	 * @see org.F11.scada.applet.symbol.Editable#isEditable()
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * ベースシステムのユーザー認証により Subject が変更されたときにディスパッチされます。
	 * 編集可能なシンボルが保持している、データプロバイダ名＋データホルダー名をアンダーバー で結合した文字列配列を返します。
	 * 
	 * @return データプロバイダ名＋データホルダー名をアンダーバーで結合した文字列配列
	 */
	public String[] getDestinations() {
		int sz = 0;
		for (Iterator it = actions.iterator(); it.hasNext();) {
			sz += ((List) it.next()).size();
		}
		String[] ret = new String[sz];
		int i = 0;
		for (Iterator it = actions.iterator(); it.hasNext();) {
			List acs = (List) it.next();
			for (Iterator it2 = acs.iterator(); it2.hasNext(); i++) {
				ret[i] = ((ValueSetter) it2.next()).getDestination();
			}
		}
		return ret;
	}

	/**
	 * ボタン名称を設定します。
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#addDestination(Map)
	 */
	public void addDestination(Map atts) {
		buttonTexts.add(atts.get("buttontext"));
		actions.add(new ArrayList());
	}

	/**
	 * シンボルに指示動作パターンを追加します。
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#addElement(Attributes)
	 */
	public void addValueSetter(ValueSetter setter) {
		List acs = (List) actions.get(actions.size() - 1);
		acs.add(setter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#isTabkeyMove()
	 */
	public boolean isTabkeyMove() {
		return false;
	}

	public void disConnect() {
		actions.clear();
		buttonTexts.clear();
		if (null != authentication) {
			authentication.removeEditable(this);
		}
		super.disConnect();
	}
}
