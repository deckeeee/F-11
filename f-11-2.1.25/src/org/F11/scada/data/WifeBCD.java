/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/WifeBCD.java,v 1.2.6.2 2006/03/15 00:01:03 frdm Exp $
 * $Revision: 1.2.6.2 $
 * $Date: 2006/03/15 00:01:03 $
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

package org.F11.scada.data;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;

/**
 * byte��16�i���\����BCD�ň����N���X�B
 * �C���X�^���X���쐬���邱�Ƃ͂ł��܂���B
 * static method �݂̂���Ȃ�N���X�ł��B
 * @see #valueOf(byte[] bytes)
 */
public final class WifeBCD {
	private static final DecimalFormat decimalFormat = new DecimalFormat("0000");

	/** �C���X�^���X�͍쐬�ł��܂���B*/
	private WifeBCD() {}

	/**
	 * byte�z���BCD�\���ŕ\�����������Ԃ��܂��B
	 * @param bytes BCD�\����byte�z��B
	 */
	public static String toString(byte[] bytes) {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		StringBuffer sb = new StringBuffer();
		byte source[] = new byte[1];
		while (in.read(source, 0, 1) != -1) {
			byte low = (byte)(source[0] & 0x0F);
			byte high = (byte)(source[0] & 0xF0);
			high = (byte)((high & 0xFF) >>> 4);
			if (low > 9 || high > 9) {
				/** @todo �L���b�`������ŃG���[�n���h�����O���Ă������� */
				throw new BCDConvertException(Integer.toHexString(low).toUpperCase() +
												   Integer.toHexString(high).toUpperCase());
			}
			Byte byteLow = new Byte(low);
			Byte byteHigh = new Byte(high);
			int intLow = byteLow.intValue();
			int intHigh = byteHigh.intValue();
			sb.append(intHigh);
			sb.append(intLow);
		}
		return sb.toString();
	}

	/**
	 * BCD�\����byte�z���double�ɕϊ����ĕԂ��܂��B
	 * @param bytes BCD�\����byte�z��B
	 */
	public static double valueOf(byte[] bytes) {
		return Double.parseDouble(toString(bytes));
	}

	/**
	 * double���l��BCD�\����byte�z��ɕϊ����ĕԂ��܂��B
	 * @param value �ϊ������l
	 * @param format �\���t�H�[�}�b�g
	 */
	public static byte[] valueOf(double value, String format) {
		DecimalFormat df = new DecimalFormat(format);
		return WifeBCD.toByteArray(df.format(value));
	}
	
	public static byte[] valueOf(double value) {
		synchronized (decimalFormat) {
			return WifeBCD.toByteArray(decimalFormat.format(value));
		}
	}

	/**
	 * ������->byte�z�� �ϊ����[�`��
	 * @param srcString BCD�\���̕�����
	 */
	public static byte[] toByteArray(String srcString) {
		if (0 < srcString.length() % 2)
			throw new IllegalArgumentException("Specify an even number!");
		String getno = "0123456789abcdef0123456789ABCDEF";
		byte[] retval = new byte[srcString.length() / 2];
		for(int spos = 0; spos < srcString.length(); spos += 2) {
			int ch = getno.indexOf(srcString.charAt(spos)) % 16;
			int cl = getno.indexOf(srcString.charAt(spos + 1)) % 16;
			if (ch < 0 || cl < 0)
				throw new BCDConvertException("Specify the character of 'a' to 'f'!");
			int bpos = spos / 2;
			retval[bpos] = (byte)(ch * 0x10 + cl);
		}
		return retval;
	}
}
