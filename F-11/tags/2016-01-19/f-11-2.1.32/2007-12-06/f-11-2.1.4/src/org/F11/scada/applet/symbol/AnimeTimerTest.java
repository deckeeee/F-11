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

package org.F11.scada.applet.symbol;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import junit.framework.TestCase;

/**
 * AnimeTimerのテストケース
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class AnimeTimerTest extends TestCase {
    private AnimeTimer timer;

    public static void main(String[] args) {
        junit.swingui.TestRunner.run(AnimeTimerTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        timer = AnimeTimer.getInstance();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        timer.stop();
    }

    /**
     * Constructor for AnimeTimerTest.
     * @param arg0
     */
    public AnimeTimerTest(String arg0) {
        super(arg0);
    }

    public void testAnimeTimer() throws Exception {
        TestActionListener l = new TestActionListener();
        timer.addActionListener(l);

        int now = l.current;
        int old = now;
        assertEquals(0, now);

        Thread.sleep(1000L);
        old = now;
        now = l.current;
        assertTrue(now != old);

        Thread.sleep(1000L);
        old = now;
        now = l.current;
        assertTrue(now != old);

        Thread.sleep(1000L);
        old = now;
        now = l.current;
        assertTrue(now != old);

        Thread.sleep(1000L);
        old = now;
        now = l.current;
        assertTrue(now != old);

        Thread.sleep(1000L);
        old = now;
        now = l.current;
        assertTrue(now != old);

        Thread.sleep(1000L);
        old = now;
        now = l.current;
        assertTrue(now != old);

        Thread.sleep(1000L);
        old = now;
        now = l.current;
        assertTrue(now != old);

        Thread.sleep(1000L);
        old = now;
        now = l.current;
        assertTrue(now != old);
    }


    static class TestActionListener implements ActionListener {
        int[] intArray = new int[]{0, 1, 2, 3, 4, 5, 6};
        int current;
        AnimeTimer timer = AnimeTimer.getInstance();
        public void actionPerformed(ActionEvent e) {
            current = intArray[timer.getAnimeCount(intArray.length)];
        }
    }
}
