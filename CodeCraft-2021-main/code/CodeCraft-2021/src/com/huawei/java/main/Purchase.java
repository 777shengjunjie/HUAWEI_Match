package com.huawei.java.main;

import java.util.List;

public class Purchase {
    public ServerType serverType;

    public int count;

    public Purchase(ServerType serverType, int count) {
        this.serverType = serverType;
        this.count = count;
    }

    @Override
    public String toString() {
        return "(" + serverType.name + ", " + count + ")\n";
    }

    public int mergeToList(List<Purchase> purchases) {
        if(purchases.size() == 0) {
            purchases.add(this);
            return 0;
        }

        int flagIndex = -1;
        for(int index = 0; index < purchases.size(); index++) {
            Purchase purchase = purchases.get(index);
            if(this.serverType == purchase.serverType) {
                purchase.count++;
                flagIndex = index;
                break;
            }
        }

        if(flagIndex == -1) {
            purchases.add(this);
            return 0;
        } else {
            int shiftCount = 0;
            while(flagIndex < purchases.size() - 1) {
                flagIndex++;
                shiftCount += purchases.get(flagIndex).count;
            }
            return shiftCount;
        }
    }
}
