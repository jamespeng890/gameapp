package com.gamedev.match3.effect;

/**
 * 瓷砖动画效果 - 处理瓷砖的消除、移动等动画
 */
public class TileAnimation {
    public int tileRow;
    public int tileCol;
    
    public float startX, startY;
    public float targetX, targetY;
    public float currentX, currentY;
    
    public float duration = 0.3f; // 动画时长
    public float elapsed = 0;
    public boolean isActive = true;
    public int animationType; // 0=移动，1=消除，2=缩放
    
    public float scale = 1.0f;
    public float alpha = 1.0f;
    public float rotation = 0;

    public TileAnimation(int row, int col, float startX, float startY, 
                        float targetX, float targetY, int type) {
        this.tileRow = row;
        this.tileCol = col;
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.currentX = startX;
        this.currentY = startY;
        this.animationType = type;
    }

    /**
     * 更新动画
     */
    public void update(float deltaTime) {
        elapsed += deltaTime;
        float progress = Math.min(elapsed / duration, 1.0f);

        // 使用缓动函数计算进度
        float eased = Easing.easeOutQuad(progress);

        // 根据动画类型设置效果
        switch (animationType) {
            case 0: // 移动
                currentX = startX + (targetX - startX) * eased;
                currentY = startY + (targetY - startY) * eased;
                break;

            case 1: // 消除（缩放消失）
                float dismissEased = Easing.easeInQuad(progress);
                scale = 1.0f - dismissEased;
                alpha = 1.0f - dismissEased;
                rotation = progress * 360;
                break;

            case 2: // 跳跃
                float jumpEased = Easing.easeOutElastic(progress);
                currentX = startX + (targetX - startX) * jumpEased;
                currentY = startY + (targetY - startY) * Easing.easeOutQuad(progress);
                break;
        }

        if (progress >= 1.0f) {
            isActive = false;
        }
    }

    /**
     * 检查动画是否完成
     */
    public boolean isComplete() {
        return !isActive;
    }
}
