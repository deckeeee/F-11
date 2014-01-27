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
package org.F11.scada.applet.graph.bargraph2;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.SortedMap;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.data.ConvertValue;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;

public class GeppoBarGraphModel extends AbstractBarGraphModel {
	/** ���M���OAPI */
	private static Logger logger = Logger.getLogger(GeppoBarGraphModel.class);

	/** �f�[�^�̎Q�Ɠ��𑀍삷��N���X */
	protected ReferenceDate refDate;
	/** �Q�Ɠ��� */
	protected Calendar refCal;
	/** �ŐV�`�惂�[�h */
	private boolean realTimeMode = true;

	public GeppoBarGraphModel() throws RemoteException {
		super();
	}

	/**
	 * ���M���O�f�[�^�ǂݍ���
	 * @param pattern
	 * @throws RemoteException
	 */
	protected BarData[][] loadLoggingData() throws RemoteException {
		List<HolderString> holderStrings = new ArrayList<HolderString>();
		holderStrings.add(new HolderString(refPoint.getProviderName(),
				refPoint.getHolderName()));
		// �Q�Ɠ��̗���0:00�܂�
		Calendar last = new GregorianCalendar();
		last.setTime(refCal.getTime());
		last.add(Calendar.MONTH, 1);
		// barCount���O�̓���0:00����
		Calendar first = new GregorianCalendar();
		first.setTime(last.getTime());
		first.add(Calendar.MONTH, -getBarCount());
		SortedMap<Timestamp, DoubleList> source = loadRmi(holderStrings,
				first.getTime(), last.getTime());
		if (logger.isDebugEnabled()) {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			logger.debug("load first=" + sf.format(first.getTime()) + " last="
					+ sf.format(last.getTime()) + " count=" + source.size());
		}
		return rebuildData(first.getTime(), source);
	}

	/*
	 * ���[�h�f�[�^����BarData�̃}�g���b�N�X�ւ̕ϊ�
	 */
	private BarData[][] rebuildData(Date startDate,
			SortedMap<Timestamp, DoubleList> source) {
		BarData[][] result = new BarData[getBlockCount()][getBarCount()];
		Calendar cal = new GregorianCalendar();
		for (int bar = 0; bar < getBarCount(); bar++) {
			cal.setTimeInMillis(startDate.getTime());
			cal.add(Calendar.MONTH, bar);
			int moon = cal.get(Calendar.MONTH);
			for (int block = 0; block < getBlockCount(); block++) {
				result[block][bar] = null;
				DoubleList dlist = searchDay(source, cal);
				if (dlist != null) {
					Calendar key = new GregorianCalendar();
					key.setTime(cal.getTime());
					key.set(Calendar.HOUR_OF_DAY, 0);
					key.set(Calendar.MINUTE, 0);
					key.set(Calendar.SECOND, 0);
					key.set(Calendar.MILLISECOND, 0);
					key.add(Calendar.DAY_OF_MONTH, -1); // �l�͑O����
					if (key.get(Calendar.MONTH) == moon) {
						BarData data = new BarData();
						data.setDate(key.getTime());
						data.setValues(dlist);
						result[block][bar] = data;
					}
				}
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		return result;
	}

	/*
	 * ���[�h�f�[�^����w������`�P���ȓ��̃f�[�^������
	 */
	private DoubleList searchDay(SortedMap<Timestamp, DoubleList> source,
			Calendar cal) {
		Calendar cal2 = new GregorianCalendar();
		cal2.setTimeInMillis(cal.getTimeInMillis());
		cal2.add(Calendar.DAY_OF_MONTH, 1);
		SortedMap<Timestamp, DoubleList> sub = source.subMap(new Timestamp(
				cal.getTimeInMillis()), new Timestamp(cal2.getTimeInMillis()));
		if (0 < sub.size()) {
			return sub.get(sub.firstKey());
		}
		return null;
	}

	/**
	 * ���ݒl��ݒ�
	 */
	protected void setNowValue(BarData[][] dataMatrix) {
		// ���ݒl�̃z���_����
		if (refPoint.getNowValueProviderName().length() <= 0
				|| refPoint.getNowValueHolderName().length() <= 0)
			return;
		DataProvider provider = Manager.getInstance().getDataProvider(
				refPoint.getNowValueProviderName());
		DataHolder holder = provider.getDataHolder(refPoint.getNowValueHolderName());
		if (holder == null)
			return;
		ConvertValue converter = (ConvertValue) holder.getParameter(WifeDataProvider.PARA_NAME_CONVERT);
		Object o = holder.getValue();
		if (!(o instanceof WifeDataAnalog))
			return;
		WifeDataAnalog wa = (WifeDataAnalog) o;
		double value = converter.convertDoubleValue(wa.doubleValue());

		Calendar today = new GregorianCalendar();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 10);
		int block = today.get(Calendar.DAY_OF_MONTH) - 1;
		// �Q�Ɠ��O��0:00����
		Calendar start = new GregorianCalendar();
		start.setTime(refCal.getTime());
		start.add(Calendar.DAY_OF_MONTH, -1);
		// ��������0:00�܂�
		Calendar end = new GregorianCalendar();
		end.setTime(start.getTime());
		end.add(Calendar.MONTH, 1);
		if (start.compareTo(today) <= 0 && today.before(end)) {
			if (logger.isDebugEnabled()) {
				SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
				logger.debug("now value " + sf.format(today.getTime()) + ","
						+ value);
			}
			int bar = getBarCount() - 1;
			BarData data = dataMatrix[block][bar];
			if (data == null) {
				data = new BarData();
				data.setDate(today.getTime()); // ���ݒl�͓�����
				dataMatrix[block][bar] = data;
			}
			today.setTime(data.getDate());
			if (today.get(Calendar.MILLISECOND) == 10) {
				DoubleList dlist = new ArrayDoubleList();
				dlist.add(value);
				data.setValues(dlist);
			}
		}
	}

	/**
	 * �ŐV�`�惂�[�h���擾
	 */
	protected boolean isRealTimeMode() {
		return realTimeMode;
	}

	/**
	 * ���X�P�[���̐���Ԃ��܂��B
	 * @return
	 */
	public int getBlockCount() {
		return 31;
	}

	/**
	 * �f�[�^�̎Q�Ɠ��𑀍삷��N���X��ݒ肵�܂��B
	 * @param refDate
	 */
	public synchronized void setReferenceDate(ReferenceDate refDate) {
		// �����l�͓���2��0:00
		refCal = new GregorianCalendar();
		refCal.set(Calendar.DAY_OF_MONTH, 2);
		refCal.set(Calendar.HOUR_OF_DAY, 0);
		refCal.set(Calendar.MINUTE, 0);
		refCal.set(Calendar.SECOND, 0);
		refCal.set(Calendar.MILLISECOND, 0);
		refDate.setDate(refCal.getTime());
		this.refDate = refDate;
		this.realTimeMode = true;
		logger.debug("�ŐV�\�����[�h");
	}

	/**
	 * �\���f�[�^�̎Q�ƈʒu��ύX���܂��B
	 */
	public synchronized void changePeriod(int offset) {
		refCal.setTime(refDate.getDate());
		refCal.add(Calendar.MONTH, offset);
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, 1); // �f�[�^�����ɍ��킹��
		if (refCal.after(cal) || offset == 0) {
			refCal.setTime(cal.getTime());
			refCal.add(Calendar.DAY_OF_MONTH, -1); // �����̑Ή�
		}
		cal.setTime(firstLoggingDate);
		cal.add(Calendar.MONTH, -1);
		if (refCal.before(cal)) { // �擪�f�[�^�͖{���͈͂�
			refCal.setTime(firstLoggingDate);
			refCal.add(Calendar.DAY_OF_MONTH, -1); // �����̑Ή�
		}
		refCal.set(Calendar.DAY_OF_MONTH, 2);
		refCal.set(Calendar.HOUR_OF_DAY, 0);
		refCal.set(Calendar.MINUTE, 0);
		refCal.set(Calendar.SECOND, 0);
		refCal.set(Calendar.MILLISECOND, 0);

		cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, 1); // �f�[�^�����ɍ��킹��
		cal.add(Calendar.MONTH, -1);
		if (refCal.after(cal)) {
			realTimeMode = true;
			logger.debug("�ŐV�\�����[�h");
		} else {
			realTimeMode = false;
			logger.debug("�ߋ��Q�ƃ��[�h");
		}
		try {
			fireViewChangeData();
			refDate.setDate(refCal.getTime());
		} catch (RemoteException e) {
			logger.warn("logg load error!", e);
		}
	}

}
