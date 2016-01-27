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
 */
package org.F11.scada.server.wifi;

import java.sql.Timestamp;

import org.F11.scada.server.io.postgresql.S2ContainerUtil;
import org.F11.scada.server.wifi.LocationHistorysDao;
import org.F11.scada.server.wifi.dto.LocationHistorys;
import org.F11.scada.util.ThreadUtil;
import org.seasar.framework.container.S2Container;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class Test {
    private volatile int xPos;
    private volatile int yPos;
    
    public Test() {
        S2Container container = S2ContainerUtil.getS2Container();
        LocationHistorysDao dao = (LocationHistorysDao) container.getComponent(LocationHistorysDao.class);
        LocationHistorys dto = new LocationHistorys();

        /*
         INSERT into location_historys (time_stamp, id, x_position, y_position, floor_id)
         values ('2005-5-19 09:00:00', '000CCC5BF259', 0, 0, 1);
         */
        while (true) {
            dto.setId("000CCC5BF259");
            dto.setFloorId(1);
            dto.setTimeStamp(new Timestamp(System.currentTimeMillis()));
            dto.setXPosition(getXPosition());
            for (int j = 0; j < 5; j++) {
                dto.setYPosition(getYPosition());
                dao.insertLocationHistorys(dto);
                ThreadUtil.sleep(1000L);
            }
        }
    }
    
    int getXPosition() {
/*        
        if (xPos >= 500) {
            xPos = 0;
        } else {
            xPos += 100;
        }
*/
        return xPos;
    }

    int getYPosition() {
        if (yPos >= 500) {
            yPos = 0;
        } else {
            yPos += 100;
        }
        return yPos;
    }

    public static void main(String[] args) {
        new Test();
    }
}
