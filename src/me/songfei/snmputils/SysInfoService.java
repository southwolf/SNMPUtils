package me.songfei.snmputils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OID;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by southwolf on 24/05/2017.
 */
public class SysInfoService {
    private static Logger logger = LoggerFactory.getLogger(SysInfoService.class);

    private static SNMPNodeService service;
    private static String result = null;

    public static class NetworkInterface implements Comparable<NetworkInterface> {
        private Integer id;
        private String name;
        private String mac;
        private String ip;
        private BigInteger inbound_bit;
        private BigInteger outbound_bit;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public BigInteger getInboundBit() {
            return inbound_bit;
        }

        public void setInboundBit(BigInteger inbound_bit) {
            this.inbound_bit = inbound_bit;
        }

        public BigInteger getOutboundBit() {
            return outbound_bit;
        }

        public void setOutboundBit(BigInteger outbound_bit) {
            this.outbound_bit = outbound_bit;
        }

        public String toString() {
            return this.id + ": " + this.name +
                   ", IN: " + this.inbound_bit +
                   ", OUT: " + this.outbound_bit +
                   ", IP: " + this.ip;
        }

        @Override
        public int compareTo(NetworkInterface other) {
            return this.id - other.getId();
        }

    }

    private String get(String address, OID oid) {
        try {
            service = new SNMPNodeService(address);
            result = service.getAsString(oid);
            service.stop();
            return result;
        } catch (IOException e) {
            logger.debug("Cannot get SNMP info: ", e);
            return null;
        }
    }

    public static String getCPULoad_1(String address) {
        return new SysInfoService().get(address, Constants.one_min_load);
    }

    public static String getCPULoad_5(String address) {
        return new SysInfoService().get(address, Constants.five_min_load);
    }

    public static String getCPULoad_15(String address) {
        return new SysInfoService().get(address, Constants.fifteen_min_load);

    }

    public static String getMemFree(String address) {
        return new SysInfoService().get(address, Constants.total_ram_free);
    }

    public static String getDiskFree(String address) {
        return formatSize(new SysInfoService().get(address, Constants.available_space));
    }

    public static String getDiskUsed(String address) {
        return formatSize(new SysInfoService().get(address, Constants.used_space));
    }

    public static String getDiskUsedPercent(String address) {
        return new SysInfoService().get(address, Constants.disk_percent);
    }


    public static HashMap<String, String> getSysDesc(String address) {
        return walk(address, Constants.sys_tree);
    }

    public static HashMap<String, String> getNetDesc(String address) {
        return walk(address, Constants.net_tree);
    }

    public static HashMap<String, String> getIpTree(String address) {
        return walk(address, Constants.ip_addr_entry);
    }

    public static String getCPUName(String address) {
        HashMap<String, String> values = getSysDesc(address);

        if (values == null || values.size() == 0) {
            return null;
        }
        for(String key : values.keySet()) {
            if (values.get(key).equals(Constants.hr_type_processor.toString())) {
                String cpu_id = key.substring(key.lastIndexOf('.')+1);
                return values.get(Constants.sys_desc.toString() + "." + cpu_id);
            }
        }
        return null;
    }

    public static Integer getCPUCoreNumber(String address) {
        return walk(address, Constants.cpu_load).size();
    }

    public static Integer getCPUPercent(String address) {
        String idle = new SysInfoService().get(address, Constants.cpu_idle_time);
        if (idle == null || idle.equals("noSuchObject")) {
            return null;
        } else {
            return 100 - Integer.valueOf(idle);
        }
    }

    public static String getDiskTotal(String address) {
        return formatSize(new SysInfoService().get(address, Constants.disk_total));
    }

    public static List<NetworkInterface> getNetList(String address) {
        HashMap<String, String> values = getNetDesc(address);
        HashMap<String, String> ip_values = getIpTree(address);
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();

        if (values == null || values.size() == 0) {
            return null;
        }

        for(String key : values.keySet()) {
            if (key.startsWith(Constants.if_descr.toString() + ".")) {
                NetworkInterface net = new NetworkInterface();

                net.setId(Integer.valueOf(key.substring(key.lastIndexOf('.')+1)));
                net.setName(values.get(key));
                net.setInboundBit(new BigInteger(values.get(Constants.if_in_octets + "." + net.getId())));
                net.setOutboundBit(new BigInteger(values.get(Constants.if_out_octets + "." + net.getId())));
                networkInterfaces.add(net);
            }
        }

        for(String key : ip_values.keySet()) {
            if (key.startsWith(Constants.ip_addr_index.toString())) {
                for(NetworkInterface net : networkInterfaces) {
                    if (ip_values.get(key).equals(net.getId().toString())) {
                        net.setIp(key.replace(Constants.ip_addr_index.toString() + ".", ""));
                    }
                }
            }
        }
        Collections.sort(networkInterfaces);
        return networkInterfaces;
    }

    public static HashMap<String, String> walk(String address, OID startOid) {
        try {
            return new SNMPNodeService(address).walk(startOid);
        } catch (IOException e) {
            logger.debug("SNMP walk error: ", e);
            return null;
        }

    }

    private static String formatSize(String size) {
        if (size == null || size.equals("noSuchInstance")) {
            return null;
        }

        Integer num = Integer.valueOf(size);

        if (num > 1073741823) {
            return String.format("%.2f TB", num / 1073741824.0);
        }
        if (num > 1048575) {
            return String.format("%.2f GB", num / 1048576.0);
        }
        if (num > 1023) {
            return String.format("%.2f MB", num / 1024.0);
        }
        return size + " KB";
    }
}
