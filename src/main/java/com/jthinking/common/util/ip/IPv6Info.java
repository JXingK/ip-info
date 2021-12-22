package com.jthinking.common.util.ip;

import java.math.BigInteger;

public class IPv6Info extends IPInfo implements Comparable<IPv6Info> {

    /**
     * 起始IP数字表示
     */
    private BigInteger start;

    /**
     * 终止IP数字表示
     */
    private BigInteger end;

    public IPv6Info(BigInteger start, BigInteger end) {
        this.start = start;
        this.end = end;
    }

    public IPv6Info(BigInteger start, BigInteger end, String country, String province, String address, String isp, boolean overseas, double lat, double lng) {
        super(country, province, address, isp, overseas, lat, lng);
        this.start = start;
        this.end = end;
    }

    public BigInteger getStart() {
        return start;
    }

    public void setStart(BigInteger start) {
        this.start = start;
    }

    public BigInteger getEnd() {
        return end;
    }

    public void setEnd(BigInteger end) {
        this.end = end;
    }

    @Override
    public int compareTo(IPv6Info o) {
        if (this.start.equals(o.start) || this.start.equals(o.end) || this.end.equals(o.start) || this.end.equals(o.end)) {
            return 0;
        } else if (this.start.compareTo(o.end) > 0) {
            return 1;
        } else if (this.end.compareTo(o.start) < 0) {
            return -1;
        } else if (this.start.compareTo(o.start) > 0 && this.end.compareTo(o.end) < 0) {
            return 0;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "IPv6Info{" +
                "start=" + start +
                ", end=" + end +
                "} " + super.toString();
    }
}