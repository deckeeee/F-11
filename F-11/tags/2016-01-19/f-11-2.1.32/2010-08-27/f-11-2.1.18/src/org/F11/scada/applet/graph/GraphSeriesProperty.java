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
 * GraphSeriesProperty �̎��������N���X�ł��B
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class GraphSeriesProperty {

	/** �V���[�Y�̃T�C�Y�i�O���[�v���̃V���[�Y���j */
	private int seriesSize;
	/** �c�X�P�[���̍ŏ��l�z�� */
	private double[] verticalMinimums;
	/** �c�X�P�[���̍ő�l�z�� */
	private double[] verticalMaximums;
	/** �c�X�P�[���̍ŏ����͒l�z�� */
	private double[] verticalInputMinimums;
	/** �c�X�P�[���̍ő���͒l�z�� */
	private double[] verticalInputMaximums;
	/** �f�[�^�v���o�C�_���z�� */
	private String[] dataProviderNames;
	/** �f�[�^�z���_���z�� */
	private String[] dataHolderNames;
	/** �Q�ƒl�N���X�̔z�� */
	private GraphFoldProperty[] referenceValues;
	/** �V���[�Y(�O���[�v)�� */
	private String seriesName;
	/** �|�C���g���̂̔z�� */
	private String[] pointNames;
	/** �|�C���g�L���̔z�� */
	private String[] pointMarks;
	/** ���ݒl�\���V���{�� */
	private transient ExplanatoryNotesText[] symbols;

	/**
	 * �R���X�g���N�^
	 * �܂�Ԃ��Ή��o�[�W����
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
	 * �R���X�g���N�^
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
	 * �R�s�[�R���X�g���N�^
	 * @param src �R�s�[�� GraphSeriesProperty �I�u�W�F�N�g
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
	 * �ݒ肳��Ă���V���[�Y�̃T�C�Y��Ԃ��܂��B
	 * @return �ݒ肳��Ă���V���[�Y�̃T�C�Y�i�O���[�v���̃V���[�Y���j
	 */
	public int getSeriesSize() {
		return seriesSize;
	}

	/**
	 * �c�X�P�[���̍ŏ��l��Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �c�X�P�[���̍ŏ��l
	 */
	public double getVerticalMinimum(int series) {
		return isValidSeries(series) ? verticalMinimums[series] : 0;
	}

	/**
	 * �c�X�P�[���̍ő�l��Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �c�X�P�[���̍ő�l
	 */
	public double getVerticalMaximum(int series) {
		return isValidSeries(series) ? verticalMaximums[series] : 0;
	}

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�c�X�P�[���̓��͍ŏ��l��Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �c�X�P�[���̓��͍ŏ��l
	 */
	public double getVerticalInputMinimum(int series) {
		return isValidSeries(series) ? verticalInputMinimums[series] : 0;
	}

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�c�X�P�[���̓��͍ő�l��Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �c�X�P�[���̓��͍ő�l
	 */
	public double getVerticalInputMaximum(int series) {
		return isValidSeries(series) ? verticalInputMaximums[series] : 0;
	}

	/**
	 * �c�X�P�[���̍ŏ��l��ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param verticalMinimum �c�X�P�[���̍ŏ��l
	 */
	public void setVerticalMinimum(int series, double verticalMinimum) {
		checkArgument(series);
		this.verticalMinimums[series] = verticalMinimum;
	}

	/**
	 * �c�X�P�[���̍ő�l��ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param verticalMaximum �c�X�P�[���̍ő�l
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
	 * �f�[�^�v���o�C�_����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �f�[�^�v���o�C�_��
	 */
	public String getDataProviderName(int series) {
		return isValidSeries(series) ? dataProviderNames[series] : "";
	}

	/**
	 * �f�[�^�z���_����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �f�[�^�z���_��
	 */
	public String getDataHolderName(int series) {
		return isValidSeries(series) ? dataHolderNames[series] : "";
	}

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�ƒl��Ԃ��܂��B
	 * @param series �V���[�Y
	 * @param fold �܂�Ԃ��ʒu
	 * @return �Q�ƒl
	 */
	public double getReferenceValue(int series, int fold) {
		return isValidSeries(series) ? referenceValues[series].getReferenceValue(fold) : 0;
	}

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�ƒl��ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param fold �܂�Ԃ��ʒu
	 * @param referenceValue �Q�ƒl
	 */
	public void setReferenceValue(int series, int fold, double referenceValue) {
		checkArgument(series);
		this.referenceValues[series].setReferenceValue(fold, referenceValue);
	}

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�Ǝ�����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @param fold �܂�Ԃ��ʒu
	 * @return �Q�Ǝ���
	 */
	public Timestamp getReferenceTime(int series, int fold) {
		return isValidSeries(series) ? referenceValues[series].getReferenceTime(fold) : new Timestamp(0);
	}

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�Ǝ�����ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param fold �܂�Ԃ��ʒu
	 * @param referenceTime �Q�Ǝ���
	 */
	public void setReferenceTime(int series, int fold, Timestamp referenceTime) {
		checkArgument(series);
		this.referenceValues[series].setReferenceTime(fold, referenceTime);
	}
	
	/**
	 * ���̃V���[�Y�̖���(�O���[�v��)��Ԃ��܂��B
	 * @return ���̃V���[�Y�̖���(�O���[�v��)��Ԃ��܂��B
	 */
	public String getSeriesName() {
		return seriesName;
	}
	
	/**
	 * �f�[�^�z���_�[�Ɋ֘A�Â���ꂽ���̂�Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �f�[�^�z���_�[�Ɋ֘A�Â���ꂽ���̂�Ԃ��܂��B
	 */
	public String getPointName(int series) {
		return isValidSeries(series) ? pointNames[series] : "";
	}
	
	/**
	 * �f�[�^�z���_�[�Ɋ֘A�Â���ꂽ�P�ʋL����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �f�[�^�z���_�[�Ɋ֘A�Â���ꂽ�P�ʋL����Ԃ��܂��B
	 */
	public String getPointMark(int series) {
		return isValidSeries(series) ? pointMarks[series] : "";
	}
	
	/**
	 * ���ݒl�̃A�i���O�V���{����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return ���ݒl�̃A�i���O�V���{����Ԃ��܂��B
	 */
	public ExplanatoryNotesText getSymbol(int series) {
		return isValidSeries(series) ? symbols[series] : null;
	}
	

	/**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 * �����v���p�e�B�𕶎���\�����ĕԂ��܂��B���A���̃��\�b�h�͊J���i�K�Ŏg�p���ׂ��ł���A
	 * �e�X�g�ړI�ȊO�Ŏg�p���Ȃ��ł��������B�����A�Ԃ������e���ύX����邱�Ƃ�����܂��B
	 * @return �I�u�W�F�N�g�̕�����\��
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
	 * �ʂ̃I�u�W�F�N�g���A���̃I�u�W�F�N�g�Ɠ��������ׂ܂��B
	 * ���ʂ͈����� null �łȂ��A���̃I�u�W�F�N�g�Ɠ����e�v���p�e�B�[�i�t�B�[���h�j�̒l������
	 * �I�u�W�F�N�g�ł���ꍇ�� true ��Ԃ��܂��B
	 * @param obj ���� GraphSeriesProperty �Ɠ��������ǂ��������肳���I�u�W�F�N�g
	 * @return �I�u�W�F�N�g�������ł���ꍇ�� true�A�����łȂ��ꍇ�� false
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
	 * ���� GraphSeriesProperty �̃n�b�V���R�[�h���v�Z���܂��B
	 * @return ���� GraphSeriesProperty �̃n�b�V���R�[�h�l
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
	 * �Q�ƒl��ێ���������N���X�ł��B
	 * �܂�Ԃ��f�[�^�i�Q�Ǝ����̈Ⴄ����f�[�^�j���T�|�[�g���܂��B
	 * @author hori <hori@users.sourceforge.jp>
	 */
	private static class GraphFoldProperty {
		/** �܂�Ԃ��� */
		private int foldCount;
		/** �Q�ƒl */
		private double[] referenceValues;
		/** �Q�Ǝ��� */
		private Timestamp[] referenceTimes;

		/**
		 * �R���X�g���N�^
		 */
		public GraphFoldProperty(int foldCount) {
			this.foldCount = foldCount;
			this.referenceValues = new double[foldCount + 1];
			Arrays.fill(this.referenceValues, 0);
			this.referenceTimes = new Timestamp[foldCount + 1];
			Arrays.fill(this.referenceTimes, new Timestamp(0));
		}

		/**
		 * �R�s�[�R���X�g���N�^
		 * @param src �R�s�[�� GraphSeriesProperty �I�u�W�F�N�g
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
		 * �܂�Ԃ��ʒu�̎Q�ƒl��Ԃ��܂��B
		 * @param fold �܂�Ԃ��ʒu
		 * @return �Q�ƒl
		 */
		public double getReferenceValue(int fold) {
			return referenceValues[fold];
		}

		/**
		 * �܂�Ԃ��ʒu�̎Q�ƒl��ݒ肵�܂��B
		 * @param fold �܂�Ԃ��ʒu
		 * @param referenceValue �Q�ƒl
		 */
		public void setReferenceValue(int fold, double referenceValue) {
			checkArgument(fold);
			this.referenceValues[fold] = referenceValue;
		}

		/**
		 * �܂�Ԃ��ʒu�̎Q�Ǝ�����Ԃ��܂��B
		 * @param fold �܂�Ԃ��ʒu
		 * @return �Q�Ǝ���
		 */
		public Timestamp getReferenceTime(int fold) {
			return referenceTimes[fold];
		}

		/**
		 * �܂�Ԃ��ʒu�̎Q�ƒl��ݒ肵�܂��B
		 * @param fold �܂�Ԃ��ʒu
		 * @param referenceTime �Q�Ǝ���
		 */
		public void setReferenceTime(int fold, Timestamp referenceTime) {
			checkArgument(fold);
			this.referenceTimes[fold] = referenceTime;
		}

		/**
		 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
		 * �����v���p�e�B�𕶎���\�����ĕԂ��܂��B���A���̃��\�b�h�͊J���i�K�Ŏg�p���ׂ��ł���A
		 * �e�X�g�ړI�ȊO�Ŏg�p���Ȃ��ł��������B�����A�Ԃ������e���ύX����邱�Ƃ�����܂��B
		 * @return �I�u�W�F�N�g�̕�����\��
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
		 * �ʂ̃I�u�W�F�N�g���A���̃I�u�W�F�N�g�Ɠ��������ׂ܂��B
		 * ���ʂ͈����� null �łȂ��A���̃I�u�W�F�N�g�Ɠ����e�v���p�e�B�[�i�t�B�[���h�j�̒l������
		 * �I�u�W�F�N�g�ł���ꍇ�� true ��Ԃ��܂��B
		 * @param obj ���� GraphSeriesProperty �Ɠ��������ǂ��������肳���I�u�W�F�N�g
		 * @return �I�u�W�F�N�g�������ł���ꍇ�� true�A�����łȂ��ꍇ�� false
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
		 * ���� GraphSeriesProperty �̃n�b�V���R�[�h���v�Z���܂��B
		 * @return ���� GraphSeriesProperty �̃n�b�V���R�[�h�l
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
