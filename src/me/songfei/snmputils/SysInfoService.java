package me.songfei.snmputils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.OID;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by southwolf on 24/05/2017.
 */
public class SysInfoService {
    private static Logger logger = LoggerFactory.getLogger(SysInfoService.class);

    private static final OID sys_name = new OID("1.3.6.1.2.1.1");
    private static final OID sys_desc = new OID("1.3.6.1.2.1.25.3.2.1.3");
    private static final OID sys_tree = new OID("1.3.6.1.2.1.25.3.2.1");
    private static final OID hr_type_processor = new OID("1.3.6.1.2.1.25.3.1.3");

    private static final OID processor_table = new OID("1.3.6.1.2.1.25.3.3");
    private static final OID cpu_load = new OID("1.3.6.1.2.1.25.3.3.1.2");
    private static final OID cpu_load_linux = new OID("1.3.6.1.2.1.25.3.3.1.2.196608");
    private static final OID one_min_load = new OID("1.3.6.1.4.1.2021.10.1.3.1");
    private static final OID five_min_load = new OID("1.3.6.1.4.1.2021.10.1.3.2");
    private static final OID fifteen_min_load = new OID("1.3.6.1.4.1.2021.10.1.3.3");
    private static final OID cpu_idle_time = new OID("1.3.6.1.4.1.2021.11.11.0");

    private static final OID total_ram_free = new OID("1.3.6.1.4.1.2021.4.11.0");

    private static final OID available_space = new OID("1.3.6.1.4.1.2021.9.1.7.1");
    private static final OID used_space = new OID("1.3.6.1.4.1.2021.9.1.8.1");
    private static final OID disk_percent = new OID("1.3.6.1.4.1.2021.9.1.9.1");

    private static SNMPNodeService service;
    private static String result = null;

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
        return new SysInfoService().get(address, one_min_load);
    }

    public static String getCPULoad_5(String address) {
        return new SysInfoService().get(address, five_min_load);
    }

    public static String getCPULoad_15(String address) {
        return new SysInfoService().get(address, fifteen_min_load);

    }

    public static String getMemFree(String address) {
        return new SysInfoService().get(address, total_ram_free);
    }

    public static String getDiskFree(String address) {
        return formatSize(new SysInfoService().get(address, available_space));
    }

    public static String getDiskUsed(String address) {
        return formatSize(new SysInfoService().get(address, used_space));
    }

    public static String getDiskUsedPercent(String address) {
        return new SysInfoService().get(address, disk_percent);
    }


    public static HashMap<String, String> getSysDesc(String address) {
        return SysInfoService.walk(address, sys_tree);
    }

    public static String getCPUName(String address) {
        HashMap<String, String> values = getSysDesc(address);

        if(values == null || values.size() == 0) {
            return null;
        }
        for(String key : values.keySet()) {
            if (values.get(key).equals(hr_type_processor.toString())) {
                String cpu_id = key.substring(key.lastIndexOf('.')+1);
                return values.get(sys_desc.toString() + "." + cpu_id);
            }
        }
        return null;
    }

    public static Integer getCPUCoreNumber(String address) {
        return SysInfoService.walk(address, cpu_load).size();
    }

    public static Integer getCPUPercent(String address) {
        String idle = new SysInfoService().get(address, cpu_idle_time);
        if(idle == null || idle == "noSuchObject") {
            return null;
        } else {
            return 100 - Integer.valueOf(idle);
        }
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
        if(size == null) {
            return null;
        }

        Integer num = Integer.valueOf(size);

        if(num > 1073741823) {
            return String.format("%.2f TB", num / 1073741824.0);
        }
        if(num > 1048575) {
            return String.format("%.2f GB", num / 1048576.0);
        }
        if(num > 1023) {
            return String.format("%.2f MB", num / 1024.0);
        }
        return size + " KB";
    }
}
