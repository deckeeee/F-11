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

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProvider;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.applet.ServerErrorUtil;
import org.F11.scada.data.ConvertValue;
import org.F11.scada.server.io.BarGraph2ValueListHandler;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.util.ThreadUtil;
import org.F11.scada.xwife.server.WifeDataProvider;
import org.apache.commons.collections.primitives.DoubleList;
import org.apache.log4j.Logger;

/**
 * BarGraphModel�̊��N���X�ł�
 */
public abstract class AbstractBarGraphModel implements BarGraphModel, Runnable {
	/** ���M���OAPI */
	private static Logger logger = Logger.getLogger(AbstractBarGraphModel.class);
	private static final PointProperty nullPoint = new PointProperty();
	/** �����f�[�^�X�V���� */
	private static final long SLEEP_TIME = 30000L;
	/** �X���b�h�I�u�W�F�N�g */
	private Thread thread;

	/** �o�[�O���t�̃x�[�X�ƂȂ�R���|�[�l���g */
	private JLabel label = new JLabel();
	/** toString()�̕Ԃ�l */
	private String labelString;
	/** �f�[�^�n���h���}�l�[�W���[ */
	private BarGraph2ValueListHandler valueListHandler;

	/** ���g�̃C���f�b�N�X */
	private int barGraphModelIndex;
	/** ���X�P�[���P�P�ʒ��̃o�[�̖{�� */
	private int barCount = 1;
	/** �Q�Ƃ���e�[�u�����̔z�� */
	private String handlerName;
	/** �o�[��`�悷��R���|�[�l���g */
	protected BarGraph2View view;
	/** �c�X�P�[�����Ǘ�����N���X */
	private VerticallyScaleModel vertical;
	/** �P�ʕ`��V���{���̃��X�g */
	private List<TextUnitsSymbol> unitsList = new ArrayList<TextUnitsSymbol>();
	/** �Q�ƒ��̃|�C���g��� */
	protected PointProperty refPoint;
	/** �f�[�^�擪���� */
	protected Date firstLoggingDate = new Date();
	/** �f�[�^�������� */
	private Date lastLoggingDate = new Date();
	/** �O���t�f�[�^�}�g���b�N�X */
	private BarData[][] dataMatrix;

	protected AbstractBarGraphModel() throws RemoteException {
		Exception serverError = null;
		for (int i = 0; i < Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				lookup();
				serverError = null;
				break;
			} catch (Exception e) {
				serverError = e;
				ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
				continue;
			}
		}
		if (serverError != null) {
			throw ServerErrorUtil.createException(serverError);
		}
	}

	/**
	 * ���M���O�f�[�^�ǂݍ���
	 */
	abstract protected BarData[][] loadLoggingData() throws RemoteException;
	/**
	 * ���ݒl��ݒ�
	 */
	abstract protected void setNowValue(BarData[][] dataMatrix);
	/**
	 * �ŐV�`�惂�[�h���擾
	 */
	abstract protected boolean isRealTimeMode();

	/**
	 * �f�[�^��ǂݍ����view�ɒʒm����B
	 */
	protected synchronized void fireViewChangeData() throws RemoteException {
		dataMatrix = loadLoggingData();
		setNowValue(dataMatrix);
		view.changeData(dataMatrix);
	}

	/**
	 * �o�[�O���t�R���|�[�l���g��Ԃ��܂��B
	 * @return
	 */
	public JComponent getJComponent() {
		return label;
	}

	/**
	 * �\���|�C���g��ύX���܂��B
	 * @param series
	 */
	public synchronized void changePoint(BarSeries series)
			throws RemoteException {
		// �Q�ƒ��̃|�C���g�̐ݒ�
		refPoint = series.getPointProperty(barGraphModelIndex);

		// �z���_����
		DataProvider provider = Manager.getInstance().getDataProvider(
				refPoint.getProviderName());
		DataHolder holder = provider.getDataHolder(refPoint.getHolderName());
		if (holder == null) {
			logger.error("holder is null [" + refPoint.getHolderName() + "]");
			// �X�P�[���N���A
			vertical.changePoint(nullPoint, "0.0");
			// view�N���A
			view.changeData(null);
			// �P�ʃN���A
			for (TextUnitsSymbol symbol : unitsList) {
				symbol.setUnits("");
				symbol.updateProperty();
			}
			return;
		}
		ConvertValue converter = (ConvertValue) holder.getParameter(WifeDataProvider.PARA_NAME_CONVERT);

		// �X�P�[���ݒ�
		vertical.changePoint(refPoint, converter.getPattern());

		// view�֐ݒ�
		view.changePattern(converter.getPattern());
		fireViewChangeData();

		// �P��
		for (TextUnitsSymbol symbol : unitsList) {
			symbol.setUnits(series.getUnit_mark());
			symbol.updateProperty();
		}
	}
	/**
	 * ���X�P�[���P�P�ʒ��̃o�[�̖{����Ԃ��܂��B
	 * @return
	 */
	public int getBarCount() {
		return barCount;
	}
	/**
	 * �\���f�[�^�̃}�g���b�N�X��Ԃ��܂��B
	 * @return
	 */
	public BarData[][] getDataMatrix() {
		return dataMatrix;
	}

	/**
	 * ���g�̃C���f�b�N�X��ݒ肵�܂��B
	 * @param barIndex
	 */
	public void setBarGraphModelIndex(int barIndex) {
		barGraphModelIndex = barIndex;
	}
	/**
	 * toString()�̕Ԃ�l��ݒ肵�܂��B
	 * @param text
	 */
	public void setText(String text) {
		this.labelString = text;
	}
	/**
	 * �R���|�[�l���g�̃o�b�N�O���E���h�C���[�W��ݒ肵�܂��B
	 * @param icon
	 */
	public void setIcon(Icon icon) {
		this.label.setIcon(icon);
	}
	/**
	 * �Q�Ƃ���e�[�u������ݒ肵�܂��B
	 * @param handlerNames
	 */
	public void setHandlerNames(String[] handlerNames) {
		this.handlerName = handlerNames[0];
	}
	/**
	 * ���X�P�[���P�P�ʒ��̃o�[�̖{����ݒ肵�܂��B
	 * @param count
	 */
	public void setBarCount(int count) {
		this.barCount = count;
	}
	/**
	 * �o�[��`�悷��R���|�[�l���g��ݒ肵�܂��B
	 * @param view
	 */
	public void setBarGraph2View(BarGraph2View view) {
		this.view = view;
		label.add(view);
		// �ǂ�������ł����X�i�[�o�^
		if (this.view != null && this.vertical != null)
			this.vertical.addScaleChangeListener(this.view);
	}
	/**
	 * �c�X�P�[�����Ǘ�����N���X��ǉ����܂��B
	 * @param symbol
	 */
	public void setVerticallyScaleModel(VerticallyScaleModel vertical) {
		this.vertical = vertical;
		this.vertical.setParent(label);
		// �ǂ�������ł����X�i�[�o�^
		if (this.view != null && this.vertical != null)
			this.vertical.addScaleChangeListener(this.view);
	}
	/**
	 * �R���|�[�l���g�ɒP�ʕ\���p�e�L�X�g�V���{����ǉ����܂��B
	 * @param symbol
	 */
	public void addUnitsSymbol(TextUnitsSymbol symbol) {
		unitsList.add(symbol);
		label.add(symbol);
	}

	/**
	 * 
	 */
	public String toString() {
		return labelString;
	}

	protected SortedMap<Timestamp, DoubleList> loadRmi(
			List<HolderString> holderStrings, Date first, Date last)
			throws RemoteException {
		SortedMap<Timestamp, DoubleList> result = new TreeMap<Timestamp, DoubleList>();
		Exception serverError = null;
		for (int i = 0; i < Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				result = valueListHandler.getLoggingData(handlerName,
						holderStrings, first, last);
				firstLoggingDate = valueListHandler.getFirstDateTime(
						handlerName, holderStrings);
				lastLoggingDate = valueListHandler.getLastDateTime(handlerName,
						holderStrings);
				serverError = null;
				break;
			} catch (Exception e) {
				try {
					ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				serverError = e;
				continue;
			}
		}
		if (serverError != null) {
			throw ServerErrorUtil.createException(serverError);
		}
		return result;
	}

	protected Date loadRmiLastDateTime(List<HolderString> holderStrings)
			throws RemoteException {
		Date result = new Date(0);
		Exception serverError = null;
		for (int i = 0; i < Globals.RMI_CONNECTION_RETRY_COUNT; i++) {
			try {
				result = valueListHandler.getLastDateTime(handlerName,
						holderStrings);
				serverError = null;
				break;
			} catch (Exception e) {
				try {
					ThreadUtil.sleep(Globals.RMI_CONNECTION_RETRY_WAIT_TIME);
					lookup();
				} catch (Exception e1) {
					serverError = e1;
				}
				serverError = e;
				continue;
			}
		}
		if (serverError != null) {
			throw ServerErrorUtil.createException(serverError);
		}
		return result;
	}

	private void lookup() throws Exception {
		logger.info("lookup ValueListHandler:"
				+ WifeUtilities.createRmiBarGraph2DataValueListHandlerManager());
		valueListHandler = (BarGraph2ValueListHandler) Naming.lookup(WifeUtilities.createRmiBarGraph2DataValueListHandlerManager());
	}

	public void run() {
		Thread ct = Thread.currentThread();

		while (ct == thread) {
			try {
				synchronized (this) {
					if (isRealTimeMode()) {
						List<HolderString> holderStrings = new ArrayList<HolderString>();
						holderStrings.add(new HolderString(
								refPoint.getProviderName(),
								refPoint.getHolderName()));
						Date lastTime = loadRmiLastDateTime(holderStrings);
						if (logger.isDebugEnabled()) {
							SimpleDateFormat sf = new SimpleDateFormat(
									"yyyy/MM/dd HH:mm");
							logger.debug("check "
									+ sf.format(lastLoggingDate.getTime())
									+ " < " + sf.format(lastTime.getTime()));
						}
						if (lastLoggingDate.getTime() < lastTime.getTime()) {
							changePeriod(0);
						} else {
							setNowValue(dataMatrix);
							view.changeData(dataMatrix);
						}
					}
				}
			} catch (RemoteException e) {
			}
			ThreadUtil.sleep(SLEEP_TIME);
		}
	}
	public synchronized void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.setName(getClass().getSimpleName());
			thread.start();
		}
	}

	public synchronized void stop() {
		if (thread != null) {
			Thread ot = thread;
			thread = null;
			ot.interrupt();
		}
	}

}
