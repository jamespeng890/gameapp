package com.gamedev.match3.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用对象池 - 用于复用对象，减少GC压力
 */
public abstract class ObjectPool<T> {
    private List<T> availableObjects;
    private List<T> inUseObjects;
    private int poolSize;

    public ObjectPool(int initialSize) {
        this.poolSize = initialSize;
        this.availableObjects = new ArrayList<>(initialSize);
        this.inUseObjects = new ArrayList<>(initialSize);

        for (int i = 0; i < initialSize; i++) {
            availableObjects.add(create());
        }
    }

    /**
     * 创建新对象 - 由子类实现
     */
    protected abstract T create();

    /**
     * 重置对象 - 由子类实现
     */
    protected abstract void reset(T obj);

    /**
     * 获取对象
     */
    public synchronized T obtain() {
        T obj;
        if (availableObjects.size() > 0) {
            obj = availableObjects.remove(availableObjects.size() - 1);
        } else {
            obj = create();
        }
        inUseObjects.add(obj);
        return obj;
    }

    /**
     * 归还对象
     */
    public synchronized void release(T obj) {
        if (inUseObjects.remove(obj)) {
            reset(obj);
            availableObjects.add(obj);
        }
    }

    /**
     * 释放所有对象
     */
    public synchronized void clear() {
        availableObjects.clear();
        inUseObjects.clear();
    }

    /**
     * 获取对象池信息
     */
    public int getPoolSize() {
        return availableObjects.size();
    }

    public int getInUseSize() {
        return inUseObjects.size();
    }
}
