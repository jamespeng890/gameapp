@echo off
REM Match3 Game - 自动构建脚本
REM 功能：快速编译和运行游戏

setlocal enabledelayedexpansion

echo ================================
echo   Match3 Game Build System
echo ================================
echo.

if "%1"=="" (
    echo 使用方法：
    echo   build.bat clean      - 清理项目
    echo   build.bat debug      - 构建Debug版本
    echo   build.bat release    - 构建Release版本 (含混淆优化)
    echo   build.bat install    - 安装到设备/模拟器
    echo   build.bat run        - 安装并运行游戏
    echo.
    exit /b
)

if "%1"=="clean" (
    echo 正在清理项目...
    call gradlew clean
    echo 清理完成！
    exit /b
)

if "%1"=="debug" (
    echo 正在构建Debug版本...
    call gradlew assembleDebug
    if errorlevel 1 (
        echo 构建失败！
        exit /b 1
    )
    echo Debug APK 位置：app\build\outputs\apk\debug\app-debug.apk
    exit /b
)

if "%1"=="release" (
    echo 正在构建Release版本（含混淆优化）...
    call gradlew assembleRelease
    if errorlevel 1 (
        echo 构建失败！
        exit /b 1
    )
    echo Release APK 位置：app\build\outputs\apk\release\app-release-unsigned.apk
    exit /b
)

if "%1"=="install" (
    echo 正在安装Debug版本到设备...
    call gradlew installDebug
    if errorlevel 1 (
        echo 安装失败！请确保设备已连接
        exit /b 1
    )
    echo 安装完成！
    exit /b
)

if "%1"=="run" (
    echo 正在安装并运行游戏...
    call gradlew installDebug
    if errorlevel 1 (
        echo 安装失败！
        exit /b 1
    )
    echo 正在启动应用...
    adb shell am start -n com.gamedev.match3/com.gamedev.match3.MainActivity
    exit /b
)

echo 未知命令：%1
exit /b 1
