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

package org.F11.scada.applet.symbol;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataReferencer;

import org.F11.scada.data.StringData;
import org.apache.log4j.Logger;

/**
 * ������l��\������N���X�ł��B
 * 
 * @author maekawa
 */
public class StringDataSymbol extends TextSymbol {
	private static final long serialVersionUID = 1L;
	private final Logger logger = Logger.getLogger(StringDataSymbol.class);
	/** �Q�Ƃ��郊�t�@�����T�ł��B */
	protected DataReferencer dataReferencer;

	/**
	 * @param property SymbolProperty �I�u�W�F�N�g
	 */
	public StringDataSymbol(SymbolProperty property) {
		super(property);
		init(getProperty("value"));
		setAlign();
	}

	protected void init(String value) {
		int p = value.indexOf('_');
		if (0 < p) {
			dataReferencer = new DataReferencer(value.substring(0, p), value
					.substring(p + 1));
			dataReferencer.connect(this);
		}
	}

	/**
	 * �������ݒ肵�܂��B
	 * 
	 * @see org.F11.scada.applet.symbol.TextSymbol#setFormatedString()
	 */
	protected void setFormatedString() {
		DataHolder dh = dataReferencer.getDataHolder();
		if (null != dh) {
			StringData sd = (StringData) dh.getValue();
			setText(sd.toString());
		} else {
			logger.error("�f�[�^�z���_ " + dataReferencer.getDataProviderName() + "_"
					+ dataReferencer.getDataHolderName() + " �����݂��܂���B");
			setText(message.getErrorText());
		}
		setAlign();
	}

	/**
	 * �e�V���{����Manager����o�^�������܂��B
	 */
	public void disConnect() {
		if (null != dataReferencer) {
			dataReferencer.disconnect(this);
		}
		super.disConnect();
	}
}
