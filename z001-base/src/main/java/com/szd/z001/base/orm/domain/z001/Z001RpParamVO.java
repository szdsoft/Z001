package com.szd.z001.base.orm.domain.z001;

import com.szd.core.client.domain.common.ClientPageEntity;
import com.szd.core.client.domain.common.ClientRangeQuery;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 总账查询参数实体
 */
@Data
public class Z001RpParamVO extends ClientPageEntity {
    private String userId;
    /**
     * 供应商
     */
    private String supp;
    /**
     * 客户
     */
    private String cust;
    /**
     * 路由编码
     */
    private String routerCode;


    /**
     * 标题
     */
    private String wfTitle;
    /**
     * 创建人
     */
    private String wfCreateBy;
    /**
     * 高级查询
     */
    private Map<String, List<ClientRangeQuery>> ranges;

}