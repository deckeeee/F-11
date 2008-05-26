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

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * シンボルイテレータークラスです。
 * 
 * ベースコンポーネントよりEditableクラスを抽出し、シンボルのリスト
 * イテレーターを返します。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ContainerListIterator implements ListIterator {
	/** このコンポーネントに載っているシンボルのリスト */
	private List symbols;
	/** シンボルイテレーター */
	private ListIterator listIterator;
	/** 正逆フラグ */
	private boolean isPreviousMode;

	/**
	 * パッケージプライベート・コンストラクタです。
	 * @param compo ベースとなるコンテナー
	 * @param symbolClass 抽出するシンボルのクラス
	 * @param current イテレーターの最初に持ってくるシンボル
	 */
	ContainerListIterator(Container compo, Class symbolClass, Editable current) {
		symbols = new ArrayList();

		int no = 0;
		for (int i = 0; i < compo.getComponentCount(); i++) {
			if (current == compo.getComponent(i)) {
				no = i;
				break;
			}
		}
		for (int i = 0; i < compo.getComponentCount(); i++) {
			Component o = compo.getComponent(no);
			if (symbolClass.isInstance(o) && o instanceof Editable) {
				Editable edit = (Editable)o;
				if (edit.isEditable() && o instanceof Symbol) {
					Symbol symbol = (Symbol)o;
					if (symbol.isBlink() || symbol.isVisible()) {
						Point po = getLocationOnScreen(symbol);
						po.translate(0, symbol.getHeight());
						edit.setPoint(po);
						symbols.add(edit);
					}
				}
			}

			no++;
			if (compo.getComponentCount() <= no)
				no = 0;
		}
	}

	private Point getLocationOnScreen(Symbol symbol) {
		boolean b = symbol.isVisible();
		try {
			symbol.setVisible(true);
			return symbol.getLocationOnScreen();
		} finally {
			if (!b) {
				symbol.setVisible(b);
			}
		}
	}

	public boolean hasNext() {
		return true;
	}

	public Object next() {
		if (listIterator == null)
			listIterator = symbols.listIterator();

		if (isPreviousMode) {
			isPreviousMode = false;
			try {
				listIterator.next();
			} catch (NoSuchElementException ex) {
				listIterator = symbols.listIterator(symbols.size());
				listIterator.next();
			}
		}

		try {
			return listIterator.next();
		} catch (NoSuchElementException ex) {
			listIterator = symbols.listIterator();
			return listIterator.next();
		}
	}

	public boolean hasPrevious() {
		return true;
	}

	public Object previous() {
		if (listIterator == null)
			listIterator = symbols.listIterator(symbols.size());
		if (!isPreviousMode) {
			isPreviousMode = true;
			try {
				listIterator.previous();
			} catch (NoSuchElementException ex) {
				listIterator = symbols.listIterator(symbols.size());
				listIterator.previous();
			}
		}

		try {
			return listIterator.previous();
		} catch (NoSuchElementException ex) {
			listIterator = symbols.listIterator(symbols.size());
			return listIterator.previous();
		}
	}

	public int nextIndex() {
		int index = listIterator.nextIndex();
		if (isPreviousMode && index == symbols.size()) {
			ListIterator lit = symbols.listIterator();
			index = lit.nextIndex();
		}
		return index;
	}

	public int previousIndex() {
		int index = listIterator.previousIndex();
		if (!isPreviousMode && index < 0) {
			ListIterator lit = symbols.listIterator(symbols.size());
			index = lit.previousIndex();
		}
		return index;
	}

	public void add(Object o) {
		// non suport
		throw new UnsupportedOperationException();
	}

	public void remove() {
		// non suport
		throw new UnsupportedOperationException();
	}

	public void set(Object o) {
		// non suport
		throw new UnsupportedOperationException();
	}
}
