package com.linklaws.cloudoa.comm.datapremission.checker;

import com.linklaws.cloudoa.auth.enums.TeamRole;
import com.linklaws.cloudoa.comm.datapremission.DataPremissionChecker;
import com.linklaws.cloudoa.comm.datapremission.DataType;
import com.linklaws.cloudoa.team.dao.TeamMemberMapper;
import com.linklaws.cloudoa.team.model.TeamMember;
import com.linklaws.core.consts.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 团队/部门负责人 子校验器，校验用户是不是团队负责人，可供其他校验器复用
 *
 * @author Min.Xu
 * @date 2018-05-21 18:32
 **/
@Component
public class TeamLeaderChecker implements DataPremissionChecker {

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Override
    public boolean check(DataType dataType, Object dataId, Integer userId) {
        Integer teamId = (Integer) dataId;
        return isTeamLeader(userId, teamId);
    }

    private boolean isTeamLeader(Integer userId, Integer teamId) {
        TeamMember query = new TeamMember();
        query.setState(State.enable);
        query.setTeamId(teamId);
        query.setUserId(userId);
        query.setRole(TeamRole.admin);
        int count = teamMemberMapper.selectCount(query);
        return count > 0;
    }

    @Override
    public boolean isSupportDataType(DataType dataType) {
        return DataType.TEAM.equals(dataType);
    }

    @Override
    public DataType getSupportDataType() {
        return DataType.TEAM;
    }
}
