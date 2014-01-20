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

package org.F11.scada.server.communicater;

/**
 * ����@���\���C���^�[�t�F�C�X�ł��B
 */
public interface Environment {
	/**
	 * �f�o�C�XID��Ԃ��܂��B
	 * @return �f�o�C�XID
	 */
	public String getDeviceID();

	/**
	 * �f�o�C�X�̎�ނ�Ԃ��܂��B
	 * @return �f�o�C�X�̎��
	 */
	public String getDeviceKind();

	/**
	 * �f�o�C�X��IP�A�h���X��Ԃ��܂��B
	 * @return �f�o�C�X��IP�A�h���X
	 */
	public String getPlcIpAddress();

	/**
	 * �f�o�C�X�̒ʐM�|�[�g��Ԃ��܂��B
	 * @return �f�o�C�X�̒ʐM�|�[�g
	 */
	public int getPlcPortNo();

	/**
	 * �f�o�C�X�̃R�}���h�`�Ԃ�Ԃ��܂��B
	 * @return �f�o�C�X�̃R�}���h�`��
	 */
	public String getPlcCommKind();

	/**
	 * �f�o�C�X�̃l�b�g�ԍ���Ԃ��܂��B
	 * @return �f�o�C�X�̃l�b�g�ԍ�
	 */
	public int getPlcNetNo();

	/**
	 * �f�o�C�X�̃m�[�h�ԍ���Ԃ��܂��B
	 * @return �f�o�C�X�̃m�[�h�ԍ�
	 */
	public int getPlcNodeNo();

	/**
	 * �f�o�C�X�̃��j�b�g�ԍ���Ԃ��܂��B
	 * @return �f�o�C�X�̃��j�b�g�ԍ�
	 */
	public int getPlcUnitNo();

	/**
	 * �f�o�C�X�̒ʐM�҂����Ԃ�Ԃ��܂��B
	 * @return �f�o�C�X�̒ʐM�҂�����
	 */
	public int getPlcWatchWait();

	/**
	 * �f�o�C�X�̃^�C���A�E�g���Ԃ�Ԃ��܂��B
	 * @return �f�o�C�X�̃^�C���A�E�g����
	 */
	public int getPlcTimeout();

	/**
	 * �f�o�C�X�̃G���[���g���C�񐔂�Ԃ��܂��B
	 * @return �f�o�C�X�̃G���[���g���C��
	 */
	public int getPlcRetryCount();

	/**
	 * �f�o�C�X�̒ʐM�����҂����Ԃ�Ԃ��܂��B
	 * @return �f�o�C�X�̒ʐM�����҂�����
	 */
	public int getPlcRecoveryWait();

	/**
	 * �z�X�g�̃l�b�g�A�h���X��Ԃ��܂��B
	 * @return �z�X�g�̃l�b�g�A�h���X
	 */
	public int getHostNetNo();

	/**
	 * �z�X�g�̒ʐM�|�[�g��Ԃ��܂��B
	 * <p>�z�X�g�̒ʐM�|�[�g���󔒂̏ꍇ�́APLC�Ɠ����ʐM�|�[�g��Ԃ��܂��B
	 * @return �z�X�g�̒ʐM�|�[�g
	 */
	public int getHostPortNo();

	/**
	 * <p>�z�X�g��IP�A�h���X��Ԃ��܂��B
	 * <p>�z�X�g�̃l�b�g�A�h���X���󔒂̏ꍇ�́APLC�Ɠ����A�h���X��Ԃ��܂��B
	 * @return �z�X�g��IP�A�h���X
	 */
	public String getHostIpAddress();

	/**
	 * �z�X�g�̃z�X�g�A�h���X��Ԃ��܂��B
	 * @return �z�X�g�̃z�X�g�A�h���X
	 */
	public int getHostAddress();

	/**
	 * �f�o�C�X��IP�A�h���X(��d���p)��Ԃ��܂��B
	 * @return �f�o�C�X��IP�A�h���X
	 */
	public String getPlcIpAddress2();
}
