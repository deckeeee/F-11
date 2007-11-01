package org.F11.scada.applet.symbol;

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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.Box;
import javax.swing.BoxLayout;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.DataReferencerOwner;
import jp.gr.javacons.jim.DataValueChangeEvent;
import jp.gr.javacons.jim.DataValueChangeListener;

import org.F11.scada.data.WifeData;

/**
 * �X�e�[�^�X�o�[�N���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class StatusBar extends Box implements CompositeProperty,
										DataReferencerOwner, DataValueChangeListener, SymbolCollection, ActionListener {

	private static final long serialVersionUID = -9065562415194991838L;

	/** DataHolder�^�C�v���ł��B */
	private static final Class[][] WIFE_TYPE_INFO = new Class[][] {
		{DataHolder.class, WifeData.class}
	};

	/** �_�ŗp�̃^�C�}�[�ł� */
	private WifeTimer timer = WifeTimer.getInstance();
	/** �_�Ńt���O�ł��B */
	private boolean isBlink = false;
	/** �v���p�e�B�̃Z�b�g�ł��B */
	private List propertys = new ArrayList();

	public StatusBar() {
		super(BoxLayout.X_AXIS);
		init();
	}

	public StatusBar(SymbolProperty property) {
		this();
	}

	private void init() {
		timer.addActionListener(this);
	}

	/**
	 * �f�[�^�ύX�C�x���g����
	 */
	public void dataValueChanged(DataValueChangeEvent evt) {
		Object o = evt.getSource();
		if (!(o instanceof DataHolder)) {
			return;
		}
		updateProperty();
	}

	/**
	 * �v���p�e�B��ύX���܂��B
	 */
	public void updateProperty() {
		/** false�f�t�H���g */
		if ("true".equals(getProperty("opaque")))
			this.setOpaque(true);
		else
			this.setOpaque(false);

		Color color = ColorFactory.getColor(getProperty("foreground"));
		if (color != null)
			this.setForeground(color);

		color = ColorFactory.getColor(getProperty("background"));
		if (color != null)
			this.setBackground(color);

		/** true�f�t�H���g */
		if ("false".equals(getProperty("visible")))
			this.setVisible(false);
		else
			this.setVisible(true);
	}

	/**
	 * �v���p�e�B��ݒ肵�܂��B
	 * @param property   �R���|�W�b�g�p�^�[��
	 */
	public void addCompositeProperty(CompositeProperty property) {
		this.propertys.add(property);
	}

	/**
	 * �v���p�e�B���擾���܂��B
	 * @param key   �v���p�e�B�̖���
	 */
	public String getProperty(String key) {
		ListIterator li = propertys.listIterator(propertys.size());
		while (li.hasPrevious()) {
			CompositeProperty prop = (CompositeProperty)li.previous();
			if (prop != null && prop.getProperty(key) != null)
				return prop.getProperty(key);
		}
		return null;
	}

	/**
	 * DataHolder�^�C�v����Ԃ��܂��B
	 */
	public Class[][] getReferableDataHolderTypeInfo(DataReferencer dr) {
		return WIFE_TYPE_INFO;
	}

	/**
	 * �y�[�W�ɃR���|�[�l���g��ǉ����܂��B
	 */
	public Component addPageSymbol(Component comp) {
		return add(comp);
	}

	/**
	 * �V���{���C�e���[�^�[��Ԃ��܂�
	 */
	public ListIterator listIterator(List para) {
		return new ContainerListIterator(this, (Class)para.get(0), (Editable)para.get(1));
	}

	public void actionPerformed(ActionEvent e) {
		if (isBlink) {
			setVisible(timer.isShowTime());
		}
	}
}
