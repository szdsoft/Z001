package com.szd.z001.base.orm.domain.z001;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 费用-调整分录 sexp_cost_acc_adjust
 */
@Data
public class Z001Item {
    /** 费用ID */
    private String costId;
    /** 行项目 */
    private int itemNo;
    /** 会计科目 */
    private String accs;
    private String accsName;
    /** 借贷(S借 H贷) */
    private String debitCreditFlag;
    /** 金额 */
    private BigDecimal amt;
    /** 供应商 */
    private String supp;
    private String suppName;
    /** 客户 */
    private String cust;
    private String custName;
    /** 特别总账标识 */
    private String accSubjectFlag;
    /** 摘要 */
    private String summary;
    /** 成本中心 */
    private String cstc;
    private String cstcName;
    /** 内部订单 */
    private String orderCode;
    private String orderName;
    /** 利润中心 */
    private String prfc;
    private String prfcName;
    /** 贸易伙伴 */
    private String tradePartnerCode;
    private String tradePartnerName;
    /** 业务范围 */
    private String busc;
    private String buscName;
    /** 段 */
    private String segmentCode;
    private String segmentName;
    /** 分配编号 */
    private String assignNo;
    /** 反记帐 */
    private String reverseFlag;
    /** 税率 */
    private BigDecimal taxr;
    /** 原因代码 */
    private String reas;
    private String reasName;
    /** 功能范围 */
    private String funcAreaCode;
    /** 采购凭证 */
    private String purchaseDoc;
    /** 采购凭证行项目 */
    private String purchaseDocItem;
    /** 固定资产 */
    private String assetCode;
    /** 固定资产编号 */
    private String assetCodeNo;
    /** 收付条件 */
    private String payTerm;
    /** 基准日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date baseDate;
    /** 天数 */
    private int days;
    /** 参考码1 */
    private String reference01;
    /** 参考码2 */
    private String reference02;
    /** 参考码3 */
    private String reference03;
}