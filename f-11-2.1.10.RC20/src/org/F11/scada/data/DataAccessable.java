/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/DataAccessable.java,v 1.9.2.6 2006/08/11 02:24:33 frdm Exp $
 * $Revision: 1.9.2.6 $
 * $Date: 2006/08/11 02:24:33 $
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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.QualityFlag;

import org.F11.scada.xwife.applet.Session;

/**
 * �f�[�^�v���o�C�_�E�f�[�^�v���o�C�_�v���N�V�Ԃ̃����[�g�C���^�[�t�F�C�X�ł��B
 * �A�v���b�g�o�R�Ńf�[�^�v���o�C�_�ɃA�N�Z�X����ׂ̃C���^�[�t�F�C�X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public interface DataAccessable extends Remote {
	/**
	 * �w�肳�ꂽ�f�[�^��Ԃ��܂��B
	 * @param dpname �f�[�^�v���o�C�_��
	 * @param dhname �f�[�^�z���_��
	 * @return �w�肵���f�[�^�z���_�̃f�[�^�I�u�W�F�N�g
	 * @exception RemoteException RMI �G���[�����������Ƃ�
	 */
	public Object getValue(String dpname, String dhname)
		throws RemoteException;

	/**
	 * �w�肳�ꂽ�N�I���e�B�t���O��Ԃ��܂��B
	 * @param dpname �f�[�^�v���o�C�_��
	 * @param dhname �f�[�^�z���_��
	 * @return �w�肵���f�[�^�z���_�̃f�[�^�I�u�W�F�N�g
	 * @exception RemoteException RMI �G���[�����������Ƃ�
	 */
	public QualityFlag getQualityFlag(String dpname, String dhname)
		throws RemoteException;

	/**
	 * �f�[�^�z���_�ɒl�I�u�W�F�N�g��ݒ肵�܂��B
	 * @param dpname �f�[�^�v���o�C�_��
	 * @param dhname �f�[�^�z���_��
	 * @param dataValue �l�I�u�W�F�N�g
	 * @exception RemoteException RMI �G���[�����������Ƃ�
	 */
	public void setValue(String dpname, String dhname, Object dataValue)
		throws RemoteException;

	/**
	 * �f�[�^�z���_�ɒl�I�u�W�F�N�g��ݒ肵�܂��B
	 * @param dpname �f�[�^�v���o�C�_��
	 * @param dhname �f�[�^�z���_��
	 * @param dataValue �l�I�u�W�F�N�g
	 * @param user ���[�U�[
	 * @param ip �[��IP
	 * @exception RemoteException RMI �G���[�����������Ƃ�
	 */
	public void setValue(String dpname, String dhname, Object dataValue, String user, String ip)
		throws RemoteException;

	/**
	 * �q�X�g���e�[�u���̊m�F�t���O��ݒ肵�܂��B
	 * @param point �|�C���g�ԍ�
	 * @param dpname �f�[�^�v���o�C�_��
	 * @param dhname �f�[�^�z���_��
	 * @param date �X�V���t
	 * @param row �}�E�X�N���b�N�i�ύX�j�s
	 * @throws RemoteException RemoteException RMI �G���[�����������Ƃ�
	 */
	public void setHistoryTable(
		Integer point,
		String dpname,
		String dhname,
		Timestamp date,
		Integer row)
		throws RemoteException;

	/**
	 * �w�肳�ꂽ�f�[�^�z���_��Ԃ��܂��B
	 * @param dpname �f�[�^�v���o�C�_��
	 * @param dhname �f�[�^�z���_��
	 * @return ���݂����ꍇ�� DataHolder �I�u�W�F�N�g�A�����ꍇ�� null ��Ԃ��܂��B
	 * @throws RemoteException RemoteException RMI �G���[�����������Ƃ�
	 */
	public DataHolder findDataHolder(String dpname, String dhname)
		throws RemoteException;

	/**
	 * �w�肳�ꂽ�f�[�^�z���_�̃p�����^��Ԃ��܂��B
	 * @param dpname �f�[�^�v���o�C�_��
	 * @param dhname �f�[�^�z���_��
	 * @param paraName �p�����^��
	 * @return �p�����^�I�u�W�F�N�g
	 * @throws RemoteException RemoteException RMI �G���[�����������Ƃ�
	 */
	public Object getParameta(String dpname, String dhname, String paraName)
		throws RemoteException;

	/**
	 * �f�[�^�z���_�X�V�f�[�^��Ԃ��܂��B
	 * @param provider �f�[�^�v���o�C�_��
	 * @return �f�[�^�z���_�X�V�f�[�^��List�I�u�W�F�N�g 
	 */
	public List getHoldersData(String provider) throws RemoteException;

	/**
	 * �f�[�^�z���_�X�V�f�[�^��Ԃ��܂��B
	 * @param provider �f�[�^�v���o�C�_��
	 * @param t �ێ��f�[�^�̍ŐV���t�� long �l
	 * @return �f�[�^�z���_�X�V�f�[�^��List�I�u�W�F�N�g 
	 */
	public List getHoldersData(String provider, long t, Session session) throws RemoteException;

	/**
	 * �T�[�o�[�Ŏ��s����Ă���A�f�[�^�v���o�C�_���̔z���Ԃ��܂��B
	 * @return �f�[�^�v���o�C�_���̃��X�g
	 */
	public List getDataProviders() throws RemoteException;


	/**
	 * �f�[�^�z���_�[�����I�u�W�F�N�g�̔z���Ԃ��܂��B
	 * @param dataProvider �f�[�^�v���o�C�_��
	 * @return CreateHolderData[] �f�[�^�z���_�[�����I�u�W�F�N�g�̔z�� 
	 */
	public List getCreateHolderDatas(String dataProvider)
		throws RemoteException;

	/**
	 * �^�C���X�^���v�Ŏw�肳�ꂽ�ȏ�̃W���[�i���f�[�^��Ԃ��܂��B
	 * @param t �^�C���X�^���v��Long�l
	 * @param provider �f�[�^�v���o�C�_�[��
	 * @param holder �f�[�^�z���_�[��
	 * @return SortedMap �W���[�i���f�[�^�̃��X�g
	 */
	public SortedMap getAlarmJournal(long t, String provider, String holder)
		throws RemoteException;

	/**
	 * �^�C���X�^���v�Ŏw�肳�ꂽ�ȏ�̃W���[�i���f�[�^��Ԃ��܂��B
	 * @param t �^�C���X�^���v��Long�l
	 * @return SortedMap �X�V������ PointTableBean �I�u�W�F�N�g�̃}�b�v
	 * @since 1.0.3
	 */
	public SortedMap getPointJournal(long t) throws RemoteException;

	/**
	 * �f�[�^�z���_�[�����I�u�W�F�N�g�̔z���Ԃ��܂��B
	 * @param holderStrings �f�[�^�z���_���
	 * @return CreateHolderData[] �f�[�^�z���_�[�����I�u�W�F�N�g�̔z�� 
	 */
	public List getCreateHolderDatas(Collection holderStrings)
		throws RemoteException;

	/**
	 * �T�[�o�[�̃R�}���h���Ăяo���܂��B
	 * @param command �R�}���h��
	 * @param args ����
	 * @return �߂�l������΂����̓I�u�W�F�N�g���Ԃ����A�����łȂ��ꍇ�� null ���Ԃ����B
	 */
	Object invoke(String command, Object[] args) throws RemoteException;
}
