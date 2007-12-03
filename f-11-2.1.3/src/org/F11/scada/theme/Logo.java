package org.F11.scada.theme;

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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.F11.scada.Service;
import org.F11.scada.applet.symbol.ColorFactory;
import org.F11.scada.applet.symbol.GraphicManager;

/**
 * F-11のロゴと日時を表示するクラスです。
 */
public class Logo extends JPanel implements Runnable, Service {
	private static final long serialVersionUID = 1279383348535111891L;
	private static final int H_GAP = 5;
	private LogoImage logoImage;
	private LogoTime logoTime;

	private Thread thread;

	public Logo() {
		setLayout(new FlowLayout(FlowLayout.LEFT, H_GAP, 0));
		logoImage = new LogoImage();
		logoTime = new LogoTime();
		add(logoTime);
		add(logoImage);

		start();
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop() {
		if (thread != null) {
			Thread ot = thread;
			thread = null;
			ot.interrupt();
		}
	}

	public void run() {
		Thread ct = Thread.currentThread();
		while (thread == ct) {
			try {
				logoImage.repaint();
				Thread.sleep(90L);
			} catch (InterruptedException ex) {
			}
		}
	}

	/**
	 * Wife Project ロゴを表示するクラスです。
	 */
	public class LogoImage extends JComponent {
		private static final long serialVersionUID = 3163242685437791367L;
		private boolean pointFlag1;
		private Point p1, p2;
		private Icon f11Icon;

		public LogoImage() {
			super();
			setDoubleBuffered(true);

			f11Icon = GraphicManager.get("/images/f-11s.png");

			p1 = new Point(0, 0);
			p2 = new Point(100, 50);
			addMouseListener(new AboutDialogListener());
		}

		/**
		 * コンポーネントを描画します。
		 * @param g グラフィックコンテキスト
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			createBackColor();

			Graphics2D g2d = (Graphics2D) g;

			GradientPaint paint =
				new GradientPaint(
					p1.x,
					p1.y,
					ColorFactory.getColor("midnightblue"),
					p2.x,
					p2.y,
					ColorFactory.getColor("lime"));
			Rectangle rec = new Rectangle(getSize());
			g2d.setPaint(paint);
			g2d.fill(rec);

			f11Icon.paintIcon(this, g, 0, 0);
		}

		public Dimension getPreferredSize() {
			//		return new Dimension(400, 30);
			return new Dimension(
				f11Icon.getIconWidth(),
				f11Icon.getIconHeight());
		}

		private void createBackColor() {
			if (pointFlag1) {
				if (p1.x <= 0) {
					pointFlag1 = false;
				} else {
					p1.x -= 10;
					p2.x += 10;
				}
			} else {
				if (p1.x >= 200) {
					pointFlag1 = true;
				} else {
					p1.x += 10;
					p2.x -= 10;
				}
			}
		}
	}

	/*
	 * テスト用メイン
	 */
	public static void main(String[] argv) {
		JFrame frame = new JFrame();
		Logo logo = new Logo();
		//		logo.setUserName("maekawa");
		frame.getContentPane().add(logo);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
