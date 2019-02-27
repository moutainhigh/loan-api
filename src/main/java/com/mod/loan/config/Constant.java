package com.mod.loan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Constant {

	public static String ENVIROMENT;
	public static String SERVER_API_URL;
	public static String SERVER_H5_URL;
	public static String SERVER_ITF_URL;
	public static String SERVER_PREFIX_URL;

	public static String FUIOU_PAY_URL;

	public static String HUIJU_SMS_URL;
	public static String HUIJU_PAY_URL;
	public static String HUIJU_NOTIFY_URL;

	public static String OSS_STATIC_BUCKET_NAME;
	public static String OSS_ACCESSKEY_ID;
	public static String OSS_ACCESS_KEY_SECRET;

	public static String JWT_SERCETKEY;

	@Value("${environment:}")
	public void setENVIROMENT(String environment) {
		Constant.ENVIROMENT = environment;
	}

	@Value("${server.api.url:}")
	public void setServerApiUrl(String serverApiUrl) {
		SERVER_API_URL = serverApiUrl;
	}

	@Value("${server.h5.url:}")
	public void setServerH5Url(String serverH5Url) {
		SERVER_H5_URL = serverH5Url;
	}

	@Value("${server.itf.url:}")
	public void setServerItfUrl(String serverItfUrl) {
		SERVER_ITF_URL = serverItfUrl;
	}

	@Value("${oss.static.prefix:}")
	public void setServerPrefixUrl(String serverPrefixUrl) {
		SERVER_PREFIX_URL = serverPrefixUrl;
	}
	
	@Value("${fuiou.pay.url:}")
	public void setFuiouPayUrl(String fuiouPayUrl) {
		FUIOU_PAY_URL = fuiouPayUrl;
	}

	@Value("${jwt.sercetKey:}")
	public void setJwtSercetkey(String jwtSercetkey) {
		JWT_SERCETKEY = jwtSercetkey;
	}

	@Value("${huiju.pay.url:}")
	public void setHuijuPayUrl(String huijuPayUrl) {
		HUIJU_PAY_URL = huijuPayUrl;
	}

	@Value("${huiju.notify.url:}")
	public void setHuijuNotifyUrl(String huijuNotifyUrl) {
		HUIJU_NOTIFY_URL = huijuNotifyUrl;
	}

	@Value("${huiju.sms.url:}")
	public void setHuijuSmsUrl(String huijuSmsUrl) {
		HUIJU_SMS_URL = huijuSmsUrl;
	}

	@Value("${oss.static.bucket.name:}")
	public void setOSS_STATIC_BUCKET_NAME(String oSS_STATIC_BUCKET_NAME) {
		OSS_STATIC_BUCKET_NAME = oSS_STATIC_BUCKET_NAME;
	}

	@Value("${oss.accesskey.id:}")
	public void setOSS_ACCESSKEY_ID(String oSS_ACCESSKEY_ID) {
		OSS_ACCESSKEY_ID = oSS_ACCESSKEY_ID;
	}

	@Value("${oss.accesskey.secret:}")
	public void setOSS_ACCESS_KEY_SECRET(String oSS_ACCESS_KEY_SECRET) {
		OSS_ACCESS_KEY_SECRET = oSS_ACCESS_KEY_SECRET;
	}

}