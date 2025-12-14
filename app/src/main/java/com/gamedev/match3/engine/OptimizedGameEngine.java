package com.gamedev.match3.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 优化的游戏引擎 - 包含性能优化和内存管理
 */
public class OptimizedGameEngine extends GameEngine {
    private PerformanceMonitor performanceMonitor;
    private TilePool tilePool;
    private static final int TILE_POOL_SIZE = 100;

    public OptimizedGameEngine() {
        super();
        this.performanceMonitor = new PerformanceMonitor();
        this.tilePool = new TilePool(TILE_POOL_SIZE);
    }

    /**
     * 获取性能监控器
     */
    public PerformanceMonitor getPerformanceMonitor() {
        return performanceMonitor;
    }

    /**
     * 更新帧统计
     */
    public void updateFrameStats() {
        performanceMonitor.updateFrame();
    }

    /**
     * 获取性能统计字符串
     */
    public String getPerformanceStats() {
        return String.format("FPS: %.1f | Mem: %dMB / %dMB (%.1f%%)",
                performanceMonitor.getAverageFps(),
                performanceMonitor.getUsedMemoryMB(),
                performanceMonitor.getTotalMemoryMB(),
                performanceMonitor.getMemoryUsagePercent());
    }

    /**
     * 瓷砖对象池
     */
    public static class TilePool extends ObjectPool<Tile> {
        public TilePool(int initialSize) {
            super(initialSize);
        }

        @Override
        protected Tile create() {
            return new Tile(Tile.TYPE_EMPTY, 0, 0);
        }

        @Override
        protected void reset(Tile obj) {
            obj.type = Tile.TYPE_EMPTY;
            obj.row = 0;
            obj.col = 0;
            obj.isMarked = false;
        }
    }
}
