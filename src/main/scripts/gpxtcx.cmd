@echo off
rem Copyright 2011-2015 Thomas Kern
rem 
rem Licensed under the MIT License

setlocal EnableDelayedExpansion EnableExtensions

set JAVA_EXE=java.exe

If defined JAVA_HOME (
    set JAVA_EXE=%JAVA_HOME%\bin\%JAVA_EXE%
) 

set VM_OPTS=-Dfile.encoding=utf-8 -Xmx768m -Xverify:none -XX:+TieredCompilation -XX:TieredStopAtLevel=1

set DIR=%~dp0
set LIBDIR=%DIR%lib

FOR /F "tokens=*" %%i IN ('DIR /A:-D /B %LIBDIR%') DO (
    IF NOT %%i==%~nx0 (                                                     
        set "CLASSPATH=!CLASSPATH!;%LIBDIR%\%%i"
    )
)

set CLASSPATH=%CLASSPATH%;%DIR%\gpxtcx-1.0-SNAPSHOT.jar


"%JAVA_EXE%" %VM_OPTS% -cp "%CLASSPATH%" de.kernwelt.gpxtcx.Main %1 %2 %3 %4 %5 %6 %7 %8 %9
endlocal