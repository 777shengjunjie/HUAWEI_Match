package com.huawei.java.main;

import java.util.*;

public class Reader {
    private final Scanner scanner;

    public Reader(Scanner scanner) {
        this.scanner = scanner;
    }

    public List<ServerType> readServerTypes() {
        ArrayList<ServerType> serverTypes = new ArrayList<>();

        int typeCount = readInt();
        for(int i = 0; i < typeCount; i++) {
            String[] info = readList();
            String name = info[0].substring(1).trim();
            int core = Integer.parseInt(info[1].trim());
            int memory = Integer.parseInt(info[2].trim());
            int hardwareCost = Integer.parseInt(info[3].trim());
            int dailyEnergyCost = Integer.parseInt(info[4].split("\\)")[0].trim());

            serverTypes.add(new ServerType(name, core, memory, hardwareCost, dailyEnergyCost));
        }

        return serverTypes;
    }

    public List<VmType> readVmTypes() {
        ArrayList<VmType> vmTypes = new ArrayList<>();

        int typeCount = readInt();
        for(int i = 0; i < typeCount; i++) {
            String[] info = readList();
            String name = info[0].substring(1).trim();
            int core = Integer.parseInt(info[1].trim());
            int memory = Integer.parseInt(info[2].trim());
            boolean isDoubleNode = "1".equals(info[3].split("\\)")[0].trim());

            vmTypes.add(new VmType(name, core, memory, isDoubleNode));
        }

        return vmTypes;
    }

    public List<ArrayList<Request>> readRequests(List<VmType> vmTypes) {
        Map<String, VmType> vmTypeMap = new HashMap<>();
        for(VmType vmType : vmTypes) {
            vmTypeMap.put(vmType.name, vmType);
        }

        ArrayList<ArrayList<Request>> allRequest = new ArrayList<>();

        int dayCount = readInt();
        for(int day = 0; day < dayCount; day++) {
            ArrayList<Request> dayRequest = new ArrayList<>();

            int count = readInt();
            for(int i = 0; i < count; i++) {
                Request request;
                String[] info = readList();
                String command = info[0].substring(1).trim();
                if(command.equals("add")) {
                    String vmTypeName = info[1].trim();
                    String vmId = info[2].split("\\)")[0].trim();
                    request = new Request(true, vmTypeMap.get(vmTypeName), vmId);
                } else {
                    String vmId = info[1].split("\\)")[0].trim();
                    request = new Request(false, null, vmId);
                }

                dayRequest.add(request);
            }

            allRequest.add(dayRequest);
        }

        return allRequest;
    }

    private int readInt() {
        return Integer.parseInt(scanner.nextLine());
    }

    private String[] readList() {
        return scanner.nextLine().split(",");
    }
}
