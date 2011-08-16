/*
 * Created on 2003/02/21
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package org.F11.scada.xwife.applet;

import java.util.EventObject;

/**
 * ページ切換イベントです。
 * 
 * @author hori
 */
public class PageChangeEvent extends EventObject {
	private static final long serialVersionUID = 2778173152004323742L;
	/** 切替えページのキーです */
	private final String key;
	/** 自動切替えモードのフラグです */
	private final boolean auto;
	/** 頁履歴からの頁切替モードのフラグ */
	private final boolean history;
	/** ページ呼び出しの引数 */
	private Object argv;

	/**
	 * コンストラクタ
	 * 
	 * @param source ソースオブジェクト
	 * @param key 切替えページのキーです
	 * @param auto 自動切替えモードのフラグです
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
	 * 切替えページのキーを返します
	 * 
	 * @return String 切替えページのキー
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 切換えモードを返します
	 * 
	 * @return boolean 自動切換えの場合 true
	 */
	public boolean isAuto() {
		return auto;
	}

	/**
	 * 頁履歴からのイベントかどうかを返します。
	 * 
	 * @return
	 */
	public boolean isHistory() {
		return history;
	}

	/**
	 * ページ呼び出しの引数を返します。
	 * 
	 * @return ページ呼び出しの引数
	 */
	public Object getArgv() {
		return argv;
	}

	public String toString() {
		return super.toString() + ", key=" + key + ", auto=" + auto
				+ ", history=" + history + ", args=" + argv;
	}
}
