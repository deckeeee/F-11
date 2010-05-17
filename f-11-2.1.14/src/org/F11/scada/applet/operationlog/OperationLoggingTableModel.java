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
package org.F11.scada.applet.operationlog;

import javax.swing.table.TableModel;

import org.F11.scada.server.operationlog.dto.FinderConditionDto;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public interface OperationLoggingTableModel extends TableModel {
    /**
     * �������ʂ̏����f�[�^���擾���܂�
     * @param finder ��������
     */
    void find(FinderConditionDto finder);
    
    /**
     * ���y�[�W�̃f�[�^���擾���܂�
     */
    void next();
    
    /**
     * �O�y�[�W�̃f�[�^���擾���܂�
     */
    void previous();
    
    /**
     * ���ݕ\�����Ă���y�[�W��Ԃ��܂�
     * @return ���ݕ\�����Ă���y�[�W
     */
    int getCurrentPage();
    
    /**
     * ���������Ŏ擾���������R�[�h�ɂ��S�y�[�W����Ԃ��܂�
     * @return ���������Ŏ擾���������R�[�h�ɂ��S�y�[�W��
     */
    int getAllPage();
    
    /**
     * ���샍�O�v���t�B�b�N�X�@�\�̗L��
     * @return ���샍�O�v���t�B�b�N�X�@�\�L��̏ꍇ<code> true </code>���Ȃ��ꍇ��<code> false </code>��Ԃ��܂��B
     */
    boolean isPrefix();
}
