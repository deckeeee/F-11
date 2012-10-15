/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/xwife/server/WifeDataProvider.java,v 1.51.2.11 2007/10/18 09:48:42 frdm Exp $
 * $Revision: 1.51.2.11 $
 * $Date: 2007/10/18 09:48:42 $
 *
 * =============================================================================
 * Projrct    F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All
 * Rights Reserved.
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

package org.F11.scada.xwife.server;

import java.util.List;

import jp.gr.javacons.jim.DataProvider;

import org.F11.scada.Service;
import org.F11.scada.data.WifeData;
import org.F11.scada.server.frame.SendRequestSupport;
import org.F11.scada.xwife.applet.Session;

/**
 * �f�[�^�v���o�C�_�N���X�ł��B�ʐM��PLC����f�[�^���擾���āA�f�[�^�z���_�[�ɒl��ݒ肵�Ă����܂��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface WifeDataProvider extends DataProvider, Runnable, Service {
	/** �ϊ��I�u�W�F�N�g�擾�̃p�����[�^���ł� */
	public static final String PARA_NAME_CONVERT = "convert";
	/** �ʐM�R�}���h�I�u�W�F�N�g�擾�̃p�����[�^���ł� */
	public static final String PARA_NAME_COMAND = "command";
	/** �|�C���g���擾�̃p�����[�^���ł� */
	public static final String PARA_NAME_POINT = "point";
	/** �펞�ǂݓǂ݃t���O�̃p�����[�^���ł� */
	public static final String PARA_NAME_CYCLEREAD = "cycleread";
	/** �펞�ǂݓǂݎ����ł� */
	public static final String PARA_NAME_CYCLEREADTIME = "cyclereadtime";
	/** �`���^�����O�h�~�^�C�}�l�̃p�����[�^���ł� */
	public static final String PARA_NAME_OFFDELAY = "off_delay";
	/**	�x�񃊃t�@�����T�[�̃p�����[�^���ł� */
	public static final String PARA_NAME_ALARM = "alarmReferencer";
	/**	�f�}���h�x�񃊃t�@�����T�[�̃p�����[�^���ł� */
	public static final String PARA_NAME_DEMAND = "demandReferencer";
	/** INITIAL����GOOD�ɂȂ����ꍇ�̃t���O(�x��E��Ԃł͖�������K�v�������) */
	public static final String PARA_NAME_INIT2GOOD = "org.F11.scada.xwife.server.init2good";
	/** �v�Z���̃p�����[�^���ł� */
	public static final String PARA_NAME_EXPRESSION = "org.F11.scada.xwife.server.expression";

	/** �v���o�C�_�[�E�z���_�[�ԃZ�p���[�^�[������ */
	public static final String SEPARATER = "_";

	/**
	 * ������long�l(�X�V���t��long�l)�����HolderData��Ԃ��܂��B
	 * @param t
	 * @return HolderData[]
	 */
	public List getHoldersData(long t, Session session);

	/**
	 * �t���[���}�l�[�W���[��ݒ肵�܂��B
	 * @param frameDefineManager �t���[���}�l�[�W���[
	 */
    public void setSendRequestSupport(SendRequestSupport sendRequestSupport);

	/**
	 * �v���o�C�_�̃��b�N���J�n���܂��B���b�N���J�n����ہA�ʐM�X���b�h�Ɋ��荞�݂������܂��B
	 */
    void lock();

	/**
	 * �X���b�h�̃��b�N���O���܂��B
	 */
    void unlock();

    /**
     *
     * @param entryDate
     * @param value
     */
    void addJurnal(long entryDate, WifeData value);
}
