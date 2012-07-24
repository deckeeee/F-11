package org.F11.scada.scheduling;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * ���� ��莞����Ԃ��X�P�W���[���E�C�e���[�^�[�N���X�ł�
 */
public class DailyIterator implements ScheduleIterator {
	/** ���̓������v�Z����J�����_�[ */
	private final Calendar calendar = Calendar.getInstance();
	/** Logging API */
	private static Logger log = Logger.getLogger(DailyIterator.class);
	/** �t�H�[�}�b�^�[ */
	private final DateFormat fmt = DateFormat.getDateTimeInstance();

	/**
	 * ���E���E�b���w�肵�āA�C�e���[�^�[�����������܂��B
	 *
	 * @param hourOfDay ��
	 * @param minute ��
	 * @param second �b
	 */
	public DailyIterator(int hourOfDay, int minute, int second) {
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
	public DailyIterator(int hourOfDay, int minute, int second, Date date) {
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		if (!calendar.getTime().before(date)) {
			calendar.add(Calendar.DATE, -1);
		}
	}


	/**
	 * ���t�E���E���E�b���w�肵�āA�C�e���[�^�[�����������܂��B
	 * ���t�I�u�W�F�N�g�̎��E���E�b�͈����̎��E���E�b�ŏ㏑������܂�
	 *
	 * @param hourOfDay ��
	 * @param minute ��
	 * @param second �b
	 * @param date ���t
	 * @param millioffset �~���I�t�Z�b�g
	 */
	public DailyIterator(int hourOfDay, int minute, int second, Date date, int millioffset) {
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.add(Calendar.MILLISECOND, millioffset);
		if (!calendar.getTime().before(date)) {
			calendar.add(Calendar.DATE, -1);
		}
	}

	/**
	 * ���Ɏ��s���������Ԃ��܂��B
	 * @return ���Ɏ��s���������Ԃ��܂��B
	 */
	public Date next() {
		calendar.add(Calendar.DATE, 1);
		if (log.isInfoEnabled()) {
			log.info("Next schedule date : " + fmt.format(calendar.getTime()));
		}
		return calendar.getTime();
	}
}
