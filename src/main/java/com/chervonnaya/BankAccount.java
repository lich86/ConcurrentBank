package com.chervonnaya;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class BankAccount {
    private final UUID id;
    private BigDecimal balance;
    private final AccountLockManager manager = new AccountLockManager();
    private ReentrantLock lock;

    public BankAccount(BigDecimal balance) {
        this.id = UUID.randomUUID();
        this.balance = balance;
        lock = manager.getLock(this.id.toString());
    }

    public void deposit(BigDecimal amount) {
        lock.lock();
        try {
            balance = balance.add(amount);
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(BigDecimal amount) {
        lock.lock();
        try {
            balance = balance.subtract(amount);
        } finally {
            lock.unlock();
        }
    }

    public BigDecimal getBalance() {
        lock.lock();
        try {
            return this.balance;
        } finally {
            lock.unlock();
        }
    }
}
