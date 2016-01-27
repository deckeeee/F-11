package org.F11.scada.scheduling;

import java.util.Date;

/**
 * スケジュール・イテレーターのインターフェイスです。
 * スケジュールの間隔を java.util.Date オブジェクトで返すように実装します。
 */
public interface ScheduleIterator {
    
	/**
	 * 次に {@link SchedulerTask} を実行する日時を返します。
	 * @return 次に実行する日時。
	 */
	public Date next();
}
