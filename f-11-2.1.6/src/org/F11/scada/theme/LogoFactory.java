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

package org.F11.scada.theme;

import java.awt.FlowLayout;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.F11.scada.applet.symbol.GraphicManager;
import org.F11.scada.parser.alarm.AlarmDefine;
import org.F11.scada.parser.alarm.TitleConfig;

/**
 * Logoコンポーネントを生成するファクトリークラスです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class LogoFactory {
    private final AlarmDefine alarmDefine;

    /**
     * デフォルトの定義ファイルでファクトリーを初期化します。
     */
    public LogoFactory() {
        alarmDefine = new AlarmDefine();
    }
    
    /**
     * 引数で指定されたファイルで、ファクトリーを初期化します。
     * @param filePath 定義ファイル
     */
    public LogoFactory(String filePath) {
        alarmDefine = new AlarmDefine(filePath);
    }

    /**
     * Logoコンポーネントを生成して返します。
     * @return Logoコンポーネント
     */
    public JComponent getLogo() {
        JComponent component = new Logo();
        TitleConfig config = alarmDefine.getAlarmConfig().getTitleConfig();
        if (config != null) {
            String image = config.getImage();
            if (image != null && !"".equals(image)) {
                Icon icon = GraphicManager.get(image);
                if (icon != null) {
                    component = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                    component.add(new LogoTime());
                    JLabel iconLabel = new JLabel(icon);
                    iconLabel.addMouseListener(new AboutDialogListener());
                    component.add(iconLabel);
                }
            }
        }
        
        return component;
    }
}
