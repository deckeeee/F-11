/*
 * �쐬��: 2005/10/27
 *
 * TODO ���̐������ꂽ�t�@�C���̃e���v���[�g��ύX����ɂ͎��փW�����v:
 * �E�B���h�E - �ݒ� - Java - �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
package org.F11.scada.tool.io.parser;

/**
 * @author Administrator
 * 
 * TODO ���̐������ꂽ�^�R�����g�̃e���v���[�g��ύX����ɂ͎��փW�����v: �E�B���h�E - �ݒ� - Java - �R�[�h�E�X�^�C�� -
 * �R�[�h�E�e���v���[�g
 */
public class Column {
    private String provider;
    private String holder;

    public Column() {
    }

    public Column(String provider, String holder) {
    	this.provider = provider;
    	this.holder = holder;
    }

    /**
     * @return holder ��߂��܂��B
     */
    public String getHolder() {
        return holder;
    }

    /**
     * @param holder
     *            holder ��ݒ�B
     */
    public void setHolder(String holder) {
        this.holder = holder;
    }

    /**
     * @return provider ��߂��܂��B
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @param provider
     *            provider ��ݒ�B
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderHolder() {
        return provider + "_" + holder;
    }
}