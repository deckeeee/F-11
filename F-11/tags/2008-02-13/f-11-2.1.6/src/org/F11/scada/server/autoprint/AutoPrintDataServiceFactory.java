/*
 * =============================================================================
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

package org.F11.scada.server.autoprint;

import org.F11.scada.server.io.AutoPrintDataService;

/**
 * �����󎚂̃f�[�^���o�T�[�r�X�𐶐�����t�@�N�g���[�ł��B
 * @author maekawa
 */
public interface AutoPrintDataServiceFactory {
	/**
	 * �^�X�N��(�e�[�u����)���v�����g�T�[�r�X��Ԃ��܂��B
	 * @param tableName �^�X�N��(�e�[�u����)
	 * @return �^�X�N��(�e�[�u����)���v�����g�T�[�r�X��Ԃ��܂��B
	 */
	AutoPrintDataService getAutoPrintDataService(String tableName);
}
