package com.szd.z001.z001.controller;

import com.szd.core.client.ClientCore;
import com.szd.core.client.constants.ClientConstMdm;
import com.szd.core.client.domain.common.ClientAjaxResult;
import com.szd.core.client.domain.common.ClientPageTableRet;
import com.szd.core.client.domain.mdm.ClientObjCmpy;
import com.szd.core.client.domain.wf.ClientWfOper;
import com.szd.core.client.exception.ClientCustomException;
import com.szd.core.client.service.ClientBaseController;
import com.szd.z001.base.common.constant.Constants;
import com.szd.z001.base.common.util.QRUtils;
import com.szd.z001.base.common.util.CommonService;
import com.szd.z001.base.orm.domain.z001.Z001Header;
import com.szd.z001.base.orm.domain.z001.Z001RpParamVO;
import com.szd.z001.base.orm.domain.z001.Z001Item;
import com.szd.z001.z001.domain.*;
import com.szd.z001.z001.service.Z001Service;
import com.szd.z001.z001.service.Z001ItemService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 总账清单接口
 */
@RestController
@RequestMapping("/z001")
public class Z001Controller extends ClientBaseController {
    @Resource
    private Z001Service z001Service;
    @Resource
    private CommonService commonService;
    @Resource
    private Z001ItemService z001ItemService;
    @Resource
    private ClientCore clientCore;

    /**
     * 总账清单报表
     */
    @PostMapping("/list")
    public ClientPageTableRet list(@RequestBody Z001RpParamVO header) {
        startPage();
        header.setRouterCode(Constants.Z001_01A);
        List<Z001Header> list = z001Service.selectList(header);
        getDescList(list);
        return getPageTable(list);
    }

    /**
     * 页面查询(包含业务数据、流程按钮、字段状态)
     */
    @GetMapping("/get")
    public ClientAjaxResult getData(String bussId, String userId, String option) {
        // 业务数据
        Z001DataVO data = z001Service.selectData(bussId);
        // 添加描述
        getDesc(data);
        // 流程操作授权
        ClientWfOper wfOper = clientCore.wf.wfInit(bussId, userId, option);
        // 返回
        Z001ResultVO dataResultVO = new Z001ResultVO();
        dataResultVO.setData(data);
        dataResultVO.setWfOper(wfOper);
        return ClientAjaxResult.success(dataResultVO);
    }

    /**
     * 页面按钮操作(保存草稿，提交，同意，驳回。。。)
     */
    @PostMapping("/save")
    public ClientAjaxResult saveData(@RequestBody Z001ParamVO z001ParamVO) {
        // 保存业务数据
        try {
            z001ParamVO.getData().getHeader().setRouterCode(Constants.Z001_01A);
            Z001ReturnVO z001ReturnVO = z001Service.saveData(z001ParamVO);
            return ClientAjaxResult.success(z001ReturnVO);
        } catch (ClientCustomException clientCustomException) {
            return ClientAjaxResult.error(clientCustomException.getMessage());
        }
    }

    /**
     * 数据检查
     */
    @PostMapping("/check")
    public CheckDataResult checkData(@RequestBody Z001ParamVO z001ParamVO) {
        return z001Service.check(z001ParamVO.getData());
    }

    /**
     * 打印
     */
    @PostMapping("/print")
    public ClientAjaxResult printData(@RequestBody String[] bussIds) {
        List<Z001DataPrintVO> printVOList = new ArrayList<>();
        for (String bussId : bussIds) {
            // 返回打印页面数据及包含bussId的二维码
            Z001DataPrintVO printVO = new Z001DataPrintVO();
            printVO.setTitle(Constants.Z001_01A_DESC);
            // 业务数据
            Z001DataVO data = z001Service.selectData(bussId);
            getDescHeader(data.getHeader());
            printVO.setHeader(data.getHeader());
            // 调整信息
            printVO.setZ001ItemList(data.getZ001ItemList());
            printVOList.add(printVO);
            printVO.setQrCode(QRUtils.getQRCode("B39." + bussId));
        }
        return ClientAjaxResult.success(printVOList);
    }

    /**
     * 获取字段描述-报表
     */
    private void getDescList(List<Z001Header> list) {
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(header -> {
                header.setUserName(clientCore.mdm.getDescA("CORE_USER", header.getUserId(), "0"));
                header.setCmpyNameExp(clientCore.mdm.getDescA("CORE_CMPY", header.getCmpyExp(), "1"));
                header.setCstcNameExp(clientCore.mdm.getDescA("CORE_CSTC", header.getCstcExp(), "1"));
                header.setBstpName(clientCore.mdm.getDescA("CORE_BSTP", header.getBstp(), "1"));
                header.setCurrName(clientCore.mdm.getDescA("CORE_CURR", header.getCurr(), "1"));
                header.setStatusName(clientCore.mdm.getDescDict("SEXP_DOC_STAU", header.getStatus(), "0"));
                commonService.getBussBaseDesc(header, "0");
            });
        }
    }

    /**
     * 获取字段描述
     */
    private void getDesc(Z001DataVO data) {
        ClientObjCmpy cmpyObj = clientCore.mdm.getObjA(ClientConstMdm.CORE_CMPY, data.getHeader().getCmpyExp(), ClientObjCmpy.class);
        getDescHeader(data.getHeader());
        getDescAccAdjust(data.getZ001ItemList(), cmpyObj);
    }

    /**
     * 获取字段描述-抬头
     */
    private void getDescHeader(Z001Header header) {
        if (header != null) {
            header.setUserName(clientCore.mdm.getDescA("CORE_USER", header.getUserId(), "1"));
            header.setCmpyNameExp(clientCore.mdm.getDescA("CORE_CMPY", header.getCmpyExp(), "1"));
            header.setCstcNameExp(clientCore.mdm.getDescA("CORE_CSTC", header.getCstcExp(), "1"));
            header.setBstpName(clientCore.mdm.getDescA("CORE_BSTP", header.getBstp(), "1"));
            header.setCurrName(clientCore.mdm.getDescA("CORE_CURR", header.getCurr(), "1"));
            header.setStatusName(clientCore.mdm.getDescDict("SEXP_DOC_STAU", header.getStatus(), "0"));
            commonService.getBussBaseDesc(header, "1");
        }
    }

    /**
     * 获取字段描述-调整分录
     */
    private void getDescAccAdjust(List<Z001Item> z001ItemList, ClientObjCmpy cmpyObj) {
        z001ItemService.getDescAccAdjust(z001ItemList, cmpyObj);
    }
}