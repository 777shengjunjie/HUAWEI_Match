package com.huawei.java.main;

public class ServerType {
    public String name;

    public Room room;

    public int expense;

    public int dailyCost;

    public ServerType(String name, int coreCount, int memorySize, int expense, int dailyCost) {
        this.name = name;
        this.room = new Room(coreCount, memorySize);
        this.expense = expense;
        this.dailyCost = dailyCost;
    }
}
