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

package org.F11.scada.server.register.impl;

import jp.gr.javacons.jim.DataHolder;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.register.WifeDataUtil;
import org.F11.scada.xwife.server.WifeDataProvider;

/**
 * デジタルデータのHolderRegisterクラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AnalogHolderRegister extends AbstractHolderRegister {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.F11.scada.server.register.HolderRegister#register(org.F11.scada.server.entity.Item)
	 */
	public void register(Item item) {
		WifeData wd = WifeDataUtil.getWifeData(item);
		DataHolder dh = RegisterUtil.getDataHolder(item, wd);
		ConvertValue conv = RegisterUtil.getConvertValue(item);
		dh.setParameter(WifeDataProvider.PARA_NAME_CONVERT, conv);

		RegisterUtil.addDataHolder(dh, item);
		RegisterUtil.setDemand(item, dh);
	}
}
