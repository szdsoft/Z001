package com.szd.z001.base.orm.mapper.z001;

import com.szd.z001.base.orm.domain.z001.Z001Header;
import com.szd.z001.base.orm.domain.z001.Z001RpParamVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 总账业务抬头Mapper接口
 */
public interface Z001HeaderMapper {
    List<Z001Header> selectList(Z001RpParamVO header);
    Z001Header selectById(String adjustId);
    int insert(Z001Header z001Header);
    int update(Z001Header z001Header);
    void updateStatusAndId(@Param("bussId") String bussId, @Param("status") String status, @Param("bussDocId") String bussDocId);
    void updateStatusByBussId(@Param("status") String status, @Param("bussDocId") String bussDocId);
    int deleteById(String adjustId);

}