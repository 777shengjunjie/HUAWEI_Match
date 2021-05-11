package com.huawei.java.main;

public class Room {
    public int coreCount;

    public int memorySize;

    public float coreMemRatio;

    public Room(int coreCount, int memorySize) {
        this.coreCount = coreCount;
        this.memorySize = memorySize;

        if(memorySize == 0) {
            this.coreMemRatio = Float.MAX_VALUE;
        } else {
            this.coreMemRatio = (float) coreCount / memorySize;
        }
    }

    public Room minus(Room room) {
        return new Room(coreCount - room.coreCount, memorySize - room.memorySize);
    }

    public Room add(Room room) {
        return new Room(coreCount + room.coreCount, memorySize + room.memorySize);
    }

    public Room divide(int len) {
        return new Room(coreCount / len, memorySize / len);
    }

    public float[] divide(Room room) {
        float[] result = new float[2];
        result[0] = (float) coreCount / room.coreCount;
        result[1] = (float) memorySize / room.memorySize;

        return result;
    }

    public Room multiple(float time) {
        return new Room((int) (coreCount * time), (int) (memorySize * time));
    }

    public boolean canFit(int coreCount, int memorySize) {
        return this.coreMemRatio >= coreCount && this.memorySize >= memorySize;
    }

    public boolean canFit(Room room) {
        return coreCount >= room.coreCount && memorySize >= room.memorySize;
    }

    @Override
    public String toString() {
        return "Room{" +
                "coreCount=" + coreCount +
                ", memorySize=" + memorySize +
                '}';
    }
}
