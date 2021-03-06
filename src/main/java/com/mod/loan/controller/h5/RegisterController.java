package com.mod.loan.controller.h5;

import com.alibaba.fastjson.JSON;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.config.rabbitmq.RabbitConst;
import com.mod.loan.config.redis.RedisConst;
import com.mod.loan.config.redis.RedisMapper;
import com.mod.loan.model.Blacklist;
import com.mod.loan.model.MerchantOrigin;
import com.mod.loan.model.Order;
import com.mod.loan.model.UserRegisterCodeStat;
import com.mod.loan.service.*;
import com.mod.loan.util.*;
import com.mod.loan.util.sms.EnumSmsTemplate;
import com.mod.loan.util.sms.SmsMessage;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用户注册
 *
 * @author wugy 2018年5月3日 下午9:32:05
 */
@CrossOrigin("*")
@RestController
@RequestMapping(value = "web")
public class RegisterController {

    private static Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private RedisMapper redisMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private UserDeductionService userDeductionService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private BlacklistService blacklistService;
    @Autowired
    private MerchantOriginService merchantOriginService;
    @Autowired
    private UserRegisterCodeStatService userRegisterCodeStatService;
    @Autowired
    private DataCenterService dataCenterService;

    @RequestMapping(value = "graph_code")
    public ResultMessage graph_code() throws IOException {
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        // 生产验证码字符串并保存到session中
        String createText = defaultKaptcha.createText();
        // 使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
        BufferedImage challenge = defaultKaptcha.createImage(createText);
        ImageIO.write(challenge, "jpg", jpegOutputStream);
        String base64String = Base64.encodeBase64String(jpegOutputStream.toByteArray());
        Map<String, String> data = new HashMap<>();
        String uuid = UUID.randomUUID().toString();
        data.put("uuid", uuid);
        data.put("graph_code", "data:image/jpeg;base64," + base64String);
        redisMapper.set(RedisConst.WEB_USER_GRAPH_CODE + uuid, createText, 120);
        return new ResultMessage(ResponseEnum.M2000, data);
    }

    //@RequestMapping(value = "mobile_code")
    public ResultMessage mobile_code(String alias, String phone, String graph_code, String uuid) {
        if (!CheckUtils.isMobiPhoneNum(phone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "手机号码错误");
        }
        if (!NumberUtils.isDigits(graph_code) || StringUtils.isBlank(uuid)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "验证码错误");
        }
        if (merchantService.findMerchantByAlias(alias) == null) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "商户不存在");
        }
        if (userService.selectUserByPhone(phone, alias) != null) {
            return new ResultMessage(ResponseEnum.M2001);
        }

        String verifyCode = redisMapper.get(RedisConst.WEB_USER_GRAPH_CODE + uuid);
        redisMapper.remove(RedisConst.WEB_USER_GRAPH_CODE + uuid);
        if (!graph_code.equals(verifyCode)) {
            return new ResultMessage(ResponseEnum.M2002);
        }

        UserRegisterCodeStat userRegisterCodeStat = userRegisterCodeStatService.selectDayCount(phone, alias);
        if (userRegisterCodeStat.getDayCount().compareTo(3) > 0) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "操作过于频繁");
        }

        String randomNum = RandomUtils.generateRandomNum(4);
        // 发送验证码，5分钟内有效
        redisMapper.set(RedisConst.USER_PHONE_CODE + phone, randomNum, 300);
        rabbitTemplate.convertAndSend(RabbitConst.queue_sms,
                new SmsMessage(alias, EnumSmsTemplate.T1001.getKey(), phone, randomNum + "|5分钟"));
        return new ResultMessage(ResponseEnum.M2000);
    }

   // @RequestMapping(value = "mobile_code_no_graph_code")
    public ResultMessage mobile_code(String alias, String phone) {
        if (!CheckUtils.isMobiPhoneNum(phone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "手机号码错误");
        }
        if (merchantService.findMerchantByAlias(alias) == null) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "商户不存在");
        }
        if (userService.selectUserByPhone(phone, alias) != null) {
            return new ResultMessage(ResponseEnum.M2001);
        }

        UserRegisterCodeStat userRegisterCodeStat = userRegisterCodeStatService.selectDayCount(phone, alias);
        if (userRegisterCodeStat.getDayCount().compareTo(3) > 0) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "操作过于频繁");
        }

        String randomNum = RandomUtils.generateRandomNum(4);
        // 发送验证码，5分钟内有效
        redisMapper.set(RedisConst.USER_PHONE_CODE + phone, randomNum, 300);
        rabbitTemplate.convertAndSend(RabbitConst.queue_sms,
                new SmsMessage(alias, EnumSmsTemplate.T1001.getKey(), phone, randomNum + "|5分钟"));
        return new ResultMessage(ResponseEnum.M2000);
    }

    @RequestMapping(value = "register")
    public ResultMessage user_register(String phone, String password, String phone_code, String alias,
                                       String origin_id, String browser_type) {
        if (StringUtils.isBlank(origin_id)) {
            origin_id = "android";
        }
        if (!CheckUtils.isMobiPhoneNum(phone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "手机号码错误");
        }
        if (StringUtils.isBlank(password)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "密码不能为空");
        }
        if (password.length() < 6) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "密码至少6位");
        }
        String redis_phone_code = redisMapper.get(RedisConst.USER_PHONE_CODE + phone);
        if (redis_phone_code == null) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "验证码错误");
        }
        if (!redis_phone_code.equals(phone_code)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "验证码错误");
        }
        if (merchantService.findMerchantByAlias(alias) == null) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "商户不存在");
        }
        if (userService.selectUserByPhone(phone, alias) != null) {
            return new ResultMessage(ResponseEnum.M2001);
        }

        return register(phone, password, alias, origin_id, browser_type, null);
    }


    // 不需要验证码注册
//    @RequestMapping(value = "register_no_code")
    public ResultMessage user_register_no_code(String phone, String password, String alias,
                                               String origin_id, String browser_type) {
        if (StringUtils.isBlank(origin_id)) {
            origin_id = "android";
        }
        if (!CheckUtils.isMobiPhoneNum(phone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "手机号码错误");
        }
        if (StringUtils.isBlank(password)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "密码不能为空");
        }
        if (password.length() < 6) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "密码至少6位");
        }
        if (merchantService.findMerchantByAlias(alias) == null) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "商户不存在");
        }
        if (userService.selectUserByPhone(phone, alias) != null) {
            return new ResultMessage(ResponseEnum.M2001);
        }
        return register(phone, password, alias, origin_id, browser_type, null);

    }

    private ResultMessage register(String phone, String password, String alias, String origin_id, String browser_type, String uuid) {
        //海豚对渠道号base64
        if ("haitun".equals(alias)) {
            origin_id = Base64ToMultipartFileUtil.decodeOrigin(origin_id);
        } else if ("huijie".equals(alias)) {
            try {
                origin_id = DesUtil.decryption(origin_id, null);
            } catch (Exception e) {
                logger.info("渠道编号异常 origin_id={} error={}", origin_id, e.getStackTrace());
                return new ResultMessage(ResponseEnum.M4000.getCode(), "渠道编号异常");
            }
        } else {
            // 新商户全部用des解密 care/xiaoxiang
            try {
                origin_id = DesUtil.decryption(origin_id, null);
            } catch (Exception e) {
                logger.info("渠道编号异常 origin_id={} error={}", origin_id, e.getStackTrace());
                return new ResultMessage(ResponseEnum.M4000.getCode(), "渠道编号异常");
            }
        }

        MerchantOrigin merchantOrigin = merchantOriginService.selectByPrimaryKey(Long.valueOf(origin_id));

        // 检查渠道号是否禁用
        if (null == merchantOrigin || merchantOrigin.getStatus() != 0) {
            logger.error("渠道号异常: merchant: {}, merchantOrigin: {}", alias, JSON.toJSON(merchantOrigin));
            return new ResultMessage(ResponseEnum.M4000.getCode(), "渠道编号异常");
        }

        // 检查是否该merchant的渠道号
        if (null == alias || !alias.equals(merchantOrigin.getMerchant())) {
            logger.error("渠道号异常: merchant:{}, merchantOrigin:{}", alias, JSON.toJSON(merchantOrigin));
            return new ResultMessage(ResponseEnum.M4000.getCode(), "渠道编号异常");
        }

        if (merchantOrigin.getCheckBlacklist() == 1) {
            Blacklist blacklist = blacklistService.getByPhone(phone);
            if (null != blacklist) {
                logger.info("存在黑名单中，无法注册， phone={}", phone);
                return new ResultMessage(ResponseEnum.M4000.getCode(), "审核不通过");
            }
        }

        if (merchantOrigin.getCheckRepay() == 1) {
            // 检查是否存在多头借贷
            if (dataCenterService.isMultiLoan(phone, null, alias)) {
                logger.info("存在多头借贷，无法注册， phone={}", phone);
                return new ResultMessage(ResponseEnum.M4000.getCode(), "审核不通过");
            }
        }

        if (merchantOrigin.getCheckOverdue() == 1) {
            Order orderOverDue = orderService.findOverdueByCertNo(phone);
            if (null != orderOverDue) {
                logger.info("存在逾期订单，无法注册， phone={}", phone);
                return new ResultMessage(ResponseEnum.M4000.getCode(), "审核不通过");
            }
        }

        Long uid = userService.addUser(phone, MD5.toMD5(password), origin_id, alias, browser_type);
        userDeductionService.addUser(uid, origin_id, alias, phone);
        // 删缓存
        redisMapper.remove(RedisConst.USER_PHONE_CODE + phone);
        redisMapper.remove(RedisConst.WEB_USER_GRAPH_CODE + uuid);
        //
        return new ResultMessage(ResponseEnum.M2000);
    }

    /**
     * 带图形验证码的注册
     *
     * @return
     */
    @RequestMapping(value = "register_graph_code")
    public ResultMessage userRegister4GraphCode(String phone, String password, String graph_code, String alias,
                                                String origin_id, String browser_type, String uuid) {
        logger.info("#[带图形验证码的注册]-[开始]-phone={},graph_code={},alias={},browser_type={},uuid={}", phone, graph_code, alias, browser_type, uuid);
        if (StringUtils.isBlank(origin_id)) {
            origin_id = "android";
        }
        if (!CheckUtils.isMobiPhoneNum(phone)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "手机号码错误");
        }
        if (StringUtils.isBlank(password)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "密码不能为空");
        }
        if (password.length() < 6) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "密码至少6位");
        }
        if (StringUtils.isBlank(uuid)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "请求参数错误");
        }
        if (StringUtils.isBlank(graph_code)) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "图形验证码错误");
        }
        String verifyCode = redisMapper.get(RedisConst.WEB_USER_GRAPH_CODE + uuid);
        if (verifyCode == null) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "图形验证码错误");
        }
        if (!graph_code.equals(verifyCode)) {
            return new ResultMessage(ResponseEnum.M2002);
        }
        if (merchantService.findMerchantByAlias(alias) == null) {
            return new ResultMessage(ResponseEnum.M4000.getCode(), "商户不存在");
        }
        if (userService.selectUserByPhone(phone, alias) != null) {
            return new ResultMessage(ResponseEnum.M2001);
        }
        return register(phone, password, alias, origin_id, browser_type, uuid);
    }

}
