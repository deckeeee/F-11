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

package org.F11.scada.server.communicater;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

/**
 * �Z���N�^�̃��X�i�[�C���^�[�t�F�[�X�ł��B
 */
public interface PortListener {
	/** ���̃��X�i�[�̕ێ�����|�[�g���J���܂��B */
	public SelectableChannel open() throws IOException, InterruptedException;
	/** ���̃��X�i�[�̕ێ�����|�[�g����܂��B */
	public void close() throws IOException;

	/** �|�[�g��Accept�\��ԂɂȂ������Ɍďo����܂��B */
	public void onAccept() throws IOException;
	/** �|�[�g��Connect�\��ԂɂȂ������Ɍďo����܂��B */
	public void onConnect() throws IOException, InterruptedException;
	/** �|�[�g��Read�\��ԂɂȂ������Ɍďo����܂��B */
	public void onRead() throws IOException;
	/** �|�[�g��Write�\��ԂɂȂ������Ɍďo����܂��B */
	public void onWrite() throws IOException, InterruptedException;
}
