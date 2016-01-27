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

package org.F11.scada.server.io.postgresql;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.F11.scada.WifeUtilities;
import org.F11.scada.data.BCDConvertException;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.server.communicater.Communicater;
import org.F11.scada.server.communicater.CommunicaterFactory;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.communicater.EnvironmentMap;
import org.F11.scada.server.dao.ItemArrayDao;
import org.F11.scada.server.dao.ItemDao;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.entity.MultiRecordDefine;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.logging.column.ColumnManager;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.server.register.WifeDataUtil;
import org.F11.scada.server.register.impl.RegisterUtil;
import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public final class ItemUtilImpl implements ItemUtil {
	private static final Logger log = Logger.getLogger(ItemUtilImpl.class);
	private final Map itemPool;
	private ColumnManager columnManager;
	private ItemArrayDao itemArrayDao;
	private ItemDao itemDao;
	private CommunicaterFactory communicaterFactory;

	public ItemUtilImpl() {
		// itemPool = Collections.synchronizedMap(new WeakHashMap());
		itemPool = new ConcurrentHashMap();
		columnManager = new ColumnManager();
	}

	public void setItemArrayDao(ItemArrayDao itemArrayDao) {
		this.itemArrayDao = itemArrayDao;
	}

	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	public void setCommunicaterFactory(CommunicaterFactory communicaterFactory) {
		this.communicaterFactory = communicaterFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.io.postgresql.ItemUtil#createConvertValue(java.util
	 * .Collection, java.lang.String)
	 */
	public ConvertValue[] createConvertValue(
			Collection holders,
			String tablename) {
		Item[] items = getItems(holders, itemPool);

		if (log.isDebugEnabled()) {
			log.debug(Arrays.asList(items));
		}

		ConvertValue[] convertValues = new ConvertValue[holders.size()];
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			ConvertValue convertValue = RegisterUtil.getConvertValue(item);
			int column =
				columnManager.getColumnIndex(
					tablename,
					item.getProvider(),
					item.getHolder());
			convertValues[column] = convertValue;
		}
		return convertValues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.io.postgresql.ItemUtil#getItems(java.util.Collection
	 * , java.util.Map)
	 */
	public Item[] getItems(Collection holders, Map itemPool) {
		if (itemPool.containsKey(holders)) {
			return (Item[]) itemPool.get(holders);
		} else {
			Item[] items = itemArrayDao.getItems(holders);
			itemPool.put(holders, items);
			return items;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.io.postgresql.ItemUtil#getItem(org.F11.scada.server
	 * .register.HolderString, java.util.Map)
	 */
	public Item getItem(HolderString dataHolder, Map itemPool) {
		if (itemPool.containsKey(dataHolder)) {
			return (Item) itemPool.get(dataHolder);
		} else {
			Item item = itemDao.getItem(dataHolder);
			itemPool.put(dataHolder, item);
			return item;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.io.postgresql.ItemUtil#getItemMap(org.F11.scada.
	 * server.entity.Item[])
	 */
	public Map getItemMap(Item[] items) {
		HashMap map = new HashMap();
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			if (map.containsKey(item.getProvider())) {
				List list = (List) map.get(item.getProvider());
				list.add(item);
			} else {
				ArrayList list = new ArrayList(items.length);
				list.add(item);
				map.put(item.getProvider(), list);
			}
		}
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.io.postgresql.ItemUtil#createHolderValue(java.util
	 * .Collection, java.lang.String)
	 */
	public DoubleList createHolderValue(Collection dataHolders, String tableName) {
		Item[] items = getItems(dataHolders, itemPool);
		Map itemMap = getItemMap(items);
		Map providerBytedataMap = new HashMap();
		try {
			for (Iterator it = itemMap.keySet().iterator(); it.hasNext();) {
				String provider = (String) it.next();
				List itemList = (List) itemMap.get(provider);
				// commandsはHashSetに置き換えができません。
				ArrayList commands = new ArrayList(items.length);
				for (Iterator it2 = itemList.iterator(); it2.hasNext();) {
					Item item = (Item) it2.next();
					WifeCommand wc = new WifeCommand(item);
					commands.add(wc);
				}
				Environment environment = EnvironmentMap.get(provider);
				Communicater communicater =
					communicaterFactory.createCommunicator(environment);
				communicater.addReadCommand(commands);
				SyncReadWrapper wrapper = new SyncReadWrapper();
				Map bytedataMap = wrapper.syncRead(communicater, commands);
				providerBytedataMap.put(provider, bytedataMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Item[] manageSortedItems = columnManager.sortLogging(items, tableName);

		DoubleList values = new ArrayDoubleList(items.length);
		ConvertValue[] convertValues =
			createConvertValue(dataHolders, tableName);
		for (int i = 0; i < manageSortedItems.length; i++) {
			Item item = manageSortedItems[i];
			Map bytedataMap = (Map) providerBytedataMap.get(item.getProvider());
			WifeCommand wc = new WifeCommand(item);
			byte[] data = (byte[]) bytedataMap.get(wc);
			// log.debug(item.getProvider() + " " + item.getHolder() + ": byte("
			// + WifeUtilities.toString(data) + ")" + " command : " + wc);
			WifeData wd = WifeDataUtil.getWifeData(item);
			try {
				if (data != null) {
					wd = wd.valueOf(data);
				} else {
					if (log.isDebugEnabled()) {
						log.debug(item.getProvider()
							+ " "
							+ item.getHolder()
							+ ": byte("
							+ WifeUtilities.toString(data)
							+ ")"
							+ " command : "
							+ wc);
					}
				}
				if (wd instanceof WifeDataAnalog) {
					WifeDataAnalog wda = (WifeDataAnalog) wd;
					values.add(convertValues[i].convertDoubleValue(wda
						.doubleValue()));
				} else if (wd instanceof WifeDataDigital) {
					WifeDataDigital wdd = (WifeDataDigital) wd;
					if (wdd.isOnOff(true)) {
						values.add(1);
					} else {
						values.add(0);
					}
				} else {
					throw new IllegalArgumentException(
						"value is not WifeDataDigital and WifeDataAnalog! : "
							+ wd.getClass().getName());
				}
			} catch (BCDConvertException e) {
				log.error("BCD変換エラー発生、初期値をログに書き込みます。"
					+ item.getProvider()
					+ "_"
					+ item.getHolder(), e);
				values.add(0);
				continue;
			}
		}

		return values;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.io.postgresql.ItemUtil#createDateHolderValuesMap
	 * (java.util.Collection, java.lang.String,
	 * org.F11.scada.server.entity.MultiRecordDefine)
	 */
	public Map createDateHolderValuesMap(
			Collection dataHolders,
			String tableName,
			MultiRecordDefine multiRecordDefine) {
		Map timeMap = new HashMap();
		byte[] srcBytes = null;
		int byteRecSize =
			multiRecordDefine.getWordLength()
				/ multiRecordDefine.getRecordCount()
				* 2;
		try {
			List commands = new ArrayList();
			WifeCommand wc =
				new WifeCommand(
					multiRecordDefine.getProvider(),
					0,
					0,
					multiRecordDefine.getComMemoryKinds(),
					multiRecordDefine.getComMemoryAddress(),
					multiRecordDefine.getWordLength());
			commands.add(wc);
			Environment environment =
				EnvironmentMap.get(multiRecordDefine.getProvider());
			Communicater communicater =
				communicaterFactory.createCommunicator(environment);
			communicater.addReadCommand(commands);
			Map bytedataMap = communicater.syncRead(commands, false);
			srcBytes = (byte[]) bytedataMap.get(wc);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Item[] items = getItems(dataHolders, itemPool);

		for (int recno = 0; recno < multiRecordDefine.getRecordCount(); recno++) {
			Timestamp timestamp = getTimestamp(srcBytes, recno * byteRecSize);
			if (timestamp == null) {
				log.warn("多レコード 日付の形式が違います。" + tableName + " recno=" + recno);
				continue;
			}

			Item[] manageSortedItems =
				columnManager.sortLogging(items, tableName);

			DoubleList values = new ArrayDoubleList(items.length);
			ConvertValue[] convertValues =
				createConvertValue(dataHolders, tableName);
			for (int i = 0; i < manageSortedItems.length; i++) {
				Item item = manageSortedItems[i];
				WifeData wd = WifeDataUtil.getWifeData(item);
				if (multiRecordDefine.getComMemoryKinds() != item
					.getComMemoryKinds()
					|| multiRecordDefine.getComMemoryAddress() > item
						.getComMemoryAddress()
					|| multiRecordDefine.getComMemoryAddress() + byteRecSize < item
						.getComMemoryAddress()
						+ wd.getWordSize()) {
					throw new IllegalArgumentException("多レコード データ定義がブロック範囲外です。"
						+ item.getProvider()
						+ " "
						+ item.getHolder());
				}
				int byteOffset =
					recno
						* byteRecSize
						+ (int) (item.getComMemoryAddress() - multiRecordDefine
							.getComMemoryAddress())
						* 2;
				byte[] data = new byte[wd.getWordSize() * 2];
				System.arraycopy(srcBytes, byteOffset, data, 0, wd
					.getWordSize() * 2);
				try {
					wd = wd.valueOf(data);
					if (wd instanceof WifeDataAnalog) {
						WifeDataAnalog wda = (WifeDataAnalog) wd;
						values.add(convertValues[i].convertDoubleValue(wda
							.doubleValue()));
					} else if (wd instanceof WifeDataDigital) {
						WifeDataDigital wdd = (WifeDataDigital) wd;
						if (wdd.isOnOff(true)) {
							values.add(1);
						} else {
							values.add(0);
						}
					} else {
						throw new IllegalArgumentException(
							"value is not WifeDataDigital and WifeDataAnalog! : "
								+ wd.getClass().getName());
					}
				} catch (BCDConvertException e) {
					log.error("BCD変換エラー発生、初期値をログに書き込みます", e);
					values.add(0);
					continue;
				}
			}
			timeMap.put(timestamp, values);
		}
		return timeMap;
	}

	private Timestamp getTimestamp(byte[] srcBytes, int pos) {
		if (srcBytes.length < pos + 8) {
			return null;
		}

		int year =
			Integer.parseInt(WifeUtilities.toString(srcBytes, pos + 0, 2));
		int month =
			Integer.parseInt(WifeUtilities.toString(srcBytes, pos + 2, 1)) - 1;
		int date =
			Integer.parseInt(WifeUtilities.toString(srcBytes, pos + 3, 1));
		int hour =
			Integer.parseInt(WifeUtilities.toString(srcBytes, pos + 5, 1));
		int minute =
			Integer.parseInt(WifeUtilities.toString(srcBytes, pos + 6, 1));
		int second =
			Integer.parseInt(WifeUtilities.toString(srcBytes, pos + 7, 1));
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month, date, hour, minute, second);
		if (cal.get(Calendar.YEAR) != year
			|| cal.get(Calendar.MONTH) != month
			|| cal.get(Calendar.DATE) != date
			|| cal.get(Calendar.HOUR_OF_DAY) != hour
			|| cal.get(Calendar.MINUTE) != minute
			|| cal.get(Calendar.SECOND) != second) {
			return null;
		}
		return new Timestamp(cal.getTimeInMillis());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.io.postgresql.ItemUtil#createConvertValueMap(java
	 * .util.Collection, java.lang.String)
	 */
	public Map createConvertValueMap(Collection holders, String tableTame) {
		HashMap map = new HashMap(holders.size());
		ConvertValue[] convertValues = createConvertValue(holders, tableTame);
		int idx = 0;
		for (Iterator i = holders.iterator(); i.hasNext(); idx++) {
			HolderString hs = (HolderString) i.next();
			map.put(hs, convertValues[idx]);
		}

		if (log.isDebugEnabled()) {
			log.debug(map);
		}

		return map;
	}

	private ConvertValue[] createConvertValue(Collection holders) {
		Item[] items = getItems(holders, itemPool);

		ConvertValue[] convertValues = new ConvertValue[holders.size()];
		for (int i = 0; i < items.length; i++) {
			convertValues[i] = RegisterUtil.getConvertValue(items[i]);
		}
		return convertValues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.F11.scada.server.io.postgresql.ItemUtil#createConvertValueMap(java
	 * .util.Collection)
	 */
	public Map createConvertValueMap(Collection holders) {
		HashMap map = new HashMap(holders.size());

		Comparator comparator = new Comparator() {
			public int compare(Object o1, Object o2) {
				HolderString hs1 = (HolderString) o1;
				HolderString hs2 = (HolderString) o2;

				int providerComp =
					hs1.getProvider().compareTo(hs2.getProvider());
				return providerComp != 0 ? providerComp : hs1
					.getHolder()
					.compareTo(hs2.getHolder());
			}
		};

		ConvertValue[] convertValues = createConvertValue(holders);
		SortedSet set = new TreeSet(comparator);
		set.addAll(holders);

		if (log.isDebugEnabled()) {
			log.debug(set);
		}

		int idx = 0;
		for (Iterator i = set.iterator(); i.hasNext(); idx++) {
			HolderString hs = (HolderString) i.next();
			map.put(hs, convertValues[idx]);
		}

		if (log.isDebugEnabled()) {
			log.debug(map);
		}

		return map;
	}
}
