package com.huawei.java.main;

import java.util.List;

public class VmInstance {
    public String id;

    public VmType vmType;

    public ServerInstance host;

    public List<String> nodes;

    public VmInstance(String id, VmType vmType) {
        this.id = id;
        this.vmType = vmType;
    }

    public void removeFromHost() throws Exception {
        if(host == null) {
            throw new Exception(String.format("虚拟机实例 id = %s 尚未部署在主机实例上", id));
        }

        host.removeInstance(this);
    }
}
