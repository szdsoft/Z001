package com.szd.z001.base.common.util;

import com.szd.core.client.ClientCore;
import com.szd.core.client.domain.common.ClientResult;
import com.szd.core.client.domain.wf.ClientWfBussBase;
import com.szd.core.client.exception.ClientCustomException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 〈一句话功能简述〉<br>
 */
@Service
public class Z001Util {
    @Resource
    private ClientCore clientCore;

    public static Z001Util z001Util;

    @PostConstruct
    public void init() {
        z001Util = this;
        z001Util.clientCore = this.clientCore;
    }

    /**
     * 获取各业务主表的辅助状态 status
     */
    public static String getBussStatus(String bussId) {
        String status = "";
        ClientWfBussBase wfBussBase = z001Util.clientCore.wf.baseGetObj(bussId);
        if (wfBussBase == null) {
            return status;
        }
        if ("0".equals(wfBussBase.getRejectStatus())) {
            status = "B";
        } else if ("0".equals(wfBussBase.getStatus())) {
            status = "A";
        } else if ("1".equals(wfBussBase.getStatus())) {
            status = "C";
        } else if ("6".equals(wfBussBase.getStatus())) {
            status = "D";
        } else if ("9".equals(wfBussBase.getStatus())) {
            status = "E";
        }
        return status;
    }
    public static void checkResult(ClientResult res){
        if(res == null || !"S".equals(res.getStatus())){
            throw new ClientCustomException(res == null?"校验失败":res.getMsg());
        }
    }
    /**
     * 获取bussId
     */
    public static String getBussId() {
        return z001Util.clientCore.tool.getNumSeq();
    }
}