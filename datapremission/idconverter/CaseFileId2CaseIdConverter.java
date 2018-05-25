package com.linklaws.cloudoa.comm.datapremission.idconverter;

import com.linklaws.cloudoa.comm.datapremission.DataIdConverter;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.lawcase.dao.CaseUploadRecordMapper;
import com.linklaws.cloudoa.lawcase.model.CaseTask;
import com.linklaws.cloudoa.lawcase.model.CaseUploadRecord;
import com.linklaws.core.consts.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 根据caseFileId获得caseid转换器
 *
 * @author Min.Xu
 * @date 2018-05-23 16:14
 **/
@Component
public class CaseFileId2CaseIdConverter implements DataIdConverter {

    @Autowired
    CaseUploadRecordMapper caseUploadRecordMapper;

    @Override
    public DataType formType() {
        return DataType.CASE_FILE;
    }

    @Override
    public DataType toType() {
        return DataType.CASE;
    }

    @Override
    public Object convert(Object dataId) {
        Integer caseFileId = (Integer) dataId;
        CaseUploadRecord record = new CaseUploadRecord();
        record.setId(caseFileId);
        record.setState(State.enable);
        CaseUploadRecord file = caseUploadRecordMapper.selectOne(record);
        return file == null ? null : file.getCaseId();
    }

}
