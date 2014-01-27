package org.F11.scada.scheduling;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * ���T��(���j��) ��莞����Ԃ��X�P�W���[���E�C�e���[�^�[�N���X�ł�
 */
public class WeeklyIterator implements ScheduleIterator {
	/** ���̓������v�Z����J�����_�[ */
	private final Calendar calendar = Calendar.getInstance();
	/** Logging API */
	private static Logger log = Logger.getLogger(WeeklyIterator.class);

	/**
	 * ���E���E�b���w�肵�āA�C�e���[�^�[�����������܂��B
	 *
	 * @param hourOfDay ��
	 * @param minute ��
	 * @param second �b
	 */
	public WeeklyIterator(int hourOfDay, int minute, int second) {
		this(hourOfDay, minute, second, new Date());
	}

	/**
	 * ���t�E���E���E�b���w�肵�āA�C�e���[�^�[�����������܂��B
	 * ���t�I�u�W�F�N�g�̎��E���E�b�͈����̎��E���E�b�ŏ㏑������܂�
	 *
	 * @param hourOfDay ��
	 * @param minute ��
	 * @param second �b
	 * @param date ���t
	 */
	public WeeklyIterator(int hourOfDay, int minute, int second, Date date) {
		calendar.setTime(date);
		while (Calendar.MONDAY != calendar.get(Calendar.DAY_OF_WEEK)) {
			calendar.add(Calendar.DATE, 1);
		}
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		// ���T�����Z�o
		if (!calendar.getTime().before(date)) {
			calendar.add(Calendar.DATE, -7);
		}
	}

	/**
	 * ���Ɏ��s���������Ԃ��܂��B
	 * @return ���Ɏ��s���������Ԃ��܂��B
	 */
	public Date next() {
		calendar.add(Calendar.DATE, 7);
		if (log.isInfoEnabled()) {
			DateFormat fmt = DateFormat.getDateTimeInstance();
			log.info("Next schedule date : " + fmt.format(calendar.getTime()));
		}
		return calendar.getTime();
	}
}
