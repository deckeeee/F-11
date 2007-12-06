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

package org.F11.scada.xwife.applet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.F11.scada.util.PageHistory;

/**
 * 頁履歴ボタンのアクションクラスのインスタンスを生成するファクトリーです。
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class PageHistoryActionFactory {
    private PageHistoryActionFactory() {}

    public static ActionListener createForwardAction(PageHistory history, PageChanger changer) {
        return new ForwardAction(history, changer);
    }
    
    public static ActionListener createBackAction(PageHistory history, PageChanger changer) {
        return new BackAction(history, changer);
    }
    
    private static class ForwardAction implements ActionListener {
        private final PageHistory history;
        private final PageChanger changer;

        ForwardAction(PageHistory history, PageChanger changer) {
            this.history = history;
            this.changer = changer;
        }

        public void actionPerformed(ActionEvent e) {
            String pageId = history.next();
            if (pageId != PageHistory.EOP) {
                changer.changePage(new PageChangeEvent(this, pageId, false, true));
            }
        }
    }
    
    private static class BackAction implements ActionListener {
        private final PageHistory history;
        private final PageChanger changer;

        BackAction(PageHistory history, PageChanger changer) {
            this.history = history;
            this.changer = changer;
        }

        public void actionPerformed(ActionEvent e) {
            String pageId = history.previous();
            if (pageId != PageHistory.EOP) {
                changer.changePage(new PageChangeEvent(this, pageId, false, true));
            }
        }
    }
}
