package org.F11.scada.scheduling;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;



/**
 * 任意の間隔でタスクを実行するスケジューラークラスです。
 * このクラスはスレッドセーフです：同期化すること無しに、複数の Schedule オブジェクトを共有することが可能です。
 * 
 * このクラスは java.util.Timer クラスを使用しています
 */
public class Scheduler {

	/**
	 * タスクを実行するインナークラスです。
	 * 指定された日時になると、タスクの run メソッドを実行し ScheduleIterator にて再スケジュール処理を実行します。
	 */
    class SchedulerTimerTask extends TimerTask {
    	/** 実行タスク */
        private SchedulerTask schedulerTask;
        /** 次の日時を返す ScheduleIterator */
        private ScheduleIterator iterator;

        /**
         * 実行タスクと ScheduleIterator でオブジェクトを初期化します
         * @param schedulerTask 実行タスク
         * @param iterator ScheduleIterator オブジェクト
         */
        public SchedulerTimerTask(SchedulerTask schedulerTask,
                ScheduleIterator iterator) {
            this.schedulerTask = schedulerTask;
            this.iterator = iterator;
        }

		/**
		 * 実行内容を処理し、再スケジュールします
		 */
        public void run() {
            schedulerTask.run();
            reschedule(schedulerTask, iterator);
        }
    }

	/** スケジュールに使用する Timer オブジェクト */
    private final Timer timer = new Timer();

	/**
	 * コンストラクタ
	 */
    public Scheduler() {}

	/**
	 * 登録されたタスクを取消し、このスケジューラーを中止します。
	 * もし実行中のタスクがあった場合は、終了するまで待ってから停止します。
	 * このメソッドが2回以上呼び出された時は何も起こりません。
	 */
    public void cancel() {
        timer.cancel();
    }

	/**
	 * 実行タスクをイテレーターで定義された間隔で実行します。
	 * @param schedulerTask 実行スケジュール
	 * @param iterator 実行間隔を定義したイテレーター
	 * @throws IllegalStateException 既に存在するものをスケジュールしようとした時、又は中止されたスケジュールを登録しようとした時
	 */
    private void schedule(SchedulerTask schedulerTask,
            ScheduleIterator iterator) {

        Date time = iterator.next();
        if (time == null) {
            schedulerTask.cancel();
        } else {
            synchronized(schedulerTask.lock) {
                if (schedulerTask.state != SchedulerTask.VIRGIN) {
                    throw new IllegalStateException("Task already scheduled " +
                        "or cancelled");
                }
                schedulerTask.state = SchedulerTask.SCHEDULED;
                schedulerTask.timerTask =
                    new SchedulerTimerTask(schedulerTask, iterator);
                timer.schedule(schedulerTask.timerTask, time);
            }
        }
    }

	/**
	 * 実行タスクをイテレーターで定義された間隔で実行します。
	 * @param schedulerTask 実行スケジュール
	 * @throws IllegalStateException 既に存在するものをスケジュールしようとした時、又は中止されたスケジュールを登録しようとした時
	 */
	public void schedule(Schedule schedule) {
		schedule(schedule.getTask(), schedule.getScheduleIterator());
	}

	/**
	 * 再スケジュール処理
	 * @param schedulerTask 実行タスク
	 * @param iterator イテレーター
	 */
    private void reschedule(SchedulerTask schedulerTask,
            ScheduleIterator iterator) {

        Date time = iterator.next();
        if (time == null) {
            schedulerTask.cancel();
        } else {
            synchronized(schedulerTask.lock) {
                if (schedulerTask.state != SchedulerTask.CANCELLED) {
                    schedulerTask.timerTask =
                        new SchedulerTimerTask(schedulerTask, iterator);
                    timer.schedule(schedulerTask.timerTask, time);
                }
            }
        }
    }

}
