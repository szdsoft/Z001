package com.szd.z001.z001.domain;

import com.szd.z001.base.orm.domain.z001.Z001Header;
import com.szd.z001.base.orm.domain.z001.Z001Item;
import lombok.Data;

import java.util.List;

/**
 * 总账视图对象
 */
@Data
public class Z001DataVO {
    /**总账项目抬头 */
    private Z001Header header;
    /** 调整分录列表 */
    private List<Z001Item> z001ItemList;
}