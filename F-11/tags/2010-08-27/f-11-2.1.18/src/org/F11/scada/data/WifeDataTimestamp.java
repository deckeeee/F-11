/*
 * Projrct    F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All
 * Rights Reserved.
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

package org.F11.scada.data;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * �^�C���X�^���v�f�[�^�N���X�ł��B
 * ���̃N���X�͕s�σN���X�ł��Anew���Z�q�ŃC���X�^���X�𐶐����邱�Ƃ��ł��܂���B
 * valueOf�`���\�b�h���g�p���ăC���X�^���X���쐬���Ă��������B
 * @author hori
 */
public final class WifeDataTimestamp implements WifeData, Serializable {
	static final long serialVersionUID = 933696283955624139L;

	private transient static final int TIMESTAMP_WORD_COUNT = 8;
	/** �j���ԍ��ϊ��e�[�u�� */
	private transient static final Map WEEK2NO;
	static {
		WEEK2NO = new HashMap();
		WEEK2NO.put(new Integer(Calendar.SUNDAY), new Integer(0));
		WEEK2NO.put(new Integer(Calendar.MONDAY), new Integer(1));
		WEEK2NO.put(new Integer(Calendar.TUESDAY), new Integer(2));
		WEEK2NO.put(new Integer(Calendar.WEDNESDAY), new Integer(3));
		WEEK2NO.put(new Integer(Calendar.THURSDAY), new Integer(4));
		WEEK2NO.put(new Integer(Calendar.FRIDAY), new Integer(5));
		WEEK2NO.put(new Integer(Calendar.SATURDAY), new Integer(6));
	}
	/** Calendar �^�� value ��\���B�V���A���C�Y���Ȃ� */
	private transient final Calendar calendarValue;
	/** ���̃I�u�W�F�N�g���ێ�����l */
	private long value;

	/**
	 * �R���X�g���N�^
	 */
	private WifeDataTimestamp(int year, int month, int date, int hour, int minute, int second) {
		this.calendarValue = new GregorianCalendar(year, month, date, hour, minute, second);
		this.value = calendarValue.getTimeInMillis();
	}
	private WifeDataTimestamp(long millis) {
		this.calendarValue = new GregorianCalendar();
		this.calendarValue.setTimeInMillis(millis);
		this.value = calendarValue.getTimeInMillis();
	}

	/**
	 * ���̃f�[�^�̃��[�h����Ԃ��܂��B
	 * @return ���̃f�[�^�̃��[�h��
	 */
	public int getWordSize() {
		return TIMESTAMP_WORD_COUNT;
	}

	/**
	 * ���̃f�[�^�̒l���o�C�g�z��ϊ����ĕԂ��܂��B
	 * @return �o�C�g�z��
	 */
	public byte[] toByteArray() {
		byte[] ret = new byte[TIMESTAMP_WORD_COUNT * 2];
		Arrays.fill(ret, (byte) 0x00);
		int pos = 0;
		byte[] b = WifeBCD.valueOf(calendarValue.get(Calendar.YEAR));
		System.arraycopy(b, 0, ret, pos, b.length);
		pos += b.length;
		b = WifeBCD.valueOf(calendarValue.get(Calendar.MONTH) + 1);
		System.arraycopy(b, 0, ret, pos, b.length);
		pos += b.length;
		b = WifeBCD.valueOf(calendarValue.get(Calendar.DAY_OF_MONTH));
		System.arraycopy(b, 0, ret, pos, b.length);
		pos += b.length;
		Integer week = new Integer(calendarValue.get(Calendar.DAY_OF_WEEK));
		b = WifeBCD.valueOf(((Integer) WEEK2NO.get(week)).intValue());
		System.arraycopy(b, 0, ret, pos, b.length);
		pos += b.length;
		b = WifeBCD.valueOf(calendarValue.get(Calendar.HOUR_OF_DAY));
		System.arraycopy(b, 0, ret, pos, b.length);
		pos += b.length;
		b = WifeBCD.valueOf(calendarValue.get(Calendar.MINUTE));
		System.arraycopy(b, 0, ret, pos, b.length);
		pos += b.length;
		b = WifeBCD.valueOf(calendarValue.get(Calendar.SECOND));
		System.arraycopy(b, 0, ret, pos, b.length);
		pos += b.length;
		ret[pos++] = 0x00;
		ret[pos++] = 0x01;
		return ret;
	}

	/**
	 * �o�C�g�z����f�[�^�ɕϊ����܂��B
	 * @param b �o�C�g�z��
	 */
	public WifeData valueOf(byte[] b) {
		int[] paras = new int[TIMESTAMP_WORD_COUNT];
		byte[] item = new byte[2];
		for (int i = 0; i < TIMESTAMP_WORD_COUNT - 1; i++) {
			System.arraycopy(b, i * 2, item, 0, item.length);
			paras[i] = (int) WifeBCD.valueOf(item);
		}

		return new WifeDataTimestamp(
			paras[0],
			paras[1] - 1,
			paras[2],
			paras[4],
			paras[5],
			paras[6]);
	}

	public static WifeDataTimestamp valueOfType1(long millis) {
		return new WifeDataTimestamp(millis);
	}

	public Calendar calendarValue() {
		Calendar retcal = new GregorianCalendar();
		retcal.setTimeInMillis(this.value);
		return retcal;
	}

	/**
	 * ���̃I�u�W�F�N�g�Ǝw�肳�ꂽ�I�u�W�F�N�g���r���܂��B�l�Ɋւ��Ă�Calendar�N���X��equals���\�b�h�̋K����
	 * ���Ă͂܂�܂��B
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WifeDataTimestamp)) {
			return false;
		}
		WifeDataTimestamp wd = (WifeDataTimestamp) obj;
		return wd.value == value;
	}

	/**
	 * ���̃I�u�W�F�N�g�̃n�b�V���R�[�h��Ԃ��܂��B
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + (int) value;
		return result;
	}

	/**
	 * �I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 * {�A�i���O�f�[�^;�A�i���O�f�[�^�̌`��}�̏����ŕ\������܂��B
	 * ���A���̕\���`���͏����ύX�����\��������܂��B
	 */
	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd EEE HH:mm:ss.SSS");
		return "{" + format.format(calendarValue.getTime()) + "}";
	}

	/**
	 * �h��IreadResolve���\�b�h�B
	 * �s���Ƀf�V���A���C�Y�����̂�h�~���܂��B
	 * @return Object �f�V���A���C�Y���ꂽ�C���X�^���X
	 * @throws ObjectStreamException �f�V���A���C�Y�Ɏ��s������
	 */
	private Object readResolve() throws ObjectStreamException {
		return new WifeDataTimestamp(value);
	}
}
