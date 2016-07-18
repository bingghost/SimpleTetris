@echo off&setlocal enabledelayedexpansion

set info_unstall=按任意键卸载
set info_install=按任意键安装APP
set info_exit=按任意键结束此bat
set info_unstalling=按任意键卸载
set info_activity=启动Activity

set animation_switch=1

:: **********************************************************************
::  Main函数 程序入口点
:: **********************************************************************
:main
	set package_name=com.bingghost.simpletetris
	set activity_name=com.bingghost.simpletetris.activity.MainActivity
	set apk_name=tetris.apk

	:: apk安装卸载流程
	call :apk_test %package_name% %activity_name% %apk_name%
goto:eof


:: **********************************************************************
::  app测试
:: **********************************************************************
:apk_test
	set package_name=%1
	set activity_name=%2
	set apk_name=%3


	:: 卸载APK
	call :process_bar %info_unstalling%:%package_name%
	call :uninstall_apk %package_name%
	call :show_info_not_pause %info_install%:%package_name%

	:: 安装APK
	call :process_bar %info_install%:%package_name%
	call :install_apk %apk_name%

	:: 启动Activity
	call :show_info_not_pause %info_activity%:%activity_name%
	call :run_activity %package_name% %activity_name%
	call :show_info %info_unstalling%:%package_name%

	:: 卸载APK
	call :process_bar %info_unstalling%:%package_name%
	call :uninstall_apk %package_name%
	call :show_info %info_exit%

goto:eof

:: **********************************************************************
::  显示信息
:: **********************************************************************
:show_info 
	color F9
	@echo ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@echo          %1
	@echo ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	pause > nul
goto:eof

:: **********************************************************************
::  显示信息
:: **********************************************************************
:show_info_not_pause
	color F9
	@echo .
	@echo .
	@echo .
	@echo ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@echo          %1
	@echo ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
goto:eof

:process_bar

if "%animation_switch%"=="0" goto end_loop
cls
color 3e
set box=■
set count=0
set sum=5

	:process_loop
	cls
	echo %1
	echo %box%
	echo 当前进度：%sum%\100
	set box=%box%■■

	set /a count+=5
	set /a sum=%count%+5
	if "%count%"=="100" goto end_loop
	ping /n 1 127.0.0.1 > nul
	goto process_loop
	:end_loop
goto:eof


:: **********************************************************************
::  安装APK
:: **********************************************************************
:install_apk
	adb install %1
goto:eof


:: **********************************************************************
::  卸装APK
:: **********************************************************************
:uninstall_apk
	adb uninstall %1
goto:eof

:run_activity
	set package_name=%1
	set activity_name=%2
	set format_symbol=/
	set name_format=%package_name%%format_symbol%%activity_name%

	adb shell am start -n %name_format%
goto:eof



