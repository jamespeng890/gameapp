package com.gamedev.match3.effect;

/**
 * 粒子类 - 用于实现消除特效
 */
public class Particle {
    public float x, y;
    public float vx, vy; // 速度
    public float ax, ay; // 加速度
    public float lifespan; // 剩余生命周期（0-1）
    public int color;
    public float size;
    public float alpha; // 透明度

    public Particle(float x, float y, float vx, float vy, int color, float size) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.color = color;
        this.size = size;
        this.lifespan = 1.0f;
        this.alpha = 1.0f;
        this.ax = 0;
        this.ay = 0.3f; // 重力
    }

    /**
     * 更新粒子状态
     */
    public void update(float deltaTime) {
        // 更新速度
        vx += ax * deltaTime;
        vy += ay * deltaTime;

        // 更新位置
        x += vx * deltaTime;
        y += vy * deltaTime;

        // 减少生命周期
        lifespan -= deltaTime * 1.5f;
        alpha = Math.max(0, lifespan);

        // 根据生命周期缩小
        size *= 0.98f;
    }

    /**
     * 检查粒子是否已死亡
     */
    public boolean isDead() {
        return lifespan <= 0;
    }
}
