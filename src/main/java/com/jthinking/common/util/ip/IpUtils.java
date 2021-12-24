package com.jthinking.common.util.ip;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class IpUtils {

    private static List<PrintIP.StartAndEnd> lanIpList = new ArrayList<>();

    private static final Pattern IP_V4_PATTERN = Pattern.compile("([0-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");

    static {
        List<String> ipRangeList = Arrays.asList("10.0.0.0-10.255.255.255", "172.16.0.0-172.31.255.255", "192.168.0.0-192.168.255.255");
        for (String ipRange : ipRangeList) {
            String[] split = ipRange.split("-");
            lanIpList.add(new PrintIP.StartAndEnd(IpUtils.ip2long(split[0]), IpUtils.ip2long(split[1])));
        }
    }

    public static boolean isLan(String ip) {
        if (ip == null) {
            return false;
        }
        //TODO ipv6支持。暂时忽略ipv6地址
        if (ip.contains(":")) {
            return false;
        }
        for (PrintIP.StartAndEnd se : lanIpList) {
            long ipLong = IpUtils.ip2long(ip);
            if (IpUtils.isInRange(se.getStart(), se.getEnd(), ipLong)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断给定字符串是否是合法的端口
     *
     * @param port 端口字符串
     * @return true：是合法端口、false：不是合法端口
     */
    public static boolean isPort(String port) {
        try {
            int p = Integer.parseInt(port);
            return isValidPort(p);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断给定数字是否是合法的端口（数字范围在0到65535之间）
     *
     * @param port 端口数字
     * @return true：是合法端口、false：不是合法端口
     */
    public static boolean isValidPort(int port) {
        return port >= 0 && port <= 65535;
    }

    /**
     * 判断给定字符串是否是合法的端口范围。格式：起始端口-终止端口
     *
     * @param portRange 端口字符串
     * @return true：是合法端口范围、false：不是合法端口范围
     */
    public static boolean isPortRange(String portRange) {
        if (!portRange.contains("-")) {
            return false;
        }
        String[] startAndEnd = portRange.split("-");
        if (startAndEnd.length != 2) {
            return false;
        }
        int startPort = Integer.parseInt(startAndEnd[0]);
        int endPort = Integer.parseInt(startAndEnd[1]);
        return isValidPort(startPort) && isValidPort(endPort) && startPort <= endPort;
    }


    /**
     * 判断端口是否在端口段中
     *
     * @param portsStr 端口范围。e.g. 8080-8090
     * @param portStr  单个端口 e.g. 8080
     * @return
     */
    public static boolean portInRange(String portsStr, String portStr) {
        int port = Integer.parseInt(portStr);
        if (portsStr.contains("-")) {
            String[] startAndEnd = portsStr.split("-");
            int startPort = Integer.parseInt(startAndEnd[0]);
            int endPort = Integer.parseInt(startAndEnd[1]);
            if (port == startPort || port == endPort) {
                return true;
            }
            if (port > startPort && port < endPort) {
                return true;
            }
            return false;
        } else if (portsStr.contains(",")) {
            String[] ports = portsStr.split(",");
            for (String p : ports) {
                if (port == Integer.parseInt(p)) {
                    return true;
                }
            }
            return false;
        } else {
            int ports = Integer.parseInt(portsStr);
            if (port == ports) {
                return true;
            }
            return false;
        }

    }

    /**
     * 判断给定IP是否属于给定IP段
     *
     * @return
     */
    public static boolean isInRange(long startIp, long endIp, long ip) {
        if (ip == startIp || ip == endIp) {
            return true;
        }
        if (ip > startIp && ip < endIp) {
            return true;
        }
        return false;
    }

    /**
     * 判断给定字符串是否是单个IPv4地址
     *
     * @param str
     * @return
     */
    public static boolean isIp(String str) {
        if (str == null) {
            return false;
        }
        // 单个IP e.g. 192.168.1.1
        /*if (str.matches("([0-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}")) {
            return true;
        }*/
        if (IP_V4_PATTERN.matcher(str).matches()) {
            return true;
        }

        return false;
    }

    public static boolean isSubnetMaskFormat(String ipRange) {
        // 子网掩码格式 e.g. 192.168.1.1/25
        if (ipRange.matches("([0-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}/([0-9]|([1-2][0-9])|(3[0-2]))")) {
            return true;
        }
        return false;
    }

    public static boolean isStartEndFormat(String ipRange) {
        // 完整格式横线分割地址段 e.g. 192.168.1.1-192.168.1.12
        if (ipRange.matches("([0-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}\\-([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}")) {
            return true;
        }
        return false;
    }

    public static boolean isSimpleStartEndFormat(String ipRange) {
        // 简化格式横线分割地址段 e.g. 192.168.1.1-12
        if (ipRange.matches("([0-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}\\-\\d+")) {
            return true;
        }
        return false;
    }

    /**
     * 判断给定字符串是否是一个IPv4地址段
     *
     * @param str
     * @return
     */
    public static boolean isIpRange(String str) {
        if (str == null) {
            return false;
        }
        // 子网掩码格式 e.g. 192.168.1.1/25
        if (isSubnetMaskFormat(str)) {
            return true;
        }
        // 完整格式横线分割地址段 e.g. 192.168.1.1-192.168.1.12
        if (isStartEndFormat(str)) {
            return true;
        }
        // 简化格式横线分割地址段 e.g. 192.168.1.1-12
        if (isSimpleStartEndFormat(str)) {
            return true;
        }
        return false;
    }

    /**
     * 判断给定字符串是否是一个IPv4地址或者是一个IPv4地址段
     *
     * @param str
     * @return
     */
    public static boolean isIpOrIpRange(String str) {
        if (isIpRange(str)) {
            return true;
        }
        if (isIp(str)) {
            return true;
        }
        return false;
    }


    /**
     * 根据掩码位数获取掩码
     */
    public static String getNetMask(String mask) {
        int inetMask = Integer.parseInt(mask);
        if (inetMask > 32) {
            return null;
        }
        //子网掩码为1占了几个字节
        int num1 = inetMask / 8;
        //子网掩码的补位位数
        int num2 = inetMask % 8;
        int array[] = new int[4];
        for (int i = 0; i < num1; i++) {
            array[i] = 255;
        }
        for (int i = num1; i < 4; i++) {
            array[i] = 0;
        }
        for (int i = 0; i < num2; num2--) {
            array[num1] += Math.pow(2, 8 - num2);
        }
        String netMask = array[0] + "." + array[1] + "." + array[2] + "." + array[3];
        return netMask;
    }

    /**
     * 根据ip地址和掩码获取起始IP
     *
     * @param ipinfo
     * @param netMask
     * @return
     */
    public static String getLowAddr(String ipinfo, String netMask) {
        String lowAddr = "";
        int ipArray[] = new int[4];
        int netMaskArray[] = new int[4];
        if (4 != ipinfo.split("\\.").length || "" == netMask) {
            return null;
        }
        for (int i = 0; i < 4; i++) {
            try {
                ipArray[i] = Integer.parseInt(ipinfo.split("\\.")[i]);
            } catch (NumberFormatException e) {
                String ip = ipinfo.replaceAll("\n", "");
                ipArray[i] = Integer.parseInt(ip.split("\\.")[i]);
            }
            netMaskArray[i] = Integer.parseInt(netMask.split("\\.")[i]);
            if (ipArray[i] > 255 || ipArray[i] < 0 || netMaskArray[i] > 255 || netMaskArray[i] < 0) {
                return null;
            }
            ipArray[i] = ipArray[i] & netMaskArray[i];
        }
        //构造最小地址
        for (int i = 0; i < 4; i++) {
            if (i == 3) {
                ipArray[i] = ipArray[i] + 1;
            }
            if ("" == lowAddr) {
                lowAddr += ipArray[i];
            } else {
                lowAddr += "." + ipArray[i];
            }
        }
        return lowAddr;
    }

    /**
     * 根据ip地址和掩码获取终止IP
     *
     * @param ipinfo
     * @param netMask
     * @return
     */
    public static String getHighAddr(String ipinfo, String netMask) {
        String lowAddr = getLowAddr(ipinfo, netMask);
        int hostNumber = getHostNumber(netMask);
        if ("" == lowAddr || hostNumber == 0) {
            return null;
        }
        int lowAddrArray[] = new int[4];
        for (int i = 0; i < 4; i++) {
            lowAddrArray[i] = Integer.parseInt(lowAddr.split("\\.")[i]);
            if (i == 3) {
                lowAddrArray[i] = lowAddrArray[i] - 1;
            }
        }
        lowAddrArray[3] = lowAddrArray[3] + (hostNumber - 1);
        if (lowAddrArray[3] > 255) {
            int k = lowAddrArray[3] / 256;
            lowAddrArray[3] = lowAddrArray[3] % 256;
            lowAddrArray[2] = lowAddrArray[2] + k;
        }
        if (lowAddrArray[2] > 255) {
            int j = lowAddrArray[2] / 256;
            lowAddrArray[2] = lowAddrArray[2] % 256;
            lowAddrArray[1] = lowAddrArray[1] + j;
            if (lowAddrArray[1] > 255) {
                int k = lowAddrArray[1] / 256;
                lowAddrArray[1] = lowAddrArray[1] % 256;
                lowAddrArray[0] = lowAddrArray[0] + k;
            }
        }
        String highAddr = "";
        for (int i = 0; i < 4; i++) {
            if (i == 3) {
                lowAddrArray[i] = lowAddrArray[i] - 1;
            }
            if ("" == highAddr) {
                highAddr = lowAddrArray[i] + "";
            } else {
                highAddr += "." + lowAddrArray[i];
            }
        }
        return highAddr;
    }

    /**
     * ip转换Long
     *
     * @param ip
     * @return
     */
    public static long ip2long(String ip) {
        String[] ips = ip.split("[.]");
        long num = 16777216L * Long.parseLong(ips[0]) + 65536L
                * Long.parseLong(ips[1]) + 256 * Long.parseLong(ips[2])
                + Long.parseLong(ips[3]);
        return num;
    }

    /**
     * ip转BigInteger。支持ipv6
     *
     * @param ip ipv4、ipv6
     * @return
     * @throws UnknownHostException
     */
    public static BigInteger ip2BigInteger(String ip) throws UnknownHostException {
        return new BigInteger(1, InetAddress.getByName(ip).getAddress());
    }

    /**
     * 实际可用ip数量
     *
     * @param netMask
     * @return
     */
    public static int getHostNumber(String netMask) {
        int hostNumber = 0;
        int netMaskArray[] = new int[4];
        for (int i = 0; i < 4; i++) {
            netMaskArray[i] = Integer.parseInt(netMask.split("\\.")[i]);
            if (netMaskArray[i] < 255) {
                hostNumber = (int) (Math.pow(256, 3 - i) * (256 - netMaskArray[i]));
                break;
            }
        }
        return hostNumber;
    }

    /**
     * 获取两个IP之间的IP数量
     *
     * @param startIp
     * @param endIp
     * @return
     */
    public static long getIpNum(String startIp, String endIp) {
        long start = ip2long(startIp);
        long end = ip2long(endIp);
        return Math.abs(end - start) + 1;
    }

    /**
     * 通过网段起始IP和终止IP获取网段掩码表示形式。注意！起始IP必须为网段的网络地址，终止IP必须为网段的广播地址
     *
     * @param startIp 起始IP
     * @param endIp   终止IP
     * @return 网络号/掩码
     */
    public static String ipRangeToSubnet(String startIp, String endIp) {
        int maskBitNum = subIpNumToMaskBitNum(getIpNum(startIp, endIp));
        return startIp + "/" + maskBitNum;
    }

    /**
     * 通过网段起始IP和终止IP获取网段掩码表示形式。注意！起始IP必须为网段的网络地址，终止IP必须为网段的广播地址
     *
     * @param startIpAndEndIp 起始IP-终止IP
     * @return 网络号/掩码
     */
    public static String ipRangeToSubnet(String startIpAndEndIp) {
        startIpAndEndIp = formatStartIpAndEndIp(startIpAndEndIp);
        String[] startAndEnd = startIpAndEndIp.split("\\-");
        return ipRangeToSubnet(startAndEnd[0], startAndEnd[1]);
    }

    /**
     * 格式化起始终止IP，将简写终止IP补全
     *
     * @param startIpAndEndIp 111.11.253.57-157
     * @return 111.11.253.57-111.11.253.157
     */
    public static String formatStartIpAndEndIp(String startIpAndEndIp) {

        String[] endIp = new String[4];

        String[] split = startIpAndEndIp.split("-");

        String start = split[0];
        String end = split[1];

        String[] startSplit = start.split("\\.");
        String[] endSplit = end.split("\\.");

        for (int i = endSplit.length, j = endIp.length; i > 0 && j > 0; i--, j--) {
            endIp[j - 1] = endSplit[i - 1];
        }
        for (int i = 0; i < startSplit.length - endSplit.length; i++) {
            endIp[i] = startSplit[i];
        }

        StringBuilder builder = new StringBuilder();
        builder.append(Arrays.stream(startSplit).collect(Collectors.joining(".")));
        builder.append("-");
        builder.append(Arrays.stream(endIp).collect(Collectors.joining(".")));
        return builder.toString();
    }

    /**
     * 将IP十进制格式转为二进制格式
     *
     * @param ip e.g. 111.11.254.8
     * @return e.g. 01101111.00001011.11111110.00001000
     */
    public static String ipToBinaryString(String ip) {
        return Arrays.stream(ip.split("\\."))
                .map(i -> String.format("%08d", Integer.valueOf(Integer.toBinaryString(Integer.valueOf(i)))))
                .collect(Collectors.joining("."));
    }

    /**
     * 通过子网IP数量获取二进制主机号所占位数
     *
     * @param num e.g. 4
     * @return e.g. 2
     */
    public static int subIpNumToHostBitNum(long num) {
        return (int) (Math.log(num) / Math.log(2));
    }

    /**
     * 通过子网IP数量获取二进制子网掩码所占位数
     *
     * @param num e.g. 4
     * @return e.g. 30
     */
    public static int subIpNumToMaskBitNum(long num) {
        return 32 - subIpNumToHostBitNum(num);
    }


    /**
     * 打印出两个IP之间的所有IP值
     *
     * @author https://blog.csdn.net/henryzhang2009/article/details/46295917
     */
    public static class PrintIP {

        // 一个IP，是一个３２位无符号的二进制数。故用long的低32表示无符号32位二进制数。
        public static long getIP(InetAddress ip) {
            byte[] b = ip.getAddress();
            long l = b[0] << 24L & 0xff000000L | b[1] << 16L & 0xff0000L | b[2] << 8L & 0xff00L | b[3] << 0L & 0xffL;
            return l;
        }

        // 由低32位二进制数构成InetAddress对象
        public static InetAddress toIP(long ip) throws UnknownHostException {
            byte[] b = new byte[4];
            int i = (int) ip;// 低３２位
            b[0] = (byte) ((i >> 24) & 0x000000ff);
            b[1] = (byte) ((i >> 16) & 0x000000ff);
            b[2] = (byte) ((i >> 8) & 0x000000ff);
            b[3] = (byte) ((i >> 0) & 0x000000ff);
            return InetAddress.getByAddress(b);
        }

        public static Optional<List<String>> listIpFromStartToEnd(String startIp, String endIp) {
            try {
                List<String> list = new ArrayList<>();
                long ip1 = getIP(InetAddress.getByName(startIp));
                long ip2 = getIP(InetAddress.getByName(endIp));
                for (long ip = ip1; ip <= ip2; ip++) {
                    list.add(toIP(ip).getHostAddress());
                }
                return Optional.ofNullable(list);
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }


        public static class StartAndEnd {
            private long start;
            private long end;

            public StartAndEnd(long start, long end) {
                this.start = start;
                this.end = end;
            }

            public long getStart() {
                return start;
            }

            public void setStart(long start) {
                this.start = start;
            }

            public long getEnd() {
                return end;
            }

            public void setEnd(long end) {
                this.end = end;
            }
        }

    }

}
