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

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.F11.scada.applet.ClientConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 * �V���{���Ɏg�p����O���t�B�b�N�摜�̊Ǘ������܂��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public abstract class GraphicManager {
	private static final String IMAGES_BASE_PATH = "/images";
	private static ImageLoader imageLoader;
	static {
		Configuration configuration = new ClientConfiguration();
		if (configuration.getBoolean("org.F11.scada.applet.symbol.GraphicManager", true)) {
			imageLoader = new DefaultImageLoader();
		} else {
			imageLoader = new IconImageLoader();
		}
	}

	/**
	 * Icon�̃C���X�^���X����
	 * 
	 * @param path �摜�C���[�W�ւ̃p�X
	 * @return �摜�C���[�W�� Icon �I�u�W�F�N�g�B�t�@�C�������݂��Ȃ����null ��Ԃ��B
	 */
	public static synchronized Icon get(String path) {
		return imageLoader.getIcon(path);
	}

	/**
	 * �C���[�W�t�@�C���̃x�[�X�p�X��Ԃ��܂��B �G�f�B�^�p
	 * 
	 * @return
	 */
	public static File getBasePath() {
		URL url = GraphicManager.class.getResource(IMAGES_BASE_PATH);
		return new File(url.getFile());
	}

	/**
	 * �t���p�X����x�[�X�p�X�̕������폜�����p�X�𕶎���ŕԂ��܂��B �G�f�B�^�p
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

	interface ImageLoader {
		Icon getIcon(String path);
	}

	private static class DefaultImageLoader implements ImageLoader {
		private Map<String, Icon> iconMap = new WeakHashMap<String, Icon>();

		/**
		 * Icon�̃C���X�^���X����
		 * 
		 * @param path �摜�C���[�W�ւ̃p�X
		 * @return �摜�C���[�W�� Icon �I�u�W�F�N�g�B�t�@�C�������݂��Ȃ����null ��Ԃ��B
		 */
		public Icon getIcon(String path) {
			Icon ic = null;
			if (iconMap.containsKey(path)) {
				ic = (Icon) iconMap.get(path);
			} else {
				if (path != null) {
					Image image = loadImage(path);
					if (null != image) {
						ic = new ImageIcon(image);
						iconMap.put(path, ic);
					}
				}
			}
			return ic;
		}

		private Image loadImage(String path) {
			InputStream is = null;
			URL url = GraphicManager.class.getResource(path);
			if (null == url) {
				return null;
			}
			try {
				is = url.openStream();
				return ImageIO.read(is);
			} catch (Exception e) {
				return null;
			} finally {
				if (null != is) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	private static class IconImageLoader implements ImageLoader {
		private Map<String, Icon> iconMap = new WeakHashMap<String, Icon>();

		public Icon getIcon(String path) {
			Icon ic = null;
			if (iconMap.containsKey(path)) {
				ic = (Icon) iconMap.get(path);
			} else {
				if (path != null) {
					URL url = GraphicManager.class.getResource(path);
					if (url != null) {
						ic = new ImageIcon(url);
						iconMap.put(path, ic);
					}
				}
			}
			return ic;
		}
	}
}
