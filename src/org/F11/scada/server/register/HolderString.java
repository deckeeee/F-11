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

package org.F11.scada.server.register;

import java.io.Serializable;

/**
 * データプロバイダ名とデータホルダー名を保持するヘルパークラスです。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class HolderString implements Serializable {
	private static final long serialVersionUID = 69885175004763840L;
	private String provider;
	private String holder;
	private volatile int hashCode = 0;

	public HolderString() {
	}

	public HolderString(String provider, String holder) {
		this.provider = provider;
		this.holder = holder;
	}

	public HolderString(String value) {
		try {
			this.provider = value.substring(0, value.indexOf("_"));
			this.holder = value.substring(value.indexOf("_") + 1);
		} catch (StringIndexOutOfBoundsException e) {
			throw new IllegalArgumentException(
					"文字列が 'プロバイダ_ホルダ' で無い (設定された文字列 = " + value + ")",
					e);
		}
	}

	public String getHolder() {
		return holder;
	}

	public String getProvider() {
		return provider;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	/*
	 * (Javadoc なし)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "{provider=" + provider + ", holder=" + holder + "}";
	}

	/*
	 * (Javadoc なし)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof HolderString)) {
			return false;
		}

		HolderString hs = (HolderString) obj;

		return provider.equals(hs.provider) && holder.equals(hs.holder);
	}

	/*
	 * (Javadoc なし)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			result = 37 * result + provider.hashCode();
			result = 37 * result + holder.hashCode();
			hashCode = result;
		}
		return hashCode;
	}
}
