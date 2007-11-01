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

import javax.swing.JDialog;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.dialog.DialogFactory;
import org.F11.scada.applet.dialog.WifeDialog;
import org.F11.scada.applet.schedule.DefaultScheduleModel;
import org.F11.scada.applet.schedule.ScheduleModel;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.util.ComponentUtil;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 * @author hori
 */
public class TextScheduleSymbolEditable extends TextAnalogSymbol implements
		ScheduleEditable {
	private static final long serialVersionUID = -3912966575546294875L;

	private final Logger logger = Logger
			.getLogger(TextScheduleSymbolEditable.class);
	/** ダイアログ表示位置 */
	private Point dialogPoint;
	/** 編集可能フラグ */
	private boolean editable;
	/** アクションのリスト */
	private List actions;
	/** スケジュールモデルの参照 */
	private ScheduleModel scheduleModel;
	/** グループ書込み対象ホルダ指定 */
	private String grProviderName;
	private String grHolderName;
	/** 認証コントロールの参照 */
	private Authenticationable authentication;
	/** ダイアログのタイトル */
	private String dlgTitle;

	/**
	 * コンストラクタ
	 * 
	 * @param property SymbolProperty オブジェクト
	 * @param authentication
	 */
	public TextScheduleSymbolEditable(
			SymbolProperty property,
			Authenticationable authentication) {
		super(property);
		addMouseListener();
		actions = new ArrayList();
		this.authentication = authentication;
		this.authentication.addEditable(this);
		this.scheduleModel = new DefaultScheduleModel(this.authentication);
		dlgTitle = getProperty("dlgtitle");
	}

	public TextScheduleSymbolEditable(
			String dataProviderName,
			String dataHolderName) {
		super(dataProviderName, dataHolderName);
		addMouseListener();
		actions = new ArrayList();
	}

	public TextScheduleSymbolEditable(SymbolProperty property) {
		super(property);
		addMouseListener();
		actions = new ArrayList();
		this.scheduleModel = new DefaultScheduleModel();
		this.scheduleModel.setEditable(new boolean[] { true });
		dlgTitle = getProperty("dlgtitle");
	}

	public TextScheduleSymbolEditable() {
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

			public void mouseEntered(MouseEvent e) {
				if (HandCursorListener.handcursor) {
					Component comp = (Component) e.getSource();
					comp.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
			}
		});
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
		JDialog dlg = getDialog(frame, (SymbolCollection) getParent(), para);
		System.out.println(dlg.requestFocusInWindow());
		dlg.show();
	}

	/**
	 * スケジュールモデルを取得します。
	 */
	public ScheduleModel getScheduleModel() {
		return scheduleModel;
	}

	public String getValue() {
		return getText();
	}

	public void setValue(String value) {
		for (Iterator it = actions.iterator(); it.hasNext();) {
			((ValueSetter) it.next()).writeValue(value);
		}
		/** 再読込み */
		updateProperty();
	}

	public ConvertValue getConvertValue() {
		DataHolder dh = Manager.getInstance().findDataHolder(
				grProviderName,
				grHolderName);
		WifeData wd = (WifeData) dh.getValue();
		if (!(wd instanceof WifeDataAnalog))
			return null;

		return (ConvertValue) dh.getParameter("convert");
	}

	public void addScheduleHolder(String providerName, String holderName) {
		scheduleModel.addGroup(providerName, holderName, "");
	}

	public WifeDialog getDialog(
			Window window,
			SymbolCollection collection,
			List para) {
		String dialogId = getProperty("dlgname");
		if (dialogId == null) {
			logger.warn("dialogId null");
			dialogId = "3";
		}
		WifeDialog d = DialogFactory.get(window, dialogId);
		if (d == null) {
			logger.warn(getClass().getName() + "dialog null");
		}
		d.setListIterator(collection.listIterator(para));
		d.setTitle(dlgTitle);

		return d;
	}

	public Point getPoint() {
		return dialogPoint;
	}

	public void setPoint(Point point) {
		dialogPoint = point;
	}

	public void setEditable(boolean[] editable) {
		this.editable = true;
		for (int i = 0; i < editable.length; i++) {
			if (!editable[i]) {
				this.editable = false;
			}
		}
	}

	public boolean isEditable() {
		return editable;
	}

	public String[] getDestinations() {
		String[] ret = new String[actions.size()];
		int i = 0;
		for (Iterator it = actions.iterator(); it.hasNext(); i++) {
			ret[i] = ((ValueSetter) it.next()).getDestination();
		}
		return ret;
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
		if (this.grHolderName == null) {
			String phname = setter.getDestination();
			int p = phname.indexOf('_');
			this.grProviderName = phname.substring(0, p);
			this.grHolderName = phname.substring(p + 1);
		}
		actions.add(setter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#isTabkeyMove()
	 */
	public boolean isTabkeyMove() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.symbol.TextAnalogSymbol#disConnect()
	 */
	public void disConnect() {
		scheduleModel.disConnect();
		actions.clear();
		if (null != authentication) {
			authentication.removeEditable(this);
		}
		super.disConnect();
	}
}
