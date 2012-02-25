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
 * �V���{���I�u�W�F�N�g�̊��N���X�ł��B
 * 
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public abstract class Symbol extends JLabel implements CompositeProperty,
		DataReferencerOwner, DataValueChangeListener, ReferencerOwnerSymbol {
	/** DataHolder�^�C�v���ł��B */
	private static final Class[][] WIFE_TYPE_INFO = new Class[][] { {
			DataHolder.class, WifeData.class } };

	/** �_�ŗp�̃^�C�}�[�ł� */
	private WifeTimer timer = WifeTimer.getInstance();
	/** �_�Ńt���O�ł��B */
	private boolean isBlink = false;
	/** �v���p�e�B�̃Z�b�g�ł��B */
	private List propertys = new CopyOnWriteArrayList();
	/** �_�ł̃��X�i�[�N���X */
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
	 * @param property SymbolProperty �I�u�W�F�N�g
	 */
	public Symbol(SymbolProperty property) {
		super();

		/* �_�ł̎��� */
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
	 * �v���p�e�B��ύX���܂��B �T�u�N���X��updatePropertyImpl���\�b�h���������A�T�u�N���X�ŗL�̃v���p�e�B�[
	 * �̍X�V�������s���܂��B���̃��\�b�h�̓I�[�o�[���C�h�ł��Ȃ��悤��final�錾����Ă��܂��B <!-- Template Method
	 * Pattern -->
	 */
	public final void updateProperty() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				/** false�f�t�H���g */
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

				/** true�f�t�H���g */
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
	 * �T�u�N���X�ŗL�̃v���p�e�B�[�̍X�V�������s���܂��B
	 */
	protected abstract void updatePropertyImpl();

	/**
	 * �v���p�e�B��ݒ肵�܂��B
	 * 
	 * @see org.F11.scada.applet.symbol.CompositeProperty#addCompositeProperty(CompositeProperty)
	 * @param property �R���|�W�b�g�p�^�[��
	 */
	public void addCompositeProperty(CompositeProperty property) {
		if (property != null) {
			propertys.add(property);
		}
	}

	/**
	 * �v���p�e�B�̃Z�b�g����l���擾���܂��B
	 * 
	 * @see org.F11.scada.applet.symbol.CompositeProperty#getProperty(String)
	 * @param key �v���p�e�B�̃L�[������
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
	 * DataHolder�^�C�v����Ԃ��܂��B
	 * 
	 * @see jp.gr.javacons.jim.DataReferencerOwner#getReferableDataHolderTypeInfo(DataReferencer)
	 */
	public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
		return WIFE_TYPE_INFO;
	}

	/**
	 * �f�[�^�ύX�C�x���g����
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
	 * �e�V���{����Manager����o�^�������܂��B
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
	 * �_�ŏ�Ԃ�Ԃ��܂��B
	 * 
	 * @return �_�ŏ�Ԃ�Ԃ��܂�
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
