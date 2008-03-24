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
	 * s2pagerを使用してitem_tableよりシステム属性のアイテムをIndividualDtoのリストで返します。
	 * 
	 * @param dto ページャー制御dto
	 * @return s2pagerを使用してitem_tableよりシステム属性のアイテムをIndividualDtoのリストで返します。
	 */
	List findAllItem(IndividualCondition dto);

	public static final String findAllItem_ARGS = "dto";

	/**
	 * item_tableより引数で指定したアイテムをIndividualDtoで返します。
	 * 
	 * @param provider プロバイダ名
	 * @param holder ホルダ名
	 * @return 引数で指定したアイテムをIndividualDtoで返します。
	 */
	IndividualDto getItem(String provider, String holder);

	public static final String getItem_ARGS = "provider, holder";

	/**
	 * 引数で指定したアイテムの警報タイプを更新します。
	 * 
	 * @param dto 警報音Dto
	 * @return 更新したレコード数。
	 */
	int updateIndividual(IndividualDto dto);

	/**
	 * 引数で指定したアイテムの警報タイプを挿入します。
	 * 
	 * @param dto 警報音Dto
	 * @return 挿入したレコード数。
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
	 * 引数属性のアイテムをIndividualDtoのリストで返します。
	 * 
	 * @param attibute 抽出する属性
	 * @return アイテムをIndividualDtoのリストで返します。
	 */
	List<IndividualDto> getItems(int attibute);

	public static final String getItems_ARGS = "attibute";
}
