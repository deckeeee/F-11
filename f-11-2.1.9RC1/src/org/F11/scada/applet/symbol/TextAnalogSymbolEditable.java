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
import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.security.auth.login.Authenticationable;
import org.F11.scada.util.ComponentUtil;
import org.xml.sax.Attributes;

/**
 * �ҏW�\�ȃA�i���O�l�V���{���ł��B���[�U�[���F�� true ���߂��ꂽ�ꍇ�ɁA �e���L�[�_�C�A���O��\�����܂��B���[�U�[���F�� false
 * ���߂��ꂽ�ꍇ�́A �N���b�N�𖳎����e���L�[�_�C�A���O��\�����܂���B
 * 
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class TextAnalogSymbolEditable extends TextAnalogSymbol implements
		TenkeyEditable {
	private static final long serialVersionUID = -5915497980060573755L;
	/** �_�C�A���O�\���ʒu */
	private Point dialogPoint;
	/** �ҏW�\�t���O */
	private boolean editable;
	/** �A�N�V�����̃��X�g */
	private List actions;
	/** �����ݑΏۃz���_�w�� */
	private String providerName;
	private String holderName;
	/** �_�C�A���O�^�C�g�� */
	private String dlgTitle;
	private Authenticationable authentication;

	/**
	 * Constructor for TextAnalogSymbolEditable.
	 * 
	 * @param property SymbolProperty �I�u�W�F�N�g
	 * @param authentication
	 */
	public TextAnalogSymbolEditable(
			SymbolProperty property,
			Authenticationable authentication) {
		this(property);
		this.authentication = authentication;
		this.authentication.addEditable(this);
		actions = new ArrayList();
	}

	/**
	 * Constructor for TextAnalogSymbolEditable.
	 * 
	 * @param property SymbolProperty �I�u�W�F�N�g
	 */
	public TextAnalogSymbolEditable(SymbolProperty property) {
		super(property);
		addMouseListener();
		actions = new ArrayList();
		dlgTitle = getProperty("dlgtitle");
	}

	/**
	 * Constructor for TextAnalogSymbolEditable.
	 * 
	 * @param dataProviderName �v���o�C�_��
	 * @param dataHolderName �z���_��
	 */
	public TextAnalogSymbolEditable(
			String dataProviderName,
			String dataHolderName) {
		super(dataProviderName, dataHolderName);
		addMouseListener();
		actions = new ArrayList();
	}

	/**
	 * Constructor for TextAnalogSymbolEditable.
	 */
	public TextAnalogSymbolEditable() {
		this(null);
	}

	/**
	 * �}�E�X�C�x���g�̓o�^
	 */
	private void addMouseListener() {
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
	}

	/**
	 * �}�E�X�N���b�N�C�x���g
	 * 
	 * @param e �}�E�X�C�x���g�I�u�W�F�N�g
	 */
	public void this_mouseClicked(java.awt.event.MouseEvent e) {
		if (!isEditable()) {
			return;
		}
		final Frame frame = WifeUtilities.getParentFrame(this);
		final ArrayList para = new ArrayList();
		para.add(this.getClass());
		para.add(this);
		final WifeDialog dlg = getDialog(
				frame,
				(SymbolCollection) getParent(),
				para);
		dlg.show();
	}

	public String getValue() {
		return getText();
	}

	public void setValue(String value) {
		for (Iterator it = actions.iterator(); it.hasNext();) {
			((ValueSetter) it.next()).writeValue(value);
		}
		/** �ēǍ��� */
		updateProperty();
	}

	public double getConvertMin() {
		DataHolder dh = Manager.getInstance().findDataHolder(
				providerName,
				holderName);
		WifeData wd = (WifeData) dh.getValue();
		if (!(wd instanceof WifeDataAnalog))
			return 0;

		ConvertValue conv = (ConvertValue) dh.getParameter("convert");
		return conv.getConvertMin();
	}

	public double getConvertMax() {
		DataHolder dh = Manager.getInstance().findDataHolder(
				providerName,
				holderName);
		WifeData wd = (WifeData) dh.getValue();
		if (!(wd instanceof WifeDataAnalog))
			return 0;

		ConvertValue conv = (ConvertValue) dh.getParameter("convert");
		return conv.getConvertMax();
	}

	public String getFormatString() {
		if (pattern != null)
			return pattern;

		DataHolder dh = Manager.getInstance().findDataHolder(
				providerName,
				holderName);
		WifeData wd = (WifeData) dh.getValue();
		if (!(wd instanceof WifeDataAnalog))
			return "0.0";

		ConvertValue conv = (ConvertValue) dh.getParameter("convert");
		return conv.getPattern();
	}

	public WifeDialog getDialog(
			Window window,
			SymbolCollection collection,
			java.util.List para) {

		String dialogId = getProperty("dlgname");
		if (dialogId == null) {
			System.out.println("dialogId null");
			dialogId = "1";
		}

		WifeDialog d = DialogFactory.get(window, dialogId);
		if (d == null)
			System.out.println(this.getClass().getName() + "dialog null");
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

	public synchronized void setEditable(boolean[] editable) {
		this.editable = true;
		for (int i = 0; i < editable.length; i++) {
			if (!editable[i]) {
				this.editable = false;
			}
		}
	}

	public synchronized boolean isEditable() {
		return editable;
	}

	/*
	 * @see org.F11.scada.applet.symbol.Editable#getDestinations()
	 */
	public String[] getDestinations() {
		String[] ret = new String[actions.size()];
		int i = 0;
		for (Iterator it = actions.iterator(); it.hasNext(); i++) {
			ret[i] = ((ValueSetter) it.next()).getDestination();
		}
		return ret;
	}

	/**
	 * �������ݐ�̒ǉ��͂��Ȃ��B
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#addDestination(Attributes)
	 */
	public void addDestination(Map atts) {
	}

	/**
	 * �������ݐ��ݒ肵�܂��B
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.applet.symbol.Editable#isTabkeyMove()
	 */
	public boolean isTabkeyMove() {
		String dialogId = getProperty("dlgname");
		if (dialogId == null) {
			System.out.println("dialogId null");
			dialogId = "1";
		}

		return "1".equals(dialogId) && (isVisible() || isBlink());
	}

	public String getDialogTitle() {
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
