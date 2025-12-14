package com.gamedev.match3.effect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 粒子系统 - 管理所有粒子效果
 */
public class ParticleSystem {
    private List<Particle> particles;
    private static final int MAX_PARTICLES = 500; // 防止过多粒子

    public ParticleSystem() {
        this.particles = new ArrayList<>(MAX_PARTICLES);
    }

    /**
     * 在指定位置创建爆炸效果
     */
    public void createExplosion(float x, float y, int color, int count) {
        if (particles.size() >= MAX_PARTICLES) return;

        for (int i = 0; i < count; i++) {
            double angle = Math.random() * Math.PI * 2;
            float speed = 150 + (float) (Math.random() * 150);
            float vx = (float) (Math.cos(angle) * speed);
            float vy = (float) (Math.sin(angle) * speed);

            Particle p = new Particle(x, y, vx, vy, color, 8 + (float) Math.random() * 4);
            particles.add(p);
        }
    }

    /**
     * 创建击中特效
     */
    public void createHitEffect(float x, float y, int color) {
        createExplosion(x, y, color, 8);
    }

    /**
     * 更新所有粒子
     */
    public void update(float deltaTime) {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();
            p.update(deltaTime);
            if (p.isDead()) {
                iterator.remove();
            }
        }
    }

    /**
     * 获取所有粒子
     */
    public List<Particle> getParticles() {
        return particles;
    }

    /**
     * 清空所有粒子
     */
    public void clear() {
        particles.clear();
    }

    /**
     * 获取粒子数量
     */
    public int getParticleCount() {
        return particles.size();
    }
}
