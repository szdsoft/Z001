package com.szd.z001.z001.service;

import com.szd.core.client.ClientCore;
import com.szd.core.client.domain.common.ClientResult;
import com.szd.core.client.domain.mdm.ClientObjBstp;
import com.szd.core.client.domain.wf.ClientWfBussBase;
import com.szd.core.client.security.ClientSecurity;

import com.szd.z001.base.common.util.Z001Util;
import com.szd.z001.base.orm.domain.z001.Z001Header;
import com.szd.z001.base.orm.domain.z001.Z001RpParamVO;
import com.szd.z001.base.orm.mapper.z001.Z001HeaderMapper;
import com.szd.z001.base.orm.mapper.z001.Z001ItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 总账业务抬头Service
 */
@Service
@Slf4j
public class Z001HeaderService {
    @Resource
    private Z001HeaderMapper z001HeaderMapper;
    @Resource
    private Z001ItemMapper z001ItemMapper;
    @Resource
    private ClientCore clientCore;

    public List<Z001Header> selectList(Z001RpParamVO header) {
        List<Z001Header> headerList = z001HeaderMapper.selectList(header);
        if (!CollectionUtils.isEmpty(headerList)) {
            List<String> bussIds = new ArrayList<>(16);
            for (Z001Header temp : headerList) {
                bussIds.add(temp.getAdjustId());
            }
            List<ClientWfBussBase> clientwfBussBaselist = clientCore.wf.baseGetObjBatch(bussIds);
            if(clientwfBussBaselist != null) {
                for (Z001Header temp : headerList) {
                    ClientWfBussBase wfBussBase = clientwfBussBaselist.stream().filter(item -> item.getBussId().equals(temp.getAdjustId())).findFirst().orElse(null);
                    if (wfBussBase != null) {
                        BeanUtils.copyProperties(wfBussBase, temp, new String[]{"status"});
                    }
                }
            }
        }
        return headerList;
    }
    /**
     * 查询抬头
     */
    public Z001Header selectById(String adjustId) {
        // 查询抬头
        Z001Header header = z001HeaderMapper.selectById(adjustId);
        if (header == null) {
            return new Z001Header();
        }
        ClientWfBussBase wfBussBase = clientCore.wf.baseGetObj(adjustId);
        if (wfBussBase != null) {
            BeanUtils.copyProperties(wfBussBase, header, new String[]{"status"});
        }
        return header;
    }

    /**
     * 保存抬头
     */
    public int insert(Z001Header z001Header) {
        // 初始化状态
        z001Header.setStatus("A");
        z001Header.setWfCreateBy(ClientSecurity.getUserId());
        return z001HeaderMapper.insert(z001Header);
    }

    /**
     * 更新抬头
     */
    public int update(Z001Header z001Header) {
        return z001HeaderMapper.update(z001Header);
    }

    public void updateStatusAndId(String bussId, String bussDocId) {
        String status = Z001Util.getBussStatus(bussId);
        if (StringUtils.isNotBlank(status)) {
            z001HeaderMapper.updateStatusAndId(bussId, status, bussDocId);
        }
    }
    public void updateStatusByBussId(String bussDocId,String status) {
        z001HeaderMapper.updateStatusByBussId(status, bussDocId);
    }
    /**
     * 删除抬头
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteById(String adjustId) {
        z001ItemMapper.deleteByCostId(adjustId);
        return z001HeaderMapper.deleteById(adjustId);
    }

    /**
     * 抬头数据校验
     */
    public void checkData(Z001Header header) {
        header.setWfBstp(header.getBstp());
        Z001Util.checkResult(clientCore.mdm.checkA("CORE_USER", header.getUserId(), "调账人", "1"));
        Z001Util.checkResult(clientCore.mdm.checkA("CORE_CMPY", header.getCmpyExp(), "承担公司", "1"));
        Z001Util.checkResult(clientCore.mdm.checkB("CORE_CSTC", header.getCmpyExp(), header.getCstcExp(), "承担部门", "1"));
        Z001Util.checkResult(clientCore.mdm.checkA("CORE_BSTP", header.getBstp(), "业务类型", "1"));
        Z001Util.checkResult(clientCore.mdm.checkA("CORE_CURR", header.getCurr(), "货币", "1"));
        if (StringUtils.isBlank(header.getWfStatus()) || "0".equals(header.getWfStatus())) {
            ClientResult retResult = clientCore.wf.dyCheck(header.getBstp(), header.getUserId(), header.getWfDyNo());
            Z001Util.checkResult(retResult);
            header.setWfDyId(retResult.getDocId());
        }

    }
}