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
 * <p>重複値があれば上書きする SortedMap の実装です。
 * <p>このクラスは基本的に TreeMap と同等です。
 * 但し、キー値が異なっていても、値のハッシュが同じなら以前の値のレコードは削除され、
 * その後に追加します。このクラスのキー及び値に null を使用することはできません。
 * キー及び値に null を使用すると、NullPointerException をスローします。
 *
 * <p>例.
 * key = a, value = A というレコードが存在する時に、
 * key = b, value = A というレコードを put メソッドで追加した場合。
 * 値が同じ key = a, value = A のレコードは削除され、
 * key = b, value = A のレコードが put されます。
 * 
 * このクラスはあるリストの、更新時刻を管理する場合等に使用します。
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class SingletonSortedMap implements SortedMap, Serializable {
	private static final long serialVersionUID = 6119828374691483069L;
	/** 基本となる SortedMap */
	private SortedMap map;
	/** 重複値を管理する Map オブジェクト */
	private Map valueMap;

	/**
	 * 空のマップを生成します。キーオブジェクトの制限等はTreeMapと同様です。
	 * @see java.util.TreeMap
	 */
	public SingletonSortedMap() {
		map = new TreeMap();
		valueMap = new HashMap();
	}

	/**
	 * 指定のコンパレータに従ってソートされた、空のマップを生成します。
	 * @param c このマップをソートするために使用されるコンパレータ。null 値は、キーの「自然順序付け」を使用することを示す
	 */
	public SingletonSortedMap(Comparator c) {
		map = new TreeMap(c);
		valueMap = new HashMap();
	}

	/**
	 * 指定のマップと同じマッピングを持ち、キーの「自然順序付け」に従ってソートされた新しいマップを生成します。
	 * @param m マッピングがこのマップに配置されるマップ
	 */
	public SingletonSortedMap(Map m) {
		this();
		putAll(m);
	}

	/**
	 * 指定の SortedMap と同じマッピングを持ち、同じ順序付けに従ってソートされた、新しいマップを作成します。このメソッドは、線形時間で実行されます。
	 * @param m マッピングがこのマップに配置され、コンパレータがこのマップのソートに使用される、ソートされたマップ
	 */
	public SingletonSortedMap(SortedMap m) {
		this(m.comparator());
		putAll(m);
	}

	/**
	 * 指定の値と指定されたキーをこのマップに関連付けます。
	 * マップが以前にこのキーのマッピングを保持していた場合、古い値が置き換えられます。
	 * また、指定の値が既にマッピングされていた場合は、古い値を保持するエントリーが
	 * 削除され、新たにキーのマッピングを行います。
	 * @param key 指定される値が関連付けられるキー
	 * @param value 指定されるキーに関連付けられる値
	 * @return 指定されたキーに関連した値。または、キーのマッピングがなかった場合は null。戻り値 null は、マップが以前に null と指定されたキーを関連付けていたことを示す場合もある
	 * @exception NullPointerException キー又は値に null を指定した時
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
	 * 指定のマップからすべてのマッピングをマップにコピーします。
	 * これにより、マップが指定のマップ内に現在あるキーのすべてに対して
	 * 持っていたマッピングが置き換えられます。
	 * また、指定の値が既にマッピングされていた場合は、古い値を保持するエントリーが
	 * 削除され、新たにキーのマッピングを行います。
	 * 
	 * @param t マップに格納されるマッピング 
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
	 * 内部保持されている SortedMap の entrySet を返します。
	 * 但し、返される Set は変更することができません。この実装は厳密には SortedMap インターフェイスを
	 * 正しく実装していません。
	 * 
	 * @return 変更不可のentrySet
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
	 * 内部保持されている SortedMap の keySet を返します。
	 * 但し、返される Set は変更することができません。この実装は厳密には SortedMap インターフェイスを
	 * 正しく実装していません。
	 * 
	 * @return 変更不可のkeySet
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
	 * 内部保持されている SortedMap の 値 Collection ビューを返します。
	 * 但し、返される Collection は変更することができません。この実装は厳密には SortedMap インターフェイスを
	 * 正しく実装していません。
	 * 
	 * @return 変更不可の値 Collection ビュー
	 */
	public Collection values() {
		return Collections.unmodifiableCollection(map.values());
	}
	
	/**
	 * 保持している Map オブジェクトでハッシュを生成します。
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + map.hashCode();
		result = 37 * result + valueMap.hashCode();
		return result;
	}

	/**
	 * 保持している Map オブジェクトで比較します。
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
	 * 防御的readResolveメソッド。
	 * 不正にデシリアライズされるのを防止します。
	 * @return Object デシリアライズされたインスタンス
	 * @throws ObjectStreamException デシリアライズに失敗した時
	 */
	private Object readResolve() throws ObjectStreamException {
		return new SingletonSortedMap(map);
	}
}
