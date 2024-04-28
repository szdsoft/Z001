package com.szd.z001.z001.service;

import com.github.pagehelper.util.StringUtil;
import com.szd.core.client.ClientCore;
import com.szd.core.client.constants.ClientConstMdm;
import com.szd.core.client.domain.mdm.ClientObjCmpy;
import com.szd.z001.base.common.util.Z001Util;
import com.szd.z001.base.orm.domain.z001.Z001Item;
import com.szd.z001.base.orm.mapper.z001.Z001ItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 *  费用-调整分录业务处理
 */
@Service
public class Z001ItemService {
    @Resource
    private Z001ItemMapper z001ItemMapper;
    @Resource
    private ClientCore clientCore;

    /**
     * 查询费用-调整分录列表
     */
    public List<Z001Item> selectList(String costId) {
        return z001ItemMapper.selectList(costId);
    }

    /**
     * 新增费用-调整分录
     */
    public void batchInsert(String costId, List<Z001Item> list) {
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(t -> t.setCostId(costId));
            z001ItemMapper.batchInsert(list);
        }
    }

    public void update(String costId, List<Z001Item> adjustList) {
        z001ItemMapper.deleteByCostId(costId);
        batchInsert(costId, adjustList);
    }

    public void checkData(List<Z001Item> z001ItemList, String cmpy) {
        if (!CollectionUtils.isEmpty(z001ItemList)) {
            ClientObjCmpy cmpyObj = clientCore.mdm.getObjA(ClientConstMdm.CORE_CMPY, cmpy, ClientObjCmpy.class);
            z001ItemList.forEach(t -> {
                if (cmpyObj != null && StringUtil.isNotEmpty(cmpyObj.getAccSet())) {
                    Z001Util.checkResult(clientCore.mdm.checkB("CORE_ACCS", cmpyObj.getAccSet(), t.getAccs(), "会计科目", "1"));
                }
                Z001Util.checkResult(clientCore.mdm.checkA("CORE_SUPP", t.getSupp(), "供应商", "1"));
                Z001Util.checkResult(clientCore.mdm.checkA("CORE_CUST", t.getCust(), "客户", "1"));
                Z001Util.checkResult(clientCore.mdm.checkA("CORE_CSTC", t.getCstc(), "成本中心", "1"));
                Z001Util.checkResult(clientCore.mdm.checkA("CORE_PRFC", t.getPrfc(), "利润中心", "1"));
                Z001Util.checkResult(clientCore.mdm.checkA("CORE_BUSC", t.getBusc(), "业务范围", "1"));
            });
        }
    }

    /**
     * 获取字段描述-调整分录
     */
    public void getDescAccAdjust(List<Z001Item> z001ItemList, ClientObjCmpy cmpyObj) {
        if (!CollectionUtils.isEmpty(z001ItemList)) {
            z001ItemList.forEach(t -> {
                t.setSuppName(clientCore.mdm.getDescA("CORE_SUPP", t.getSupp(), "1"));
                t.setCustName(clientCore.mdm.getDescA("CORE_CUST", t.getCust(), "1"));
                t.setCstcName(clientCore.mdm.getDescA("CORE_CSTC", t.getCstc(), "1"));
                t.setPrfcName(clientCore.mdm.getDescA("CORE_PRFC", t.getPrfc(), "1"));
                t.setBuscName(clientCore.mdm.getDescA("CORE_BUSC", t.getBusc(), "1"));
                if (cmpyObj != null && StringUtil.isNotEmpty(cmpyObj.getAccSet())) {
                    t.setAccsName(clientCore.mdm.getDescB("CORE_ACCS", cmpyObj.getAccSet(), t.getAccs(), "1"));
                }
                t.setReasName(clientCore.mdm.getDescA("CORE_REAS", t.getReas(), "1"));
            });
        }
    }

}