package jp.gr.java_conf.skrb.gui.lattice;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * LatticeLayout クラスはコンポーネントを格子状に配置できるレイアウトマネージャです。
 */
public class LatticeLayout implements LayoutManager2, Serializable {
	private static final long serialVersionUID = 7530505093812432074L;
	private Map compTable;
	private int latticeX = 1;
	private int latticeY = 1;

	private static final int PREFERRED_SIZE = 0;
	private static final int MINIMUM_SIZE = 1;
	private static final int MAXIMUM_SIZE = 2;
	/** レイアウト変更フラグ */
	private volatile boolean isNotEdit;

	/**
	 * デフォルト値の LatticeConstraints オブジェクト
	 */
	private final LatticeConstraints defaultConstraints = new LatticeConstraints();

	public LatticeLayout() {
		compTable = new HashMap();
	}

	public LatticeLayout(int x, int y) {
		this();

		latticeX = x;
		latticeY = y;
	}

	public void addLayoutComponent(String name, Component comp) {
		throw new UnsupportedOperationException();
	}

	public void addLayoutComponent(Component comp, Object constraints) {
		if (constraints instanceof LatticeConstraints) {
			setConstraints(comp, (LatticeConstraints) constraints);
		} else if (constraints != null) {
			throw new IllegalArgumentException("cannot add to layout: constraints must be a LatticeConstraint");
		}
	}

	public void removeLayoutComponent(Component comp) {
		compTable.remove(comp);
		isNotEdit = false;
	}

	public void setConstraints(
		Component comp,
		LatticeConstraints constraints) {
		compTable.put(comp, constraints.clone());
		isNotEdit = false;
	}

	public LatticeConstraints getConstraints(Component comp) {
		LatticeConstraints constraints = lookupConstraints(comp);

		return (LatticeConstraints) constraints.clone();
	}

	protected LatticeConstraints lookupConstraints(Component comp) {
	    if (compTable.containsKey(comp)) {
	        return (LatticeConstraints) compTable.get(comp);
	    } else {
			setConstraints(comp, defaultConstraints);
			return defaultConstraints;
	    }
	}

	private Dimension getComponentLayoutSize(Component comp, int preferred) {
		Dimension d;

		if (preferred == PREFERRED_SIZE) {
			d = comp.getPreferredSize();
		} else if (preferred == MINIMUM_SIZE) {
			d = comp.getMinimumSize();
		} else {
			d = comp.getMaximumSize();
		}

		return d;
	}

	private Dimension getLayoutSize(Container parent, int preferred) {
		Dimension dim = new Dimension(0, 0);
		int num = parent.getComponentCount();

		for (int i = 0; i < num; i++) {
			Component m = parent.getComponent(i);
			if (m.isVisible()) {
				Dimension d = getComponentLayoutSize(m, preferred);
				dim.width = Math.max(d.width, dim.width);
				dim.height = Math.max(d.height, dim.height);
			}
		}

		dim.width = dim.width * latticeX;
		dim.height = dim.height * latticeY;

		return dim;
	}

	public Dimension preferredLayoutSize(Container parent) {
		return getLayoutSize(parent, PREFERRED_SIZE);
	}

	public Dimension minimumLayoutSize(Container parent) {
		return getLayoutSize(parent, MINIMUM_SIZE);
	}

	public Dimension maximumLayoutSize(Container parent) {
		return getLayoutSize(parent, MAXIMUM_SIZE);
	}

	public float getLayoutAlignmentX(Container parent) {
		return 0.5f;
	}

	public float getLayoutAlignmentY(Container parent) {
		return 0.5f;
	}

	public void invalidateLayout(Container target) {
		isNotEdit = false;
	}

	public void layoutContainer(Container parent) {
		if (isNotEdit) {
			return;
		}

		Dimension containerSize = parent.getSize();
		Insets insets = parent.getInsets();

		double oneLatticeX =
			(double) (containerSize.width - insets.left - insets.right)
				/ latticeX;
		double oneLatticeY =
			(double) (containerSize.height - insets.top - insets.bottom)
				/ latticeY;

		Component[] comps = parent.getComponents();

		for (int i = 0; i < comps.length; i++) {
			Component comp = comps[i];
			Dimension dim = comp.getPreferredSize();
			int prefW = dim.width;
			int prefH = dim.height;
			LatticeConstraints cons = lookupConstraints(comp);

			int x = (int) (cons.x * oneLatticeX) + cons.left + insets.left;
			int y = (int) (cons.y * oneLatticeY) + cons.top + insets.top;
			int w = (int) (cons.width * oneLatticeX) - cons.left - cons.right;
			int h = (int) (cons.height * oneLatticeY) - cons.top - cons.bottom;

			if (w > prefW) {
				switch (cons.fill) {
					case LatticeConstraints.NONE :
					case LatticeConstraints.VERTICAL :
						switch (cons.halign) {
							case LatticeConstraints.CENTER :
								x += ((w - prefW) / 2);
								break;
							case LatticeConstraints.RIGHT :
								x += (w - prefW);
								break;
						}
						w = prefW;
						break;
				}
			} else {
				switch (cons.adjust) {
					case LatticeConstraints.NONE :
					case LatticeConstraints.VERTICAL :
						w = prefW;
						break;
				}
			}

			if (h > prefH) {
				switch (cons.fill) {
					case LatticeConstraints.NONE :
					case LatticeConstraints.HORIZONTAL :
						switch (cons.valign) {
							case LatticeConstraints.CENTER :
								y += ((h - prefH) / 2);
								break;
							case LatticeConstraints.BOTTOM :
								y += (h - prefH);
								break;
						}

						h = prefH;
						break;
				}
			} else {
				switch (cons.adjust) {
					case LatticeConstraints.NONE :
					case LatticeConstraints.HORIZONTAL :
						h = prefH;
						break;
				}
			}

			if (x + w > containerSize.width
				|| x + w < 0
				|| y + h < 0
				|| y + h > containerSize.height) {
				comp.setBounds(0, 0, 0, 0);
			} else {
				comp.setBounds(x, y, w, h);
			}
		}
		isNotEdit = true;
	}
}
