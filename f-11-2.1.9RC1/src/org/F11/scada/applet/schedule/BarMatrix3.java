package org.F11.scada.applet.schedule;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.applet.symbol.ColorFactory;

/**
 * 時間をバーメイトリクスに描画するコンポーネントです。
 */
class BarMatrix3 extends AbstractBarMatrix {
	private static final long serialVersionUID = -6845863045230966764L;
	private final Color barColor;

	/**
	 * コンストラクタ
	 * @param model スケジュールモデル・オブジェクト
	 */
	BarMatrix3(ScheduleRowModel model) {
		super(model);
		ClientConfiguration configuration = new ClientConfiguration();
		barColor = ColorFactory.getColor(configuration.getString("barmatrix2.barcolor", "PINK"));
	}

	void paintTime(Graphics2D g2d) {
		for (int i = 0; i < model.getColumnCount(); i++) {
			int onTime = model.getOnTime(i);
			int offTime = model.getOffTime(i);
			printTime(g2d, onTime, offTime, barColor);
		}
	}

	private void printTime(Graphics2D g2d, int onTime, int offTime, Color color) {
		int onHour = onTime / 100;
		int onMinute = onTime % 100;

		int offHour = offTime / 100;
		int offMinute = offTime % 100;

		int start = MARGIN_WIDTH + (onHour * SCALE_RATIO * SCALE_HOUR_RATIO) + (onMinute / 5) * (SCALE_RATIO / 2);
		int end = MARGIN_WIDTH + (offHour * SCALE_RATIO * SCALE_HOUR_RATIO) + (offMinute / 5) * (SCALE_RATIO / 2);
		Rectangle r = new Rectangle(start,
									SCALE_HEIGHT,
									end - start,
									BAR_HEIGHT);
		g2d.setColor(color);
		g2d.fill(r);
	}
}