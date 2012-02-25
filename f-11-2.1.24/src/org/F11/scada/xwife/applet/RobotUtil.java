package org.F11.scada.xwife.applet;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

/**
 * Robot �N���X�̃V���O���g�����[�e�B���e�B�N���X�ł��B Robot �N���X�� new
 * ���Ďg�p����ƁA�������[���[�N���N�������߃V���O���g���ɂĎg�p����K�v������B
 *
 * @author maekawa
 *
 */
public class RobotUtil {
	private final Logger logger = Logger.getLogger(RobotUtil.class);
	private static final RobotUtil ROBOT_UTIL = new RobotUtil();
	private Robot robot;

	/**
	 * �v���C�x�[�g�R���X�g���N�^�ł��B �C���X�^���X������{@link #getInstance() getInstance}���\�b�h���g�p���ĉ������B
	 */
	private RobotUtil() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			logger.error("Robot�I�u�W�F�N�g�𐶐��ł��܂���ł����B:", e);
		}
	}

	/**
	 * RobotUtil �C���X�^���X��Ԃ��܂��B
	 *
	 * @return RobotUtil �C���X�^���X��Ԃ��܂��B
	 */
	public static RobotUtil getInstance() {
		return ROBOT_UTIL;
	}

	/**
	 * �����̃L�[���v���X���ă����[�X���܂��B
	 *
	 * @param keyEvent �L�[
	 *
	 */
	public void keyClick(int keyEvent) {
		if (null != robot) {
			robot.keyPress(keyEvent);
			robot.keyRelease(keyEvent);
		}
	}

	/**
	 * {@link Robot#createScreenCapture(Rectangle) createScreenCapture}�����s���܂��B
	 *
	 * @param rectangle �L���v�`���[����͈�
	 * @return �X�N���[���V���b�g��{@link BufferedImage}
	 */
	public BufferedImage createScreenCapture(Rectangle rectangle) {
		return robot != null ? robot.createScreenCapture(rectangle) : null;
	}

	public void moveMouse() {
		Point mp = MouseInfo.getPointerInfo().getLocation();
		int move = 5;
		if (move < mp.x) {
			robot.mouseMove(mp.x - move, mp.y);
			robot.delay(100);
			robot.mouseMove(mp.x, mp.y);
		} else {
			robot.mouseMove(mp.x + move, mp.y);
			robot.delay(100);
			robot.mouseMove(mp.x, mp.y);
		}
	}
}
