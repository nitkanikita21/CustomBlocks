package me.nitkanikita21.customblocks.core.transfer;

import io.vavr.collection.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Transaction implements AutoCloseable {
    @Getter
    private boolean committed = false;
    private List<Runnable> rollbackActions = List.empty();

    /**
     * Marks the transaction as committed.
     */
    public void commit() {
        committed = true;
    }

    /**
     * Adds a rollback action to be executed if transaction is aborted.
     */
    public void addRollback(Runnable action) {
        rollbackActions = rollbackActions.append(action);
    }

    /**
     * Executes rollback actions if the transaction was not committed.
     */
    @Override
    public void close() {
        if (!committed) {
            rollbackActions.forEach(Runnable::run);
        }
    }
}