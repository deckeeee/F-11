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
package org.F11.scada.parser.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * �ݒ�t�@�C�����A�N���C�A���g���̐ݒ�𐶐�����N���X�ł��B
 */
public class ClientsDefine {
	private final Map configMap = new HashMap();

	/**
	 * �R���X�g���N�^
	 */
	public ClientsDefine() {
		this("/resources/ClientsDefine.xml");
	}

	/**
	 * �R���X�g���N�^
	 */
	public ClientsDefine(String file) {
		Digester digester = new Digester();
		digester.push(this);

		addPageRule(digester);

		URL xml = getClass().getResource(file);
		if (xml == null) {
			return;
		}
		InputStream is = null;
		try {
			is = xml.openStream();
			digester.parse(is);
			is.close();
		} catch (Exception e) {
			throw new IllegalStateException();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * �_�C�W�F�X�^�Ƀ��[����ǉ����܂��B
	 */
	private void addPageRule(Digester digester) {
		digester.addObjectCreate("client_map/client", ClientConfig.class);
		digester.addSetNext("client_map/client", "addClientConfig");
		digester.addSetProperties("client_map/client");
		digester.addSetProperties("client_map/client/defaultuser");
	}

	/**
	 * �N���C�A���g�̐ݒ��Ԃ��܂��B
	 * 
	 * @param key �N���C�A���g��IP�A�h���X������
	 * @return �N���C�A���g�̐ݒ�
	 */
	public ClientConfig getClientConfig(String key) {
		ClientConfig cd = (ClientConfig) configMap.get(key);
		if (cd != null)
			return cd;
		return ClientConfig.Nothing;
	}

	/**
	 * �N���C�A���g�̐ݒ��ǉ����܂��B
	 * 
	 * @param config �N���C�A���g�̐ݒ�
	 */
	public void addClientConfig(ClientConfig config) {
		configMap.put(config.getIpaddress(), config);
	}

	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂�
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
