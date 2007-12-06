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
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * �Z���N�^�y�у|�[�g���Ǘ�����ׂ̃N���X�ł��B
 * �|�[�g�ւ̃��X�i�[�ǉ��A�폜�͕K�����̃I�u�W�F�N�g����čs���܂��B
 * �V���O���g��
 */
public final class PortChannelManager {
	private final static Logger log =
		Logger.getLogger(PortChannelManager.class);

	/** ���̃I�u�W�F�N�g�̃C���X�^���X */
	private final static PortChannelManager instance = new PortChannelManager();
	/** �|�[�g�̃}�b�v */
	protected final static Map portChannels = new HashMap();
	/** �Z���N�^ */
	protected static PortSelector portSelector;

	/**
	 * �v���C�x�[�g�R���X�g���N�^
	 */
	private PortChannelManager() {
	}

	/**
	 * ���̃I�u�W�F�N�g�̃C���X�^���X��Ԃ��܂��B
	 */
	public static PortChannelManager getInstance() {
		return instance;
	}

	/**
	 * �|�[�g���擾���A���X�i�[��o�^���܂��B
	 * �|�[�g���o�^����Ă��Ȃ��ꍇ�͐V���Ƀ|�[�g���J���A�Z���N�^�ɓo�^���܂��B
	 * �Z���N�^�������ꍇ�͍쐬���܂��B
	 * @param portKind �|�[�g���
	 * @param local �z�X�g�̃A�h���X
	 * @param listener �o�^���郊�X�i�[
	 * @return �|�[�g���Ǘ�����I�u�W�F�N�g
	 */
	public synchronized PortChannel addPortListener(
		String portKind,
		InetSocketAddress target,
		InetSocketAddress local,
		RecvListener listener)
		throws IOException, InterruptedException {
		log.debug("addPortListener");
		PortChannel port = getPortChannel(portKind, target, local);
		port.addListener(listener);
		return port;
	}

	/**
	 * ���X�i�[���폜���܂��B
	 * �|�[�g�̃��X�i�[���S�č폜���ꂽ�ꍇ�ɁA�|�[�g����ăZ���N�^����������܂��B
	 * �S�Ẵ|�[�g���������ꂽ�ꍇ�ɃZ���N�^���폜���܂��B
	 * @param local �z�X�g�̃A�h���X
	 * @param listener �폜���郊�X�i�[
	 */
	public synchronized void removePortListener(
		InetSocketAddress local,
		RecvListener listener)
		throws InterruptedException {
		String key = getKey(local);
		log.debug("removePortListener :" + key);
		PortChannel port = (PortChannel) portChannels.get(key);
		if (port.removeListener(listener)) {
			portChannels.remove(key);
			port.closeReq();
			if (!portSelector.isActive()) {
				portSelector = null;
			}
		}
	}

	/*
	 * �|�[�g���擾���܂��B
	 * �|�[�g���o�^����Ă��Ȃ��ꍇ�͐V���Ƀ|�[�g���J���A�Z���N�^�ɓo�^���܂��B
	 * �Z���N�^�������ꍇ�͍쐬���܂��B
	 */
	private PortChannel getPortChannel(
		String portKind,
		InetSocketAddress target,
		InetSocketAddress local)
		throws IOException, InterruptedException {
		if (portSelector == null) {
			portSelector = new PortSelector();
			log.debug("create PortSelector");
		}

		String key = getKey(local);
		PortChannel port = (PortChannel) portChannels.get(key);
		if (port == null) {
			key = getKey(target);
			port = (PortChannel) portChannels.get(key);
		}
		if (port == null) {
			if ("UDP".equals(portKind)) {
				key = getKey(local);
				port = new UdpPortChannel(local, portSelector);
				log.debug("create UdpPortChannel");
			} else if ("TCP".equals(portKind)) {
				key = getKey(target);
				port = new TcpPortChannel(portSelector, target);
				log.debug("create TcpPortChannel");
			} else {
				throw new IllegalArgumentException(
					"Non suport port kind. [" + portKind + "]");
			}
			portChannels.put(key, port);
		}
		log.debug("getPortChannel :" + key);
		return port;
	}

	/*
	 * �|�[�g���Ǘ�����ׂ̃L�[���쐬���܂��B
	 */
	private String getKey(InetSocketAddress local) {
		return local.getAddress().getHostAddress() + ":" + local.getPort();
	}
}
