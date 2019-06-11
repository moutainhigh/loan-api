package com.mod.loan.util.kuaiqian;


import com.mod.loan.util.kuaiqian.mgw.entity.TransInfo;
import com.mod.loan.util.kuaiqian.mgw.util.Base64Binrary;
import com.mod.loan.util.kuaiqian.mgw.util.MyX509TrustManager;
import com.mod.loan.util.kuaiqian.mgw.util.ParseUtil;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.KeyStore;
import java.util.HashMap;

public class KuaiqianPost {
    /**
     * 向指定URL发送POST方法的请求
     *
     * @param url    发送请求的URL
     * @param tr1XML 请求参数，请求参数应该是name1=value1&name2=value2的形式。
     * @return URL所代表远程资源的响应
     * @throws Exception
     */
    public static HashMap sendPost(String kqCertPath, String kqCertPwd, String kqMerchantId, String url, String tr1XML, TransInfo transInfo) throws Exception {
        System.setProperty("jsse.enableSNIExtension", "false");
        ParseUtil parseUtil = new ParseUtil();
        OutputStream out = null;
        HashMap respXml = null;
        //获取证书路径
        //测试证书，生产环境需要替换
        File certFile = new File(kqCertPath);
        //访问Java密钥库，JKS是keytool创建的Java密钥库，保存密钥。
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(certFile), kqCertPwd.toCharArray());
        //创建用于管理JKS密钥库的密钥管理器
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        //初始化证书
        kmf.init(ks, kqCertPwd.toCharArray());

        //同位体验证信任决策源//同位体验证可信任的证书来源
        TrustManager[] tm = {new MyX509TrustManager()};

        //初始化安全套接字
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        //初始化SSL环境。第二个参数是告诉JSSE使用的可信任证书的来源，设置为null是从javax.net.ssl.trustStore中获得证书。
        //第三个参数是JSSE生成的随机数，这个参数将影响系统的安全性，设置为null是个好选择，可以保证JSSE的安全性。
        sslContext.init(kmf.getKeyManagers(), tm, null);

        //根据上面配置的SSL上下文来产生SSLSocketFactory,与通常的产生方法不同
        SSLSocketFactory factory = sslContext.getSocketFactory();

        try {
            URL realUrl = new URL(url);
            //打开和URL之间的连接
            HttpsURLConnection conn = (HttpsURLConnection) realUrl.openConnection();
            //创建安全的连接套接字
            conn.setSSLSocketFactory(factory);
            //发送POST请求必须设置如下两行,使用 URL 连接进行输出、入
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //设置URL连接的超时时限
            conn.setReadTimeout(100000);

            //设置通用的请求属性  812451145110002 104110045112012
            //测试帐号，生产环境需要替换生产商户编号812451145110002
            String authString = kqMerchantId + ":" + kqCertPwd;
            String auth = "Basic " + Base64Binrary.encodeBase64Binrary(authString.getBytes());
            conn.setRequestProperty("Authorization", auth);

            // 获取URLConnection对象对应的输出流
            out = conn.getOutputStream();
            //发送请求参数
            out.write(tr1XML.getBytes("utf-8"));

            //flush 输出流的缓冲
            out.flush();

            //得到服务端返回
            InputStream is = conn.getInputStream();
            String reqData = "";
            if (is != null && !"".equals(is)) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                //缓冲区长度
                byte[] receiveBuffer = new byte[2048];
                //读取数据长度，InputStream要读取的数据长度一定要小于等于缓冲区中的字节数
                int readBytesSize = is.read(receiveBuffer);
                //判断流是否位于文件末尾而没有可用的字节
                while (readBytesSize != -1) {
                    //从receiveBuffer内存处的0偏移开始写，写与readBytesSize长度相等的字节
                    bos.write(receiveBuffer, 0, readBytesSize);
                    readBytesSize = is.read(receiveBuffer);
                }
                //编码后的tr2报文
                reqData = new String(bos.toByteArray(), "UTF-8");
            }
            System.out.println("tr2报文：" + reqData);
            //给解析XML的函数传递快钱返回的TR2的XML数据流
            respXml = parseUtil.parseXML(reqData, transInfo);

        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            if (out != null) {
                out.close();
            }
        }
        return respXml;
    }
}