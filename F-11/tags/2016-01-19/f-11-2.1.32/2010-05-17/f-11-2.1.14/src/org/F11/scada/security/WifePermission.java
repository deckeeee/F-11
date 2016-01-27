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
 * <p>WIFE �ɂ�����p�[�~�b�V������\���N���X�ł��B
 * ��Ƀf�[�^�z���_�[�ւ̋�������\���܂��B
 * <p>WIFE �ł̓f�[�^�z���_�[�ւ̏����������Ƃ��Ďg�p���܂��B
 * <p>�����ł���A�N�V������ read, write, execute, delete �̂S��ނł��B
 * �A���AWIFE �ł� write �ȊO�̋����������݂ł͎g�p���Ă��܂���B
 */
public class WifePermission extends Permission implements Serializable {
	private static final long serialVersionUID = 468656706087496919L;
	/** �A�N�V�����̑��� */
	private final ActionAttribute action;

	/**
	 * �w�肳�ꂽ���O�ƃA�N�V�����ŏ��������܂��B
	 * @param name ���O
	 * @param action �A�N�V����
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
	 * �h��IreadResolve���\�b�h�B
	 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	private Object readResolve() throws ObjectStreamException {
		return new WifePermission(super.getName(), action.getActions());
	}


	/**
	 * �A�N�V�����̑�����\���N���X�ł��B
	 */
	private final static class ActionAttribute implements Serializable {
		private static final long serialVersionUID = 3292611298816397721L;
		/** �A�N�V�����̎�ނ�\�����l */
		private int mask;

		/**
		 * �w�肵�����O�ő��������������܂��B
		 * @param action �A�N�V������\�������� read, write, execute, delete ���J���}��
		 * ��؂��Ďw�肵�܂��B
		 */
		ActionAttribute(String action) {
			init(action);
		}

		/**
		 * ����������
		 * @param action �A�N�V������\��������
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
		 * �A�N�V����������̐������`�F�b�N
		 * @param action �A�N�V����������
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
		 * �A�N�V�����̎�ނ�\�����l��Ԃ��܂��B
		 * @return �A�N�V�����̎�ނ�\�����l
		 */
		int getMask() {
			return mask;
		}

		/**
		 * ���̃I�u�W�F�N�g���ێ�����A�N�V�������A�N�V����������ŕԂ��܂��B
		 * @return ���̃I�u�W�F�N�g���ێ�����A�N�V�������A�N�V����������
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
		 * ���� ActionAttribute �I�u�W�F�N�g�ɁA�w�肳�ꂽ�A�N�Z�X�����܂܂�Ă��邩�ǂ����𔻒肵�܂��B
		 * @param attribute �`�F�b�N�Ώۂ̃A�N�V��������
		 * @return �w�肳�ꂽ�A�N�V���������̃I�u�W�F�N�g�Ɋ܂܂��ꍇ�� true�A�����łȂ���� false ��Ԃ��܂��B
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
		 * �h��IreadResolve���\�b�h�B
		 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
		 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
		 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
		 */
		Object readResolve() throws ObjectStreamException {
			return new ActionAttribute(getActions());
		}
	}


	/**
	 * WifePermission ���i�[���� PermissionCollection �N���X�ł��B
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
		 * �h��IreadResolve���\�b�h�B
		 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
		 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
		 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
		 */
		Object readResolve() throws ObjectStreamException {
			WifePermissionCollection pc = new WifePermissionCollection();
			pc.permissions = this.permissions;
			pc.classType = this.classType;
			return pc; 
		}
	}
}
