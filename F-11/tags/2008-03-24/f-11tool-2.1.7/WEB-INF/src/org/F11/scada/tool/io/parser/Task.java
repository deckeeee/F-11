/*
 * �쐬��: 2005/10/27
 *
 * TODO ���̐������ꂽ�t�@�C���̃e���v���[�g��ύX����ɂ͎��փW�����v:
 * �E�B���h�E - �ݒ� - Java - �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
package org.F11.scada.tool.io.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Administrator
 * 
 * TODO ���̐������ꂽ�^�R�����g�̃e���v���[�g��ύX����ɂ͎��փW�����v: �E�B���h�E - �ݒ� - Java - �R�[�h�E�X�^�C�� -
 * �R�[�h�E�e���v���[�g
 */
public class Task {
    private List columns = new ArrayList();
    private String name;

    /**
     *  
     */
    public Task() {
        super();
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public Collection getColumns() {
        return columns;
    }

    /**
     * @return name ��߂��܂��B
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            name ��ݒ�B
     */
    public void setName(String name) {
        this.name = name;
    }
}