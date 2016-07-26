call setenv.bat
java -Dcom.sun.management.jmxremote -server -Xmx256m -Xms256m -Xmn128m -XX:SurvivorRatio=2 -Djava.rmi.server.codebase="file:classes file:lib/jim.jar" -Djava.security.policy=file:policy -classpath %CL% org.F11.scada.xwife.server.WifeMain %1 %2
