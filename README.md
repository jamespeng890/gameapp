# Match3 Game - 消消乐游戏

一个高性能的Android消消乐游戏应用，采用Java开发，具有炫酷特效和优化的内存管理。

## 功能特性

### 核心游戏玩法
- **棋盘系统**：6x8的游戏棋盘
- **消除机制**：相邻的3个或更多相同颜色的瓷砖可以消除
- **链式反应**：消除后瓷砖下落会自动触发新的匹配
- **计分系统**：3个50分，4个200分，5个500分，6个1000分，以此类推
- **关卡系统**：每匹配5次自动升级关卡

### 特效系统
- **粒子特效**：消除时产生炫彩粒子爆炸
- **动画系统**：
  - 瓷砖消除旋转缩放动画
  - 瓷砖移动平滑过渡
  - 瓷砖下落跳跃效果
- **浮动文字**：显示获得的分数
- **音效系统**：
  - 匹配音效
  - 消除音效
  - 关卡升级音效

### 性能优化
- **SurfaceView渲染**：高效的2D绘制，避免UI线程阻塞
- **对象池**：复用游戏对象减少GC压力
- **粒子限制**：最多500个粒子，防止内存溢出
- **动画缓冲**：Easing函数优化动画流畅性
- **内存监控**：实时跟踪内存使用情况
- **代码混淆**：ProGuard优化和混淆

## 项目结构

```
gameapp/
├── app/
│   ├── src/main/
│   │   ├── java/com/gamedev/match3/
│   │   │   ├── MainActivity.java           # 主Activity
│   │   │   ├── engine/
│   │   │   │   ├── Tile.java              # 瓷砖类
│   │   │   │   ├── GameEngine.java        # 游戏引擎
│   │   │   │   ├── OptimizedGameEngine.java # 优化的游戏引擎
│   │   │   │   ├── ObjectPool.java        # 对象池
│   │   │   │   └── PerformanceMonitor.java # 性能监控
│   │   │   ├── view/
│   │   │   │   └── GameView.java          # 游戏视图
│   │   │   └── effect/
│   │   │       ├── Particle.java          # 粒子
│   │   │       ├── ParticleSystem.java    # 粒子系统
│   │   │       ├── TileAnimation.java     # 瓷砖动画
│   │   │       ├── AnimationManager.java  # 动画管理器
│   │   │       ├── FloatingTextSystem.java # 浮动文字系统
│   │   │       ├── Easing.java            # 缓动函数
│   │   │       └── SoundManager.java      # 音效管理
│   │   ├── res/
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── styles.xml
│   │   │   └── raw/                       # 放置音频文件
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

## 核心类说明

### GameEngine
游戏逻辑核心，处理：
- 棋盘初始化和管理
- 瓷砖交换和匹配检测
- 分数计算
- 关卡升级

### GameView
使用SurfaceView的高效渲染：
- 60 FPS稳定帧率
- 触摸输入处理
- 多图层渲染（板、动画、粒子、UI）

### 特效系统
- **ParticleSystem**：管理粒子特效，最多500个
- **AnimationManager**：管理瓷砖动画（移动、消除、跳跃）
- **SoundManager**：低延迟音效播放（SoundPool）
- **FloatingTextSystem**：浮动分数文字

### 性能优化
- **ObjectPool**：通用对象池基类
- **PerformanceMonitor**：实时性能监控
- **Easing**：优化的动画插值函数

## 游戏玩法

1. **开始游戏**：应用启动后直接进入游戏
2. **滑动瓷砖**：
   - 在棋盘内点击瓷砖并向上下左右滑动以交换相邻瓷砖
   - 必须形成3个或更多相同颜色的直线才能消除
3. **获得分数**：
   - 消除时获得分数
   - 消除块数越多分数越高
4. **升级关卡**：每消除5次自动升级关卡

## 性能指标

- **帧率**：目标60 FPS（通过SurfaceView和优化的渲染实现）
- **内存使用**：约30-50MB（根据设备）
- **CPU占用**：低至5-10%（优化的渲染循环）
- **粒子限制**：最多500个活跃粒子
- **动画缓存**：所有动画使用缓动函数优化

## 编译和运行

### 前提条件
- Android Studio 4.0+
- Android SDK 21+ (最低API级别)
- JDK 11+

### 构建步骤

1. **导入项目**
```bash
cd gameapp
```

2. **构建Debug版本**
```bash
./gradlew assembleDebug
```

3. **构建Release版本（含ProGuard优化）**
```bash
./gradlew assembleRelease
```

4. **在Android Studio中运行**
- 打开项目
- 选择设备或模拟器
- 点击运行按钮

## 优化建议

### 内存优化
- 已启用ProGuard代码混淆和优化
- 使用对象池减少垃圾回收
- 粒子系统有硬上限（500个）

### 渲染优化
- 使用SurfaceView而非View
- 渲染线程独立，不阻塞UI
- 采用批量绘制减少drawCall

### CPU优化
- 60 FPS帧率上限
- 优化的碰撞检测
- 高效的匹配查找算法

## 扩展功能建议

1. **游戏模式**：
   - 计时模式
   - 目标模式
   - 无限模式

2. **社交功能**：
   - 排行榜
   - 成就系统
   - 多人对战

3. **视觉增强**：
   - 更多瓷砖类型
   - 自定义主题
   - 背景动画

4. **道具系统**：
   - 炸弹（消除周围瓷砖）
   - 闪电（消除一整行）
   - 魔法棒（消除指定颜色）

## 许可证

MIT License

## 联系方式

如有问题，请提交Issue或PR。

---

**最后更新**: 2025年12月
**版本**: 1.0
**目标设备**: Android 5.0+ (API 21+)
