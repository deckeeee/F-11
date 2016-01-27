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
 */

package org.F11.scada.server.command;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import org.F11.scada.server.alarm.DataValueChangeEventKey;

/**
 * �w�肳�ꂽ�p�X�Ƀr�b�g�����o�͂���N���X�ł��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class WriteTermCommand implements Command {
	/** �X���b�h�v�[�����s�N���X */
	private static Executor executor = Executors.newCachedThreadPool();

	/** �v���o�C�_�� */
	private String provider;
	/** �z���_�� */
	private String holder;
	/** �������ޒl */
	private String value;

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * �R�}���h�����s���܂�
	 * 
	 * @param evt �f�[�^�ύX�C�x���g
	 */
	public void execute(DataValueChangeEventKey evt) {
		if (null == provider) {
			throw new IllegalStateException("provider���ݒ肳��Ă��܂���");
		}
		if (null == holder) {
			throw new IllegalStateException("holder���ݒ肳��Ă��܂���");
		}
		if (null == value) {
			throw new IllegalStateException("value���ݒ肳��Ă��܂���");
		}

		try {
			executor.execute(new WriteTermCommandTask(
				evt,
				provider,
				holder,
				value));
		} catch (RejectedExecutionException e) {
		}
	}
}
