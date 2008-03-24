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

package org.F11.scada.tool.sound.individual;

import java.util.List;

public interface IndividualDao {
	public static final Class BEAN = IndividualDto.class;

	/**
	 * s2pager���g�p����item_table���V�X�e�������̃A�C�e����IndividualDto�̃��X�g�ŕԂ��܂��B
	 * 
	 * @param dto �y�[�W���[����dto
	 * @return s2pager���g�p����item_table���V�X�e�������̃A�C�e����IndividualDto�̃��X�g�ŕԂ��܂��B
	 */
	List findAllItem(IndividualCondition dto);

	public static final String findAllItem_ARGS = "dto";

	/**
	 * item_table�������Ŏw�肵���A�C�e����IndividualDto�ŕԂ��܂��B
	 * 
	 * @param provider �v���o�C�_��
	 * @param holder �z���_��
	 * @return �����Ŏw�肵���A�C�e����IndividualDto�ŕԂ��܂��B
	 */
	IndividualDto getItem(String provider, String holder);

	public static final String getItem_ARGS = "provider, holder";

	/**
	 * �����Ŏw�肵���A�C�e���̌x��^�C�v���X�V���܂��B
	 * 
	 * @param dto �x��Dto
	 * @return �X�V�������R�[�h���B
	 */
	int updateIndividual(IndividualDto dto);

	/**
	 * �����Ŏw�肵���A�C�e���̌x��^�C�v��}�����܂��B
	 * 
	 * @param dto �x��Dto
	 * @return �}���������R�[�h���B
	 */
	int insertIndividual(IndividualDto dto);

	public static final String insertIndividual_NO_PERSISTENT_PROPS =
		"alarmIndividualSettingId";

	/**
	 * 
	 * @param provider
	 * @param holder
	 * @return
	 */
	IndividualDto selectIndividual(String provider, String holder);

	public static final String selectIndividual_QUERY =
		"provider = ? AND holder = ?";

	/**
	 * ���������̃A�C�e����IndividualDto�̃��X�g�ŕԂ��܂��B
	 * 
	 * @param attibute ���o���鑮��
	 * @return �A�C�e����IndividualDto�̃��X�g�ŕԂ��܂��B
	 */
	List<IndividualDto> getItems(int attibute);

	public static final String getItems_ARGS = "attibute";
}
