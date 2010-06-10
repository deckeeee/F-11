/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package org.F11.scada.server.communicater;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.F11.scada.WifeException;
import org.F11.scada.server.converter.Converter;

/**
 * Communicater�̃t�@�N�g���ł��B static class ���� instance
 * class�ɕύX�B�V���O���g���̐���́ADI�R���e�i�ɈϏ�����悤�ɕύX�B
 */
public final class CommunicaterFactoryImpl implements CommunicaterFactory {
	private final Map<Environment, Communicater> communicaterMap;

	/**
	 * �f�t�H���g�R���X�g���N�^
	 */
	public CommunicaterFactoryImpl() {
		communicaterMap = new HashMap<Environment, Communicater>();
	}

	/**
	 * �V����Communicater���쐬����Ɠ����ɁA���X�i�[�o�^���܂��B
	 * @param device �f�o�C�X���
	 * @param listener �o�^���郊�X�i�[
	 * @return �V����Communicater�I�u�W�F�N�g
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws WifeException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public Communicater createCommunicator(Environment device) throws Exception {
		if (communicaterMap.containsKey(device)) {
			return communicaterMap.get(device);
		}

		Communicater communicater = new PlcCommunicater(device,
				getConverter(device));
		communicaterMap.put(device, communicater);
		return communicater;
	}

	/**
	 * �R�}���h�ϊ��N���X�̃t�@�N�g�����\�b�h
	 */
	private Converter getConverter(Environment device) throws WifeException {
		String className = "org.F11.scada.server.converter."
				+ device.getPlcCommKind();
		try {
			Class<?> c = Class.forName(className);
			Converter conv = (Converter) c.newInstance();
			/** ����ݒ� */
			conv.setEnvironment(device);
			return conv;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		throw new WifeException();
	}
}
