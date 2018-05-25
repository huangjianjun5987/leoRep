package com.linklaws.cloudoa.comm.datapremission;

/**
 * 数据权限校验实现
 *
 * @author Min.Xu
 * @date 2018-05-21 15:53
 **/
public interface DataPremissionChecker {

    /**
     * 默认方法,执行检查
     *
     * @param dataType
     * @param dataId
     * @param userId
     * @return
     */
    default public boolean doCheck(DataType dataType, Object dataId, Integer userId) {
//        一个checker支持多个DataType时用
//        if (!isSupportDataType(dataType)) {
//            throw new IllegalArgumentException(this.getClass() + " not support dataType:" + dataType);
//        }

//        一个checker仅支持一个DataType时用
        if(dataType!=getSupportDataType()){
            throw new IllegalArgumentException(this.getClass() + " not support dataType:" + dataType);
        }
        return check(dataType, dataId, userId);
    }

    /**
     * 校验用户是否有权限或角色
     *
     * @param dataType 数据类型
     * @param dataId   数据资源id
     * @param userId   用户id
     * @return true表示拥有权限，false表示没有该权限
     */
    boolean check(DataType dataType, Object dataId, Integer userId);

    /**
     * 返回该校验器是否支持某个数据类型
     *
     * @return
     */
    @Deprecated
    boolean isSupportDataType(DataType dataType);

    DataType getSupportDataType();
}
