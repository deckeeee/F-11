/*
 * �쐬��: 2005/10/27
 *
 * TODO ���̐������ꂽ�t�@�C���̃e���v���[�g��ύX����ɂ͎��փW�����v:
 * �E�B���h�E - �ݒ� - Java - �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
 */
package org.F11.scada.tool.logdata;

import org.F11.scada.data.ConvertValue;


public class PointItemConverter {
    private String provider;
    private String holder;

    private String pointunit;
    private String pointname;
    private String pointunit_mark;

    private ConvertValue convertValue;

    /**
     * @return convertValue ��߂��܂��B
     */
    public ConvertValue getConvertValue() {
        return convertValue;
    }

    /**
     * @param convertValue
     *            convertValue ��ݒ�B
     */
    public void setConvertValue(ConvertValue convertValue) {
        this.convertValue = convertValue;
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
     * @return pointname ��߂��܂��B
     */
    public String getPointname() {
        return pointname;
    }

    /**
     * @param pointname
     *            pointname ��ݒ�B
     */
    public void setPointname(String pointname) {
        this.pointname = pointname;
    }

    /**
     * @return pointunit ��߂��܂��B
     */
    public String getPointunit() {
        return pointunit;
    }

    /**
     * @param pointunit
     *            pointunit ��ݒ�B
     */
    public void setPointunit(String pointunit) {
        this.pointunit = pointunit;
    }

    /**
     * @return pointunit_mark ��߂��܂��B
     */
    public String getPointunit_mark() {
        return pointunit_mark;
    }

    /**
     * @param pointunit_mark
     *            pointunit_mark ��ݒ�B
     */
    public void setPointunit_mark(String pointunit_mark) {
        this.pointunit_mark = pointunit_mark;
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
}