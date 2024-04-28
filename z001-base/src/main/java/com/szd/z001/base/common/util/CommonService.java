package com.szd.z001.base.common.util;

import com.alibaba.fastjson.JSONObject;
import com.szd.core.client.ClientCore;
import com.szd.core.client.domain.wf.*;
import com.szd.core.client.exception.ClientCustomException;
import com.szd.core.client.security.ClientSecurity;
import com.szd.core.client.util.ClientUtilReflect;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用方法
 *
 * @author chx
 */
@Service
public class CommonService {
    private static final Logger logger = LoggerFactory.getLogger(CommonService.class);


    @Resource
    private ClientCore clientCore;

    /**
     * sexp审批处理
     */
    public ClientWfReturn approval(Object header, ClientWfParamEvt wfEvt, String bussId, boolean accDocSap) {
        ClientWfReturn wfReturn = new ClientWfReturn();
        try {
            ClientWfParamBuss wfParamBuss = new ClientWfParamBuss();
            // 业务参数-通用
            this.setWfParamBuss(wfParamBuss, header, bussId);

            // 是否调用工作流
            if (clientCore.wf.wfCheckEvt(wfEvt.getOperate())) {
                // 调用工作流
                ClientWfParam wfParam = new ClientWfParam(wfEvt, wfParamBuss);
                wfReturn = clientCore.wf.wfCall(wfParam);
            } else {
                wfReturn.setRetCodeF("S");
                wfReturn.setRetMsg("处理成功");
            }
            if ("B".equals(wfReturn.getRetCodeF())) {
                wfReturn.setRetMsg("处理成功");
            }

            String status = Z001Util.getBussStatus(bussId);

        } catch (Exception e) {
            wfReturn.setRetCodeF("E");
            wfReturn.setRetMsg(e.getMessage());
        }
        return wfReturn;
    }

    /**
     * 保存业务主体
     */
    public void saveBussBase(Object header, String bussId, String cmpy, String cstc) {
        ClientWfBussBase wfBussBase = new ClientWfBussBase();
        BeanUtils.copyProperties(header, wfBussBase);
        wfBussBase.setWfCreateBy(ClientSecurity.getUserId());
        wfBussBase.setWfBussId(bussId);
        wfBussBase.setWfCmpy(cmpy);
        wfBussBase.setWfCstc(cstc);
        clientCore.wf.baseSave(wfBussBase);
    }

    /**
     * 设置业务参数
     */
    public ClientWfParamBuss setWfParamBuss(ClientWfParamBuss wfParamBuss, Object header, String bussId) {
        // 查询业务类型对应的流程组-流程-流程字段
        Map<String, String> wfFields = new HashMap<>(16);
        Map<String, String> stringStringMap = clientCore.wf.wfSetField(header);
        if (CollectionUtils.isEmpty(stringStringMap)) {
            stringStringMap = new HashMap<>();
        }
        if (!stringStringMap.containsKey("amtApply")) {
            stringStringMap.put("amtApply", "");
        }
        for (String code : stringStringMap.keySet()) {
            String refField = "wf" + code.substring(0, 1).toUpperCase() + code.substring(1);
            Object fieldValue = ClientUtilReflect.getFieldValue(header, refField);
            if (fieldValue != null) {
                wfFields.put(code, fieldValue.toString());
            }
        }
        wfParamBuss.setBussId(bussId);
        Object cmpy = ClientUtilReflect.getFieldValue(header, "cmpyExp");
        if (cmpy != null) {
            wfFields.put("cmpy", cmpy.toString());
        }
        Object cstc = ClientUtilReflect.getFieldValue(header, "cstcExp");
        if (cstc != null) {
            wfFields.put("cstc", cstc.toString());
        }
        wfParamBuss.setWfFileds(wfFields);
        return wfParamBuss;
    }

    /**
     * 获取业务主体描述
     *
     * @param flag (0-code.name;1-name)
     */
    public void getBussBaseDesc(Object obj, String flag) {
        ClientWfBussBase wfBussBase = new ClientWfBussBase();
        BeanUtils.copyProperties(obj, wfBussBase);
        getDesc(wfBussBase, flag);
        BeanUtils.copyProperties(wfBussBase, obj);
    }

    public void getDesc(ClientWfBussBase wfBussBase, String flag) {
        wfBussBase.setWfCmpyName(clientCore.mdm.getDescA("CORE_CMPY", wfBussBase.getWfCmpy(), flag));
        wfBussBase.setWfCstcName(clientCore.mdm.getDescA("CORE_CSTC", wfBussBase.getWfCstc(), flag));
        wfBussBase.setWfBstpName(clientCore.mdm.getDescA("CORE_BSTP", wfBussBase.getWfBstp(), flag));
        if ("0".equals(flag)) {
            wfBussBase.setWfStatusName(clientCore.mdm.getDescDict("CORE_WF_DOST", wfBussBase.getWfStatus(), flag));
            wfBussBase.setWfRejectStatusName(clientCore.mdm.getDescDict("CORE_SYS_YENO", wfBussBase.getWfRejectStatus(), flag));
        }
        wfBussBase.setWfCreateByName(clientCore.mdm.getDescA("CORE_USER", wfBussBase.getWfCreateBy(), flag));
        wfBussBase.setWfCommitByName(clientCore.mdm.getDescA("CORE_USER", wfBussBase.getWfCommitBy(), flag));
        wfBussBase.setWfUpdateByName(clientCore.mdm.getDescA("CORE_USER", wfBussBase.getWfUpdateBy(), flag));
        ClientWfDyInfo wfDyInfo = null;
        if (StringUtils.isNotBlank(wfBussBase.getWfDyId())) {
            wfDyInfo = clientCore.wf.dyGetObj(wfBussBase.getWfDyId());
        }
        if (wfDyInfo != null) {
            wfBussBase.setWfDyNo(String.valueOf(wfDyInfo.getWfDyNo()));
            if ("0".equals(flag)) {
                wfBussBase.setWfDyName(wfDyInfo.getWfDyNo() + "." + wfDyInfo.getWfDyName());
            } else {
                wfBussBase.setWfDyName(wfDyInfo.getWfDyName());
            }
        } else {
            wfBussBase.setWfDyNo("");
            wfBussBase.setWfDyName("");
        }
    }

    /**
     * 处理字段状态组
     */
    public Object handleBstp(Object object, String bstp) {
        //得到配置
        JSONObject fstgConfig = clientCore.tool.getFstg(bstp);
        //得到组件和bean的映射关系
        //遍历key和value
        for (Map.Entry<String, Object> entry : fstgConfig.entrySet()) {
            JSONObject json = (JSONObject) JSONObject.toJSON(entry.getValue());
            String dataObj = (String) json.get("dataObj");
            //配置了组件对应的对象
            if (StringUtils.isNotBlank(dataObj)) {
                //有可能是多层嵌套
                if (dataObj.contains(".")) {
                    String[] split = StringUtils.split(dataObj, ".");
                    this.handleMultModule(split, 0, object, json);
                } else {
                    Object fieldValue = ClientUtilReflect.getFieldValue(object, dataObj);
                    fieldValue = this.handleModule(fieldValue, json);
                    ClientUtilReflect.setFieldValue(object, dataObj, fieldValue);
                }
            }
        }
        return object;
    }

    /**
     * 多次嵌套处理组件
     */
    private Object handleMultModule(String[] split, int num, Object object, JSONObject json) {
        Object fieldValue = null;
        if (split.length > num) {
            fieldValue = ClientUtilReflect.getFieldValue(object, split[num]);
            if (fieldValue instanceof Collection) {
                List<Object> list = (List<Object>) fieldValue;
                for (Object obj : list) {
                    //如果是最后一层
                    if (split.length == num + 1) {
                        this.handleModule(fieldValue, json);
                    } else {
                        this.handleMultModule(split, num + 1, obj, json);
                    }
                }
            } else {
                if (split.length == num + 1) {
                    this.handleModule(fieldValue, json);
                } else {
                    this.handleMultModule(split, num + 1, fieldValue, json);
                }
            }
        }
        return fieldValue;
    }

    /**
     * 处理单个组件
     */
    private Object handleModule(Object fieldValue, JSONObject json) {
        // 判断要修改的对象是否是集合
        if (fieldValue != null) {
            if (fieldValue instanceof Collection) {
                List<Object> list = (List<Object>) fieldValue;
                for (Object obj : list) {
                    //处理对应的列字段
                    JSONObject table = (JSONObject) json.get("table");
                    obj = this.handleField(table, obj);
                    //处理对应的列行
                    JSONObject tbSub = (JSONObject) json.get("tbSub");
                    if (tbSub != null) {
                        for (Map.Entry<String, Object> ent : tbSub.entrySet()) {
                            String key = ent.getKey();
                            JSONObject tbSubTwo = (JSONObject) ent.getValue();
                            Object bstpItemObj = ClientUtilReflect.getFieldValue(obj, "bsub");
                            if (bstpItemObj != null) {
                                // 2必输 5隐藏清空
                                if (bstpItemObj.equals(key)) {
                                    obj = this.handleField(tbSubTwo, obj);
                                }
                            }
                        }
                    }
                    //处理 字段和按钮配置
                    JSONObject fst = (JSONObject) JSONObject.toJSON(json.get("fst"));
                    this.handleField(fst, obj);
                }
            } else {
                //处理 字段和按钮配置
                JSONObject fst = (JSONObject) JSONObject.toJSON(json.get("fst"));
                fieldValue = this.handleField(fst, fieldValue);
            }
        }
        return fieldValue;
    }

    /**
     * 处理单个字段
     */
    private Object handleField(JSONObject json, Object fieldValue) {
        if (json != null) {
            for (Map.Entry<String, Object> ent : json.entrySet()) {
                // 2必输 5隐藏清空
                String key = ent.getKey();
                String value = (String) ent.getValue();
                try {
                    if ("2".equals(value)) {
                        Field field = ClientUtilReflect.getAccessibleField(fieldValue, key);
                        // 字段找不到有可能是按钮
                        if (field != null) {
                            Object objKeyVlaue = field.get(fieldValue);
                            if (objKeyVlaue == null || "".equals(objKeyVlaue.toString())) {
                                throw new ClientCustomException(key + "不能为空");
                            }
                        }
                    }
                    if ("5".equals(value)) {
                        Field field = ClientUtilReflect.getAccessibleField(fieldValue, key);
                        String type = field.getType().getName();
                        if ("byte".equals(type) || "short".equals(type) || "int".equals(type) || "long".equals(type) || "double".equals(type) || "float".equals(type)) {
                            ClientUtilReflect.setFieldValue(fieldValue, key, 0);
                        } else if ("char".equals(type)) {
                            ClientUtilReflect.setFieldValue(fieldValue, key, "");
                        } else if ("boolean".equals(type)) {
                            ClientUtilReflect.setFieldValue(fieldValue, key, false);
                        } else {
                            ClientUtilReflect.setFieldValue(fieldValue, key, null);
                        }
                    }
                } catch (ClientCustomException var3) {
                    throw var3;
                } catch (Exception var4) {
                    throw new ClientCustomException(key + "配置信息有误:" + var4.getMessage());
                }
            }
        }
        return fieldValue;
    }
}