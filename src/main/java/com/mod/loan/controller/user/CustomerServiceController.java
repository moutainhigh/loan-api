package com.mod.loan.controller.user;

import com.mod.loan.common.annotation.Api;
import com.mod.loan.common.annotation.LoginRequired;
import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.RequestThread;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.model.MerchantConfig;
import com.mod.loan.service.MerchantConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "customer_service")
public class CustomerServiceController {

    @Autowired
    private MerchantConfigService merchantConfigService;


//        telMap.put("mx", "13282106125");
//        telMap.put("dawang", "13282106125");
//        telMap.put("huijie", "13282106125");
//        telMap.put("haitun", "13221050721");
//        telMap.put("care", "13221050721");
//        telMap.put("xiaoxiang", "16505243279");

    /**
     * 客服信息查询
     */
    @Api
    @LoginRequired()
    @RequestMapping(value = "/info")
    public ResultMessage getInfo() {
        String clientAlias = RequestThread.getClientAlias();
        String servicePhone = null;
        MerchantConfig merchantConfig = merchantConfigService.selectByMerchant(clientAlias);
        if (null != merchantConfig) {
            servicePhone = merchantConfig.getServicePhone();
        }
        Map<String, Object> data = new HashMap<>();
        //data.put("qq", "2513171881");
        data.put("tel", servicePhone);
        return new ResultMessage(ResponseEnum.M2000, data);
    }


}
