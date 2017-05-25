package me.songfei.snmputil;

import me.songfei.snmputils.SysInfoService;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * Created by southwolf on 25/05/2017.
 */
public class SysInfoServiceTest {

    String ip = "192.168.199.212";
//String ip = "127.0.0.1";

    @Test
    public void testGetCPULoad_1() throws IOException {
        assertNotNull(SysInfoService.getCPULoad_1(ip));
    }

    @Test
    public void testGetCPULoad_5() throws IOException {
        assertNotNull(SysInfoService.getCPULoad_5(ip));
    }

    @Test
    public void testGetCPULoad_15() throws IOException {
        assertNotNull(SysInfoService.getCPULoad_15(ip));
    }

    @Test
    public void testGetMemFree() throws IOException {
        assertNotNull(SysInfoService.getMemFree(ip));
    }

    @Test
    public void testGetDiskFree() throws IOException {
        assertNotNull(SysInfoService.getDiskFree(ip));
    }

    @Test
    public void testGetDiskUsed() throws IOException {
        assertNotNull(SysInfoService.getDiskUsed(ip));
    }

    @Test
    public void testGetDiskUsedPercent() throws IOException {
        assertNotNull(SysInfoService.getDiskUsedPercent(ip));
    }

    @Test
    public void testGetCPUName() throws IOException {
        assertNotNull(SysInfoService.getCPUName(ip));
    }

    @Test
    public void testGetCPUPercent() throws  IOException {
        assertNotNull(SysInfoService.getCPUPercent(ip));
    }

    @Test
    public void testGetCPUNumber() throws  IOException {
        assertNotNull(SysInfoService.getCPUCoreNumber(ip));
    }

    @Test
    public void testGetDiskTotal() throws  IOException {
        assertNotNull(SysInfoService.getDiskTotal(ip));
    }
}
