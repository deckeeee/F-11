 F-11 SCADA for Java                                       
 2002 Copyright (C) 2002 Freedom, Inc. All Rights Reserved.
 
 1.ライセンス
 2.謝辞
 3.ビルド・実行に必要な外部ライブラリ
 4.実行に必要な環境
 5.各設定
 6.ソースの文字コード等
 7.PLC通信コマンド


 
 1.ライセンス
 ------------
 このプログラムはGPL2でライセンスされます。詳しくは原文の通りです。
 添付のテキストファイル又は、以下のURIを参照して下さい。
 http://www.gnu.org/
 
 また、このプログラムで使用している、外部ライブラリはそれぞれの
 ライセンスで提供されます。



 2.謝辞
 ------
 This product includes software developed 
 by the Apache Software Foundation (http://www.apache.org/).

 This product includes software developed by the 
 Seasar Project (http://www.seasar.org/).
 
 本ソフトウェアは数多くのボランティアによる貢献から成りたっています。


 
 3.ビルド・実行に必要な外部ライブラリの配布元
 --------------------------------------------
 F-11
   http://www.F-11.org/
   
 JIM
   http://www.j-industry.org/jiae/index.html
   
 Java Look and Feel Graphics Repository
   http://developer.java.sun.com/developer/techDocs/hi/repository/
   
 JUnit
   http://www.junit.org/
   
 Jakarta Apache Project
   http://jakarta.apache.org/
   
 PostgreSQL JDBC Driver
   http://jdbc.postgresql.org/



 4.実行に必要な環境
 ------------------
 Java2 Platform, Standard Edition (J2SE) SDK 1.4.0以上
   http://java.sun.com/
 


 5.各設定
 --------
 予めDBMSにTCP/IP経由で接続できる環境が必要です。現段階でのテスト済み
 のDBMSはPostgreSQL 7.2.xのみです。

 PostgreSQL以外のDBMSを使用する場合は、そのDBMSのJDBC Driverが必要になります。

 
 
 6.文字コード等
 --------------
 ソース等の文字コードは、MS932(所謂Shift-JIS)です。開発にWindows2000上で
 Eclipseを使用している為です。
 
 但し、SQLソースのみEUC-JPです。これはPostgreSQLで使用する（定義した）、
 文字コードにあわせてください。
 
 
 
 7.PLC通信コマンド
 -----------------
 現状(2002/11)ではOMRON社製PLC用のFINSコマンドのみ実装されています。
