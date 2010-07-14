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
package org.F11.scada.etc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Hideaki Maekawa <frdm@user.sourceforge.jp>
 */
public class TextToSql {
    public static SqlOutput INSERT = new SqlOutputInsert();
    public static SqlOutput UPDATE = new SqlOutputUpdate();

    public TextToSql(String path, SqlOutput out) throws IOException {
        BufferedReader in = null;
        try {
             in = new BufferedReader(new FileReader(path));
             out.write(in);
        } catch (FileNotFoundException e) {
            System.err.println("ファイルがありません。" + path);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
    
    interface SqlOutput {
        void write(BufferedReader in) throws IOException;
    }

    static class SqlOutputInsert implements SqlOutput {
        public void write(BufferedReader in) throws IOException {
            String table = in.readLine();
            in.readLine();	// skip primary key
            String header = createString(in.readLine());
            for (String s = in.readLine(); s != null; s = in.readLine()) {
                String d = createString(s);
                System.out.println("INSERT INTO " + table + " (" + header + ")" + " VALUES (" + d + ");");
            }
        }
        
        String createString(String header) {
            StringTokenizer st = new StringTokenizer(header, "\t");
            StringBuffer b = new StringBuffer(header.length());
            while (st.hasMoreTokens()) {
                String h = st.nextToken();
                if (st.hasMoreTokens()) {
                    b.append(h).append(", ");
                } else {
                    b.append(h);
                }
            }
            return b.toString();
        }
    }

    static class SqlOutputUpdate implements SqlOutput {
        public void write(BufferedReader in) throws IOException {
            String table = in.readLine();
            String[] primaryKeys = createArray(in.readLine());
            String[] header = createArray(in.readLine());
            for (String s = in.readLine(); s != null; s = in.readLine()) {
                Map map = createMap(header, s);
                String update = createString(map);
                String where = createWhere(map, primaryKeys);
                System.out.println("UPDATE " + table + " SET " + update + " WHERE " + where + ";");
            }
        }
        
        String[] createArray(String header) {
            StringTokenizer st = new StringTokenizer(header, "\t");
            String[] headerArray = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                headerArray[i++] = st.nextToken();
            }
            return headerArray;
        }
        
        Map createMap(String[] keys, String values) {
            StringTokenizer st = new StringTokenizer(values, "\t");
            String[] valuesArray = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                valuesArray[i++] = st.nextToken();
            }
            
            return createMap(keys, valuesArray);
        }
        
        Map createMap(String[] keys, String[] values) {
            if (keys.length != values.length) {
                throw new IllegalArgumentException("column length invalid. (keys:" + keys.length + " values:" + values.length + ")");
            }

            LinkedHashMap map = new LinkedHashMap();
            for (int i = 0; i < keys.length; i++) {
                map.put(keys[i], values[i]);
            }
            
            return map;
        }
        
        String createString(Map map) {
            StringBuffer b = new StringBuffer(map.size() * 30);
            for (Iterator i = map.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                String value = (String) map.get(key);
                if (i.hasNext()) {
                    b.append(key + "=" + value + ", ");
                } else {
                    b.append(key + "=" + value);
                }
            }
            return b.toString();
        }
        
        String createWhere(Map map, String[] primaryKeys) {
            StringBuffer b = new StringBuffer(primaryKeys.length * 30);
            for (int i = 0; i < primaryKeys.length; i++) {
                String primaryKey = primaryKeys[i];
                if (i == (primaryKeys.length - 1)) {
                    b.append(primaryKey).append("=").append(map.get(primaryKey));
                } else {
                    b.append(primaryKey).append("=").append(map.get(primaryKey) + " AND ");
                }
            }
            return b.toString();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
          System.err.println("usage : TextToSql <input> [INSERT or UPDATE]");  
          return;
        }
        try {
            Field f = TextToSql.class.getField(args[1]);
            SqlOutput out = (SqlOutput) f.get(null);
            new TextToSql(args[0], out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            System.err.println("usage : TextToSql <input> [INSERT or UPDATE]");  
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
