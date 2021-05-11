package com.huawei.java.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerInstance {
    public int id;

    public ServerType serverType;

    public Map<String, Room> remainRoom = new HashMap<>();

    public Map<String, List<VmInstance>> deployedVmInstance = new HashMap<>();

    public ServerInstance(int id, ServerType type) {
        this.id = id;
        this.serverType = type;

        this.remainRoom.put("A", new Room(type.room.coreCount / 2, type.room.memorySize / 2));
        this.remainRoom.put("B", new Room(type.room.coreCount / 2, type.room.memorySize / 2));
        this.remainRoom.put("T", new Room(type.room.coreCount, type.room.memorySize));

        this.deployedVmInstance.put("A", new ArrayList<>());
        this.deployedVmInstance.put("B", new ArrayList<>());
        this.deployedVmInstance.put("T", new ArrayList<>());
    }

    public float getCurrentCoreMemRatio(String node) {
        Room remainRoom = this.remainRoom.get(node);
        if(remainRoom.memorySize == 0) {
            return Float.MAX_VALUE;
        }
        return (float) remainRoom.coreCount / remainRoom.memorySize;
    }

    public float getUsageRatio(String node) {
        Room remainRoomInNode = remainRoom.get(node);
        float[] ratio = remainRoomInNode.divide(serverType.room);
        float averageRatio = (ratio[0] + ratio[1]) / 2;
        if(!node.equals("T")) {
            averageRatio *= 2;
        }

        return 1 - averageRatio;
    }

    public boolean canPlace(Room room, String node) {
        if(node.equals("T")) {
            Room newRoom = room.divide(2);
            return canPlace(newRoom, "A") && canPlace(newRoom, "B");
        }

        Room remainRoomInNode = remainRoom.get(node);
        return remainRoomInNode.canFit(room);
    }

    public void addVmInstance(VmInstance vm, List<String> nodes) throws Exception {
        int len = nodes.size();
        for(String node : nodes) {
            Room deleteRoom = vm.vmType.room.divide(len);
            Room newRemainRoom = remainRoom.get(node).minus(deleteRoom);

            if(!newRemainRoom.canFit(0, 0)) {
                throw new Exception("存在主机实例的空间不足");
            }

            remainRoom.put(node, newRemainRoom);
            deployedVmInstance.get(node).add(vm);
        }
        Room newRoom = remainRoom.get("T").minus(vm.vmType.room);
        remainRoom.put("T", newRoom);
        deployedVmInstance.get("T").add(vm);
        vm.host = this;
        vm.nodes = nodes;
    }

    public void removeInstance(VmInstance vm) throws Exception {
        ServerInstance server = vm.host;
        if(server != this) {
            throw new Exception(String.format("虚拟机实例的宿主（id = %s）不是当前主机实例（id = %d）", vm.id, server.id));
        }

        int len = vm.nodes.size();
        for(String node : vm.nodes) {
            Room roomFreed = vm.vmType.room.divide(len);
            Room newRemainRoom = remainRoom.get(node).add(roomFreed);
            remainRoom.put(node, newRemainRoom);
            deployedVmInstance.get(node).remove(vm);
        }
        Room newRoom = remainRoom.get("T").add(vm.vmType.room);
        remainRoom.put("T", newRoom);
        deployedVmInstance.get("T").remove(vm);
    }

    public boolean isOn() {
        Room totalRemainRoom = remainRoom.get("T");
        return totalRemainRoom.coreCount < serverType.room.coreCount || totalRemainRoom.memorySize < serverType.room.memorySize;
    }
}
