package org.F11.scada.applet.schedule;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * ���Ԃ��o�[���C�g���N�X�ɕ`�悷��R���|�[�l���g�ł��B
 */
class BarMatrix extends AbstractBarMatrix {
	private static final long serialVersionUID = 1747894179858693368L;

	/**
	 * �R���X�g���N�^
	 * @param model �X�P�W���[�����f���E�I�u�W�F�N�g
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