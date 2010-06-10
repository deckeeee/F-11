package org.F11.scada.applet.schedule;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * 時間をバーメイトリクスに描画するコンポーネントです。
 */
class BarMatrix extends AbstractBarMatrix {
	private static final long serialVersionUID = 1747894179858693368L;

	/**
	 * コンストラクタ
	 * @param model スケジュールモデル・オブジェクト
	 */
	BarMatrix(ScheduleRowModel model) {
		super(model);
	}

	void paintTime(Graphics2D g2d) {
		for (int i = 0; i < model.getColumnCount(); i++) {
			paintTime(g2d, model.getOnTime(i), onColor);
			paintTime(g2d, model.getOffTime(i), offColor);
		}
	}

	/**
	 * 時間をバーに描画します。
	 * @param g2d グラフィックコンテキスト
	 * @param time 描画する時間
	 * @param color バーに描画する色
	 */
	private void paintTime(Graphics2D g2d, int time, Color color) {
		if (time == 0)
			return;

		int hour = time / 100;
		int minute = time % 100;

		Rectangle r = new Rectangle(MARGIN_WIDTH + (hour * SCALE_RATIO * SCALE_HOUR_RATIO) +
									(minute / 5) * (SCALE_RATIO / 2),
									SCALE_HEIGHT,
									SCALE_RATIO / 2,
									BAR_HEIGHT);
		g2d.setColor(color);
		g2d.fill(r);
	}
}