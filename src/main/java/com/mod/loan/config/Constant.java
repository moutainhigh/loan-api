package com.mod.loan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Constant {

    public static String ENVIROMENT;
    public static String SERVER_API_URL;
    public static String SERVER_H5_URL;
    public static String SERVER_PREFIX_URL;

    public static String FUIOU_PAY_URL;

    public static String HUIJU_SMS_URL;
    public static String HUIJU_PAY_URL;
    public static String HUIJU_NOTIFY_URL;

    public static String KUAIQIAN_PAY_URL;
    public static String KUAIQIAN_PAY_QUERY_URL;

    public static String OSS_STATIC_BUCKET_NAME;
    public static String OSS_ACCESSKEY_ID;
    public static String OSS_ACCESS_KEY_SECRET;

    public static String JWT_SERCETKEY;

    /**
     * 同盾数据魔盒报告
     */
    public static String MOHE_PARTNER_CODE;
    public static String MOHE_PARTNER_KEY;
    public static String MOHE_TOKEN_URL;
    public static String MOHE_REPORT_URL;
    public static String MOHE_LOGIN_REPORT_URL;

    public static Integer SMS_EXPIRATION_TIME = 60 * 5; //5分钟
    public static long NATURE_ORIGIN_ID = 61L; //自然流量配置的ID号

    public static String OSS_ENDPOINT_OUT_URL;
    public static String OSS_ENDPOINT_IN_URL;

    /**
     * 合利宝委托代付
     */
    public static String HELIPAY_ENTRUSTED_URL;
    public static String HELIPAY_ENTRUSTED_FILE_URL;

    /**
     * 查询是否存在多头借贷的接口
     */
    public static String MULTI_LOAN_QUERY_URL;
    public static String MULTI_LOAN_DEL_URL;
    public static String MULTI_LOAN_QUERY_OVERDUE_URL;


    /**
     * 商户最大额度限制
     */
    public static Integer MERCHANT_MAX_PRODUCT_MONEY;

    public static String MX_RISK_API_MODEL_SCORE_URL;

    public static String MX_RISK_TOKEN;

    /**
     * 需要贷超按钮功能得商户 商户alias1,商户alias2,...
     */
    public static String MERCHANT_NEED_LOAN_MARKET_BUTTON;

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

    @Value("${kuaiqian.pay.url:}")
    public void setKuaiqianPayUrl(String kuaiqianPayUrl) {
        KUAIQIAN_PAY_URL = kuaiqianPayUrl;
    }

    @Value("${kuaiqian.pay.query.url:}")
    public void setKuaiqianPayQueryUrl(String kuaiqianPayQueryUrl) {
        KUAIQIAN_PAY_QUERY_URL = kuaiqianPayQueryUrl;
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

    @Value("${mohe.partner_code:}")
    public void setMohePartnerCode(String mohePartnerCode) {
        Constant.MOHE_PARTNER_CODE = mohePartnerCode;
    }

    @Value("${mohe.partner_key:}")
    public void setMohePartnerKey(String mohePartnerKey) {
        Constant.MOHE_PARTNER_KEY = mohePartnerKey;
    }

    @Value("${mohe.token_url:}")
    public void setMoheTokenUrl(String moheTokenUrl) {
        Constant.MOHE_TOKEN_URL = moheTokenUrl;
    }

    @Value("${mohe.report_url:}")
    public void setMoheReportUrl(String moheReportUrl) {
        Constant.MOHE_REPORT_URL = moheReportUrl;
    }

    @Value("${mohe.login_report_url:}")
    public void setMoheLoginReportUrl(String moheLoginReportUrl) {
        Constant.MOHE_LOGIN_REPORT_URL = moheLoginReportUrl;
    }

    @Value("${oss.endpoint.out:}")
    public void setOssEndpointOutUrl(String ossEndpointOutUrl) {
        Constant.OSS_ENDPOINT_OUT_URL = ossEndpointOutUrl;
    }

    @Value("${oss.endpoint.in:}")
    public void setOssEndpointInUrl(String ossEndpointInUrl) {
        Constant.OSS_ENDPOINT_IN_URL = ossEndpointInUrl;
    }

    @Value("${helipay.entrusted.url:}")
    public void setHelipayEntrustedUrl(String helipayEntrustedUrl) {
        Constant.HELIPAY_ENTRUSTED_URL = helipayEntrustedUrl;
    }

    @Value("${helipay.entrusted.file.url:}")
    public void setHelipayEntrustedFileUrl(String helipayEntrustedFileUrl) {
        Constant.HELIPAY_ENTRUSTED_FILE_URL = helipayEntrustedFileUrl;
    }

    @Value("${multi.loan.query.url:}")
    public void setMultiLoanQueryUrl(String multiLoanQueryUrl) {
        MULTI_LOAN_QUERY_URL = multiLoanQueryUrl;
    }

    @Value("${multi.loan.del.url:}")
    public void setMultiLoanDelUrl(String multiLoanDelUrl) {
        MULTI_LOAN_DEL_URL = multiLoanDelUrl;
    }

    @Value("${merchant.max.product.money:5000}")
    public void setMerchantMaxProductMoney(Integer merchantMaxProductMoney) {
        if (merchantMaxProductMoney > 10000) {
            merchantMaxProductMoney = 10000;
        }
        MERCHANT_MAX_PRODUCT_MONEY = merchantMaxProductMoney;
    }

    @Value("${mx.risk.api.model.score.url:}")
    public void setMxRiskApiModelScoreUrl(String mxRiskApiModelScoreUrl) {
        MX_RISK_API_MODEL_SCORE_URL = mxRiskApiModelScoreUrl;
    }

    @Value("${mx.risk.api.token:}")
    public void setMxRiskToken(String mxRiskToken) {
        MX_RISK_TOKEN = mxRiskToken;
    }

    @Value("${merchant.need.loan.market.button:unk}")
    public void setMerchantNeedLoanMarketButton(String merchantNeedLoanMarketButton) {
        MERCHANT_NEED_LOAN_MARKET_BUTTON = merchantNeedLoanMarketButton;
    }

    @Value("${multi.loan.query.overdue.url:}")
    public void setMultiLoanQueryOverdueUrl(String multiLoanQueryOverdueUrl) {
        MULTI_LOAN_QUERY_OVERDUE_URL = multiLoanQueryOverdueUrl;
    }
}
