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

package org.F11.scada.server.alarm.mail.send;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.DataProviderDoesNotSupportException;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Service;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.data.WifeQualityFlag;
import org.F11.scada.server.alarm.DataValueChangeEventKey;
import org.F11.scada.server.alarm.mail.dao.AlarmEmailSentService;
import org.apache.log4j.Logger;

/**
 * 警報メール送信サービスです。メール送信に成功すれば、送信履歴に書き出します。
 * 
 * @author maekawa
 * 
 */
public class TransportServiceImpl implements TransportService, Runnable,
		Service {
	private static Logger log = Logger.getLogger(TransportServiceImpl.class);
	private final BlockingQueue queue;
	private final TransportCore core;
	private final AlarmEmailSentService sentService;
	private Thread thread;

	private int retryCount = 5;
	private long waitTime = 1000L;

	public TransportServiceImpl(
			BlockingQueue queue,
			TransportCore core,
			AlarmEmailSentService sentService) {
		this.queue = queue;
		this.core = core;
		this.sentService = sentService;
		start();
		log.debug("メール送信サービス起動");
	}

	/**
	 * メール送信エラー時の最大リトライ回数です。
	 * 
	 * @param retryCount 最大リトライ回数
	 */
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
		log.info("メール送信エラー時の最大リトライ回数 : " + retryCount);
	}

	/**
	 * メール送信エラー時のリトライ待機時間です。
	 * 
	 * @param waitTime リトライ待機時間
	 */
	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
		log.info("メール送信エラー時のリトライ待機時間 : " + waitTime + "(ms)");
	}

	public void send(
			MimeMessage msg,
			Collection addresses,
			DataValueChangeEventKey key) {
		queue.offer(new Message(msg, addresses, key));
		log.debug("メッセージ投入");
	}

	public void run() {
		Thread currentThread = Thread.currentThread();

		log.debug("メール送信スレッド開始");
		while (thread == currentThread) {
			try {
				Message msg = (Message) queue.take();
				MessagingException ex = null;
				for (int i = 0; i < retryCount; i++) {
					try {
						log.info("メール送信開始");
						core.sendMessage(msg.getMessage(), getAddresses(msg
							.getAddresses()));
						if (isAlarmEmailSent()) {
							log.info("メール送信履歴開始");
							sentService.setAlarmEmailSent(msg.getKey(), msg
								.getAddresses());
						}
						log.info("メール送信成功");
						ex = null;
						if (isMailSendError()) {
							setMailSendError(false);
						}
						break;
					} catch (MessagingException e) {
						ex = e;
						log
							.info("メール送信処理 (メールサーバーエラー) : リトライ("
								+ (i + 1)
								+ ")");
						Thread.sleep(waitTime);
						continue;
					}
				}
				if (null != ex) {
					log.error("メール送信処理 (メールサーバーエラー) 最大リトライ回数を超えました。", ex);
					if (!isMailSendError()) {
						setMailSendError(true);
					}
				}
			} catch (InterruptedException e) {
			}
		}
	}

	private boolean isMailSendError() {
		String mailErrorHolder =
			EnvironmentManager.get("/server/mail/errorholder", "");
		if (!"".equals(mailErrorHolder)) {
			DataHolder dh =
				Manager.getInstance().findDataHolder(mailErrorHolder);
			if (dh != null) {
				try {
					WifeDataDigital digital = (WifeDataDigital) dh.getValue();
					return digital.isOnOff(true);
				} catch (ClassCastException e) {
					log.error("ライフチェックにはデジタルタイプを指定してください ("
						+ dh.getDataHolderName()
						+ ")", e);
				}
			}
		}
		return false;
	}

	private void setMailSendError(boolean b) {
		String mailErrorHolder =
			EnvironmentManager.get("/server/mail/errorholder", "");
		if (!"".equals(mailErrorHolder)) {
			DataHolder dh =
				Manager.getInstance().findDataHolder(mailErrorHolder);
			if (dh != null) {
				try {
					WifeDataDigital digital = (WifeDataDigital) dh.getValue();
					dh.setValue(
						digital.valueOf(b),
						new Date(),
						WifeQualityFlag.GOOD);
					dh.syncWrite();
				} catch (DataProviderDoesNotSupportException e) {
					e.printStackTrace();
				} catch (ClassCastException e) {
					log.error("ライフチェックにはデジタルタイプを指定してください ("
						+ dh.getDataHolderName()
						+ ")", e);
				}
			}
		}
	}

	private Address[] getAddresses(Collection addresses)
			throws AddressException {
		InternetAddress[] address = new InternetAddress[addresses.size()];
		Iterator it = addresses.iterator();
		for (int i = 0; it.hasNext(); i++) {
			address[i] = new InternetAddress((String) it.next());
		}
		return address;
	}

	private boolean isAlarmEmailSent() {
		String b = EnvironmentManager.get("/server/alarm/sentmail", "false");
		return Boolean.valueOf(b).booleanValue();
	}

	public void start() {
		if (null == thread) {
			thread = new Thread(this);
			thread.setName(getClass().getName());
			thread.start();
		}
	}

	public void stop() {
		if (null != thread) {
			Thread th = thread;
			thread = null;
			th.interrupt();
		}
	}

	private static class Message {
		private final MimeMessage message;
		private final Collection addresses;
		private final DataValueChangeEventKey key;

		Message(
				MimeMessage message,
				Collection addresses,
				DataValueChangeEventKey key) {
			this.message = message;
			this.addresses = addresses;
			this.key = key;
		}

		Collection getAddresses() {
			return addresses;
		}

		DataValueChangeEventKey getKey() {
			return key;
		}

		MimeMessage getMessage() {
			return message;
		}
	}
}
