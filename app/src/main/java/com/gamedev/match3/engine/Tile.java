package com.gamedev.match3.engine;

/**
 * 游戏瓷砖类 - 代表游戏板上的一个元素
 */
public class Tile {
    public static final int TYPE_EMPTY = 0;
    public static final int TYPE_RED = 1;
    public static final int TYPE_BLUE = 2;
    public static final int TYPE_YELLOW = 3;
    public static final int TYPE_GREEN = 4;
    public static final int TYPE_PURPLE = 5;
    public static final int TYPE_CYAN = 6;

    public int type;
    public int row;
    public int col;
    public boolean isMarked = false;
    public float targetX = 0;
    public float targetY = 0;

    public Tile(int type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
    }

    public boolean isEmpty() {
        return type == TYPE_EMPTY;
    }

    public boolean matches(Tile other) {
        if (other == null) return false;
        return this.type == other.type && !isEmpty();
    }
}
