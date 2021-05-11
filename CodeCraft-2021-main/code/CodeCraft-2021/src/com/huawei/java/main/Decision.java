package com.huawei.java.main;

import java.util.List;

public class Decision {

    public List<Purchase> purchases;

    public List<Migration> migrations;

    public List<Deployment> deployments;

    public Decision(List<Purchase> purchases, List<Migration> migrations, List<Deployment> deployments) {
        this.purchases = purchases;
        this.migrations = migrations;
        this.deployments = deployments;
    }
}
