package com.szd.z001.z001.domain;

import com.szd.core.client.domain.wf.ClientWfParamEvt;
import lombok.Data;

/**
 * 业务参数
 */
@Data
public class Z001ParamVO {
    /** 业务表单数据 */
    private Z001DataVO data;
    /** 流程参数 */
    private ClientWfParamEvt wfEvt;
}