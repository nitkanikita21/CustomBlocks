package me.nitkanikita21.customblocks.core.block;

public enum ActionResult {
    SUCCESS,    // Дія виконана — скасувати подію
    FAIL,       // Щось не так — скасувати + можливо повідомлення
    PASS,       // Нічого не зроблено — дозволити події йти далі
    CONSUME     // Виконати дію й забрати предмет
}