call setenv.bat
java -agentlib:yjpagent -Xloggc:"gc.txt" -XX:+PrintGCDetails -XX:+PrintClassHistogram -Xmx128m -Xms128m -Xmn64m -classpath %CL% org.F11.scada.xwife.applet.WifeAppletD %1
@rem start javaw -Xloggc:"gc.txt" -XX:+PrintGCDetails -XX:+PrintClassHistogram -Xmx128m -Xms128m -Xmn64m -classpath %CL% org.F11.scada.xwife.applet.WifeAppletD %1
@rem java -Xloggc:"gc.txt" -XX:+PrintGCDetails -XX:+PrintClassHistogram -Xmx128m -Xms128m -Xmn64m -classpath %CL% org.F11.scada.xwife.applet.WifeAppletD %1 > a.txt 2>&1
@rem start java -Xmx128m -Xms128m -Xmn64m -classpath %CL% org.F11.scada.xwife.applet.WifeAppletD -nosound -x 800 -y 0 -width 800 -height 500
@rem start java -Xmx128m -Xms128m -Xmn64m -classpath %CL% org.F11.scada.xwife.applet.WifeAppletD -nosound -x 0 -y 500 -width 800 -height 500
@rem start java -Xmx128m -Xms128m -Xmn64m -classpath %CL% org.F11.scada.xwife.applet.WifeAppletD -nosound -x 800 -y 500 -width 800 -height 500
@rem java -Xmx128m -Xms128m -Xmn64m -classpath %CL% org.F11.scada.xwife.applet.WifeAppletD -x 0 -y 0 -width 600 -height 600
