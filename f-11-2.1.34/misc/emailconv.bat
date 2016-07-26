cd ..
call setenv.bat
java -client -Xmx256m -Xms128m -classpath %CL% org.F11.scada.misc.convert.EMailAttributeConvertImpl
java -client -Xmx256m -Xms128m -classpath %CL% org.F11.scada.misc.convert.EMailIndividualConvertImpl
cd misc