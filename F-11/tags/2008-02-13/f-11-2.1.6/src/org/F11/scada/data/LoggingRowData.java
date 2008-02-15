/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/LoggingRowData.java,v 1.3.4.1 2004/09/07 01:44:23 frdm Exp $
 * $Revision: 1.3.4.1 $
 * $Date: 2004/09/07 01:44:23 $
 * 
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

package org.F11.scada.data;

import java.sql.Timestamp;

import org.apache.commons.collections.primitives.DoubleList;

public interface LoggingRowData {

	/**
	 * ���R�[�h�̃^�C���X�^���v��Ԃ��܂�
	 * @return ���R�[�h�̃^�C���X�^���v
	 */
	public Timestamp getTimestamp();

	/**
	 * ���̃��R�[�h�̎w���l��Ԃ��܂��B
	 * @param column �w���
	 * @return ���̃��R�[�h�̎w���l
	 */
	public double getDouble(int column);
	
	/**
	 * ���̃��R�[�h�̃f�[�^�̃��X�g��Ԃ��܂��B
	 * @return ���̃��R�[�h�̃f�[�^�̃��X�g��Ԃ��܂��B
	 */
	public DoubleList getList();
}
