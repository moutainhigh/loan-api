package com.mod.loan.service;

public interface DataCenterService {

	boolean checkMultiLoan(String phone, String certNo);

	void delMultiLoanOrder(String merchant, Long orderId);


}
