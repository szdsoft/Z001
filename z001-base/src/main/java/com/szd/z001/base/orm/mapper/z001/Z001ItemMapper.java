package com.szd.z001.base.orm.mapper.z001;

import com.szd.z001.base.orm.domain.z001.Z001Item;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 费用-调整分录Mapper接口
 */
public interface Z001ItemMapper {
    /**
     * 查询费用-调整分录列表
     */
    List<Z001Item> selectList(String costId);
    /**
     * 查询费用-调整分录详情
     */
    Z001Item selectById(@Param("costId") String costId, @Param("itemNo") String itemNo);
    /**
     * 新增费用-调整分录
     */
    int batchInsert(List<Z001Item> list);
    /**
     * 删除费用-调整分录
     */
    int deleteByCostId(String costId);
}