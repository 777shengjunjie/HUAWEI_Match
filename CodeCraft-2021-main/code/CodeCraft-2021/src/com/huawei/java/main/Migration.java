package com.huawei.java.main;

public class Migration {
    public VmInstance vm;

    public ServerInstance server;

    public String node;

    public Migration(VmInstance vm, ServerInstance server, String node) {
        this.vm = vm;
        this.server = server;
        this.node = node;
    }

    @Override
    public String toString() {
        if(node.equals("T")) {
            return "(" + vm.id + ", " + server.id + ")\n";
        }

        return "(" + vm.id + ", " + server.id + ", " + node + ")\n";
    }
}
