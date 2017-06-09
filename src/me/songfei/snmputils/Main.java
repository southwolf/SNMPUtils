package me.songfei.snmputils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by southwolf on 26/05/2017.
 */
public class Main {
    public static void main(String[] args) {
//        final String host = "127.0.0.1";
//        final String host = "192.168.199.212";

        final String host = "192.168.2.85";
        final String c = "public";
        int net_id = 0;
        System.out.println("IP: " + host);
        System.out.println("CPU 型号: " + SysInfoService.getCPUName(host, c));
        System.out.println("CPU 核心数量: " + SysInfoService.getCPUCoreNumber(host, c));
        System.out.println("CPU 利用率: " + SysInfoService.getCPUPercent(host, c) + " %");
        System.out.println("内存: " + SysInfoService.getMemTotal(host, c));

        System.out.println("磁盘容量: " + SysInfoService.getDiskTotal(host, c));
        System.out.println("磁盘利用率: " + SysInfoService.getDiskUsedPercent(host, c) + " %");

        System.out.println("网卡列表:");
        List<NetworkInterface> netList = SysInfoService.getNetList(host, c);
        NetworkInterface network = null;

        if (netList == null) {
            return;
        }

        for (NetworkInterface net : netList) {
            System.out.println("ID: " + net.getId() + ", NAME: " + net.getName() + " IP: " + net.getIp());
            if (net.getIp().equals(host)) {
                network = net;
            }
        }

        final long bits[] = {0, 0};
        final NetworkInterface targetNetwork = network;
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("ID: " + targetNetwork.getId());
                System.out.println("IP: " + targetNetwork.getIp());
                long in_new = targetNetwork.getInboundBit();
                long out_new = targetNetwork.getOutboundBit();
                long delta_in = in_new - bits[0];
                long delta_out = out_new - bits[1];
                System.out.println("IN: " + delta_in / 8 / 1024.0 + " kbps");
                System.out.println("OUT: " + delta_out / 8 / 1024.0 + " kbps");
                bits[0] = in_new;
                bits[1] = out_new;
            }
        }, 0, 10000);
    }
}
