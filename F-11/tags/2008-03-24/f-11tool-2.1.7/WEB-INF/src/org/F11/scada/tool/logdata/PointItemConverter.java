/*
 * 作成日: 2005/10/27
 *
 * TODO この生成されたファイルのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
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
     * @return convertValue を戻します。
     */
    public ConvertValue getConvertValue() {
        return convertValue;
    }

    /**
     * @param convertValue
     *            convertValue を設定。
     */
    public void setConvertValue(ConvertValue convertValue) {
        this.convertValue = convertValue;
    }

    /**
     * @return holder を戻します。
     */
    public String getHolder() {
        return holder;
    }

    /**
     * @param holder
     *            holder を設定。
     */
    public void setHolder(String holder) {
        this.holder = holder;
    }

    /**
     * @return pointname を戻します。
     */
    public String getPointname() {
        return pointname;
    }

    /**
     * @param pointname
     *            pointname を設定。
     */
    public void setPointname(String pointname) {
        this.pointname = pointname;
    }

    /**
     * @return pointunit を戻します。
     */
    public String getPointunit() {
        return pointunit;
    }

    /**
     * @param pointunit
     *            pointunit を設定。
     */
    public void setPointunit(String pointunit) {
        this.pointunit = pointunit;
    }

    /**
     * @return pointunit_mark を戻します。
     */
    public String getPointunit_mark() {
        return pointunit_mark;
    }

    /**
     * @param pointunit_mark
     *            pointunit_mark を設定。
     */
    public void setPointunit_mark(String pointunit_mark) {
        this.pointunit_mark = pointunit_mark;
    }

    /**
     * @return provider を戻します。
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @param provider
     *            provider を設定。
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }
}