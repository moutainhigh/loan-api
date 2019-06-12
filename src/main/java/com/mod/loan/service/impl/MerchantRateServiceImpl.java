package com.mod.loan.service.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mod.loan.common.mapper.BaseServiceImpl;
import com.mod.loan.mapper.MerchantRateMapper;
import com.mod.loan.model.MerchantRate;
import com.mod.loan.service.MerchantRateService;

@Service
public class MerchantRateServiceImpl extends BaseServiceImpl<MerchantRate, Long> implements MerchantRateService {

	@Autowired
	MerchantRateMapper merchantRateMapper;

	@Override
	public MerchantRate findByMerchantAndBorrowType(String merchant, Integer borrowType) {
		if (StringUtils.isEmpty(merchant)) {
			return merchantRateMapper.findByMoneyAndDay(new BigDecimal(1000), 7);
		}
		MerchantRate merchantRate = merchantRateMapper.findByMerchantAndBorrowType(merchant, borrowType);
		return merchantRate;
	}

}
