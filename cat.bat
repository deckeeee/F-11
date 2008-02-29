@call setenv.bat
@SET CL=./cat;build;cat/lib/AppFramework-1.03.jar;cat/lib/swing-worker.jar;%CL%
java -Xmx256m -Xms128m -classpath %CL% org.F11.scada.cat.MainFrame
