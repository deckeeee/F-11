package org.F11.scada.scheduling;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 毎月 一定時刻を返すスケジュール・イテレータークラスです
 */
public class MonthlyIterator implements ScheduleIterator {
	/** 次の日時を計算するカレンダー */
	private final Calendar calendar = Calendar.getInstance();
	/** Logging API */
	private static Logger log = Logger.getLogger(MonthlyIterator.class);
	/** フォーマッター */
	private final DateFormat fmt = DateFormat.getDateTimeInstance();

	/**
	 * 日付は当日を使用し日・時・分・秒を指定して、イテレーターを初期化します。
	 * 日付には1～28を指定します
	 *
	 * @param day 日
	 * @param hourOfDay 時
	 * @param minute 分
	 * @param second 秒
	 */
	public MonthlyIterator(int day, int hourOfDay, int minute, int second) {
		this(day, hourOfDay, minute, second, new Date());
	}

	/**
	 * 日付・日・時・分・秒を指定して、イテレーターを初期化します。
	 * 日付オブジェクトの日・時・分・秒は引数の時・分・秒で上書きされます
	 * 日付には1～28を指定します
	 *
	 * @param day 日
	 * @param hourOfDay 時
	 * @param minute 分
	 * @param second 秒
	 * @param date 日付
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
	 * 次に実行する日時を返します。
	 * @return 次に実行する日時を返します。
	 */
	public Date next() {
		calendar.add(Calendar.MONTH, 1);
		if (log.isInfoEnabled()) {
			log.info("Next schedule date : " + fmt.format(calendar.getTime()));
		}
		return calendar.getTime();
	}

}
