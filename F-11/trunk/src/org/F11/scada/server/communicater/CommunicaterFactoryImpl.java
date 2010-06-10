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
 * Communicaterのファクトリです。 static class から instance
 * classに変更。シングルトンの制御は、DIコンテナに委譲するように変更。
 */
public final class CommunicaterFactoryImpl implements CommunicaterFactory {
	private final Map<Environment, Communicater> communicaterMap;

	/**
	 * デフォルトコンストラクタ
	 */
	public CommunicaterFactoryImpl() {
		communicaterMap = new HashMap<Environment, Communicater>();
	}

	/**
	 * 新しいCommunicaterを作成すると同時に、リスナー登録します。
	 * @param device デバイス情報
	 * @param listener 登録するリスナー
	 * @return 新しいCommunicaterオブジェクト
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
	 * コマンド変換クラスのファクトリメソッド
	 */
	private Converter getConverter(Environment device) throws WifeException {
		String className = "org.F11.scada.server.converter."
				+ device.getPlcCommKind();
		try {
			Class<?> c = Class.forName(className);
			Converter conv = (Converter) c.newInstance();
			/** 環境を設定 */
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
