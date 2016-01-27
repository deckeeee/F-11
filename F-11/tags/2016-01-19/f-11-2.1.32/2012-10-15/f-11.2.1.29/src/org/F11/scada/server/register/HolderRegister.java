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

package org.F11.scada.server.register;

import org.F11.scada.server.entity.Item;

/**
 * �z���_�[�����I�u�W�F�N�g�𐶐����AJIM�ɓo�^����C���^�[�t�F�C�X
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface HolderRegister {
    /**
     * �z���_�[�����I�u�W�F�N�g�𐶐����AJIM�ɓo�^���܂�
     * @param item �z���_�[���
     */
    void register(Item item);

    /**
     * �z���_�[�����JIM����폜���܂�
     * @param item �z���_�[���
     */
    void unregister(Item item);
}
