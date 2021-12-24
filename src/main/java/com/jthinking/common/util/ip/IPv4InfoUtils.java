package com.jthinking.common.util.ip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.*;

public class IPv4InfoUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(IPv4InfoUtils.class);

    private static List<IPv4Info> ipv4InfoList;

    private static final IPv4Info UNKNOWN = new IPv4Info(0, 0, "未知", "未知", "未知", "未知", false, 0, 0);

    private static final Map<String, String> COUNTRY_GEO = IPInfoUtils.COUNTRY_GEO;

    private static final Map<String, String> PROVINCE_GEO = IPInfoUtils.PROVINCE_GEO;

    private synchronized static void init() {

        if (ipv4InfoList != null && !ipv4InfoList.isEmpty()) {
            LOGGER.info("IPv4InfoUtils has already init");
            return;
        }

        String zipName = "ip.zip";
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

        URL resource = IPv4InfoUtils.class.getClassLoader().getResource(zipName);
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

        String ipv4InfoName;
        try {
            ipv4InfoName = ZipUtils.unZipReturnDir(zipFile, tmp.getAbsolutePath());
        } catch (Exception e) {
            LOGGER.error("", e);
            return;
        }
        File ipv4InfoFile = new File(tmp.getAbsolutePath() + "/" + ipv4InfoName);

        try {
            FileInputStream inputStream = new FileInputStream(ipv4InfoFile);
            ipv4InfoList = importTxt(inputStream);
            if (ipv4InfoList == null) {
                ipv4InfoList = Collections.emptyList();
            }
            ipv4InfoList.sort(Comparator.comparing(IPv4Info::getStartLong));
        } catch (FileNotFoundException e) {
            LOGGER.error("", e);
        }

        ipv4InfoFile.deleteOnExit();
        zipFile.deleteOnExit();

    }


    public static IPv4Info getIPv4Info(String ip) {
        if (ip == null) {
            return UNKNOWN;
        }
        //TODO ipv6支持。暂时忽略ipv6地址
        if (ip.contains(":") || !IpUtils.isIp(ip)) {
            return UNKNOWN;
        }
        if (ipv4InfoList == null) {
            init();
        }
        int index = Collections.binarySearch(ipv4InfoList, new IPv4Info(IpUtils.ip2long(ip), IpUtils.ip2long(ip)));
        if (index < 0 || index >= ipv4InfoList.size()) {
            return UNKNOWN;
        }
        return ipv4InfoList.get(index);
    }


    private static List<IPv4Info> importTxt(InputStream file) {
        List<IPv4Info> ipv4InfoList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file, "GB18030"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                IPv4Info IPv4Info = convertToIPv4Info(line);
                if (IPv4Info == null) {
                    continue;
                }
                ipv4InfoList.add(IPv4Info);
            }
            return ipv4InfoList;
        } catch (Exception e) {
            LOGGER.error("", e);
            return null;
        }

    }


    private static IPv4Info convertToIPv4Info(String row) {
        if (row.trim().equals("")) {
            return null;
        }
        String[] split = row.split("\\s+", 4);
        if (split.length != 4) {
            return null;
        }
        String startIp = split[0].trim();
        if (!IpUtils.isIp(startIp)) {
            return null;
        }
        long startLong = IpUtils.ip2long(startIp);
        String endIp = split[1].trim();
        if (!IpUtils.isIp(endIp)) {
            return null;
        }
        long endLong = IpUtils.ip2long(endIp);
        String region = split[2].trim();
        String isp = split[3].replaceAll("CZ88\\.NET", "").trim();
        if (startIp.equals("255.255.255.0")) {
            region = "保留地址";
            isp = "保留地址";
        }
        String tmp = region.substring(0, 2);
        String country;
        boolean overseas;
        String province;
        String address;
        String geo = "0,0";
        if (provinces.contains(tmp)) {
            country = "中国";
            overseas = false;
            province = formatProvince(tmp);
            address = region;
            String g = PROVINCE_GEO.get(province);
            if (g != null) {
                geo = g;
            }
        } else if (tmp.equals("广州")) {
            country = "中国";
            overseas = false;
            province = "广东";
            address = region;
            geo = PROVINCE_GEO.get("广东");
        } else if (tmp.equals("成都")) {
            country = "中国";
            overseas = false;
            province = "四川";
            address = region;
            geo = PROVINCE_GEO.get("四川");
        } else if (tmp.equals("沈阳")) {
            country = "中国";
            overseas = false;
            province = "辽宁";
            address = region;
            geo = PROVINCE_GEO.get("辽宁");
        } else if (region.contains("大学") || region.contains("学院") || region.contains("东北")) {
            country = "中国";
            overseas = false;
            province = "";
            address = region;
            geo = PROVINCE_GEO.get("中国");
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
        return new IPv4Info(startLong, endLong, country, province, address, isp, overseas, lat, lng);
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
