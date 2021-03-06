package me.songfei.snmputils;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

/**
 * Created by southwolf on 25/05/2017.
 */
public class SysInfoServiceTest {

//    String ip = "192.168.199.212";
//    String ip = "127.0.0.1";
    String ip = "192.168.199.200";
    String community = "public";

    @Test
    public void testGetCPULoad_1() throws IOException {
        assertNotNull(SysInfoService.getCPULoad_1(ip, community));
    }

    @Test
    public void testGetCPULoad_5() throws IOException {
        assertNotNull(SysInfoService.getCPULoad_5(ip, community));
    }

    @Test
    public void testGetCPULoad_15() throws IOException {
        assertNotNull(SysInfoService.getCPULoad_15(ip, community));
    }

    @Test
    public void testGetMemFree() throws IOException {
        assertNotNull(SysInfoService.getMemFree(ip, community));
    }

    @Test
    public void testGetMemUsed() throws IOException {
        assertNotNull(SysInfoService.getMemUsed(ip, community));
    }

    @Test
    public void testGetMemUsedPercent() throws IOException {
        assertNotNull(SysInfoService.getMemUsedPercent(ip, community));
    }

    @Test
    public void testGetMemTotal() throws IOException {
        assertNotNull(SysInfoService.getMemTotal(ip, community));
    }

    @Test
    public void testGetDiskFree() throws IOException {
        assertNotNull(SysInfoService.getDiskFree(ip, community));
    }

    @Test
    public void testGetDiskUsed() throws IOException {
        assertNotNull(SysInfoService.getDiskUsed(ip, community));
    }

    @Test
    public void testGetDiskUsedPercent() throws IOException {
        assertNotNull(SysInfoService.getDiskUsedPercent(ip, community));
    }

    @Test
    public void testGetCPUName() throws IOException {
        assertNotNull(SysInfoService.getCPUName(ip, community));
    }

    @Test
    public void testGetCPUPercent() throws  IOException {
        assertNotNull(SysInfoService.getCPUPercent(ip, community));
    }

    @Test
    public void testGetCPUNumber() throws  IOException {
        assertNotNull(SysInfoService.getCPUCoreNumber(ip, community));
    }

    @Test
    public void testGetDiskTotal() throws  IOException {
        assertNotNull(SysInfoService.getDiskTotal(ip, community));
    }

    @Test
    public void testGetNetList() throws  IOException {
        assertNotNull(SysInfoService.getNetList(ip, community));
    }

    @Test
    public void testGetCPULoads() throws IOException {
        assertNotNull(SysInfoService.getCPULoads(ip, community));
    }
}
