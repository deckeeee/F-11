/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/data/ConvertValueTest.java,v 1.7 2004/06/21 07:16:33 frdm Exp $
 * $Revision: 1.7 $
 * $Date: 2004/06/21 07:16:33 $
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
 *
 */
package org.F11.scada.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

/**
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class ConvertValueTest extends TestCase {
	/**
	 * Constructor for ConvertValueTest.
	 * @param arg0
	 */
	public ConvertValueTest(String arg0) {
		super(arg0);
	}

	public void test001() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfANALOG(-100, 300, 0, 400, "###0.0");
		assertEquals(
			"{cnvMin=-100.0 cnvMax=300.0 inMin=0.0 inMax=400.0 format=###0.0 type=ANALOG PFtype=DECIMAL}",
			fixture.toString());
		assertEquals(-100.0, fixture.getConvertMin(), 0.0);
		assertEquals(300.0, fixture.getConvertMax(), 0.0);

		// double変換値 <- PLC値
		assertEquals(-200.0, fixture.convertDoubleValue(-100.0), 0.0);
		assertEquals(-100.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(0.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(100.0, fixture.convertDoubleValue(200.0), 0.0);
		assertEquals(200.0, fixture.convertDoubleValue(300.0), 0.0);
		assertEquals(300.0, fixture.convertDoubleValue(400.0), 0.0);
		assertEquals(400.0, fixture.convertDoubleValue(500.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-100.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(   0.0), 0.0);
		assertEquals(200.0, fixture.convertInputValue( 100.0), 0.0);
		assertEquals(300.0, fixture.convertInputValue( 200.0), 0.0);
		assertEquals(400.0, fixture.convertInputValue( 300.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-200.0), 0.0);
		assertEquals(400.0, fixture.convertInputValue( 400.0), 0.0);

		// 変換値 <- PLC値
		assertEquals("-100.0", fixture.convertStringValue(0.0));
		assertEquals("0.0", fixture.convertStringValue(100.0));
		assertEquals("100.0", fixture.convertStringValue(200.0));
		assertEquals("200.0", fixture.convertStringValue(300.0));
		assertEquals("300.0", fixture.convertStringValue(400.0));
		// 範囲外
		assertEquals("-100.0", fixture.convertStringValue(-100.0));
		assertEquals("300.0", fixture.convertStringValue(500.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("-100.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("   0.0"), 0.0);
		assertEquals(200.0, fixture.convertInputValue(" 100.0"), 0.0);
		assertEquals(300.0, fixture.convertInputValue(" 200.0"), 0.0);
		assertEquals(400.0, fixture.convertInputValue(" 300.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("-200.0"), 0.0);
		assertEquals(400.0, fixture.convertInputValue(" 400.0"), 0.0);

		// 変換率 3/1
		fixture = ConvertValue.valueOfANALOG(-1000, 200, 0, 400, "###0");
		assertEquals(
			"{cnvMin=-1000.0 cnvMax=200.0 inMin=0.0 inMax=400.0 format=###0 type=ANALOG PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-1300.0, fixture.convertDoubleValue(-100.0), 0.0);
		assertEquals(-1000.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-700.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(-400.0, fixture.convertDoubleValue(200.0), 0.0);
		assertEquals(-100.0, fixture.convertDoubleValue(300.0), 0.0);
		assertEquals(200.0, fixture.convertDoubleValue(400.0), 0.0);
		assertEquals(500.0, fixture.convertDoubleValue(500.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-1000), 0.0);
		assertEquals(100.0, fixture.convertInputValue(-700), 0.0);
		assertEquals(200.0, fixture.convertInputValue(-400), 0.0);
		assertEquals(300.0, fixture.convertInputValue(-100), 0.0);
		assertEquals(400.0, fixture.convertInputValue( 200), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-1300), 0.0);
		assertEquals(400.0, fixture.convertInputValue( 500), 0.0);

		// 変換値 <- PLC値
		assertEquals("-1000", fixture.convertStringValue(0.0));
		assertEquals("-700", fixture.convertStringValue(100.0));
		assertEquals("-400", fixture.convertStringValue(200.0));
		assertEquals("-100", fixture.convertStringValue(300.0));
		assertEquals("200", fixture.convertStringValue(400.0));
		// 範囲外
		assertEquals("-1000", fixture.convertStringValue(-100.0));
		assertEquals("200", fixture.convertStringValue(500.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("-1000"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("-700"), 0.0);
		assertEquals(200.0, fixture.convertInputValue("-400"), 0.0);
		assertEquals(300.0, fixture.convertInputValue("-100"), 0.0);
		assertEquals(400.0, fixture.convertInputValue(" 200"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("-1300"), 0.0);
		assertEquals(400.0, fixture.convertInputValue(" 500"), 0.0);

		// 変換率 1/4
		fixture = ConvertValue.valueOfANALOG(-80, 20, 0, 400, "###0.00");
		assertEquals(
			"{cnvMin=-80.0 cnvMax=20.0 inMin=0.0 inMax=400.0 format=###0.00 type=ANALOG PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-105.0, fixture.convertDoubleValue(-100.0), 0.0);
		assertEquals(-80.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-55.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(-30.0, fixture.convertDoubleValue(200.0), 0.0);
		assertEquals(-5.0, fixture.convertDoubleValue(300.0), 0.0);
		assertEquals(20.0, fixture.convertDoubleValue(400.0), 0.0);
		assertEquals(45.0, fixture.convertDoubleValue(500.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue( -80.00), 0.0);
		assertEquals(100.0, fixture.convertInputValue( -55.00), 0.0);
		assertEquals(200.0, fixture.convertInputValue( -30.00), 0.0);
		assertEquals(300.0, fixture.convertInputValue(  -5.00), 0.0);
		assertEquals(400.0, fixture.convertInputValue(  20.00), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-105.00), 0.0);
		assertEquals(400.0, fixture.convertInputValue(  45.00), 0.0);

		// 変換値 <- PLC値
		assertEquals("-80.00", fixture.convertStringValue(0.0));
		assertEquals("-55.00", fixture.convertStringValue(100.0));
		assertEquals("-30.00", fixture.convertStringValue(200.0));
		assertEquals("-5.00", fixture.convertStringValue(300.0));
		assertEquals("20.00", fixture.convertStringValue(400.0));
		// 範囲外
		assertEquals("-80.00", fixture.convertStringValue(-100.0));
		assertEquals("20.00", fixture.convertStringValue(500.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(" -80.00"), 0.0);
		assertEquals(100.0, fixture.convertInputValue(" -55.00"), 0.0);
		assertEquals(200.0, fixture.convertInputValue(" -30.00"), 0.0);
		assertEquals(300.0, fixture.convertInputValue("  -5.00"), 0.0);
		assertEquals(400.0, fixture.convertInputValue("  20.00"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("-105.00"), 0.0);
		assertEquals(400.0, fixture.convertInputValue("  45.00"), 0.0);

		// フォーマット指定
		fixture = ConvertValue.valueOfANALOG(-100, 300, 0, 400, "0000.0");
		assertEquals(
			"{cnvMin=-100.0 cnvMax=300.0 inMin=0.0 inMax=400.0 format=0000.0 type=ANALOG PFtype=DECIMAL}",
			fixture.toString());
		// 変換値 <- PLC値
		assertEquals("-0100.0", fixture.convertStringValue(0.0));
		assertEquals("0100.0", fixture.convertStringValue(200.0));
		assertEquals("0300.0", fixture.convertStringValue(400.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue( -100), 0.0);
		assertEquals(200.0, fixture.convertInputValue( 100.00), 0.0);
		assertEquals(400.0, fixture.convertInputValue( 300), 0.0);

		assertEquals("-100.0", fixture.convertStringValue(0.0, "0.0"));
		assertEquals("100", fixture.convertStringValue(200.0, "###0"));
		assertEquals("D 300.0", fixture.convertStringValue(400.0, "D ##0.0"));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(" -0100"), 0.0);
		assertEquals(200.0, fixture.convertInputValue(" 100.00"), 0.0);
		assertEquals(400.0, fixture.convertInputValue(" 300"), 0.0);
		
		serialTest(fixture);
	}

	/** 変換値 大小逆転 */
	public void test002() throws Exception {
		/** 変換率 1/1 */
		ConvertValue fixture = ConvertValue.valueOfANALOG(100, -300, 0, 400, " 0.0; -0.0");
		assertEquals(
			"{cnvMin=100.0 cnvMax=-300.0 inMin=0.0 inMax=400.0 format= 0.0; -0.0 type=ANALOG PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(200.0, fixture.convertDoubleValue(-100.0), 0.0);
		assertEquals(100.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(0.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(-100.0, fixture.convertDoubleValue(200.0), 0.0);
		assertEquals(-200.0, fixture.convertDoubleValue(300.0), 0.0);
		assertEquals(-300.0, fixture.convertDoubleValue(400.0), 0.0);
		assertEquals(-400.0, fixture.convertDoubleValue(500.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(100.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(200.0, fixture.convertInputValue(-100.0), 0.0);
		assertEquals(300.0, fixture.convertInputValue(-200.0), 0.0);
		assertEquals(400.0, fixture.convertInputValue(-300.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(200.0), 0.0);
		assertEquals(400.0, fixture.convertInputValue(-400.0), 0.0);

		// 変換値 <- PLC値
		assertEquals(" 100.0", fixture.convertStringValue(0.0));
		assertEquals(" 0.0", fixture.convertStringValue(100.0));
		assertEquals(" -100.0", fixture.convertStringValue(200.0));
		assertEquals(" -200.0", fixture.convertStringValue(300.0));
		assertEquals(" -300.0", fixture.convertStringValue(400.0));
		// 範囲外
		assertEquals(" 100.0", fixture.convertStringValue(-100.0));
		assertEquals(" -300.0", fixture.convertStringValue(500.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("100.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("0.0"), 0.0);
		assertEquals(200.0, fixture.convertInputValue("-100.0"), 0.0);
		assertEquals(300.0, fixture.convertInputValue("-200.0"), 0.0);
		assertEquals(400.0, fixture.convertInputValue("-300.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("200.0"), 0.0);
		assertEquals(400.0, fixture.convertInputValue("-400.0"), 0.0);

		/** 変換率 3/1 */
		fixture = ConvertValue.valueOfANALOG(1000, -200, 0, 400, " 0");
		assertEquals(
			"{cnvMin=1000.0 cnvMax=-200.0 inMin=0.0 inMax=400.0 format= 0 type=ANALOG PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(1300.0, fixture.convertDoubleValue(-100.0), 0.0);
		assertEquals(1000.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(700.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(400.0, fixture.convertDoubleValue(200.0), 0.0);
		assertEquals(100.0, fixture.convertDoubleValue(300.0), 0.0);
		assertEquals(-200.0, fixture.convertDoubleValue(400.0), 0.0);
		assertEquals(-500.0, fixture.convertDoubleValue(500.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(1000), 0.0);
		assertEquals(100.0, fixture.convertInputValue(700), 0.0);
		assertEquals(200.0, fixture.convertInputValue(400), 0.0);
		assertEquals(300.0, fixture.convertInputValue(100), 0.0);
		assertEquals(400.0, fixture.convertInputValue(-200), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(1300), 0.0);
		assertEquals(400.0, fixture.convertInputValue(-500), 0.0);

		// 変換値 <- PLC値
		assertEquals(" 1000", fixture.convertStringValue(0.0));
		assertEquals(" 700", fixture.convertStringValue(100.0));
		assertEquals(" 400", fixture.convertStringValue(200.0));
		assertEquals(" 100", fixture.convertStringValue(300.0));
		assertEquals("- 200", fixture.convertStringValue(400.0));
		// 範囲外
		assertEquals(" 1000", fixture.convertStringValue(-100.0));
		assertEquals("- 200", fixture.convertStringValue(500.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("1000"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("700"), 0.0);
		assertEquals(200.0, fixture.convertInputValue("400"), 0.0);
		assertEquals(300.0, fixture.convertInputValue("100"), 0.0);
		assertEquals(400.0, fixture.convertInputValue("-200"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("1300"), 0.0);
		assertEquals(400.0, fixture.convertInputValue("-500"), 0.0);

		/** 変換率 1/4 */
		fixture = ConvertValue.valueOfANALOG(80, -20, 0, 400, "###0.00");
		assertEquals(
			"{cnvMin=80.0 cnvMax=-20.0 inMin=0.0 inMax=400.0 format=###0.00 type=ANALOG PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(105.0, fixture.convertDoubleValue(-100.0), 0.0);
		assertEquals(80.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(55.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(30.0, fixture.convertDoubleValue(200.0), 0.0);
		assertEquals(5.0, fixture.convertDoubleValue(300.0), 0.0);
		assertEquals(-20.0, fixture.convertDoubleValue(400.0), 0.0);
		assertEquals(-45.0, fixture.convertDoubleValue(500.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(  80.00), 0.0);
		assertEquals(100.0, fixture.convertInputValue(  55.00), 0.0);
		assertEquals(200.0, fixture.convertInputValue(  30.00), 0.0);
		assertEquals(300.0, fixture.convertInputValue(   5.00), 0.0);
		assertEquals(400.0, fixture.convertInputValue( -20.00), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue( 105.00), 0.0);
		assertEquals(400.0, fixture.convertInputValue( -45.00), 0.0);

		// 変換値 <- PLC値
		assertEquals("80.00", fixture.convertStringValue(0.0));
		assertEquals("55.00", fixture.convertStringValue(100.0));
		assertEquals("30.00", fixture.convertStringValue(200.0));
		assertEquals("5.00", fixture.convertStringValue(300.0));
		assertEquals("-20.00", fixture.convertStringValue(400.0));
		// 範囲外
		assertEquals("80.00", fixture.convertStringValue(-100.0));
		assertEquals("-20.00", fixture.convertStringValue(500.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("  80.00"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("  55.00"), 0.0);
		assertEquals(200.0, fixture.convertInputValue("  30.00"), 0.0);
		assertEquals(300.0, fixture.convertInputValue("   5.00"), 0.0);
		assertEquals(400.0, fixture.convertInputValue(" -20.00"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(" 105.00"), 0.0);
		assertEquals(400.0, fixture.convertInputValue(" -45.00"), 0.0);
		
		serialTest(fixture);
	}

	/**
	 * 力率
	 */
	public void test_valueOfM05M1_DECIMAL() throws Exception {
		/** DECIMAL */
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfDECIMAL(-0.5, -1, 0, 100, "0.00");
		assertEquals(
			"{cnvMin=-0.5 cnvMax=-1.0 inMin=0.0 inMax=100.0 format=0.00 type=POWERFACTOR PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-0.51, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(-0.50, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-0.49, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(-0.01, fixture.convertDoubleValue(49.0), 0.00001);
		assertEquals(0.0, fixture.convertDoubleValue(50.0), 0.0);
		assertEquals(0.01, fixture.convertDoubleValue(51.0), 0.00001);
		assertEquals(0.49, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(0.50, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(0.51, fixture.convertDoubleValue(101.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-0.50), 0.0);
		assertEquals(1.0, fixture.convertInputValue(-0.49), 0.00001);
		assertEquals(49.0, fixture.convertInputValue(-0.01), 0.0);
		assertEquals(50.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(51.0, fixture.convertInputValue(0.01), 0.0);
		assertEquals(99.0, fixture.convertInputValue(0.49), 0.0);
		assertEquals(100.0, fixture.convertInputValue(0.50), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-0.51), 0.0);
		assertEquals(100.0, fixture.convertInputValue(0.51), 0.0);

		// 変換値 <- PLC値
		assertEquals("-0.50", fixture.convertStringValue(0.0));
		assertEquals("-0.51", fixture.convertStringValue(1.0));
		assertEquals("-0.99", fixture.convertStringValue(49.0));
		assertEquals("1.00", fixture.convertStringValue(50.0));
		assertEquals("0.99", fixture.convertStringValue(51.0));
		assertEquals("0.51", fixture.convertStringValue(99.0));
		assertEquals("0.50", fixture.convertStringValue(100.0));
		// 範囲外
		assertEquals("-0.50", fixture.convertStringValue(-1.0));
		assertEquals("0.50", fixture.convertStringValue(101.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("-0.50"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("-0.51"), 0.00001);
		assertEquals(49.0, fixture.convertInputValue("-0.99"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("1.00"), 0.0);
		assertEquals(51.0, fixture.convertInputValue("0.99"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("0.51"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("0.50"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("-0.49"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("0.49"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("-1.10"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("-1.00"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("1.10"), 0.0);

		// 変換率 1/4000
		fixture = ConvertValue.valueOfDECIMAL(-0.5, -1, 0, 4000, "#0.000");
		assertEquals(
			"{cnvMin=-0.5 cnvMax=-1.0 inMin=0.0 inMax=4000.0 format=#0.000 type=POWERFACTOR PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-0.501, fixture.convertDoubleValue(-4.0), 0.0);
		assertEquals(-0.500, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-0.499, fixture.convertDoubleValue(4.0), 0.0);
		assertEquals(-0.001, fixture.convertDoubleValue(1996.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(0.001, fixture.convertDoubleValue(2004.0), 0.00000001);
		assertEquals(0.499, fixture.convertDoubleValue(3996.0), 0.0);
		assertEquals(0.500, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(0.501, fixture.convertDoubleValue(4004.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-0.500), 0.0000);
		assertEquals(4.0, fixture.convertInputValue(-0.499), 0.000001);
		assertEquals(1996.0, fixture.convertInputValue(-0.001), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(2004.0, fixture.convertInputValue(0.001), 0.0);
		assertEquals(3996.0, fixture.convertInputValue(0.499), 0.0000);
		assertEquals(4000.0, fixture.convertInputValue(0.500), 0.0000);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-0.501), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(0.501), 0.0);

		// 変換値 <- PLC値
		assertEquals("-0.500", fixture.convertStringValue(0.0));
		assertEquals("-0.500", fixture.convertStringValue(2.0));
		assertEquals("-0.501", fixture.convertStringValue(3.0));
		assertEquals("-0.999", fixture.convertStringValue(1997.0));
		assertEquals("1.000", fixture.convertStringValue(1998.0));
		assertEquals("1.000", fixture.convertStringValue(2000.0));
		assertEquals("1.000", fixture.convertStringValue(2001.0));
		assertEquals("0.999", fixture.convertStringValue(2002.0));
		assertEquals("0.501", fixture.convertStringValue(3997.0));
		assertEquals("0.500", fixture.convertStringValue(3998.0));
		assertEquals("0.500", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("-0.500", fixture.convertStringValue(-10.0));
		assertEquals("0.500", fixture.convertStringValue(4010.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("-0.500"), 0.0);
		assertEquals(4.0, fixture.convertInputValue("-0.501"), 0.00001);
		assertEquals(1996.0, fixture.convertInputValue("-0.999"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("-1.000"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(" 1.000"), 0.0);
		assertEquals(2004.0, fixture.convertInputValue(" 0.999"), 0.00001);
		assertEquals(3996.0, fixture.convertInputValue(" 0.501"), 0.00001);
		assertEquals(4000.0, fixture.convertInputValue(" 0.500"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("-0.499"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(" 0.499"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(" 1.100"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("-1.000"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("-1.100"), 0.0);

		// フォーマット指定
		fixture = ConvertValue.valueOfDECIMAL(-0.5, -1, 0, 100, "0.00");
		assertEquals(
			"{cnvMin=-0.5 cnvMax=-1.0 inMin=0.0 inMax=100.0 format=0.00 type=POWERFACTOR PFtype=DECIMAL}",
			fixture.toString());
		// 変換値 <- PLC値
		assertEquals("1.00", fixture.convertStringValue(50.0));
		assertEquals("0.99", fixture.convertStringValue(51.0));

		assertEquals("1.0", fixture.convertStringValue(50.0, "0.0"));
		assertEquals("0.990", fixture.convertStringValue(51.0, "##0.000"));
		
		serialTest(fixture);
	}

	public void test_valueOfM50M100_DECIMAL() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfDECIMAL(-50, -100, 0, 100, "0.0");
		assertEquals(
			"{cnvMin=-50.0 cnvMax=-100.0 inMin=0.0 inMax=100.0 format=0.0 type=POWERFACTOR PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-51.0, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(-50.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-49.0, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(-1.0, fixture.convertDoubleValue(49.0), 0.0);
		assertEquals(0.0, fixture.convertDoubleValue(50.0), 0.0);
		assertEquals(1.0, fixture.convertDoubleValue(51.0), 0.0);
		assertEquals(49.0, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(50.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(51.0, fixture.convertDoubleValue(101.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-50.0), 0.0);
		assertEquals(1.0, fixture.convertInputValue(-49.0), 0.0);
		assertEquals(49.0, fixture.convertInputValue(-1.0), 0.0);
		assertEquals(50.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(51.0, fixture.convertInputValue(1.0), 0.0);
		assertEquals(99.0, fixture.convertInputValue(49.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(50.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-51.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(51.0), 0.0);

		// 変換値 <- PLC値
		assertEquals("-50.0", fixture.convertStringValue(0.0));
		assertEquals("-51.0", fixture.convertStringValue(1.0));
		assertEquals("-99.0", fixture.convertStringValue(49.0));
		assertEquals("100.0", fixture.convertStringValue(50.0));
		assertEquals("99.0", fixture.convertStringValue(51.0));
		assertEquals("51.0", fixture.convertStringValue(99.0));
		assertEquals("50.0", fixture.convertStringValue(100.0));
		// 範囲外
		assertEquals("-50.0", fixture.convertStringValue(-1.0));
		assertEquals("50.0", fixture.convertStringValue(101.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("-50.0"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("-51.0"), 0.0);
		assertEquals(49.0, fixture.convertInputValue("-99.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("100.0"), 0.0);
		assertEquals(51.0, fixture.convertInputValue("99.0"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("51.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("50.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("-49.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("49.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("101.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("-100.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("-101.0"), 0.0);

		// 変換率 1/4000
		fixture = ConvertValue.valueOfDECIMAL(-50, -100, 0, 4000, "0.0");
		assertEquals(
			"{cnvMin=-50.0 cnvMax=-100.0 inMin=0.0 inMax=4000.0 format=0.0 type=POWERFACTOR PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-50.1, fixture.convertDoubleValue(-4.0), 0.0);
		assertEquals(-50.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-49.9, fixture.convertDoubleValue(4.0), 0.0);
		assertEquals(-0.1, fixture.convertDoubleValue(1996.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(0.1, fixture.convertDoubleValue(2004.0), 0.00000001);
		assertEquals(49.9, fixture.convertDoubleValue(3996.0), 0.00000001);
		assertEquals(50.0, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(50.1, fixture.convertDoubleValue(4004.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-50.0), 0.0);
		assertEquals(4.0, fixture.convertInputValue(-49.9), 0.001);
		assertEquals(1996.0, fixture.convertInputValue(-0.1), 0.00);
		assertEquals(2000.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(2004.0, fixture.convertInputValue(0.1), 0.00);
		assertEquals(3996.0, fixture.convertInputValue(49.9), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(50.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-50.1), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(50.1), 0.0);

		// 変換値 <- PLC値
		assertEquals("-50.0", fixture.convertStringValue(0.0));
		assertEquals("-50.0", fixture.convertStringValue(2.0));
		assertEquals("-50.1", fixture.convertStringValue(3.0));
		assertEquals("-99.9", fixture.convertStringValue(1997.0));
		assertEquals("100.0", fixture.convertStringValue(1998.0));
		assertEquals("100.0", fixture.convertStringValue(2000.0));
		assertEquals("100.0", fixture.convertStringValue(2001.0));
		assertEquals("99.9", fixture.convertStringValue(2002.0));
		assertEquals("50.1", fixture.convertStringValue(3998.0));
		assertEquals("50.0", fixture.convertStringValue(3999.0));
		assertEquals("50.0", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("-50.0", fixture.convertStringValue(-10.0));
		assertEquals("50.0", fixture.convertStringValue(4010.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("-50.0"), 0.0);
		assertEquals(4.0, fixture.convertInputValue("-50.1"), 0.001);
		assertEquals(1996.0, fixture.convertInputValue("-99.9"), 0.001);
		assertEquals(2000.0, fixture.convertInputValue("100.0"), 0.0);
		assertEquals(2004.0, fixture.convertInputValue("99.9"), 0.001);
		assertEquals(3996.0, fixture.convertInputValue("50.1"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("50.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("-49.9"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("49.9"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(" 100.1"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("-100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("-100.1"), 0.0);
		
		serialTest(fixture);
	}

	public void test_valueOfM0M100_DECIMAL() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfDECIMAL(-0.0, -100, 0, 200, "0.0");
		assertEquals(
			"{cnvMin=-0.0 cnvMax=-100.0 inMin=0.0 inMax=200.0 format=0.0 type=POWERFACTOR PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-101.0, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(-100.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-99.0, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(-1.0, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(0.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(1.0, fixture.convertDoubleValue(101.0), 0.0);
		assertEquals(99.0, fixture.convertDoubleValue(199.0), 0.0);
		assertEquals(100.0, fixture.convertDoubleValue(200.0), 0.0);
		assertEquals(101.0, fixture.convertDoubleValue(201.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-100.0), 0.0);
		assertEquals(1.0, fixture.convertInputValue(-99.0), 0.0);
		assertEquals(99.0, fixture.convertInputValue(-1.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(101.0, fixture.convertInputValue(1.0), 0.0);
		assertEquals(199.0, fixture.convertInputValue(99.0), 0.0);
		assertEquals(200.0, fixture.convertInputValue(100.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-101.0), 0.0);
		assertEquals(200.0, fixture.convertInputValue(101.0), 0.0);

		// 変換値 <- PLC値
		assertEquals("-0.0", fixture.convertStringValue(0.0));
		assertEquals("-1.0", fixture.convertStringValue(1.0));
		assertEquals("-99.0", fixture.convertStringValue(99.0));
		assertEquals("100.0", fixture.convertStringValue(100.0));
		assertEquals("99.0", fixture.convertStringValue(101.0));
		assertEquals("1.0", fixture.convertStringValue(199.0));
		assertEquals("0.0", fixture.convertStringValue(200.0));
		// 範囲外
		assertEquals("-0.0", fixture.convertStringValue(-1.0));
		assertEquals("0.0", fixture.convertStringValue(201.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("-0.0"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("-1.0"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("-99.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("100.0"), 0.0);
		assertEquals(101.0, fixture.convertInputValue("99.0"), 0.0);
		assertEquals(199.0, fixture.convertInputValue("1.0"), 0.0);
		assertEquals(200.0, fixture.convertInputValue("0.0"), 0.0);
		// 範囲外
		assertEquals(100.0, fixture.convertInputValue("101.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("-100.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("-101.0"), 0.0);

		// 変換率 1/2000
		fixture = ConvertValue.valueOfDECIMAL(-0.0, -100, 0, 4000, "0.0");
		assertEquals(
			"{cnvMin=-0.0 cnvMax=-100.0 inMin=0.0 inMax=4000.0 format=0.0 type=POWERFACTOR PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-100.1, fixture.convertDoubleValue(-2.0), 0.0);
		assertEquals(-100.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-99.9, fixture.convertDoubleValue(2.0), 0.0);
		assertEquals(-0.1, fixture.convertDoubleValue(1998.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(0.1, fixture.convertDoubleValue(2002.0), 0.00000001);
		assertEquals(99.9, fixture.convertDoubleValue(3998.0), 0.00000001);
		assertEquals(100.0, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(100.1, fixture.convertDoubleValue(4002.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-100.0), 0.0);
		assertEquals(2.0, fixture.convertInputValue(-99.9), 0.001);
		assertEquals(1998.0, fixture.convertInputValue(-0.1), 0.00);
		assertEquals(2000.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(2002.0, fixture.convertInputValue(0.1), 0.00);
		assertEquals(3998.0, fixture.convertInputValue(99.9), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(100.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-100.1), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(100.1), 0.0);

		// 変換値 <- PLC値
		assertEquals("-0.0", fixture.convertStringValue(0.0));
		assertEquals("-0.1", fixture.convertStringValue(2.0));
		assertEquals("-0.2", fixture.convertStringValue(3.0));
		assertEquals("-99.9", fixture.convertStringValue(1997.0));
		assertEquals("-99.9", fixture.convertStringValue(1998.0));
		assertEquals("100.0", fixture.convertStringValue(2000.0));
		assertEquals("99.9", fixture.convertStringValue(2001.0));
		assertEquals("99.9", fixture.convertStringValue(2002.0));
		assertEquals("0.1", fixture.convertStringValue(3998.0));
		assertEquals("0.0", fixture.convertStringValue(3999.0));
		assertEquals("0.0", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("-0.0", fixture.convertStringValue(-10.0));
		assertEquals("0.0", fixture.convertStringValue(4010.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("-0.0"), 0.0);
		assertEquals(2.0, fixture.convertInputValue("-0.1"), 0.001);
		assertEquals(1998.0, fixture.convertInputValue("-99.9"), 0.001);
		assertEquals(2000.0, fixture.convertInputValue("100.0"), 0.0);
		assertEquals(2002.0, fixture.convertInputValue("99.9"), 0.001);
		assertEquals(3998.0, fixture.convertInputValue("0.1"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("0.0"), 0.0);
		// 範囲外
		assertEquals(2000.0, fixture.convertInputValue(" 100.1"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("-100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("-100.1"), 0.0);
		
		serialTest(fixture);
	}

	public void test_valueOfM05M1_LALE() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfLALE(-0.5, -1, 0, 100, "0.00");
		assertEquals(
			"{cnvMin=-0.5 cnvMax=-1.0 inMin=0.0 inMax=100.0 format=0.00 type=POWERFACTOR PFtype=LALE}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-0.51, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(-0.50, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-0.49, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(-0.01, fixture.convertDoubleValue(49.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(50.0), 0.0);
		assertEquals(0.01, fixture.convertDoubleValue(51.0), 0.00000001);
		assertEquals(0.49, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(0.50, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(0.51, fixture.convertDoubleValue(101.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-0.50), 0.0);
		assertEquals(1.0, fixture.convertInputValue(-0.49), 0.00000001);
		assertEquals(49.0, fixture.convertInputValue(-0.01), 0.0);
		assertEquals(50.0, fixture.convertInputValue(0.00), 0.0);
		assertEquals(51.0, fixture.convertInputValue(0.01), 0.0);
		assertEquals(99.0, fixture.convertInputValue(0.49), 0.0);
		assertEquals(100.0, fixture.convertInputValue(0.50), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-0.51), 0.0);
		assertEquals(100.0, fixture.convertInputValue(0.51), 0.0);

		// 変換値 <- PLC値
		assertEquals("LA0.50", fixture.convertStringValue(0.0));
		assertEquals("LA0.51", fixture.convertStringValue(1.0));
		assertEquals("LA0.99", fixture.convertStringValue(49.0));
		assertEquals("  1.00", fixture.convertStringValue(50.0));
		assertEquals("LE0.99", fixture.convertStringValue(51.0));
		assertEquals("LE0.51", fixture.convertStringValue(99.0));
		assertEquals("LE0.50", fixture.convertStringValue(100.0));
		// 範囲外
		assertEquals("LA0.50", fixture.convertStringValue(-1.0));
		assertEquals("LE0.50", fixture.convertStringValue(101.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LA0.50"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("LA0.51"), 0.00001);
		assertEquals(49.0, fixture.convertInputValue("LA0.99"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("  1.00"), 0.0);
		assertEquals(51.0, fixture.convertInputValue("LE0.99"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("LE0.51"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE0.50"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LA0.49"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE0.49"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE1.00"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE1.01"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA1.00"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA1.01"), 0.0);

		// 変換率 1/4000
		fixture = ConvertValue.valueOfLALE(-0.5, -1, 0, 4000, "0.00");
		assertEquals(
			"{cnvMin=-0.5 cnvMax=-1.0 inMin=0.0 inMax=4000.0 format=0.00 type=POWERFACTOR PFtype=LALE}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-0.501, fixture.convertDoubleValue(-4.0), 0.0);
		assertEquals(-0.500, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-0.499, fixture.convertDoubleValue(4.0), 0.0);
		assertEquals(-0.001, fixture.convertDoubleValue(1996.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(0.001, fixture.convertDoubleValue(2004.0), 0.00000001);
		assertEquals(0.499, fixture.convertDoubleValue(3996.0), 0.0);
		assertEquals(0.500, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(0.501, fixture.convertDoubleValue(4004.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-0.500), 0.0);
		assertEquals(4.0, fixture.convertInputValue(-0.499), 0.00001);
		assertEquals(1996.0, fixture.convertInputValue(-0.001), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(0.000), 0.0);
		assertEquals(2004.0, fixture.convertInputValue(0.001), 0.0);
		assertEquals(3996.0, fixture.convertInputValue(0.499), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(0.500), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-0.501), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(0.501), 0.0);

		// 変換値 <- PLC値
		assertEquals("LA0.50", fixture.convertStringValue(0.0));
		assertEquals("LA0.50", fixture.convertStringValue(20.0));
		assertEquals("LA0.51", fixture.convertStringValue(21.0));
		assertEquals("LA0.99", fixture.convertStringValue(1979.0));
		assertEquals("  1.00", fixture.convertStringValue(1980.0));
		assertEquals("  1.00", fixture.convertStringValue(2000.0));
		assertEquals("  1.00", fixture.convertStringValue(2020.0));
		assertEquals("LE0.99", fixture.convertStringValue(2021.0));
		assertEquals("LE0.51", fixture.convertStringValue(3979.0));
		assertEquals("LE0.50", fixture.convertStringValue(3980.0));
		assertEquals("LE0.50", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("LA0.50", fixture.convertStringValue(-100.0));
		assertEquals("LE0.50", fixture.convertStringValue(4100.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LA0.50"), 0.0);
		assertEquals(40.0, fixture.convertInputValue("LA0.51"), 0.00001);
		assertEquals(1960.0, fixture.convertInputValue("LA0.99"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("  1.00"), 0.0);
		assertEquals(2040.0, fixture.convertInputValue("LE0.99"), 0.0);
		assertEquals(3960.0, fixture.convertInputValue("LE0.51"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LE0.50"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LA0.49"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LE0.49"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE1.00"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE1.01"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA1.00"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA1.01"), 0.0);

		// フォーマット指定
		fixture = ConvertValue.valueOfLALE(-0.5, -1, 0, 100, "0.00");
		assertEquals(
			"{cnvMin=-0.5 cnvMax=-1.0 inMin=0.0 inMax=100.0 format=0.00 type=POWERFACTOR PFtype=LALE}",
			fixture.toString());
		// 変換値 <- PLC値
		assertEquals("  1.00", fixture.convertStringValue(50.0));
		assertEquals("LE0.99", fixture.convertStringValue(51.0));

		assertEquals("  1", fixture.convertStringValue(50.0, "0"));
		assertEquals("LE  0.990", fixture.convertStringValue(51.0, "  0.000"));
		
		serialTest(fixture);
	}

	public void test_valueOfM50M100_LALE() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfLALE(-50, -100, 0, 100, " 0.0");
		assertEquals(
			"{cnvMin=-50.0 cnvMax=-100.0 inMin=0.0 inMax=100.0 format= 0.0 type=POWERFACTOR PFtype=LALE}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-51.0, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(-50.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-49.0, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(-1.0, fixture.convertDoubleValue(49.0), 0.0);
		assertEquals(0.0, fixture.convertDoubleValue(50.0), 0.0);
		assertEquals(1.0, fixture.convertDoubleValue(51.0), 0.0);
		assertEquals(49.0, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(50.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(51.0, fixture.convertDoubleValue(101.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-50.0), 0.0);
		assertEquals(1.0, fixture.convertInputValue(-49.0), 0.0);
		assertEquals(49.0, fixture.convertInputValue(-1.0), 0.0);
		assertEquals(50.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(51.0, fixture.convertInputValue(1.0), 0.0);
		assertEquals(99.0, fixture.convertInputValue(49.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(50.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-51.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(51.0), 0.0);

		// 変換値 <- PLC値
		assertEquals("LA 50.0", fixture.convertStringValue(0.0));
		assertEquals("LA 51.0", fixture.convertStringValue(1.0));
		assertEquals("LA 99.0", fixture.convertStringValue(49.0));
		assertEquals("   100.0", fixture.convertStringValue(50.0));
		assertEquals("LE 99.0", fixture.convertStringValue(51.0));
		assertEquals("LE 51.0", fixture.convertStringValue(99.0));
		assertEquals("LE 50.0", fixture.convertStringValue(100.0));
		// 範囲外
		assertEquals("LA 50.0", fixture.convertStringValue(-1.0));
		assertEquals("LE 50.0", fixture.convertStringValue(101.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LA 50.0"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("LA 51.0"), 0.0);
		assertEquals(49.0, fixture.convertInputValue("LA 99.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(51.0, fixture.convertInputValue("LE 99.0"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("LE 51.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE 50.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LA 49.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE 49.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE101.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA101.0"), 0.0);

		// 変換率 1/4000
		fixture = ConvertValue.valueOfLALE(-50, -100, 0, 4000, " 0.0");
		assertEquals(
			"{cnvMin=-50.0 cnvMax=-100.0 inMin=0.0 inMax=4000.0 format= 0.0 type=POWERFACTOR PFtype=LALE}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-50.1, fixture.convertDoubleValue(-4.0), 0.0);
		assertEquals(-50.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-49.9, fixture.convertDoubleValue(4.0), 0.0);
		assertEquals(-0.1, fixture.convertDoubleValue(1996.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(0.1, fixture.convertDoubleValue(2004.0), 0.00000001);
		assertEquals(49.9, fixture.convertDoubleValue(3996.0), 0.00000001);
		assertEquals(50.0, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(50.1, fixture.convertDoubleValue(4004.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-50.0), 0.0);
		assertEquals(4.0, fixture.convertInputValue(-49.9), 0.00001);
		assertEquals(1996.0, fixture.convertInputValue(-0.1), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(2004.0, fixture.convertInputValue(0.1), 0.00001);
		assertEquals(3996.0, fixture.convertInputValue(49.9), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(50.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-50.1), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(50.1), 0.0);

		// 変換値 <- PLC値
		assertEquals("LA 50.0", fixture.convertStringValue(0.0));
		assertEquals("LA 50.0", fixture.convertStringValue(2.0));
		assertEquals("LA 50.1", fixture.convertStringValue(3.0));
		assertEquals("LA 99.9", fixture.convertStringValue(1997.0));
		assertEquals("   100.0", fixture.convertStringValue(1998.0));
		assertEquals("   100.0", fixture.convertStringValue(2000.0));
		assertEquals("   100.0", fixture.convertStringValue(2001.0));
		assertEquals("LE 99.9", fixture.convertStringValue(2002.0));
		assertEquals("LE 50.1", fixture.convertStringValue(3998.0));
		assertEquals("LE 50.0", fixture.convertStringValue(3999.0));
		assertEquals("LE 50.0", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("LA 50.0", fixture.convertStringValue(-10.0));
		assertEquals("LE 50.0", fixture.convertStringValue(4010.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LA 50.0"), 0.0);
		assertEquals(4.0, fixture.convertInputValue("LA 50.1"), 0.00001);
		assertEquals(1996.0, fixture.convertInputValue("LA 99.9"), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(2004.0, fixture.convertInputValue("LE 99.9"), 0.00001);
		assertEquals(3996.0, fixture.convertInputValue("LE 50.1"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LE 50.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LA 49.9"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LE 49.9"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE101.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA101.0"), 0.0);
		
		serialTest(fixture);
	}

	public void test_valueOfM0M100_LALE() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfLALE(-0.0, -100, 0, 200, " 0.0");
		assertEquals(
			"{cnvMin=-0.0 cnvMax=-100.0 inMin=0.0 inMax=200.0 format= 0.0 type=POWERFACTOR PFtype=LALE}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-101.0, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(-100.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-99.0, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(-1.0, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(0.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(1.0, fixture.convertDoubleValue(101.0), 0.0);
		assertEquals(99.0, fixture.convertDoubleValue(199.0), 0.0);
		assertEquals(100.0, fixture.convertDoubleValue(200.0), 0.0);
		assertEquals(101.0, fixture.convertDoubleValue(201.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-100.0), 0.0);
		assertEquals(1.0, fixture.convertInputValue(-99.0), 0.0);
		assertEquals(99.0, fixture.convertInputValue(-1.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(101.0, fixture.convertInputValue(1.0), 0.0);
		assertEquals(199.0, fixture.convertInputValue(99.0), 0.0);
		assertEquals(200.0, fixture.convertInputValue(100.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-101.0), 0.0);
		assertEquals(200.0, fixture.convertInputValue(101.0), 0.0);

		// 変換値 <- PLC値
		assertEquals("LA 0.0", fixture.convertStringValue(0.0));
		assertEquals("LA 1.0", fixture.convertStringValue(1.0));
		assertEquals("LA 99.0", fixture.convertStringValue(99.0));
		assertEquals("   100.0", fixture.convertStringValue(100.0));
		assertEquals("LE 99.0", fixture.convertStringValue(101.0));
		assertEquals("LE 1.0", fixture.convertStringValue(199.0));
		assertEquals("LE 0.0", fixture.convertStringValue(200.0));
		// 範囲外
		assertEquals("LA 0.0", fixture.convertStringValue(-1.0));
		assertEquals("LE 0.0", fixture.convertStringValue(201.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LA 0.0"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("LA 1.0"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("LA 99.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(101.0, fixture.convertInputValue("LE 99.0"), 0.0);
		assertEquals(199.0, fixture.convertInputValue("LE 1.0"), 0.0);
		assertEquals(200.0, fixture.convertInputValue("LE 0.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LA -1.0"), 0.0);
		assertEquals(200.0, fixture.convertInputValue("LE -1.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE101.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA101.0"), 0.0);

		// 変換率 1/2000
		fixture = ConvertValue.valueOfLALE(-0.0, -100, 0, 4000, " 0.0");
		assertEquals(
			"{cnvMin=-0.0 cnvMax=-100.0 inMin=0.0 inMax=4000.0 format= 0.0 type=POWERFACTOR PFtype=LALE}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-100.1, fixture.convertDoubleValue(-2.0), 0.0);
		assertEquals(-100.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-99.9, fixture.convertDoubleValue(2.0), 0.0);
		assertEquals(-0.1, fixture.convertDoubleValue(1998.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(0.1, fixture.convertDoubleValue(2002.0), 0.00000001);
		assertEquals(99.9, fixture.convertDoubleValue(3998.0), 0.00000001);
		assertEquals(100.0, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(100.1, fixture.convertDoubleValue(4002.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-100.0), 0.0);
		assertEquals(2.0, fixture.convertInputValue(-99.9), 0.00001);
		assertEquals(1998.0, fixture.convertInputValue(-0.1), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(2002.0, fixture.convertInputValue(0.1), 0.00001);
		assertEquals(3998.0, fixture.convertInputValue(99.9), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(100.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-100.1), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(100.1), 0.0);

		// 変換値 <- PLC値
		assertEquals("LA 0.0", fixture.convertStringValue(0.0));
		assertEquals("LA 0.1", fixture.convertStringValue(2.0));
		assertEquals("LA 0.2", fixture.convertStringValue(3.0));
		assertEquals("LA 99.9", fixture.convertStringValue(1997.0));
		assertEquals("LA 99.9", fixture.convertStringValue(1998.0));
		assertEquals("   100.0", fixture.convertStringValue(2000.0));
		assertEquals("LE 99.9", fixture.convertStringValue(2001.0));
		assertEquals("LE 99.9", fixture.convertStringValue(2002.0));
		assertEquals("LE 0.1", fixture.convertStringValue(3998.0));
		assertEquals("LE 0.0", fixture.convertStringValue(3999.0));
		assertEquals("LE 0.0", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("LA 0.0", fixture.convertStringValue(-10.0));
		assertEquals("LE 0.0", fixture.convertStringValue(4010.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LA 0.0"), 0.0);
		assertEquals(2.0, fixture.convertInputValue("LA 0.1"), 0.00001);
		assertEquals(1998.0, fixture.convertInputValue("LA 99.9"), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(2002.0, fixture.convertInputValue("LE 99.9"), 0.00001);
		assertEquals(3998.0, fixture.convertInputValue("LE 0.1"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LE 0.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LA -1.0"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LE -1.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE101.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA101.0"), 0.0);
		
		serialTest(fixture);
	}

	public void test_valueOfM05M1_LELA() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfLELA(-0.5, -1, 0, 100, "0.00");
		assertEquals(
			"{cnvMin=-0.5 cnvMax=-1.0 inMin=0.0 inMax=100.0 format=0.00 type=POWERFACTOR PFtype=LELA}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-0.51, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(-0.50, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-0.49, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(-0.01, fixture.convertDoubleValue(49.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(50.0), 0.0);
		assertEquals(0.01, fixture.convertDoubleValue(51.0), 0.00000001);
		assertEquals(0.49, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(0.50, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(0.51, fixture.convertDoubleValue(101.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-0.50), 0.0);
		assertEquals(1.0, fixture.convertInputValue(-0.49), 0.00001);
		assertEquals(49.0, fixture.convertInputValue(-0.01), 0.0);
		assertEquals(50.0, fixture.convertInputValue(0.00), 0.0);
		assertEquals(51.0, fixture.convertInputValue(0.01), 0.0);
		assertEquals(99.0, fixture.convertInputValue(0.49), 0.0);
		assertEquals(100.0, fixture.convertInputValue(0.50), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-0.51), 0.0);
		assertEquals(100.0, fixture.convertInputValue(0.51), 0.0);

		// 変換値 <- PLC値
		assertEquals("LE0.50", fixture.convertStringValue(0.0));
		assertEquals("LE0.51", fixture.convertStringValue(1.0));
		assertEquals("LE0.99", fixture.convertStringValue(49.0));
		assertEquals("  1.00", fixture.convertStringValue(50.0));
		assertEquals("LA0.99", fixture.convertStringValue(51.0));
		assertEquals("LA0.51", fixture.convertStringValue(99.0));
		assertEquals("LA0.50", fixture.convertStringValue(100.0));
		// 範囲外
		assertEquals("LE0.50", fixture.convertStringValue(-1.0));
		assertEquals("LA0.50", fixture.convertStringValue(101.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LE0.50"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("LE0.51"), 0.00001);
		assertEquals(49.0, fixture.convertInputValue("LE0.99"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("  1.00"), 0.0);
		assertEquals(51.0, fixture.convertInputValue("LA0.99"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("LA0.51"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA0.50"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LE0.49"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA0.49"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA1.00"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA1.01"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE1.00"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE1.01"), 0.0);

		// 変換率 1/4000
		fixture = ConvertValue.valueOfLELA(-0.5, -1, 0, 4000, "0.00");
		assertEquals(
			"{cnvMin=-0.5 cnvMax=-1.0 inMin=0.0 inMax=4000.0 format=0.00 type=POWERFACTOR PFtype=LELA}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-0.501, fixture.convertDoubleValue(-4.0), 0.0);
		assertEquals(-0.500, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-0.499, fixture.convertDoubleValue(4.0), 0.0);
		assertEquals(-0.001, fixture.convertDoubleValue(1996.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(0.001, fixture.convertDoubleValue(2004.0), 0.00000001);
		assertEquals(0.499, fixture.convertDoubleValue(3996.0), 0.0);
		assertEquals(0.500, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(0.501, fixture.convertDoubleValue(4004.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-0.500), 0.0);
		assertEquals(4.0, fixture.convertInputValue(-0.499), 0.00001);
		assertEquals(1996.0, fixture.convertInputValue(-0.001), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(0.000), 0.0);
		assertEquals(2004.0, fixture.convertInputValue(0.001), 0.0);
		assertEquals(3996.0, fixture.convertInputValue(0.499), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(0.500), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-0.501), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(0.501), 0.0);

		// 変換値 <- PLC値
		assertEquals("LE0.50", fixture.convertStringValue(0.0));
		assertEquals("LE0.50", fixture.convertStringValue(20.0));
		assertEquals("LE0.51", fixture.convertStringValue(21.0));
		assertEquals("LE0.99", fixture.convertStringValue(1979.0));
		assertEquals("  1.00", fixture.convertStringValue(1980.0));
		assertEquals("  1.00", fixture.convertStringValue(2000.0));
		assertEquals("  1.00", fixture.convertStringValue(2020.0));
		assertEquals("LA0.99", fixture.convertStringValue(2021.0));
		assertEquals("LA0.51", fixture.convertStringValue(3979.0));
		assertEquals("LA0.50", fixture.convertStringValue(3980.0));
		assertEquals("LA0.50", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("LE0.50", fixture.convertStringValue(-100.0));
		assertEquals("LA0.50", fixture.convertStringValue(4100.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LE0.50"), 0.0);
		assertEquals(40.0, fixture.convertInputValue("LE0.51"), 0.00001);
		assertEquals(1960.0, fixture.convertInputValue("LE0.99"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("  1.00"), 0.0);
		assertEquals(2040.0, fixture.convertInputValue("LA0.99"), 0.0);
		assertEquals(3960.0, fixture.convertInputValue("LA0.51"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LA0.50"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LE0.49"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LA0.49"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA1.00"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA1.01"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE1.00"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE1.01"), 0.0);
		
		serialTest(fixture);
	}

	public void test_valueOfM50M100_LELA() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfLELA(-50, -100, 0, 100, " 0.0");
		assertEquals(
			"{cnvMin=-50.0 cnvMax=-100.0 inMin=0.0 inMax=100.0 format= 0.0 type=POWERFACTOR PFtype=LELA}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-51.0, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(-50.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-49.0, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(-1.0, fixture.convertDoubleValue(49.0), 0.0);
		assertEquals(0.0, fixture.convertDoubleValue(50.0), 0.0);
		assertEquals(1.0, fixture.convertDoubleValue(51.0), 0.0);
		assertEquals(49.0, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(50.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(51.0, fixture.convertDoubleValue(101.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-50.0), 0.0);
		assertEquals(1.0, fixture.convertInputValue(-49.0), 0.0);
		assertEquals(49.0, fixture.convertInputValue(-1.0), 0.0);
		assertEquals(50.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(51.0, fixture.convertInputValue(1.0), 0.0);
		assertEquals(99.0, fixture.convertInputValue(49.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(50.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-51.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(51.0), 0.0);

		// 変換値 <- PLC値
		assertEquals("LE 50.0", fixture.convertStringValue(0.0));
		assertEquals("LE 51.0", fixture.convertStringValue(1.0));
		assertEquals("LE 99.0", fixture.convertStringValue(49.0));
		assertEquals("   100.0", fixture.convertStringValue(50.0));
		assertEquals("LA 99.0", fixture.convertStringValue(51.0));
		assertEquals("LA 51.0", fixture.convertStringValue(99.0));
		assertEquals("LA 50.0", fixture.convertStringValue(100.0));
		// 範囲外
		assertEquals("LE 50.0", fixture.convertStringValue(-1.0));
		assertEquals("LA 50.0", fixture.convertStringValue(101.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LE 50.0"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("LE 51.0"), 0.0);
		assertEquals(49.0, fixture.convertInputValue("LE 99.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(51.0, fixture.convertInputValue("LA 99.0"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("LA 51.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA 50.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LE 49.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA 49.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA101.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE101.0"), 0.0);

		// 変換率 1/4000
		fixture = ConvertValue.valueOfLELA(-50, -100, 0, 4000, " 0.0");
		assertEquals(
			"{cnvMin=-50.0 cnvMax=-100.0 inMin=0.0 inMax=4000.0 format= 0.0 type=POWERFACTOR PFtype=LELA}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-50.1, fixture.convertDoubleValue(-4.0), 0.0);
		assertEquals(-50.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-49.9, fixture.convertDoubleValue(4.0), 0.0);
		assertEquals(-0.1, fixture.convertDoubleValue(1996.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(0.1, fixture.convertDoubleValue(2004.0), 0.00000001);
		assertEquals(49.9, fixture.convertDoubleValue(3996.0), 0.00000001);
		assertEquals(50.0, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(50.1, fixture.convertDoubleValue(4004.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-50.0), 0.0);
		assertEquals(4.0, fixture.convertInputValue(-49.9), 0.00001);
		assertEquals(1996.0, fixture.convertInputValue(-0.1), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(2004.0, fixture.convertInputValue(0.1), 0.00001);
		assertEquals(3996.0, fixture.convertInputValue(49.9), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(50.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-50.1), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(50.1), 0.0);

		// 変換値 <- PLC値
		assertEquals("LE 50.0", fixture.convertStringValue(0.0));
		assertEquals("LE 50.0", fixture.convertStringValue(2.0));
		assertEquals("LE 50.1", fixture.convertStringValue(3.0));
		assertEquals("LE 99.9", fixture.convertStringValue(1997.0));
		assertEquals("   100.0", fixture.convertStringValue(1998.0));
		assertEquals("   100.0", fixture.convertStringValue(2000.0));
		assertEquals("   100.0", fixture.convertStringValue(2001.0));
		assertEquals("LA 99.9", fixture.convertStringValue(2002.0));
		assertEquals("LA 50.1", fixture.convertStringValue(3998.0));
		assertEquals("LA 50.0", fixture.convertStringValue(3999.0));
		assertEquals("LA 50.0", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("LE 50.0", fixture.convertStringValue(-10.0));
		assertEquals("LA 50.0", fixture.convertStringValue(4010.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LE 50.0"), 0.0);
		assertEquals(4.0, fixture.convertInputValue("LE 50.1"), 0.00001);
		assertEquals(1996.0, fixture.convertInputValue("LE 99.9"), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(2004.0, fixture.convertInputValue("LA 99.9"), 0.00001);
		assertEquals(3996.0, fixture.convertInputValue("LA 50.1"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LA 50.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LE 49.9"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LA 49.9"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA100.1"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE100.1"), 0.0);
		
		serialTest(fixture);
	}

	public void test_valueOfM0M100_LELA() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfLELA(-0.0, -100, 0, 200, " 0.0");
		assertEquals(
			"{cnvMin=-0.0 cnvMax=-100.0 inMin=0.0 inMax=200.0 format= 0.0 type=POWERFACTOR PFtype=LELA}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-101.0, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(-100.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-99.0, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(-1.0, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(0.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(1.0, fixture.convertDoubleValue(101.0), 0.0);
		assertEquals(99.0, fixture.convertDoubleValue(199.0), 0.0);
		assertEquals(100.0, fixture.convertDoubleValue(200.0), 0.0);
		assertEquals(101.0, fixture.convertDoubleValue(201.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-100.0), 0.0);
		assertEquals(1.0, fixture.convertInputValue(-99.0), 0.0);
		assertEquals(99.0, fixture.convertInputValue(-1.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(101.0, fixture.convertInputValue(1.0), 0.0);
		assertEquals(199.0, fixture.convertInputValue(99.0), 0.0);
		assertEquals(200.0, fixture.convertInputValue(100.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-101.0), 0.0);
		assertEquals(200.0, fixture.convertInputValue(101.0), 0.0);

		// 変換値 <- PLC値
		assertEquals("LE 0.0", fixture.convertStringValue(0.0));
		assertEquals("LE 1.0", fixture.convertStringValue(1.0));
		assertEquals("LE 99.0", fixture.convertStringValue(99.0));
		assertEquals("   100.0", fixture.convertStringValue(100.0));
		assertEquals("LA 99.0", fixture.convertStringValue(101.0));
		assertEquals("LA 1.0", fixture.convertStringValue(199.0));
		assertEquals("LA 0.0", fixture.convertStringValue(200.0));
		// 範囲外
		assertEquals("LE 0.0", fixture.convertStringValue(-1.0));
		assertEquals("LA 0.0", fixture.convertStringValue(201.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LE 0.0"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("LE 1.0"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("LE 99.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(101.0, fixture.convertInputValue("LA 99.0"), 0.0);
		assertEquals(199.0, fixture.convertInputValue("LA 1.0"), 0.0);
		assertEquals(200.0, fixture.convertInputValue("LA 0.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LE -1.0"), 0.0);
		assertEquals(200.0, fixture.convertInputValue("LA -1.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA101.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE101.0"), 0.0);

		// 変換率 1/2000
		fixture = ConvertValue.valueOfLELA(-0.0, -100, 0, 4000, " 0.0");
		assertEquals(
			"{cnvMin=-0.0 cnvMax=-100.0 inMin=0.0 inMax=4000.0 format= 0.0 type=POWERFACTOR PFtype=LELA}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(-100.1, fixture.convertDoubleValue(-2.0), 0.0);
		assertEquals(-100.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(-99.9, fixture.convertDoubleValue(2.0), 0.0);
		assertEquals(-0.1, fixture.convertDoubleValue(1998.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(0.1, fixture.convertDoubleValue(2002.0), 0.00000001);
		assertEquals(99.9, fixture.convertDoubleValue(3998.0), 0.00000001);
		assertEquals(100.0, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(100.1, fixture.convertDoubleValue(4002.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(-100.0), 0.0);
		assertEquals(2.0, fixture.convertInputValue(-99.9), 0.00001);
		assertEquals(1998.0, fixture.convertInputValue(-0.1), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(2002.0, fixture.convertInputValue(0.1), 0.00001);
		assertEquals(3998.0, fixture.convertInputValue(99.9), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(100.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(-100.1), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(100.1), 0.0);

		// 変換値 <- PLC値
		assertEquals("LE 0.0", fixture.convertStringValue(0.0));
		assertEquals("LE 0.1", fixture.convertStringValue(2.0));
		assertEquals("LE 0.2", fixture.convertStringValue(3.0));
		assertEquals("LE 99.9", fixture.convertStringValue(1997.0));
		assertEquals("LE 99.9", fixture.convertStringValue(1998.0));
		assertEquals("   100.0", fixture.convertStringValue(2000.0));
		assertEquals("LA 99.9", fixture.convertStringValue(2001.0));
		assertEquals("LA 99.9", fixture.convertStringValue(2002.0));
		assertEquals("LA 0.1", fixture.convertStringValue(3998.0));
		assertEquals("LA 0.0", fixture.convertStringValue(3999.0));
		assertEquals("LA 0.0", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("LE 0.0", fixture.convertStringValue(-10.0));
		assertEquals("LA 0.0", fixture.convertStringValue(4010.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LE 0.0"), 0.0);
		assertEquals(2.0, fixture.convertInputValue("LE 0.1"), 0.00001);
		assertEquals(1998.0, fixture.convertInputValue("LE 99.9"), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(2002.0, fixture.convertInputValue("LA 99.9"), 0.00001);
		assertEquals(3998.0, fixture.convertInputValue("LA 0.1"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LA 0.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LE -1.0"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LA -1.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA100.1"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE100.1"), 0.0);
		
		serialTest(fixture);
	}

	/*********************************************
	 * 力率
	 */
	public void test_valueOfP05P1_DECIMAL() throws Exception {
		/** DECIMAL */
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfDECIMAL(0.5, 1, 0, 100, "0.00");
		assertEquals(
			"{cnvMin=0.5 cnvMax=1.0 inMin=0.0 inMax=100.0 format=0.00 type=POWERFACTOR PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(0.51, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(0.50, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(0.49, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(0.01, fixture.convertDoubleValue(49.0), 0.00001);
		assertEquals(0.0, fixture.convertDoubleValue(50.0), 0.0);
		assertEquals(-0.01, fixture.convertDoubleValue(51.0), 0.00001);
		assertEquals(-0.49, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(-0.50, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(-0.51, fixture.convertDoubleValue(101.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(0.50), 0.0);
		assertEquals(1.0, fixture.convertInputValue(0.49), 0.00001);
		assertEquals(49.0, fixture.convertInputValue(0.01), 0.0);
		assertEquals(50.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(51.0, fixture.convertInputValue(-0.01), 0.0);
		assertEquals(99.0, fixture.convertInputValue(-0.49), 0.0);
		assertEquals(100.0, fixture.convertInputValue(-0.50), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(0.51), 0.0);
		assertEquals(100.0, fixture.convertInputValue(-0.51), 0.0);

		// 変換値 <- PLC値
		assertEquals("0.50", fixture.convertStringValue(0.0));
		assertEquals("0.51", fixture.convertStringValue(1.0));
		assertEquals("0.99", fixture.convertStringValue(49.0));
		assertEquals("1.00", fixture.convertStringValue(50.0));
		assertEquals("-0.99", fixture.convertStringValue(51.0));
		assertEquals("-0.51", fixture.convertStringValue(99.0));
		assertEquals("-0.50", fixture.convertStringValue(100.0));
		// 範囲外
		assertEquals("0.50", fixture.convertStringValue(-1.0));
		assertEquals("-0.50", fixture.convertStringValue(101.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("0.50"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("0.51"), 0.00001);
		assertEquals(49.0, fixture.convertInputValue("0.99"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("1.00"), 0.0);
		assertEquals(51.0, fixture.convertInputValue("-0.99"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("-0.51"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("-0.50"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("0.49"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("-0.49"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("1.10"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("1.00"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("-1.10"), 0.0);

		// 変換率 1/4000
		fixture = ConvertValue.valueOfDECIMAL(0.5, 1, 0, 4000, "#0.000");
		assertEquals(
			"{cnvMin=0.5 cnvMax=1.0 inMin=0.0 inMax=4000.0 format=#0.000 type=POWERFACTOR PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(0.501, fixture.convertDoubleValue(-4.0), 0.0);
		assertEquals(0.500, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(0.499, fixture.convertDoubleValue(4.0), 0.0);
		assertEquals(0.001, fixture.convertDoubleValue(1996.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(-0.001, fixture.convertDoubleValue(2004.0), 0.00000001);
		assertEquals(-0.499, fixture.convertDoubleValue(3996.0), 0.0);
		assertEquals(-0.500, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(-0.501, fixture.convertDoubleValue(4004.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(0.500), 0.0000);
		assertEquals(4.0, fixture.convertInputValue(0.499), 0.000001);
		assertEquals(1996.0, fixture.convertInputValue(0.001), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(2004.0, fixture.convertInputValue(-0.001), 0.0);
		assertEquals(3996.0, fixture.convertInputValue(-0.499), 0.0000);
		assertEquals(4000.0, fixture.convertInputValue(-0.500), 0.0000);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(0.501), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-0.501), 0.0);

		// 変換値 <- PLC値
		assertEquals("0.500", fixture.convertStringValue(0.0));
		assertEquals("0.500", fixture.convertStringValue(2.0));
		assertEquals("0.501", fixture.convertStringValue(3.0));
		assertEquals("0.999", fixture.convertStringValue(1997.0));
		assertEquals("1.000", fixture.convertStringValue(1998.0));
		assertEquals("1.000", fixture.convertStringValue(2000.0));
		assertEquals("1.000", fixture.convertStringValue(2001.0));
		assertEquals("-0.999", fixture.convertStringValue(2002.0));
		assertEquals("-0.501", fixture.convertStringValue(3997.0));
		assertEquals("-0.500", fixture.convertStringValue(3998.0));
		assertEquals("-0.500", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("0.500", fixture.convertStringValue(-10.0));
		assertEquals("-0.500", fixture.convertStringValue(4010.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(" 0.500"), 0.0);
		assertEquals(4.0, fixture.convertInputValue(" 0.501"), 0.00001);
		assertEquals(1996.0, fixture.convertInputValue(" 0.999"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(" 1.000"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("-1.000"), 0.0);
		assertEquals(2004.0, fixture.convertInputValue("-0.999"), 0.00001);
		assertEquals(3996.0, fixture.convertInputValue("-0.501"), 0.00001);
		assertEquals(4000.0, fixture.convertInputValue("-0.500"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(" 0.499"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("-0.499"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("-1.100"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(" 1.000"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(" 1.100"), 0.0);

		// フォーマット指定
		fixture = ConvertValue.valueOfDECIMAL(0.5, 1, 0, 100, "0.00");
		assertEquals(
			"{cnvMin=0.5 cnvMax=1.0 inMin=0.0 inMax=100.0 format=0.00 type=POWERFACTOR PFtype=DECIMAL}",
			fixture.toString());
		// 変換値 <- PLC値
		assertEquals("1.00", fixture.convertStringValue(50.0));
		assertEquals("-0.99", fixture.convertStringValue(51.0));

		assertEquals("1.0", fixture.convertStringValue(50.0, "0.0"));
		assertEquals("-0.990", fixture.convertStringValue(51.0, "##0.000"));
		
		serialTest(fixture);
	}

	public void test_valueOfP50P100_DECIMAL() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfDECIMAL(50, 100, 0, 100, "0.0");
		assertEquals(
			"{cnvMin=50.0 cnvMax=100.0 inMin=0.0 inMax=100.0 format=0.0 type=POWERFACTOR PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(51.0, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(50.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(49.0, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(1.0, fixture.convertDoubleValue(49.0), 0.0);
		assertEquals(0.0, fixture.convertDoubleValue(50.0), 0.0);
		assertEquals(-1.0, fixture.convertDoubleValue(51.0), 0.0);
		assertEquals(-49.0, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(-50.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(-51.0, fixture.convertDoubleValue(101.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(50.0), 0.0);
		assertEquals(1.0, fixture.convertInputValue(49.0), 0.0);
		assertEquals(49.0, fixture.convertInputValue(1.0), 0.0);
		assertEquals(50.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(51.0, fixture.convertInputValue(-1.0), 0.0);
		assertEquals(99.0, fixture.convertInputValue(-49.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(-50.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(51.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(-51.0), 0.0);

		// 変換値 <- PLC値
		assertEquals("50.0", fixture.convertStringValue(0.0));
		assertEquals("51.0", fixture.convertStringValue(1.0));
		assertEquals("99.0", fixture.convertStringValue(49.0));
		assertEquals("100.0", fixture.convertStringValue(50.0));
		assertEquals("-99.0", fixture.convertStringValue(51.0));
		assertEquals("-51.0", fixture.convertStringValue(99.0));
		assertEquals("-50.0", fixture.convertStringValue(100.0));
		// 範囲外
		assertEquals("50.0", fixture.convertStringValue(-1.0));
		assertEquals("-50.0", fixture.convertStringValue(101.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("50.0"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("51.0"), 0.0);
		assertEquals(49.0, fixture.convertInputValue("99.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("100.0"), 0.0);
		assertEquals(51.0, fixture.convertInputValue("-99.0"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("-51.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("-50.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("49.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("-49.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("-101.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("100.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("101.0"), 0.0);

		// 変換率 1/4000
		fixture = ConvertValue.valueOfDECIMAL(50, 100, 0, 4000, "0.0");
		assertEquals(
			"{cnvMin=50.0 cnvMax=100.0 inMin=0.0 inMax=4000.0 format=0.0 type=POWERFACTOR PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(50.1, fixture.convertDoubleValue(-4.0), 0.0);
		assertEquals(50.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(49.9, fixture.convertDoubleValue(4.0), 0.0);
		assertEquals(0.1, fixture.convertDoubleValue(1996.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(-0.1, fixture.convertDoubleValue(2004.0), 0.00000001);
		assertEquals(-49.9, fixture.convertDoubleValue(3996.0), 0.00000001);
		assertEquals(-50.0, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(-50.1, fixture.convertDoubleValue(4004.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(50.0), 0.0);
		assertEquals(4.0, fixture.convertInputValue(49.9), 0.001);
		assertEquals(1996.0, fixture.convertInputValue(0.1), 0.00);
		assertEquals(2000.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(2004.0, fixture.convertInputValue(-0.1), 0.00);
		assertEquals(3996.0, fixture.convertInputValue(-49.9), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-50.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(50.1), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-50.1), 0.0);

		// 変換値 <- PLC値
		assertEquals("50.0", fixture.convertStringValue(0.0));
		assertEquals("50.0", fixture.convertStringValue(2.0));
		assertEquals("50.1", fixture.convertStringValue(3.0));
		assertEquals("99.9", fixture.convertStringValue(1997.0));
		assertEquals("100.0", fixture.convertStringValue(1998.0));
		assertEquals("100.0", fixture.convertStringValue(2000.0));
		assertEquals("100.0", fixture.convertStringValue(2001.0));
		assertEquals("-99.9", fixture.convertStringValue(2002.0));
		assertEquals("-50.1", fixture.convertStringValue(3998.0));
		assertEquals("-50.0", fixture.convertStringValue(3999.0));
		assertEquals("-50.0", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("50.0", fixture.convertStringValue(-10.0));
		assertEquals("-50.0", fixture.convertStringValue(4010.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("50.0"), 0.0);
		assertEquals(4.0, fixture.convertInputValue("50.1"), 0.001);
		assertEquals(1996.0, fixture.convertInputValue("99.9"), 0.001);
		assertEquals(2000.0, fixture.convertInputValue("100.0"), 0.0);
		assertEquals(2004.0, fixture.convertInputValue("-99.9"), 0.001);
		assertEquals(3996.0, fixture.convertInputValue("-50.1"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("-50.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("49.9"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("-49.9"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("-100.1"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(" 100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(" 100.1"), 0.0);
		
		serialTest(fixture);
	}

	public void test_valueOfP0P100_DECIMAL() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfDECIMAL(0, 100, 0, 200, "0.0");
		assertEquals(
			"{cnvMin=0.0 cnvMax=100.0 inMin=0.0 inMax=200.0 format=0.0 type=POWERFACTOR PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(101.0, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(100.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(99.0, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(1.0, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(0.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(-1.0, fixture.convertDoubleValue(101.0), 0.0);
		assertEquals(-99.0, fixture.convertDoubleValue(199.0), 0.0);
		assertEquals(-100.0, fixture.convertDoubleValue(200.0), 0.0);
		assertEquals(-101.0, fixture.convertDoubleValue(201.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(100.0), 0.0);
		assertEquals(1.0, fixture.convertInputValue(99.0), 0.0);
		assertEquals(99.0, fixture.convertInputValue(1.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(101.0, fixture.convertInputValue(-1.0), 0.0);
		assertEquals(199.0, fixture.convertInputValue(-99.0), 0.0);
		assertEquals(200.0, fixture.convertInputValue(-100.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(101.0), 0.0);
		assertEquals(200.0, fixture.convertInputValue(-101.0), 0.0);

		// 変換値 <- PLC値
		assertEquals("0.0", fixture.convertStringValue(0.0));
		assertEquals("1.0", fixture.convertStringValue(1.0));
		assertEquals("99.0", fixture.convertStringValue(99.0));
		assertEquals("100.0", fixture.convertStringValue(100.0));
		assertEquals("-99.0", fixture.convertStringValue(101.0));
		assertEquals("-1.0", fixture.convertStringValue(199.0));
		assertEquals("-0.0", fixture.convertStringValue(200.0));
		// 範囲外
		assertEquals("0.0", fixture.convertStringValue(-1.0));
		assertEquals("-0.0", fixture.convertStringValue(201.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("0.0"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("1.0"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("99.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("100.0"), 0.0);
		assertEquals(101.0, fixture.convertInputValue("-99.0"), 0.0);
		assertEquals(199.0, fixture.convertInputValue("-1.0"), 0.0);
		assertEquals(200.0, fixture.convertInputValue("-0.0"), 0.0);
		// 範囲外
		assertEquals(100.0, fixture.convertInputValue("-101.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("100.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("101.0"), 0.0);

		// 変換率 1/2000
		fixture = ConvertValue.valueOfDECIMAL(0, 100, 0, 4000, "0.0");
		assertEquals(
			"{cnvMin=0.0 cnvMax=100.0 inMin=0.0 inMax=4000.0 format=0.0 type=POWERFACTOR PFtype=DECIMAL}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(100.1, fixture.convertDoubleValue(-2.0), 0.0);
		assertEquals(100.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(99.9, fixture.convertDoubleValue(2.0), 0.0);
		assertEquals(0.1, fixture.convertDoubleValue(1998.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(-0.1, fixture.convertDoubleValue(2002.0), 0.00000001);
		assertEquals(-99.9, fixture.convertDoubleValue(3998.0), 0.00000001);
		assertEquals(-100.0, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(-100.1, fixture.convertDoubleValue(4002.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(100.0), 0.0);
		assertEquals(2.0, fixture.convertInputValue(99.9), 0.001);
		assertEquals(1998.0, fixture.convertInputValue(0.1), 0.00);
		assertEquals(2000.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(2002.0, fixture.convertInputValue(-0.1), 0.00);
		assertEquals(3998.0, fixture.convertInputValue(-99.9), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-100.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(100.1), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-100.1), 0.0);

		// 変換値 <- PLC値
		assertEquals("0.0", fixture.convertStringValue(0.0));
		assertEquals("0.1", fixture.convertStringValue(2.0));
		assertEquals("0.2", fixture.convertStringValue(3.0));
		assertEquals("99.9", fixture.convertStringValue(1997.0));
		assertEquals("99.9", fixture.convertStringValue(1998.0));
		assertEquals("100.0", fixture.convertStringValue(2000.0));
		assertEquals("-99.9", fixture.convertStringValue(2001.0));
		assertEquals("-99.9", fixture.convertStringValue(2002.0));
		assertEquals("-0.1", fixture.convertStringValue(3998.0));
		assertEquals("-0.0", fixture.convertStringValue(3999.0));
		assertEquals("-0.0", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("0.0", fixture.convertStringValue(-10.0));
		assertEquals("-0.0", fixture.convertStringValue(4010.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("0.0"), 0.0);
		assertEquals(2.0, fixture.convertInputValue("0.1"), 0.001);
		assertEquals(1998.0, fixture.convertInputValue("99.9"), 0.001);
		assertEquals(2000.0, fixture.convertInputValue("100.0"), 0.0);
		assertEquals(2002.0, fixture.convertInputValue("-99.9"), 0.001);
		assertEquals(3998.0, fixture.convertInputValue("-0.1"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("-0.0"), 0.0);
		// 範囲外
		assertEquals(2000.0, fixture.convertInputValue("-100.1"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(" 100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(" 100.1"), 0.0);
		
		serialTest(fixture);
	}

	public void test_valueOfP05P1_LALE() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfLALE(0.5, 1, 0, 100, "0.00");
		assertEquals(
			"{cnvMin=0.5 cnvMax=1.0 inMin=0.0 inMax=100.0 format=0.00 type=POWERFACTOR PFtype=LALE}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(0.51, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(0.50, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(0.49, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(0.01, fixture.convertDoubleValue(49.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(50.0), 0.0);
		assertEquals(-0.01, fixture.convertDoubleValue(51.0), 0.00000001);
		assertEquals(-0.49, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(-0.50, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(-0.51, fixture.convertDoubleValue(101.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(0.50), 0.0);
		assertEquals(1.0, fixture.convertInputValue(0.49), 0.00000001);
		assertEquals(49.0, fixture.convertInputValue(0.01), 0.0);
		assertEquals(50.0, fixture.convertInputValue(0.00), 0.0);
		assertEquals(51.0, fixture.convertInputValue(-0.01), 0.0);
		assertEquals(99.0, fixture.convertInputValue(-0.49), 0.0);
		assertEquals(100.0, fixture.convertInputValue(-0.50), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(0.51), 0.0);
		assertEquals(100.0, fixture.convertInputValue(-0.51), 0.0);

		// 変換値 <- PLC値
		assertEquals("LA0.50", fixture.convertStringValue(0.0));
		assertEquals("LA0.51", fixture.convertStringValue(1.0));
		assertEquals("LA0.99", fixture.convertStringValue(49.0));
		assertEquals("  1.00", fixture.convertStringValue(50.0));
		assertEquals("LE0.99", fixture.convertStringValue(51.0));
		assertEquals("LE0.51", fixture.convertStringValue(99.0));
		assertEquals("LE0.50", fixture.convertStringValue(100.0));
		// 範囲外
		assertEquals("LA0.50", fixture.convertStringValue(-1.0));
		assertEquals("LE0.50", fixture.convertStringValue(101.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LA0.50"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("LA0.51"), 0.00001);
		assertEquals(49.0, fixture.convertInputValue("LA0.99"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("  1.00"), 0.0);
		assertEquals(51.0, fixture.convertInputValue("LE0.99"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("LE0.51"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE0.50"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LA0.49"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE0.49"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE1.00"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE1.01"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA1.00"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA1.01"), 0.0);

		// 変換率 1/4000
		fixture = ConvertValue.valueOfLALE(0.5, 1, 0, 4000, "0.00");
		assertEquals(
			"{cnvMin=0.5 cnvMax=1.0 inMin=0.0 inMax=4000.0 format=0.00 type=POWERFACTOR PFtype=LALE}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(0.501, fixture.convertDoubleValue(-4.0), 0.0);
		assertEquals(0.500, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(0.499, fixture.convertDoubleValue(4.0), 0.0);
		assertEquals(0.001, fixture.convertDoubleValue(1996.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(-0.001, fixture.convertDoubleValue(2004.0), 0.00000001);
		assertEquals(-0.499, fixture.convertDoubleValue(3996.0), 0.0);
		assertEquals(-0.500, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(-0.501, fixture.convertDoubleValue(4004.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(0.500), 0.0);
		assertEquals(4.0, fixture.convertInputValue(0.499), 0.00001);
		assertEquals(1996.0, fixture.convertInputValue(0.001), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(0.000), 0.0);
		assertEquals(2004.0, fixture.convertInputValue(-0.001), 0.0);
		assertEquals(3996.0, fixture.convertInputValue(-0.499), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-0.500), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(0.501), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-0.501), 0.0);

		// 変換値 <- PLC値
		assertEquals("LA0.50", fixture.convertStringValue(0.0));
		assertEquals("LA0.50", fixture.convertStringValue(20.0));
		assertEquals("LA0.51", fixture.convertStringValue(21.0));
		assertEquals("LA0.99", fixture.convertStringValue(1979.0));
		assertEquals("  1.00", fixture.convertStringValue(1980.0));
		assertEquals("  1.00", fixture.convertStringValue(2000.0));
		assertEquals("  1.00", fixture.convertStringValue(2020.0));
		assertEquals("LE0.99", fixture.convertStringValue(2021.0));
		assertEquals("LE0.51", fixture.convertStringValue(3979.0));
		assertEquals("LE0.50", fixture.convertStringValue(3980.0));
		assertEquals("LE0.50", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("LA0.50", fixture.convertStringValue(-100.0));
		assertEquals("LE0.50", fixture.convertStringValue(4100.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LA0.50"), 0.0);
		assertEquals(40.0, fixture.convertInputValue("LA0.51"), 0.00001);
		assertEquals(1960.0, fixture.convertInputValue("LA0.99"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("  1.00"), 0.0);
		assertEquals(2040.0, fixture.convertInputValue("LE0.99"), 0.0);
		assertEquals(3960.0, fixture.convertInputValue("LE0.51"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LE0.50"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LA0.49"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LE0.49"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE1.00"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE1.01"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA1.00"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA1.01"), 0.0);

		// フォーマット指定
		fixture = ConvertValue.valueOfLALE(0.5, 1, 0, 100, "0.00");
		assertEquals(
			"{cnvMin=0.5 cnvMax=1.0 inMin=0.0 inMax=100.0 format=0.00 type=POWERFACTOR PFtype=LALE}",
			fixture.toString());
		// 変換値 <- PLC値
		assertEquals("  1.00", fixture.convertStringValue(50.0));
		assertEquals("LE0.99", fixture.convertStringValue(51.0));

		assertEquals("  1", fixture.convertStringValue(50.0, "0"));
		assertEquals("LE  0.990", fixture.convertStringValue(51.0, "  0.000"));
		
		serialTest(fixture);
	}

	public void test_valueOfP50P100_LALE() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfLALE(50, 100, 0, 100, " 0.0");
		assertEquals(
			"{cnvMin=50.0 cnvMax=100.0 inMin=0.0 inMax=100.0 format= 0.0 type=POWERFACTOR PFtype=LALE}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(51.0, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(50.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(49.0, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(1.0, fixture.convertDoubleValue(49.0), 0.0);
		assertEquals(0.0, fixture.convertDoubleValue(50.0), 0.0);
		assertEquals(-1.0, fixture.convertDoubleValue(51.0), 0.0);
		assertEquals(-49.0, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(-50.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(-51.0, fixture.convertDoubleValue(101.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(50.0), 0.0);
		assertEquals(1.0, fixture.convertInputValue(49.0), 0.0);
		assertEquals(49.0, fixture.convertInputValue(1.0), 0.0);
		assertEquals(50.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(51.0, fixture.convertInputValue(-1.0), 0.0);
		assertEquals(99.0, fixture.convertInputValue(-49.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(-50.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(51.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(-51.0), 0.0);

		// 変換値 <- PLC値
		assertEquals("LA 50.0", fixture.convertStringValue(0.0));
		assertEquals("LA 51.0", fixture.convertStringValue(1.0));
		assertEquals("LA 99.0", fixture.convertStringValue(49.0));
		assertEquals("   100.0", fixture.convertStringValue(50.0));
		assertEquals("LE 99.0", fixture.convertStringValue(51.0));
		assertEquals("LE 51.0", fixture.convertStringValue(99.0));
		assertEquals("LE 50.0", fixture.convertStringValue(100.0));
		// 範囲外
		assertEquals("LA 50.0", fixture.convertStringValue(-1.0));
		assertEquals("LE 50.0", fixture.convertStringValue(101.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LA 50.0"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("LA 51.0"), 0.0);
		assertEquals(49.0, fixture.convertInputValue("LA 99.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(51.0, fixture.convertInputValue("LE 99.0"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("LE 51.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE 50.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LA 49.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE 49.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE101.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA101.0"), 0.0);

		// 変換率 1/4000
		fixture = ConvertValue.valueOfLALE(50, 100, 0, 4000, " 0.0");
		assertEquals(
			"{cnvMin=50.0 cnvMax=100.0 inMin=0.0 inMax=4000.0 format= 0.0 type=POWERFACTOR PFtype=LALE}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(50.1, fixture.convertDoubleValue(-4.0), 0.0);
		assertEquals(50.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(49.9, fixture.convertDoubleValue(4.0), 0.0);
		assertEquals(0.1, fixture.convertDoubleValue(1996.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(-0.1, fixture.convertDoubleValue(2004.0), 0.00000001);
		assertEquals(-49.9, fixture.convertDoubleValue(3996.0), 0.00000001);
		assertEquals(-50.0, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(-50.1, fixture.convertDoubleValue(4004.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(50.0), 0.0);
		assertEquals(4.0, fixture.convertInputValue(49.9), 0.00001);
		assertEquals(1996.0, fixture.convertInputValue(0.1), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(2004.0, fixture.convertInputValue(-0.1), 0.00001);
		assertEquals(3996.0, fixture.convertInputValue(-49.9), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-50.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(50.1), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-50.1), 0.0);

		// 変換値 <- PLC値
		assertEquals("LA 50.0", fixture.convertStringValue(0.0));
		assertEquals("LA 50.0", fixture.convertStringValue(2.0));
		assertEquals("LA 50.1", fixture.convertStringValue(3.0));
		assertEquals("LA 99.9", fixture.convertStringValue(1997.0));
		assertEquals("   100.0", fixture.convertStringValue(1998.0));
		assertEquals("   100.0", fixture.convertStringValue(2000.0));
		assertEquals("   100.0", fixture.convertStringValue(2001.0));
		assertEquals("LE 99.9", fixture.convertStringValue(2002.0));
		assertEquals("LE 50.1", fixture.convertStringValue(3998.0));
		assertEquals("LE 50.0", fixture.convertStringValue(3999.0));
		assertEquals("LE 50.0", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("LA 50.0", fixture.convertStringValue(-10.0));
		assertEquals("LE 50.0", fixture.convertStringValue(4010.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LA 50.0"), 0.0);
		assertEquals(4.0, fixture.convertInputValue("LA 50.1"), 0.00001);
		assertEquals(1996.0, fixture.convertInputValue("LA 99.9"), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(2004.0, fixture.convertInputValue("LE 99.9"), 0.00001);
		assertEquals(3996.0, fixture.convertInputValue("LE 50.1"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LE 50.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LA 49.9"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LE 49.9"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE101.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA101.0"), 0.0);
		
		serialTest(fixture);
	}

	public void test_valueOfP0P100_LALE() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfLALE(0, 100, 0, 200, " 0.0");
		assertEquals(
			"{cnvMin=0.0 cnvMax=100.0 inMin=0.0 inMax=200.0 format= 0.0 type=POWERFACTOR PFtype=LALE}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(101.0, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(100.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(99.0, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(1.0, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(0.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(-1.0, fixture.convertDoubleValue(101.0), 0.0);
		assertEquals(-99.0, fixture.convertDoubleValue(199.0), 0.0);
		assertEquals(-100.0, fixture.convertDoubleValue(200.0), 0.0);
		assertEquals(-101.0, fixture.convertDoubleValue(201.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(100.0), 0.0);
		assertEquals(1.0, fixture.convertInputValue(99.0), 0.0);
		assertEquals(99.0, fixture.convertInputValue(1.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(101.0, fixture.convertInputValue(-1.0), 0.0);
		assertEquals(199.0, fixture.convertInputValue(-99.0), 0.0);
		assertEquals(200.0, fixture.convertInputValue(-100.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(101.0), 0.0);
		assertEquals(200.0, fixture.convertInputValue(-101.0), 0.0);

		// 変換値 <- PLC値
		assertEquals("LA 0.0", fixture.convertStringValue(0.0));
		assertEquals("LA 1.0", fixture.convertStringValue(1.0));
		assertEquals("LA 99.0", fixture.convertStringValue(99.0));
		assertEquals("   100.0", fixture.convertStringValue(100.0));
		assertEquals("LE 99.0", fixture.convertStringValue(101.0));
		assertEquals("LE 1.0", fixture.convertStringValue(199.0));
		assertEquals("LE 0.0", fixture.convertStringValue(200.0));
		// 範囲外
		assertEquals("LA 0.0", fixture.convertStringValue(-1.0));
		assertEquals("LE 0.0", fixture.convertStringValue(201.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LA 0.0"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("LA 1.0"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("LA 99.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(101.0, fixture.convertInputValue("LE 99.0"), 0.0);
		assertEquals(199.0, fixture.convertInputValue("LE 1.0"), 0.0);
		assertEquals(200.0, fixture.convertInputValue("LE 0.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LA -1.0"), 0.0);
		assertEquals(200.0, fixture.convertInputValue("LE -1.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE101.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA101.0"), 0.0);

		// 変換率 1/2000
		fixture = ConvertValue.valueOfLALE(0, 100, 0, 4000, " 0.0");
		assertEquals(
			"{cnvMin=0.0 cnvMax=100.0 inMin=0.0 inMax=4000.0 format= 0.0 type=POWERFACTOR PFtype=LALE}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(100.1, fixture.convertDoubleValue(-2.0), 0.0);
		assertEquals(100.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(99.9, fixture.convertDoubleValue(2.0), 0.0);
		assertEquals(0.1, fixture.convertDoubleValue(1998.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(-0.1, fixture.convertDoubleValue(2002.0), 0.00000001);
		assertEquals(-99.9, fixture.convertDoubleValue(3998.0), 0.00000001);
		assertEquals(-100.0, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(-100.1, fixture.convertDoubleValue(4002.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(100.0), 0.0);
		assertEquals(2.0, fixture.convertInputValue(99.9), 0.00001);
		assertEquals(1998.0, fixture.convertInputValue(0.1), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(2002.0, fixture.convertInputValue(-0.1), 0.00001);
		assertEquals(3998.0, fixture.convertInputValue(-99.9), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-100.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(100.1), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-100.1), 0.0);

		// 変換値 <- PLC値
		assertEquals("LA 0.0", fixture.convertStringValue(0.0));
		assertEquals("LA 0.1", fixture.convertStringValue(2.0));
		assertEquals("LA 0.2", fixture.convertStringValue(3.0));
		assertEquals("LA 99.9", fixture.convertStringValue(1997.0));
		assertEquals("LA 99.9", fixture.convertStringValue(1998.0));
		assertEquals("   100.0", fixture.convertStringValue(2000.0));
		assertEquals("LE 99.9", fixture.convertStringValue(2001.0));
		assertEquals("LE 99.9", fixture.convertStringValue(2002.0));
		assertEquals("LE 0.1", fixture.convertStringValue(3998.0));
		assertEquals("LE 0.0", fixture.convertStringValue(3999.0));
		assertEquals("LE 0.0", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("LA 0.0", fixture.convertStringValue(-10.0));
		assertEquals("LE 0.0", fixture.convertStringValue(4010.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LA 0.0"), 0.0);
		assertEquals(2.0, fixture.convertInputValue("LA 0.1"), 0.00001);
		assertEquals(1998.0, fixture.convertInputValue("LA 99.9"), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(2002.0, fixture.convertInputValue("LE 99.9"), 0.00001);
		assertEquals(3998.0, fixture.convertInputValue("LE 0.1"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LE 0.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LA -1.0"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LE -1.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE101.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA101.0"), 0.0);
		
		serialTest(fixture);
	}

	public void test_valueOfP05P1_LELA() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfLELA(0.5, 1, 0, 100, "0.00");
		assertEquals(
			"{cnvMin=0.5 cnvMax=1.0 inMin=0.0 inMax=100.0 format=0.00 type=POWERFACTOR PFtype=LELA}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(0.51, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(0.50, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(0.49, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(0.01, fixture.convertDoubleValue(49.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(50.0), 0.0);
		assertEquals(-0.01, fixture.convertDoubleValue(51.0), 0.00000001);
		assertEquals(-0.49, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(-0.50, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(-0.51, fixture.convertDoubleValue(101.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(0.50), 0.0);
		assertEquals(1.0, fixture.convertInputValue(0.49), 0.00001);
		assertEquals(49.0, fixture.convertInputValue(0.01), 0.0);
		assertEquals(50.0, fixture.convertInputValue(0.00), 0.0);
		assertEquals(51.0, fixture.convertInputValue(-0.01), 0.0);
		assertEquals(99.0, fixture.convertInputValue(-0.49), 0.0);
		assertEquals(100.0, fixture.convertInputValue(-0.50), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(0.51), 0.0);
		assertEquals(100.0, fixture.convertInputValue(-0.51), 0.0);

		// 変換値 <- PLC値
		assertEquals("LE0.50", fixture.convertStringValue(0.0));
		assertEquals("LE0.51", fixture.convertStringValue(1.0));
		assertEquals("LE0.99", fixture.convertStringValue(49.0));
		assertEquals("  1.00", fixture.convertStringValue(50.0));
		assertEquals("LA0.99", fixture.convertStringValue(51.0));
		assertEquals("LA0.51", fixture.convertStringValue(99.0));
		assertEquals("LA0.50", fixture.convertStringValue(100.0));
		// 範囲外
		assertEquals("LE0.50", fixture.convertStringValue(-1.0));
		assertEquals("LA0.50", fixture.convertStringValue(101.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LE0.50"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("LE0.51"), 0.00001);
		assertEquals(49.0, fixture.convertInputValue("LE0.99"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("  1.00"), 0.0);
		assertEquals(51.0, fixture.convertInputValue("LA0.99"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("LA0.51"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA0.50"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LE0.49"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA0.49"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA1.00"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA1.01"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE1.00"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE1.01"), 0.0);

		// 変換率 1/4000
		fixture = ConvertValue.valueOfLELA(0.5, 1, 0, 4000, "0.00");
		assertEquals(
			"{cnvMin=0.5 cnvMax=1.0 inMin=0.0 inMax=4000.0 format=0.00 type=POWERFACTOR PFtype=LELA}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(0.501, fixture.convertDoubleValue(-4.0), 0.0);
		assertEquals(0.500, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(0.499, fixture.convertDoubleValue(4.0), 0.0);
		assertEquals(0.001, fixture.convertDoubleValue(1996.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(-0.001, fixture.convertDoubleValue(2004.0), 0.00000001);
		assertEquals(-0.499, fixture.convertDoubleValue(3996.0), 0.0);
		assertEquals(-0.500, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(-0.501, fixture.convertDoubleValue(4004.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(0.500), 0.0);
		assertEquals(4.0, fixture.convertInputValue(0.499), 0.00001);
		assertEquals(1996.0, fixture.convertInputValue(0.001), 0.0);
		assertEquals(2000.0, fixture.convertInputValue(0.000), 0.0);
		assertEquals(2004.0, fixture.convertInputValue(-0.001), 0.0);
		assertEquals(3996.0, fixture.convertInputValue(-0.499), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-0.500), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(0.501), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-0.501), 0.0);

		// 変換値 <- PLC値
		assertEquals("LE0.50", fixture.convertStringValue(0.0));
		assertEquals("LE0.50", fixture.convertStringValue(20.0));
		assertEquals("LE0.51", fixture.convertStringValue(21.0));
		assertEquals("LE0.99", fixture.convertStringValue(1979.0));
		assertEquals("  1.00", fixture.convertStringValue(1980.0));
		assertEquals("  1.00", fixture.convertStringValue(2000.0));
		assertEquals("  1.00", fixture.convertStringValue(2020.0));
		assertEquals("LA0.99", fixture.convertStringValue(2021.0));
		assertEquals("LA0.51", fixture.convertStringValue(3979.0));
		assertEquals("LA0.50", fixture.convertStringValue(3980.0));
		assertEquals("LA0.50", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("LE0.50", fixture.convertStringValue(-100.0));
		assertEquals("LA0.50", fixture.convertStringValue(4100.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LE0.50"), 0.0);
		assertEquals(40.0, fixture.convertInputValue("LE0.51"), 0.00001);
		assertEquals(1960.0, fixture.convertInputValue("LE0.99"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("  1.00"), 0.0);
		assertEquals(2040.0, fixture.convertInputValue("LA0.99"), 0.0);
		assertEquals(3960.0, fixture.convertInputValue("LA0.51"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LA0.50"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LE0.49"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LA0.49"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA1.00"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA1.01"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE1.00"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE1.01"), 0.0);
		
		serialTest(fixture);
	}

	public void test_valueOfP50P100_LELA() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfLELA(50, 100, 0, 100, " 0.0");
		assertEquals(
			"{cnvMin=50.0 cnvMax=100.0 inMin=0.0 inMax=100.0 format= 0.0 type=POWERFACTOR PFtype=LELA}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(51.0, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(50.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(49.0, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(1.0, fixture.convertDoubleValue(49.0), 0.0);
		assertEquals(0.0, fixture.convertDoubleValue(50.0), 0.0);
		assertEquals(-1.0, fixture.convertDoubleValue(51.0), 0.0);
		assertEquals(-49.0, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(-50.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(-51.0, fixture.convertDoubleValue(101.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(50.0), 0.0);
		assertEquals(1.0, fixture.convertInputValue(49.0), 0.0);
		assertEquals(49.0, fixture.convertInputValue(1.0), 0.0);
		assertEquals(50.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(51.0, fixture.convertInputValue(-1.0), 0.0);
		assertEquals(99.0, fixture.convertInputValue(-49.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(-50.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(51.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(-51.0), 0.0);

		// 変換値 <- PLC値
		assertEquals("LE 50.0", fixture.convertStringValue(0.0));
		assertEquals("LE 51.0", fixture.convertStringValue(1.0));
		assertEquals("LE 99.0", fixture.convertStringValue(49.0));
		assertEquals("   100.0", fixture.convertStringValue(50.0));
		assertEquals("LA 99.0", fixture.convertStringValue(51.0));
		assertEquals("LA 51.0", fixture.convertStringValue(99.0));
		assertEquals("LA 50.0", fixture.convertStringValue(100.0));
		// 範囲外
		assertEquals("LE 50.0", fixture.convertStringValue(-1.0));
		assertEquals("LA 50.0", fixture.convertStringValue(101.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LE 50.0"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("LE 51.0"), 0.0);
		assertEquals(49.0, fixture.convertInputValue("LE 99.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(51.0, fixture.convertInputValue("LA 99.0"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("LA 51.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA 50.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LE 49.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA 49.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LA101.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(50.0, fixture.convertInputValue("LE101.0"), 0.0);

		// 変換率 1/4000
		fixture = ConvertValue.valueOfLELA(50, 100, 0, 4000, " 0.0");
		assertEquals(
			"{cnvMin=50.0 cnvMax=100.0 inMin=0.0 inMax=4000.0 format= 0.0 type=POWERFACTOR PFtype=LELA}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(50.1, fixture.convertDoubleValue(-4.0), 0.0);
		assertEquals(50.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(49.9, fixture.convertDoubleValue(4.0), 0.0);
		assertEquals(0.1, fixture.convertDoubleValue(1996.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(-0.1, fixture.convertDoubleValue(2004.0), 0.00000001);
		assertEquals(-49.9, fixture.convertDoubleValue(3996.0), 0.00000001);
		assertEquals(-50.0, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(-50.1, fixture.convertDoubleValue(4004.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(50.0), 0.0);
		assertEquals(4.0, fixture.convertInputValue(49.9), 0.00001);
		assertEquals(1996.0, fixture.convertInputValue(0.1), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(2004.0, fixture.convertInputValue(-0.1), 0.00001);
		assertEquals(3996.0, fixture.convertInputValue(-49.9), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-50.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(50.1), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-50.1), 0.0);

		// 変換値 <- PLC値
		assertEquals("LE 50.0", fixture.convertStringValue(0.0));
		assertEquals("LE 50.0", fixture.convertStringValue(2.0));
		assertEquals("LE 50.1", fixture.convertStringValue(3.0));
		assertEquals("LE 99.9", fixture.convertStringValue(1997.0));
		assertEquals("   100.0", fixture.convertStringValue(1998.0));
		assertEquals("   100.0", fixture.convertStringValue(2000.0));
		assertEquals("   100.0", fixture.convertStringValue(2001.0));
		assertEquals("LA 99.9", fixture.convertStringValue(2002.0));
		assertEquals("LA 50.1", fixture.convertStringValue(3998.0));
		assertEquals("LA 50.0", fixture.convertStringValue(3999.0));
		assertEquals("LA 50.0", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("LE 50.0", fixture.convertStringValue(-10.0));
		assertEquals("LA 50.0", fixture.convertStringValue(4010.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LE 50.0"), 0.0);
		assertEquals(4.0, fixture.convertInputValue("LE 50.1"), 0.00001);
		assertEquals(1996.0, fixture.convertInputValue("LE 99.9"), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(2004.0, fixture.convertInputValue("LA 99.9"), 0.00001);
		assertEquals(3996.0, fixture.convertInputValue("LA 50.1"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LA 50.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LE 49.9"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LA 49.9"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA100.1"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE100.1"), 0.0);
		
		serialTest(fixture);
	}

	public void test_valueOfP0P100_LELA() throws Exception {
		// 変換率 1/1
		ConvertValue fixture = ConvertValue.valueOfLELA(0, 100, 0, 200, " 0.0");
		assertEquals(
			"{cnvMin=0.0 cnvMax=100.0 inMin=0.0 inMax=200.0 format= 0.0 type=POWERFACTOR PFtype=LELA}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(101.0, fixture.convertDoubleValue(-1.0), 0.0);
		assertEquals(100.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(99.0, fixture.convertDoubleValue(1.0), 0.0);
		assertEquals(1.0, fixture.convertDoubleValue(99.0), 0.0);
		assertEquals(0.0, fixture.convertDoubleValue(100.0), 0.0);
		assertEquals(-1.0, fixture.convertDoubleValue(101.0), 0.0);
		assertEquals(-99.0, fixture.convertDoubleValue(199.0), 0.0);
		assertEquals(-100.0, fixture.convertDoubleValue(200.0), 0.0);
		assertEquals(-101.0, fixture.convertDoubleValue(201.0), 0.0);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(100.0), 0.0);
		assertEquals(1.0, fixture.convertInputValue(99.0), 0.0);
		assertEquals(99.0, fixture.convertInputValue(1.0), 0.0);
		assertEquals(100.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(101.0, fixture.convertInputValue(-1.0), 0.0);
		assertEquals(199.0, fixture.convertInputValue(-99.0), 0.0);
		assertEquals(200.0, fixture.convertInputValue(-100.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(101.0), 0.0);
		assertEquals(200.0, fixture.convertInputValue(-101.0), 0.0);

		// 変換値 <- PLC値
		assertEquals("LE 0.0", fixture.convertStringValue(0.0));
		assertEquals("LE 1.0", fixture.convertStringValue(1.0));
		assertEquals("LE 99.0", fixture.convertStringValue(99.0));
		assertEquals("   100.0", fixture.convertStringValue(100.0));
		assertEquals("LA 99.0", fixture.convertStringValue(101.0));
		assertEquals("LA 1.0", fixture.convertStringValue(199.0));
		assertEquals("LA 0.0", fixture.convertStringValue(200.0));
		// 範囲外
		assertEquals("LE 0.0", fixture.convertStringValue(-1.0));
		assertEquals("LA 0.0", fixture.convertStringValue(201.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LE 0.0"), 0.0);
		assertEquals(1.0, fixture.convertInputValue("LE 1.0"), 0.0);
		assertEquals(99.0, fixture.convertInputValue("LE 99.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(101.0, fixture.convertInputValue("LA 99.0"), 0.0);
		assertEquals(199.0, fixture.convertInputValue("LA 1.0"), 0.0);
		assertEquals(200.0, fixture.convertInputValue("LA 0.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LE -1.0"), 0.0);
		assertEquals(200.0, fixture.convertInputValue("LA -1.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LA101.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(100.0, fixture.convertInputValue("LE101.0"), 0.0);

		// 変換率 1/2000
		fixture = ConvertValue.valueOfLELA(0, 100, 0, 4000, " 0.0");
		assertEquals(
			"{cnvMin=0.0 cnvMax=100.0 inMin=0.0 inMax=4000.0 format= 0.0 type=POWERFACTOR PFtype=LELA}",
			fixture.toString());
		// double変換値 <- PLC値
		assertEquals(100.1, fixture.convertDoubleValue(-2.0), 0.0);
		assertEquals(100.0, fixture.convertDoubleValue(0.0), 0.0);
		assertEquals(99.9, fixture.convertDoubleValue(2.0), 0.0);
		assertEquals(0.1, fixture.convertDoubleValue(1998.0), 0.00000001);
		assertEquals(0.0, fixture.convertDoubleValue(2000.0), 0.0);
		assertEquals(-0.1, fixture.convertDoubleValue(2002.0), 0.00000001);
		assertEquals(-99.9, fixture.convertDoubleValue(3998.0), 0.00000001);
		assertEquals(-100.0, fixture.convertDoubleValue(4000.0), 0.0);
		assertEquals(-100.1, fixture.convertDoubleValue(4002.0), 0.00000001);

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue(100.0), 0.0);
		assertEquals(2.0, fixture.convertInputValue(99.9), 0.00001);
		assertEquals(1998.0, fixture.convertInputValue(0.1), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue(0.0), 0.0);
		assertEquals(2002.0, fixture.convertInputValue(-0.1), 0.00001);
		assertEquals(3998.0, fixture.convertInputValue(-99.9), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-100.0), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue(100.1), 0.0);
		assertEquals(4000.0, fixture.convertInputValue(-100.1), 0.0);

		// 変換値 <- PLC値
		assertEquals("LE 0.0", fixture.convertStringValue(0.0));
		assertEquals("LE 0.1", fixture.convertStringValue(2.0));
		assertEquals("LE 0.2", fixture.convertStringValue(3.0));
		assertEquals("LE 99.9", fixture.convertStringValue(1997.0));
		assertEquals("LE 99.9", fixture.convertStringValue(1998.0));
		assertEquals("   100.0", fixture.convertStringValue(2000.0));
		assertEquals("LA 99.9", fixture.convertStringValue(2001.0));
		assertEquals("LA 99.9", fixture.convertStringValue(2002.0));
		assertEquals("LA 0.1", fixture.convertStringValue(3998.0));
		assertEquals("LA 0.0", fixture.convertStringValue(3999.0));
		assertEquals("LA 0.0", fixture.convertStringValue(4000.0));
		// 範囲外
		assertEquals("LE 0.0", fixture.convertStringValue(-10.0));
		assertEquals("LA 0.0", fixture.convertStringValue(4010.0));

		// PLC値 <- 変換値
		assertEquals(0.0, fixture.convertInputValue("LE 0.0"), 0.0);
		assertEquals(2.0, fixture.convertInputValue("LE 0.1"), 0.00001);
		assertEquals(1998.0, fixture.convertInputValue("LE 99.9"), 0.00001);
		assertEquals(2000.0, fixture.convertInputValue("  100.0"), 0.0);
		assertEquals(2002.0, fixture.convertInputValue("LA 99.9"), 0.00001);
		assertEquals(3998.0, fixture.convertInputValue("LA 0.1"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LA 0.0"), 0.0);
		// 範囲外
		assertEquals(0.0, fixture.convertInputValue("LE -1.0"), 0.0);
		assertEquals(4000.0, fixture.convertInputValue("LA -1.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LA101.1"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE100.0"), 0.0);
		assertEquals(2000.0, fixture.convertInputValue("LE101.1"), 0.0);
		
		serialTest(fixture);
	}
	
	private void serialTest(ConvertValue value) throws Exception {
		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		ObjectOutputStream outs =
			new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(value);
		outs.flush();
		outs.close();
		ObjectInputStream ins =
			new ObjectInputStream(new FileInputStream(temp));
		ConvertValue d = (ConvertValue) ins.readObject();
		ins.close();
		assertNotNull(d);
		assertEquals(value.getConvertMax(), d.getConvertMax(), 0);
		assertEquals(value.getConvertMin(), d.getConvertMin(), 0);
		assertEquals(value.getPattern(), d.getPattern());
		
		assertEquals(value.hashCode(), d.hashCode());
		assertEquals(value, d);
		temp.delete();
	}
	
	public void testConvertValueType() throws Exception {
		ConvertValue.ConvertValueType cvt = ConvertValue.ConvertValueType.ANALOG;
		assertSame(ConvertValue.ConvertValueType.ANALOG, cvt);
		assertEquals(ConvertValue.ConvertValueType.ANALOG, cvt);

		cvt = ConvertValue.ConvertValueType.POWERFACTOR;
		assertSame(ConvertValue.ConvertValueType.POWERFACTOR, cvt);
		assertEquals(ConvertValue.ConvertValueType.POWERFACTOR, cvt);
		
		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		ObjectOutputStream outs =
			new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(cvt);
		outs.flush();
		outs.close();
		ObjectInputStream ins =
			new ObjectInputStream(new FileInputStream(temp));
		ConvertValue.ConvertValueType d = (ConvertValue.ConvertValueType) ins.readObject();
		ins.close();
		assertNotNull(d);
		assertSame(d, cvt);
		temp.delete();
	}
	
	public void testPowerFactorType() throws Exception {
		ConvertValue.PowerFactorType cpt = ConvertValue.PowerFactorType.DECIMAL;
		assertSame(ConvertValue.PowerFactorType.DECIMAL, cpt); 
		assertEquals(ConvertValue.PowerFactorType.DECIMAL, cpt); 

		cpt = ConvertValue.PowerFactorType.LALE;
		assertSame(ConvertValue.PowerFactorType.LALE, cpt); 
		assertEquals(ConvertValue.PowerFactorType.LALE, cpt); 

		cpt = ConvertValue.PowerFactorType.LELA;
		assertSame(ConvertValue.PowerFactorType.LELA, cpt); 
		assertEquals(ConvertValue.PowerFactorType.LELA, cpt); 
		
		//デシリアライズのテスト。
		File temp = File.createTempFile("sertest", ".ser");
		temp.deleteOnExit();
		ObjectOutputStream outs =
			new ObjectOutputStream(new FileOutputStream(temp));
		outs.writeObject(cpt);
		outs.flush();
		outs.close();
		ObjectInputStream ins =
			new ObjectInputStream(new FileInputStream(temp));
		ConvertValue.PowerFactorType d = (ConvertValue.PowerFactorType) ins.readObject();
		ins.close();
		assertNotNull(d);
		assertSame(d, cpt);
		temp.delete();
	}
}
