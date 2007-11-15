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
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.F11.scada.applet.dialog.WifeDialog;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.util.ComponentUtil;
import org.xml.sax.Attributes;

/**
 * 編集可能なイメージグラフィックを表示するシンボルクラスです。
 * 
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class ImageSymbolFixedEditable extends ImageSymbol implements
		DigitalEditable {

	private static final long serialVersionUID = 3562271830679503861L;
	/** 編集可能フラグ */
	private boolean editable;
	/** アクションのリスト */
	private List actions;
	private Authenticationable authentication;

	/**
	 * Constructor for ImageSymbolEditable.
	 * 
	 * @param property
	 * @param authentication
	 */
	public ImageSymbolFixedEditable(
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
	public ImageSymbolFixedEditable(SymbolProperty property) {
		super(property);

		/* マウスクリックイベントの登録 */
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

			public void mouseEntered(MouseEvent e) {
				if (HandCursorListener.handcursor) {
					Component comp = (Component) e.getSource();
					comp.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
			}
		});

		actions = new ArrayList();
	}

	/**
	 * Constructor for ImageSymbolEditable.
	 */
	public ImageSymbolFixedEditable() {
		this(null);
	}

	/**
	 * マウスクリックイベント
	 */
	public void this_mouseClicked(MouseEvent e) {
		if (!isEditable()) {
			return;
		}
		pushButton(0);
	}

	/**
	 * @see org.F11.scada.applet.symbol.DigitalEditable#pushButton(int)
	 */
	public void pushButton(int n) {
		for (Iterator i = actions.iterator(); i.hasNext();) {
			List action = (List) i.next();
			for (Iterator it = action.iterator(); it.hasNext();) {
				ValueSetter setter = (ValueSetter) it.next();
				if (setter.isFixed()) {
					setter.writeValue(null);
				}
			}
		}
	}

	/**
	 * @see org.F11.scada.applet.symbol.DigitalEditable#getButtonString(int)
	 */
	public String getButtonString(int n) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.F11.scada.applet.symbol.Editable#getDialog(Window,
	 *      SymbolCollection, List)
	 */
	public WifeDialog getDialog(
			Window window,
			SymbolCollection collection,
			List para) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.F11.scada.applet.symbol.Editable#getPoint()
	 */
	public Point getPoint() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see org.F11.scada.applet.symbol.Editable#setPoint(Point)
	 */
	public void setPoint(Point point) {
		throw new UnsupportedOperationException();
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
		if (null != authentication) {
			authentication.removeEditable(this);
		}
		super.disConnect();
	}
}
