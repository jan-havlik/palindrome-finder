@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  genetika startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and GENETIKA_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\genetika-2.8.0.jar;%APP_HOME%\lib\jetty-webapp-9.3.30.v20211001.jar;%APP_HOME%\lib\javaee-api-7.0.jar;%APP_HOME%\lib\jersey-container-servlet-core-2.22.4.jar;%APP_HOME%\lib\jersey-media-moxy-2.22.4.jar;%APP_HOME%\lib\jersey-media-multipart-2.22.4.jar;%APP_HOME%\lib\jasdb_localservice-1.1.2.jar;%APP_HOME%\lib\jasdb_transwriter-1.1.2.jar;%APP_HOME%\lib\jasdb_index_btreeplus-1.1.2.jar;%APP_HOME%\lib\jasdb_entity-1.1.2.jar;%APP_HOME%\lib\jasdb_api-1.1.2.jar;%APP_HOME%\lib\jasdb_core-1.1.2.jar;%APP_HOME%\lib\jcl-over-slf4j-1.7.5.jar;%APP_HOME%\lib\slf4j-reload4j-1.7.36.jar;%APP_HOME%\lib\slf4j-api-1.7.36.jar;%APP_HOME%\lib\log4j-1.2.17.jar;%APP_HOME%\lib\commons-lang3-3.4.jar;%APP_HOME%\lib\commons-collections4-4.0.jar;%APP_HOME%\lib\commons-cli-1.4.jar;%APP_HOME%\lib\commons-io-2.4.jar;%APP_HOME%\lib\commons-codec-1.10.jar;%APP_HOME%\lib\gson-2.3.1.jar;%APP_HOME%\lib\commons-email-1.3.1.jar;%APP_HOME%\lib\opencsv-2.3.jar;%APP_HOME%\lib\javafx-base-12.0.1-linux.jar;%APP_HOME%\lib\jetty-xml-9.3.30.v20211001.jar;%APP_HOME%\lib\jetty-servlet-9.3.30.v20211001.jar;%APP_HOME%\lib\javax.mail-1.5.0.jar;%APP_HOME%\lib\jersey-server-2.22.4.jar;%APP_HOME%\lib\jersey-client-2.22.4.jar;%APP_HOME%\lib\jersey-media-jaxb-2.22.4.jar;%APP_HOME%\lib\jersey-common-2.22.4.jar;%APP_HOME%\lib\hk2-locator-2.4.0-b34.jar;%APP_HOME%\lib\javax.inject-2.4.0-b34.jar;%APP_HOME%\lib\jersey-entity-filtering-2.22.4.jar;%APP_HOME%\lib\javax.ws.rs-api-2.0.1.jar;%APP_HOME%\lib\org.eclipse.persistence.moxy-2.6.0.jar;%APP_HOME%\lib\mimepull-1.9.6.jar;%APP_HOME%\lib\spring-security-core-3.2.0.RELEASE.jar;%APP_HOME%\lib\spring-context-4.0.0.RELEASE.jar;%APP_HOME%\lib\guice-assistedinject-3.0.jar;%APP_HOME%\lib\guice-3.0.jar;%APP_HOME%\lib\hk2-api-2.4.0-b34.jar;%APP_HOME%\lib\hk2-utils-2.4.0-b34.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\mail-1.4.5.jar;%APP_HOME%\lib\activation-1.1.1.jar;%APP_HOME%\lib\jetty-security-9.3.30.v20211001.jar;%APP_HOME%\lib\jetty-server-9.3.30.v20211001.jar;%APP_HOME%\lib\jetty-http-9.3.30.v20211001.jar;%APP_HOME%\lib\jetty-io-9.3.30.v20211001.jar;%APP_HOME%\lib\jetty-util-9.3.30.v20211001.jar;%APP_HOME%\lib\javax.annotation-api-1.2.jar;%APP_HOME%\lib\jersey-guava-2.22.4.jar;%APP_HOME%\lib\osgi-resource-locator-1.0.1.jar;%APP_HOME%\lib\validation-api-1.1.0.Final.jar;%APP_HOME%\lib\org.eclipse.persistence.core-2.6.0.jar;%APP_HOME%\lib\javax.json-1.0.4.jar;%APP_HOME%\lib\jackson-mapper-asl-1.9.4.jar;%APP_HOME%\lib\spring-aop-4.0.0.RELEASE.jar;%APP_HOME%\lib\spring-beans-4.0.0.RELEASE.jar;%APP_HOME%\lib\spring-expression-4.0.0.RELEASE.jar;%APP_HOME%\lib\spring-core-4.0.0.RELEASE.jar;%APP_HOME%\lib\reload4j-1.2.19.jar;%APP_HOME%\lib\aopalliance-repackaged-2.4.0-b34.jar;%APP_HOME%\lib\javassist-3.18.1-GA.jar;%APP_HOME%\lib\org.eclipse.persistence.asm-2.6.0.jar;%APP_HOME%\lib\guava-16.0.jar;%APP_HOME%\lib\jackson-core-asl-1.9.4.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\commons-logging-1.1.1.jar;%APP_HOME%\lib\cglib-2.2.1-v20090111.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\asm-3.1.jar

@rem Execute genetika
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GENETIKA_OPTS%  -classpath "%CLASSPATH%" cz.mendelu.genetika.Start %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable GENETIKA_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%GENETIKA_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
