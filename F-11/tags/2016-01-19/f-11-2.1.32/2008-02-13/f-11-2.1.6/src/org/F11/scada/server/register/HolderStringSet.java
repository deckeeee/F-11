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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.F11.scada.applet.expression.Expression;

/**
 * HolderString��ێ�����Set�N���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
 public class HolderStringSet implements Set, Serializable {
	 private static final long serialVersionUID = -718325314424531400L;

	 private final Set holderStringSet;
     
     public HolderStringSet() {
         this(16);
     }

     public HolderStringSet(Collection c) {
         holderStringSet = new HashSet(c);
     }
     
     public HolderStringSet(int initialCapacity) {
         this(initialCapacity, 0.75F);
     }
     
     public HolderStringSet(int initialCapacity, float loadFactor) {
         holderStringSet = new HashSet(initialCapacity, loadFactor);
     }

    /* (Javadoc �Ȃ�)
     * @see java.util.Collection#size()
     */
    public int size() {
        return holderStringSet.size();
    }

    /* (Javadoc �Ȃ�)
     * @see java.util.Collection#clear()
     */
    public void clear() {
        holderStringSet.clear();
    }

    /* (Javadoc �Ȃ�)
     * @see java.util.Collection#isEmpty()
     */
    public boolean isEmpty() {
        return holderStringSet.isEmpty();
    }

    /* (Javadoc �Ȃ�)
     * @see java.util.Collection#toArray()
     */
    public Object[] toArray() {
        return holderStringSet.toArray();
    }

    /* (Javadoc �Ȃ�)
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o) {
        return holderStringSet.add(o);
    }

    /* (Javadoc �Ȃ�)
     * @see java.util.Collection#contains(java.lang.Object)
     */
    public boolean contains(Object o) {
        return holderStringSet.contains(o);
    }

    /* (Javadoc �Ȃ�)
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o) {
        return holderStringSet.remove(o);
    }

    /* (Javadoc �Ȃ�)
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection c) {
        return holderStringSet.addAll(c);
    }

    /* (Javadoc �Ȃ�)
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection c) {
        return holderStringSet.containsAll(c);
    }

    /* (Javadoc �Ȃ�)
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection c) {
        return holderStringSet.removeAll(c);
    }

    /* (Javadoc �Ȃ�)
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection c) {
        return holderStringSet.retainAll(c);
    }

    /* (Javadoc �Ȃ�)
     * @see java.util.Collection#iterator()
     */
    public Iterator iterator() {
        return holderStringSet.iterator();
    }

    /* (Javadoc �Ȃ�)
     * @see java.util.Collection#toArray(java.lang.Object[])
     */
    public Object[] toArray(Object[] a) {
        return holderStringSet.toArray(a);
    }

    /* (Javadoc �Ȃ�)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return holderStringSet.toString();
    }

    /* (Javadoc �Ȃ�)
     * @see java.util.Set#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return holderStringSet.equals(o);
    }

    /* (Javadoc �Ȃ�)
     * @see java.util.Set#hashCode()
     */
    public int hashCode() {
        return holderStringSet.hashCode();
    }

    /**
     * �A�i���O�^�O�̎��\����������A�z���_�[�𒊏o���Z�b�g�ɒǉ����܂��B
     * @param value �A�i���O�^�O�̎��\��������
     */
    public void setValue(String value) {
        Expression e = new Expression();
        e.toPostfix(value);
        
        for (Iterator it = e.getProviderHolderNames().iterator();
        		it.hasNext();) {
            String holderStr = (String) it.next();
            HolderString holder = new HolderString(holderStr);
            holderStringSet.add(holder);
        }
    }
    
    public void setStart_x(String value) {
        setValue(value);
    }
    
    public void setStart_y(String value) {
        setValue(value);
    }
    
    public void setX(String value) {
        setValue(value);
    }
    
    public void setY(String value) {
        setValue(value);
    }

	/**
	 * �h��IreadResolve���\�b�h�B
	 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	private Object readResolve() throws ObjectStreamException {
		return new HolderStringSet(holderStringSet);
	}
}
