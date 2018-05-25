package com.linklaws.cloudoa.comm.datapremission.idconverter;

import com.linklaws.cloudoa.comm.datapremission.DataIdConverter;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.lawcase.dao.CaseFlowMapper;
import com.linklaws.cloudoa.lawcase.model.CaseFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 阶段流程id转换为caseId
 *
 * @author Min.Xu
 * @date 2018-05-24 14:00
 **/
@Component
public class FlowId2CaseIdConverter implements DataIdConverter {

    @Autowired
    private CaseFlowMapper caseFlowMapper;

    @Override
    public Object convert(Object dataId) {
        Integer flowId = (Integer) dataId;
        CaseFlow caseFlow = caseFlowMapper.selectByPrimaryKey(flowId);
        return caseFlow == null ? null : caseFlow.getCaseId();
    }

    @Override
    public DataType formType() {
        return DataType.FLOW;
    }

    @Override
    public DataType toType() {
        return DataType.CASE;
    }
}
