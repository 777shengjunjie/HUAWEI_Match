package com.huawei.java.main;

public class Deployment {
    public ServerInstance server;

    public String node;

    public Deployment(ServerInstance server, String node) {
        this.server = server;
        this.node = node;
    }


    @Override
    public String toString() {
        if(node.equals("T")) {
            return "(" + server.id + ")\n";
        }
        return "(" + server.id + ", " + node + ")\n";
    }
}
