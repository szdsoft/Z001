package com.szd.z001.base.orm.domain.z001;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szd.core.client.domain.wf.ClientWfBussBase;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 总账抬头
 */
@Data
public class Z001Header extends ClientWfBussBase {
    /** 总账ID */
    private String adjustId;
    /** 制单人公司代码 */
    private String cmpyCreate;
    private String cmpyNameCreate;
    /** 制单人成本中心 */
    private String cstcCreate;
    private String cstcNameCreate;
    /** 报账人 */
    private String userId;
    private String userName;
    /** 供应商 */
    private String supp;
    private String suppName;
    /** 客户 */
    private String cust;
    private String custName;
    /** 费用承担公司 */
    private String cmpyExp;
    private String cmpyNameExp;
    /** 费用承担成本中心 */
    private String cstcExp;
    private String cstcNameExp;
    /** 业务发生成本公司 */
    private String cmpyBuss;
    private String cmpyNameBuss;
    /** 业务发生成本中心 */
    private String cstcBuss;
    private String cstcNameBuss;
    /** 业务类型 */
    private String bstp;
    private String bstpName;
    /** 业务路由 */
    private String routerCode;
    /** 附件张数 */
    private int attNum;
    /** 币种 */
    private String curr;
    private String currName;
    /** 汇率 */
    private String exchangeRate;
    /** 业务日期 */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date bussDate;
    /** 费用说明 */
    private String remark;
    /** 银行摘要 */
    private String bankSummary;
    /** 出差人数 */
    private int userNum;
    /** 状态 */
    private String status;
    private String statusName;
    /** 外围系统状态描述 */
    private String sapStatusDesc;
    /** 付款方式 */
    private String paymentType;
    private String paymentTypeName;
    /** 计划票据金额 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amtBillApply;
    /** 实际票据金额 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amtBillActual;
}