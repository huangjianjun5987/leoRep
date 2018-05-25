package com.linklaws.cloudoa.comm.datapremission;

/**
 * 数据id转换器。根据给出的数据类型的数据id得到目标数据类型的数据id
 *
 * @author Min.Xu
 * @date 2018-05-23 16:11
 **/
public interface DataIdConverter {

    public Object convert(Object dataId);

    public DataType formType();

    public DataType toType();
}
