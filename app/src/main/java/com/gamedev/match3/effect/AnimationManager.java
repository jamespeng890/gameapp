package com.gamedev.match3.effect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 动画管理系统 - 管理所有游戏动画
 */
public class AnimationManager {
    private List<TileAnimation> tileAnimations = new ArrayList<>();

    /**
     * 添加瓷砖移动动画
     */
    public void addMoveAnimation(int row, int col, float startX, float startY,
                                float targetX, float targetY) {
        TileAnimation anim = new TileAnimation(row, col, startX, startY, 
                                              targetX, targetY, 0);
        tileAnimations.add(anim);
    }

    /**
     * 添加消除动画
     */
    public void addDismissAnimation(int row, int col, float x, float y) {
        TileAnimation anim = new TileAnimation(row, col, x, y, x, y, 1);
        anim.duration = 0.4f;
        tileAnimations.add(anim);
    }

    /**
     * 添加跳跃动画
     */
    public void addJumpAnimation(int row, int col, float startX, float startY,
                                float targetX, float targetY) {
        TileAnimation anim = new TileAnimation(row, col, startX, startY,
                                              targetX, targetY, 2);
        anim.duration = 0.5f;
        tileAnimations.add(anim);
    }

    /**
     * 更新所有动画
     */
    public void update(float deltaTime) {
        Iterator<TileAnimation> iterator = tileAnimations.iterator();
        while (iterator.hasNext()) {
            TileAnimation anim = iterator.next();
            anim.update(deltaTime);
            if (anim.isComplete()) {
                iterator.remove();
            }
        }
    }

    /**
     * 获取动画列表
     */
    public List<TileAnimation> getAnimations() {
        return tileAnimations;
    }

    /**
     * 清空所有动画
     */
    public void clear() {
        tileAnimations.clear();
    }

    /**
     * 获取动画数量
     */
    public int getAnimationCount() {
        return tileAnimations.size();
    }
}
