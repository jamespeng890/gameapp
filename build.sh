#!/bin/bash
# Match3 Game - 自动构建脚本
# 功能：快速编译和运行游戏

echo "================================"
echo "  Match3 Game Build System"
echo "================================"
echo

if [ -z "$1" ]; then
    echo "使用方法："
    echo "  ./build.sh clean      - 清理项目"
    echo "  ./build.sh debug      - 构建Debug版本"
    echo "  ./build.sh release    - 构建Release版本 (含混淆优化)"
    echo "  ./build.sh install    - 安装到设备/模拟器"
    echo "  ./build.sh run        - 安装并运行游戏"
    echo
    exit 0
fi

case "$1" in
    clean)
        echo "正在清理项目..."
        ./gradlew clean
        echo "清理完成！"
        ;;
    debug)
        echo "正在构建Debug版本..."
        ./gradlew assembleDebug
        if [ $? -eq 0 ]; then
            echo "Debug APK 位置：app/build/outputs/apk/debug/app-debug.apk"
        else
            echo "构建失败！"
            exit 1
        fi
        ;;
    release)
        echo "正在构建Release版本（含混淆优化）..."
        ./gradlew assembleRelease
        if [ $? -eq 0 ]; then
            echo "Release APK 位置：app/build/outputs/apk/release/app-release-unsigned.apk"
        else
            echo "构建失败！"
            exit 1
        fi
        ;;
    install)
        echo "正在安装Debug版本到设备..."
        ./gradlew installDebug
        if [ $? -eq 0 ]; then
            echo "安装完成！"
        else
            echo "安装失败！请确保设备已连接"
            exit 1
        fi
        ;;
    run)
        echo "正在安装并运行游戏..."
        ./gradlew installDebug
        if [ $? -eq 0 ]; then
            echo "正在启动应用..."
            adb shell am start -n com.gamedev.match3/com.gamedev.match3.MainActivity
        else
            echo "安装失败！"
            exit 1
        fi
        ;;
    *)
        echo "未知命令：$1"
        exit 1
        ;;
esac
