@REM F-11.jar‚ðƒŠƒrƒ‹ƒh‚µ‚Ü‚·
del /q /f /s webapps\ROOT\lib
copy lib\* webapps\ROOT\lib
jar cvfm webapps\ROOT\lib\F-11.jar META-INF/MANIFEST.MF -C classes .
jar uvf webapps\ROOT\lib\F-11.jar resources images sounds
copy webapps\ROOT\lib\F-11.jar webapps\ROOT\WEB-INF\lib
@rem for jdk1.4
rmic -v1.2 -classpath webapps\ROOT\WEB-INF\classes;webapps\ROOT\WEB-INF\lib\struts.jar -d webapps\ROOT\WEB-INF\classes org.F11.scada.tool.autoprint.AutoPrintServiceImpl
rmic -v1.2 -classpath webapps\ROOT\WEB-INF\classes;webapps\ROOT\WEB-INF\lib\struts.jar -d webapps\ROOT\WEB-INF\classes org.F11.scada.tool.emailgroup.attribute.AttributeServiceImpl
rmic -v1.2 -classpath webapps\ROOT\WEB-INF\classes;webapps\ROOT\WEB-INF\lib\struts.jar -d webapps\ROOT\WEB-INF\classes org.F11.scada.tool.emailgroup.individual.IndividualServiceImpl
rmic -v1.2 -classpath webapps\ROOT\WEB-INF\classes;webapps\ROOT\WEB-INF\lib\struts.jar -d webapps\ROOT\WEB-INF\classes org.F11.scada.tool.emailgroup.master.MasterServiceImpl
rmic -v1.2 -classpath webapps\ROOT\WEB-INF\classes;webapps\ROOT\WEB-INF\lib\struts.jar -d webapps\ROOT\WEB-INF\classes org.F11.scada.tool.group.GroupServiceImpl
rmic -v1.2 -classpath webapps\ROOT\WEB-INF\classes;webapps\ROOT\WEB-INF\lib\struts.jar -d webapps\ROOT\WEB-INF\classes org.F11.scada.tool.page.bar.BarServiceImpl
rmic -v1.2 -classpath webapps\ROOT\WEB-INF\classes;webapps\ROOT\WEB-INF\lib\struts.jar -d webapps\ROOT\WEB-INF\classes org.F11.scada.tool.page.trend.TrendServiceImpl
rmic -v1.2 -classpath webapps\ROOT\WEB-INF\classes;webapps\ROOT\WEB-INF\lib\struts.jar -d webapps\ROOT\WEB-INF\classes org.F11.scada.tool.point.name.NameServiceImpl
rmic -v1.2 -classpath webapps\ROOT\WEB-INF\classes;webapps\ROOT\WEB-INF\lib\struts.jar -d webapps\ROOT\WEB-INF\classes org.F11.scada.tool.sound.attribute.AttributeSetServiceImpl
rmic -v1.2 -classpath webapps\ROOT\WEB-INF\classes;webapps\ROOT\WEB-INF\lib\struts.jar -d webapps\ROOT\WEB-INF\classes org.F11.scada.tool.sound.individual.IndividualSetServiceImpl
rmic -v1.2 -classpath webapps\ROOT\WEB-INF\classes;webapps\ROOT\WEB-INF\lib\struts.jar -d webapps\ROOT\WEB-INF\classes org.F11.scada.tool.user.UserGroupServiceImpl
