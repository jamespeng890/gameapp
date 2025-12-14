package com.gamedev.match3.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 游戏引擎 - 处理所有游戏逻辑
 */
public class GameEngine {
    private static final int BOARD_WIDTH = 6;
    private static final int BOARD_HEIGHT = 8;
    private static final int MIN_MATCH = 3;

    private Tile[][] board;
    private int score = 0;
    private int level = 1;
    private int moves = 0;
    private int matchCount = 0;
    private boolean isGameRunning = true;
    private Random random;

    private List<Tile> matchedTiles = new ArrayList<>();
    private Set<Integer> changedCells = new HashSet<>();

    public GameEngine() {
        this.random = new Random();
        this.board = new Tile[BOARD_HEIGHT][BOARD_WIDTH];
        initializeBoard();
    }

    /**
     * 初始化游戏板
     */
    private void initializeBoard() {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                int type = random.nextInt(6) + 1; // 类型1-6
                board[row][col] = new Tile(type, row, col);
            }
        }
        // 清除初始匹配
        while (findMatches().size() > 0) {
            fillBoard();
        }
    }

    /**
     * 处理瓷砖交换
     */
    public boolean swapTiles(int row1, int col1, int row2, int col2) {
        if (!isValidPosition(row1, col1) || !isValidPosition(row2, col2)) {
            return false;
        }

        // 检查是否相邻
        if (!isAdjacent(row1, col1, row2, col2)) {
            return false;
        }

        // 交换
        Tile temp = board[row1][col1];
        board[row1][col1] = board[row2][col2];
        board[row2][col2] = temp;

        // 更新位置信息
        board[row1][col1].row = row1;
        board[row1][col1].col = col1;
        board[row2][col2].row = row2;
        board[row2][col2].col = col2;

        // 检查匹配
        List<Tile> matches = findMatches();
        if (matches.size() >= MIN_MATCH) {
            matchCount++;
            moves++;
            return true;
        } else {
            // 没有匹配，回滚
            temp = board[row1][col1];
            board[row1][col1] = board[row2][col2];
            board[row2][col2] = temp;
            board[row1][col1].row = row1;
            board[row1][col1].col = col1;
            board[row2][col2].row = row2;
            board[row2][col2].col = col2;
            return false;
        }
    }

    /**
     * 查找所有匹配的瓷砖
     */
    public List<Tile> findMatches() {
        matchedTiles.clear();
        Set<Tile> matched = new HashSet<>();

        // 检查水平匹配
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col].isEmpty()) continue;

                int matchLen = 1;
                while (col + matchLen < BOARD_WIDTH &&
                        board[row][col].matches(board[row][col + matchLen])) {
                    matchLen++;
                }

                if (matchLen >= MIN_MATCH) {
                    for (int i = 0; i < matchLen; i++) {
                        matched.add(board[row][col + i]);
                    }
                }
            }
        }

        // 检查垂直匹配
        for (int col = 0; col < BOARD_WIDTH; col++) {
            for (int row = 0; row < BOARD_HEIGHT; row++) {
                if (board[row][col].isEmpty()) continue;

                int matchLen = 1;
                while (row + matchLen < BOARD_HEIGHT &&
                        board[row][col].matches(board[row + matchLen][col])) {
                    matchLen++;
                }

                if (matchLen >= MIN_MATCH) {
                    for (int i = 0; i < matchLen; i++) {
                        matched.add(board[row + i][col]);
                    }
                }
            }
        }

        matchedTiles.addAll(matched);
        return matchedTiles;
    }

    /**
     * 移除匹配的瓷砖并计分
     */
    public void removeMatches() {
        List<Tile> matches = findMatches();
        if (matches.isEmpty()) return;

        // 计分：3个50分，4个200分，5个500分，6个1000分
        int multiplier = Math.min(matches.size() - 2, 4);
        int baseScore = new int[]{50, 200, 500, 1000, 2000}[multiplier];
        score += baseScore;

        changedCells.clear();
        for (Tile tile : matches) {
            tile.type = Tile.TYPE_EMPTY;
            changedCells.add(tile.row * BOARD_WIDTH + tile.col);
        }

        // 检查关卡升级
        if (matchCount % 5 == 0) {
            level++;
        }
    }

    /**
     * 填充游戏板 - 移动瓷砖向下并填充空位
     */
    public void fillBoard() {
        changedCells.clear();
        
        // 第一步：将瓷砖下移
        for (int col = 0; col < BOARD_WIDTH; col++) {
            int writePos = BOARD_HEIGHT - 1;
            for (int row = BOARD_HEIGHT - 1; row >= 0; row--) {
                if (!board[row][col].isEmpty()) {
                    if (row != writePos) {
                        board[writePos][col] = board[row][col];
                        board[writePos][col].row = writePos;
                        changedCells.add(writePos * BOARD_WIDTH + col);
                        changedCells.add(row * BOARD_WIDTH + col);
                    }
                    writePos--;
                }
            }
        }

        // 第二步：生成新瓷砖填充顶部空位
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col].isEmpty()) {
                    int type = random.nextInt(6) + 1;
                    board[row][col] = new Tile(type, row, col);
                    changedCells.add(row * BOARD_WIDTH + col);
                }
            }
        }
    }

    /**
     * 执行一次游戏循环
     */
    public void update() {
        // 移除匹配
        removeMatches();
        
        // 如果有移除，填充并再次检查
        if (!matchedTiles.isEmpty()) {
            fillBoard();
            
            // 继续链式反应
            if (findMatches().size() > 0) {
                update(); // 递归调用处理链式反应
            }
        }

        // 检查是否游戏结束
        if (matchCount > 50) { // 简单的游戏结束条件
            isGameRunning = false;
        }
    }

    /**
     * 检查位置是否有效
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < BOARD_HEIGHT && col >= 0 && col < BOARD_WIDTH;
    }

    /**
     * 检查两个位置是否相邻
     */
    private boolean isAdjacent(int row1, int col1, int row2, int col2) {
        int distance = Math.abs(row1 - row2) + Math.abs(col1 - col2);
        return distance == 1;
    }

    // Getter方法
    public Tile getTile(int row, int col) {
        if (isValidPosition(row, col)) {
            return board[row][col];
        }
        return null;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public int getBoardHeight() {
        return BOARD_HEIGHT;
    }

    public int getBoardWidth() {
        return BOARD_WIDTH;
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    public List<Tile> getMatchedTiles() {
        return matchedTiles;
    }

    public Set<Integer> getChangedCells() {
        return changedCells;
    }

    public void reset() {
        score = 0;
        level = 1;
        moves = 0;
        matchCount = 0;
        isGameRunning = true;
        initializeBoard();
    }
}
