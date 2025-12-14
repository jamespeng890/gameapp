# Proguard配置 - 用于代码优化和混淆

# 保留游戏引擎类
-keep class com.gamedev.match3.engine.** { *; }
-keep class com.gamedev.match3.effect.** { *; }
-keep class com.gamedev.match3.view.** { *; }

# 保留MainActivity
-keep class com.gamedev.match3.MainActivity { *; }

# 保留native方法
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 优化设置
-optimizationpasses 5
-verbose

# 移除日志
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# 保持构造器
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
