/*
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
 *
 */

package org.F11.scada.server.entity;

import java.io.Serializable;

/**
 * item_tableのエンティティクラス
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class Item implements Serializable {
	private static final long serialVersionUID = 6125340900210379839L;

	public static final String TABLE = "item_table";
	public static final String point_COLUMN = "point";
	public static final String provider_COLUMN = "provider";
	public static final String holder_COLUMN = "holder";
	public static final String comCycle_COLUMN = "com_cycle";
	public static final String comCycleMode_COLUMN = "com_cycle_mode";
	public static final String comMemoryKinds_COLUMN = "com_memory_kinds";
	public static final String comMemoryAddress_COLUMN = "com_memory_address";
	public static final String bFlag_COLUMN = "b_flag";
	public static final String messageId_COLUMN = "message_id";
	public static final String attributeId_COLUMN = "attribute_id";
	public static final String dataType_COLUMN = "data_type";
	public static final String dataArgv_COLUMN = "data_argv";
	public static final String jumpPath_COLUMN = "jump_path";
	public static final String autoJumpFlag_COLUMN = "auto_jump_flag";
	public static final String autoJumpPriority_COLUMN = "auto_jump_priority";
	public static final String onSoundPath_COLUMN = "on_sound_path";
	public static final String offSoundPath_COLUMN = "off_sound_path";
	public static final String analogTypeId_COLUMN = "analog_type_id";
	public static final String emailGroupId_COLUMN = "email_group_id";
	public static final String emailSendMode_COLUMN = "email_send_mode";
	public static final String offDelay_COLUMN = "off_delay";
	public static final int analogType_RELNO = 0;
	public static final String analogType_RELKEYS = "analog_type_id";
	public static final int summary_RELNO = 1;
	public static final String summary_RELKEYS = "point, provider, holder";
	public static final int _point_RELNO = 2;
	public static final String _point_RELKEYS = "point:point";
	public static final int attribute_RELNO = 3;
	public static final String attribute_RELKEYS = "attribute_id:attribute";

	private Integer point;
	private String provider;
	private String holder;
	private int comCycle;
	private boolean comCycleMode;
	private int comMemoryKinds;
	private long comMemoryAddress;
	private boolean bFlag;
	private int messageId;
	private int attributeId;
	private int dataType;
	private String dataArgv;
	private String jumpPath;
	private boolean autoJumpFlag;
	private int autoJumpPriority;
	private String onSoundPath;
	private String offSoundPath;
	private Integer analogTypeId;
	private Integer emailGroupId;
	private Integer emailSendMode;
	private Integer offDelay;
	private boolean system;
	private AnalogType analogType;
	private Summary summary;
	private Point _point;
	private Attribute attribute;

	public Integer getAnalogTypeId() {
		return analogTypeId;
	}

	public void setAnalogTypeId(Integer analogTypeId) {
		this.analogTypeId = analogTypeId;
	}

	public int getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(int attributeId) {
		this.attributeId = attributeId;
	}

	public boolean isAutoJumpFlag() {
		return autoJumpFlag;
	}

	public void setAutoJumpFlag(boolean autoJumpFlag) {
		this.autoJumpFlag = autoJumpFlag;
	}

	public int getAutoJumpPriority() {
		return autoJumpPriority;
	}

	public void setAutoJumpPriority(int autoJumpPriority) {
		this.autoJumpPriority = autoJumpPriority;
	}

	public boolean isBFlag() {
		return bFlag;
	}

	public void setBFlag(boolean flag) {
		bFlag = flag;
	}

	public int getComCycle() {
		return comCycle;
	}

	public void setComCycle(int comCycle) {
		this.comCycle = comCycle;
	}

	public boolean isComCycleMode() {
		return comCycleMode;
	}

	public void setComCycleMode(boolean comCycleMode) {
		this.comCycleMode = comCycleMode;
	}

	public int getComMemoryKinds() {
		return comMemoryKinds;
	}

	public void setComMemoryKinds(int comMemoryKinds) {
		this.comMemoryKinds = comMemoryKinds;
	}

	public String getDataArgv() {
		return dataArgv;
	}

	public void setDataArgv(String dataArgv) {
		this.dataArgv = dataArgv;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public Integer getEmailGroupId() {
		return emailGroupId;
	}

	public void setEmailGroupId(Integer emailGroupId) {
		this.emailGroupId = emailGroupId;
	}

	public Integer getEmailSendMode() {
		return emailSendMode;
	}

	public void setEmailSendMode(Integer emailSendMode) {
		this.emailSendMode = emailSendMode;
	}

	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public String getJumpPath() {
		return jumpPath;
	}

	public void setJumpPath(String jumpPath) {
		this.jumpPath = jumpPath;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public Integer getOffDelay() {
		return offDelay;
	}

	public void setOffDelay(Integer offDelay) {
		this.offDelay = offDelay;
	}

	public String getOffSoundPath() {
		return offSoundPath;
	}

	public void setOffSoundPath(String offSoundPath) {
		this.offSoundPath = offSoundPath;
	}

	public String getOnSoundPath() {
		return onSoundPath;
	}

	public void setOnSoundPath(String onSoundPath) {
		this.onSoundPath = onSoundPath;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public long getComMemoryAddress() {
		return comMemoryAddress;
	}

	public void setComMemoryAddress(long comMemoryAddress) {
		this.comMemoryAddress = comMemoryAddress;
	}

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}

	public AnalogType getAnalogType() {
		return analogType;
	}

	public void setAnalogType(AnalogType analogType) {
		this.analogType = analogType;
	}

	/**
	 * 2.1.10 RC11未満はサマリーを2.1.10 RC11以降はシステムサマリーを返します。
	 *
	 * @return 2.1.10 RC11未満はサマリーを2.1.10 RC11以降はシステムサマリーを返します。
	 */
	public Summary getSummary() {
		return summary;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	public Point get_point() {
		return _point;
	}

	public void set_point(Point _point) {
		this._point = _point;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return point
			+ " "
			+ provider
			+ " "
			+ holder
			+ " "
			+ comCycle
			+ " "
			+ comCycleMode
			+ " "
			+ comMemoryKinds
			+ " "
			+ comMemoryAddress
			+ " "
			+ bFlag
			+ " "
			+ messageId
			+ " "
			+ attributeId
			+ " "
			+ dataType
			+ " "
			+ dataArgv
			+ " "
			+ jumpPath
			+ " "
			+ autoJumpFlag
			+ " "
			+ autoJumpPriority
			+ " "
			+ onSoundPath
			+ " "
			+ offSoundPath
			+ " "
			+ analogTypeId
			+ " "
			+ emailGroupId
			+ " "
			+ emailSendMode
			+ " "
			+ offDelay
			+ " "
			+ system
			+ " "
			+ analogType
			+ " "
			+ summary
			+ " "
			+ _point
			+ " "
			+ attribute;
	}
}
