package me.songfei.snmputils;

/**
 * Created by southwolf on 26/05/2017.
 */
public class NetworkInterface implements Comparable<NetworkInterface> {
    private int id = 0;
    private String name = null;
    private String mac = null;
    private String ip = null;
    private long inbound_bit = 0l;
    private long outbound_bit = 0l;

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