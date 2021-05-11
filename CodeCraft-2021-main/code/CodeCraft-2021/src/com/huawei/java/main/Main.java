package com.huawei.java.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        boolean isTestMode = true;
        // TODO: read standard input
        Scanner scanner;
        if(isTestMode) {
            File file = new File("D:\\Todos\\CodeCraft\\SDK\\CodeCraft-2021\\training-data\\training-1.txt");
            scanner = new Scanner(file);
        } else {
            scanner = new Scanner(System.in);
        }
        Reader reader = new Reader(scanner);
        List<ServerType> serverTypes = reader.readServerTypes();
        List<VmType> vmTypes = reader.readVmTypes();
        List<ArrayList<Request>> allRequest = reader.readRequests(vmTypes);
        // TODO: process
        Solver solver = new Solver(serverTypes, vmTypes);
        List<Decision> decisions = solver.solve(allRequest);
        // TODO: write standard output
        if(isTestMode) {
            Simulator simulator = new Simulator(serverTypes, vmTypes);
            simulator.Simulate(allRequest, decisions);
        } else {
            Writer writer = new Writer();
            writer.writeAll(decisions);
            System.out.flush();
        }
    }
}