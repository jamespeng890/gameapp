package com.gamedev.match3.engine;

/**
 * 游戏性能监控 - 跟踪CPU和内存使用
 */
public class PerformanceMonitor {
    private long lastFrameTime = System.currentTimeMillis();
    private float averageFps = 60;
    private int frameCount = 0;
    private long totalMemory = 0;
    private long usedMemory = 0;

    private static final int FPS_SAMPLE_SIZE = 60; // 统计60帧的平均FPS

    /**
     * 更新帧统计
     */
    public void updateFrame() {
        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastFrameTime;
        lastFrameTime = currentTime;

        frameCount++;

        // 每60帧计算一次平均FPS
        if (frameCount % FPS_SAMPLE_SIZE == 0) {
            averageFps = FPS_SAMPLE_SIZE * 1000.0f / deltaTime;
            updateMemoryStats();
        }
    }

    /**
     * 更新内存统计
     */
    private void updateMemoryStats() {
        Runtime runtime = Runtime.getRuntime();
        totalMemory = runtime.totalMemory();
        usedMemory = totalMemory - runtime.freeMemory();
    }

    /**
     * 获取平均FPS
     */
    public float getAverageFps() {
        return averageFps;
    }

    /**
     * 获取使用的内存（MB）
     */
    public long getUsedMemoryMB() {
        return usedMemory / (1024 * 1024);
    }

    /**
     * 获取总内存（MB）
     */
    public long getTotalMemoryMB() {
        return totalMemory / (1024 * 1024);
    }

    /**
     * 获取内存使用百分比
     */
    public float getMemoryUsagePercent() {
        if (totalMemory == 0) return 0;
        return (usedMemory * 100.0f) / totalMemory;
    }

    /**
     * 强制垃圾回收（谨慎使用）
     */
    public static void forceGarbageCollection() {
        System.gc();
        System.runFinalization();
    }
}
