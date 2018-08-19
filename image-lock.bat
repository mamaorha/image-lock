@ECHO OFF
set mypath=%~dp0
start javaw -jar "%mypath%/image-lock-1.0.0.jar" %1%
exit