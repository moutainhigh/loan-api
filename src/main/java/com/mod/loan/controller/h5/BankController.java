package com.mod.loan.controller.h5;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mod.loan.common.annotation.LoginRequired;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.mapper.BankMapper;
import com.mod.loan.mapper.UserIdentMapper;
import com.mod.loan.model.*;
import com.mod.loan.service.MerchantService;
import com.mod.loan.service.OrderService;
import com.mod.loan.service.UserBankService;
import com.mod.loan.service.UserService;
import com.mod.loan.util.CheckUtils;
import com.mod.loan.util.GetBankUtil;
import com.mod.loan.util.StringReplaceUtil;
import com.mod.loan.util.heli.vo.request.AgreementBindCardValidateCodeVo;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户银行卡绑定
 * 
 * @author wugy 2018年5月3日 下午9:31:27
 */
@CrossOrigin("*")
@RestController
public class BankController {
	private static Logger log = LoggerFactory.getLogger(BankController.class);
	private static String bank_url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo=%s&cardBinCheck=true";
	private static String alipay_cookie_spanner = "";
	@Value("${environment:}")
	String environment;
	@Value("${server.api.url:}")
	String api_url;
	@Value("${server.h5.url:}")
	String h5_url;
	@Autowired
	UserIdentMapper userIdentMapper;
	@Autowired
	UserBankService userBankService;
	@Autowired
	BankMapper bankMapper;
	@Autowired
	OrderService orderService;
	@Autowired
	MerchantService merchantService;
	@Autowired
	UserService userService;
	@Autowired
	RedisMapper redisMapper;

	@RequestMapping(value = "bank_user")
	@LoginRequired(check = true)
	public ResultMessage bank_user() {
		User user = userService.selectByPrimaryKey(RequestThread.getUid());
		Map<String, String> data = new HashMap<>();
		data.put("name", user.getUserName());
		return new ResultMessage(ResponseEnum.M2000, data);
	}

	@RequestMapping(value = "bank_name")
	public ResultMessage bank_name(String cardNo) throws IOException {
		String url = String.format(bank_url, cardNo);
		Response response = Jsoup.connect(url).cookie("spanner", alipay_cookie_spanner).ignoreContentType(true)
				.execute();
		alipay_cookie_spanner = response.cookie("spanner");
		JSONObject json = JSONObject.parseObject(response.body());
		if (!json.getBooleanValue("validated")) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "银行卡号不正确");
		}
		if (!"DC".equals(json.getString("cardType"))) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "只支持储蓄卡");
		}
		String string = json.getString("bank");
		Bank bank = bankMapper.selectByPrimaryKey(string);
		if (bank == null) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "不支持该银行");
		}
		if (bank.getBankStatus() == 0) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "不支持" + bank.getBankName());
		}
		return new ResultMessage(ResponseEnum.M2000, bank.getBankName());
	}

	@RequestMapping(value = "bank_list")
	public ResultMessage bank_list() {
		Bank bank = new Bank();
		bank.setBankStatus(1);
		List<Bank> data = bankMapper.select(bank);
		data.forEach(item -> {
			item.setIdx(null);
			item.setCreateTime(null);
			item.setBankStatus(null);
			item.setBankImgurl(null);
			item.setMoneyUnitLimit(null);
			item.setMoneyDayLimit(null);
		});
		return new ResultMessage(ResponseEnum.M2000, data);
	}

	@RequestMapping(value = "bank_info")
	@LoginRequired(check = true)
	public ResultMessage user_bank_info() {
		UserIdent ident = userIdentMapper.selectByPrimaryKey(RequestThread.getUid());
		Map<String, Object> data = new HashMap<>();
		// 未实名先去实名，先去认证页面认证
		if (ident.getRealName() == 0) {
			return new ResultMessage(ResponseEnum.M3001);
		}
		// 未绑卡，先绑卡
		if (ident.getBindbank() == 0) {
			return new ResultMessage(ResponseEnum.M3003);
		}
		// 当前有订单则无法重新绑卡
		Order order = orderService.findUserLatestOrder(RequestThread.getUid());
		if (order != null && order.getStatus() < 40) {
			data.put("status", 0);
		} else {
			data.put("status", 1);
		}
		// 绑卡使用中的逻辑
		UserBank record = new UserBank();
		record.setUid(RequestThread.getUid());
		record.setCardStatus(1);
		UserBank userBank = userBankService.selectOne(record);
		data.put("cardName", userBank.getCardName());
		data.put("cardNo",
				userBank.getCardNo().substring(userBank.getCardNo().length() - 4, userBank.getCardNo().length()));
		data.put("cardPhone", StringReplaceUtil.phoneReplaceWithStar(userBank.getCardPhone()));
		return new ResultMessage(ResponseEnum.M2000, data);
	}

	/**
	 * 鉴权绑卡短信
	 * 
	 * @param cardNo
	 * @param cardPhone
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "bank_card_code")
	@LoginRequired(check = true)
	public ResultMessage bank_card_code(String cardNo, String cardPhone) throws Exception {
		ResultMessage message = null;
		if (GetBankUtil.checkBankCard(cardNo) == false) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "银行卡号不正确");
		}
		if (CheckUtils.isMobiPhoneNum(cardPhone) == false) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "手机号不合法");
		}
		String url = String.format(bank_url, cardNo);
		JSONObject json = JSONObject.parseObject(
				Jsoup.connect(url).cookie("spanner", alipay_cookie_spanner).ignoreContentType(true).execute().body());
		if (!json.getBooleanValue("validated")) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "银行卡号不正确");
		}
		if (!"DC".equals(json.getString("cardType"))) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "只支持储蓄卡");
		}
		String cardCode = json.getString("bank");
		Bank bank = bankMapper.selectByPrimaryKey(cardCode);
		if (bank == null || bank.getBankStatus() == 0) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "不支持该银行");
		}
		UserBank userBank = userBankService.selectUserCurrentBankCard(RequestThread.getUid());
		if (userBank != null && userBank.getCardNo().equals(cardNo)) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "当前银行卡已绑定");
		}
		Long uid = RequestThread.getUid();
		if (!redisMapper.lock(RedisConst.lock_user_bind_card_code + uid, 2)) {
			return new ResultMessage(ResponseEnum.M4005);
		}
		Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
		Integer bindType = merchant.getBindType() == null ? 1 : merchant.getBindType();
		switch (bindType) {
		case 1:
			message = userBankService.sendHeliSms(uid, cardNo, cardPhone, bank);
			break;
		case 2:
			message = userBankService.sendFuyouSms(uid, cardNo, cardPhone, bank);
			break;
		case 3:
			message = userBankService.sendHuijuSms(uid, cardNo, cardPhone, bank);
			break;
		default:
			log.error("绑卡异常,该商户未开通相关绑卡渠道,merchant={},bindType={}", merchant.getMerchantAlias(), bindType);
			message = new ResultMessage(ResponseEnum.M4000);
			break;
		}
		return message;
	}

	@RequestMapping(value = "bank_card_info")
	@LoginRequired(check = true)
	public ResultMessage bank_card_info() {
		Long uid = RequestThread.getUid();
		String string = redisMapper.get(RedisConst.user_bank_bind + uid);
		AgreementBindCardValidateCodeVo requestVo = JSON.parseObject(string, AgreementBindCardValidateCodeVo.class);
		if (requestVo == null) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "绑卡信息不存在");
		}
		Map<String, String> data = new HashMap<>();
		data.put("bankName", requestVo.getBankName());
		data.put("cardNo", requestVo.getP6_cardNo());
		data.put("userName", requestVo.getP10_payerName());
		data.put("userPhone", requestVo.getP7_phone());
		return new ResultMessage(ResponseEnum.M2000, data);
	}

	@RequestMapping(value = "bank_bind")
	@LoginRequired(check = true)
	public ResultMessage bank_bind(String validateCode) {
		ResultMessage message = null;
		if (StringUtils.isBlank(validateCode)) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "验证码不能为空");
		}
		if (validateCode.length() > 6) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "验证码长度过长");
		}
		Order order = orderService.findUserLatestOrder(RequestThread.getUid());
		if (order != null && order.getStatus() < 40) {
			return new ResultMessage(ResponseEnum.M4000.getCode(), "当前无法绑定银行卡");
		}
		Long uid = RequestThread.getUid();
		String bindInfo = redisMapper.get(RedisConst.user_bank_bind + uid);
		if (StringUtils.isBlank(bindInfo)) {
			return new ResultMessage(ResponseEnum.M5000.getCode(), "验证码失效,请重新获取");
		}
		if (!redisMapper.lock(RedisConst.lock_user_bind_card_code + uid, 2)) {
			return new ResultMessage(ResponseEnum.M4005);
		}
		Merchant merchant = merchantService.findMerchantByAlias(RequestThread.getClientAlias());
		Integer bindType = merchant.getBindType() == null ? 1 : merchant.getBindType();
		switch (bindType) {
		case 1:
			message = userBankService.bindByHeliSms(validateCode, uid, bindInfo);
			break;
		case 2:
			message = userBankService.bindByFuyouSms(validateCode, uid, bindInfo);
			break;
		case 3:
			message = userBankService.bindByHuijuSms(validateCode, uid, bindInfo);
			break;
		default:
			log.error("绑卡异常,该商户未开通相关绑卡渠道,merchant={},bindType={}", merchant.getMerchantAlias(), bindType);
			message = new ResultMessage(ResponseEnum.M4000);
			break;
		}
		return message;
	}

}