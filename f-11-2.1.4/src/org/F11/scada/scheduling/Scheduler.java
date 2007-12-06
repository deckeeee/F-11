package org.F11.scada.scheduling;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;



/**
 * �C�ӂ̊Ԋu�Ń^�X�N�����s����X�P�W���[���[�N���X�ł��B
 * ���̃N���X�̓X���b�h�Z�[�t�ł��F���������邱�Ɩ����ɁA������ Schedule �I�u�W�F�N�g�����L���邱�Ƃ��\�ł��B
 * 
 * ���̃N���X�� java.util.Timer �N���X���g�p���Ă��܂�
 */
public class Scheduler {

	/**
	 * �^�X�N�����s����C���i�[�N���X�ł��B
	 * �w�肳�ꂽ�����ɂȂ�ƁA�^�X�N�� run ���\�b�h�����s�� ScheduleIterator �ɂčăX�P�W���[�����������s���܂��B
	 */
    class SchedulerTimerTask extends TimerTask {
    	/** ���s�^�X�N */
        private SchedulerTask schedulerTask;
        /** ���̓�����Ԃ� ScheduleIterator */
        private ScheduleIterator iterator;

        /**
         * ���s�^�X�N�� ScheduleIterator �ŃI�u�W�F�N�g�����������܂�
         * @param schedulerTask ���s�^�X�N
         * @param iterator ScheduleIterator �I�u�W�F�N�g
         */
        public SchedulerTimerTask(SchedulerTask schedulerTask,
                ScheduleIterator iterator) {
            this.schedulerTask = schedulerTask;
            this.iterator = iterator;
        }

		/**
		 * ���s���e���������A�ăX�P�W���[�����܂�
		 */
        public void run() {
            schedulerTask.run();
            reschedule(schedulerTask, iterator);
        }
    }

	/** �X�P�W���[���Ɏg�p���� Timer �I�u�W�F�N�g */
    private final Timer timer = new Timer();

	/**
	 * �R���X�g���N�^
	 */
    public Scheduler() {}

	/**
	 * �o�^���ꂽ�^�X�N��������A���̃X�P�W���[���[�𒆎~���܂��B
	 * �������s���̃^�X�N���������ꍇ�́A�I������܂ő҂��Ă����~���܂��B
	 * ���̃��\�b�h��2��ȏ�Ăяo���ꂽ���͉����N����܂���B
	 */
    public void cancel() {
        timer.cancel();
    }

	/**
	 * ���s�^�X�N���C�e���[�^�[�Œ�`���ꂽ�Ԋu�Ŏ��s���܂��B
	 * @param schedulerTask ���s�X�P�W���[��
	 * @param iterator ���s�Ԋu���`�����C�e���[�^�[
	 * @throws IllegalStateException ���ɑ��݂�����̂��X�P�W���[�����悤�Ƃ������A���͒��~���ꂽ�X�P�W���[����o�^���悤�Ƃ�����
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
	 * ���s�^�X�N���C�e���[�^�[�Œ�`���ꂽ�Ԋu�Ŏ��s���܂��B
	 * @param schedulerTask ���s�X�P�W���[��
	 * @throws IllegalStateException ���ɑ��݂�����̂��X�P�W���[�����悤�Ƃ������A���͒��~���ꂽ�X�P�W���[����o�^���悤�Ƃ�����
	 */
	public void schedule(Schedule schedule) {
		schedule(schedule.getTask(), schedule.getScheduleIterator());
	}

	/**
	 * �ăX�P�W���[������
	 * @param schedulerTask ���s�^�X�N
	 * @param iterator �C�e���[�^�[
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
