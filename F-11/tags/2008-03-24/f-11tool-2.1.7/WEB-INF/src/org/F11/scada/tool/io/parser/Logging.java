/*
 * �쐬��: 2005/10/27
 *
 * TODO ���̐������ꂽ�t�@�C���̃e���v���[�g��ύX����ɂ͎��փW�����v:
 * �E�B���h�E - �ݒ� - Java - �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
package org.F11.scada.tool.io.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * 
 * TODO ���̐������ꂽ�^�R�����g�̃e���v���[�g��ύX����ɂ͎��փW�����v: �E�B���h�E - �ݒ� - Java - �R�[�h�E�X�^�C�� -
 * �R�[�h�E�e���v���[�g
 */
public class Logging {
    private Map tasks = new HashMap();

    public void addTask(Task task) {
        tasks.put(task.getName(), task);
    }

    public Task getTask(String name) {
        return (Task) tasks.get(name);
    }

    public boolean containsKey(String name) {
    	return tasks.containsKey(name);
    }
}