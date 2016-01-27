package org.F11.scada;

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

/**
 *  Wife �V�X�e�����ʗ�O
 */
public class WifeException extends Exception
{
	private static final long serialVersionUID = 482171786856143943L;
	/**
	 * �G���[�̃��x����\���܂��B
	 */
	private int cCode;
	/**
	 * �ڍ׃R�[�h��\���܂��B
	 */
	private int dCode;
	/**
	 * PLC�ʐM�̃G���[�R�[�h��\���܂��B
	 */
	private byte[] plcCode;

	// �G���[�R�[�h�ƃG���[���̂̑Δ�\���L�q
	// �R���f�B�V�����R�[�h
	public static final int WIFE_NOTHING = 0;
	public static final int WIFE_WARNING = 1;
	public static final int WIFE_ERROR = 2;

	// �ڍ׃R�[�h
	public static final int WIFE_NET_OPEN_ERROR             = 1;  // �l�b�g���[�N�I�[�v���G���[
	public static final int WIFE_NET_COMMAND_ERROR          = 2;  // �R�}���h�w��p�����[�^�G���[
	public static final int WIFE_NET_RESPONCE_ERROR         = 3;  // ���X�|���X���ُ�
	public static final int WIFE_NET_SOCKET_ERROR           = 4;  // �^�C���A�E�g�ݒ莞�G���[
	public static final int WIFE_NET_IO_ERROR               = 5;  // ����M���o�̓G���[
	public static final int WIFE_NET_RETRYOVER_ERROR        = 6;  // ���g���C�I�[�o�[
	public static final int WIFE_NET_RESPONCE_HEAD_ERROR    = 7;  // ���X�|���X�w�b�_�G���[
	public static final int WIFE_NET_RESPONCE_CMND_ERROR    = 8;  // ���X�|���X�R�}���h�G���[
	public static final int WIFE_NET_RESPONCE_ENDCODE_ERROR = 9;  // ���X�|���X�I���R�[�h�G���[
	public static final int WIFE_IOEXCEPTION_ERROR          = 10; // ��������I/O�G���[
	public static final int WIFE_UNKNOWNHOSTEXCEPTION_ERROR = 11; // ���������G���[

	public static final int WIFE_NOTHING_PLCID_ERROR        = 12; // PLCID�͖��o�^
	public static final int WIFE_PLCID_OVERLAPS_ERROR       = 13; // PLCID�͓o�^�ς�

	public static final int WIFE_EXPRESSION_WARNING         = 14; // �v�Z���s��
	public static final int WIFE_BAD_DATA_WARNING           = 15; // �f�[�^�s��
	public static final int WIFE_INITIALDATA_WARNING        = 16; // �f�[�^���X�V

//	public static final int WIFE_CODING_ERROR               = 9999; // �R�[�f�B���O�G���[

	/**
	 * �R���X�g���N�^
	 * �f�t�H���g�̃R���X�g���N�^�ł��B
	 */
	 public WifeException()
	{
		super();
	}

	/**
	 * �R���X�g���N�^
	 * �e�G���[�R�[�h���܂߂�WifeException�𐶐����܂��B
	 *@param s �G���[������\��
	 *@param cc �R���f�B�V�����R�[�h
	 *@param dc �ڍ׃R�[�h
	 */
	public WifeException(int cc, int dc, String s)
	{
		this(cc, dc, null, s);
	}

	/**
	 * �R���X�g���N�^
	 * �e�G���[�R�[�h���܂߂�WifeException�𐶐����܂��B
	 *@param s �G���[������\��
	 *@param cc �R���f�B�V�����R�[�h
	 *@param dc �ڍ׃R�[�h
	 *@param pc PLC�ʐM�G���[�R�[�h
	 */
	public WifeException(int cc, int dc, byte[] pc, String s)
	{
		super(s);
		cCode = cc;
		dCode = dc;
		plcCode = pc;
	}

	/**
	 * �R���X�g���N�^
	 * �e�G���[�R�[�h���܂߂�WifeException�𐶐����܂��B
	 *@param s �G���[������\��
	 *@param cc �R���f�B�V�����R�[�h
	 *@param dc �ڍ׃R�[�h
	 *@param pc PLC�ʐM�G���[�R�[�h
	 *@param cause �l�X�g������O�I�u�W�F�N�g
	 */
	public WifeException(int cc, int dc, byte[] pc, String s, Throwable cause)
	{
		super(s, cause);
		cCode = cc;
		dCode = dc;
		plcCode = pc;
	}

	/**
	 * �R���X�g���N�^
	 * �e�G���[�R�[�h���܂߂�WifeException�𐶐����܂��B
	 *@param s �G���[������\��
	 *@param cc �R���f�B�V�����R�[�h
	 *@param dc �ڍ׃R�[�h
	 *@param cause �l�X�g������O�I�u�W�F�N�g
	 */
	public WifeException(int cc, int dc, String s, Throwable cause)
	{
		this(cc, dc, null, s, cause);
	}

	/**
	 * �R���f�B�V�����R�[�h��Ԃ��܂��B
	 */
	public int getConditionCode()
	{
		return cCode;
	}

	/**
	 * �ڍ׃R�[�h��Ԃ��܂��B
	 */
	public int getDetailCode()
	{
		return dCode;
	}

	/**
	 * PLC�ʐM�G���[�R�[�h��Ԃ��܂��B
	 */
	public byte[] getPlcErrCode()
	{
		return plcCode;
	}
}
