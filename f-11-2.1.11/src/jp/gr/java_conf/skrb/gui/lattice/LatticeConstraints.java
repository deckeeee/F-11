package jp.gr.java_conf.skrb.gui.lattice;

import java.awt.Insets;

/**
 * LatticeConstraints �N���X�� LatticeLayout �N���X�p�̐���N���X�ł��B
 * 
 * @see LatticeLayout
 */
public class LatticeConstraints implements Cloneable, java.io.Serializable {
	private static final long serialVersionUID = 7717871975607487221L;

	/**
	 * �R���|�[�l���g�� x ���W
	 * �f�t�H���g�� 0
	 */
	public int x;

	/**
	 * �R���|�[�l���g�� y ���W
	 * �f�t�H���g�� 0
	 */
	public int y;

	/**
	 * �R���|�[�l���g�̕\���̈敝
	 * �f�t�H���g�� 1
	 */
	public int width;

	/**
	 * �R���|�[�l���g�̕\���̈捂
	 * �f�t�H���g�� 1
	 */
	public int height;

	/**
	 * �R���|�[�l���g���\���̈敝��菬�����ꍇ�A�`��̎w��Ɏg�p����
	 * LEFT, CENTER, RIGHT �̂����ꂩ��l���Ƃ�
	 * �f�t�H���g�� LEFT
	 */
	public int halign;

	/**
	 * �R���|�[�l���g���\���̈捂��菬�����ꍇ�A�`��̎w��Ɏg�p����
	 * TOP, CENTER, BOTTOM �̂����ꂩ��l���Ƃ�
	 * �f�t�H���g�� TOP
	 */
	public int valign;

	/**
	 * �R���|�[�l���g���\���̈��菬�����ꍇ�A�g��\�����s�����ǂ����w�肷��
	 * NONE, HORIZONTAL, VERTICAL, BOTH �̂����ꂩ��l���Ƃ�
	 * �f�t�H���g�� NONE
	 */
	public int fill;

	/**
	 * �R���|�[�l���g���\���̈���傫���ꍇ�A�k���\�����s�����ǂ����w�肷��
	 * NONE, HORIZONTAL, VERTICAL, BOTH �̂����ꂩ��l���Ƃ�
	 * �f�t�H���g�� NONE
	 */
	public int adjust;

	/**
	 * �����̗]��
	 * �f�t�H���g�� 0
	 */
	public int left;

	/**
	 * �E���̗]��
	 * �f�t�H���g�� 0
	 */
	public int right;

	/**
	 * �㕔�̗]��
	 * �f�t�H���g�� 0
	 */
	public int top;

	/**
	 * �����̗]��
	 * �f�t�H���g�� 0
	 */
	public int bottom;

	/**
	 * fill �� adjust �Ŏg�p����萔�B�g��E�k�������Ȃ��ꍇ
	 */
	public final static int NONE = -1;

	/**
	 * fill �� adjust �Ŏg�p����萔�B�g��E�k�����c�E�����ɍs���ꍇ
	 */
	public final static int BOTH = 0;

	/**
	 * fill �� adjust �Ŏg�p����萔�B�c�����̊g��E�k�����s���ꍇ
	 */
	public final static int VERTICAL = 1;

	/**
	 * fill �� adjust �Ŏg�p����萔�B�������̊g��E�k�����s���ꍇ
	 */
	public final static int HORIZONTAL = 2;

	/**
	 * halign �� valign �Ŏg�p����萔�B�����ɕ\������ꍇ
	 */
	public final static int CENTER = 0;

	/**
	 * valign �Ŏg�p����萔�B��t���ŕ\������ꍇ
	 */
	public final static int TOP = 1;

	/**
	 * valign �Ŏg�p����萔�B���t���ŕ\������ꍇ
	 */
	public final static int BOTTOM = 2;

	/**
	 * halign �Ŏg�p����萔�B���l�ŕ\������ꍇ
	 */
	public final static int LEFT = 3;

	/**
	 * halign �Ŏg�p����萔�B�E�l�ŕ\������ꍇ
	 */
	public final static int RIGHT = 4;

	/**
	 * �S�Ă��f�t�H���g�l�Ő�������R���X�g���N�^
	 */
	public LatticeConstraints() {
		this(0, 0, 1, 1, LEFT, TOP, NONE, NONE, 0, 0, 0, 0);
	}

	public LatticeConstraints(int x, int y, int width, int height) {
		this(x, y, width, height, LEFT, TOP, NONE, NONE, 0, 0, 0, 0);
	}

	public LatticeConstraints(
		int x,
		int y,
		int width,
		int height,
		Insets insets) {
		this(
			x,
			y,
			width,
			height,
			LEFT,
			TOP,
			NONE,
			NONE,
			insets.top,
			insets.left,
			insets.bottom,
			insets.right);
	}

	public LatticeConstraints(
		int x,
		int y,
		int width,
		int height,
		int fill,
		int adjust,
		Insets insets) {
		this(
			x,
			y,
			width,
			height,
			LEFT,
			TOP,
			fill,
			adjust,
			insets.top,
			insets.left,
			insets.bottom,
			insets.right);
	}

	public LatticeConstraints(
		int x,
		int y,
		int width,
		int height,
		int halign,
		int valign,
		int fill,
		int adjust,
		Insets insets) {
		this(
			x,
			y,
			width,
			height,
			valign,
			halign,
			fill,
			adjust,
			insets.top,
			insets.left,
			insets.bottom,
			insets.right);
	}

	/**
	 * �S�Ẵv���p�e�B���w�肵�Đ�������R���X�g���N�^
	 */
	public LatticeConstraints(
		int x,
		int y,
		int width,
		int height,
		int halign,
		int valign,
		int fill,
		int adjust,
		int top,
		int left,
		int bottom,
		int right) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.halign = halign;
		this.valign = valign;
		this.fill = fill;
		this.adjust = adjust;
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	/**
	 * �I�u�W�F�N�g�̕������쐬
	 */
	public Object clone() {
		try {
			LatticeConstraints c = (LatticeConstraints) super.clone();
			return c;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	/**
	 * ������ւ̕ύX
	 */
	public String toString() {
		String fillStr = "";
		switch (fill) {
			case NONE :
				fillStr = "none";
				break;
			case BOTH :
				fillStr = "both";
				break;
			case VERTICAL :
				fillStr = "vertical";
				break;
			case HORIZONTAL :
				fillStr = "horizontal";
				break;
		}

		String adjustStr = "";
		switch (adjust) {
			case NONE :
				adjustStr = "none";
				break;
			case BOTH :
				adjustStr = "both";
				break;
			case VERTICAL :
				adjustStr = "vertical";
				break;
			case HORIZONTAL :
				adjustStr = "horizontal";
				break;
		}

		String halignStr = "";
		switch (halign) {
			case LEFT :
				halignStr = "left";
				break;
			case CENTER :
				halignStr = "center";
				break;
			case RIGHT :
				halignStr = "right";
				break;
		}

		String valignStr = "";
		switch (valign) {
			case TOP :
				valignStr = "top";
				break;
			case CENTER :
				valignStr = "center";
				break;
			case BOTTOM :
				valignStr = "bottom";
				break;
		}

		return "LatticeConstraints [x: "
			+ x
			+ " y: "
			+ y
			+ " width: "
			+ width
			+ " height: "
			+ height
			+ " halign: "
			+ halignStr
			+ " valign: "
			+ valignStr
			+ " fill: "
			+ fillStr
			+ " adjust: "
			+ adjustStr
			+ " top: "
			+ top
			+ " left: "
			+ left
			+ " bottom: "
			+ bottom
			+ " right: "
			+ right
			+ "]";
	}
}
