/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.wizerd.panel;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.F11.scada.wizerd.Main;
import org.jdesktop.application.Task;

/**
 * �e�E�B�U�[�h�̋K��N���X
 * 
 * @author maekawa
 * 
 */
public abstract class Wizerd extends JPanel {
	protected boolean isNext;

	public Wizerd(LayoutManager layout) {
		super(layout);
	}

	/**
	 * ��������
	 */
	public abstract void init();

	/**
	 * ���s�{�^���̏����B�K��Task�I�u�W�F�N�g��Ԃ��A�o�b�N�O���E���h�X���b�h�Ŏ��s���܂��B
	 * 
	 * @param main
	 * @return Task�I�u�W�F�N�g
	 */
	public abstract Task<Void, Void> execute(Main main);

	/**
	 * ��{(����)�f�B���N�g����Ԃ��܂��B
	 * 
	 * @return ��{(����)�f�B���N�g����Ԃ��܂��B
	 */
	public abstract String getBaseDirectory();
}
