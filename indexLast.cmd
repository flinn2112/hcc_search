REM ECHO OFF
REM XXX
X:
cd X:\SharedDownloads\Projekte\hcc\hcc_search
IF "%1"=="" GOTO USAGE

IF "%2" NEQ "" GOTO DEXDIR	
	java -jar X:\SharedDownloads\Projekte\hcc\hcc_search\dist\hcc_search.jar -lastMod %1 
GOTO XIT

:DEXDIR
java -jar X:\SharedDownloads\Projekte\hcc\hcc_search\dist\hcc_search.jar -lastMod %1 -deltaDir %2

:USAGE
ECHO OFF
ECHO.
ECHO ==================================
ECHO USAGE: indexLast.cmd lastModDays deltaDir
ECHO ==================================
ECHO.
ECHO lastModDays is the number of days back when files where modified
ECHO deltaDir is optional and will index only this directory tree
:XIT
