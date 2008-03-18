/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.cat.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * �R���N�V�����Ɋւ��郆�[�e�B���e�B�[�N���X�B���̃N���X��static�C���|�[�g���A���ꂼ��̃��\�b�h���Ăяo�����Ƃňȉ��̌Ăяo�����\�ɂȂ�܂��B
 * 
 * <pre>
 *  List&lt;String&gt; strings = list(&quot;A&quot;, &quot;B&quot;, &quot;C&quot;);
 *  Map&lt;String, String&gt; stringMap = map($(&quot;X&quot;, &quot;1&quot;), $(&quot;Y&quot;, &quot;2&quot;));
 *  Set&lt;String&gt; stringSet = set(&quot;A&quot;, &quot;B&quot;, &quot;C&quot;);
 * </pre>
 * 
 * ���\�b�h�Ăяo���̍ۂɎg�p����N���X���ސ��ł���ׁAnew �ŋL�q��������킸��킵�����������܂��B
 * 
 * @author maekawa
 * 
 */
public abstract class CollectionUtil {
	/**
	 * �w�肵���N���X��ArrayList�𐶐����܂�
	 * 
	 * @param <T> �N���X
	 * @return �w�肵���N���X��ArrayList�𐶐����܂�
	 */
	public static <T> List<T> list() {
		return new ArrayList<T>();
	}

	/**
	 * �w�肵���N���X�Ə����e�ʂ�ArrayList�𐶐����܂�
	 * 
	 * @param <T> �N���X
	 * @param size �����e��
	 * @return �w�肵���N���X�Ə����e�ʂ�ArrayList�𐶐����܂�
	 */
	public static <T> List<T> list(int size) {
		return new ArrayList<T>(size);
	}

	/**
	 * �w�肵�����e�Ń��X�g�𐶐����܂��B
	 * 
	 * @param <T> �N���X
	 * @param elements �����l
	 * @return �w�肵�����e�Ń��X�g�𐶐����܂��B
	 */
	public static <T> List<T> list(T... elements) {
		return new ArrayList<T>(Arrays.asList(elements));
	}

	/**
	 * �w�肵���N���X��HashSet�𐶐����܂�
	 * 
	 * @param <T> �N���X
	 * @return �w�肵���N���X��HashSet�𐶐����܂�
	 */
	public static <T> Set<T> set() {
		return new HashSet<T>();
	}

	/**
	 * �w�肵���N���X��HashSet�������e�ʃT�C�Y�Ő������܂�
	 * 
	 * @param <T> �N���X
	 * @param size �����e�ʃT�C�Y
	 * @return �w�肵���N���X��HashSet�𐶐����܂�
	 */
	public static <T> Set<T> set(int size) {
		return new HashSet<T>(size);
	}

	/**
	 * �w�肵�����e�ŃZ�b�g�𐶐����܂��B
	 * 
	 * @param <T> �N���X
	 * @param elements �����l
	 * @return �w�肵�����e�ŃZ�b�g�𐶐����܂��B
	 */
	public static <T> Set<T> set(T... elements) {
		return new HashSet<T>(Arrays.asList(elements));
	}

	/**
	 * �w�肵���N���X��HashMap�𐶐����܂�
	 * 
	 * @param <K> �L�[�ɂȂ�N���X
	 * @param <V> �l�ɂȂ�N���X
	 * @return �w�肵���N���X��HashMap�𐶐����܂�
	 */
	public static <K, V> Map<K, V> map() {
		return new HashMap<K, V>();
	}

	private static class Pair<A, B> {
		private A first;
		private B second;

		public Pair(A first, B second) {
			this.first = first;
			this.second = second;
		}

		public A getFirst() {
			return first;
		}

		public B getSecond() {
			return second;
		}
	}

	/**
	 * �w�肳�ꂽ���e�����Ń}�b�v�𐶐����܂��B
	 * 
	 * <pre>
	 * Map&lt;Integer, String&gt; m = map($(0, &quot;0&quot;), $(1, &quot;1&quot;));
	 * </pre>
	 * 
	 * �̂悤�ɋL�q���܂��B
	 * 
	 * @param <K> �L�[�̃N���X
	 * @param <V> �l�̃N���X
	 * @param entries �G���g���[�̃��e����
	 * @return �w�肳�ꂽ���e�����Ń}�b�v�𐶐����܂��B
	 */
	public static <K, V> Map<K, V> map(Pair<K, V>... entries) {
		Map<K, V> map = new HashMap<K, V>();
		for (Pair<K, V> entry : entries) {
			map.put(entry.getFirst(), entry.getSecond());
		}
		return map;
	}

	/**
	 * �l�ƃL�[�̃y�A�𐶐����܂��B
	 * 
	 * @param <A> �L�[�̃N���X
	 * @param <B> �l�̃N���X
	 * @param first �L�[�l
	 * @param second �l
	 * @return �l�ƃL�[�̃y�A�𐶐����܂��B
	 */
	public static <A, B> Pair<A, B> $(A first, B second) {
		return new Pair<A, B>(first, second);
	}
}
