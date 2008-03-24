/*
 * 作成日: 2005/10/27
 *
 * TODO この生成されたファイルのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
package org.F11.scada.tool.io.parser;

/**
 * @author Administrator
 * 
 * TODO この生成された型コメントのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java - コード・スタイル -
 * コード・テンプレート
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

    public String getProviderHolder() {
        return provider + "_" + holder;
    }
}