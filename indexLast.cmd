X:
cd X:\SharedDownloads\Projekte\hcc\hcc_search
IF "%1"=="" GOTO USAGE

java -jar X:\SharedDownloads\Projekte\hcc\hcc_search\dist\hcc_search.jar -lastMod %1
GOTO XIT

:USAGE
ECHO OFF
ECHO.
ECHO ==================================
ECHO USAGE: indexLast.cmd lastModDays 
ECHO ==================================
ECHO.
ECHO lastModDays is the number of days back when files where modified
:XIT
