package com.mod.loan.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "tb_merchant_rate")
public class MerchantRate {
    @Id
    private Long id;

    /**
     * 产品名称
     */
    @Column(name = "product_name")
    private String productName;

    /**
     * 借款期限
     */
    @Column(name = "product_day")
    private Integer productDay;

    /**
     * 借款金额
     */
    @Column(name = "product_money")
    private BigDecimal productMoney;

    /**
     * 优先级
     */
    @Column(name = "product_level")
    private Integer productLevel;

    /**
     * 年化利率
     */
    @Column(name = "product_rate")
    private BigDecimal productRate;

    /**
     * 状态：状态：1:启用；0:禁用
     */
    @Column(name = "product_status")
    private Integer productStatus;

    /**
     * 综合费率
     */
    @Column(name = "total_rate")
    private BigDecimal totalRate;

    /**
     * 逾期费率
     */
    @Column(name = "overdue_rate")
    private BigDecimal overdueRate;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    private Date updateTime;
    
    /**
     * 产商户别名
     */
    @Column(name = "merchant")
    private String merchant;

    /**
     * 新老客区分：状态：1:新客；3:老客
     */
    @Column(name = "product_type")
    private Integer productType;

    /**
     * 借款次数
     */
    @Column(name = "borrow_type")
    private Integer borrowType;


    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取产品名称
     *
     * @return product_name - 产品名称
     */
    public String getProductName() {
        return productName;
    }

    /**
     * 设置产品名称
     *
     * @param productName 产品名称
     */
    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    /**
     * 获取借款期限
     *
     * @return product_day - 借款期限
     */
    public Integer getProductDay() {
        return productDay;
    }

    /**
     * 设置借款期限
     *
     * @param productDay 借款期限
     */
    public void setProductDay(Integer productDay) {
        this.productDay = productDay;
    }

    /**
     * 获取借款金额
     *
     * @return product_money - 借款金额
     */
    public BigDecimal getProductMoney() {
        return productMoney;
    }

    /**
     * 设置借款金额
     *
     * @param productMoney 借款金额
     */
    public void setProductMoney(BigDecimal productMoney) {
        this.productMoney = productMoney;
    }

    /**
     * 获取优先级
     *
     * @return product_level - 优先级
     */
    public Integer getProductLevel() {
        return productLevel;
    }

    /**
     * 设置优先级
     *
     * @param productLevel 优先级
     */
    public void setProductLevel(Integer productLevel) {
        this.productLevel = productLevel;
    }

    /**
     * 获取年化利率
     *
     * @return product_rate - 年化利率
     */
    public BigDecimal getProductRate() {
        return productRate;
    }

    /**
     * 设置年化利率
     *
     * @param productRate 年化利率
     */
    public void setProductRate(BigDecimal productRate) {
        this.productRate = productRate;
    }


    public Integer getProductStatus() {
        return productStatus;
    }


    public void setProductStatus(Integer productStatus) {
        this.productStatus = productStatus;
    }

    /**
     * 获取综合费率
     *
     * @return total_rate - 综合费率
     */
    public BigDecimal getTotalRate() {
        return totalRate;
    }

    /**
     * 设置综合费率
     *
     * @param totalRate 综合费率
     */
    public void setTotalRate(BigDecimal totalRate) {
        this.totalRate = totalRate;
    }

    /**
     * 获取逾期费率
     *
     * @return overdue_rate - 逾期费率
     */
    public BigDecimal getOverdueRate() {
        return overdueRate;
    }

    /**
     * 设置逾期费率
     *
     * @param overdueRate 逾期费率
     */
    public void setOverdueRate(BigDecimal overdueRate) {
        this.overdueRate = overdueRate;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取修改时间
     *
     * @return update_time - 修改时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置修改时间
     *
     * @param updateTime 修改时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    /**
     * 获取0-通用，1-新客，2-次新，3-续客
     *
     * @return product_type - 0-通用，1-新客，2-次新，3-续客
     */
    public Integer getProductType() {
        return productType;
    }

    /**
     * 设置0-通用，1-新客，2-次新，3-续客
     *
     * @param productType 0-通用，1-新客，2-次新，3-续客
     */
    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    /**
     * 获取借款次数
     *
     * @return borrow_time - 借款次数
     */
    public Integer getBorrowType() {
        return borrowType;
    }

    /**
     * 设置借款次数
     *
     * @param borrowTime 借款次数
     */
    public void setBorrowType(Integer borrowType) {
        this.borrowType = borrowType;
    }
}