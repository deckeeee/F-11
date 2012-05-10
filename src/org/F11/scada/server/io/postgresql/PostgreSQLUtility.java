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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jp.gr.javacons.jim.DataHolder;
import jp.gr.javacons.jim.Manager;

import org.F11.scada.EnvironmentManager;
import org.F11.scada.Globals;
import org.F11.scada.WifeUtilities;
import org.F11.scada.data.BCDConvertException;
import org.F11.scada.data.WifeData;
import org.F11.scada.data.WifeDataAnalog;
import org.F11.scada.data.WifeDataDigital;
import org.F11.scada.server.communicater.Communicater;
import org.F11.scada.server.communicater.CommunicaterFactory;
import org.F11.scada.server.communicater.Environment;
import org.F11.scada.server.communicater.EnvironmentMap;
import org.F11.scada.server.entity.Item;
import org.F11.scada.server.entity.MultiRecordDefine;
import org.F11.scada.server.event.WifeCommand;
import org.F11.scada.server.io.ItemUtil;
import org.F11.scada.server.io.SQLUtility;
import org.F11.scada.server.register.HolderString;
import org.F11.scada.server.register.WifeDataUtil;
import org.F11.scada.util.BooleanUtil;
import org.apache.log4j.Logger;

/**
 * SQL�Ɋւ��郆�[�e�B���e�B�[�N���X �قƂ�ǂ̃��\�b�h�́ASQL ��������쐬����ÓI���\�b�h�ł��B
 */
public final class PostgreSQLUtility implements SQLUtility {
	public static final String DATE_FIELD_NAME = "f_date";
	private static final String REVISION_FIELD_NAME = "f_revision";
	private static final String PRIMARY_KEY = "PRIMARY KEY("
		+ DATE_FIELD_NAME
		+ ", "
		+ REVISION_FIELD_NAME
		+ ")";

	private final Map itemArrayPool;
	private final Map itemPool;
	private final ItemUtil itemUtil;
	private final CommunicaterFactory communicaterFactory;
	private final boolean isNoRevision;

	private static Logger logger = Logger.getLogger(PostgreSQLUtility.class);

	public PostgreSQLUtility(ItemUtil itemUtil,
			CommunicaterFactory communicaterFactory) {
		itemArrayPool = new ConcurrentHashMap();
		itemPool = new ConcurrentHashMap();
		this.itemUtil = itemUtil;
		this.communicaterFactory = communicaterFactory;
		isNoRevision = isNoRevision();
		logger.info("noRevison mode = " + isNoRevision);
	}

	private boolean isNoRevision() {
		String isNoRevision =
			EnvironmentManager.get("/server/logging/noRevision", "false");
		return Boolean.valueOf(isNoRevision).booleanValue();
	}

	/**
	 * INSERT ���� SQL ��������쐬���܂��B
	 *
	 * @param tableName �e�[�u����
	 * @param columnNames �J������
	 * @param values �l
	 * @return INSERT SQL �̕�����
	 */
	public String getInsertString(String tableName,
			String[] columnNames,
			Object[] values) {
		if (columnNames.length != values.length)
			throw new IllegalArgumentException(
				"Argument Count error : columnNames="
					+ columnNames.length
					+ " values="
					+ values.length);

		StringBuffer buffer = new StringBuffer();
		buffer.append("INSERT INTO ").append(tableName).append("(");
		for (int i = 0; i < (columnNames.length - 1); i++) {
			buffer.append(columnNames[i]);
			buffer.append(", ");
		}
		buffer.append(columnNames[columnNames.length - 1]);
		buffer.append(") VALUES(");
		for (int i = 0; i < (values.length - 1); i++) {
			setValue(buffer, values[i]);
			buffer.append(", ");
		}
		setValue(buffer, values[values.length - 1]);
		buffer.append(")");
		return buffer.toString();
	}

	/**
	 * INSERT ���� SQL ��������쐬���܂��B�X�V���t�ƃ��r�W������t�����܂��B
	 *
	 * @param tableName �e�[�u����
	 * @param dataHolders �f�[�^�z���_�̃��X�g
	 * @param revision ���r�W�����ԍ�
	 * @param timestamp �X�V���t
	 * @return INSERT SQL �̕�����
	 */
	public String getInsertString(String tableName,
			List dataHolders,
			Timestamp timestamp,
			int revision) {
		// ���t�ƃ��r�W�����ԍ���
		int dateAndRevision = 2;
		// �S�̗̂�
		int columnSize = dataHolders.size() + dateAndRevision;

		ArrayList columnNames = new ArrayList(columnSize);
		ArrayList values = new ArrayList(columnSize);

		columnNames.add(DATE_FIELD_NAME);
		columnNames.add(REVISION_FIELD_NAME);
		values.add(timestamp.toString());
		values.add(new Integer(revision));

		Item[] items = itemUtil.getItems(dataHolders, itemArrayPool);
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
				Environment environment = EnvironmentMap.get(provider);
				Communicater communicater =
					communicaterFactory.createCommunicator(environment);
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
					columnNames.add("f_"
						+ item.getProvider()
						+ "_"
						+ item.getHolder());
					WifeData wd = WifeDataUtil.getWifeData(item);
					WifeCommand wc = (WifeCommand) commandIt.next();
					byte[] data = (byte[]) bytedataMap.get(wc);
					// logger.info(item.getProvider() + " " + item.getHolder() +
					// ": byte(" + WifeUtilities.toString(data) + ")");
					try {
						wd = wd.valueOf(data);
						if (wd instanceof WifeDataAnalog) {
							WifeDataAnalog wda = (WifeDataAnalog) wd;
							values.add(new Double(wda.doubleValue()));
						} else if (wd instanceof WifeDataDigital) {
							WifeDataDigital wdd = (WifeDataDigital) wd;
							if (wdd.isOnOff(true)) {
								values.add(BooleanUtil.getDigitalValue(true));
							} else {
								values.add(BooleanUtil.getDigitalValue(false));
							}
						} else {
							throw new IllegalArgumentException(
								"value is not WifeDataDigital and WifeDataAnalog! : "
									+ wd.getClass().getName());
						}
					} catch (BCDConvertException e) {
						logger.error(
							"BCD�ϊ��G���[�����A�����l�����O�ɏ������݂܂��B"
								+ item.getProvider()
								+ "_"
								+ item.getHolder(),
							e);
						if (wd instanceof WifeDataAnalog) {
							values.add(new Double(0));
						} else if (wd instanceof WifeDataDigital) {
							values.add(BooleanUtil.getDigitalValue(false));
						} else {
							throw new IllegalArgumentException(
								"value is not WifeDataDigital and WifeDataAnalog! : "
									+ wd.getClass().getName());
						}
						continue;
					}
				}
			}
		} catch (Exception e) {
			logger.error("���M���OSQL�������G���[", e);
		}
		return getInsertString(
			tableName,
			(String[]) columnNames.toArray(new String[0]),
			values.toArray());
	}

	private boolean isNetError(String provider) {
		DataHolder errHolder =
			Manager.getInstance().findDataHolder(provider, Globals.ERR_HOLDER);
		WifeDataDigital wd = WifeDataDigital.valueOfTrue(0);
		return wd.equals(errHolder.getValue());
	}

	/**
	 * INSERT ���� SQL ��������쐬���܂��B�X�V���t�ƃ��r�W������t�����܂��B
	 *
	 * @param tableName �e�[�u����
	 * @param dataHolders �f�[�^�z���_�̃��X�g
	 * @param revision ���r�W�����ԍ�
	 * @param timestamp �X�V���t
	 * @return ���ږ��̔z��ƒl�̔z��̃��X�g
	 */
	public List getColumnValueString(String tableName,
			List dataHolders,
			MultiRecordDefine multiRecordDefine,
			Connection con) {
		List colval = new ArrayList(multiRecordDefine.getRecordCount() + 1);
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
			byte[] srcBytes = (byte[]) bytedataMap.get(wc);
			int byteRecSize =
				multiRecordDefine.getWordLength()
					/ multiRecordDefine.getRecordCount()
					* 2;

			// ���t�ƃ��r�W�����ԍ���
			int dateAndRevision = 2;
			// �S�̗̂�
			int columnSize = dataHolders.size() + dateAndRevision;

			ArrayList columnNames = new ArrayList(columnSize);

			columnNames.add(DATE_FIELD_NAME);
			columnNames.add(REVISION_FIELD_NAME);

			Item[] items = itemUtil.getItems(dataHolders, itemArrayPool);
			Map itemMap = itemUtil.getItemMap(items);

			boolean isFirstLoop = true;
			List itemList = (List) itemMap.get(multiRecordDefine.getProvider());
			for (int recno = 0; recno < multiRecordDefine.getRecordCount(); recno++) {
				Timestamp timestamp =
					getTimestamp(srcBytes, recno * byteRecSize);
				if (timestamp == null) {
					logger.warn("�����R�[�h ���t�̌`�����Ⴂ�܂��B"
						+ tableName
						+ " recno="
						+ recno);
					continue;
				}
				ArrayList values = new ArrayList(columnSize);
				values.add(timestamp.toString());
				values
					.add(new Integer(checkRevision(tableName, timestamp, con)));
				for (Iterator itemIt = itemList.iterator(); itemIt.hasNext();) {
					Item item = (Item) itemIt.next();
					WifeData wd = WifeDataUtil.getWifeData(item);
					if (multiRecordDefine.getComMemoryKinds() != item
						.getComMemoryKinds()
						|| multiRecordDefine.getComMemoryAddress() > item
							.getComMemoryAddress()
						|| multiRecordDefine.getComMemoryAddress()
							+ byteRecSize < item.getComMemoryAddress()
							+ wd.getWordSize()) {
						logger.warn("�����R�[�h �f�[�^��`���u���b�N�͈͊O�ł��B"
							+ item.getProvider()
							+ " "
							+ item.getHolder());
						continue;
					}
					if (isFirstLoop) {
						columnNames.add("f_"
							+ item.getProvider()
							+ "_"
							+ item.getHolder());
					}
					int byteOffset =
						recno
							* byteRecSize
							+ (int) (item.getComMemoryAddress() - multiRecordDefine
								.getComMemoryAddress()) * 2;
					byte[] data = new byte[wd.getWordSize() * 2];
					System.arraycopy(
						srcBytes,
						byteOffset,
						data,
						0,
						wd.getWordSize() * 2);
					// logger.debug(item.getProvider() + " " + item.getHolder()
					// + ": byte(" + WifeUtilities.toString(data) + ")");
					try {
						wd = wd.valueOf(data);
						if (wd instanceof WifeDataAnalog) {
							WifeDataAnalog wda = (WifeDataAnalog) wd;
							values.add(new Double(wda.doubleValue()));
						} else if (wd instanceof WifeDataDigital) {
							WifeDataDigital wdd = (WifeDataDigital) wd;
							if (wdd.isOnOff(true)) {
								values.add(BooleanUtil.getDigitalValue(true));
							} else {
								values.add(BooleanUtil.getDigitalValue(false));
							}
						} else {
							throw new IllegalArgumentException(
								"value is not WifeDataDigital and WifeDataAnalog! : "
									+ wd.getClass().getName());
						}
					} catch (BCDConvertException e) {
						logger.error("BCD�ϊ��G���[�����A�����l�����O�ɏ������݂܂�", e);
						if (wd instanceof WifeDataAnalog) {
							values.add(new Double(0));
						} else if (wd instanceof WifeDataDigital) {
							values.add(BooleanUtil.getDigitalValue(false));
						} else {
							throw new IllegalArgumentException(
								"value is not WifeDataDigital and WifeDataAnalog! : "
									+ wd.getClass().getName());
						}
						continue;
					}
				}
				colval.add(values.toArray());
				isFirstLoop = false;
			}
			colval.add(0, (String[]) columnNames.toArray(new String[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return colval;
	}

	/**
	 * ���݂̓��t�ŏd�����R�[�h���`�F�b�N���܂��B�d�����R�[�h�����݂����ꍇ�A�C���N�������g���� ���r�W�����ԍ���Ԃ��܂��B
	 *
	 * @param tableName �f�o�C�X��
	 * @param today �`�F�b�N���郌�R�[�h�̃^�C���X�^���v
	 * @return ���r�W�����ԍ�
	 * @exception SQLException �f�[�^�x�[�X�G���[�����������ꍇ
	 */
	public int checkRevision(String tableName, Timestamp today, Connection con)
			throws SQLException {
		if (isNoRevision) {
			return 0;
		}
		int revision = 0;
		Statement st = null;
		ResultSet rs = null;
		try {
			st =
				con.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = st.executeQuery(getRevisionString(tableName, today));
			rs.last();
			if (rs.getRow() > 0) {
				logger.info("���r�W�������� : " + rs.getRow());
				rs.first();
				revision = rs.getInt(1) + 1;
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
		}
		return revision;
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

	/**
	 * SELECT ���� SQL ��������쐬���܂��B�A���Awhere ��͂����ł͍쐬���܂���B �N���C�A���g�ō쐬���Ă��������B
	 *
	 * @param tableName �e�[�u����
	 * @param columnNames �J������
	 * @return SELECT SQL �̕�����
	 */
	private String getSelectString(String tableName, String[] columnNames) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT ");
		for (int i = 0; i < (columnNames.length - 1); i++) {
			buffer.append(columnNames[i]);
			buffer.append(", ");
		}
		buffer.append(columnNames[columnNames.length - 1]);
		buffer.append(" FROM ").append(tableName);
		return buffer.toString();
	}

	/**
	 * CREATE ���� SQL ��������쐬���܂��B
	 *
	 * @param tableName �e�[�u����
	 * @param columnNames �J������
	 * @param values �l
	 * @param primaryKey �v���C�}���L�[������
	 * @return CREATE SQL �̕�����
	 */
	public String getCreateString(String tableName,
			String[] columnNames,
			Object[] values,
			String primaryKey) {
		if (columnNames.length != values.length)
			throw new IllegalArgumentException(
				"Argument Count error : columnNames="
					+ columnNames.length
					+ " values="
					+ values.length);

		StringBuffer buffer = new StringBuffer();
		buffer.append("CREATE TABLE ");
		buffer.append(tableName);
		buffer.append(" (");

		logger.debug("count = " + columnNames.length);
		for (int i = 0; i < (columnNames.length - 1); i++) {
			logger.debug("columnName["
				+ i
				+ "]"
				+ columnNames[i]
				+ " value["
				+ i
				+ "]"
				+ values[i]);
			buffer.append(columnNames[i]);
			setCreateType(buffer, values[i]);
			buffer.append(", ");
		}
		buffer.append(columnNames[columnNames.length - 1]);
		setCreateType(buffer, values[values.length - 1]);
		buffer.append(", ");
		buffer.append(primaryKey);
		buffer.append(")");
		return buffer.toString();
	}

	/**
	 * CREATE ���� SQL ��������쐬���܂��B
	 *
	 * @param tableName �e�[�u����
	 * @param dataHolders �f�[�^�z���_�̃��X�g
	 * @return CREATE SQL �̕�����
	 */
	public String getCreateString(String tableName, List dataHolders) {
		// ���t�ƃ��r�W�����ԍ���
		int dateAndRevision = 2;
		// �S�̗̂�
		int columnSize = dataHolders.size() + dateAndRevision;

		String[] columnNames = new String[columnSize];
		Object[] values = new Object[columnSize];

		columnNames[0] = DATE_FIELD_NAME;
		columnNames[1] = REVISION_FIELD_NAME;
		values[0] = new Timestamp(System.currentTimeMillis());
		values[1] = new Integer(0);

		Item[] items = itemUtil.getItems(dataHolders, itemArrayPool);

		if (isNotEqualCount(dataHolders, items)) {
			throw new IllegalStateException("not equal count. dataholder="
				+ dataHolders.size()
				+ " item="
				+ items.length);
		}

		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			// logger.debug("item[" + i + "] = " + item);
			columnNames[i + dateAndRevision] =
				"f_" + item.getProvider() + "_" + item.getHolder();
			values[i + dateAndRevision] = WifeDataUtil.getWifeData(item);
		}
		return getCreateString(tableName, columnNames, values, PRIMARY_KEY);
	}

	private boolean isNotEqualCount(List dataHolders, Item[] items) {
		if (items.length == dataHolders.size()) {
			return false;
		} else {
			Set itemSet = new HashSet();
			for (int i = 0; i < items.length; i++) {
				Item item = items[i];
				HolderString hs = new HolderString();
				hs.setProvider(item.getProvider());
				hs.setHolder(item.getHolder());
				itemSet.add(hs);
			}
			Set srcSet = new HashSet(dataHolders);
			srcSet.removeAll(itemSet);
			logger.error("not regster item = " + srcSet);
			return true;
		}
	}

	/**
	 * ALTER ���� SQL ��������쐬���܂��B
	 *
	 * @param tableName �e�[�u����
	 * @param columnName �J������
	 * @param value �l
	 * @return ALTER SQL �̕�����
	 */
	public String getAlterString(String tableName,
			String columnName,
			Object value) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("ALTER TABLE ");
		buffer.append(tableName);
		buffer.append(" ADD COLUMN ");
		buffer.append(columnName);
		setCreateType(buffer, value);
		return buffer.toString();
	}

	/**
	 * ALTER ���� SQL ��������쐬���܂��B
	 *
	 * @param tableName �e�[�u����
	 * @param dataHolder �f�[�^�z���_
	 * @return ALTER SQL �̕�����
	 */
	public String getAlterString(String tableName, HolderString dataHolder) {
		String s =
			"f_" + dataHolder.getProvider() + "_" + dataHolder.getHolder();
		Item item = itemUtil.getItem(dataHolder, itemPool);
		return getAlterString(tableName, s, WifeDataUtil.getWifeData(item));
	}

	private void setValue(StringBuffer buffer, Object value) {
		if (value instanceof String) {
			buffer.append("'");
			buffer.append((String) value);
			buffer.append("'");
		} else if (value instanceof Short) {
			buffer.append(((Number) value).shortValue());
		} else if (value instanceof Integer) {
			buffer.append(((Number) value).intValue());
		} else if (value instanceof Long) {
			buffer.append(((Number) value).longValue());
		} else if (value instanceof Float) {
			buffer.append(((Number) value).floatValue());
		} else if (value instanceof Double) {
			buffer.append(((Number) value).doubleValue());
		} else if (value instanceof Boolean) {
			buffer.append(((Boolean) value).toString().toUpperCase());
		} else {
			throw new IllegalArgumentException(
				"value is not String and Number! : "
					+ value.getClass().getName());
		}
	}

	private void setCreateType(StringBuffer buffer, Object value) {
		if (value instanceof WifeDataDigital) {
			buffer.append(" BOOL");
		} else if (value instanceof WifeDataAnalog) {
			buffer.append(" double precision");
		} else if (value instanceof Integer) {
			buffer.append(" INTEGER NOT NULL");
		} else if (value instanceof Timestamp) {
			buffer.append(" DATETIME NOT NULL");
		} else {
			throw new IllegalArgumentException(
				"value is not WifeDataDigital and WifeDataAnalog! : "
					+ value.getClass().getName());
		}
	}

	/**
	 * SELECT * FROM �e�[�u���� LIMIT 0 �� SQL ��������쐬���܂��B �e�[�u���̗񐔂����߂�ׂɎg�p���܂��B
	 *
	 * @param tableName �e�[�u����
	 * @return SQL ������
	 */
	public String getSelectAllLimitZeroString(String tableName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT * FROM ").append(tableName).append(" LIMIT 0");
		return buffer.toString();
	}

	/**
	 * �d�����R�[�h������ SQL �����쐬���܂��B
	 *
	 * @param name �f�o�C�X��
	 * @param today �X�V���t
	 * @return SQL ������
	 */
	public String getRevisionString(String name, Timestamp today) {
		return "SELECT f_revision FROM "
			+ name
			+ " WHERE f_date="
			+ "'"
			+ today
			+ "'"
			+ " ORDER BY f_revision DESC";
	}

	/**
	 * <p>
	 * �f�[�^�z���_�[�Ŏw�肳�ꂽ���Ԃ� SQL �������Ԃ��܂��B
	 * <p>
	 * <b>�A���A�i�[���ꂽ�f�[�^�̃��r�W�����i���o�[���A'0' �݂̂̃f�[�^���Z���N�g���܂��B</b>
	 *
	 * @param name �f�o�C�X��
	 * @param dataHolder �f�[�^�z���_�̃��X�g
	 * @param limit �ő僌�R�[�h����
	 * @return SQL������
	 */
	public String getSelectAllString(String name, List dataHolder, int limit) {
		String[] columnNames = new String[dataHolder.size() + 1];
		columnNames[0] = DATE_FIELD_NAME;
		int count = 1;
		for (Iterator i = dataHolder.iterator(); i.hasNext(); count++) {
			HolderString hs = (HolderString) i.next();
			columnNames[count] = "f_" + hs.getProvider() + "_" + hs.getHolder();
		}

		StringBuffer b = new StringBuffer();
		b.append(getSelectString(name, columnNames));
		b.append(" WHERE f_revision = 0 ORDER BY ");
		b.append(DATE_FIELD_NAME);
		b.append(" DESC LIMIT ");
		b.append(limit);

		logger.debug(b.toString());

		return b.toString();
	}

	public String getSelectTimeString(String name,
			List dataHolder,
			Timestamp time) {
		String[] columnNames = createFieldNames(dataHolder);

		StringBuffer b = new StringBuffer();
		b.append(getSelectString(name, columnNames));
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		b
			.append(" WHERE f_revision = 0 AND f_date > '")
			.append(f.format(time))
			.append("'");
		b.append(" ORDER BY ");
		b.append(DATE_FIELD_NAME);
		b.append(" DESC LIMIT ");
		b.append(PostgreSQLValueListHandler.MAX_MAP_SIZE);

		logger.debug(b.toString());

		return b.toString();
	}

	private String[] createFieldNames(List dataHolder) {
		String[] columnNames = new String[dataHolder.size() + 1];
		columnNames[0] = DATE_FIELD_NAME;
		int count = 1;
		for (Iterator i = dataHolder.iterator(); i.hasNext(); count++) {
			HolderString hs = (HolderString) i.next();
			columnNames[count] = "f_" + hs.getProvider() + "_" + hs.getHolder();
		}
		return columnNames;
	}

	private String[] createFieldNames(List dataHolder, List<String> tables) {
		String[] columnNames = new String[dataHolder.size() + 1];
		columnNames[0] = tables.get(0) + "." + DATE_FIELD_NAME;
		int count = 1;
		for (Iterator i = dataHolder.iterator(); i.hasNext(); count++) {
			HolderString hs = (HolderString) i.next();
			columnNames[count] = "f_" + hs.getProvider() + "_" + hs.getHolder();
		}
		return columnNames;
	}

	public String getFirstData(String name, List dataHolder) {
		String[] columnNames = createFieldNames(dataHolder);

		StringBuffer b = new StringBuffer();
		b
			.append(getSelectString(name, columnNames))
			.append(" ORDER BY ")
			.append(DATE_FIELD_NAME)
			.append(" ASC LIMIT ")
			.append("1");

		return b.toString();
	}

	public String getLastData(String name, List dataHolder) {
		String[] columnNames = createFieldNames(dataHolder);
		StringBuffer b = new StringBuffer();
		b
			.append(getSelectString(name, columnNames))
			.append(" ORDER BY ")
			.append(DATE_FIELD_NAME)
			.append(" DESC LIMIT ")
			.append("1");

		return b.toString();
	}

	public String getSelectBefore(String name,
			List dataHolder,
			Timestamp start,
			int limit) {
		String[] columnNames = createFieldNames(dataHolder);

		StringBuffer b = new StringBuffer();
		b.append(getSelectString(name, columnNames));
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		b
			.append(" WHERE f_revision = 0 AND f_date < '")
			.append(f.format(start))
			.append("'");
		b.append(" ORDER BY ");
		b.append(DATE_FIELD_NAME);
		b.append(" DESC LIMIT ");
		b.append(limit);

		logger.debug(b.toString());

		return b.toString();
	}

	public String getSelectAfter(String name,
			List dataHolder,
			Timestamp start,
			int limit) {
		String[] columnNames = createFieldNames(dataHolder);

		StringBuffer b = new StringBuffer();
		b.append(getSelectString(name, columnNames));
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		b
			.append(" WHERE f_revision = 0 AND f_date >= '")
			.append(f.format(start))
			.append("'");
		b.append(" ORDER BY ");
		b.append(DATE_FIELD_NAME);
		b.append(" ASC LIMIT ");
		b.append(limit);

		logger.debug(b.toString());

		return b.toString();
	}

	public String getSelectAllString(String name,
			List dataHolder,
			int limit,
			List<String> tables) {
		String[] columnNames = createFieldNames(dataHolder, tables);

		StringBuffer b = new StringBuffer();
		b.append(getSelectString(name, columnNames, tables));
		b
			.append(" WHERE ")
			.append(tables.get(0))
			.append(".f_revision = 0 ORDER BY ");
		b.append(tables.get(0)).append(".");
		b.append(DATE_FIELD_NAME);
		b.append(" DESC LIMIT ");
		b.append(limit);

		return b.toString();
	}

	private Object getSelectString(String name,
			String[] columnNames,
			List<String> tables) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT ");
		for (int i = 0; i < (columnNames.length - 1); i++) {
			buffer.append(columnNames[i]);
			buffer.append(", ");
		}
		buffer.append(columnNames[columnNames.length - 1]);
		buffer.append(" FROM ").append(getTables(tables));
		return buffer.toString();
	}

	private String getTables(List<String> tables) {
		if (tables.size() == 1) {
			return tables.get(0);
		} else {
			StringBuilder b = new StringBuilder();
			for (int i = 0, count = tables.size() - 1; i < count; i++) {
				if (i == 0) {
					b.append(tables.get(i)).append(" ");
				}
				b
					.append("LEFT JOIN ")
					.append(tables.get(i + 1))
					.append(" ON ")
					.append(tables.get(i))
					.append(".f_date = ")
					.append(tables.get(i + 1))
					.append(".f_date");
				if (i < (count - 1)) {
					b.append(" ");
				}
			}
			return b.toString();
		}
	}

	public String getSelectTimeString(String name,
			List dataHolder,
			Timestamp time,
			List<String> tables) {
		String[] columnNames = createFieldNames(dataHolder, tables);

		StringBuffer b = new StringBuffer();
		b.append(getSelectString(name, columnNames, tables));
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		b
			.append(" WHERE ")
			.append(tables.get(0))
			.append(".f_revision = 0 AND ")
			.append(tables.get(0))
			.append(".f_date > '")
			.append(f.format(time))
			.append("'");
		b.append(" ORDER BY ");
		b.append(tables.get(0)).append(".");
		b.append(DATE_FIELD_NAME);
		b.append(" DESC LIMIT ");
		b.append(PostgreSQLValueListHandler.MAX_MAP_SIZE);

		logger.debug(b.toString());

		return b.toString();
	}

	public String getSelectAfter(String name,
			List dataHolder,
			Timestamp start,
			int limit,
			List<String> tables) {
		String[] columnNames = createFieldNames(dataHolder, tables);

		StringBuffer b = new StringBuffer();
		b.append(getSelectString(name, columnNames, tables));
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		b
			.append(" WHERE ")
			.append(tables.get(0))
			.append(".f_revision = 0 AND ")
			.append(tables.get(0))
			.append(".f_date >= '")
			.append(f.format(start))
			.append("'");
		b.append(" ORDER BY ");
		b.append(tables.get(0)).append(".");
		b.append(DATE_FIELD_NAME);
		b.append(" ASC LIMIT ");
		b.append(limit);

		logger.debug(b.toString());

		return b.toString();
	}

	public String getSelectBefore(String name,
			List dataHolder,
			Timestamp start,
			int limit,
			List<String> tables) {
		String[] columnNames = createFieldNames(dataHolder, tables);

		StringBuffer b = new StringBuffer();
		b.append(getSelectString(name, columnNames, tables));
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		b
			.append(" WHERE ")
			.append(tables.get(0))
			.append(".f_revision = 0 AND ")
			.append(tables.get(0))
			.append(".f_date < '")
			.append(f.format(start))
			.append("'");
		b.append(" ORDER BY ");
		b.append(tables.get(0)).append(".");
		b.append(DATE_FIELD_NAME);
		b.append(" DESC LIMIT ");
		b.append(limit);

		logger.debug(b.toString());

		return b.toString();
	}

	public String getFirstData(String name, List dataHolder, List<String> tables) {
		String[] columnNames = createFieldNames(dataHolder, tables);

		StringBuffer b = new StringBuffer();
		b
			.append(getSelectString(name, columnNames, tables))
			.append(" ORDER BY ")
			.append(tables.get(0))
			.append(".")
			.append(DATE_FIELD_NAME)
			.append(" ASC LIMIT 1");

		return b.toString();
	}

	public String getLastData(String name, List dataHolder, List<String> tables) {
		String[] columnNames = createFieldNames(dataHolder, tables);
		StringBuffer b = new StringBuffer();
		b
			.append(getSelectString(name, columnNames, tables))
			.append(" ORDER BY ")
			.append(tables.get(0))
			.append(".")
			.append(DATE_FIELD_NAME)
			.append(" DESC LIMIT 1");

		return b.toString();
	}

	/**
	 * �����̃^�C���X�^���v�͈�(start�ȏ�,end����)�̃f�[�^��Ԃ��܂�
	 *
	 * @param name �e�[�u����
	 * @param data ���o�z���_�̃��X�g
	 * @param start ���̃^�C���X�^���v�ȏ�
	 * @param end ���̃^�C���X�^���v����
	 * @return �����̃^�C���X�^���v�͈͂̃f�[�^��Ԃ��܂�
	 * @see #getSelectBefore(String, List, Timestamp, Timestamp)
	 */
	public String getSelectPeriod(String name,
			List dataHolder,
			Timestamp start,
			Timestamp end) {
		String[] columnNames = createFieldNames(dataHolder);

		StringBuffer b = new StringBuffer();
		b.append(getSelectString(name, columnNames));
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		b
			.append(" WHERE ")
			.append("f_revision = 0 AND ")
			.append("f_date >= '")
			.append(f.format(start))
			.append("' AND ")
			.append("f_date < '")
			.append(f.format(end))
			.append("'");
		b.append(" ORDER BY ");
		b.append(DATE_FIELD_NAME);
		b.append(" DESC ");

		logger.debug(b.toString());

		return b.toString();
	}

	public String getSelectPeriod(String name,
			List dataHolder,
			Timestamp start,
			Timestamp end,
			List<String> tables) {
		String[] columnNames = createFieldNames(dataHolder, tables);

		StringBuffer b = new StringBuffer();
		b.append(getSelectString(name, columnNames, tables));
		SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		b
			.append(" WHERE ")
			.append(tables.get(0))
			.append(".f_revision = 0 AND ")
			.append(tables.get(0))
			.append(".f_date >= '")
			.append(f.format(start))
			.append("' AND ")
			.append(tables.get(0))
			.append(".f_date < '")
			.append(f.format(end))
			.append("'");
		b.append(" ORDER BY ");
		b.append(tables.get(0)).append(".");
		b.append(DATE_FIELD_NAME);
		b.append(" DESC ");

		logger.debug(b.toString());

		return b.toString();
	}

	public String getPaddingSql(String tableName,
			List<HolderString> dataHolders,
			Timestamp timestamp,
			int revision) {
		// ���t�ƃ��r�W�����ԍ���
		int dateAndRevision = 2;
		// �S�̗̂�
		int columnSize = dataHolders.size() + dateAndRevision;

		ArrayList columnNames = new ArrayList(columnSize);
		ArrayList values = new ArrayList(columnSize);

		columnNames.add(DATE_FIELD_NAME);
		columnNames.add(REVISION_FIELD_NAME);
		values.add(timestamp.toString());
		values.add(new Integer(revision));
		Double zero = new Double(0);

		for (HolderString hs : dataHolders) {
			columnNames.add("f_" + hs.getProvider() + "_" + hs.getHolder());
			values.add(zero);
		}
		return getInsertString(
			tableName,
			(String[]) columnNames.toArray(new String[0]),
			values.toArray());
	}
}
