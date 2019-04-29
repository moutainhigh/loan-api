package com.mod.loan.controller.origin;


import com.mod.loan.common.enums.ResponseEnum;
import com.mod.loan.common.model.ResultMessage;
import com.mod.loan.model.MerchantOrigin;
import com.mod.loan.service.OriginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "origin")
public class OriginController {

    private final OriginService originService;

    @Autowired
    public OriginController(OriginService originService) {
        this.originService = originService;
    }

    @RequestMapping(value = "status")
    public ResultMessage origin_list(Long id) {
        MerchantOrigin merchantOrigin = originService.selectByPrimaryKey(id);
        if (merchantOrigin==null){
            return new ResultMessage(ResponseEnum.M2000, 1); //1表示停用
        }
        return new ResultMessage(ResponseEnum.M2000, merchantOrigin.getStatus());
    }


}
