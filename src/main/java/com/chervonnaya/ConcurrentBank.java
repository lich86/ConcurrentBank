package com.chervonnaya;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentBank {
    private final List<BankAccount> accounts = new ArrayList<>();
    ReentrantLock bankLock = new ReentrantLock();

    protected void transfer(BankAccount from, BankAccount to, BigDecimal amount) {
        ReentrantLock firstLock;
        ReentrantLock secondLock;
        if(from.getId().compareTo(to.getId()) < 0) {
            firstLock = from.getLock();
            secondLock = to.getLock();
        } else {
            firstLock = to.getLock();
            secondLock = from.getLock();
        }
        firstLock.lock();
        try{
            secondLock.lock();
            try{
                if (from.getBalance().compareTo(amount) < 0) {
                    throw new IllegalArgumentException("Insufficient funds for transfer.");
                }
                from.withdraw(amount);
                to.deposit(amount);
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }

    protected BankAccount createAccount(BigDecimal initialBalance) {
        bankLock.lock();
        try {
            BankAccount bankAccount = new BankAccount(initialBalance);
            accounts.add(bankAccount);
            return bankAccount;
        } finally {
            bankLock.unlock();
        }
    }

    protected BigDecimal getTotalBalance() {
        bankLock.lock();
        try {
            return accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        } finally {
            bankLock.unlock();
        }
    }
}
