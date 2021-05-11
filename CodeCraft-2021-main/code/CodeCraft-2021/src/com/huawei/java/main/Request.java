package com.huawei.java.main;

public class Request {
    public boolean isAddOperation;

    public VmType vmType;

    public String vmId;

    public Request(boolean isAddOperation, VmType vmType, String vmId) {
        this.isAddOperation = isAddOperation;
        this.vmType = vmType;
        this.vmId = vmId;
    }
}
