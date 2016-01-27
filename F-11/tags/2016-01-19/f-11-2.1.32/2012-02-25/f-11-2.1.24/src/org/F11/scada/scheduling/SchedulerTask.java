package org.F11.scada.scheduling;

import java.util.TimerTask;


/**
 * �X�P�W���[���[�N���X�Ŏ��s����^�X�N�N���X�ł��B
 */
public abstract class SchedulerTask implements Runnable {
	/** �������b�N */
    final Object lock = new Object();

	/** ���s�^�X�N�̏�� */
    int state = VIRGIN;
    /** ���X�P�W���[�� */
    static final int VIRGIN = 0;
    /** �X�P�W���[������ */
    static final int SCHEDULED = 1;
    /** ���~���� */
    static final int CANCELLED = 2;
	/** �^�X�N�N���X */
    TimerTask timerTask;

	/**
	 * ���s�^�X�N�𐶐����܂��B
	 */
    protected SchedulerTask() {}

	/**
	 * �X�P�W���[�����s�������������܂�
	 */
    public abstract void run();

	/**
	 * ���̎��s�^�X�N�𒆎~���܂�
	 * <p>
	 * ���̃��\�b�h��2��ȏ�Ăяo����Ă��������܂���B
	 * @return ���̃^�X�N���X�P�W���[�������������� true ��Ԃ��܂�
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
	 * ���̃^�X�N���ŋ߁u���ۂɁv���s����悤�Ɂu�X�P�W���[�����ꂽ�v���s���Ԃ�Ԃ��܂� (���̃��\�b�h���^�X�N�̎��s���ɌĂяo���ꂽ�ꍇ�A�߂�l�͐i�s���̃^�X�N���s�̃X�P�W���[�����ꂽ���s���ԂɂȂ�܂�)�B 
	 * @return ���̃^�X�N�̎��s���ŋ߃X�P�W���[�����ꂽ���ԁBDate.getTime() �ŕԂ����`���B�^�X�N���܂����s����Ă��Ȃ��ꍇ�A�߂�l�͖���`
	 */
    public long scheduledExecutionTime() {
        synchronized(lock) {
            return timerTask == null ? 0 : timerTask.scheduledExecutionTime();
        }
    }

}
