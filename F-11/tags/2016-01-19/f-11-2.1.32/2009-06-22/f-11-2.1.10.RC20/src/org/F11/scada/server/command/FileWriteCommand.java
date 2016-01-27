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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.apache.log4j.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * �w�肳�ꂽ�p�X�Ƀr�b�g�����o�͂���N���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class FileWriteCommand implements Command {
	/** �p�X�� */
	private String path;
	/** �������݃G���[���g���C�Ԋu */
	private long errorRetryTime = 1000;
	/** �������݃G���[���g���C�� */
	private int errorRetryCount = 1;
	/** Logging API */
	private static Logger log = Logger.getLogger(FileWriteCommand.class);
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
			executor.execute(new FileWriteCommandTask(evt));
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
	 * �������݃G���[���g���C�񐔂�ݒ肵�܂�
	 * @param i �������݃G���[���g���C��
	 */
	public void setErrorRetryCount(int i) {
		errorRetryCount = i;
	}

	/**
	 * �������݃G���[���g���C�Ԋu��ݒ肵�܂��B
	 * @param i �������݃G���[���g���C�Ԋu
	 */
	public void setErrorRetryTime(int i) {
		errorRetryTime = i;
	}


	/**
	 * Executor �Ŏ��s�����^�X�N�̃N���X�ł��B
	 * 
	 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
	 */
	private class FileWriteCommandTask implements Runnable {
		/** �f�[�^�ύX�C�x���g�̎Q�� */
		private final DataValueChangeEventKey evt;

		/**
		 * �^�X�N�����������܂�
		 * @param evt �f�[�^�ύX�C�x���g
		 */
		FileWriteCommandTask(DataValueChangeEventKey evt) {
			this.evt = evt;
		}

		/**
		 * Executor �ɂ����s����郁�\�b�h�ł��B
		 */
		public void run() {
			PrintWriter out = null;
			for (int i = 1; i <= errorRetryCount; i++) {
				try {
					out = new PrintWriter(new BufferedWriter(new FileWriter(path)));
					if (evt.getValue().booleanValue()) {
						out.write("1");
					} else {
						out.write("0");
					}
					break;
				} catch (IOException e) {
					log.error(
						"�������݃G���[���������܂����B"
							+ errorRetryTime
							+ "�~���b��Ƀ��g���C���܂��B ("
							+ i
							+ "/"
							+ errorRetryCount
							+ ")");
					sleep(errorRetryTime);
					continue;
				} finally {
					if (out != null) {
						out.close();
					}
				}
			}
		}
	
		private void sleep(long l) {
			try {
				Thread.sleep(l);
			} catch (InterruptedException e) {}
		}
	}
}
