package com.gamedev.match3.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gamedev.match3.engine.GameEngine;
import com.gamedev.match3.engine.Tile;
import com.gamedev.match3.effect.AnimationManager;
import com.gamedev.match3.effect.FloatingTextSystem;
import com.gamedev.match3.effect.Particle;
import com.gamedev.match3.effect.ParticleSystem;
import com.gamedev.match3.effect.SoundManager;
import com.gamedev.match3.effect.TileAnimation;

import java.util.List;

/**
 * 游戏视图 - 使用SurfaceView实现高效绘制
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder surfaceHolder;
    private com.gamedev.match3.engine.OptimizedGameEngine gameEngine;
    private ParticleSystem particleSystem;
    private AnimationManager animationManager;
    private FloatingTextSystem floatingTextSystem;
    private SoundManager soundManager;
    private Thread renderThread;
    private boolean isRunning = false;

    // 渲染参数
    private int tileSize = 60;
    private int boardStartX = 20;
    private int boardStartY = 150;

    // 触摸处理
    private int selectedRow = -1;
    private int selectedCol = -1;
    private float lastTouchX = 0;
    private float lastTouchY = 0;

    // 画笔对象（复用以减少GC）
    private Paint tilePaint;
    private Paint textPaint;
    private Paint borderPaint;
    private Paint particlePaint;

    // 颜色数组
    private int[] tileColors = {
            0xFF000000, // 空
            0xFFFF5252, // 红
            0xFF2196F3, // 蓝
            0xFFFFC107, // 黄
            0xFF4CAF50, // 绿
            0xFF9C27B0, // 紫
            0xFF00BCD4  // 青
    };

    private float frameTime = 0;
    private int frameCount = 0;
    private long lastFrameTime = 0;

    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        gameEngine = new com.gamedev.match3.engine.OptimizedGameEngine();
        particleSystem = new ParticleSystem();
        animationManager = new AnimationManager();
        floatingTextSystem = new FloatingTextSystem();
        soundManager = new SoundManager(context);
        soundManager.loadSounds(context);

        initPaints();
        setFocusable(true);
    }

    /**
     * 初始化画笔对象（避免每帧创建新对象）
     */
    private void initPaints() {
        tilePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tilePaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(32);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(0xFF666666);
        borderPaint.setStrokeWidth(2);
        borderPaint.setStyle(Paint.Style.STROKE);

        particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 根据屏幕大小调整瓷砖大小
        calculateTileSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
        try {
            if (renderThread != null) {
                renderThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastUpdateTime = System.currentTimeMillis();

        while (isRunning) {
            long currentTime = System.currentTimeMillis();
            float deltaTime = Math.min((currentTime - lastUpdateTime) / 1000.0f, 0.016f); // 最多16ms
            lastUpdateTime = currentTime;

            // 更新游戏逻辑
            update(deltaTime);

            // 绘制
            draw();

            // 帧率限制
            try {
                Thread.sleep(16); // 目标60fps
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新游戏状态
     */
    private void update(float deltaTime) {
        if (!gameEngine.isGameRunning()) return;

        // 更新性能监控
        gameEngine.updateFrameStats();

        // 更新所有特效系统
        particleSystem.update(deltaTime);
        animationManager.update(deltaTime);
        floatingTextSystem.update(deltaTime);

        // 执行游戏逻辑
        List<Tile> matches = gameEngine.findMatches();
        if (!matches.isEmpty()) {
            // 添加消除动画
            for (Tile tile : matches) {
                float x = boardStartX + tile.col * tileSize + tileSize / 2;
                float y = boardStartY + tile.row * tileSize + tileSize / 2;
                animationManager.addDismissAnimation(tile.row, tile.col, x, y);
                particleSystem.createExplosion(x, y, tileColors[tile.type], 12);
                
                // 添加浮动分数文字
                floatingTextSystem.addScore(x, y, 50);
            }

            // 播放音效
            soundManager.playClearSound();
        }

        gameEngine.update();

        frameTime += deltaTime;
        frameCount++;
    }

    /**
     * 绘制游戏画面
     */
    private void draw() {
        Canvas canvas = null;
        try {
            canvas = surfaceHolder.lockCanvas();
            if (canvas == null) return;

            // 清除画布
            canvas.drawColor(0xFFE8E8F0, PorterDuff.Mode.SRC);

            // 绘制游戏板
            drawBoard(canvas);

            // 绘制瓷砖动画
            drawTileAnimations(canvas);

            // 绘制粒子效果
            drawParticles(canvas);

            // 绘制浮动文字
            floatingTextSystem.draw(canvas);

            // 绘制UI信息
            drawUI(canvas);

        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * 绘制游戏板
     */
    private void drawBoard(Canvas canvas) {
        int height = gameEngine.getBoardHeight();
        int width = gameEngine.getBoardWidth();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Tile tile = gameEngine.getTile(row, col);
                if (tile != null) {
                    drawTile(canvas, tile);
                }
            }
        }

        // 绘制选中框
        if (selectedRow >= 0 && selectedCol >= 0) {
            int x = boardStartX + selectedCol * tileSize;
            int y = boardStartY + selectedRow * tileSize;
            canvas.drawRect(x, y, x + tileSize, y + tileSize, borderPaint);
        }
    }

    /**
     * 绘制单个瓷砖
     */
    private void drawTile(Canvas canvas, Tile tile) {
        int x = boardStartX + tile.col * tileSize;
        int y = boardStartY + tile.row * tileSize;

        if (tile.isEmpty()) {
            // 绘制空瓷砖边框
            canvas.drawRect(x, y, x + tileSize, y + tileSize, borderPaint);
            return;
        }

        // 检查是否有动画
        for (TileAnimation anim : animationManager.getAnimations()) {
            if (anim.tileRow == tile.row && anim.tileCol == tile.col) {
                // 使用动画位置绘制
                x = (int) anim.currentX;
                y = (int) anim.currentY;
                break;
            }
        }

        // 绘制彩色瓷砖
        tilePaint.setColor(tileColors[tile.type]);
        tilePaint.setAlpha(255);
        canvas.drawRoundRect(x + 2, y + 2, x + tileSize - 2, y + tileSize - 2,
                8, 8, tilePaint);

        // 绘制阴影和光影效果
        borderPaint.setColor(0x33000000);
        canvas.drawRoundRect(x + 2, y + 2, x + tileSize - 2, y + tileSize - 2,
                8, 8, borderPaint);

        // 绘制高光
        Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightPaint.setColor(0x33FFFFFF);
        canvas.drawRoundRect(x + 4, y + 4, x + tileSize - 8, y + 8,
                4, 4, highlightPaint);
    }

    /**
     * 绘制瓷砖动画
     */
    private void drawTileAnimations(Canvas canvas) {
        for (TileAnimation anim : animationManager.getAnimations()) {
            if (anim.animationType == 1) { // 消除动画
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setAlpha((int) (anim.alpha * 255));

                float centerX = anim.currentX;
                float centerY = anim.currentY;

                paint.setColor(tileColors[gameEngine.getTile(anim.tileRow, anim.tileCol).type]);

                // 绘制旋转缩放效果
                canvas.save();
                canvas.rotate(anim.rotation, centerX, centerY);
                canvas.drawCircle(centerX, centerY,
                        (tileSize / 2) * anim.scale, paint);
                canvas.restore();
            }
        }
    }

    /**
     * 绘制粒子效果
     */
    private void drawParticles(Canvas canvas) {
        List<Particle> particles = particleSystem.getParticles();
        for (Particle p : particles) {
            particlePaint.setColor(p.color);
            particlePaint.setAlpha((int) (p.alpha * 255));

            // 在游戏坐标系中绘制粒子
            float screenX = boardStartX + p.x;
            float screenY = boardStartY + p.y;

            canvas.drawCircle(screenX, screenY, p.size, particlePaint);
        }
    }

    /**
     * 绘制UI信息
     */
    private void drawUI(Canvas canvas) {
        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(32);

        // 绘制分数和关卡
        canvas.drawText("Score: " + gameEngine.getScore(), 20, 50, textPaint);
        canvas.drawText("Level: " + gameEngine.getLevel(), 20, 100, textPaint);

        // 绘制性能信息
        textPaint.setTextSize(16);
        textPaint.setColor(0xFF666666);
        String perfStats = gameEngine.getPerformanceStats();
        canvas.drawText(perfStats, 20, getHeight() - 40, textPaint);

        // 绘制活跃对象统计
        textPaint.setTextSize(14);
        canvas.drawText("Particles: " + particleSystem.getParticleCount() + 
                        " | Animations: " + animationManager.getAnimationCount(),
                        20, getHeight() - 15, textPaint);

        // 游戏结束
        if (!gameEngine.isGameRunning()) {
            drawGameOver(canvas);
        }
    }

    /**
     * 绘制游戏结束界面
     */
    private void drawGameOver(Canvas canvas) {
        // 半透明黑色覆盖
        Paint overlayPaint = new Paint();
        overlayPaint.setColor(0x80000000);
        canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);

        // 绘制游戏结束文字
        textPaint.setColor(0xFFFFFFFF);
        textPaint.setTextSize(48);
        String gameOverText = "Game Over!";
        float x = (getWidth() - textPaint.measureText(gameOverText)) / 2;
        canvas.drawText(gameOverText, x, getHeight() / 2 - 50, textPaint);

        textPaint.setTextSize(32);
        String scoreText = "Score: " + gameEngine.getScore();
        x = (getWidth() - textPaint.measureText(scoreText)) / 2;
        canvas.drawText(scoreText, x, getHeight() / 2 + 50, textPaint);
    }

    /**
     * 计算瓷砖大小
     */
    private void calculateTileSize(int width, int height) {
        int maxTileWidth = (width - 40) / gameEngine.getBoardWidth();
        int maxTileHeight = (height - 200) / gameEngine.getBoardHeight();
        tileSize = Math.min(maxTileWidth, maxTileHeight);
        boardStartX = (width - tileSize * gameEngine.getBoardWidth()) / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = x;
                lastTouchY = y;
                handleTouchDown(x, y);
                break;

            case MotionEvent.ACTION_UP:
                handleTouchUp(x, y);
                break;
        }

        return true;
    }

    /**
     * 处理触摸按下
     */
    private void handleTouchDown(float x, float y) {
        // 检查触摸是否在游戏板内
        if (x >= boardStartX && y >= boardStartY) {
            int col = (int) ((x - boardStartX) / tileSize);
            int row = (int) ((y - boardStartY) / tileSize);

            if (col >= 0 && col < gameEngine.getBoardWidth() &&
                row >= 0 && row < gameEngine.getBoardHeight()) {
                selectedRow = row;
                selectedCol = col;
            }
        }
    }

    /**
     * 执行交换
     */
    private void handleTouchUp(float x, float y) {
        if (selectedRow < 0 || selectedCol < 0) return;

        // 计算滑动方向
        float dx = x - lastTouchX;
        float dy = y - lastTouchY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < 20) {
            // 点击太小，忽略
            selectedRow = -1;
            selectedCol = -1;
            return;
        }

        // 确定交换目标
        int targetRow = selectedRow;
        int targetCol = selectedCol;

        if (Math.abs(dx) > Math.abs(dy)) {
            // 水平滑动
            targetCol += (dx > 0) ? 1 : -1;
        } else {
            // 竖直滑动
            targetRow += (dy > 0) ? 1 : -1;
        }

        // 执行交换
        if (gameEngine.swapTiles(selectedRow, selectedCol, targetRow, targetCol)) {
            // 播放匹配音效
            soundManager.playMatchSound();

            // 创建瓷砖移动动画
            float startX = boardStartX + selectedCol * tileSize + tileSize / 2;
            float startY = boardStartY + selectedRow * tileSize + tileSize / 2;
            float targetX = boardStartX + targetCol * tileSize + tileSize / 2;
            float targetY = boardStartY + targetRow * tileSize + tileSize / 2;

            animationManager.addMoveAnimation(selectedRow, selectedCol, startX, startY, targetX, targetY);
            animationManager.addMoveAnimation(targetRow, targetCol, targetX, targetY, startX, startY);

            // 创建击中特效
            Tile tile = gameEngine.getTile(selectedRow, selectedCol);
            if (tile != null) {
                float px = boardStartX + selectedCol * tileSize + tileSize / 2;
                float py = boardStartY + selectedRow * tileSize + tileSize / 2;
                particleSystem.createHitEffect(px, py, tileColors[tile.type]);
            }
        }

        selectedRow = -1;
        selectedCol = -1;
    }
}
