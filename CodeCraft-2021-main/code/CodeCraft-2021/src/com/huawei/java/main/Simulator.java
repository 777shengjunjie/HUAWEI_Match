package com.huawei.java.main;

import java.util.ArrayList;
import java.util.List;

public class Simulator {
    private final ServerManager serverManager;
    private final VmManager vmManager;
    private float totalCost;
    private float totalMigrationCount;

    public Simulator(List<ServerType> serverTypes,
                     List<VmType> vmTypes) {
        this.totalCost = 0;
        this.totalMigrationCount = 0;

        this.serverManager = new ServerManager(serverTypes);
        this.vmManager = new VmManager(vmTypes);
    }

    public void Simulate(List<ArrayList<Request>> allRequest, List<Decision> decisions) throws Exception {
        for(int day = 0; day < allRequest.size(); day++) {
            List<Request> dayRequest = allRequest.get(day);
            Decision decision = decisions.get(day);

            purchase(decision.purchases);
            migrate(decision.migrations);

            int deployIndex = 0;
            for(Request request : dayRequest) {
                if(request.isAddOperation) {
                    VmInstance vm = vmManager.createVmInstance(request.vmId, request.vmType);
                    deploy(decision.deployments.get(deployIndex), vm);
                    deployIndex++;
                } else {
                    vmManager.removeVmInstance(request.vmId);
                }
            }

            for(ServerInstance server : serverManager.servers) {
                if(server.isOn()) {
                    totalCost += server.serverType.dailyCost;
                }
            }
        }

        System.out.printf("cost: %f%n", totalCost);
        System.out.printf("migration: %f%n", totalMigrationCount);
    }

    private void purchase(List<Purchase> purchases) {
        for(Purchase purchase : purchases) {
            totalCost += purchase.serverType.expense * purchase.count;
            for(int i = 0; i < purchase.count; i++) {
                serverManager.addServerInstance(purchase.serverType);
            }
        }
    }

    private void migrate(List<Migration> migrations) throws Exception {
        for(Migration migration : migrations) {
            ServerInstance server = serverManager.servers.get(migration.server.id);
            VmInstance vm = vmManager.getVmInstance(migration.vm.id);
            totalMigrationCount++;
            vm.removeFromHost();
            deploy(new Deployment(server, migration.node), vm);
        }
    }

    private void deploy(Deployment deployment, VmInstance vm) throws Exception {
        ServerInstance server = serverManager.servers.get(deployment.server.id);
        if(deployment.node.equals("T")) {
            server.addVmInstance(vm, new ArrayList<String>() {{
                add("A");
                add("B");
            }});
        } else {
            server.addVmInstance(vm, new ArrayList<String>() {{
                add(deployment.node);
            }});
        }
    }
}
