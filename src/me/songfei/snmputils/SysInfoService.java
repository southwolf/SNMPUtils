package me.songfei.snmputils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OID;

import java.io.IOException;
import java.util.*;

/**
 * Created by southwolf on 24/05/2017.
 */
public class SysInfoService {
    private static Logger logger = LoggerFactory.getLogger(SysInfoService.class);

    private static SNMPNodeService service;
    private static String result = null;

    public static class NetworkInterface implements Comparable<NetworkInterface> {
        private int id;
        private String name;
        private String mac;
        private String ip;
        private long inbound_bit;
        private long outbound_bit;

        public int getId() {
            return id;
        }

        public void setId(int id) {
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

        public String getIp()
        {
            if(ip == null) {
                return "null";
            } else {
                return ip;
            }
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public long getInboundBit() {
            return inbound_bit;
        }

        public void setInboundBit(long inbound_bit) {
            this.inbound_bit = inbound_bit;
        }

        public long getOutboundBit() {
            return outbound_bit;
        }

        public void setOutboundBit(long outbound_bit) {
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

    private String get(String address, String community, OID oid) {
        try {
            service = new SNMPNodeService(address, community);
            result = service.getAsString(oid);
            service.stop();
            return result;
        } catch (IOException e) {
            logger.debug("Cannot get SNMP info: ", e);
            return null;
        }
    }

    public static String getCPULoad_1(String address, String community) {
        return new SysInfoService().get(address, community, Constants.one_min_load);
    }

    public static String getCPULoad_5(String address, String community) {
        return new SysInfoService().get(address, community, Constants.five_min_load);
    }

    public static String getCPULoad_15(String address, String community) {
        return new SysInfoService().get(address, community, Constants.fifteen_min_load);

    }

    public static String getDiskUsedPercent(String address, String community) {
        String result = new SysInfoService().get(address, community, Constants.disk_percent);
        if(result == null || result == "noSuchObject") {
            result = String.valueOf(Long.valueOf(getDiskUsed_raw(address, community)) * 100 / Long.valueOf(getDiskTotal_raw(address, community)));
        }
        return result;
    }


    public static Map<String, String> getSysDesc(String address, String community) {
        return walk(address, community, Constants.sys_tree);
    }

    public static Map<String, String> getNetDesc(String address, String community) {
        return walk(address, community, Constants.net_tree);
    }

    public static Map<String, String> getIpTree(String address, String community) {
        return walk(address, community, Constants.ip_addr_entry);
    }

    public static Map<String, String> getCPUTree(String address, String community) {
        return walk(address, community, Constants.cpu_load);
    }

    public static Integer getCPUCoreNumber(String address, String community) {
        return getCPUTree(address, community).size();
    }

    public static String getCPUName(String address, String community) {
        Map<String, String> values = getSysDesc(address, community);

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

    public static Map<String, String> getCPULoads(String address, String community) {
        Map<String, String> values = getCPUTree(address, community);
        Map<String, String> temp = new TreeMap<String, String>(values);
        Map<String, String> result = new TreeMap<String, String>();
        for(int i = 0; i < temp.size(); i++) {
            String key = (String) temp.keySet().toArray()[i];
            result.put(String.valueOf(i), temp.get(key));
        }

        return result;
    }

    public static double getCPUPercent(String address, String community) {
        String idle = new SysInfoService().get(address, community, Constants.cpu_idle_time);
        if (idle == null || idle.equals("noSuchObject")) {
            return getCPUPercent_win(address, community);
        } else {
            return (double)(100 - Integer.valueOf(idle));
        }
    }

    public static double getCPUPercent_win(String address, String community) {
        Map<String, String> loads = getCPULoads(address, community);
        double percent = 0.0;

        if(loads == null || loads.size() == 0) {
            return 0.0;
        }

        for(String i : loads.keySet()) {
            percent += Double.parseDouble(loads.get(i));
        }

        return percent / loads.size();
    }


    public static String getDiskTotal_raw(String address, String community) {
        String result = new SysInfoService().get(address, community, Constants.disk_total);
        if(result == null || result.equals("noSuchObject")) {
            result = getDiskTotal_win(address, community);
        }
        return result;
    }

    public static String getDiskTotal(String address, String community) {
        return formatSize(getDiskTotal_raw(address, community));
    }

    public static String getDiskTotal_win(String address, String community) {
        Map<String, String> values = SysInfoService.walk(address, community, Constants.storage_tree);
        Map<String, String> result = new HashMap<String, String>();
        if(values == null || values.size() == 0) {
            return null;
        }
        for(String key : values.keySet()) {
            if(key.startsWith(Constants.hr_storage_index.toString() + ".")) {
                if(values.get(key).equals(Constants.hr_storage_fixed_disk.toString())) {
                    String disk_id = key.substring(key.lastIndexOf('.')+1);
                    result.put(
                        values.get(Constants.hr_storage_size.toString() + "." + disk_id),
                        values.get(Constants.hr_storage_allocation_unit.toString() + "." + disk_id)
                    );
                }
            }
        }
        long sum = 0;
        for(String key : result.keySet()) {
            sum += Long.valueOf(result.get(key)) * Long.valueOf(key);
        }
        return String.valueOf(sum / 1024);
    }


    public static String getDiskUsed_raw(String address, String community) {
        String result = new SysInfoService().get(address, community, Constants.used_space);
        if(result == null || result.equals("noSuchObject")) {
            result = getDiskUsed_win(address, community);
        }
        return result;
    }

    public static String getDiskUsed(String address, String community) {
        return formatSize(getDiskUsed_raw(address, community));
    }

    public static String getDiskUsed_win(String address, String community) {
        Map<String, String> values = SysInfoService.walk(address, community, Constants.storage_tree);
        Map<String, String> result = new HashMap<String, String>();
        if(values == null || values.size() == 0) {
            return null;
        }
        for(String key : values.keySet()) {
            if(key.startsWith(Constants.hr_storage_index.toString() + ".")) {
                if(values.get(key).equals(Constants.hr_storage_fixed_disk.toString())) {
                    String disk_id = key.substring(key.lastIndexOf('.')+1);
                    result.put(
                            values.get(Constants.hr_storage_used.toString() + "." + disk_id),
                            values.get(Constants.hr_storage_allocation_unit.toString() + "." + disk_id)
                    );
                }
            }
        }
        long sum = 0;
        for(String key : result.keySet()) {
            sum += Long.valueOf(result.get(key)) * Long.valueOf(key);
        }
        return String.valueOf(sum / 1024);
    }

    public static String getDiskFree_raw(String address, String community) {
        String result = new SysInfoService().get(address, community, Constants.available_space);
        long result_l = 0l;
        if(result == null || result.equals("noSuchObject")) {
            result_l = Long.valueOf(getDiskTotal_raw(address, community)) - Long.valueOf(getDiskUsed_raw(address, community));
        }
        result = String.valueOf(result_l);
        return result;
    }

    public static String getDiskFree(String address, String community) {
        return formatSize(getDiskFree_raw(address, community));
    }

    public static String getMemTotal(String address, String community) {
        return formatSize(getMemTotal_raw(address, community));
    }


    public static String getMemTotal_raw(String address, String community) {
        String result = new SysInfoService().get(address, community, Constants.ram_size);
        if(result == null || result.equals("noSuchObject")) {
            result = getMemTotal_win(address, community);
        }
        return result;
    }

    public static String getMemTotal_win(String address, String community) {
        Map<String, String> values = SysInfoService.walk(address, community, Constants.storage_tree);
        if(values == null || values.size() == 0) {
            return null;
        }
        for(String key : values.keySet()) {
            if(key.startsWith(Constants.hr_storage_index.toString() + ".")) {
                if(values.get(key).equals(Constants.hr_storage_ram.toString())) {
                    String mem_id = key.substring(key.lastIndexOf('.')+1);
                    long result = Long.valueOf(values.get(Constants.hr_storage_size.toString() + "." + mem_id)) *
                            Long.valueOf(values.get(Constants.hr_storage_allocation_unit.toString() + "." + mem_id)) / 1024;
                    return String.valueOf(result);
                }
            }
        }
        return null;
    }

    public static String getMemFree(String address, String community) {
        return formatSize(getMemFree_raw(address, community));
    }


    public static String getMemFree_raw(String address, String community) {
        String result = new SysInfoService().get(address, community, Constants.total_ram_free);
        long result_l = 0l;
        if(result == null || result.equals("noSuchObject")) {
            result_l = Long.valueOf(getMemTotal_raw(address, community)) - Long.valueOf(getMemUsed_raw(address, community));
        }
        result = String.valueOf(result_l);
        return result;
    }

    public static String getMemUsed(String address, String community) {
        return formatSize(getMemUsed_raw(address, community));
    }


    public static String getMemUsed_raw(String address, String community) {
        String result = new SysInfoService().get(address, community, Constants.ram_size);
        if(result == null || result.equals("noSuchObject")) {
            result = getMemTotal_win(address, community);
        }
        return result;
    }

    public static String getMemUsed_win(String address, String community) {
        Map<String, String> values = SysInfoService.walk(address, community, Constants.storage_tree);
        if(values == null || values.size() == 0) {
            return null;
        }
        for(String key : values.keySet()) {
            if(key.startsWith(Constants.hr_storage_index.toString() + ".")) {
                if(values.get(key).equals(Constants.hr_storage_ram.toString())) {
                    String mem_id = key.substring(key.lastIndexOf('.')+1);
                    long result = Long.valueOf(values.get(Constants.hr_storage_used.toString() + "." + mem_id)) *
                            Long.valueOf(values.get(Constants.hr_storage_allocation_unit.toString() + "." + mem_id)) / 1024;
                    return String.valueOf(result);
                }
            }
        }
        return null;
    }

    public static List<NetworkInterface> getNetList(String address, String community) {
        Map<String, String> values = getNetDesc(address, community);
        Map<String, String> ip_values = getIpTree(address, community);
        List<NetworkInterface> networkInterfaces = new ArrayList<NetworkInterface>();

        if (values == null || values.size() == 0) {
            return null;
        }

        for(String key : values.keySet()) {
            if (key.startsWith(Constants.if_descr.toString() + ".")) {
                NetworkInterface net = new NetworkInterface();

                net.setId(Integer.valueOf(key.substring(key.lastIndexOf('.')+1)));
                net.setName(parseName(values.get(key)));
                net.setInboundBit(Long.parseLong(values.get(Constants.if_in_octets + "." + net.getId())));
                net.setOutboundBit(Long.parseLong(values.get(Constants.if_out_octets + "." + net.getId())));
                networkInterfaces.add(net);
            }
        }

        for(String key : ip_values.keySet()) {
            if (key.startsWith(Constants.ip_addr_index.toString())) {
                for(NetworkInterface net : networkInterfaces) {
                    if (ip_values.get(key).equals(String.valueOf(net.getId()))) {
                        net.setIp(key.replace(Constants.ip_addr_index.toString() + ".", ""));
                    }
                }
            }
        }
        Collections.sort(networkInterfaces);
        return networkInterfaces;
    }

    public static Map<String, String> walk(String address, String community, OID startOid) {
        try {
            return new SNMPNodeService(address, community).walk(startOid);
        } catch (IOException e) {
            logger.debug("SNMP walk error: ", e);
            return null;
        }

    }

    private static String formatSize(String size) {
        if (size == null || size.equals("noSuchInstance") || size.equals("noSuchObject")) {
            return null;
        }

        Long num = Long.valueOf(size);

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

    private static String parseName(String name) {
        if(name == null || name.isEmpty()) {
            return "";
        }
        if(Character.isDigit(name.charAt(0))) {
            String[] byte_array = name.split(":");
            byte[] bytes = new byte[1000];
            int i = 0;
            for (String b : byte_array) {
                if (b.equals("00")) {
                    bytes[i] = 0;
                    break;
                }
                bytes[i] = (byte) Integer.parseInt(b, 16);
                i += 1;
            }

            return new String(bytes).trim();
        }
        return name;
    }
}
