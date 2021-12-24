package com.jthinking.common.util.ip;

import com.jthinking.common.util.ip.parser.IpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.util.*;

public class IPv6InfoUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IPv6InfoUtils.class);

    private static List<IPv6Info> ipInfoList;

    private static final IPv6Info UNKNOWN = new IPv6Info(BigInteger.ZERO, BigInteger.ZERO, "未知", "未知", "未知", "未知", false, 0, 0);

    private static final Map<String, String> COUNTRY_GEO = IPInfoUtils.COUNTRY_GEO;

    private static final Map<String, String> PROVINCE_GEO = IPInfoUtils.PROVINCE_GEO;

    private synchronized static void init() {

        if (ipInfoList != null && !ipInfoList.isEmpty()) {
            LOGGER.info("IPInfoUtils has already init");
            return;
        }

        String zipName = "ipv6.zip";
        String userDir = System.getProperty("user.dir");
        File tmp = new File(userDir + "/tmp");
        if (!tmp.exists()) {
            boolean mkdir = tmp.mkdir();
            LOGGER.info("{} mkdir {}", tmp, mkdir);
        }
        File zipFile = new File(tmp.getAbsolutePath() + "/" + zipName);
        if (zipFile.exists()) {
            zipFile.delete();
        }
        zipFile.setWritable(true);
        zipFile.setReadable(true);

        URL resource = IPv6InfoUtils.class.getClassLoader().getResource(zipName);
        if (resource == null) {
            LOGGER.error("{} zip file not found", zipName);
            return;
        }

        try (InputStream in = resource.openStream(); FileOutputStream out = new FileOutputStream(zipFile)) {
            byte[] temp = new byte[1024];
            int len;
            while ((len = in.read(temp, 0, temp.length)) != -1) {
                out.write(temp, 0, len);
            }
        } catch (IOException e) {
            LOGGER.error("", e);
            return;
        }

        String ipInfoName;
        try {
            ipInfoName = ZipUtils.unZipReturnDir(zipFile, tmp.getAbsolutePath());
        } catch (Exception e) {
            LOGGER.error("", e);
            return;
        }
        File ipInfoFile = new File(tmp.getAbsolutePath() + "/" + ipInfoName);

        try {
            FileInputStream inputStream = new FileInputStream(ipInfoFile);
            ipInfoList = importTxt(inputStream);
            if (ipInfoList == null) {
                ipInfoList = Collections.emptyList();
            }
            ipInfoList.sort(Comparator.comparing(IPv6Info::getStart));
        } catch (FileNotFoundException e) {
            LOGGER.error("", e);
        }

        ipInfoFile.deleteOnExit();
        zipFile.deleteOnExit();

    }


    public static IPv6Info getIPv6Info(BigInteger ipv6) {
        if (ipv6 == null) {
            return UNKNOWN;
        }
        if (ipInfoList == null) {
            init();
        }
        int index = Collections.binarySearch(ipInfoList, new IPv6Info(ipv6, ipv6));
        if (index < 0 || index >= ipInfoList.size()) {
            return UNKNOWN;
        }
        return ipInfoList.get(index);
    }

    public static IPv6Info getIPv6Info(byte[] ipv6Bytes) {
        return getIPv6Info(new BigInteger(ipv6Bytes));
    }


    public static IPv6Info getIpInfoByIPv6BytesHex(String ipv6BytesHex) {
        StringBuilder builder = new StringBuilder(ipv6BytesHex);
        int count = 32 - ipv6BytesHex.length();
        for (int i = 0; i < count; i++) {
            builder.append('0');
        }
        byte[] bytes = IpParser.hexStringToByte(builder.toString());
        return getIPv6Info(bytes);
    }


    public static BigInteger getBigIntegerIPv6BytesHex(String ipv6BytesHex) {
        StringBuilder builder = new StringBuilder(ipv6BytesHex);
        int count = 32 - ipv6BytesHex.length();
        for (int i = 0; i < count; i++) {
            builder.append('0');
        }
        return new BigInteger(builder.toString(), 16);
    }


    public static IPv6Info getIPv6Info(String ipv6) {
        return getIPv6Info(IpParser.ipv6ToBytes(IpParser.getFullIPv6(ipv6)));
    }

    private static List<IPv6Info> importTxt(InputStream file) {
        List<IPv6Info> ipInfoList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                IPv6Info ipInfo = convertToIPv6Info(line);
                if (ipInfo == null) {
                    continue;
                }
                ipInfoList.add(ipInfo);
            }
            return ipInfoList;
        } catch (Exception e) {
            LOGGER.error("", e);
            return null;
        }

    }


    private static IPv6Info convertToIPv6Info(String row) {
        if ("".equals(row.trim())) {
            return null;
        }
        String[] split = row.split("\\s+", 4);
        if (split.length != 4) {
            return null;
        }
        String startIp = split[0].trim();
        BigInteger startLong;
        try {
            startLong = getBigIntegerIPv6BytesHex(startIp);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }

        String endIp = split[1].trim();
        BigInteger endLong;
        try {
            endLong = getBigIntegerIPv6BytesHex(endIp);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }

        String region = split[2].replace("ZX", "").trim();
        String isp = split[3].replace("ZX", "").trim();

        String country;
        boolean overseas;
        String province;
        String address;
        String geo = "0,0";
        if (region.startsWith("中国")) {
            country = "中国";
            province = "中国";
            overseas = false;
            address = region;
            if (region.length() > 2) {
                String tmp = region.substring(2, 4);
                if (provinces.contains(tmp)) {
                    province = formatProvince(tmp);
                    String g = PROVINCE_GEO.get(province);
                    if (g != null) {
                        geo = g;
                    }
                }
            }
        } else if (region.contains("局域网") || isp.contains("局域网")) {
            country = "局域网";
            overseas = false;
            province = "";
            address = region;
        } else if (region.contains("保留地址") || isp.contains("保留地址")) {
            country = "保留地址";
            overseas = false;
            province = "";
            address = region;
        } else if (region.contains("特殊地址") || isp.contains("特殊地址")) {
            country = "保留地址";
            overseas = false;
            province = "";
            address = region;
        } else if (region.contains("本机地址") || isp.contains("本机地址")) {
            country = "本机地址";
            overseas = false;
            province = "";
            address = region;
        } else {
            country = region;
            overseas = true;
            province = "";
            address = region;
            String g = COUNTRY_GEO.get(country);
            if (g != null) {
                geo = g;
            }
        }
        String[] latAndLng = geo.split(",");
        double lat = Double.parseDouble(latAndLng[0]);
        double lng = Double.parseDouble(latAndLng[1]);
        return new IPv6Info(startLong, endLong, country, province, address, isp, overseas, lat, lng);
    }

    private static final String provinces = "河北,山西,辽宁,吉林,黑龙,江苏,浙江,安徽,福建,江西,山东,河南,"
            + "湖北,湖南,广东,海南,四川,贵州,云南,陕西,甘肃,青海,台湾"
            + "西藏,广西,内蒙,宁夏,新疆,香港,澳门"//黑龙江和内蒙古用黑龙和内蒙就可以匹配到
            + "北京,上海,天津,重庆,中国";//23省+5自治区+4直辖市+2特别行政区+中国

    /***
     * 规范省份名称
     * @param originalData 需要进行规范名字的省份
     * @return 规范后的名字
     */
    private static String formatProvince(String originalData) {
        String result;
        switch (originalData) {
            case "内蒙":
                result = "内蒙古";
                break;
            case "黑龙":
                result = "黑龙江";
                break;
            default:
                result = originalData;
                break;
        }
        return result;
    }

}
