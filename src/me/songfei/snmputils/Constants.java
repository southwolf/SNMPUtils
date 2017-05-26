package me.songfei.snmputils;

import org.snmp4j.smi.OID;

/**
 * Created by southwolf on 25/05/2017.
 */
public class Constants {
    static final OID sys_name = new OID("1.3.6.1.2.1.1");
    static final OID sys_desc = new OID("1.3.6.1.2.1.25.3.2.1.3");
    static final OID sys_tree = new OID("1.3.6.1.2.1.25.3.2.1");
    static final OID hr_type_processor = new OID("1.3.6.1.2.1.25.3.1.3");

    static final OID processor_table = new OID("1.3.6.1.2.1.25.3.3");
    static final OID cpu_load = new OID("1.3.6.1.2.1.25.3.3.1.2");
    static final OID one_min_load = new OID("1.3.6.1.4.1.2021.10.1.3.1");
    static final OID five_min_load = new OID("1.3.6.1.4.1.2021.10.1.3.2");
    static final OID fifteen_min_load = new OID("1.3.6.1.4.1.2021.10.1.3.3");
    static final OID cpu_idle_time = new OID("1.3.6.1.4.1.2021.11.11.0");

    static final OID total_ram_free = new OID("1.3.6.1.4.1.2021.4.11.0");

    static final OID available_space = new OID("1.3.6.1.4.1.2021.9.1.7.1");
    static final OID used_space = new OID("1.3.6.1.4.1.2021.9.1.8.1");
    static final OID disk_percent = new OID("1.3.6.1.4.1.2021.9.1.9.1");
    static final OID disk_total = new OID("1.3.6.1.4.1.2021.9.1.6.1");

    static final OID net_tree = new OID("1.3.6.1.2.1.2.2.1");
    static final OID if_descr = new OID("1.3.6.1.2.1.2.2.1.2");
    static final OID if_in_octets = new OID("1.3.6.1.2.1.2.2.1.10");
    static final OID if_out_octets = new OID("1.3.6.1.2.1.2.2.1.16");
    static final OID ip_addr_entry = new OID("1.3.6.1.2.1.4.20.1");
    static final OID ip_addr_index = new OID("1.3.6.1.2.1.4.20.1.2");

    static final OID storage_tree = new OID("1.3.6.1.2.1.25.2");
    static final OID hr_storage_fixed_disk = new OID("1.3.6.1.2.1.25.2.1.4");
    static final OID hr_storage_index = new OID("1.3.6.1.2.1.25.2.3.1.2");
    static final OID hr_storage_size = new OID("1.3.6.1.2.1.25.2.3.1.5");
    static final OID hr_storage_used = new OID("1.3.6.1.2.1.25.2.3.1.6");
    static final OID hr_storage_allocation_unit = new OID("1.3.6.1.2.1.25.2.3.1.4");


}
