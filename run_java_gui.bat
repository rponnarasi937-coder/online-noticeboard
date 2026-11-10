@echo off
title KEC Online Notice Board - Pure Java Application
echo ========================================================
echo    Launching KEC Online Notice Board (Java Application)
echo ========================================================
echo.
mvn compile exec:java -Dexec.mainClass="com.kec.noticeboard.KecNoticeBoardSwingApp"
pause
