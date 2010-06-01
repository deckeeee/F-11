package org.F11.scada.applet.schedule;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.applet.symbol.ColorFactory;

/**
 * 時間をバーメイトリクスに描画するコンポーネントです。
 */
abstract class AbstractBarMatrix extends JComponent {
	/** データモデルの参照 */
	protected ScheduleRowModel model;
	/** On の色 */
	protected Color onColor;
	/** Off の色 */
	protected Color offColor;

	/** メモリ領域の高さ */
	static final int SCALE_HEIGHT = 20;
	/** バーの下のマージン */
	static final int MARGIN_BOTTOM = 5;
	/** 両横のマージン */
	static final int MARGIN_WIDTH = 10;
	/** 1メモリのポイント */
	static final int SCALE_RATIO = 6;
	/** 1メモリ/1時間 */
	static final int SCALE_HOUR_RATIO = 6;
	/** 24時間 */
	static final int HOUR_COUNT = 24;

	/** バーの横幅 */
	static final int BAR_WIDTH = SCALE_RATIO * SCALE_HOUR_RATIO * HOUR_COUNT;
	/** バーの高さ */
	static final int BAR_HEIGHT = 25;

	/**
	 * コンストラクタ
	 * @param model スケジュールモデル・オブジェクト
	 */
	AbstractBarMatrix(ScheduleRowModel model) {
		super();
		this.model = model;
		init();
	}

	/**
	 * 初期処理です。
	 */
	private void init() {
		setDoubleBuffered(true);
		setPreferredSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
		setMinimumSize(getPreferredSize());

		ClientConfiguration configuration = new ClientConfiguration();
		onColor = ColorFactory.getColor(configuration.getString("barmatrix.oncolor", "RED"));
		offColor = ColorFactory.getColor(configuration.getString("barmatrix.offcolor", "LIME"));
	}

	/**
	 * 推奨サイズを返します。
	 * バーのサイズにより、固定長のサイズを返します。
	 */
	public Dimension getPreferredSize() {
		return new Dimension(BAR_WIDTH + MARGIN_WIDTH * 2, BAR_HEIGHT + SCALE_HEIGHT + MARGIN_BOTTOM);
	}

	/**
	 * このコンポーネントを描画します。
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;

		// バーの土台を描画します。
		g2d.setColor(Color.gray);
		g2d.fill(new Rectangle(MARGIN_WIDTH, SCALE_HEIGHT, BAR_WIDTH, BAR_HEIGHT));

		// On/Off 時間データを描画します。
		paintTime(g2d);

		// バーの土台の枠を描画します。
		g2d.setColor(Color.black);
		g2d.draw(new Rectangle(MARGIN_WIDTH, SCALE_HEIGHT, BAR_WIDTH, BAR_HEIGHT));

		// 目盛りを描画します。
		for (int i = MARGIN_WIDTH, hour = 0; i <= BAR_WIDTH + MARGIN_WIDTH; i += SCALE_RATIO) {
			if ((i - MARGIN_WIDTH) % (SCALE_RATIO * SCALE_HOUR_RATIO) == 0) {
				g2d.drawLine(i, SCALE_HEIGHT - 3, i, SCALE_HEIGHT);
				if (hour >= 10)
					g2d.drawString(String.valueOf(hour++), i - 6, SCALE_HEIGHT - 5);
				else
					g2d.drawString(String.valueOf(hour++), i - 3, SCALE_HEIGHT - 5);
			} else if ((i - MARGIN_WIDTH) % (SCALE_RATIO * SCALE_HOUR_RATIO / 2) == 0) {
				g2d.drawLine(i, SCALE_HEIGHT - 2, i, SCALE_HEIGHT);
			} else {
				g2d.drawLine(i, SCALE_HEIGHT - 1, i, SCALE_HEIGHT);
			}
		}
	}

	abstract void paintTime(Graphics2D g2d);
}