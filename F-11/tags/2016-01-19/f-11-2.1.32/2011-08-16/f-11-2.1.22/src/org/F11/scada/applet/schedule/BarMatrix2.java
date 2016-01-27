package org.F11.scada.applet.schedule;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.F11.scada.applet.ClientConfiguration;
import org.F11.scada.applet.symbol.ColorFactory;

/**
 * ���Ԃ��o�[���C�g���N�X�ɕ`�悷��R���|�[�l���g�ł��B
 */
class BarMatrix2 extends AbstractBarMatrix {
	private static final long serialVersionUID = -6845863045230966764L;
	private final Color barColor;

	/**
	 * �R���X�g���N�^
	 * @param model �X�P�W���[�����f���E�I�u�W�F�N�g
	 */
	BarMatrix2(ScheduleRowModel model) {
		super(model);
		ClientConfiguration configuration = new ClientConfiguration();
		barColor = ColorFactory.getColor(configuration.getString("barmatrix2.barcolor", "PINK"));
	}

	void paintTime(Graphics2D g2d) {
		for (int i = 0; i < model.getColumnCount(); i++) {
			int onTime = model.getOnTime(i);
			int offTime = model.getOffTime(i);
			if (!isShot(onTime, offTime)) {
				printTime(g2d, onTime, offTime, barColor);
			}
		}

		for (int i = 0; i < model.getColumnCount(); i++) {
			int onTime = model.getOnTime(i);
			int offTime = model.getOffTime(i);
			if (isShot(onTime, offTime)) {
				paintTime(g2d, onTime, onColor);
				paintTime(g2d, offTime, offColor);
			}
		}
	}

	private boolean isShot(int onTime, int offTime) {
		return onTime == 0000 || offTime == 0000;
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

	/**
	 * ���Ԃ��o�[�ɕ`�悵�܂��B
	 * @param g2d �O���t�B�b�N�R���e�L�X�g
	 * @param time �`�悷�鎞��
	 * @param color �o�[�ɕ`�悷��F
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