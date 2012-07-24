package org.F11.scada.scheduling;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 毎日 一定時刻を返すスケジュール・イテレータークラスです
 */
public class DailyIterator implements ScheduleIterator {
	/** 次の日時を計算するカレンダー */
	private final Calendar calendar = Calendar.getInstance();
	/** Logging API */
	private static Logger log = Logger.getLogger(DailyIterator.class);
	/** フォーマッター */
	private final DateFormat fmt = DateFormat.getDateTimeInstance();

	/**
	 * 時・分・秒を指定して、イテレーターを初期化します。
	 *
	 * @param hourOfDay 時
	 * @param minute 分
	 * @param second 秒
	 */
	public DailyIterator(int hourOfDay, int minute, int second) {
		this(hourOfDay, minute, second, new Date());
	}

	/**
	 * 日付・時・分・秒を指定して、イテレーターを初期化します。
	 * 日付オブジェクトの時・分・秒は引数の時・分・秒で上書きされます
	 *
	 * @param hourOfDay 時
	 * @param minute 分
	 * @param second 秒
	 * @param date 日付
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
	 * 日付・時・分・秒を指定して、イテレーターを初期化します。
	 * 日付オブジェクトの時・分・秒は引数の時・分・秒で上書きされます
	 *
	 * @param hourOfDay 時
	 * @param minute 分
	 * @param second 秒
	 * @param date 日付
	 * @param millioffset ミリオフセット
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
	 * 次に実行する日時を返します。
	 * @return 次に実行する日時を返します。
	 */
	public Date next() {
		calendar.add(Calendar.DATE, 1);
		if (log.isInfoEnabled()) {
			log.info("Next schedule date : " + fmt.format(calendar.getTime()));
		}
		return calendar.getTime();
	}
}
