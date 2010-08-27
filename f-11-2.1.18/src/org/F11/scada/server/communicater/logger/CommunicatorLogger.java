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

package org.F11.scada.server.communicater.logger;

import org.apache.log4j.Logger;

/**
 * �ʐM�����̃��M���O�����񐔊Ԋu�ŏo�͂���B
 * 
 * �ʐM�����̃��M���O��S�ďo�͂���ƁA�����Ɉ���̂ł��̃N���X
 * ���g�p���āA���Ԋu�Ń��O�o�͂��s���B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class CommunicatorLogger {
	/** Logging API */
	private static Logger log = Logger.getLogger(CommunicatorLogger.class);
	/** ���O�o�̓J�E���^ */
	private int count;
	/** ���̌������Ƀ��O�o�͂��s���܂��B */
	private final int max;

	/**
	 * �f�t�H���g�̌���(1000��)���Ƀ��O�o�͂���悤�ɏ��������܂��B
	 */
	public CommunicatorLogger() {
		this(1000);
	}

	/**
	 * �����̌������Ƀ��O�o�͂���悤�ɏ��������܂��B
	 * @param max ���O�o�͂��錏��
	 */	
	public CommunicatorLogger(int max) {
		this.max = max;
		count = max;
	}

	/**
	 * ���M���O�o�͂��܂��B
	 * @param o ���M���O���
	 */
	public void log(Object o) {
		if (count >= max) {
			if (log.isDebugEnabled()) {
				log.debug(o);
			}
			count = 0;
		} else {
			count++;
		}
	}
}
