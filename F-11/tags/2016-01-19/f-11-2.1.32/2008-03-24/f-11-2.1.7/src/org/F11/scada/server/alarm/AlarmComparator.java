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

package org.F11.scada.server.alarm;

import java.util.Comparator;

import jp.gr.javacons.jim.DataReferencer;


/**
 * データプロバイダ名とデータホルダー名を使用したDataReferencerのコンパレーターです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmComparator implements Comparator {
	public int compare(Object o1, Object o2) {
		DataReferencer dr1 = (DataReferencer) o1;
		DataReferencer dr2 = (DataReferencer) o2;

		String dr1Pro = dr1.getDataProviderName();
		String dr1Hol = dr1.getDataHolderName();
		String dr2Pro = dr2.getDataProviderName();
		String dr2Hol = dr2.getDataHolderName();
	
		if (dr1Pro.compareTo(dr2Pro) < 0) {
			return -1;
		} else if (dr1Pro.compareTo(dr2Pro) > 0) {
			 return 1;
		}
		if (dr1Hol.compareTo(dr2Hol) < 0) {
			return -1;
		} else if (dr1Hol.compareTo(dr2Hol) > 0) {
			 return 1;
		}
		return 0;
	}
}