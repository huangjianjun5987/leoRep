package com.linklaws.cloudoa.comm.datapremission.idconverter;

import com.linklaws.cloudoa.comm.datapremission.DataIdConverter;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.lawcase.dao.CaseBaseMapper;
import com.linklaws.cloudoa.lawcase.model.CaseBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 根据caseId获得teamId转换器
 *
 * @author Min.Xu
 * @date 2018-05-23 16:14
 **/
@Component
public class CaseId2TeamIdConverter implements DataIdConverter {

    @Autowired
    private CaseBaseMapper caseBaseMapper;

    @Override
    public DataType formType() {
        return DataType.CASE;
    }

    @Override
    public DataType toType() {
        return DataType.TEAM;
    }

    @Override
    public Object convert(Object dataId) {
        Integer caseId = (Integer) dataId;
        CaseBase caseBase = caseBaseMapper.selectByPrimaryKey(caseId);
        return caseBase == null ? null : caseBase.getTeamId();
    }

}
