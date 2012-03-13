package org.F11.scada.server.logging;

/*
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002 Freedom, Inc. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.F11.scada.scheduling.DailyIterator;
import org.F11.scada.scheduling.Schedule;
import org.F11.scada.scheduling.Scheduler;
import org.F11.scada.scheduling.SchedulerTask;
import org.F11.scada.util.ThreadUtil;
import org.apache.log4j.Logger;

/**
 * ロギングスケジュールクラスです。 このクラスは抽象クラスで、各具象クラスのインスタンスを得るには、 公開されたフィールドを使用します。
 */
public abstract class LoggingSchedule {
	private static Logger log = Logger.getLogger(LoggingSchedule.class);
	/** 分間隔のロギングスケジュール。 */
	public static final LoggingSchedule MINUTE = new MinuteSchedule();
	/** 10分間隔のロギングスケジュール。 */
	public static final LoggingSchedule TENMINUTE = new TenMinuteSchedule();
	/** 時間隔のロギングスケジュール */
	public static final LoggingSchedule HOUR = new HourSchedule();
	/** 時間隔のロギングスケジュール(CSV出力が当日) */
	public static final LoggingSchedule TDHOUR = new HourSchedule();
	/** 日間隔のロギングスケジュール */
	public static final LoggingSchedule DAILY = new DailySchedule();
	/** 日間隔のロギングスケジュール(CSV出力が当月1～末日) */
	public static final LoggingSchedule TMDAILY = new DailySchedule();
	/** 月間隔のロギングスケジュール */
	public static final LoggingSchedule MONTHLY = new MonthlySchedule();
	/** 月間隔のロギングスケジュール(CSV出力が前年4～3月)  */
	public static final LoggingSchedule MONTHLY4 = new MonthlySchedule();
	/** 月間隔のロギングスケジュール(CSV出力が本年4～3月)  */
	public static final LoggingSchedule TYMONTHLY4 = new MonthlySchedule();
	/** 年間隔のロギングスケジュール */
	public static final LoggingSchedule YEARLY = new YearlySchedule();
	/** 一定間隔のロギングスケジュール */
	public static final LoggingSchedule REGULAR = new RegularSchedule();
	/** 1分毎ロギングスケジュール */
	public static final LoggingSchedule ONEMINUTE = new OneMinuteSchedule();
	/** 1秒毎ロギングスケジュール */
	public static final LoggingSchedule ONESECOND = new OneSecondSchedule();
	/** BMS(1分毎)ロギングスケジュール */
	public static final LoggingSchedule BMS = new OneMinuteSchedule();
	/** 分間隔のロギングスケジュール。 */
	public static final LoggingSchedule MINUTEHOUROUT = new MinuteSchedule();
	/** 15分間隔のロギングスケジュール */
	public static final LoggingSchedule QMINUTE = new QMinuteSchedule();
	public static final LoggingSchedule FIVEMINUTE = new WaitMinuteSchedule(5L);
	public static final LoggingSchedule THIRTYMINUTE = new WaitMinuteSchedule(
		30L);
	public static final LoggingSchedule SIXTYMINUTE = new WaitMinuteSchedule(
		60L);
	/** 1分毎ロギング月出力スケジュール */
	public static final LoggingSchedule ONEHOURMONTHOUT = new HourSchedule();
	public static final LoggingSchedule ONEHOURMONTHOUT2 = new HourSchedule();

	/** 1月分/月ロギング出力スケジュール(吉本専用) */
	public static final LoggingSchedule MONTHLYMONTHOUT = new MonthlySchedule();

	/** GODA(10分間隔)のロギングスケジュール。 */
	public static final LoggingSchedule GODA = new TenMinuteSchedule();
	public static final LoggingSchedule GODA01 = new OneMinuteSchedule();
	public static final LoggingSchedule GODA05 = FIVEMINUTE;
	public static final LoggingSchedule GODA10 = GODA;
	public static final LoggingSchedule GODA30 = THIRTYMINUTE;
	public static final LoggingSchedule GODA60 = SIXTYMINUTE;

	/** 30分ロギングの7時30分出力のスケジュール(関電専用) **/
	public static final LoggingSchedule KANDEN = HOUR;
	public static final LoggingSchedule NEWDAILY = new NewDailySchedule();
	public static final LoggingSchedule EIGHTHOUR = HOUR;

	/** 最後にタスクを実行したカレンダーです。 */
	protected Calendar startDate;

	/**
	 * コンストラクタ。
	 */
	protected LoggingSchedule() {
		startDate = Calendar.getInstance();
	}

	/**
	 * TimerTask をスケジュールします。 ロギングの間隔は各スケジュールクラスの間隔になります。具体的には、MinuteSchedule は
	 * 秒が 0 になる毎、HourSchedule は分が 0 になる毎にロギングを開始します。
	 *
	 * @param task TimerTask
	 */
	abstract public void add(TimerTask task);

	/**
	 * TimerTask をスケジュールします。
	 * <p>
	 * ロギングの間隔は各スケジュールクラスの間隔になります。具体的には、MinuteSchedule は 秒が 0 + offset
	 * になる毎、HourSchedule は分が 0 + offset になる毎にロギングを開始します。
	 * </p>
	 * <p>
	 * offset は各スケジュール毎に単位が異なります。ロギングスケジュールの単位の、下を 表す単位が offset の単位となります。具体的には
	 * MinuteSchedule では秒のオフセットを指定し、 HourSchedule では分のオフセットを指定します。
	 * </p>
	 *
	 * @param task タスク
	 * @param offset オフセット
	 */
	abstract public void add(TimerTask task, int offset);

	/**
	 * TimerTask をスケジュールします。
	 * <p>
	 * ロギングの間隔は各スケジュールクラスの間隔になります。具体的には、MinuteSchedule は 秒が 0 + offset
	 * になる毎、HourSchedule は分が 0 + offset になる毎にロギングを開始します。
	 * </p>
	 * <p>
	 * offset はどのスケジュールでもミリ秒を指定します。
	 * </p>
	 *
	 * @param task タスク
	 * @param offset オフセット
	 */
	abstract public void addMilliOffset(TimerTask task, int offset);

	/**
	 * オフセット値のチェックをします。
	 *
	 * @param offset オフセット値
	 */
	abstract protected void checkOffset(int offset);

	/**
	 * 時・分間隔のロギングスケジュールの基底クラスです。
	 */
	private abstract static class TimerSchedule extends LoggingSchedule {
		private Timer timer;

		TimerSchedule() {
			super();
		}

		public void add(TimerTask task) {
			add(task, 0);
		}

		public void add(TimerTask task, int offset) {
			checkOffset(offset);
			if (timer == null) {
				timer = new Timer();
			}
			scheduleAtFixedRate(task, offset, timer);
		}

		public void addMilliOffset(TimerTask task, int offset) {
			if (timer == null) {
				timer = new Timer();
			}
			scheduleAtFixedRateMilliOffset(task, offset, timer);
		}

		protected void checkOffset(int offset) {
			if (offset < 0 || offset > 59) {
				throw new IllegalArgumentException("offset range error : "
					+ offset);
			}
		}

		abstract protected void scheduleAtFixedRate(TimerTask task,
				int offset,
				Timer timer);

		abstract protected void scheduleAtFixedRateMilliOffset(TimerTask task,
				int offset,
				Timer timer);

	}

	/**
	 * 分間隔のロギングスケジュールの実装です。
	 */
	private static class MinuteSchedule extends TimerSchedule {
		private static final long period = 1000L * 60L;

		MinuteSchedule() {
			super();
		}

		protected void scheduleAtFixedRate(TimerTask task,
				int offset,
				Timer timer) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.SECOND, offset);
			timer.scheduleAtFixedRate(task, cal.getTime(), period);
		}

		protected void scheduleAtFixedRateMilliOffset(TimerTask task,
				int offset,
				Timer timer) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, offset);
			timer.scheduleAtFixedRate(task, cal.getTime(), period);
		}
	}

	/**
	 * 10分間隔のロギングスケジュールの実装です。
	 */
	private static class TenMinuteSchedule extends TimerSchedule {
		private static final long period = 1000L * 60L * 10L;

		TenMinuteSchedule() {
			super();
		}

		protected void scheduleAtFixedRate(TimerTask task,
				int offset,
				Timer timer) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, offset);
			int minute = cal.get(Calendar.MINUTE);
			if (minute % 10 != 0) {
				minute += 10 - (minute % 10);
			}
			cal.set(Calendar.MINUTE, minute);
			timer.scheduleAtFixedRate(task, cal.getTime(), period);
		}

		protected void scheduleAtFixedRateMilliOffset(TimerTask task,
				int offset,
				Timer timer) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, offset);
			cal.set(Calendar.SECOND, 0);
			int minute = cal.get(Calendar.MINUTE);
			if (minute % 10 != 0) {
				minute += 10 - (minute % 10);
			}
			cal.set(Calendar.MINUTE, minute);
			timer.scheduleAtFixedRate(task, cal.getTime(), period);
		}
	}

	/**
	 * 1分毎のロギングスケジュールの実装です。
	 */
	private static class OneMinuteSchedule extends TimerSchedule {
		private static final long period = 1000L * 60L;

		OneMinuteSchedule() {
			super();
		}

		protected void scheduleAtFixedRate(TimerTask task,
				int offset,
				Timer timer) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 1);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 0);
			timer.scheduleAtFixedRate(task, cal.getTime(), period);
		}

		protected void scheduleAtFixedRateMilliOffset(TimerTask task,
				int offset,
				Timer timer) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 1);
			cal.set(Calendar.MILLISECOND, offset);
			cal.set(Calendar.SECOND, 0);
			timer.scheduleAtFixedRate(task, cal.getTime(), period);
		}
	}

	/**
	 * 1秒毎のロギングスケジュールの実装です。
	 */
	private static class OneSecondSchedule extends TimerSchedule {
		private static final long period = 1000L;

		OneSecondSchedule() {
			super();
		}

		protected void scheduleAtFixedRate(TimerTask task,
				int offset,
				Timer timer) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.SECOND, 1);
			timer.scheduleAtFixedRate(task, cal.getTime(), period);
		}

		protected void scheduleAtFixedRateMilliOffset(TimerTask task,
				int offset,
				Timer timer) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, offset);
			cal.add(Calendar.SECOND, 1);
			timer.scheduleAtFixedRate(task, cal.getTime(), period);
		}
	}

	/**
	 * 時間隔のロギングスケジュールの実装です。
	 */
	private static class HourSchedule extends TimerSchedule {
		private static final long period = 1000L * 60L * 60L;

		HourSchedule() {
			super();
		}

		protected void scheduleAtFixedRate(TimerTask task,
				int offset,
				Timer timer) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.SECOND, 0);
			if (offset < cal.get(Calendar.MINUTE)) {
				cal.add(Calendar.HOUR_OF_DAY, 1);
			}
			cal.set(Calendar.MINUTE, offset + 1);
			timer.scheduleAtFixedRate(task, cal.getTime(), period);
		}

		protected void scheduleAtFixedRateMilliOffset(TimerTask task,
				int offset,
				Timer timer) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.SECOND, 0);
			if (toMinute(offset) < cal.get(Calendar.MINUTE)) {
				cal.add(Calendar.HOUR_OF_DAY, 1);
			}
			cal.set(Calendar.MINUTE, 1);
			cal.set(Calendar.MILLISECOND, offset);
			timer.scheduleAtFixedRate(task, cal.getTime(), period);
		}

		private int toMinute(int offset) {
			return offset / 60000;
		}
	}

	/**
	 * 15分間隔のロギングスケジュールの実装です。
	 */
	private static class QMinuteSchedule extends TimerSchedule {
		private static final long qminite = 15L;
		private static final long period = 1000L * 60L * qminite;

		QMinuteSchedule() {
			super();
		}

		protected void scheduleAtFixedRate(TimerTask task,
				int offset,
				Timer timer) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, offset);
			int minute = cal.get(Calendar.MINUTE);
			if (minute % qminite != 0) {
				minute += qminite - (minute % qminite);
			}
			cal.set(Calendar.MINUTE, minute);
			timer.scheduleAtFixedRate(task, cal.getTime(), period);
		}

		protected void scheduleAtFixedRateMilliOffset(TimerTask task,
				int offset,
				Timer timer) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, offset);
			cal.set(Calendar.SECOND, 0);
			int minute = cal.get(Calendar.MINUTE);
			if (minute % qminite != 0) {
				minute += qminite - (minute % qminite);
			}
			cal.set(Calendar.MINUTE, minute);
			timer.scheduleAtFixedRate(task, cal.getTime(), period);
		}
	}

	/**
	 * X分間隔のロギングスケジュールの実装です。
	 */
	private static class WaitMinuteSchedule extends TimerSchedule {
		private final long waitMinite;
		private final long period;

		WaitMinuteSchedule(long waitMinite) {
			super();
			this.waitMinite = waitMinite;
			period = 1000L * 60L * waitMinite;
		}

		protected void scheduleAtFixedRate(TimerTask task,
				int offset,
				Timer timer) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, offset);
			int minute = cal.get(Calendar.MINUTE);
			if (minute % waitMinite != 0) {
				minute += waitMinite - (minute % waitMinite);
			}
			cal.set(Calendar.MINUTE, minute);
			timer.scheduleAtFixedRate(task, cal.getTime(), period);
		}

		protected void scheduleAtFixedRateMilliOffset(TimerTask task,
				int offset,
				Timer timer) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, offset);
			cal.set(Calendar.SECOND, 0);
			int minute = cal.get(Calendar.MINUTE);
			if (minute % waitMinite != 0) {
				minute += waitMinite - (minute % waitMinite);
			}
			cal.set(Calendar.MINUTE, minute);
			timer.scheduleAtFixedRate(task, cal.getTime(), period);
		}
	}

	/**
	 * 日・月・年間隔のロギングスケジュールの基底クラスです。
	 */
	private static abstract class ScheduleThread extends LoggingSchedule
			implements Runnable {
		/** スリープ時間 */
		protected long SLEEP_TIME = 1000L;
		/** タスクのリストです */
		protected List scheduleList;
		/** スレッド */
		protected Thread thread;

		protected ScheduleThread() {
			super();
			thread = new Thread(this);
			thread.setName(getClass().getName());
			thread.start();
		}

		public void add(TimerTask task) {
			add(task, 0);
		}

		public void add(TimerTask task, int offset) {
			checkOffset(offset);
			if (scheduleList == null) {
				scheduleList = Collections.synchronizedList(new LinkedList());
			}
			scheduleList.add(new ScheduleThreadTask(task, offset));
		}

		public void addMilliOffset(TimerTask task, int offset) {
			if (scheduleList == null) {
				scheduleList = Collections.synchronizedList(new LinkedList());
			}
			scheduleList.add(new ScheduleThreadTask(task, offset, true));
		}

		protected void sleep() {
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException ex) {
			}
		}

		/**
		 * 補助クラスです。
		 */
		protected static class ScheduleThreadTask {
			private final TimerTask task;
			private final int offset;
			private final boolean isMilliSecondMode;

			ScheduleThreadTask(TimerTask task, int offset) {
				this(task, offset, false);
			}

			ScheduleThreadTask(TimerTask task,
					int offset,
					boolean isMilliSecondMode) {
				this.task = task;
				this.offset = offset;
				this.isMilliSecondMode = isMilliSecondMode;
			}

			TimerTask getTimerTask() {
				return task;
			}

			int getOffset() {
				return offset;
			}

			public boolean isMilliSecondMode() {
				return isMilliSecondMode;
			}

			public String toString() {
				return "task="
					+ task
					+ ", offset="
					+ offset
					+ ", isMillisecondMode="
					+ isMilliSecondMode;
			}
		}
	}

	/**
	 * 日間隔のロギングスケジュールの実装です。
	 */
	private static class DailySchedule extends ScheduleThread {
		DailySchedule() {
			super();
		}

		public void run() {
			Thread ct = Thread.currentThread();
			while (thread == ct) {
				if (scheduleList == null) {
					sleep();
					continue;
				}

				Calendar nowDate = Calendar.getInstance();
				if (nowDate.get(Calendar.MINUTE) < 1) {
					sleep();
					continue;
				}
				int nd = nowDate.get(Calendar.DATE);
				int nh = nowDate.get(Calendar.HOUR);
				synchronized (scheduleList) {
					boolean execute = false;
					for (Iterator i = scheduleList.iterator(); i.hasNext();) {
						ScheduleThreadTask scheduleThreadTask =
							(ScheduleThreadTask) i.next();
						int sd = startDate.get(Calendar.DATE);
						int sh = scheduleThreadTask.getOffset();
						if (scheduleThreadTask.isMilliSecondMode) {
							if (sd != nd) {
								ThreadUtil.sleep(sh);
								scheduleThreadTask.getTimerTask().run();
								execute = true;
							}
						} else {
							if (sd != nd && sh <= nh) {
								scheduleThreadTask.getTimerTask().run();
								execute = true;
							}
						}
					}
					if (execute) {
						startDate = nowDate;
					}
				}
				sleep();
			}
		}

		protected void checkOffset(int offset) {
			if (offset < 0 || offset > 23) {
				throw new IllegalArgumentException("offset range error : "
					+ offset);
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.F11.scada.server.logging.LoggingSchedule#add(java.util.TimerTask,
		 * int)
		 */
		public void add(TimerTask task, int offset) {
			super.add(task, offset);
		}
	}

	/**
	 * 月間隔のロギングスケジュールの実装です。
	 */
	private static class MonthlySchedule extends ScheduleThread {

		MonthlySchedule() {
			super();
		}

		public void run() {
			Thread ct = Thread.currentThread();
			while (thread == ct) {
				if (scheduleList == null) {
					sleep();
					continue;
				}

				Calendar nowDate = Calendar.getInstance();
				if (nowDate.get(Calendar.MINUTE) < 1) {
					sleep();
					continue;
				}
				int nm = nowDate.get(Calendar.MONTH);
				int nd = nowDate.get(Calendar.DATE);
				synchronized (scheduleList) {
					boolean execute = false;
					for (Iterator i = scheduleList.iterator(); i.hasNext();) {
						ScheduleThreadTask scheduleThreadTask =
							(ScheduleThreadTask) i.next();
						int sm = startDate.get(Calendar.MONTH);
						int sd = scheduleThreadTask.getOffset();
						if (scheduleThreadTask.isMilliSecondMode) {
							if (sm != nm) {
								ThreadUtil.sleep(sd);
								scheduleThreadTask.getTimerTask().run();
								execute = true;
							}
						} else {
							if (sm != nm && sd <= nd) {
								scheduleThreadTask.getTimerTask().run();
								execute = true;
							}
						}
					}
					if (execute) {
						startDate = nowDate;
					}
				}
				sleep();
			}
		}

		protected void checkOffset(int offset) {
			if (offset < 0 || offset > 31) {
				throw new IllegalArgumentException("offset range error : "
					+ offset);
			}
		}
	}

	/**
	 * 年間隔のロギングスケジュールの実装です。
	 */
	private static class YearlySchedule extends ScheduleThread {
		YearlySchedule() {
			super();
		}

		public void run() {
			Thread ct = Thread.currentThread();
			while (thread == ct) {
				if (scheduleList == null) {
					sleep();
					continue;
				}

				Calendar nowDate = Calendar.getInstance();
				if (nowDate.get(Calendar.MINUTE) < 1) {
					sleep();
					continue;
				}
				int ny = nowDate.get(Calendar.YEAR);
				int nm = nowDate.get(Calendar.MONTH);
				synchronized (scheduleList) {
					boolean execute = false;
					for (Iterator i = scheduleList.iterator(); i.hasNext();) {
						ScheduleThreadTask scheduleThreadTask =
							(ScheduleThreadTask) i.next();
						int sy = startDate.get(Calendar.YEAR);
						int sm = scheduleThreadTask.getOffset();
						if (scheduleThreadTask.isMilliSecondMode) {
							if (sy < ny) {
								ThreadUtil.sleep(sm);
								scheduleThreadTask.getTimerTask().run();
								execute = true;
							}
						} else {
							if (sy < ny && sm <= nm) {
								scheduleThreadTask.getTimerTask().run();
								execute = true;
							}
						}
					}
					if (execute) {
						startDate = nowDate;
					}
				}
				sleep();
			}
		}

		protected void checkOffset(int offset) {
			if (offset < 0 || offset > 11) {
				throw new IllegalArgumentException("offset range error : "
					+ offset);
			}
		}
	}

	/**
	 * 一定間隔のロギングスケジュールの実装です。
	 */
	private static class RegularSchedule extends LoggingSchedule {
		private Timer timer;

		RegularSchedule() {
			super();
		}

		public void add(TimerTask task) {
			throw new UnsupportedOperationException(
				"Must use \"add(TimerTask task, int offset)\" method.");
		}

		public void add(TimerTask task, int offset) {
			checkOffset(offset);
			if (timer == null) {
				timer = new Timer();
			}
			timer.schedule(task, 0, offset * 1000L);
		}

		public void addMilliOffset(TimerTask task, int offset) {
			if (timer == null) {
				timer = new Timer();
			}
			timer.schedule(task, 0, offset);
		}

		protected void checkOffset(int offset) {
			if (offset < Integer.MIN_VALUE || offset > Integer.MAX_VALUE) {
				throw new IllegalArgumentException("offset range error : "
					+ offset);
			}
		}
	}

	private static class NewDailySchedule extends LoggingSchedule {
		private final Scheduler scheduler = new Scheduler();

		@Override
		public void add(TimerTask task) {
			DailyIterator dailyIterator = new DailyIterator(0, 1, 0);
			final TimerTask t = task;
			SchedulerTask schedulerTask = new SchedulerTask() {
				@Override
				public void run() {
					t.run();
				}
			};
			Schedule schedule = new Schedule(schedulerTask, dailyIterator);
			scheduler.schedule(schedule);
		}

		@Override
		public void add(TimerTask task, int offset) {
			DailyIterator dailyIterator = new DailyIterator(offset, 1, 0);
			final TimerTask t = task;
			SchedulerTask schedulerTask = new SchedulerTask() {
				@Override
				public void run() {
					t.run();
				}
			};
			Schedule schedule = new Schedule(schedulerTask, dailyIterator);
			scheduler.schedule(schedule);
		}

		@Override
		public void addMilliOffset(TimerTask task, int offset) {
			throw new IllegalArgumentException("NEWDAILYはmilliOffsetモードを使えません。");
		}

		@Override
		protected void checkOffset(int offset) {
		}
	}

	/**
	 * テストメインクラス
	 *
	 * @param argv コマンド引数
	 */
	public static void main(String[] argv) {
		LoggingSchedule.FIVEMINUTE.addMilliOffset(new PrintTask(
			"5MinuteSchedule"), 0);
		LoggingSchedule.THIRTYMINUTE.addMilliOffset(new PrintTask(
			"30MinuteSchedule"), 0);
		LoggingSchedule.SIXTYMINUTE.addMilliOffset(new PrintTask(
			"60MiniteSchedule"), 0);
	}

	private static class PrintTask extends TimerTask {
		private String display;

		PrintTask(String display) {
			super();
			this.display = display;
		}

		public void run() {
			Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			System.out.println("["
				+ display
				+ "] Now : "
				+ format.format(new Date()));
		}
	}
}
