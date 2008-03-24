package org.F11.scada.tool.point.name;

import java.io.Serializable;

/**
 * ポイント名称変更のDtoです。
 * 
 * @author maekawa
 */
public class PointNameBean implements Serializable {
	private static final long serialVersionUID = -1405907651554660297L;
	public static final String TABLE = "point_table";
	public static final String used_COLUMN = "used";

	private int point;
	private String unit;
	private String name;
	private String unit_mark;
	private boolean used;
	private String attribute1;
	private String attribute2;
	private String attribute3;

	public String getName() {
		return name;
	}

	public int getPoint() {
		return point;
	}

	public String getUnit() {
		return unit;
	}

	public String getUnit_mark() {
		return unit_mark;
	}

	public boolean isUsed() {
		return used;
	}

	public void setName(String string) {
		name = string;
	}

	public void setPoint(int i) {
		point = i;
	}

	public void setUnit(String string) {
		unit = string;
	}

	public void setUnit_mark(String string) {
		unit_mark = string;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public String getAttribute1() {
		return attribute1;
	}

	public void setAttribute1(String attribute1) {
		this.attribute1 = attribute1;
	}

	public String getAttribute2() {
		return attribute2;
	}

	public void setAttribute2(String attribute2) {
		this.attribute2 = attribute2;
	}

	public String getAttribute3() {
		return attribute3;
	}

	public void setAttribute3(String attribute3) {
		this.attribute3 = attribute3;
	}

	public String toString() {
		return "[" + point + ", " + unit + ", " + name + ", " + unit_mark
				+ ", " + used + "," + attribute1 + "," + attribute2 + "," + attribute3 + "]";
	}
}
