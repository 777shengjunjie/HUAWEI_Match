package com.huawei.java.main;

import java.util.List;

public class Writer {

    public void writeAll(List<Decision> decisions) {
        StringBuilder sb = new StringBuilder();
        for(Decision decision : decisions) {
            sb.append("(purchase, ").append(decision.purchases.size()).append(")\n");
            for(Purchase purchase : decision.purchases) {
                sb.append(purchase.toString());
            }
            sb.append("(migration, ").append(decision.migrations.size()).append(")\n");
            for(Migration migration : decision.migrations) {
                sb.append(migration.toString());
            }
            for(Deployment deployment : decision.deployments) {
                sb.append(deployment.toString());
            }
        }
        System.out.println(sb.toString());
    }
}
