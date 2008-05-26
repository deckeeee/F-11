package org.F11.scada;

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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.nio.ByteBuffer;

import javax.swing.SwingUtilities;

import org.F11.scada.server.io.ValueListHandler;

/**
 * ���̃N���X�ɂ́A�l�X�ȃ��\�b�h������܂��B ���ɕ��ނ���K�v���Ȃ��A���낢��ȃN���X�Ŏg�p����A���S���Y����Z�߂Ă���܂��B
 */
public class WifeUtilities {

	private static final String NULL_STRING = "null";

	private WifeUtilities() {
	}

	/**
	 * �R���|�[�l���g�̐e�t���[����T�����܂��B
	 * 
	 * @param comp �T���ΏۃR���|�[�l���g
	 * @return �e�t���[��������΂��̃t���[���A�Ȃ���� null ��Ԃ��܂��B
	 */
	static public Frame getParentFrame(Component comp) {
		return (Frame) SwingUtilities.getAncestorOfClass(Frame.class, comp);
	}

	/**
	 * �R���|�[�l���g�̐e�t���[����T�����܂��B
	 * 
	 * @param comp �T���ΏۃR���|�[�l���g
	 * @return �e�t���[��������΂��̃t���[���A�Ȃ���� null ��Ԃ��܂��B
	 */
	static public Window getDialogParent(Component comp) {
		return (Window) SwingUtilities.getAncestorOfClass(Window.class, comp);
	}

	/**
	 * �R���|�[�l���g�̃t�H���g���g��E�k�����܂��B
	 * 
	 * @param comp �R���|�[�l���g
	 * @param m �k���E�g�嗦
	 */
	static public void setFontSize(Component comp, double m) {
		comp.setFont(comp.getFont().deriveFont(
			(float) (comp.getFont().getSize2D() * m)));
	}

	private static String createRmiManagerDelegatorBase() {
		return "//"
			+ EnvironmentManager.get("/server/rmi/managerdelegator/name", "")
			+ ":"
			+ EnvironmentManager.get(
				"/server/rmi/managerdelegator/port",
				"1099")
			+ "/";
	}

	/**
	 * RMI �x�񃊃��[�g�I�u�W�F�N�g�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiManagerDelegator() {
		return createRmiManagerDelegatorBase() + "ManagerDelegator";
	}

	static private String createRmiCollectorBase() {
		return "//"
			+ EnvironmentManager.get("/server/rmi/collectorserver/name", "")
			+ ":"
			+ EnvironmentManager
				.get("/server/rmi/collectorserver/port", "1099")
			+ "/";
	}

	/**
	 * RMI �f�[�^���W�T�[�o�[�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiCollector() {
		return createRmiCollectorBase() + "DataCollector";
	}

	/**
	 * RMI RmiActionControl �T�[�o�[�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiActionControl() {
		return createRmiCollectorBase() + "ActionControl";
	}

	/**
	 * RMI RmiValueListHandler �T�[�o�[�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiValueListHandler() {
		return createRmiCollectorBase() + "ValueListHandler";
	}

	/**
	 * RMI RmiValueListHandler �T�[�o�[�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiSelectiveValueListHandler() {
		return createRmiCollectorBase() + "SelectiveValueListHandler";
	}

	/**
	 * RMI RmiValueListHandlerManager �T�[�o�[�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiValueListHandlerManager() {
		return createRmiManagerDelegatorBase() + "ValueListHandlerManager";
	}

	/**
	 * RMI RmiValueListHandlerManager �T�[�o�[�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiSelectiveValueListHandlerManager() {
		return createRmiManagerDelegatorBase()
			+ "SelectiveValueListHandlerManager";
	}

	/**
	 * RMI RmiValueListHandlerManager �T�[�o�[�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiSelectiveAllDataValueListHandlerManager() {
		return createRmiManagerDelegatorBase()
			+ "SelectiveAllDataValueListHandlerManager";
	}

	/**
	 * RMI RmiValueListHandler �T�[�o�[�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiSelectiveAllDataValueListHandler() {
		return createRmiCollectorBase() + "SelectiveAllDataValueListHandler";
	}

	/**
	 * RMI RmiFrameDefineManager �T�[�o�[�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiFrameDefineManager() {
		return createRmiManagerDelegatorBase() + "FrameDefineManager";
	}

	/**
	 * RMI RmiFrameDefineHandler �T�[�o�[�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiFrameDefineHandler() {
		return createRmiCollectorBase() + "FrameDefineHandler";
	}

	/**
	 * RMI RmiFrameEditManager �T�[�o�[�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiFrameEditManager() {
		return createRmiManagerDelegatorBase() + "FrameEditManager";
	}

	/**
	 * RMI RmiFrameEditHandler �T�[�o�[�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiFrameEditHandler() {
		return createRmiCollectorBase() + "FrameEditHandler";
	}

	/**
	 * RMI RmiServerEditManager �T�[�o�[�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiServerEditManager() {
		return createRmiManagerDelegatorBase() + "ServerEditManager";
	}

	/**
	 * RMI RmiAlarmListFinderManager �T�[�o�[�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiAlarmListFinderManager() {
		return createRmiManagerDelegatorBase() + "AlarmListFinderManager";
	}

	/**
	 * RMI RmiAlarmListFinderHandler �T�[�o�[�̎Q�ƕ������Ԃ��܂��B
	 */
	static public String createRmiAlarmListFinderHandler() {
		return createRmiCollectorBase() + "AlarmListFinderHandler";
	}

	static private String getDbName() {
		return EnvironmentManager.get("/server/jdbc/dbname", "");
	}

	/**
	 * JDBC�̃I�v�V�����������Ԃ��܂��B
	 * 
	 * @return JDBC�̃I�v�V����������
	 */
	static private String getJDBCOption() {
		return EnvironmentManager.get("/server/jdbc/option", "");
	}

	/**
	 * �f�[�^�x�[�X���� URI �������Ԃ��܂��B
	 */
	static public String createJdbcUri() {
		return "jdbc:"
			+ getDBMSName()
			+ "://"
			+ EnvironmentManager.get("/server/jdbc/servername", "")
			+ "/"
			+ getDbName()
			+ getJDBCOption();
	}

	/**
	 * �g�pDBMS���̂�Ԃ��܂��B
	 * 
	 * @return DBMS����
	 */
	static public String getDBMSName() {
		return EnvironmentManager.get("/server/jdbc/dbmsname", "");
	}

	/**
	 * JDBC �h���C�o�N���X�������Ԃ��܂��B
	 */
	static public String getJdbcDriver() {
		return EnvironmentManager.get("/server/jdbc/driver", "");
	}

	/**
	 * ���M���O�f�[�^�n���h���̎Q�ƕ������Ԃ��܂��B
	 * 
	 * @param name �f�o�C�X��
	 */
	static public String createValueListHandler(String name) {
		return "//"
			+ EnvironmentManager.get("/server/rmi/managerdelegator/name", "")
			+ ":"
			+ EnvironmentManager.get(
				"/server/rmi/managerdelegator/port",
				"1099")
			+ "/"
			+ ValueListHandler.class.getName()
			+ "_"
			+ name;
	}

	/**
	 * 
	 */
	static public Point getInScreenPoint(Dimension screenSize, Rectangle r) {
		Rectangle screen =
			new Rectangle(0, 0, screenSize.width, screenSize.height);
		if (screen.contains(r)) {
			return r.getLocation();
		} else {
			Rectangle in = screen.intersection(r);
			// System.out.println("�d�Ȃ蕔�� : " + in);
			int transX = 0;
			int transY = 0;
			if (in.x <= 0) {
				transX = r.width - in.width;
			} else {
				transX = (r.width - in.width) * -1;
			}
			if (in.y <= 0) {
				transY = r.height - in.height;
			} else {
				transY = (r.height - in.height) * -1;
			}
			r.translate(transX, transY);
			// System.out.println("���K���ς� : " + r);
			return r.getLocation();
		}
	}

	private static String getno = "0123456789abcdef0123456789ABCDEF";

	/**
	 * ������->byte�z�� �ϊ����[�`��
	 */
	public static byte[] toByteArray(String srcString) {
		if (0 < srcString.length() % 2)
			throw new IllegalArgumentException("Specify an even number!");
		// String getno = new String("0123456789abcdef0123456789ABCDEF");
		byte[] retval = new byte[srcString.length() / 2];
		for (int spos = 0; spos < srcString.length(); spos += 2) {
			int ch = getno.indexOf(srcString.charAt(spos)) % 16;
			int cl = getno.indexOf(srcString.charAt(spos + 1)) % 16;
			if (ch < 0 || cl < 0)
				throw new IllegalArgumentException(
					"Specify the character of 'a' to 'f'!");
			int bpos = spos / 2;
			retval[bpos] = (byte) (ch * 0x10 + cl);
		}
		return retval;
	}

	/**
	 * byte�z��->������ �ϊ����[�`��
	 */
	public static String toString(byte[] srcBytearray, int srcLength) {
		// String getno = new String("0123456789abcdef");
		char[] charArray = new char[srcLength * 2];
		for (int spos = 0; spos < srcLength * 2; spos += 2) {
			charArray[spos + 0] =
				getno.charAt((srcBytearray[spos / 2] >> 4) & 0x0f);
			charArray[spos + 1] = getno.charAt(srcBytearray[spos / 2] & 0x0f);
		}
		return new String(charArray);
	}

	public static String toString(byte[] srcBytearray, int offset, int length) {
		if (srcBytearray == null) {
			return NULL_STRING;
		}

		StringBuffer sb = new StringBuffer();
		for (int bpos = 0, spos = 0; bpos < length; bpos++, spos += 2) {
			byte bch = srcBytearray[offset + bpos];
			sb.append(getno.charAt((bch >> 4) & 0x0f));
			sb.append(getno.charAt(bch & 0x0f));
		}
		return sb.toString();
	}

	public static String toString(byte[] srcBytearray) {
		if (srcBytearray == null) {
			return NULL_STRING;
		}

		return toString(srcBytearray, 0, srcBytearray.length);
	}

	public static String toString(
			ByteBuffer srcByteBuffer,
			int offset,
			int length) {
		StringBuffer sb = new StringBuffer();
		for (int bpos = 0, spos = 0; bpos < length; bpos++, spos += 2) {
			byte bch = srcByteBuffer.get(offset + bpos);
			sb.append(getno.charAt((bch >> 4) & 0x0f));
			sb.append(getno.charAt(bch & 0x0f));
		}
		return sb.toString();
	}

	public static String toString(ByteBuffer srcByteBuffer) {
		return toString(srcByteBuffer, 0, srcByteBuffer.remaining());
	}

	/**
	 * HTML�̐��䕶�����G�X�P�[�v���܂��B
	 * 
	 * @param string �ϊ���������
	 * @return �ϊ����ʕ�����
	 */
	public static String htmlEscape(String string) {
		String ret = string;
		if (string != null) {
			ret = ret.replaceAll("&", "&amp;");
			ret = ret.replaceAll("<", "&lt;");
			ret = ret.replaceAll(">", "&gt;");
			ret = ret.replaceAll("\"", "&quot;");
			ret = ret.replaceAll("'", "&#39;");
		}
		return ret;
	}

	/**
	 * �����̃I�u�W�F�N�g�� Boolean, String �Ȃ�ȉ��̏����Ŕ��肷��B
	 * <ol>
	 * <li>null �Ȃ� false ��Ԃ��B
	 * <li>Boolean �Ȃ炻�̂܂ܓ��e��Ԃ��B
	 * <li>String �� "1" �Ȃ� true ���ȊO�Ȃ� false ��Ԃ��B
	 * <li>String �� "true" �Ȃ� true ���ȊO�Ȃ� false ��Ԃ��B
	 * <li>String �� "yes" �Ȃ� true ���ȊO�Ȃ� false ��Ԃ��B
	 * <li>String �� "t" �Ȃ� true ���ȊO�Ȃ� false ��Ԃ��B
	 * <li>Number �� 1 �Ȃ� true ���ȊO�Ȃ� false ��Ԃ��B
	 * </ol>
	 * 
	 * @param obj ���肷��I�u�W�F�N�g
	 * @return ��������� true �� false
	 */
	public static boolean isTrue(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Boolean) {
			return ((Boolean) obj).booleanValue();
		} else if (obj instanceof Number) {
			return ((Number) obj).intValue() == 1;
		} else if (obj instanceof String) {
			String str = (String) obj;
			return "yes".equalsIgnoreCase(str)
				|| "true".equalsIgnoreCase(str)
				|| "1".equalsIgnoreCase(str)
				|| "t".equalsIgnoreCase(str);
		} else {
			return false;
		}
	}

	public static void setCenter(Component c) {
		Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, c);
		Dimension dlgSize = c.getSize();
		Dimension frmSize = frame.getSize();
		Point loc = frame.getLocation();
		c.setLocation(
			(frmSize.width - dlgSize.width) / 2 + loc.x,
			(frmSize.height - dlgSize.height) / 2 + loc.y);
	}

	public static void setScreenCenter(Component c) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dlgSize = c.getSize();
		Dimension frmSize = toolkit.getScreenSize();
		c.setLocation(
			(frmSize.width - dlgSize.width) / 2,
			(frmSize.height - dlgSize.height) / 2);
	}

	public static boolean isSchedulePoint() {
		String schedulePoint =
			EnvironmentManager.get("/server/schedulepoint", "false");
		return isTrue(schedulePoint);
	}
}
