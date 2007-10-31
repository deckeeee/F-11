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

package org.F11.scada.server.alarm;

import java.util.SortedSet;

import javax.swing.table.TableModel;

import jp.gr.javacons.jim.DataReferencer;

/**
 * �x��C�x���g�̃��t�@�����T�[
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface AlarmReferencer extends TableModel {
    /**
     * �f�[�^���t�@�����T�[�I�u�W�F�N�g��ǉ����܂�
     * @param rf �f�[�^���t�@�����T�[�I�u�W�F�N�g
     */
    void addReferencer(DataReferencer dr);

    /**
     * �f�[�^���t�@�����T�[�I�u�W�F�N�g���폜���܂�
     * @param rf �f�[�^���t�@�����T�[�I�u�W�F�N�g
     */
    void removeReferencer(DataReferencer dr);

    /**
     * �f�[�^���t�@�����T�[�̃\�[�g�ς݃Z�b�g��Ԃ��܂�
     * @return �f�[�^���t�@�����T�[�̃\�[�g�ς݃Z�b�g
     */
    SortedSet getReferencers();
    
    /**
     * �f�[�^�X�g�A�E�I�u�W�F�N�g��ǉ����܂�
     * @param store�@�f�[�^�X�g�A�E�I�u�W�F�N�g
     */
    boolean addDataStore(AlarmDataStore store);
}
