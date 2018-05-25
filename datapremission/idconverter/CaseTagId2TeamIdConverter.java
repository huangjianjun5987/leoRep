package com.linklaws.cloudoa.comm.datapremission.idconverter;

import com.linklaws.cloudoa.comm.datapremission.DataIdConverter;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.lawcase.dao.TeamTagMapper;
import com.linklaws.cloudoa.lawcase.model.TeamTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 项目标签id转为团队id
 *
 * @author Min.Xu
 * @date 2018-05-23 16:14
 **/
@Component
public class CaseTagId2TeamIdConverter implements DataIdConverter {

    @Autowired
    private TeamTagMapper teamTagMapper;

    @Override
    public DataType formType() {
        return DataType.CASE_TEAMTAG;
    }

    @Override
    public DataType toType() {
        return DataType.TEAM;
    }

    @Override
    public Object convert(Object dataId) {
        Integer tagId = (Integer) dataId;
        TeamTag teamTag = teamTagMapper.selectByPrimaryKey(tagId);
        return teamTag == null ? null : teamTag.getTeamId();
    }

}
