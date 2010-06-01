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

package org.F11.scada.security;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * <p>WIFE におけるパーミッションを表すクラスです。
 * 主にデータホルダーへの許可属性を表します。
 * <p>WIFE ではデータホルダーへの書込許可属性として使用します。
 * <p>生成できるアクションは read, write, execute, delete の４種類です。
 * 但し、WIFE では write 以外の許可属性を現在では使用していません。
 */
public class WifePermission extends Permission implements Serializable {
	private static final long serialVersionUID = 468656706087496919L;
	/** アクションの属性 */
	private final ActionAttribute action;

	/**
	 * 指定された名前とアクションで初期化します。
	 * @param name 名前
	 * @param action アクション
	 */
	public WifePermission(String name, String action) {
		super(name);
		if (name == null) {
			throw new NullPointerException("name can't be null");
		}
		if (name.equals("")) {
			throw new IllegalArgumentException("name can't be empty");
		}
		this.action = new ActionAttribute(action);
	}

	public String getActions() {
		return action.getActions();
	}

	public boolean implies(Permission permission) {
		if (permission == this) {
			return true;
		}
		if (!(permission instanceof WifePermission)) {
			return false;
		}
		WifePermission that = (WifePermission) permission;
		return getName().equals(that.getName()) && action.implies(that.action);
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + getName().hashCode();
		result = 37 * result + action.hashCode();
		return result;
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WifePermission))
			return false;
		WifePermission that = (WifePermission) obj;
		return getName().equals(that.getName()) && action.equals(that.action);
	}

	public PermissionCollection newPermissionCollection() {
		return new WifePermissionCollection();
	}

	/**
	 * 防御的readResolveメソッド。
	 * 不正にデシリアライズされるのを防止します。
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new WifePermission(super.getName(), action.getActions());
	}


	/**
	 * アクションの属性を表すクラスです。
	 */
	private final static class ActionAttribute implements Serializable {
		private static final long serialVersionUID = 3292611298816397721L;
		/** アクションの種類を表す数値 */
		private int mask;

		/**
		 * 指定した名前で属性を初期化します。
		 * @param action アクションを表す文字列 read, write, execute, delete をカンマで
		 * 区切って指定します。
		 */
		ActionAttribute(String action) {
			init(action);
		}

		/**
		 * 初期化処理
		 * @param action アクションを表す文字列
		 */
		private void init(String action) {
			checkAction(action);
			boolean legalAction = false;
			if (action.toLowerCase().indexOf("execute") > -1) {
				mask |= 0x01;
				legalAction = true;
			}
			if (action.toLowerCase().indexOf("write") > -1) {
				mask |= 0x02;
				legalAction = true;
			}
			if (action.toLowerCase().indexOf("read") > -1) {
				mask |= 0x04;
				legalAction = true;
			}
			if (action.toLowerCase().indexOf("delete") > -1) {
				mask |= 0x08;
				legalAction = true;
			}
			if (!legalAction) {
				throw new IllegalArgumentException("Illegal action type : " + action);
			}
		}

		/**
		 * アクション文字列の正当性チェック
		 * @param action アクション文字列
		 */
		private void checkAction(String action) {
			if (action == null) {
				throw new NullPointerException("action can't be null");
			}
			if (action.equals("")) {
				throw new IllegalArgumentException("action can't be empty");
			}

			for (StringTokenizer st = new StringTokenizer(action, ","); st.hasMoreTokens();) {
				String token = st.nextToken();
				if (!token.equals("execute") && !token.equals("write") &&
					!token.equals("read") && !token.equals("delete")) {
					throw new IllegalArgumentException("Illegal action type : " + action);
				}
			}
		}

		/**
		 * アクションの種類を表す数値を返します。
		 * @return アクションの種類を表す数値
		 */
		int getMask() {
			return mask;
		}

		/**
		 * このオブジェクトが保持するアクションをアクション文字列で返します。
		 * @return このオブジェクトが保持するアクションをアクション文字列
		 */
		String getActions() {
			StringBuffer sb = new StringBuffer();
			boolean comma = false;

			if ((mask & 0x01) == 0x01) {
				comma = true;
				sb.append("execute");
			}

			if ((mask & 0x02) == 0x02) {
				if (comma) {
					sb.append(',');
				} else {
					comma = true;
				}
				sb.append("write");
			}

			if ((mask & 0x04) == 0x04) {
				if (comma) {
					sb.append(',');
				} else {
					comma = true;
				}
				sb.append("read");
			}

			if ((mask & 0x08) == 0x08) {
				if (comma) {
					sb.append(',');
				} else {
					comma = true;
				}
				sb.append("delete");
			}

			return sb.toString();
		}

		/**
		 * この ActionAttribute オブジェクトに、指定されたアクセス権が含まれているかどうかを判定します。
		 * @param attribute チェック対象のアクション属性
		 * @return 指定されたアクションがこのオブジェクトに含まれる場合は true、そうでなければ false を返します。
		 */
		public boolean implies(ActionAttribute attribute) {
			return (this.mask & attribute.mask) == attribute.mask;
		}

		public int hashCode() {
			int result = 17;
			result = 37 * result + mask;
			return result;
		}

		public boolean equals(Object obj) {
			if (!(obj instanceof ActionAttribute))
				return false;
			ActionAttribute that = (ActionAttribute)obj;
			return this.mask == that.mask;
		}

		/**
		 * 防御的readResolveメソッド。
		 * 不正にデシリアライズされるのを防止します。
		 * @return Object デシリアライズされたインスタンス
		 * @throws ObjectStreamException デシリアライズに失敗した時
		 */
		Object readResolve() throws ObjectStreamException {
			return new ActionAttribute(getActions());
		}
	}


	/**
	 * WifePermission を格納する PermissionCollection クラスです。
	 */
	final class WifePermissionCollection extends PermissionCollection implements Serializable {
		private static final long serialVersionUID = -7377851574207645043L;
		private Map permissions;

		private Class classType;

		WifePermissionCollection() {
			permissions = Collections.synchronizedMap(new HashMap());
		}

		public void add(Permission permission) {
			if (!(permission instanceof WifePermission)) {
				throw new IllegalArgumentException("invalid permission : " + permission);
			}
			if (isReadOnly()) {
				throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
			}
			WifePermission wp = (WifePermission) permission;

			if (permissions.size() == 0) {
				this.classType = wp.getClass();
			} else {
				if (wp.getClass()!= this.classType) {
					throw new IllegalArgumentException("invalid permission : " + permission);
				}
			}
			permissions.put(wp.getName(), permission);
		}

		public boolean implies(Permission permission) {
			if (!(permission instanceof WifePermission)) {
				throw new IllegalArgumentException("invalid permission : " + permission);
			}
			WifePermission wp = (WifePermission) permission;
			if (wp.getClass()!= this.classType) {
				return false;
			}

			Object o = this.permissions.get(wp.getName());
			if (o != null) {
				Permission ps = (Permission) o;
				return ps.implies(permission);
			} else {
				return false;
			}
		}

		public Enumeration elements(){
			return Collections.enumeration(permissions.values());
		}

		/**
		 * 防御的readResolveメソッド。
		 * 不正にデシリアライズされるのを防止します。
		 * @return Object デシリアライズされたインスタンス
		 * @throws ObjectStreamException デシリアライズに失敗した時
		 */
		Object readResolve() throws ObjectStreamException {
			WifePermissionCollection pc = new WifePermissionCollection();
			pc.permissions = this.permissions;
			pc.classType = this.classType;
			return pc; 
		}
	}
}
