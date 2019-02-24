package com.mod.loan.common.enums;

/**
 * 
 * @author wugy 2018年1月9日 下午5:27:14
 */
public enum ResponseEnum {

    M2000("2000", "success"),
    M2001("2001", "手机号已注册"),
    M2002("2002", "图形验证码错误"),

    M3001("3001", "未实名"),
    M3002("3002", "个人信息未认证"),
    M3003("3003", "银行卡未绑定"),
    M3004("3004", "手机未认证"),
    M3005("3005", "支付宝未认证"),
    M3006("3006", "人脸识别未认证"),
    
    M4000("4000", "系统异常"),
    M4001("4001", "无效的版本号"),
    M4002("4002", "无效的TOKEN"),
    M4003("4003", "版本需要强制更新"),
    M4004("4004", "公共参数异常"),
    M4005("4005", "操作过于频繁"),

    M5000("5000", "参数错误"),
    
    M6001("6001","不存在的商品"),
    M6002("6002","不存在的收货地址"),
    M6003("6003","订单金额异常"),
    M6004("6004","付款订单信息异常"),
    
    M70000("70000", "测试");

    private String code;

    private String message;

    ResponseEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
