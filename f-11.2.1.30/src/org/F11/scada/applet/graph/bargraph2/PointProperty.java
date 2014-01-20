/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.applet.graph.bargraph2;

public class PointProperty {
	private double minimums = 0.0;
	private double maximums = 1.0;
	private String providerName = "";
	private String holderName = "";
	private String nowValueProviderName = "";
	private String nowValueHolderName = "";

	public String getHolderName() {
		return holderName;
	}
	public void setHolderName(String holder) {
		this.holderName = holder;
	}
	public double getMaximums() {
		return maximums;
	}
	public void setMaximums(double maximums) {
		this.maximums = maximums;
	}
	public double getMinimums() {
		return minimums;
	}
	public void setMinimums(double minimums) {
		this.minimums = minimums;
	}
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String provider) {
		this.providerName = provider;
	}
	public String getNowValueProviderName() {
		return nowValueProviderName;
	}
	public void setNowValueProviderName(String nowValueProviderName) {
		this.nowValueProviderName = nowValueProviderName;
	}
	public String getNowValueHolderName() {
		return nowValueHolderName;
	}
	public void setNowValueHolderName(String nowValueHolderName) {
		this.nowValueHolderName = nowValueHolderName;
	}

}
