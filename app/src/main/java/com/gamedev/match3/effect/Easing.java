package com.gamedev.match3.effect;

/**
 * 缓动动画类 - 提供平滑的动画插值
 */
public class Easing {
    
    /**
     * 线性插值
     */
    public static float linear(float t) {
        return t;
    }

    /**
     * 缓入缓出（二次方）
     */
    public static float easeInOutQuad(float t) {
        if (t < 0.5f) {
            return 2 * t * t;
        } else {
            return -1 + (4 - 2 * t) * t;
        }
    }

    /**
     * 缓出（二次方）
     */
    public static float easeOutQuad(float t) {
        return 1 - (1 - t) * (1 - t);
    }

    /**
     * 缓入（二次方）
     */
    public static float easeInQuad(float t) {
        return t * t;
    }

    /**
     * 回弹效果
     */
    public static float easeOutElastic(float t) {
        if (t == 0) return 0;
        if (t == 1) return 1;
        
        float c5 = (2 * 3.14159f) / 4.5f;
        return (float) (Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75f) * c5) + 1);
    }

    /**
     * 弧形轨迹
     */
    public static float easeInOutCubic(float t) {
        if (t < 0.5f) {
            return 4 * t * t * t;
        } else {
            float f = 2 * t - 2;
            return 0.5f * f * f * f + 1;
        }
    }
}
