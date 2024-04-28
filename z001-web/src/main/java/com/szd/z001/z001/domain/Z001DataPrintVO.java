package com.szd.z001.z001.domain;

import com.szd.z001.base.orm.domain.z001.Z001Header;
import com.szd.z001.base.orm.domain.z001.Z001Item;
import lombok.Data;

import java.util.List;

/**
 * 总账单据打印视图对象
 */
@Data
public class Z001DataPrintVO {
    /** 打印标题 */
    private String title;
    /** 单据二维码 */
    private String qrCode;
    /** 单据信息 */
    private Z001Header header;
    /** 调整信息 */
    private List<Z001Item> z001ItemList;
}