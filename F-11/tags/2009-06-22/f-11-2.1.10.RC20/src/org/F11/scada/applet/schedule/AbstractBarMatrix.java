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
 * ���Ԃ��o�[���C�g���N�X�ɕ`�悷��R���|�[�l���g�ł��B
 */
abstract class AbstractBarMatrix extends JComponent {
	/** �f�[�^���f���̎Q�� */
	protected ScheduleRowModel model;
	/** On �̐F */
	protected Color onColor;
	/** Off �̐F */
	protected Color offColor;

	/** �������̈�̍��� */
	static final int SCALE_HEIGHT = 20;
	/** �o�[�̉��̃}�[�W�� */
	static final int MARGIN_BOTTOM = 5;
	/** �����̃}�[�W�� */
	static final int MARGIN_WIDTH = 10;
	/** 1�������̃|�C���g */
	static final int SCALE_RATIO = 6;
	/** 1������/1���� */
	static final int SCALE_HOUR_RATIO = 6;
	/** 24���� */
	static final int HOUR_COUNT = 24;

	/** �o�[�̉��� */
	static final int BAR_WIDTH = SCALE_RATIO * SCALE_HOUR_RATIO * HOUR_COUNT;
	/** �o�[�̍��� */
	static final int BAR_HEIGHT = 25;

	/**
	 * �R���X�g���N�^
	 * @param model �X�P�W���[�����f���E�I�u�W�F�N�g
	 */
	AbstractBarMatrix(ScheduleRowModel model) {
		super();
		this.model = model;
		init();
	}

	/**
	 * ���������ł��B
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
	 * �����T�C�Y��Ԃ��܂��B
	 * �o�[�̃T�C�Y�ɂ��A�Œ蒷�̃T�C�Y��Ԃ��܂��B
	 */
	public Dimension getPreferredSize() {
		return new Dimension(BAR_WIDTH + MARGIN_WIDTH * 2, BAR_HEIGHT + SCALE_HEIGHT + MARGIN_BOTTOM);
	}

	/**
	 * ���̃R���|�[�l���g��`�悵�܂��B
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;

		// �o�[�̓y���`�悵�܂��B
		g2d.setColor(Color.gray);
		g2d.fill(new Rectangle(MARGIN_WIDTH, SCALE_HEIGHT, BAR_WIDTH, BAR_HEIGHT));

		// On/Off ���ԃf�[�^��`�悵�܂��B
		paintTime(g2d);

		// �o�[�̓y��̘g��`�悵�܂��B
		g2d.setColor(Color.black);
		g2d.draw(new Rectangle(MARGIN_WIDTH, SCALE_HEIGHT, BAR_WIDTH, BAR_HEIGHT));

		// �ڐ����`�悵�܂��B
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