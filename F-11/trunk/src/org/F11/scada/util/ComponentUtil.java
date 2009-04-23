/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2007 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public abstract class ComponentUtil {
	/** HTML�̊J�n������ */
	public static final String HTML_START = "<html><body><p>";
	/** HTML�̏I�������� */
	public static final String HTML_END = "</p></body></html>";

	/**
	 * �R���|�[�l���g���Ƀ}�E�X�|�C���g�����邩���肵�܂��B
	 * 
	 * @param rectangle �R���|�[�l���g�ʒu
	 * @param point �}�E�X�|�C���g�ʒu
	 * @return �R���|�[�l���g���Ƀ}�E�X�|�C���g������ꍇ true�A�����ꍇ false ��Ԃ��܂��B
	 */
	public static boolean contains(Rectangle rectangle, Point point) {
		return point.x >= 0
			&& point.y >= 0
			&& rectangle.width >= point.x
			&& rectangle.height >= point.y;
	}

	/**
	 * �R���e�i���̑ΏۃN���X�q�R���|�[�l���g��Ԃ��܂��B
	 * 
	 * @param klass ��������N���X
	 * @param container �����ΏۂɂȂ�R���e�i�I�u�W�F�N�g
	 * @return �����ΏۃR���e�i�I�u�W�F�N�g���ɁA��������N���X�����݂���ꍇ�͂��̃R���|�[�l���g�𑶍݂��Ȃ��ꍇ�� null ��Ԃ��܂��B
	 */
	public static Component getChildrenComponent(
			Class<?> klass,
			Container container) {
		Component ret = null;
		Component[] components = container.getComponents();
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			if (klass.isInstance(component)) {
				ret = component;
				break;
			}
		}
		return ret;
	}

	/**
	 * {@link SwingUtilities#getAncestorOfClass}�̌^���_�ŁB �R���|�[�l���g�K�w�� comp
	 * �̏�ʂ��������邽�߂̊ȈՃ��\�b�h�ł���A�������� c �N���X�̍ŏ��̃I�u�W�F�N�g��Ԃ��܂��Bc �N���X��������Ȃ��ꍇ�� null
	 * ��Ԃ��܂��B
	 * 
	 * @param <C> ��������N���X
	 * @param c ��������N���X���e����
	 * @param comp �����ΏۃR���|�[�l���g
	 * @return ��ʂɌ����Ώۂ̃N���X������΂��̃I�u�W�F�N�g�𖳂��ꍇ�� null ��Ԃ��܂�
	 */
	public static <C extends Container> C getAncestorOfClass(
			Class<C> c,
			Component comp) {
		return (C) SwingUtilities.getAncestorOfClass(c, comp);
	}

	public static void addLabel(GridBagConstraints c, JPanel panel, JLabel label) {
		panel.add(label);
		c.weightx = 1.0;
		c.gridwidth = 1;
		panel.add(label, c);
	}

	public static void addTextArea(
			GridBagConstraints c,
			JPanel panel,
			JTextField text,
			String string) {
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		text.setText(string);
		panel.add(text, c);
	}

	/**
	 * C�N���X�̐e�̒����ɃR���|�[�l���g��z�u���܂��B
	 * 
	 * @param <C> �e�N���X
	 * @param c �e�N���X
	 * @param comp �����ɔz�u����R���|�[�l���g
	 */
	public static <C extends Window> void setCenter(Class<C> c, Component comp) {
		Window parent = ComponentUtil.getAncestorOfClass(c, comp);
		Dimension dlgSize = comp.getSize();
		Dimension frmSize = parent.getSize();
		Point loc = parent.getLocation();
		comp.setLocation(
			(frmSize.width - dlgSize.width) / 2 + loc.x,
			(frmSize.height - dlgSize.height) / 2 + loc.y);
	}
}
