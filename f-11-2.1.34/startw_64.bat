call setenv.bat
java -server -Xmx1g -Xms1g -Djava.rmi.server.useCodebaseOnly=false -Dsun.nio.cs.map=x-windows-iso2022jp/ISO-2022-JP -Dcom.sun.management.jmxremote -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Djava.rmi.server.codebase="file:classes file:lib/jim.jar" -Djava.security.policy=file:policy -classpath %CL% org.F11.scada.xwife.server.WifeMain %1 %2
