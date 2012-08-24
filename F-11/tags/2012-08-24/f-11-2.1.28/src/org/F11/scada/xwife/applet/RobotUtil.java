package org.F11.scada.xwife.applet;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

/**
 * Robot クラスのシングルトンユーティリティクラスです。 Robot クラスを new
 * して使用すると、メモリーリークを起こすためシングルトンにて使用する必要がある。
 *
 * @author maekawa
 *
 */
public class RobotUtil {
	private final Logger logger = Logger.getLogger(RobotUtil.class);
	private static final RobotUtil ROBOT_UTIL = new RobotUtil();
	private Robot robot;

	/**
	 * プライベートコンストラクタです。 インスタンス生成は{@link #getInstance() getInstance}メソッドを使用して下さい。
	 */
	private RobotUtil() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			logger.error("Robotオブジェクトを生成できませんでした。:", e);
		}
	}

	/**
	 * RobotUtil インスタンスを返します。
	 *
	 * @return RobotUtil インスタンスを返します。
	 */
	public static RobotUtil getInstance() {
		return ROBOT_UTIL;
	}

	/**
	 * 引数のキーをプレスしてリリースします。
	 *
	 * @param keyEvent キー
	 *
	 */
	public void keyClick(int keyEvent) {
		if (null != robot) {
			robot.keyPress(keyEvent);
			robot.keyRelease(keyEvent);
		}
	}

	/**
	 * {@link Robot#createScreenCapture(Rectangle) createScreenCapture}を実行します。
	 *
	 * @param rectangle キャプチャーする範囲
	 * @return スクリーンショットの{@link BufferedImage}
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
