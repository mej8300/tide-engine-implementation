@echo off
@setlocal
@echo ----------------------------
@echo Tide publisher
@echo ----------------------------
::
set RADICAL=%1
@echo Transforming %RADICAL%.xml into %RADICAL%.pdf
::
cd %~dp0
set OLIVSOFT_HOME=..\..
::
set CP=%CP%;%OLIVSOFT_HOME%\all-3rd-party\xmlparserv2.jar
set CP=%CP%;%OLIVSOFT_HOME%\all-3rd-party\orai18n-collation.jar
set CP=%CP%;%OLIVSOFT_HOME%\all-3rd-party\orai18n-mapping.jar
set CP=%CP%;%OLIVSOFT_HOME%\all-3rd-party\fnd2.zip
set CP=%CP%;%OLIVSOFT_HOME%\all-3rd-party\xdo-0301.jar
::
set XSL_STYLESHEET=%~dp0\tide2fop.xsl
@echo Publishing
@java -Xms256m -Xmx1024m -classpath %CP% oracle.apps.xdo.template.FOProcessor %PRM_OPTION% -xml %RADICAL%.xml -xsl %XSL_STYLESHEET% -pdf %RADICAL%.pdf
@echo Done transforming, displaying.
call %RADICAL%.pdf
:end
::pause
@endlocal
exit