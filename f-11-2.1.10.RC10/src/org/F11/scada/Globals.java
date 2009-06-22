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

package org.F11.scada;

import java.awt.Dimension;
import java.sql.Timestamp;

import org.F11.scada.applet.ClientConfiguration;

/**
 * �O���[�o���ϐ���ێ�����N���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class Globals {
	private Globals() {}

	/**
	 * RMI�R�l�N�V�����̍Đڑ��܂ł̑҂����Ԃł��B
	 */
	public static final long RMI_CONNECTION_RETRY_WAIT_TIME = 10000L;
	
	/**
	 * RMI�R�l�N�V�����̍Đڑ����g���C�񐔂ł��B
	 */
	public static final int RMI_CONNECTION_RETRY_COUNT = 10;
	
	/**
	 * RMI���\�b�h�̃��g���C�񐔂ł��B
	 */
	public static final int RMI_METHOD_RETRY_COUNT = 5;

	/**
	 * EPOCH �^�C���X�^���v�I�u�W�F�N�g�ł��B
	 */	
	public static final Timestamp EPOCH = new Timestamp(0);
	
	/**
	 * �󕶎���̒萔�ł��B
	 */
	public static final String NULL_STRING = "";

	/**
	 * �J�����_�[�̃f�[�^�^�C�vID�ł�
	 */
	public static final int DATA_TYPE_CALENDAR = 15;

	/**
	 * �X�P�W���[���̃f�[�^�^�C�vID�ł�
	 */
	public static final int DATA_TYPE_SCHEDULE = 16;
	
	/**
	 * �^�C���X�^���v�̃f�[�^�^�C�vID�ł�
	 */
	public static final int DATA_TYPE_TIMESTAMP = 17;

	/**
	 * Seasar2�R���|�[�l���g��`�t�@�C��
	 */
    public static final String APP_DICON = "app.dicon";
    
    /**
     * �G���[�z���_����
     */
    public static final String ERR_HOLDER = "ERR_HOLDER";
    
    /**
     * �V���{���̏����\�����g���C��
     */
    public static final int SYMBOL_REPAINT_COUNT;
	static {
    	ClientConfiguration configuration = new ClientConfiguration();
    	SYMBOL_REPAINT_COUNT = configuration.getInt("symbol.repaint.count", 3);
    }

	/**
	 * 0, 0��Dimension�I�u�W�F�N�g�ł��B
	 */
	public static final Dimension ZERO_DIMENSION = new Dimension(0, 0);
}
