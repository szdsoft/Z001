package com.szd.z001.z001.service;

import com.szd.core.client.ClientCore;
import com.szd.core.client.domain.wf.ClientWfParamBuss;
import com.szd.core.client.domain.wf.ClientWfResult;
import com.szd.core.client.domain.wf.ClientWfReturn;
import com.szd.core.client.exception.ClientCustomException;
import com.szd.z001.base.common.util.CommonService;
import com.szd.z001.base.common.util.Z001Util;
import com.szd.z001.base.orm.domain.z001.Z001Header;
import com.szd.z001.base.orm.domain.z001.Z001RpParamVO;
import com.szd.z001.base.orm.domain.z001.Z001Item;
import com.szd.z001.z001.domain.Z001DataVO;
import com.szd.z001.z001.domain.Z001ReturnVO;
import com.szd.z001.z001.domain.CheckDataResult;
import com.szd.z001.z001.domain.Z001ParamVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 总账业务处理
 */
@Service
public class Z001Service {
    private static final Logger logger = LoggerFactory.getLogger(Z001Service.class);
    @Resource
    private Z001ItemService z001ItemService;
    @Resource
    private Z001HeaderService z001HeaderService;
    @Resource
    private CommonService commonService;

    @Resource
    private ClientCore clientCore;

    /**
     * 总账报表查询
     */
    public List<Z001Header> selectList(Z001RpParamVO header) {
        return z001HeaderService.selectList(header);
    }

    /**
     *总账业务数据查询
     */
    public Z001DataVO selectData(String bussId) {
        Z001DataVO z001DataVO = new Z001DataVO();
        Z001Header header = z001HeaderService.selectById(bussId);
        // 查询调整分录列表
        List<Z001Item> z001ItemList = z001ItemService.selectList(bussId);
        z001DataVO.setHeader(header);
        z001DataVO.setZ001ItemList(z001ItemList);
        return z001DataVO;
    }

    /**
     * 员工费用报销业务数据保存
     */
    public Z001ReturnVO saveData(Z001ParamVO z001ParamVO) {
        Z001ReturnVO z001ReturnVO = new Z001ReturnVO();
        ClientWfParamBuss wfParamBuss = new ClientWfParamBuss();
        // 检查业务数据
        checkData(z001ParamVO.getData());
        // 保存业务数据
        save(z001ParamVO, wfParamBuss);
        // 审批
        ClientWfReturn wfReturn = commonService.approval(z001ParamVO.getData().getHeader(), z001ParamVO.getWfEvt(), z001ParamVO.getData().getHeader().getAdjustId(), true);
        BeanUtils.copyProperties(wfReturn, z001ReturnVO);
        z001ReturnVO.setBussId(z001ParamVO.getData().getHeader().getAdjustId());
        z001ReturnVO.setBussDocId(z001ParamVO.getData().getHeader().getWfBussDocId());
        // 更新业务单据状态
        if (!"E".equals(z001ReturnVO.getRetCodeF())) {
            //草稿状态的单据，作废时直接删除
            if ("but_delete".equals(z001ParamVO.getWfEvt().getOperate())
                    && "A".equals(z001ParamVO.getData().getHeader().getStatus())) {
                z001HeaderService.deleteById(z001ReturnVO.getBussId());
            } else {
                z001HeaderService.updateStatusAndId(z001ReturnVO.getBussId(), wfReturn.getBussDocId());
            }
        }
        return z001ReturnVO;
    }

    /**
     * 保存草稿
     */
    @Transactional(rollbackFor = Exception.class)
    public ClientWfResult save(Z001ParamVO z001ParamVO, ClientWfParamBuss wfParamBuss) {
        ClientWfResult wfResult = new ClientWfResult();
        try {
            // 字段状态组检查
            Z001DataVO z001DataVO = (Z001DataVO) commonService.handleBstp(z001ParamVO.getData(), z001ParamVO.getData().getHeader().getBstp());
            z001ParamVO.setData(z001DataVO);
            // 业务数据
            Z001DataVO data = z001ParamVO.getData();
            // 抬头数据
            Z001Header header = data.getHeader();
            // 业务类型赋值
            header.setWfBstp(header.getBstp());
            String bussId;
            // 首次保存
            if (StringUtils.isBlank(header.getAdjustId())) {
                // 单据id
                bussId = Z001Util.getBussId();
                header.setAdjustId(bussId);
                // 保存业务主体
                commonService.saveBussBase(header, header.getAdjustId(), header.getCmpyExp(), header.getCstcExp());
                // 新增
                insert(data);
            } else {
                // 保存业务主体
                commonService.saveBussBase(header, header.getAdjustId(), header.getCmpyExp(), header.getCstcExp());
                // 更新
                update(z001ParamVO);
            }
            // 设置工作流字段
            commonService.setWfParamBuss(wfParamBuss, header, header.getAdjustId());
        } catch (Exception e) {
            logger.error("保存失败", e);
            throw new ClientCustomException("保存失败:" + e.getMessage());
        }
        return wfResult;
    }

    /**
     * 业务数据新增
     */
    void insert(Z001DataVO data) {
        /** 抬头数据 */
        Z001Header header = data.getHeader();
        String adjustId = header.getAdjustId();
        /** 调整分录 */
        List<Z001Item> z001ItemList = data.getZ001ItemList();
        // 保存抬头
        z001HeaderService.insert(header);
        // 保存调整分录
        z001ItemService.batchInsert(adjustId, z001ItemList);
    }

    /**
     * 业务数据更新
     */
    void update(Z001ParamVO z001ParamVO) {
        Z001DataVO data = z001ParamVO.getData();
        /** 抬头数据 */
        Z001Header header = data.getHeader();
        String adjustId = header.getAdjustId();
        /** 调整分录 */
        List<Z001Item> z001ItemList = data.getZ001ItemList();
        // 更新抬头
        z001HeaderService.update(header);
        // 更新调整分录
        z001ItemService.update(adjustId, z001ItemList);
    }

    /**
     * 业务数据校验
     */
    private void checkData(Z001DataVO data) {
        // 校验-抬头
        z001HeaderService.checkData(data.getHeader());
        /** 校验-调整分录 */
        z001ItemService.checkData(data.getZ001ItemList(), data.getHeader().getCmpyExp());
    }

    public CheckDataResult check(Z001DataVO data) {
        try {
            checkData(data);
        } catch (ClientCustomException e) {
            return CheckDataResult.error(data.getHeader().getWfBussId(), "E", "", e.getMessage(), null);
        }
        return CheckDataResult.success(data.getHeader().getWfBussId());
    }
}