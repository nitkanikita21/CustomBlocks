package me.nitkanikita21.customblocks.core.transfer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionContext {
    public Transaction openTransaction() {
        return new Transaction();
    }
}