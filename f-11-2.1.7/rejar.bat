@REM F-11.jar‚ðƒŠƒrƒ‹ƒh‚µ‚Ü‚·
del /q /f /s webapps\ROOT\lib
copy lib\* webapps\ROOT\lib
jar cvfm webapps\ROOT\lib\F-11.jar META-INF/MANIFEST.MF -C classes .
jar uvf webapps\ROOT\lib\F-11.jar resources images sounds
copy webapps\ROOT\lib\F-11.jar webapps\ROOT\WEB-INF\lib
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\activation.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\aopalliance-1.0.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\commons-beanutils-core.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\commons-collections-3.1.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\commons-configuration-1.1.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\commons-dbcp-1.2.1.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\commons-digester-1.7.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\commons-email-1.0.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\commons-lang-2.1.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\commons-logging-1.0.4.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\commons-pool-1.3.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\commons-primitives-1.0.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\dbunit-2.1.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\F-11.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\geronimo-j2ee_1.4_spec-1.0.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\geronimo-jta_1.0.1B_spec-1.0.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\hsqldb.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\images_f11.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\itext-1.02b.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\jasperreports-0.5.3.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\javassist-3.0.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\jcalendar.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\jim.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\jlfgr-1_0.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\jta.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\log4j-1.2.8.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\mail.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\mysql-connector-java-3.1.13-bin.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\ognl-2.6.5.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\poi-2.5.1-final-20040804.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\postgresql-8.0-314.jdbc3.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\resources.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\s2-dao-1.0.39.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\s2-extension-2.3.15.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\s2-framework-2.3.15.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\Vamp.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\xalan.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\xercesImpl.jar f-11
jarsigner -keystore .keystore -storepass freedom9713 webapps\ROOT\lib\xml-apis.jar f-11
