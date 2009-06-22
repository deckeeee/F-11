/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/util/SingletonSortedMap.java,v 1.1 2003/02/28 04:39:41 frdm Exp $
 * $Revision: 1.1 $
 * $Date: 2003/02/28 04:39:41 $
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
package org.F11.scada.util;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <p>�d���l������Ώ㏑������ SortedMap �̎����ł��B
 * <p>���̃N���X�͊�{�I�� TreeMap �Ɠ����ł��B
 * �A���A�L�[�l���قȂ��Ă��Ă��A�l�̃n�b�V���������Ȃ�ȑO�̒l�̃��R�[�h�͍폜����A
 * ���̌�ɒǉ����܂��B���̃N���X�̃L�[�y�ђl�� null ���g�p���邱�Ƃ͂ł��܂���B
 * �L�[�y�ђl�� null ���g�p����ƁANullPointerException ���X���[���܂��B
 *
 * <p>��.
 * key = a, value = A �Ƃ������R�[�h�����݂��鎞�ɁA
 * key = b, value = A �Ƃ������R�[�h�� put ���\�b�h�Œǉ������ꍇ�B
 * �l������ key = a, value = A �̃��R�[�h�͍폜����A
 * key = b, value = A �̃��R�[�h�� put ����܂��B
 * 
 * ���̃N���X�͂��郊�X�g�́A�X�V�������Ǘ�����ꍇ���Ɏg�p���܂��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class SingletonSortedMap implements SortedMap, Serializable {
	private static final long serialVersionUID = 6119828374691483069L;
	/** ��{�ƂȂ� SortedMap */
	private SortedMap map;
	/** �d���l���Ǘ����� Map �I�u�W�F�N�g */
	private Map valueMap;

	/**
	 * ��̃}�b�v�𐶐����܂��B�L�[�I�u�W�F�N�g�̐�������TreeMap�Ɠ��l�ł��B
	 * @see java.util.TreeMap
	 */
	public SingletonSortedMap() {
		map = new TreeMap();
		valueMap = new HashMap();
	}

	/**
	 * �w��̃R���p���[�^�ɏ]���ă\�[�g���ꂽ�A��̃}�b�v�𐶐����܂��B
	 * @param c ���̃}�b�v���\�[�g���邽�߂Ɏg�p�����R���p���[�^�Bnull �l�́A�L�[�́u���R�����t���v���g�p���邱�Ƃ�����
	 */
	public SingletonSortedMap(Comparator c) {
		map = new TreeMap(c);
		valueMap = new HashMap();
	}

	/**
	 * �w��̃}�b�v�Ɠ����}�b�s���O�������A�L�[�́u���R�����t���v�ɏ]���ă\�[�g���ꂽ�V�����}�b�v�𐶐����܂��B
	 * @param m �}�b�s���O�����̃}�b�v�ɔz�u�����}�b�v
	 */
	public SingletonSortedMap(Map m) {
		this();
		putAll(m);
	}

	/**
	 * �w��� SortedMap �Ɠ����}�b�s���O�������A���������t���ɏ]���ă\�[�g���ꂽ�A�V�����}�b�v���쐬���܂��B���̃��\�b�h�́A���`���ԂŎ��s����܂��B
	 * @param m �}�b�s���O�����̃}�b�v�ɔz�u����A�R���p���[�^�����̃}�b�v�̃\�[�g�Ɏg�p�����A�\�[�g���ꂽ�}�b�v
	 */
	public SingletonSortedMap(SortedMap m) {
		this(m.comparator());
		putAll(m);
	}

	/**
	 * �w��̒l�Ǝw�肳�ꂽ�L�[�����̃}�b�v�Ɋ֘A�t���܂��B
	 * �}�b�v���ȑO�ɂ��̃L�[�̃}�b�s���O��ێ����Ă����ꍇ�A�Â��l���u���������܂��B
	 * �܂��A�w��̒l�����Ƀ}�b�s���O����Ă����ꍇ�́A�Â��l��ێ�����G���g���[��
	 * �폜����A�V���ɃL�[�̃}�b�s���O���s���܂��B
	 * @param key �w�肳���l���֘A�t������L�[
	 * @param value �w�肳���L�[�Ɋ֘A�t������l
	 * @return �w�肳�ꂽ�L�[�Ɋ֘A�����l�B�܂��́A�L�[�̃}�b�s���O���Ȃ������ꍇ�� null�B�߂�l null �́A�}�b�v���ȑO�� null �Ǝw�肳�ꂽ�L�[���֘A�t���Ă������Ƃ������ꍇ������
	 * @exception NullPointerException �L�[���͒l�� null ���w�肵����
	 */
	public Object put(Object key, Object value) {
		if (key == null || value == null) {
			throw new NullPointerException();
		}
		
		Object o1 = map.get(key);
		if (valueMap.containsKey(o1)) {
			valueMap.remove(o1);
		}
		
		if (valueMap.containsKey(value)) {
			Object o2 = valueMap.get(value);
			valueMap.remove(value);
			map.remove(o2);
		}

		valueMap.put(value, key);
		Object o3 = map.put(key, value);
		if (map.size() != valueMap.size()) {
			throw new IllegalStateException("map size unmatch. (map : " + map.size() + " valueMap : " + valueMap.size() + ")");
		}
		return o3;
	}

	/**
	 * �w��̃}�b�v���炷�ׂẴ}�b�s���O���}�b�v�ɃR�s�[���܂��B
	 * ����ɂ��A�}�b�v���w��̃}�b�v���Ɍ��݂���L�[�̂��ׂĂɑ΂���
	 * �����Ă����}�b�s���O���u���������܂��B
	 * �܂��A�w��̒l�����Ƀ}�b�s���O����Ă����ꍇ�́A�Â��l��ێ�����G���g���[��
	 * �폜����A�V���ɃL�[�̃}�b�s���O���s���܂��B
	 * 
	 * @param t �}�b�v�Ɋi�[�����}�b�s���O 
	 */
	public void putAll(Map t) {
		for (Iterator it = t.entrySet().iterator(); it.hasNext();) {
			Entry e = (Entry) it.next();
			put(e.getKey(), e.getValue());
		}
	}
		
	/* (non-Javadoc)
	 * @see java.util.SortedMap#get()
	 */
	public Object get(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}
		
		return map.get(key);
	}
		
	/* (non-Javadoc)
	 * @see java.util.SortedMap#size()
	 */
	public int size() {
		return map.size();
	}
		
	/* (non-Javadoc)
	 * @see java.util.SortedMap#comparator()
	 */
	public Comparator comparator() {
		return map.comparator();
	}

	/* (non-Javadoc)
	 * @see java.util.SortedMap#firstKey()
	 */
	public Object firstKey() {
		return map.firstKey();
	}

	/* (non-Javadoc)
	 * @see java.util.SortedMap#headMap(java.lang.Object)
	 */
	public SortedMap headMap(Object toKey) {
		if (toKey == null) {
			throw new NullPointerException();
		}
		
		return map.headMap(toKey);
	}

	/* (non-Javadoc)
	 * @see java.util.SortedMap#lastKey()
	 */
	public Object lastKey() {
		return map.lastKey();
	}

	/* (non-Javadoc)
	 * @see java.util.SortedMap#subMap(java.lang.Object, java.lang.Object)
	 */
	public SortedMap subMap(Object fromKey, Object toKey) {
		if (fromKey == null || toKey == null) {
			throw new NullPointerException();
		}
		
		return map.subMap(fromKey, toKey);
	}

	/* (non-Javadoc)
	 * @see java.util.SortedMap#tailMap(java.lang.Object)
	 */
	public SortedMap tailMap(Object fromKey) {
		if (fromKey == null) {
			throw new NullPointerException();
		}
		
		return map.tailMap(fromKey);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		map.clear();
		valueMap.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}
		

		return map.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		if (value == null) {
			throw new NullPointerException();
		}
		
		return map.containsValue(value);
	}

	/**
	 * �����ێ�����Ă��� SortedMap �� entrySet ��Ԃ��܂��B
	 * �A���A�Ԃ���� Set �͕ύX���邱�Ƃ��ł��܂���B���̎����͌����ɂ� SortedMap �C���^�[�t�F�C�X��
	 * �������������Ă��܂���B
	 * 
	 * @return �ύX�s��entrySet
	 */
	public Set entrySet() {
		return Collections.unmodifiableSet(map.entrySet());
	}

	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * �����ێ�����Ă��� SortedMap �� keySet ��Ԃ��܂��B
	 * �A���A�Ԃ���� Set �͕ύX���邱�Ƃ��ł��܂���B���̎����͌����ɂ� SortedMap �C���^�[�t�F�C�X��
	 * �������������Ă��܂���B
	 * 
	 * @return �ύX�s��keySet
	 */
	public Set keySet() {
		return Collections.unmodifiableSet(map.keySet());
	}

	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		if (key == null) {
			throw new NullPointerException();
		}
		
		Object o = map.remove(key);
		if (valueMap.containsKey(o)) {
			valueMap.remove(o);
		}

		if (map.size() != valueMap.size()) {
			throw new IllegalStateException("map size unmatch. (map : " + map.size() + " valueMap : " + valueMap.size() + ")");
		}
		return o;
	}

	/**
	 * �����ێ�����Ă��� SortedMap �� �l Collection �r���[��Ԃ��܂��B
	 * �A���A�Ԃ���� Collection �͕ύX���邱�Ƃ��ł��܂���B���̎����͌����ɂ� SortedMap �C���^�[�t�F�C�X��
	 * �������������Ă��܂���B
	 * 
	 * @return �ύX�s�̒l Collection �r���[
	 */
	public Collection values() {
		return Collections.unmodifiableCollection(map.values());
	}
	
	/**
	 * �ێ����Ă��� Map �I�u�W�F�N�g�Ńn�b�V���𐶐����܂��B
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + map.hashCode();
		result = 37 * result + valueMap.hashCode();
		return result;
	}

	/**
	 * �ێ����Ă��� Map �I�u�W�F�N�g�Ŕ�r���܂��B
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof SingletonSortedMap)) {
			return false;
		}
		SingletonSortedMap m = (SingletonSortedMap) obj;
		return m.map.equals(map)
				&& m.valueMap.equals(valueMap);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("map{").append(map.toString()).append("}");
		buffer.append("valueMap{").append(valueMap.toString()).append("}");
		return buffer.toString();
	}
	
	/**
	 * �h��IreadResolve���\�b�h�B
	 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	private Object readResolve() throws ObjectStreamException {
		return new SingletonSortedMap(map);
	}
}
