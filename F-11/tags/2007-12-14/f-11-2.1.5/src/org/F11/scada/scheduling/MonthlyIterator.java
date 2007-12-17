package org.F11.scada.scheduling;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * ���� ��莞����Ԃ��X�P�W���[���E�C�e���[�^�[�N���X�ł�
 */
public class MonthlyIterator implements ScheduleIterator {
	/** ���̓������v�Z����J�����_�[ */
	private final Calendar calendar = Calendar.getInstance();
	/** Logging API */
	private static Logger log = Logger.getLogger(MonthlyIterator.class);
	/** �t�H�[�}�b�^�[ */
	private final DateFormat fmt = DateFormat.getDateTimeInstance();

	/**
	 * ���t�͓������g�p�����E���E���E�b���w�肵�āA�C�e���[�^�[�����������܂��B
	 * ���t�ɂ�1�`28���w�肵�܂�
	 *
	 * @param day ��
	 * @param hourOfDay ��
	 * @param minute ��
	 * @param second �b
	 */
	public MonthlyIterator(int day, int hourOfDay, int minute, int second) {
		this(day, hourOfDay, minute, second, new Date());
	}

	/**
	 * ���t�E���E���E���E�b���w�肵�āA�C�e���[�^�[�����������܂��B
	 * ���t�I�u�W�F�N�g�̓��E���E���E�b�͈����̎��E���E�b�ŏ㏑������܂�
	 * ���t�ɂ�1�`28���w�肵�܂�
	 *
	 * @param day ��
	 * @param hourOfDay ��
	 * @param minute ��
	 * @param second �b
	 * @param date ���t
	 */
	public MonthlyIterator(int day, int hourOfDay, int minute, int second, Date date) {
		if (day < 1 || day > 28) {
			throw new IllegalArgumentException("Please specify even 1-28 to be the dates.");
		}
		calendar.setLenient(false);
		calendar.setTime(date);
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		if (!calendar.getTime().before(date)) {
			calendar.add(Calendar.MONTH, -1);
		}
	}

	/**
	 * ���Ɏ��s���������Ԃ��܂��B
	 * @return ���Ɏ��s���������Ԃ��܂��B
	 */
	public Date next() {
		calendar.add(Calendar.MONTH, 1);
		if (log.isInfoEnabled()) {
			log.info("Next schedule date : " + fmt.format(calendar.getTime()));
		}
		return calendar.getTime();
	}

}
