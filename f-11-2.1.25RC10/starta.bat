call setenv.bat
java -agentlib:yjpagent -Xmx128m -Xms128m -Xmn64m -classpath %CL% org.F11.scada.xwife.applet.WifeAppletD %1
@rem java -Xmx128m -Xms128m -Xmn64m -classpath %CL% org.F11.scada.xwife.applet.WifeAppletD %1
