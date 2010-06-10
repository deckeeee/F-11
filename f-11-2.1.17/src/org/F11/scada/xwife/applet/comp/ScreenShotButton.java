/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.xwife.applet.comp;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.xwife.applet.AbstractWifeApplet;
import org.F11.scada.xwife.applet.RobotUtil;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

/**
 * デスクトップのスクリーンショットを生成するボタン
 *
 * @author maekawa
 *
 */
public class ScreenShotButton extends JButton {
	private static final long serialVersionUID = -7550578587342892036L;
	private static final String IMAGES_SCREENSHOT_PNG =
		"/images/screenshot.png";

	public ScreenShotButton(final AbstractWifeApplet wifeApplet) {
		super(new ScreenShotAction(wifeApplet));
		initKeyEvent(wifeApplet);
	}

	private void initKeyEvent(final AbstractWifeApplet wifeApplet) {
		String ssKey =
			wifeApplet.getConfiguration().getString(
					"org.F11.scada.xwife.applet.comp.screenShotKey", "F11");
		if (null != ssKey && !"".equals(ssKey)) {
			Action action = getAction();
			InputMap imap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			imap.put(KeyStroke.getKeyStroke(ssKey), action);
			getActionMap().put(action, action);
		}
	}

	private static class ScreenShotAction extends AbstractAction {
		private static final String IO_EXCPTION_MSG =
			"スクリーンショットファイルの保存に失敗しました。";
		private static final String AWT_EXCEPTION_MSG = "スクリーンショット機能が使えない環境です。";
		private static final String DEFAULT_FILE_NAME =
			"'ss'yyyyMMddHHmmss'.png'";
		private static final long serialVersionUID = 878379803616641831L;
		private final Logger logger = Logger.getLogger(ScreenShotAction.class);
		private final ExecutorService service = Executors.newCachedThreadPool();
		private final AbstractWifeApplet wifeApplet;

		public ScreenShotAction(AbstractWifeApplet wifeApplet) {
			super(null, GraphicManager.get(IMAGES_SCREENSHOT_PNG));
			this.wifeApplet = wifeApplet;
			putValue(Action.SHORT_DESCRIPTION, "スクリーンショット");
		}

		public void actionPerformed(ActionEvent evt) {
			service.execute(new Runnable() {
				public void run() {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							save();
						}
					});
				}

				private void save() {
					try {
						Toolkit toolkit = Toolkit.getDefaultToolkit();
						Dimension screenSize = toolkit.getScreenSize();
						Rectangle screenRect = new Rectangle(screenSize);
						BufferedImage image =
							RobotUtil.getInstance().createScreenCapture(
									screenRect);
						File file = new File(getSavePath(), getFileName());
						if (showConfirmDialog(wifeApplet, "スクリーンショットファイル "
							+ file.getAbsolutePath()
							+ " を作成しますか？", "スクリーンショット", YES_NO_OPTION,
								QUESTION_MESSAGE) == YES_OPTION) {
							if (null != image) {
								ImageIO.write(image, "png", file);
							} else {
								logger.error("スクリーンショットがとれませんでした。");
							}
						}
					} catch (IOException e) {
						logger.error(IO_EXCPTION_MSG, e);
						showMessageDialog(wifeApplet, IO_EXCPTION_MSG,
								IO_EXCPTION_MSG, ERROR_MESSAGE);
					}
				}

				private String getSavePath() {
					String savePathName =
						wifeApplet
								.getConfiguration()
								.getString(
										"org.F11.scada.xwife.applet.comp.ScreenShotAction.savePathName",
										".");
					File file = new File(getFileName(savePathName));
					if (!"".equals(getFileName(savePathName)) && !file.exists()) {
						file.mkdirs();
					}
					return getFileName(savePathName);
				}

				private String getFileName() {
					String saveFileName =
						wifeApplet
								.getConfiguration()
								.getString(
										"org.F11.scada.xwife.applet.comp.ScreenShotAction.saveFileName",
										DEFAULT_FILE_NAME);
					return FastDateFormat
							.getInstance(getFileName(saveFileName)).format(
									new Date());
				}

				private String getFileName(String saveFileName) {
					return "".equals(saveFileName)
						? DEFAULT_FILE_NAME
						: saveFileName;
				}
			});
		}
	}
}
