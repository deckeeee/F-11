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
package org.F11.scada.applet.graph;

import java.sql.Timestamp;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

/**
 * GraphSeriesProperty の実装内部クラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class GraphSeriesProperty {

	/** シリーズのサイズ（グループ内のシリーズ数） */
	private int seriesSize;
	/** 縦スケールの最小値配列 */
	private double[] verticalMinimums;
	/** 縦スケールの最大値配列 */
	private double[] verticalMaximums;
	/** 縦スケールの最小入力値配列 */
	private double[] verticalInputMinimums;
	/** 縦スケールの最大入力値配列 */
	private double[] verticalInputMaximums;
	/** データプロバイダ名配列 */
	private String[] dataProviderNames;
	/** データホルダ名配列 */
	private String[] dataHolderNames;
	/** 参照値クラスの配列 */
	private GraphFoldProperty[] referenceValues;
	/** シリーズ(グループ)名 */
	private String seriesName;
	/** ポイント名称の配列 */
	private String[] pointNames;
	/** ポイント記号の配列 */
	private String[] pointMarks;
	/** 現在値表示シンボル */
	private transient ExplanatoryNotesText[] symbols;

	/**
	 * コンストラクタ
	 * 折り返し対応バージョン
	 */
	public GraphSeriesProperty(
		int seriesSize,
		double[] verticalMinimums,
		double[] verticalMaximums,
		double[] verticalInputMinimums,
		double[] verticalInputMaximums,
		String[] dataProviderNames,
		String[] dataHolderNames,
		int foldCount,
		String seriesName,
		String[] pointNames,
		String[] pointMarks,
		ExplanatoryNotesText[] symbols) {

		this.seriesSize = seriesSize;
		this.verticalMinimums = verticalMinimums;
		this.verticalMaximums = verticalMaximums;
		this.verticalInputMinimums = verticalInputMinimums;
		this.verticalInputMaximums = verticalInputMaximums;
		this.dataProviderNames = dataProviderNames;
		this.dataHolderNames = dataHolderNames;
		this.referenceValues = new GraphFoldProperty[seriesSize];
		for (int i = 0; i < referenceValues.length; i++) {
			referenceValues[i] = new GraphFoldProperty(foldCount);
		}
		this.seriesName = seriesName;
		this.pointNames = pointNames;
		this.pointMarks = pointMarks;
		this.symbols = symbols;
	}

	/**
	 * コンストラクタ
	 */
	public GraphSeriesProperty(
		int seriesSize,
		Double[] verticalMinimums,
		Double[] verticalMaximums,
		Double[] verticalInputMinimums,
		Double[] verticalInputMaximums,
		String[] dataProviderNames,
		String[] dataHolderNames,
		int foldCount,
		String seriesName,
		String[] pointNames,
		String[] pointMarks,
		ExplanatoryNotesText[] symbols) {
		this(
			seriesSize,
			toPrimitiveArray(verticalMinimums),
			toPrimitiveArray(verticalMaximums),
			toPrimitiveArray(verticalInputMinimums),
			toPrimitiveArray(verticalInputMaximums),
			dataProviderNames,
			dataHolderNames,
			foldCount,
			seriesName,
			pointNames,
			pointMarks,
			symbols);
	}

	private static double[] toPrimitiveArray(Double[] array) {
		double[] retValue = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			retValue[i] = array[i].doubleValue();
		}
		return retValue;
	}

	/**
	 * コピーコンストラクタ
	 * @param src コピー元 GraphSeriesProperty オブジェクト
	 */
	public GraphSeriesProperty(GraphSeriesProperty src) {
		seriesSize = src.seriesSize;
		verticalMinimums = new double[src.verticalMinimums.length];
		System.arraycopy(
			src.verticalMinimums,
			0,
			verticalMinimums,
			0,
			verticalMinimums.length);
		verticalMaximums = new double[src.verticalMaximums.length];
		System.arraycopy(
			src.verticalMaximums,
			0,
			verticalMaximums,
			0,
			verticalMaximums.length);
		verticalInputMinimums = new double[src.verticalInputMinimums.length];
		System.arraycopy(
			src.verticalInputMinimums,
			0,
			verticalInputMinimums,
			0,
			verticalInputMinimums.length);
		verticalInputMaximums = new double[src.verticalInputMaximums.length];
		System.arraycopy(
			src.verticalInputMaximums,
			0,
			verticalInputMaximums,
			0,
			verticalInputMaximums.length);
		dataProviderNames = new String[src.dataProviderNames.length];
		System.arraycopy(
			src.dataProviderNames,
			0,
			dataProviderNames,
			0,
			dataProviderNames.length);
		dataHolderNames = new String[src.dataHolderNames.length];
		System.arraycopy(
			src.dataHolderNames,
			0,
			dataHolderNames,
			0,
			dataHolderNames.length);
		referenceValues = new GraphFoldProperty[src.referenceValues.length];
		for (int i = 0; i < src.referenceValues.length; i++) {
			referenceValues[i] = new GraphFoldProperty(src.referenceValues[i]);
		}
		seriesName = src.seriesName;
		pointNames = new String[src.pointNames.length];
		System.arraycopy(src.pointNames, 0, pointNames, 0, pointNames.length);
		pointMarks = new String[src.pointMarks.length];
		System.arraycopy(src.pointMarks, 0, pointMarks, 0, pointMarks.length);
		symbols = new ExplanatoryNotesText[src.symbols.length];
		System.arraycopy(src.symbols, 0, symbols, 0, symbols.length);
	}

	/**
	 * 設定されているシリーズのサイズを返します。
	 * @return 設定されているシリーズのサイズ（グループ内のシリーズ数）
	 */
	public int getSeriesSize() {
		return seriesSize;
	}

	/**
	 * 縦スケールの最小値を返します。
	 * @param series シリーズ
	 * @return 縦スケールの最小値
	 */
	public double getVerticalMinimum(int series) {
		return isValidSeries(series) ? verticalMinimums[series] : 0;
	}

	/**
	 * 縦スケールの最大値を返します。
	 * @param series シリーズ
	 * @return 縦スケールの最大値
	 */
	public double getVerticalMaximum(int series) {
		return isValidSeries(series) ? verticalMaximums[series] : 0;
	}

	/**
	 * 現在位置づけられているグループの、縦スケールの入力最小値を返します。
	 * @param series シリーズ
	 * @return 縦スケールの入力最小値
	 */
	public double getVerticalInputMinimum(int series) {
		return isValidSeries(series) ? verticalInputMinimums[series] : 0;
	}

	/**
	 * 現在位置づけられているグループの、縦スケールの入力最大値を返します。
	 * @param series シリーズ
	 * @return 縦スケールの入力最大値
	 */
	public double getVerticalInputMaximum(int series) {
		return isValidSeries(series) ? verticalInputMaximums[series] : 0;
	}

	/**
	 * 縦スケールの最小値を設定します。
	 * @param series シリーズ
	 * @param verticalMinimum 縦スケールの最小値
	 */
	public void setVerticalMinimum(int series, double verticalMinimum) {
		checkArgument(series);
		this.verticalMinimums[series] = verticalMinimum;
	}

	/**
	 * 縦スケールの最大値を設定します。
	 * @param series シリーズ
	 * @param verticalMaximum 縦スケールの最大値
	 */
	public void setVerticalMaximum(int series, double verticalMaximum) {
		checkArgument(series);
		this.verticalMaximums[series] = verticalMaximum;
	}

	private void checkArgument(double argv) {
		if (argv < 0 || argv >= seriesSize) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Series size not equal argv : argv = ");
			buffer.append(argv);
			buffer.append(" seriesSize = ");
			buffer.append(seriesSize);
			throw new IllegalArgumentException(buffer.toString());
		}
	}

	/**
	 * データプロバイダ名を返します。
	 * @param series シリーズ
	 * @return データプロバイダ名
	 */
	public String getDataProviderName(int series) {
		return isValidSeries(series) ? dataProviderNames[series] : "";
	}

	/**
	 * データホルダ名を返します。
	 * @param series シリーズ
	 * @return データホルダ名
	 */
	public String getDataHolderName(int series) {
		return isValidSeries(series) ? dataHolderNames[series] : "";
	}

	/**
	 * 現在位置づけられているグループの、参照値を返します。
	 * @param series シリーズ
	 * @param fold 折り返し位置
	 * @return 参照値
	 */
	public double getReferenceValue(int series, int fold) {
		return isValidSeries(series) ? referenceValues[series].getReferenceValue(fold) : 0;
	}

	/**
	 * 現在位置づけられているグループの、参照値を設定します。
	 * @param series シリーズ
	 * @param fold 折り返し位置
	 * @param referenceValue 参照値
	 */
	public void setReferenceValue(int series, int fold, double referenceValue) {
		checkArgument(series);
		this.referenceValues[series].setReferenceValue(fold, referenceValue);
	}

	/**
	 * 現在位置づけられているグループの、参照時刻を返します。
	 * @param series シリーズ
	 * @param fold 折り返し位置
	 * @return 参照時刻
	 */
	public Timestamp getReferenceTime(int series, int fold) {
		return isValidSeries(series) ? referenceValues[series].getReferenceTime(fold) : new Timestamp(0);
	}

	/**
	 * 現在位置づけられているグループの、参照時刻を設定します。
	 * @param series シリーズ
	 * @param fold 折り返し位置
	 * @param referenceTime 参照時刻
	 */
	public void setReferenceTime(int series, int fold, Timestamp referenceTime) {
		checkArgument(series);
		this.referenceValues[series].setReferenceTime(fold, referenceTime);
	}
	
	/**
	 * このシリーズの名称(グループ名)を返します。
	 * @return このシリーズの名称(グループ名)を返します。
	 */
	public String getSeriesName() {
		return seriesName;
	}
	
	/**
	 * データホルダーに関連づけられた名称を返します。
	 * @param series シリーズ
	 * @return データホルダーに関連づけられた名称を返します。
	 */
	public String getPointName(int series) {
		return isValidSeries(series) ? pointNames[series] : "";
	}
	
	/**
	 * データホルダーに関連づけられた単位記号を返します。
	 * @param series シリーズ
	 * @return データホルダーに関連づけられた単位記号を返します。
	 */
	public String getPointMark(int series) {
		return isValidSeries(series) ? pointMarks[series] : "";
	}
	
	/**
	 * 現在値のアナログシンボルを返します。
	 * @param series シリーズ
	 * @return 現在値のアナログシンボルを返します。
	 */
	public ExplanatoryNotesText getSymbol(int series) {
		return isValidSeries(series) ? symbols[series] : null;
	}
	

	/**
	 * このオブジェクトの文字列表現を返します。
	 * 内部プロパティを文字列表現して返します。尚、このメソッドは開発段階で使用すべきであり、
	 * テスト目的以外で使用しないでください。将来、返される内容が変更されることがあります。
	 * @return オブジェクトの文字列表現
	 */
	public String toString() {
		StringBuffer b = new StringBuffer();

		b.append("seriesSize=").append(seriesSize)
		.append(",verticalMinimums=").append(ArrayUtils.toObject(verticalMinimums))
		.append(",verticalMaximums=").append(ArrayUtils.toObject(verticalMaximums))
		.append(",verticalInputMinimums=").append(ArrayUtils.toObject(verticalInputMinimums))
		.append(",verticalInputMaximums=").append(ArrayUtils.toObject(verticalInputMaximums))
		.append(",dataProviderNames=").append(Arrays.asList(dataProviderNames))
		.append(",dataHolderNames=").append(Arrays.asList(dataHolderNames))
		.append(",referenceValues=").append(Arrays.asList(referenceValues))
		.append(",seriesName=").append(seriesName)
		.append(",pointNames=").append(Arrays.asList(pointNames))
		.append(",pointMarks=").append(Arrays.asList(pointMarks));
		
		return b.toString();
	}

	/**
	 * 別のオブジェクトが、このオブジェクトと同じか調べます。
	 * 結果は引数が null でなく、このオブジェクトと同じ各プロパティー（フィールド）の値を持つ
	 * オブジェクトである場合に true を返します。
	 * @param obj この GraphSeriesProperty と等しいかどうかが判定されるオブジェクト
	 * @return オブジェクトが同じである場合は true、そうでない場合は false
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof GraphSeriesProperty)) {
			return false;
		}
		GraphSeriesProperty gp = (GraphSeriesProperty) obj;

		return gp.seriesSize == seriesSize
			&& Arrays.equals(gp.verticalMinimums, verticalMinimums)
			&& Arrays.equals(gp.verticalMaximums, verticalMaximums)
			&& Arrays.equals(gp.verticalInputMinimums, verticalInputMinimums)
			&& Arrays.equals(gp.verticalInputMaximums, verticalInputMaximums)
			&& Arrays.equals(gp.dataProviderNames, dataProviderNames)
			&& Arrays.equals(gp.dataHolderNames, dataHolderNames)
			&& Arrays.equals(gp.referenceValues, referenceValues)
			&& gp.seriesName.equals(seriesName)
			&& Arrays.equals(gp.pointNames, pointNames)
			&& Arrays.equals(gp.pointMarks, pointMarks);
	}

	/**
	 * この GraphSeriesProperty のハッシュコードを計算します。
	 * @return この GraphSeriesProperty のハッシュコード値
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + seriesSize;
		result = doubleHashCode(result, verticalMinimums);
		result = doubleHashCode(result, verticalMaximums);
		result = doubleHashCode(result, verticalInputMinimums);
		result = doubleHashCode(result, verticalInputMaximums);
		result = stringHashCode(result, dataProviderNames);
		result = stringHashCode(result, dataHolderNames);
		for (int i = 0, cnt = referenceValues.length; i < cnt; i++) {
			result = 37 * result + referenceValues[i].hashCode();
		}
		result = 37 * result + seriesName.hashCode();
		result = stringHashCode(result, pointNames);
		result = stringHashCode(result, pointMarks);

		return result;
	}
	
	private int doubleHashCode(int result, double[] src) {
		int rt = result;
		for (int i = 0; i < src.length; i++) {
			long dl = Double.doubleToLongBits(src[i]);
			rt = 37 * rt + (int) (dl ^ (dl >>> 32));
		}
		
		return rt;
	}
	
	private int stringHashCode(int result, String[] src) {
		int rt = result;
		for (int i = 0; i < src.length; i++) {
			rt = 37 * rt + src[i].hashCode();
		}
			
		return rt;
	}
	
	private boolean isValidSeries(int series) {
		return (series < 0 || series >= getSeriesSize()) ? false : true;
	}

	/**
	 * 参照値を保持する内部クラスです。
	 * 折り返しデータ（参照時刻の違う同一データ）をサポートします。
	 * @author hori <hori@users.sourceforge.jp>
	 */
	private static class GraphFoldProperty {
		/** 折り返し数 */
		private int foldCount;
		/** 参照値 */
		private double[] referenceValues;
		/** 参照時刻 */
		private Timestamp[] referenceTimes;

		/**
		 * コンストラクタ
		 */
		public GraphFoldProperty(int foldCount) {
			this.foldCount = foldCount;
			this.referenceValues = new double[foldCount + 1];
			Arrays.fill(this.referenceValues, 0);
			this.referenceTimes = new Timestamp[foldCount + 1];
			Arrays.fill(this.referenceTimes, new Timestamp(0));
		}

		/**
		 * コピーコンストラクタ
		 * @param src コピー元 GraphSeriesProperty オブジェクト
		 */
		public GraphFoldProperty(GraphFoldProperty src) {
			foldCount = src.foldCount;
			referenceValues = new double[src.referenceValues.length];
			System.arraycopy(
				src.referenceValues,
				0,
				referenceValues,
				0,
				referenceValues.length);
			referenceTimes = new Timestamp[src.referenceTimes.length];
			for (int i = 0; i < src.referenceTimes.length; i++) {
				referenceTimes[i] = new Timestamp(src.referenceTimes[i].getTime());
			}
		}

		private void checkArgument(double argv) {
			if (argv < 0 || argv > foldCount) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("FoldCount not equal argv : argv = ");
				buffer.append(argv);
				buffer.append(" foldCount = ");
				buffer.append(foldCount);
				throw new IllegalArgumentException(buffer.toString());
			}
		}

		/**
		 * 折り返し位置の参照値を返します。
		 * @param fold 折り返し位置
		 * @return 参照値
		 */
		public double getReferenceValue(int fold) {
			return referenceValues[fold];
		}

		/**
		 * 折り返し位置の参照値を設定します。
		 * @param fold 折り返し位置
		 * @param referenceValue 参照値
		 */
		public void setReferenceValue(int fold, double referenceValue) {
			checkArgument(fold);
			this.referenceValues[fold] = referenceValue;
		}

		/**
		 * 折り返し位置の参照時刻を返します。
		 * @param fold 折り返し位置
		 * @return 参照時刻
		 */
		public Timestamp getReferenceTime(int fold) {
			return referenceTimes[fold];
		}

		/**
		 * 折り返し位置の参照値を設定します。
		 * @param fold 折り返し位置
		 * @param referenceTime 参照時刻
		 */
		public void setReferenceTime(int fold, Timestamp referenceTime) {
			checkArgument(fold);
			this.referenceTimes[fold] = referenceTime;
		}

		/**
		 * このオブジェクトの文字列表現を返します。
		 * 内部プロパティを文字列表現して返します。尚、このメソッドは開発段階で使用すべきであり、
		 * テスト目的以外で使用しないでください。将来、返される内容が変更されることがあります。
		 * @return オブジェクトの文字列表現
		 */
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("foldCount:" + foldCount);
			for (int i = 0; i < referenceValues.length; i++) {
				buffer.append(",referenceValue:" + referenceValues[i]);
			}
			for (int i = 0; i < referenceTimes.length; i++) {
				buffer.append(",referenceTimes:" + referenceTimes[i]);
			}
			return buffer.toString();
		}

		/**
		 * 別のオブジェクトが、このオブジェクトと同じか調べます。
		 * 結果は引数が null でなく、このオブジェクトと同じ各プロパティー（フィールド）の値を持つ
		 * オブジェクトである場合に true を返します。
		 * @param obj この GraphSeriesProperty と等しいかどうかが判定されるオブジェクト
		 * @return オブジェクトが同じである場合は true、そうでない場合は false
		 */
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof GraphFoldProperty)) {
				return false;
			}
			GraphFoldProperty gfp = (GraphFoldProperty) obj;
			return gfp.foldCount == this.foldCount
				&& Arrays.equals(gfp.referenceValues, this.referenceValues)
				&& Arrays.equals(gfp.referenceTimes, this.referenceTimes);
		}

		/**
		 * この GraphSeriesProperty のハッシュコードを計算します。
		 * @return この GraphSeriesProperty のハッシュコード値
		 */
		public int hashCode() {
			int result = 17;
			result = 37 * result + foldCount;
			for (int i = 0; i < referenceValues.length; i++) {
				long dl = Double.doubleToLongBits(referenceValues[i]);
				result = 37 * result + (int) (dl ^ (dl >>> 32));
			}
			for (int i = 0; i < referenceTimes.length; i++) {
				result = 37 * result + referenceTimes[i].hashCode();
			}
			return result;
		}
	}
}
