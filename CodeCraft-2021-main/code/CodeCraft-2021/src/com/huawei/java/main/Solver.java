package com.huawei.java.main;

import java.util.ArrayList;
import java.util.List;

public class Solver {
    public ServerManager serverManager;

    public VmManager vmManager;

    public Solver(List<ServerType> serverTypes, List<VmType> vmTypes) {
        this.serverManager = new ServerManager(serverTypes);
        this.vmManager = new VmManager(vmTypes);
    }

    public List<Decision> solve(List<ArrayList<Request>> allRequest) throws Exception {
        ArrayList<Decision> decisions = new ArrayList<>();

        serverManager.filterServerType();
//        vmManager.updateVmTypeMigratePolicy();

        for(List<Request> dayRequest : allRequest) {
            List<Purchase> purchases = makePurchaseDecision(dayRequest);
            purchase(purchases);

            List<Migration> migrations = doMigrate();

            List<Deployment> deployments = new ArrayList<>();
            for(Request request : dayRequest) {
                if(request.isAddOperation) {
                    VmType vmType = request.vmType;
                    Deployment deployment = makeDeployDecision(vmType, null);
                    while(deployment.server == null) {
                        ServerType targetType = serverManager.getSuitableServerType(vmType.room, vmType.isDoubleNode,
                                true);
                        Purchase newPurchase = new Purchase(targetType, 1);
                        purchase(new ArrayList<Purchase>() {{
                            add(newPurchase);
                        }});
                        int shiftCount = newPurchase.mergeToList(purchases);
                        if(shiftCount != 0) {
                            serverManager.updateServerOrder(shiftCount);
                        }
                        int newIndex = serverManager.servers.size() - shiftCount - 1;
                        String deployNode = vmType.isDoubleNode ? "T" : "A";
                        deployment = new Deployment(serverManager.servers.get(newIndex), deployNode);
                    }
                    deployments.add(deployment);
                    VmInstance vm = vmManager.createVmInstance(request.vmId, vmType);
                    deploy(deployment, vm);
                } else {
                    String vmId = request.vmId;
                    VmInstance vm = vmManager.getVmInstance(vmId);
                    ServerInstance server = vm.host;
                    serverManager.addToCandidates(server);
                    vmManager.removeVmInstance(vmId);
                }
            }

            Decision dayDecision = new Decision(purchases, migrations, deployments);
            decisions.add(dayDecision);
        }

        return decisions;
    }

    public List<Purchase> makePurchaseDecision(List<Request> dayRequest) {
        ArrayList<Purchase> purchases = new ArrayList<>();

        Room totalRequiredRoom = new Room(0, 0);
        for(Request request : dayRequest) {
            if(request.isAddOperation) {
                totalRequiredRoom = totalRequiredRoom.add(request.vmType.room);
            } else {
                VmInstance vm = vmManager.getVmInstance(request.vmId);
                totalRequiredRoom = totalRequiredRoom.minus(vm.vmType.room);
            }
        }

        totalRequiredRoom = totalRequiredRoom.minus(serverManager.getFreeRoomInCandidates().multiple(0.9f));
        if(totalRequiredRoom.coreCount < 0 && totalRequiredRoom.memorySize < 0) return new ArrayList<>();

        ServerType targetType = serverManager.getSuitableServerType(totalRequiredRoom, false, false);
        float[] counts = totalRequiredRoom.divide(targetType.room);
        int count = (int) Math.ceil(Math.max(counts[0], counts[1]));
        purchases.add(new Purchase(targetType, count));

        return purchases;
    }

    public void purchase(List<Purchase> purchases) {
        for(Purchase purchase : purchases) {
            for(int i = 0; i < purchase.count; i++) {
                serverManager.addServerInstance(purchase.serverType);
            }
        }
    }

    public List<Migration> doMigrate() throws Exception {
        List<Migration> migrations = new ArrayList<>();

        int canMigrateCount = (int) Math.floor((float) vmManager.getVmCount() * 3.5 / 1000);

        for(int i = serverManager.candidates.size() - 1; i > 0; i--) {
            if(canMigrateCount <= 0) break;

            ServerInstance server = serverManager.candidates.get(i);
            if(server.getUsageRatio("T") > 0.75) continue;

            List<VmInstance> deployedVms = server.deployedVmInstance.get("T");
            int rawSizeCount = deployedVms.size();
            int currentMigrationCount = 0;
            for(int j = 0; j < rawSizeCount - currentMigrationCount; j++) {
                VmInstance vm = deployedVms.get(j);

                if(!vm.vmType.shouldMigrate) continue;

                Deployment deployment = makeDeployDecision(vm.vmType, server);
                if(deployment.server == server) continue;

                Migration migration = new Migration(vm, deployment.server, deployment.node);
                migrations.add(migration);
                server.removeInstance(vm);
                if(deploy(deployment, vm)) {
                    i--;
                }
                canMigrateCount--;
                j--;
                currentMigrationCount++;
                if(canMigrateCount <= 0) break;
            }
        }

        return migrations;
    }

    public Deployment makeDeployDecision(VmType vmType, ServerInstance stopServer) {
        return serverManager.selectServerInstanceToDeploy(vmType, stopServer);
    }

    public boolean deploy(Deployment deployment, VmInstance vmInstance) throws Exception {
        ServerInstance server = deployment.server;
        if(deployment.node.equals("T")) {
            server.addVmInstance(vmInstance, new ArrayList<String>() {{
                add("A");
                add("B");
            }});
        } else {
            server.addVmInstance(vmInstance, new ArrayList<String>() {{
                add(deployment.node);
            }});
        }

        if(server.getUsageRatio("T") > 0.95) {
            serverManager.removeFromCandidates(server);
            return true;
        }

        return false;
    }
}
