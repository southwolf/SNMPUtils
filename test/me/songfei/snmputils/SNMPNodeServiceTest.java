package me.songfei.snmputils;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.snmp4j.smi.OID;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * Created by southwolf on 24/05/2017.
 */
public class SNMPNodeServiceTest {
    static final OID sysDescr = new OID(".1.3.6.1.2.1.1.1.0");
    static final OID interfacesTable = new OID(".1.3.6.1.2.1.2.2.1");
    static final OID mem = new OID(".1.3.6.1.2.1.25.2.2.0");
    static final OID disk = new OID(".1.3.6.1.4.1.2021.9.1.7.1");

    static SNMPNodeService service;

    @BeforeClass
    public static void setUp() throws Exception {
        service = new SNMPNodeService("192.168.199.200", "public");
    }

    @AfterClass
    public static void tearDown() throws IOException {
        service.stop();
    }

    @Test
    public void testSystemDescription() throws IOException {
        assertNotNull(service.getAsString(sysDescr));
    }

    @Test
    public void testMem() throws IOException {
        assertNotNull(service.getAsString(mem));
    }

    @Test
    public void testDisk() throws IOException {
        assertNotNull(service.getAsString(disk));
    }
}
