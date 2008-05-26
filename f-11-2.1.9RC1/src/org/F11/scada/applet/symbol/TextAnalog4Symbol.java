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

import javax.swing.JLabel;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataReferencer;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog4;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.xwife.server.WifeDataProvider;

/**
 * @author hori
 */
public class TextAnalog4Symbol extends TextSymbol {
	private static final long serialVersionUID = -2578243626875816816L;
	/** �Q�Ƃ��郊�t�@�����T�ł��B */
	protected DataReferencer dataReferencer;
	/** ���[�J���ŕێ�����\���p�^�[�� */
	protected String pattern;
	/** �\���f�[�^�C���f�b�N�X */
	protected int index;

	/**
	 * Constructor for TextAnalog4Symbol.
	 * @param property SymbolProperty �I�u�W�F�N�g
	 */
	public TextAnalog4Symbol(SymbolProperty property) {
		super(property);
		init(getProperty("value"));
	}

	/**
	 * �f�t�H���g�̃R���X�g���N�^�ł��B
	 */
	public TextAnalog4Symbol() {
		this(null);
	}

	protected void init(String value) {
		index = Integer.parseInt(getProperty("index"));
		pattern = getProperty("format");
		int p = value.indexOf('_');
		if (0 < p) {
			dataReferencer = new DataReferencer(value.substring(0, p), value.substring(p + 1));
		}
		connectReferencer();
	}

	/**
	 * �������ݒ肵�܂��B
	 * @see org.F11.scada.applet.symbol.TextSymbol#setFormatedString()
	 */
	protected void setFormatedString() {
		DataHolder dh = dataReferencer.getDataHolder();
		if (null != dh) { 
			WifeData wd = (WifeData) dh.getValue();
			if (!(wd instanceof WifeDataAnalog4))
				return;

			WifeDataAnalog4 wa = (WifeDataAnalog4) wd;
			ConvertValue conv = (ConvertValue) dh.getParameter(WifeDataProvider.PARA_NAME_CONVERT);

			String strValue = message.getErrorText();
			if (dh.getQualityFlag() == WifeQualityFlag.GOOD) {
				if (pattern != null) {
					strValue = conv.convertStringValue(wa.doubleValue(index), pattern);
				} else {
					strValue = conv.convertStringValue(wa.doubleValue(index));
				}
			} else if (dh.getQualityFlag() == WifeQualityFlag.INITIAL) {
	            strValue = message.getInitText();
			}

			String halg = getProperty("h_aligin");
			if (halg != null) {
				int alignment = JLabel.RIGHT;
				if ("LEFT".equals(halg))
					alignment = JLabel.LEFT;
				else if ("CENTER".equals(halg))
					alignment = JLabel.CENTER;
				else if ("RIGHT".equals(halg))
					alignment = JLabel.RIGHT;
				else if ("LEADING".equals(halg))
					alignment = JLabel.LEADING;
				else if ("TRAILING ".equals(halg))
					alignment = JLabel.TRAILING;
				this.setHorizontalAlignment(alignment);
			}

			setText(strValue);
		}
	}

	/**
	 * �������g��Manager�ɓo�^���܂��B
	 */
	public void connectReferencer() {
		if (dataReferencer != null) {
			dataReferencer.connect(this);
		}
	}

	/**
	 * �e�V���{����Manager����o�^�������܂��B
	 */
	public void disConnect() {
		if (dataReferencer != null) {
			dataReferencer.disconnect(this);
			dataReferencer = null;
		}

		super.disConnect();
	}

}
