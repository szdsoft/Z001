package com.szd.z001.z001.domain;

import com.szd.core.client.domain.wf.ClientWfOper;
import lombok.Data;

/**
 * 业务参数
 */
@Data
public class Z001ResultVO {
    /** 业务表单数据 */
    private Z001DataVO data;
    /** 流程操作授权 */
    private ClientWfOper wfOper;
}