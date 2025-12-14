# Match3 Game - 技术文档

## 一、架构概览

### 模块划分
```
┌─────────────────────────────────────────────────┐
│           MainActivity (Activity)               │
└────────────┬──────────────────────────────────┘
             │
┌────────────▼──────────────────────────────────┐
│         GameView (SurfaceView)                 │
│  高效的游戏渲染和输入处理                      │
└────────────┬──────────────────────────────────┘
             │
     ┌───────┴──────────┬──────────────┐
     │                  │              │
┌────▼─────┐    ┌──────▼──────┐  ┌───▼─────────┐
│GameEngine│    │EffectSystem │  │InputHandler │
│核心逻辑  │    │特效管理     │  │触摸输入     │
└──────────┘    └─────────────┘  └─────────────┘
     │                  │
     │          ┌───────┴─────────┐
     │          │                 │
┌────▼─────┐ ┌─▼─────────┐ ┌────▼──────┐
│Tile数据  │ │Animation  │ │Particle   │
│Matching  │ │运动动画   │ │粒子特效   │
└──────────┘ └───────────┘ └───────────┘
```

## 二、核心系统设计

### 1. 游戏引擎 (GameEngine)

**核心职责**：
- 棋盘数据管理
- 匹配检测算法
- 分数和关卡管理

**关键算法**：

```java
// 匹配检测（双向扫描）
public List<Tile> findMatches() {
    Set<Tile> matched = new HashSet<>();
    
    // 扫描所有行的水平匹配
    for (row : rows) {
        for (col : cols) {
            if (matches(current, right)) {
                // 记录连续匹配的瓷砖
                matched.add(tiles...);
            }
        }
    }
    
    // 扫描所有列的竖直匹配
    for (col : cols) {
        for (row : rows) {
            // 类似水平扫描...
        }
    }
    
    return new ArrayList<>(matched);
}

// 瓷砖填充（重力模拟）
public void fillBoard() {
    // 1. 下移非空瓷砖
    for (col : cols) {
        writePos = bottom;
        for (row = bottom; row >= 0; row--) {
            if (!isEmpty(tile)) {
                move(tile, writePos);
                writePos--;
            }
        }
    }
    
    // 2. 生成新瓷砖填充空位
    for (row : rows) {
        for (col : cols) {
            if (isEmpty(tile)) {
                generate(new Random type);
            }
        }
    }
}

// 链式反应处理
public void update() {
    removeMatches();           // 消除匹配瓷砖
    if (hasMatched) {
        fillBoard();           // 填充棋盘
        if (findMatches() > 0) {
            update();          // 递归检查链式反应
        }
    }
}
```

**性能优化**：
- 时间复杂度：O(n×m) 每帧扫描
- 空间复杂度：O(n×m) 棋盘大小固定
- 缓存匹配结果，避免重复计算

### 2. 渲染系统 (GameView)

**双线程架构**：
```
┌──────────────────────────────────────┐
│         Main Thread (UI)             │
├──────────────────────────────────────┤
│  - 处理触摸事件                       │
│  - 更新GameEngine数据                 │
│  - 发送绘制命令到SurfaceView          │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│      Render Thread (SurfaceView)     │
├──────────────────────────────────────┤
│  - 独立绘制循环 (60 FPS)              │
│  - Canvas绘制                         │
│  - 从不阻塞UI线程                     │
└──────────────────────────────────────┘
```

**绘制管道**：
```java
while (isRunning) {
    long startTime = System.currentTimeMillis();
    
    // 1. 更新阶段
    update(deltaTime);
    
    // 2. 绘制阶段
    Canvas canvas = surfaceHolder.lockCanvas();
    
    // 2.1 清空画布
    canvas.drawColor(bgColor);
    
    // 2.2 绘制游戏板
    for (tile : board) {
        drawTile(canvas, tile);
    }
    
    // 2.3 绘制动画
    for (anim : animations) {
        drawAnimationEffect(canvas, anim);
    }
    
    // 2.4 绘制粒子
    for (particle : particles) {
        drawParticle(canvas, particle);
    }
    
    // 2.5 绘制UI
    drawUI(canvas);
    
    surfaceHolder.unlockCanvasAndPost(canvas);
    
    // 3. 帧率控制
    long elapsed = System.currentTimeMillis() - startTime;
    Thread.sleep(Math.max(0, 16 - elapsed)); // 约60 FPS
}
```

**优化技巧**：
- 复用Paint对象，避免每帧创建
- Canvas save/restore减少状态切换
- 只绘制变化的区域（可选）
- 使用硬件加速（API 14+）

### 3. 特效系统

#### 3.1 粒子系统 (ParticleSystem)

**物理模型**：
```java
class Particle {
    // 位置和速度
    float x, y;          // 当前位置
    float vx, vy;        // 速度
    float ax, ay = 0.3;  // 加速度（重力）
    
    // 生命周期
    float lifespan;      // 0-1，从1递减到0
    float alpha;         // 透明度
    
    void update(float dt) {
        // 速度受加速度影响
        vx += ax * dt;
        vy += ay * dt;
        
        // 更新位置
        x += vx * dt;
        y += vy * dt;
        
        // 生命周期递减
        lifespan -= dt * 1.5f;
        alpha = max(0, lifespan);
        
        // 逐帧缩小
        size *= 0.98f;
    }
}
```

**爆炸效果**：
```java
void createExplosion(float x, float y, int color, int count) {
    for (int i = 0; i < count; i++) {
        // 随机方向
        double angle = random() * PI * 2;
        
        // 随机速度
        float speed = 150 + random() * 150;
        float vx = cos(angle) * speed;
        float vy = sin(angle) * speed;
        
        // 创建粒子
        Particle p = new Particle(x, y, vx, vy, color);
        particles.add(p);
    }
}
```

**内存管理**：
- 硬上限：500个活跃粒子
- 超过限制后不再生成新粒子
- 每帧清除已死亡的粒子
- 避免内存泄漏

#### 3.2 动画系统 (AnimationManager)

**三种基本动画**：

```java
// 1. 移动动画 (type=0)
startPos → targetPos，使用缓动函数
progress = easeOutQuad(t)
current = start + (target - start) * progress

// 2. 消除动画 (type=1)
缩放消失 + 旋转
scale = 1 - easeInQuad(progress)  // 缩小
rotation = progress * 360         // 旋转

// 3. 跳跃动画 (type=2)
弧形轨迹下落
progress = easeOutElastic(t)  // 回弹效果
```

**缓动函数库**：
```java
// 线性
easeLinear(t) = t

// 二次方缓动
easeInQuad(t) = t²
easeOutQuad(t) = 1 - (1-t)²
easeInOutQuad(t) = t < 0.5 ? 2t² : 1 - 2(1-t)²

// 回弹效果
easeOutElastic(t) = 2^(-10t) * sin((t-0.75)*c5) + 1
```

## 三、性能优化方案

### 1. 内存优化

**对象池模式**：
```java
public abstract class ObjectPool<T> {
    private List<T> available = new ArrayList<>();
    private List<T> inUse = new ArrayList<>();
    
    public T obtain() {
        if (available.size() > 0) {
            T obj = available.remove(last);
            inUse.add(obj);
            return obj;
        }
        return create();  // 按需创建
    }
    
    public void release(T obj) {
        if (inUse.remove(obj)) {
            reset(obj);
            available.add(obj);
        }
    }
}

// 使用示例
TilePool pool = new TilePool(100);
Tile tile = pool.obtain();
// ... 使用瓷砖 ...
pool.release(tile);  // 自动复用
```

**垃圾回收优化**：
- 避免在update/draw中创建新对象
- 复用集合和数组
- 及时清理不用的列表
- 使用对象池管理频繁创建的对象

### 2. CPU优化

**算法优化**：
```
原始匹配检测：O(n²) - 每个瓷砖检查其邻接关系
优化后：O(n) - 按行列扫描，一遍过

原始填充：O(n² log n) - 多次排序
优化后：O(n) - 单遍扫描下移
```

**帧率控制**：
```java
// 目标60 FPS，每帧16ms
long target = 16;
long elapsed = System.currentTimeMillis() - frameStart;
long sleep = Math.max(0, target - elapsed);
Thread.sleep(sleep);
```

### 3. 渲染优化

**批量绘制**：
- 按类型分组绘制（棋盘、动画、粒子、UI）
- 减少Canvas save/restore调用
- 复用Paint对象

**硬件加速**：
```xml
<!-- AndroidManifest.xml -->
<application android:hardwareAccelerated="true">
    <activity android:hardwareAccelerated="true" />
</application>
```

### 4. 网络和存储（可选）

**数据持久化**：
```java
// 保存游戏进度
SharedPreferences prefs = getSharedPreferences("game", MODE_PRIVATE);
prefs.edit()
    .putInt("score", score)
    .putInt("level", level)
    .apply();
```

## 四、扩展指南

### 添加新的瓷砖类型

```java
// 1. 在Tile.java中添加常量
public static final int TYPE_BOMB = 7;
public static final int TYPE_LIGHTNING = 8;

// 2. 在GameView.java中添加颜色
private int[] tileColors = { ..., 0xFFXXXXXX, ... };

// 3. 添加特殊消除效果
void handleSpecialTile(Tile tile) {
    switch (tile.type) {
        case TYPE_BOMB:
            // 消除周围8个瓷砖
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    // ... 清除 ...
                }
            }
            break;
    }
}
```

### 添加游戏模式

```java
public enum GameMode {
    ENDLESS,        // 无限模式
    TIMED,         // 计时模式
    TARGET,        // 目标模式
    PUZZLE         // 关卡模式
}

class ModeController {
    private GameMode mode;
    
    void update(float dt) {
        switch (mode) {
            case TIMED:
                timeRemaining -= dt;
                if (timeRemaining <= 0) {
                    gameOver();
                }
                break;
        }
    }
}
```

## 五、调试技巧

### 性能分析

```java
// 监控FPS
long frameStartTime = System.currentTimeMillis();
// ... 渲染代码 ...
long frameDuration = System.currentTimeMillis() - frameStartTime;
float fps = 1000.0f / frameDuration;

// 监控内存
Runtime runtime = Runtime.getRuntime();
long totalMem = runtime.totalMemory();
long freeMem = runtime.freeMemory();
long usedMem = totalMem - freeMem;
```

### 常见问题

**卡顿**：
- 检查粒子数量是否超过限制
- 确保update()中没有重型计算
- 使用Android Profiler分析

**内存泄漏**：
- 检查Thread是否正确关闭
- 检查Listener是否反注册
- 检查对象池是否有循环引用

## 六、发布清单

- [ ] 测试所有设备（最低API 21）
- [ ] 测试不同屏幕分辨率
- [ ] 测试长时间运行的内存泄漏
- [ ] ProGuard混淆和优化
- [ ] 签名密钥生成
- [ ] 隐私政策准备
- [ ] 屏幕截图和描述准备
- [ ] 版本号和更新说明

---

**文档版本**：1.0  
**最后更新**：2025年12月14日
