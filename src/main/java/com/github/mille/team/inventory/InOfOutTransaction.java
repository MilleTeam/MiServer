package com.github.mille.team.inventory;

import com.github.mille.team.item.Item;

public class InOfOutTransaction {

    private Item in;

    private Item out;

    public InOfOutTransaction(
        Item in,
        Item out
    ) {
        this.in = in;
        this.out = out;
    }

    public InOfOutTransaction() {
        this(null, null);
    }

    public Item getIn() {
        return in;
    }

    public void setIn(Item in) {
        this.in = in;
    }

    public Item getOut() {
        return out;
    }

    public void setOut(Item out) {
        this.out = out;
    }

}
