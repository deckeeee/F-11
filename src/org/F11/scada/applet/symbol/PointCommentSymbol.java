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
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.F11.scada.applet.dialog.DialogFactory;
import org.F11.scada.applet.dialog.WifeDialog;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.server.comment.PointCommentDto;
import org.F11.scada.server.comment.PointCommentService;
import org.F11.scada.util.ComponentUtil;
import org.F11.scada.util.RmiUtil;
import org.xml.sax.Attributes;

/**
 * ポイントコメントを入力するシンボルクラスです。
 * 
 * @author maekawa
 */
public class PointCommentSymbol extends ImageSymbol implements CommentEditable {

	private static final long serialVersionUID = -879506438434994435L;
	/** ダイアログ表示位置 */
	private Point dialogPoint;
	/** 編集可能フラグ */
	private boolean editable;
	/** ダイアログ名称 */
	private String dlgName;
	/** ダイアログタイトル */
	private String dlgTitle;
	/** 設定するポイントのリスト */
	private List destinations;
	/** 設定するポイントコメント */
	private String comment;
	/** ポイントコメントサービス */
	private PointCommentService service;
	private Authenticationable authentication;

	/**
	 * Constructor for ImageSymbolEditable.
	 * 
	 * @param property
	 * @param authentication
	 */
	public PointCommentSymbol(
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
	private PointCommentSymbol(SymbolProperty property) {
		super(property);

		dlgName = getProperty("dlgname");
		dlgTitle = getProperty("dlgtitle");

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
		});
		addMouseListener(new HandCursorListener(this));
		destinations = new ArrayList();
		service = lookup();
	}

	/**
	 * マウスクリックイベント
	 */
	public void this_mouseClicked(MouseEvent e) {
		if (isEditable()) {
			final Window wd = (Window) SwingUtilities.getAncestorOfClass(
					Window.class,
					this);
			final ArrayList para = new ArrayList();
			para.add(getClass());
			para.add(this);
			JDialog dlg = getDialog(wd, (SymbolCollection) getParent(), para);
			dlg.show();
		}
	}

	/**
	 * @see org.F11.scada.applet.symbol.Editable#getDialog(Window,
	 *      SymbolCollection, List)
	 */
	public WifeDialog getDialog(
			Window window,
			SymbolCollection collection,
			List para) {
		WifeDialog dialog = DialogFactory.get(window, dlgName);
		dialog.setListIterator(collection.listIterator(para));
		dialog.setTitle(dlgTitle);
		return dialog;
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
		return (String[]) destinations.toArray(new String[0]);
	}

	/**
	 * ボタン名称を設定します。
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#addDestination(Map)
	 */
	public void addDestination(Map atts) {
	}

	private void setComment() {
		if (destinations.size() == 1) {
			String value = (String) destinations.get(0);
			PointCommentDto dto = createPointCommentDto(value);
			try {
				setComment(dto);
			} catch (RemoteException e) {
				try {
					service = lookup();
					setComment(dto);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void setComment(PointCommentDto dto) throws RemoteException {
		PointCommentDto data = service.getPointCommentDto(dto);
		if (data != null)
			comment = data.getComment();
	}

	/**
	 * シンボルに指示動作パターンを追加します。
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#addElement(Attributes)
	 */
	public void addValueSetter(ValueSetter setter) {
		String phname = setter.getDestination();
		destinations.add(phname);
		setComment();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#isTabkeyMove()
	 */
	public boolean isTabkeyMove() {
		return false;
	}

	public void setComment(String comment) {
		this.comment = comment;
		List pointComments = createPointComments();
		try {
			setPointComments(pointComments);
		} catch (RemoteException e) {
			try {
				service = lookup();
				setPointComments(pointComments);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void setPointComments(List pointComments) throws RemoteException {
		for (Iterator i = pointComments.iterator(); i.hasNext();) {
			PointCommentDto dto = (PointCommentDto) i.next();
			service.setPointComment(dto);
		}
	}

	private PointCommentService lookup() {
		return (PointCommentService) RmiUtil
				.lookupServer(PointCommentService.class);
	}

	private List createPointComments() {
		ArrayList result = new ArrayList(destinations.size());
		for (Iterator i = destinations.iterator(); i.hasNext();) {
			String value = (String) i.next();
			PointCommentDto dto = createPointCommentDto(value);
			result.add(dto);
		}
		return result;
	}

	private PointCommentDto createPointCommentDto(String value) {
		int sep = value.indexOf('_');
		String provider = value.substring(0, sep);
		String holder = value.substring(sep + 1);
		PointCommentDto dto = new PointCommentDto();
		dto.setProvider(provider);
		dto.setHolder(holder);
		dto.setComment(comment);
		return dto;
	}

	public String getComment() {
		return comment;
	}

	public void disConnect() {
		destinations.clear();
		if (null != authentication) {
			authentication.removeEditable(this);
		}
		super.disConnect();
	}
}
