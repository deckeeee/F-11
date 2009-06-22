package org.F11.scada.scheduling;

import java.util.TimerTask;


/**
 * スケジューラークラスで実行するタスククラスです。
 */
public abstract class SchedulerTask implements Runnable {
	/** 同期ロック */
    final Object lock = new Object();

	/** 実行タスクの状態 */
    int state = VIRGIN;
    /** 未スケジュール */
    static final int VIRGIN = 0;
    /** スケジュール完了 */
    static final int SCHEDULED = 1;
    /** 中止完了 */
    static final int CANCELLED = 2;
	/** タスククラス */
    TimerTask timerTask;

	/**
	 * 実行タスクを生成します。
	 */
    protected SchedulerTask() {}

	/**
	 * スケジュール実行処理を実装します
	 */
    public abstract void run();

	/**
	 * この実行タスクを中止します
	 * <p>
	 * このメソッドは2回以上呼び出されても何もしません。
	 * @return このタスクがスケジュール完了だった時 true を返します
	 */
    public boolean cancel() {
        synchronized(lock) {
            if (timerTask != null) {
                timerTask.cancel();
            }
            boolean result = (state == SCHEDULED);
            state = CANCELLED;
            return result;
        }
    }

	/**
	 * このタスクを最近「実際に」実行するように「スケジュールされた」実行時間を返します (このメソッドがタスクの実行中に呼び出された場合、戻り値は進行中のタスク実行のスケジュールされた実行時間になります)。 
	 * @return このタスクの実行が最近スケジュールされた時間。Date.getTime() で返される形式。タスクがまだ実行されていない場合、戻り値は未定義
	 */
    public long scheduledExecutionTime() {
        synchronized(lock) {
            return timerTask == null ? 0 : timerTask.scheduledExecutionTime();
        }
    }

}
