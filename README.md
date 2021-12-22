# ip-info

## 概述

IP地理位置获取。支持获取IPv4、IPv6地址信息。包括：国家中文名称、中国省份中文名称、详细地址、互联网服务提供商、是否是国外、纬度、经度。

## 源码安装

```shell
git clone https://gitee.com/jthinking/ip-info.git
cd ip-info
mvn install -DskipTests
```

## Maven依赖

```xml
<dependency>
    <groupId>com.jthinking.common.util</groupId>
    <artifactId>ip-info</artifactId>
    <version>2.1.3</version>
</dependency>
```

## 快速入门

```java

// 获取IP信息
IPInfo ipInfo = IPInfoUtils.getIpInfo("222.128.176.102");
System.out.println(ipInfo.getCountry()); // 国家中文名称
System.out.println(ipInfo.getProvince()); // 中国省份中文名称
System.out.println(ipInfo.getAddress()); // 详细地址
System.out.println(ipInfo.getIsp()); // 互联网服务提供商
System.out.println(ipInfo.isOverseas()); // 是否是国外
System.out.println(ipInfo.getLat()); // 纬度
System.out.println(ipInfo.getLng()); // 经度

for (int i = 0; i < 100; i++) {
    System.out.println(IpInfoUtils.getIpInfo("54.213.132." + i));
}

```

## 调用示例

```java
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
```

## IP库来源

- IPv4地址原始信息来自 [cz88.net](http://www.cz88.net/ip/)
- IPv6地址原始信息来自 [zxinc.org](http://ip.zxinc.org/)