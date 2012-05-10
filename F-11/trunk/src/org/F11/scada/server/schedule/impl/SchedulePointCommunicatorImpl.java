/*
 * =============================================================================
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

package org.F11.scada.server.schedule.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.Globals;
import org.F11.scada.data.BCDConvertException;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeDataSchedule;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.exception.RemoteRuntimeException;
import org.F11.scada.server.communicater.Communicater;
import org.F11.scada.server.communicater.CommunicaterFactory;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.communicater.EnvironmentMap;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.frame.FrameDefineManager;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.io.postgresql.SyncReadWrapper;
import org.F11.scada.server.operationlog.OperationLoggingService;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.server.register.WifeDataUtil;
import org.F11.scada.server.schedule.SchedulePointCommunicator;
import org.F11.scada.server.schedule.SchedulePointException;
import org.F11.scada.server.schedule.point.dto.DuplicateSeparateScheduleDto;
import org.F11.scada.server.schedule.point.dto.ScheduleGroupDto;
import org.F11.scada.server.schedule.point.dto.SchedulePointRowDto;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.log4j.Logger;

public class SchedulePointCommunicatorImpl implements SchedulePointCommunicator {
	private final Logger logger = Logger
		.getLogger(SchedulePointCommunicatorImpl.class);
	private ItemUtil itemUtil;
	private Map itemPool = new ConcurrentHashMap();
	private CommunicaterFactory communicaterFactory;
	private FrameDefineManager defineManager;
	/** データ更新ログのサービスオブジェクト */
	private OperationLoggingService service;

	public void setItemUtil(ItemUtil itemUtil) {
		this.itemUtil = itemUtil;
	}

	public void setCommunicaterFactory(CommunicaterFactory communicaterFactory) {
		this.communicaterFactory = communicaterFactory;
	}

	public void setDefineManager(FrameDefineManager defineManager) {
		this.defineManager = defineManager;
	}

	public void setService(OperationLoggingService service) {
		this.service = service;
	}

	public void setHolder(SchedulePointRowDto dto)
			throws SchedulePointException {
		defineManager.addDataHolders(getHolderSet(dto));
		Manager manager = Manager.getInstance();
		DataHolder dh =
			manager.findDataHolder(
				dto.getGroupNoProvider(),
				dto.getGroupNoHolder());
		if (null == dh) {
			String errMsg =
				"スケジュールNo.のデータホルダが見つかりません。"
					+ dto.getGroupNoProvider()
					+ " "
					+ dto.getGroupNoHolder();
			SchedulePointException e = new SchedulePointException(errMsg);
			logger.error(errMsg, e);
			throw e;
		}
		ConvertValue convertValue =
			(ConvertValue) dh.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
		double value =
			convertValue.convertInputValue(dto.getGroupNo().intValue());
		WifeDataAnalog data = (WifeDataAnalog) dh.getValue();
		WifeDataAnalog newData = (WifeDataAnalog) data.valueOf(value);
		try {
			service.logging(
				dh,
				newData.valueOf(value),
				dto.getUser(),
				dto.getIpAddress(),
				dto.getTimestamp());
			dh.setValue(
				newData.valueOf(value),
				new Date(),
				WifeQualityFlag.GOOD);
			dh.syncWrite();
		} catch (DataProviderDoesNotSupportException e) {
			logger.error("データホルダ書き込みエラー", e);
			throw new SchedulePointException("データホルダ書き込みエラー", e);
		} catch (RemoteException e) {
			logger.error("操作ログ書き込み時にエラー発生", e);
		} finally {
			defineManager.unregisterJim(getHolderSet(dto), false);
		}
	}

	private Set getHolderSet(SchedulePointRowDto dto) {
		HashSet set = new HashSet();
		set.add(new HolderString(dto.getGroupNoProvider(), dto
			.getGroupNoHolder()));
		return set;
	}

	private boolean isNetError(String provider) {
		DataHolder errHolder =
			Manager.getInstance().findDataHolder(provider, Globals.ERR_HOLDER);
		WifeDataDigital wd = WifeDataDigital.valueOfTrue(0);
		return wd.equals(errHolder.getValue());
	}

	public List getHolderData(List dto) {
		List holderStrings = convertHolderData(dto);
		Item[] items = itemUtil.getItems(holderStrings, itemPool);
		Map itemMap = itemUtil.getItemMap(items);
		ArrayList schePointRows = new ArrayList(dto.size());
		try {
			for (Iterator it = itemMap.keySet().iterator(); it.hasNext();) {
				String provider = (String) it.next();
				ArrayList commands = new ArrayList(items.length);
				List itemList = (List) itemMap.get(provider);
				for (Iterator it2 = itemList.iterator(); it2.hasNext();) {
					Item item = (Item) it2.next();
					WifeCommand wc = new WifeCommand(item);
					commands.add(wc);
				}
				Communicater communicater = getCommunicater(provider);
				communicater.addReadCommand(commands);
				SyncReadWrapper wrapper = new SyncReadWrapper();
				Map bytedataMap =
					wrapper.syncRead(
						communicater,
						commands,
						isNetError(provider));

				for (Iterator itemIt = itemList.iterator(), commandIt =
					commands.iterator(); itemIt.hasNext()
					&& commandIt.hasNext();) {
					Item item = (Item) itemIt.next();
					WifeData wd = WifeDataUtil.getWifeData(item);
					WifeCommand wc = (WifeCommand) commandIt.next();
					byte[] data = (byte[]) bytedataMap.get(wc);
					try {
						wd = wd.valueOf(data);
						if (wd instanceof WifeDataAnalog) {
							WifeDataAnalog wda = (WifeDataAnalog) wd;
							setSchedulePointRows(
								schePointRows,
								item,
								new Integer((int) wda.doubleValue()));
						} else {
							getException(wd);
						}
					} catch (BCDConvertException e) {
						logger.error("BCD変換エラー発生、0をグループNo.に書き込みます", e);
						if (wd instanceof WifeDataAnalog) {
							setSchedulePointRows(
								schePointRows,
								item,
								new Integer(0));
						} else {
							getException(wd);
						}
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("スケジュールデータ読み込み時エラー", e);
		}
		return schePointRows;
	}

	private void getException(WifeData wd) {
		throw new IllegalArgumentException(
			"value is not WifeDataDigital and WifeDataAnalog! : "
				+ wd.getClass().getName());
	}

	private void setSchedulePointRows(ArrayList schePointRows,
			Item item,
			Integer groupNo) {
		SchedulePointRowDto schePointRow = new SchedulePointRowDto();
		schePointRow.setGroupNoProvider(item.getProvider());
		schePointRow.setGroupNoHolder(item.getHolder());
		schePointRow.setGroupNo(groupNo);
		schePointRows.add(schePointRow);
	}

	private Communicater getCommunicater(String provider) throws Exception {
		Environment environment = EnvironmentMap.get(provider);
		Communicater communicater =
			communicaterFactory.createCommunicator(environment);
		return communicater;
	}

	private List convertHolderData(List dto) {
		ArrayList holderStrings = new ArrayList(dto.size());
		for (Iterator i = dto.iterator(); i.hasNext();) {
			SchedulePointRowDto row = (SchedulePointRowDto) i.next();
			holderStrings.add(new HolderString(row.getGroupNoProvider(), row
				.getGroupNoHolder()));
		}
		return holderStrings;
	}

	/**
	 * @deprecated
	 */
	public void duplicateSeparateSchedule(ScheduleGroupDto src,
			SchedulePointRowDto[] dest) {
		Set set = getHolderSet(dest);
		try {
			// 個別スケジュールをレジスト
			defineManager.addDataHolders(set);
			// マスタ→個別コピー
			masterToSeparate(src, set);
		} finally {
			// 個別スケジュールをアンレジスト
			defineManager.unregisterJim(set, false);
		}
	}

	/**
	 * @deprecated
	 */
	private void masterToSeparate(ScheduleGroupDto src, Set set) {
		Manager manager = Manager.getInstance();
		DataHolder master =
			manager.findDataHolder(src.getProvider(), src.getHolder());
		if (null != master) {
			WifeDataSchedule masterSche = (WifeDataSchedule) master.getValue();
			for (Iterator i = set.iterator(); i.hasNext();) {
				HolderString hs = (HolderString) i.next();
				DataHolder dh =
					manager.findDataHolder(hs.getProvider(), hs.getHolder());
				if (null != dh) {
					WifeDataSchedule sche = (WifeDataSchedule) dh.getValue();
					sche = sche.duplicateTodayAndTomorrow(masterSche);
					logger.info(sche);
					dh.setValue(sche, new Date(), WifeQualityFlag.GOOD);
					try {
						dh.syncWrite();
					} catch (DataProviderDoesNotSupportException e) {
						e.printStackTrace();
					}
				} else {
					logger.error("コピー先のホルダが存在しません : " + hs);
				}
			}
		} else {
			logger.error("コピー元のホルダが存在しません : "
				+ src.getProvider()
				+ "_"
				+ src.getHolder());
		}
	}

	public void duplicateSeparateSchedule(DuplicateSeparateScheduleDto dto) {
		Set set = getHolderSet(dto.getDest());
		try {
			// 個別スケジュールをレジスト
			defineManager.addDataHolders(set);
			// マスタ→個別コピー
			masterToSeparate(dto, set);
		} finally {
			// 個別スケジュールをアンレジスト
			defineManager.unregisterJim(set, false);
		}
	}

	private void masterToSeparate(DuplicateSeparateScheduleDto dto, Set set) {
		Manager manager = Manager.getInstance();
		ScheduleGroupDto src = dto.getSrc();
		DataHolder master =
			manager.findDataHolder(src.getProvider(), src.getHolder());
		if (null != master) {
			WifeDataSchedule masterSche = (WifeDataSchedule) master.getValue();
			try {
				for (Iterator i = set.iterator(); i.hasNext();) {
					HolderString hs = (HolderString) i.next();
					DataHolder dh =
						manager
							.findDataHolder(hs.getProvider(), hs.getHolder());
					logger.info(dh.getValue());
					if (null != dh) {
						WifeDataSchedule sche =
							(WifeDataSchedule) dh.getValue();
						sche = sche.duplicateTodayAndTomorrow(masterSche);
						service.logging(
							dh,
							sche,
							dto.getUser(),
							dto.getIpAddress(),
							dto.getTimestamp());
						dh.setValue(sche, new Date(), WifeQualityFlag.GOOD);
						try {
							dh.syncWrite();
						} catch (DataProviderDoesNotSupportException e) {
							e.printStackTrace();
						}
					} else {
						logger.error("コピー先のホルダが存在しません : " + hs);
					}
				}
			} catch (RemoteException e) {
				logger.error("操作ロギングでエラーが発生しました", e);
			}
		} else {
			logger.error("コピー元のホルダが存在しません : "
				+ src.getProvider()
				+ "_"
				+ src.getHolder());
		}
	}

	private Set getHolderSet(SchedulePointRowDto[] dest) {
		HashSet set = new HashSet(dest.length);
		for (int i = 0; i < dest.length; i++) {
			SchedulePointRowDto dto = dest[i];
			set.add(new HolderString(dto.getSeparateProvider(), dto
				.getSeparateHolder()));
		}
		return set;
	}

	public WifeDataSchedule getSeparateSchedule(SchedulePointRowDto rowdto) {
		List holderStrings = getHolderData(rowdto);
		Item[] items = itemUtil.getItems(holderStrings, itemPool);
		Map itemMap = itemUtil.getItemMap(items);
		try {
			for (Iterator it = itemMap.keySet().iterator(); it.hasNext();) {
				String provider = (String) it.next();
				ArrayList commands = new ArrayList(items.length);
				List itemList = (List) itemMap.get(provider);
				for (Iterator it2 = itemList.iterator(); it2.hasNext();) {
					Item item = (Item) it2.next();
					WifeCommand wc = new WifeCommand(item);
					commands.add(wc);
				}
				Communicater communicater = getCommunicater(provider);
				communicater.addReadCommand(commands);
				SyncReadWrapper wrapper = new SyncReadWrapper();
				Map bytedataMap =
					wrapper.syncRead(
						communicater,
						commands,
						isNetError(provider));

				for (Iterator itemIt = itemList.iterator(), commandIt =
					commands.iterator(); itemIt.hasNext()
					&& commandIt.hasNext();) {
					Item item = (Item) itemIt.next();
					WifeData wd = WifeDataUtil.getWifeData(item);
					WifeCommand wc = (WifeCommand) commandIt.next();
					byte[] data = (byte[]) bytedataMap.get(wc);
					wd = wd.valueOf(data);
					if (wd instanceof WifeDataSchedule) {
						return (WifeDataSchedule) wd;
					} else {
						getException(wd);
					}
				}
			}
		} catch (Exception e) {
			logger.error("スケジュールデータ読み込み時エラー", e);
		}
		throw new RemoteRuntimeException("スケジュールデータ読み込み時エラー");
	}

	private List getHolderData(SchedulePointRowDto rowdto) {
		ArrayList l = new ArrayList();
		l.add(new HolderString(rowdto.getSeparateProvider(), rowdto
			.getSeparateHolder()));
		return l;
	}

	public void updateSeperateSchedule(SchedulePointRowDto dto,
			Date date,
			WifeDataSchedule data) {
		defineManager.addDataHolders(getScheduleHolderSet(dto));
		Manager manager = Manager.getInstance();
		DataHolder dh =
			manager.findDataHolder(
				dto.getSeparateProvider(),
				dto.getSeparateHolder());
		if (null == dh) {
			String errMsg =
				"スケジュールのデータホルダが見つかりません。"
					+ dto.getSeparateProvider()
					+ " "
					+ dto.getSeparateHolder();
			SchedulePointException e = new SchedulePointException(errMsg);
			logger.error(errMsg, e);
			throw e;
		}
		try {
			service.logging(
				dh,
				data,
				dto.getUser(),
				dto.getIpAddress(),
				dto.getTimestamp());
			dh.setValue(data, date, WifeQualityFlag.GOOD);
			dh.syncWrite();
		} catch (DataProviderDoesNotSupportException e) {
			logger.error("データホルダ書き込みエラー", e);
			throw new SchedulePointException("データホルダ書き込みエラー", e);
		} catch (RemoteException e) {
			logger.error("操作ログ書き込み時にエラー発生", e);
		} finally {
			defineManager.unregisterJim(getScheduleHolderSet(dto), false);
		}
	}

	private Set getScheduleHolderSet(SchedulePointRowDto dto) {
		return new HashSet(getHolderData(dto));
	}
}
