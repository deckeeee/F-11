package org.F11.scada.scheduling;

import java.util.Date;

/**
 * �X�P�W���[���E�C�e���[�^�[�̃C���^�[�t�F�C�X�ł��B
 * �X�P�W���[���̊Ԋu�� java.util.Date �I�u�W�F�N�g�ŕԂ��悤�Ɏ������܂��B
 */
public interface ScheduleIterator {
    
	/**
	 * ���� {@link SchedulerTask} �����s���������Ԃ��܂��B
	 * @return ���Ɏ��s��������B
	 */
	public Date next();
}
