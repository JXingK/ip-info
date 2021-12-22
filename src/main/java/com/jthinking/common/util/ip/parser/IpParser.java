package com.jthinking.common.util.ip.parser;

import com.jthinking.common.util.ip.IPv4Info;
import com.jthinking.common.util.ip.IPv6Info;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;

public class IpParser {


    public static byte[] hexStringToByte(String hex) {
        byte[] b = new byte[hex.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hex.charAt(j++);
            char c1 = hex.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    public static int parse(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    public static IPv4Info getIPv4Info(byte[] ipv4Bytes) {
        if (ipv4Bytes.length != 4) {
            throw new IllegalArgumentException("ipv4 must be 4 bytes length");
        }

        return null;

    }

    public static String ipv4BytesToString(byte[] ipv4Bytes) {
        return ipv4BytesToString(ipv4Bytes, 0, ipv4Bytes.length);
    }

    public static String ipv4BytesToString(byte[] ipv4Bytes, int offset, int length) {
        if (length != 4) {
            throw new IllegalArgumentException("ipv4 must be 4 bytes length");
        }
        StringBuilder builder = new StringBuilder();
        for (int i = offset; i < offset + length; i++) {
            int ipSegment = Byte.toUnsignedInt(ipv4Bytes[i]);
            if (i != offset) {
                builder.append('.');
            }
            builder.append(ipSegment);
        }
        return builder.toString();
    }

    public static String ipBytesToString(byte[] ipBytes, int offset, int length) {
        if (length == 4) {
            return ipv4BytesToString(ipBytes, offset, length);
        } else if (length == 16) {
            return getShortIPv6(ipBytes, offset, length);
        } else {
            throw new IllegalArgumentException("Illegal ip length");
        }
    }


    public static IPv6Info getIPv6Info(byte[] ipv6Bytes) {
        if (ipv6Bytes.length != 16) {
            throw new IllegalArgumentException("ipv6 must be 16 bytes length");
        }

        // bytes 转 BigInteger
        // bytes 转 人易读格式
        BigInteger integer = new BigInteger(ipv6Bytes);

        //IPv6InfoUtils.getIpInfo()

        return null;
    }

    /**
     * 获取IPv6简写格式
     *
     * @param ipv6Bytes
     * @return
     */
    public static String getShortIPv6(byte[] ipv6Bytes, int offset, int length) {
        StringBuilder builder = new StringBuilder();
        int endPosition = offset + length;
        int zeroCount = 0;
        for (int i = offset; i < endPosition; i += 2) {
            int i1 = ipv6Bytes[i] & 0xFF;
            int i2 = ipv6Bytes[i + 1] & 0xFF;

            if (i == 0 && i1 == 0 && i2 == 0) {
                builder.append(':');
            }

            if (i1 != 0) {
                String s1 = Integer.toHexString(i1);
                builder.append(s1);
            }

            String s2 = Integer.toHexString(i2);
            if (s2.length() < 2 && i1 != 0) {
                builder.append('0');
            }
            builder.append(s2);
            if (i != ipv6Bytes.length - 2) {
                builder.append(':');
            }

            if (i1 == 0 && i2 == 0) {
                zeroCount++;
            } else {
                zeroCount = 0;
            }

            if (zeroCount > 1) {
                if (zeroCount == 2) {
                    builder.delete(builder.length() - 4, builder.length());
                    builder.append(':');
                } else {
                    builder.delete(builder.length() - 2, builder.length());
                }
            }

            if (zeroCount == 8 || (i == 14 && zeroCount > 1)) {
                builder.append(':');
            }

        }

        String ip = builder.toString();
        if (ip.endsWith(":") && !ip.endsWith("::")) {
            ip = ip.substring(0, ip.lastIndexOf(':'));
        }
        return ip;
    }

    public static String formatIPv6(String ipv6Hex) {
        char[] chars = ipv6Hex.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            builder.append(chars[i]);
            if ((i + 1) % 4 == 0 && i != chars.length - 1) {
                builder.append(':');
            }
        }
        return builder.toString();
    }

    public static String longToIP(long longIp) {
        // 直接右移24位
        return (longIp >>> 24) +
                "." +
                // 将高8位置0，然后右移16位
                ((longIp & 0x00FFFFFF) >>> 16) +
                "." +
                // 将高16位置0，然后右移8位
                ((longIp & 0x0000FFFF) >>> 8) +
                "." +
                // 将高24位置0
                (longIp & 0x000000FF);
    }

    public static String parseIP(List<Integer> ip) {
        if (ip.size() == 1) {
            long l = Integer.toUnsignedLong(ip.get(0));
            return longToIP(l);
        } else {
            return leftPadZero(Integer.toUnsignedString(ip.get(0), 16), 8) +
                    leftPadZero(Integer.toUnsignedString(ip.get(1), 16), 8) +
                    leftPadZero(Integer.toUnsignedString(ip.get(2), 16), 8) +
                    leftPadZero(Integer.toUnsignedString(ip.get(3), 16), 8);
        }
    }

    public static String parseIPBytes(byte[] ipBytes, int offset, int length) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(ipBytes, offset, length);
        if (length == 4) {
            long l = Integer.toUnsignedLong(byteBuffer.getInt());
            return longToIP(l);
        } else {
            return leftPadZero(Integer.toUnsignedString(byteBuffer.getInt(), 16), 8) +
                    leftPadZero(Integer.toUnsignedString(byteBuffer.getInt(), 16), 8) +
                    leftPadZero(Integer.toUnsignedString(byteBuffer.getInt(), 16), 8) +
                    leftPadZero(Integer.toUnsignedString(byteBuffer.getInt(), 16), 8);
        }
    }

    public static String leftPadZero(String source, int size) {
        if (source.length() >= size) {
            return source;
        } else {
            StringBuilder builder = new StringBuilder(source);
            for (int i = 0; i < size - source.length(); i++) {
                builder.insert(0, '0');
            }
            return builder.toString();
        }
    }

    public static String rightPadZero(String source, int size) {
        if (source.length() >= size) {
            return source;
        } else {
            return source + "0".repeat(size - source.length());
        }
    }

    public static byte[] intToByte4(int i) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (i & 0xFF);
        targets[2] = (byte) (i >> 8 & 0xFF);
        targets[1] = (byte) (i >> 16 & 0xFF);
        targets[0] = (byte) (i >> 24 & 0xFF);
        return targets;
    }

    public static void getIpInfo(List<Integer> ip) {
        int i = Integer.parseUnsignedInt("FFFF", 16);
        if (ip.size() == 1) {
            long l = Integer.toUnsignedLong(ip.get(0));
        } else {

            Integer.toUnsignedString(ip.get(0), 16);
        }
    }

    public static String getFullIPv6(String ipv6) {
        //入参为::时，此时全为0
        if (ipv6.equals("::")) {
            return "0000:0000:0000:0000:0000:0000:0000:0000";
        }
        //入参已::结尾时，直接在后缀加0
        if (ipv6.endsWith("::")) {
            ipv6 += "0";
        }
        String[] arrs = ipv6.split(":");
        StringBuilder symbol = new StringBuilder("::");
        int arrleng = arrs.length;
        while (arrleng < 8) {
            symbol.append(":");
            arrleng++;
        }
        ipv6 = ipv6.replace("::", symbol.toString());
        StringBuilder fullip = new StringBuilder();
        for (String ip : ipv6.split(":")) {
            StringBuilder ipBuilder = new StringBuilder(ip);
            while (ipBuilder.length() < 4) {
                ipBuilder.insert(0, "0");
            }
            ip = ipBuilder.toString();
            fullip.append(ip).append(':');
        }
        return fullip.substring(0, fullip.length() - 1);
    }


    public static BigInteger getIPv6BigInteger(String ipv6) {
        return new BigInteger(ipv6ToBytes(getFullIPv6(ipv6)));
    }

    /**
     * ipv6地址转有符号byte[17]
     */
    public static byte[] ipv6ToBytes(String ipv6) {
        byte[] ret = new byte[17];
        ret[0] = 0;
        int ib = 16;
        boolean comFlag = false;// ipv4混合模式标记
        if (ipv6.startsWith(":"))// 去掉开头的冒号
            ipv6 = ipv6.substring(1);
        String[] groups = ipv6.split(":");
        for (int ig = groups.length - 1; ig > -1; ig--) {// 反向扫描
            if (groups[ig].contains(".")) {
                // 出现ipv4混合模式
                byte[] temp = ipv4ToBytes(groups[ig]);
                ret[ib--] = temp[4];
                ret[ib--] = temp[3];
                ret[ib--] = temp[2];
                ret[ib--] = temp[1];
                comFlag = true;
            } else if ("".equals(groups[ig])) {
                // 出现零长度压缩,计算缺少的组数
                int zlg = 9 - (groups.length + (comFlag ? 1 : 0));
                while (zlg-- > 0) {// 将这些组置0
                    ret[ib--] = 0;
                    ret[ib--] = 0;
                }
            } else {
                int temp = Integer.parseInt(groups[ig], 16);
                ret[ib--] = (byte) temp;
                ret[ib--] = (byte) (temp >> 8);
            }
        }
        return ret;
    }

    /**
     * ipv4地址转有符号byte[5]
     */
    public static byte[] ipv4ToBytes(String ipv4) {
        byte[] ret = new byte[5];
        ret[0] = 0;
        // 先找到IP地址字符串中.的位置
        int position1 = ipv4.indexOf(".");
        int position2 = ipv4.indexOf(".", position1 + 1);
        int position3 = ipv4.indexOf(".", position2 + 1);
        // 将每个.之间的字符串转换成整型
        ret[1] = (byte) Integer.parseInt(ipv4.substring(0, position1));
        ret[2] = (byte) Integer.parseInt(ipv4.substring(position1 + 1,
                position2));
        ret[3] = (byte) Integer.parseInt(ipv4.substring(position2 + 1,
                position3));
        ret[4] = (byte) Integer.parseInt(ipv4.substring(position3 + 1));
        return ret;
    }

}
