package com.huawei.java.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VmManager {
    public Map<String, VmInstance> vms = new HashMap<>();

    public List<VmType> vmTypes;

    public VmManager(List<VmType> vmTypes) {
        this.vmTypes = vmTypes;
    }

    public int getVmCount() {
        return vms.size();
    }

    public VmInstance getVmInstance(String vmId) {
        return vms.get(vmId);
    }

    public void addVmInstance(VmInstance vm) {
        vms.put(vm.id, vm);
    }

    public VmInstance createVmInstance(String id, VmType vmType) {
        VmInstance vm = new VmInstance(id, vmType);
        addVmInstance(vm);

        return vm;
    }

    public void removeVmInstance(String vmId) throws Exception {
        VmInstance vm = vms.remove(vmId);
        vm.removeFromHost();
    }

    public void updateVmTypeMigratePolicy() {
        Room totalRoom = new Room(0, 0);
        int count = vmTypes.size();
        float ave = (float) 1 / count;
        for(VmType type : vmTypes) {
            totalRoom = totalRoom.add(type.room);
        }

        for(VmType type : vmTypes) {
            float[] ratio = type.room.divide(totalRoom);
            type.shouldMigrate = ratio[0] < ave * 1.1 || ratio[1] < ave * 1.1;
        }
    }
}
