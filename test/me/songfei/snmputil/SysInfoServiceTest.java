package me.songfei.snmputil;

import me.songfei.snmputils.SysInfoService;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by southwolf on 25/05/2017.
 */
public class SysInfoServiceTest {

    @Test
    public void testGetCPULoad_1() throws IOException {
        assertNotNull(SysInfoService.getCPULoad_1("127.0.0.1"));
    }

    @Test
    public void testGetCPULoad_5() throws IOException {
        assertNotNull(SysInfoService.getCPULoad_5("127.0.0.1"));
    }

    @Test
    public void testGetCPULoad_15() throws IOException {
        assertNotNull(SysInfoService.getCPULoad_15("127.0.0.1"));
    }

    @Test
    public void testGetMemFree() throws IOException {
        assertNotNull(SysInfoService.getMemFree("127.0.0.1"));
    }

    @Test
    public void testGetDiskFree() throws IOException {
        assertNotNull(SysInfoService.getDiskFree("127.0.0.1"));
    }

    @Test
    public void testGetDiskUsed() throws IOException {
        assertNotNull(SysInfoService.getDiskUsed("127.0.0.1"));
    }

    @Test
    public void testGetDiskUsedPercent() throws IOException {
        assertNotNull(SysInfoService.getDiskUsedPercent("127.0.0.1"));
    }

    @Test
    public void testGetCPUName() throws IOException {
        assertNull(SysInfoService.getCPUName("127.0.0.1"));
    }

    @Test
    public void testGetCPUNumber() throws  IOException {
        assertNull(SysInfoService.getCPUCoreNumber("127.0.0.1"));
    }

    @Test
    public void testGetCPUPercent() throws  IOException {
        assertNotNull(SysInfoService.getCPUPercent("127.0.0.1"));
    }
}
