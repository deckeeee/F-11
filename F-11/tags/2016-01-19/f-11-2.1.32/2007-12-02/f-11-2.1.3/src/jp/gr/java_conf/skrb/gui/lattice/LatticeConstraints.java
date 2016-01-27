package jp.gr.java_conf.skrb.gui.lattice;

import java.awt.Insets;

/**
 * LatticeConstraints クラスは LatticeLayout クラス用の制約クラスです。
 * 
 * @see LatticeLayout
 */
public class LatticeConstraints implements Cloneable, java.io.Serializable {
	private static final long serialVersionUID = 7717871975607487221L;

	/**
	 * コンポーネントの x 座標
	 * デフォルトは 0
	 */
	public int x;

	/**
	 * コンポーネントの y 座標
	 * デフォルトは 0
	 */
	public int y;

	/**
	 * コンポーネントの表示領域幅
	 * デフォルトは 1
	 */
	public int width;

	/**
	 * コンポーネントの表示領域高
	 * デフォルトは 1
	 */
	public int height;

	/**
	 * コンポーネントが表示領域幅より小さい場合、描画の指定に使用する
	 * LEFT, CENTER, RIGHT のいずれかを値をとる
	 * デフォルトは LEFT
	 */
	public int halign;

	/**
	 * コンポーネントが表示領域高より小さい場合、描画の指定に使用する
	 * TOP, CENTER, BOTTOM のいずれかを値をとる
	 * デフォルトは TOP
	 */
	public int valign;

	/**
	 * コンポーネントが表示領域より小さい場合、拡大表示を行うかどうか指定する
	 * NONE, HORIZONTAL, VERTICAL, BOTH のいずれかを値をとる
	 * デフォルトは NONE
	 */
	public int fill;

	/**
	 * コンポーネントが表示領域より大きい場合、縮小表示を行うかどうか指定する
	 * NONE, HORIZONTAL, VERTICAL, BOTH のいずれかを値をとる
	 * デフォルトは NONE
	 */
	public int adjust;

	/**
	 * 左側の余白
	 * デフォルトは 0
	 */
	public int left;

	/**
	 * 右側の余白
	 * デフォルトは 0
	 */
	public int right;

	/**
	 * 上部の余白
	 * デフォルトは 0
	 */
	public int top;

	/**
	 * 下部の余白
	 * デフォルトは 0
	 */
	public int bottom;

	/**
	 * fill と adjust で使用する定数。拡大・縮小をしない場合
	 */
	public final static int NONE = -1;

	/**
	 * fill と adjust で使用する定数。拡大・縮小を縦・横共に行う場合
	 */
	public final static int BOTH = 0;

	/**
	 * fill と adjust で使用する定数。縦方向の拡大・縮小を行う場合
	 */
	public final static int VERTICAL = 1;

	/**
	 * fill と adjust で使用する定数。横方向の拡大・縮小を行う場合
	 */
	public final static int HORIZONTAL = 2;

	/**
	 * halign と valign で使用する定数。中央に表示する場合
	 */
	public final static int CENTER = 0;

	/**
	 * valign で使用する定数。上付きで表示する場合
	 */
	public final static int TOP = 1;

	/**
	 * valign で使用する定数。下付きで表示する場合
	 */
	public final static int BOTTOM = 2;

	/**
	 * halign で使用する定数。左詰で表示する場合
	 */
	public final static int LEFT = 3;

	/**
	 * halign で使用する定数。右詰で表示する場合
	 */
	public final static int RIGHT = 4;

	/**
	 * 全てをデフォルト値で生成するコンストラクタ
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
	 * 全てのプロパティを指定して生成するコンストラクタ
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
	 * オブジェクトの複製を作成
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
	 * 文字列への変更
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
