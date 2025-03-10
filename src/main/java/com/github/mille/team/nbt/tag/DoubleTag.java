package com.github.mille.team.nbt.tag;

import com.github.mille.team.nbt.stream.NBTInputStream;
import com.github.mille.team.nbt.stream.NBTOutputStream;

import java.io.IOException;

public class DoubleTag extends NumberTag<Double> {

    public double data;

    public DoubleTag(String name) {
        super(name);
    }

    public DoubleTag(
        String name,
        double data
    ) {
        super(name);
        this.data = data;
    }

    @Override
    public Double getData() {
        return data;
    }

    @Override
    public void setData(Double data) {
        this.data = data == null ? 0 : data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        dos.writeDouble(data);
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        data = dis.readDouble();
    }

    @Override
    public byte getId() {
        return TAG_Double;
    }

    @Override
    public String toString() {
        return "DoubleTag " + this.getName() + " (data: " + data + ")";
    }

    @Override
    public Tag copy() {
        return new DoubleTag(getName(), data);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            DoubleTag o = (DoubleTag) obj;
            return data == o.data;
        }
        return false;
    }

}
