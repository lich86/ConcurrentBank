package com.chervonnaya;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class AccountLockManager {

    private final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    public ReentrantLock getLock(String accountId) {
        return lockMap.computeIfAbsent(accountId, k -> new ReentrantLock());
    }

}
