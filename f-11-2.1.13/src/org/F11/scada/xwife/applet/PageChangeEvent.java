/*
 * Created on 2003/02/21
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.xwife.applet;

import java.util.EventObject;

/**
 * �y�[�W�؊��C�x���g�ł��B
 * 
 * @author hori
 */
public class PageChangeEvent extends EventObject {
	private static final long serialVersionUID = 2778173152004323742L;
	/** �ؑւ��y�[�W�̃L�[�ł� */
	private final String key;
	/** �����ؑւ����[�h�̃t���O�ł� */
	private final boolean auto;
	/** �ŗ�������̕Őؑփ��[�h�̃t���O */
	private final boolean history;
	/** �y�[�W�Ăяo���̈��� */
	private Object argv;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param source �\�[�X�I�u�W�F�N�g
	 * @param key �ؑւ��y�[�W�̃L�[�ł�
	 * @param auto �����ؑւ����[�h�̃t���O�ł�
	 */
	public PageChangeEvent(Object source, String key, boolean auto) {
		this(source, key, auto, false);
	}

	public PageChangeEvent(
			Object source,
			String key,
			boolean auto,
			boolean history) {
		this(source, key, auto, history, null);
	}

	public PageChangeEvent(
			Object source,
			String key,
			boolean auto,
			boolean history,
			Object argv) {
		super(source);
		this.key = key;
		this.auto = auto;
		this.history = history;
		this.argv = argv;
	}

	/**
	 * �ؑւ��y�[�W�̃L�[��Ԃ��܂�
	 * 
	 * @return String �ؑւ��y�[�W�̃L�[
	 */
	public String getKey() {
		return key;
	}

	/**
	 * �؊������[�h��Ԃ��܂�
	 * 
	 * @return boolean �����؊����̏ꍇ true
	 */
	public boolean isAuto() {
		return auto;
	}

	/**
	 * �ŗ�������̃C�x���g���ǂ�����Ԃ��܂��B
	 * 
	 * @return
	 */
	public boolean isHistory() {
		return history;
	}

	/**
	 * �y�[�W�Ăяo���̈�����Ԃ��܂��B
	 * 
	 * @return �y�[�W�Ăяo���̈���
	 */
	public Object getArgv() {
		return argv;
	}

	public String toString() {
		return super.toString() + ", key=" + key + ", auto=" + auto
				+ ", history=" + history + ", args=" + argv;
	}
}
