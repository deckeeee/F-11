/*
 * 作成日: 2004/03/25
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
package org.F11.scada.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * @author hori
 *
 * この生成されたコメントの挿入されるテンプレートを変更するため
 * ウィンドウ > 設定 > Java > コード生成 > コードとコメント
 */
public class OperationMasterList {

	private final List operationList = new ArrayList();
	private final Map nameMap = new HashMap();

	public OperationMasterList() {
		initAdd("user", "ユーザー管理");
		initAdd("group", "グループ管理");
		initAdd("autoprint", "自動印字設定");
		initAdd("name", "ポイント名称管理");
		initAdd("policy", "ポイント認証管理");
		initAdd("item", "ポイントアイテム管理");
		initAdd("email", "ポイント電子メール送信先管理");
		initAdd("career", "ポイント履歴管理");
		initAdd("device", "デバイス管理");
		initAdd("menu", "メニュー管理");
		initAdd("page", "画面管理");
		initAdd("maintenance", "メンテナンス管理");
		initAdd("ffugroupname", "FFUグループ名称管理");
		initAdd("email_group_master", "電子メール送信グループ マスター管理");
		initAdd("email_attribute_setting", "電子メール送信グループ 属性別管理");
		initAdd("email_individual_setting", "電子メール送信グループ 個別管理");
		initAdd("logdata", "ロギングデータ ダウンロード");
		initAdd("sound_attribute_setting", "警報音設定　属性別管理");
		initAdd("sound_individual_setting", "警報音設定　個別管理");
		initAdd("sentmail", "警報メール　送信一覧");
		initAdd("opelog", "操作ログ　ダウンロード");
	}

	private void initAdd(String key, String message) {
		operationList.add(key);
		nameMap.put(key, message);
	}

	public List getKeys() {
		return new ArrayList(operationList);
	}

	public String getName(String key) {
		return (String) nameMap.get(key);
	}

}
