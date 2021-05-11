package com.huawei.java.main;

public class VmType {
    public String name;

    public Room room;

    public boolean isDoubleNode;

    public boolean shouldMigrate = true;

    public VmType(String name, int coreCount, int memorySize, boolean isDoubleNode) {
        this.name = name;
        this.room = new Room(coreCount, memorySize);
        this.isDoubleNode = isDoubleNode;
    }
}
