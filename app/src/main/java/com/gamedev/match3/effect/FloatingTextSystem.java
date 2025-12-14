package com.gamedev.match3.effect;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 浮动文字特效 - 显示分数获取
 */
public class FloatingText {
    public float x, y;
    public String text;
    public float alpha = 1.0f;
    public float lifespan = 1.0f;
    public int color;

    public FloatingText(float x, float y, String text, int color) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
    }

    public void update(float deltaTime) {
        y -= 50 * deltaTime; // 向上移动
        lifespan -= deltaTime;
        alpha = Math.max(0, lifespan);
    }

    public boolean isDead() {
        return lifespan <= 0;
    }
}

/**
 * 浮动文字系统
 */
public class FloatingTextSystem {
    private List<FloatingText> texts = new ArrayList<>();
    private Paint paint;

    public FloatingTextSystem() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(24);
    }

    public void addScore(float x, float y, int score) {
        FloatingText text = new FloatingText(x, y, "+" + score, 0xFFFFD700);
        texts.add(text);
    }

    public void update(float deltaTime) {
        Iterator<FloatingText> iterator = texts.iterator();
        while (iterator.hasNext()) {
            FloatingText text = iterator.next();
            text.update(deltaTime);
            if (text.isDead()) {
                iterator.remove();
            }
        }
    }

    public void draw(Canvas canvas) {
        for (FloatingText text : texts) {
            paint.setColor(text.color);
            paint.setAlpha((int) (text.alpha * 255));
            canvas.drawText(text.text, text.x, text.y, paint);
        }
    }

    public List<FloatingText> getTexts() {
        return texts;
    }

    public void clear() {
        texts.clear();
    }
}
