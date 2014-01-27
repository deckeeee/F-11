@echo off
@call setenv.bat
java -Dsun.nio.cs.map=x-windows-iso2022jp/ISO-2022-JP -Xmx64m -classpath %CL% org.F11.scada.xwife.server.EndCommand %1
