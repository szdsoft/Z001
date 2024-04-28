package com.szd.z001.z001.domain;

import java.util.HashMap;

/**
 * 数据检查返回体
 *
 * @author szd
 */
public class CheckDataResult extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;
    public static final String STATUS = "status";
    public static final String CODE = "code";
    public static final String MSG = "msg";
    public static final String DATA = "data";
    public static final String BUSS_ID = "bussId";

    /**
     * 初始化一个新创建的对象，使其表示一个空消息。
     */
    public CheckDataResult() {
    }

    /**
     * 初始化一个新创建的 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public CheckDataResult(String bussId, String status, String code, String msg, Object data) {
        super.put(BUSS_ID, bussId);
        super.put(STATUS, status);
        super.put(CODE, code);
        super.put(MSG, msg);
        super.put(DATA, data);
    }

    /**
     * 返回成功消息
     */
    public static CheckDataResult success(String bussId) {
        return CheckDataResult.success(bussId, "S", "", "检查通过");
    }

    /**
     * 返回成功消息
     */
    public static CheckDataResult success(String bussId, String status, String code, String msg) {
        return new CheckDataResult(bussId, status, code, msg, null);
    }

    /**
     * 返回失败消息
     */
    public static CheckDataResult error(String bussId, String status, String code, String msg, Object data) {
        return new CheckDataResult(bussId, status, code, msg, data);
    }
}