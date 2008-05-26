/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/parser/alarm/AlarmConfig.java,v 1.5.2.1 2005/01/17 05:57:21 frdm Exp $
 * $Revision: 1.5.2.1 $
 * $Date: 2005/01/17 05:57:21 $
 * 
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002 Freedom, Inc. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.F11.scada.parser.alarm;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 最新情報と警報・状態一覧の設定を保持するクラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AlarmConfig {
	/** 最新情報の設定クラスです */
	private AlarmNewsConfig alarmNewsConfig;
	/** 警報・状態一覧の設定クラスです */
	private AlarmTableConfig alarmTableConfig;
	/** サーバーコネクションエラー　メッセージ */
	private ServerErrorMessage serverErrorMessage;
	/** クライアントのページ設定 */
	private Page page;
	/** クライアントのツールバー設定 */
	private ToolBar toolBar;
	/** 初期設定 */
	private InitConfig initConfig;
	
	private TitleConfig titleConfig;


	/**
	 * このオブジェクトの文字列表現を返します
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * 最新情報の設定を返します
	 * @return 最新情報の設定
	 */
	public AlarmNewsConfig getAlarmNewsConfig() {
		return alarmNewsConfig;
	}

	/**
	 * 最新情報の設定を設定します
	 * @param config 最新情報の設定
	 */
	public void setAlarmNewsConfig(AlarmNewsConfig config) {
		alarmNewsConfig = config;
	}

	/**
	 * 警報・状態一覧の設定を返します
	 * @return 警報・状態一覧の設定
	 */
	public AlarmTableConfig getAlarmTableConfig() {
		return alarmTableConfig;
	}

	/**
	 * 警報・状態一覧の設定を設定します
	 * @param config 警報・状態一覧の設定
	 */
	public void setAlarmTableConfig(AlarmTableConfig config) {
		alarmTableConfig = config;
	}

	/**
	 * サーバーコネクションエラーメッセージを返します
	 * @return サーバーコネクションエラーメッセージを返します
	 */
	public ServerErrorMessage getServerErrorMessage() {
		return serverErrorMessage;
	}

	/**
	 * サーバーコネクションエラーメッセージを設定します
	 * @param message サーバーコネクションエラーメッセージを設定します
	 */
	public void setServerErrorMessage(ServerErrorMessage message) {
		serverErrorMessage = message;
	}

	/**
	 * クライアントのページ設定を返します
	 * @return クライアントのページ設定
	 */
	public Page getPage() {
		return page;
	}

	/**
	 * クライアントのページ設定を設定します
	 * @param page クライアントのページ設定
	 */
	public void setPage(Page page) {
		this.page = page;
	}

	/**
	 * クライアントのツールバー設定を返します
	 * @return クライアントのツールバー設定
	 */
	public ToolBar getToolBar() {
		return toolBar;
	}

	/**
	 * クライアントのツールバー設定を設定します
	 * @param bar クライアントのツールバー設定
	 */
	public void setToolBar(ToolBar bar) {
		toolBar = bar;
	}

	/**
	 * 初期設定を返します
	 * @return 初期設定
	 */
	public InitConfig getInitConfig() {
		return initConfig;
	}

	/**
	 * 初期設定を設定します
	 * @param config 初期設定
	 */
	public void setInitConfig(InitConfig config) {
		initConfig = config;
	}

    public TitleConfig getTitleConfig() {
        return titleConfig;
    }

    public void setTitleConfig(TitleConfig titleConfig) {
        this.titleConfig = titleConfig;
    }
}
