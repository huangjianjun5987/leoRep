package com.linklaws.cloudoa.comm.datapremission.checker;

import com.linklaws.cloudoa.comm.datapremission.DataPremissionChecker;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.lawcase.dao.TeamTagMapper;
import com.linklaws.cloudoa.lawcase.model.TeamTag;
import com.linklaws.cloudoa.team.dao.TeamMemberMapper;
import com.linklaws.cloudoa.team.model.TeamMember;
import com.linklaws.core.consts.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 团队成员校验器
 *
 * @author Min.Xu
 * @date 2018-05-22 19:22
 **/
@Component
public class TeamMemberChecker implements DataPremissionChecker {

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private TeamTagMapper teamTagMapper;

    /**
     * 返回用户是否为团队成员
     *
     * @param teamId
     * @param userId
     * @return
     */
    private boolean isTeamMember(Integer teamId, Integer userId) {
        TeamMember query = new TeamMember();
        query.setState(State.enable);
        query.setTeamId(teamId);
        query.setUserId(userId);
        int count = teamMemberMapper.selectCount(query);
        return count > 0;
    }

    @Override
    public boolean check(DataType dataType, Object dataId, Integer userId) {
//        if (DataType.CASE_TEAMTAG.equals(dataType)) {
//            Integer tagId = (Integer) dataId;
//            TeamTag teamTag = teamTagMapper.selectByPrimaryKey(tagId);
//            if (teamTag == null) {
//                return false;
//            }
//            dataType = DataType.TEAM;
//            dataId = teamTag.getTeamId();
//        }
        if (DataType.TEAM.equals(dataType)) {
            Integer teamId = (Integer) dataId;
            return isTeamMember(teamId, userId);
        }
        return false;
    }

    @Override
    public boolean isSupportDataType(DataType dataType) {
        return DataType.TEAM.equals(dataType) || DataType.CASE_TEAMTAG.equals(dataType);
    }

    @Override
    public DataType getSupportDataType() {
        return DataType.TEAM;
    }
}