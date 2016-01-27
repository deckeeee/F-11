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

package org.F11.scada.server.register.impl;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.DataReferencer;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.Globals;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.AlarmReferencer;
import org.F11.scada.server.demand.DemandDataReferencer;
import org.F11.scada.server.entity.AnalogType;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.register.WifeDataUtil;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;

/**
 * �f�[�^�z���_�[�o�^���[�e�B���e�B�[
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class RegisterUtil {
	private static final Logger logger = Logger.getLogger(RegisterUtil.class);
	private RegisterUtil() {
	}

	/**
	 * Item �I�u�W�F�N�g�� WifeData �I�u�W�F�N�g���f�[�^�z���_�[�𐶐����Ԃ��܂��B
	 * 
	 * @param item �f�[�^�z���_�[�̏�񂪕ێ����ꂽ Item �I�u�W�F�N�g
	 * @param wd �f�[�^�I�u�W�F�N�g
	 * @return �f�[�^�z���_�[�E�I�u�W�F�N�g
	 */
	public static DataHolder getDataHolder(Item item, WifeData wd) {
		DataHolder dh = new DataHolder();
		dh.setValueClass(WifeData.class);
		dh.setValue(wd, new Date(), WifeQualityFlag.INITIAL);
		dh.setDataHolderName(item.getHolder());
		WifeCommand wc = new WifeCommand(item);
		dh.setParameter(WifeDataProvider.PARA_NAME_COMAND, wc);
		dh.setParameter(WifeDataProvider.PARA_NAME_POINT, item.getPoint());
		if (Globals.ERR_HOLDER.equals(item.getHolder())) {
			dh
					.setParameter(
							WifeDataProvider.PARA_NAME_CYCLEREAD,
							Boolean.FALSE);
		} else {
			dh.setParameter(WifeDataProvider.PARA_NAME_CYCLEREAD, Boolean
					.valueOf(item.isComCycleMode()));
		}
		dh
				.setParameter(WifeDataProvider.PARA_NAME_OFFDELAY, item
						.getOffDelay());
		return dh;
	}

	/**
	 * �f�[�^�z���_�[�� JIM �t���[�����[�N�ɓo�^���܂��B
	 * 
	 * @param dh �f�[�^�z���_�[
	 * @param item �A�C�e���I�u�W�F�N�g
	 */
	public static void addDataHolder(DataHolder dh, Item item) {
		Manager manager = Manager.getInstance();
		DataProvider dp = manager.getDataProvider(item.getProvider());
		try {
			dp.addDataHolder(dh);
		} catch (DataProviderDoesNotSupportException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �A�C�e���I�u�W�F�N�g���A�i���O�l�R���o�[�^�[�𐶐����܂��B
	 * 
	 * @param item �A�C�e���I�u�W�F�N�g
	 * @return �A�i���O�l�R���o�[�^�[
	 */
	public static ConvertValue getConvertValue(Item item) {
		int dataType = item.getDataType();
		if (dataType == 0) {
			return createDigitalConvert();
		} else {
			return createAnalogConvert(item);
		}
	}

	private static ConvertValue createDigitalConvert() {
		Class[] types = { Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE,
				String.class };
		Object[] params = { new Double(0), new Double(1), new Double(0),
				new Double(1), "0" };
		ConvertValue conv = null;
		try {
			Method method = ConvertValue.class.getMethod(
					"valueOfDIGITAL",
					types);
			conv = (ConvertValue) method.invoke(null, params);
		} catch (Exception e) {
			throw new IllegalArgumentException("convert_type : " + "DIGITAL"
					+ "  params : " + params);
		}
		return conv;
	}

	private static ConvertValue createAnalogConvert(Item item) {
		AnalogType type = item.getAnalogType();
		String convertType = type.getConvertType();
		if (convertType == null) {
			convertType = "ANALOG";
		}
		Class[] types = { Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE,
				String.class };
		Object[] params = { type.getConvertMin(), type.getConvertMax(),
				type.getInputMin(), type.getInputMax(), type.getFormat() };
		ConvertValue conv = null;
		try {
			Method method = ConvertValue.class.getMethod("valueOf"
					+ convertType, types);
			conv = (ConvertValue) method.invoke(null, params);
		} catch (Exception e) {
			throw new IllegalArgumentException("convert_type : "
					+ type.getAnalogTypeId() + "  params : " + params);
		}

		return conv;
	}

	/**
	 * �f�}���h�p�f�[�^�z���_�[�𐶐��� JIM �ɓo�^���܂��B
	 * 
	 * @param item �A�C�e���I�u�W�F�N�g
	 * @param dh �f�[�^�z���_�[
	 * @param alarmReferencer �f�}���h�E�f�[�^���t�@�����T�[
	 */
	public static void setDemand(Item item, DataHolder dh) {

		// �f�}���h�f�[�^
		String argv = item.getDataArgv();
		// TODO ���������܂Ƃ��ȃ`�F�b�N����
		if (argv != null && !argv.matches("\\d")) {
			StringTokenizer tokenizer = new StringTokenizer(argv, ",");
			if (tokenizer.countTokens() == 2) {
				String[] argvs = new String[tokenizer.countTokens()];
				argvs[0] = tokenizer.nextToken().trim();
				argvs[1] = tokenizer.nextToken().trim();

				DataReferencer dr = new DataReferencer(item.getProvider(), item
						.getHolder());

				LinkedHashMap map = new LinkedHashMap();
				for (int i = 0; i < 60; i++) {
					map.put(new Integer(i), null);
				}
				dh.setParameter(DemandDataReferencer.GRAPH_DATA, map);
				dh.setParameter(DemandDataReferencer.HOLDER_NAME, argvs[0]);
				dh.setParameter(DemandDataReferencer.TABLE_NAME, argvs[1]);
				DataProvider dp = dh.getDataProvider();
				AlarmReferencer ar = (AlarmReferencer) dp
						.getParameter(WifeDataProvider.PARA_NAME_DEMAND);
				ar.addReferencer(dr);
			}
		}
	}

	public static void removeDataHolder(Item item) {
		WifeData wd = WifeDataUtil.getWifeData(item);
		DataHolder dh = RegisterUtil.getDataHolder(item, wd);
		Manager manager = Manager.getInstance();
		DataProvider dp = manager.getDataProvider(item.getProvider());
		try {
			dp.removeDataHolder(dh);
		} catch (DataProviderDoesNotSupportException e) {
			e.printStackTrace();
		}
	}
}
