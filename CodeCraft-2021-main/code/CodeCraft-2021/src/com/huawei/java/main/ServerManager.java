package com.huawei.java.main;

import java.util.ArrayList;
import java.util.List;

public class ServerManager {
    public List<ServerType> serverTypes;
    public List<ServerInstance> servers = new ArrayList<>();
    public int newServerId;
    public List<ServerInstance> candidates = new ArrayList<>();

    public ServerManager(List<ServerType> serverTypes) {
        this.serverTypes = serverTypes;
        this.newServerId = 0;
    }

    public void addServerInstance(ServerType type) {
        ServerInstance server = new ServerInstance(newServerId, type);
        newServerId++;
        servers.add(server);
        addToCandidates(server);
    }

    public void filterServerType() {
        Room totalRoom = new Room(0, 0);
        float totalExpense = 0f;
        float totalDailyCost = 0f;

        List<ServerType> candidates = new ArrayList<>();
        for(ServerType serverType : this.serverTypes) {
            totalExpense += serverType.expense;
            totalDailyCost += serverType.dailyCost;
            totalRoom = totalRoom.add(serverType.room);
        }

        for(ServerType serverType : this.serverTypes) {
            float expenseWeight = serverType.expense / totalExpense;
            float dailyCostWeight = serverType.dailyCost / totalDailyCost;
            float[] roomWight = serverType.room.divide(totalRoom);
            float roomAverageWeight = (roomWight[0] + roomWight[1]) / 2;
            float costAverageWeight = (expenseWeight + dailyCostWeight) / 2;
            float costEfficiency = roomAverageWeight - costAverageWeight;
            if(costEfficiency > 0) {
                candidates.add(serverType);
            }
        }

        this.serverTypes = candidates;
    }

    public Deployment selectServerInstanceToDeploy(VmType vmType, ServerInstance stopServer) {
        Deployment deployment = new Deployment(stopServer, "T");

        if(vmType.isDoubleNode) {
            for(int i = 0; i < candidates.size(); i++) {
                ServerInstance server = candidates.get(i);
                if(server == stopServer) break;
                if(server.canPlace(vmType.room, "T")) {
                    deployment.server = server;
                    float hostRatio = server.serverType.room.coreMemRatio;
                    float currentRatio = server.getCurrentCoreMemRatio("T");
                    if((currentRatio - hostRatio) * (vmType.room.coreMemRatio - hostRatio) < 0) {
                        continue;
                    }
                    deployment.server = server;
                    break;
                }
            }
        } else {
            for(int i = 0; i < candidates.size(); i++) {
                ServerInstance server = candidates.get(i);
                if(server == stopServer) break;

                if(server.canPlace(vmType.room, "A")) {
                    deployment.server = server;
                    deployment.node = "A";
                    float hostRatio = server.serverType.room.coreMemRatio;
                    float currentRatio = server.getCurrentCoreMemRatio("A");
                    if((currentRatio - hostRatio) * (vmType.room.coreMemRatio - hostRatio) < 0) {
                        continue;
                    }
                    deployment.server = server;
                    deployment.node = "A";
                    break;
                }

                if(server.canPlace(vmType.room, "B")) {
                    deployment.server = server;
                    deployment.node = "B";
                    float hostRatio = server.serverType.room.coreMemRatio;
                    float currentRatio = server.getCurrentCoreMemRatio("B");
                    if((currentRatio - hostRatio) * (vmType.room.coreMemRatio - hostRatio) < 0) {
                        continue;
                    }
                    deployment.server = server;
                    deployment.node = "B";
                    break;
                }
            }
        }

        return deployment;
    }

    public void updateServerOrder(int shiftCount) {
        int serverSize = servers.size();
        ServerInstance targetServer = servers.remove(serverSize - 1);
        targetServer.id -= shiftCount;
        servers.add(serverSize - shiftCount - 1, targetServer);

        for(int index = serverSize - shiftCount; index < serverSize; index++) {
            servers.get(index).id++;
        }
    }

    public ServerType getSuitableServerType(Room targetRoom, boolean isDoubleNode, boolean check) {
        float optimalValue = Float.MAX_VALUE;
        ServerType optimalType = null;
        for(ServerType type : serverTypes) {
            if(check) {
                Room requiredRoom = isDoubleNode ? targetRoom : targetRoom.multiple(2);
                if(!type.room.canFit(requiredRoom)) {
                    continue;
                }
            }

            float currentValue = Math.abs(type.room.coreMemRatio - targetRoom.coreMemRatio);
            if(currentValue < optimalValue) {
                optimalType = type;
                optimalValue = currentValue;
            }
        }

        return optimalType;
    }

    public void removeFromCandidates(ServerInstance server) throws Exception {
        if(!candidates.contains(server)) {
            throw new Exception(String.format("主机实例 id = %d 不在候选中", server.id));
        }

        candidates.remove(server);
    }

    public void addToCandidates(ServerInstance server) {
        if(candidates.contains(server)) {
            return;
        }

        candidates.add(server);
    }

    public Room getFreeRoomInCandidates() {
        Room freeRoom = new Room(0, 0);

        for(ServerInstance server : candidates) {
            freeRoom = freeRoom.add(server.remainRoom.get("T"));
        }

        return freeRoom;
    }
}
