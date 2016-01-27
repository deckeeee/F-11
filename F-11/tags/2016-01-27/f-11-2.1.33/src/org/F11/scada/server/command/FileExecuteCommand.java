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

import java.io.IOException;

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.apache.log4j.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * �w�肳�ꂽ�p�X�Ƀr�b�g�����o�͂���N���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class FileExecuteCommand implements Command {
	/** ���s�t�@�C���p�X�� */
	private String path;
	/** Logging API */
	private static Logger log = Logger.getLogger(FileExecuteCommand.class);
	/** �X���b�h�v�[�����s�N���X */
	private static Executor executor = Executors.newCachedThreadPool();

	/**
	 * �R�}���h�����s���܂�
	 * @param evt �f�[�^�ύX�C�x���g
	 */
	public void execute(DataValueChangeEventKey evt) {
		if (path == null) {
			throw new IllegalStateException("path not setting.");
		}

		try {
			executor.execute(new FileExecuteCommandTask(evt));
		} catch (RejectedExecutionException e) {}
	}

	/**
	 * �p�X����ݒ肵�܂�
	 * @param string �p�X��
	 */
	public void setPath(String string) {
		path = string;
	}

	/**
	 * Executor �Ŏ��s�����^�X�N�̃N���X�ł��B
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	private class FileExecuteCommandTask implements Runnable {
		/** �f�[�^�ύX�C�x���g�̎Q�� */
		private final DataValueChangeEventKey evt;

		/**
		 * �^�X�N�����������܂�
		 * @param evt �f�[�^�ύX�C�x���g
		 */
		FileExecuteCommandTask(DataValueChangeEventKey evt) {
			this.evt = evt;
		}

		/**
		 * Executor �ɂ����s����郁�\�b�h�ł��B
		 */
		public void run() {
			if (evt.getValue().booleanValue()) {
				Runtime run = Runtime.getRuntime();
				try {
					log.info(path + " �����s���܂�");
					run.exec(path);
				} catch (IOException e) {
					log.error(path + "�̎��s�Ɏ��s���܂����B", e);
				}
			}
		}
	}
}
