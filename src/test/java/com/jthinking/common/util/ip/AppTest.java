package com.jthinking.common.util.ip;

import org.junit.Test;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static com.jthinking.common.util.ip.parser.IpParser.*;

/**
 *
 * 功能列表
 * 1. 通过IPv4地址查询IP信息【已完成】
 * 2. 通过IPv6地址查询IP信息
 * 3. 加载给定的多个IPv4段，判断给定IPv4地址是否在给定范围内
 * 4. 加载给定的多个IPv6段，判断给定IPv6地址是否在给定范围内
 * 5. IPv4和IPv6地址的各种转换操作
 */
public class AppTest {



    @Test
    public void readFile2() {
        IPInfo ipInfo1 = IPInfoUtils.getIpInfo("222.128.176.102");
        IPInfo ipInfo2 = IPInfoUtils.getIpInfo("192.168.1.1");
        IPInfo ipInfo3 = IPInfoUtils.getIpInfo("240E:0378:65FF:FFFF::");
        IPInfo ipInfo4 = IPInfoUtils.getIpInfo("::1");
        IPInfo ipInfo5 = IPInfoUtils.getIpInfo("2409:807e:2001:1107:0:0:0:4");
        IPInfo ipInfo6 = IPInfoUtils.getIpInfo("2606:4700:50:0:0:0:adf5:3a5c");
        IPInfo ipInfo7 = IPInfoUtils.getIpInfo("2400:3200:2000:49:0:0:0:1");
        IPInfo ipInfo8 = IPInfoUtils.getIpInfo("2600:9000:5307:4100:0:0:0:1");
        IPInfo ipInfo9 = IPInfoUtils.getIpInfo("2400:cb00:2049:1:0:0:adf5:3a05");
        IPInfo ipInfo10 = IPInfoUtils.getIpInfo("2803:f800:50:0:0:0:6ca2:c04b");
        IPInfo ipInfo11 = IPInfoUtils.getIpInfo("2409:8900:1b01:a781:aa2e:47f8:36f9:5df2");

        System.out.println(ipInfo1);
        System.out.println(ipInfo2);
        System.out.println(ipInfo3);
        System.out.println(ipInfo4);
        System.out.println(ipInfo5);
        System.out.println(ipInfo6);
        System.out.println(ipInfo7);
        System.out.println(ipInfo8);
        System.out.println(ipInfo9);
        System.out.println(ipInfo10);
        System.out.println(ipInfo11);
        System.out.println(IPInfoUtils.getIpInfo("218.207.231.133"));

    }

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {

        IPInfo ipInfo = IPInfoUtils.getIpInfo("222.128.176.102");
        if (ipInfo != null) {
            System.out.println(ipInfo.getCountry()); // 国家中文名称
            System.out.println(ipInfo.getProvince()); // 中国省份中文名称
            System.out.println(ipInfo.getAddress()); // 详细地址
            System.out.println(ipInfo.getIsp()); // 互联网服务提供商
            System.out.println(ipInfo.isOverseas()); // 是否是国外
            System.out.println(ipInfo.getLat()); // 纬度
            System.out.println(ipInfo.getLng()); // 经度
        }


        for (int i = 0; i < 1; i++) {
            long s = System.currentTimeMillis();
            System.out.println(IPInfoUtils.getIpInfo("54.213.132." + i));
            System.out.println(System.currentTimeMillis() - s);


        }
    }



    @Test
    public void testIPv6() throws Exception {

        String s = "240E09789C000000\t240E09789CFFFFFF";



        BigInteger integer1 = new BigInteger("240E09789C0000000000000000000000", 16);
        BigInteger integer2 = new BigInteger("240E09789CFFFFFF0000000000000000", 16);

        System.out.println(integer1);
        System.out.println(integer2);

        InetAddress byAddress1 = InetAddress.getByAddress(integer1.toByteArray());
        System.out.println(byAddress1.toString());
        InetAddress byAddress2 = InetAddress.getByAddress(integer2.toByteArray());
        System.out.println(byAddress2.toString());


        System.out.println(getIPv6BigInteger("240e:978:9c00::"));
        System.out.println(new BigInteger(ipv6ToBytes("240e:978:9cff:ffff:0:0:0:0")));

        String string = Integer.toHexString(-144);
        System.out.println(string);
        System.out.println(getFullIPv6("240e:978:9c00::"));

        byte[] bytes1 = intToByte4(-534565535);


    }

    @Test
    public void testCase() {

        // 3d 87 a9 79
        // 61.135.169.121

        // fe 80 00 00 00 00 00 00 18 91 06 4d f8 54 7a 9d
        // fe80::1891:64d:f854:7a9d

        int i = Integer.parseUnsignedInt("3d87a979", 16);

        List<Integer> ipv4 = new ArrayList<>();
        ipv4.add(i);

        int i1 = Integer.parseUnsignedInt("fe800000", 16);
        int i2 = Integer.parseUnsignedInt("00000000", 16);
        int i3 = Integer.parseUnsignedInt("1891064d", 16);
        int i4 = Integer.parseUnsignedInt("f8547a9d", 16);

        List<Integer> ipv6 = new ArrayList<>();
        ipv6.add(i1);
        ipv6.add(i2);
        ipv6.add(i3);
        ipv6.add(i4);

        String s = parseIP(ipv4);
        String ss = parseIP(ipv6);

        System.out.println(s);
        System.out.println(ss);
        System.out.println(formatIPv6(ss));
    }

    @Test
    public void test000() {
        //byte[] bytes = hexStringToByte("fe800000000000001891064df8547a9d");
        //byte[] bytes = hexStringToByte("fe8000000000000069e43dd600000da6");
        byte[] bytes = hexStringToByte("ff020000000000000000000000000000");
        //byte[] bytes = hexStringToByte("00000000000000000000000000000001");
        String shortIPv6 = getShortIPv6(bytes, 0, bytes.length);

        System.out.println(shortIPv6);


    }

    @Test
    public void bytesToString() {

        String fullIPv6 = getFullIPv6("240e:978:9cff:ffff:0:0:0:0");
        byte[] bytes = hexStringToByte(fullIPv6.replaceAll(":", ""));
        String string = parseIPBytes(bytes, 0, bytes.length);
        System.out.println(string);
        System.out.println(getShortIPv6(bytes, 0, bytes.length));
        System.out.println(formatIPv6(string));


    }

    @Test
    public void ttt() {
        System.out.println(leftPadZero("9", 8));
    }


    @Test
    public void testIPv6_2() {
        double d = 27.42343;
        System.out.println(d);
        System.out.println((long)(d * 1000) / 1000.0);
    }


    @Test
    public void testExport() {
        IPInfo ipInfo1 = IPInfoUtils.getIpInfo("222.128.176.102");
        IPInfo ipInfo2 = IPInfoUtils.getIpInfo("192.168.1.1");
        IPInfo ipInfo3 = IPInfoUtils.getIpInfo("240E:0378:65FF:FFFF::");
    }


}
