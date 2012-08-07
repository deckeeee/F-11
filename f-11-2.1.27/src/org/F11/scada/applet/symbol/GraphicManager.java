package org.F11.scada.applet.symbol;

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

import java.io.File;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.F11.scada.util.SoftHashMap;
import org.apache.log4j.Logger;

/**
 * シンボルに使用するグラフィック画像の管理をします。
 *
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public abstract class GraphicManager {
	private static Logger logger = Logger.getLogger(GraphicManager.class);
	private static final String IMAGES_BASE_PATH = "/images";
	private static ImageLoader imageLoader = new NewIconImageLoader();

	/**
	 * Iconのインスタンス生成
	 *
	 * @param path 画像イメージへのパス
	 * @return 画像イメージの Icon オブジェクト。ファイルが存在しなければnull を返す。
	 */
	public static synchronized Icon get(String path) {
		return imageLoader.getIcon(path);
	}

	/**
	 * イメージファイルのベースパスを返します。 エディタ用
	 *
	 * @return
	 */
	public static File getBasePath() {
		URL url = GraphicManager.class.getResource(IMAGES_BASE_PATH);
		return new File(url.getFile());
	}

	/**
	 * フルパスからベースパスの部分を削除したパスを文字列で返します。 エディタ用
	 *
	 * @param fullPath
	 * @return
	 */
	public static String cutBasePath(File fullPath) {
		String path = fullPath.toURI().toString();
		int p = path.indexOf(IMAGES_BASE_PATH);
		if (0 < p) {
			return path.substring(p);
		}
		return path;
	}

	/**
	 * イメージをロードするクラスのインターフェイス
	 *
	 * @author maekawa
	 *
	 */
	interface ImageLoader {
		/**
		 * Iconのインスタンス生成
		 *
		 * @param path 画像イメージへのパス
		 * @return 画像イメージの Icon オブジェクト。ファイルが存在しなければnull を返す。
		 */
		Icon getIcon(String path);
	}

	private abstract static class AbstractImageLoader implements ImageLoader {
		protected Map<String, SoftReference<Icon>> iconMap =
			new HashMap<String, SoftReference<Icon>>();

		public Icon getIcon(String path) {
			if (iconMap.containsKey(path)) {
				SoftReference<Icon> ref = iconMap.get(path);
				Icon icon = ref.get();
				return null == icon ? createIcon(path) : icon;
			} else {
				return createIcon(path);
			}
		}

		abstract protected Icon createIcon(String path);
	}

	private static class IconImageLoader extends AbstractImageLoader {
		@Override
		protected Icon createIcon(String path) {
			if (path != null) {
				URL url = GraphicManager.class.getResource(path);
				if (url != null) {
					Icon ic = new ImageIcon(url);
					if (isNotSupportImage(ic)) {
						logger.error("読込めない画像ファイル形式です = " + path);
					} else {
						iconMap.put(path, new SoftReference<Icon>(ic));
					}
					return ic;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}

		private boolean isNotSupportImage(Icon ic) {
			return ic.getIconHeight() < 0 || ic.getIconWidth() < 0;
		}
	}

	private static class NewIconImageLoader implements ImageLoader {
		protected Map<String, Icon> iconMap = new SoftHashMap<String, Icon>();

		public Icon getIcon(String path) {
			if (iconMap.containsKey(path)) {
				Icon icon = iconMap.get(path);
				return null == icon ? createIcon(path) : icon;
			} else {
				return createIcon(path);
			}
		}

		private Icon createIcon(String path) {
			if (path != null) {
				URL url = GraphicManager.class.getResource(path);
				if (url != null) {
					Icon ic = new ImageIcon(url);
					if (isNotSupportImage(ic)) {
						logger.error("読込めない画像ファイル形式です = " + path);
					} else {
						iconMap.put(path, ic);
					}
					return ic;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}

		private boolean isNotSupportImage(Icon ic) {
			return ic.getIconHeight() < 0 || ic.getIconWidth() < 0;
		}
	}
}
