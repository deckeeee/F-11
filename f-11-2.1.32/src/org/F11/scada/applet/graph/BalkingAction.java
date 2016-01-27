/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002-2006 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.applet.graph;

/**
 * �{�[�L���O����A�N�V�����̃C���^�[�t�F�C�X�ł��B
 * @author maekawa
 */
interface BalkingAction {
	/**
	 * �{�[�L���O����ꍇ��true�����Ȃ��ꍇ��false��Ԃ��܂��B
	 * @param obj ����ޗ��̃I�u�W�F�N�g
	 * @return �{�[�L���O����ꍇ��true�����Ȃ��ꍇ��false��Ԃ��܂��B
	 */
	boolean isBalk(Object obj);

	/**
	 * �{�[�L���O�̔���ޗ��̃I�u�W�F�N�g��ݒ肵�܂��B
	 * @param obj �{�[�L���O�̔���ޗ��̃I�u�W�F�N�g
	 */
	void setBalk(Object obj);
}
