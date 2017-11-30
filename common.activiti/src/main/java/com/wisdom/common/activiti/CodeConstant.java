package com.wisdom.common.activiti;

/**
 * 用于Code表中使用的常量
 * 
 * @author shanglei
 * @date 2016/03/22
 */
public class CodeConstant {

    /*
     * 审核状态
     */
    public static final String AUDIT_EDIT = "E"; // 编制
    public static final String AUDIT_RETURN = "R"; // 退回
    public static final String AUDIT_AUDITING = "A"; // 审核中
    public static final String AUDIT_PASS = "P"; // 通过

    /*
     * 流程判断条件
     */
    public static final String AUDIT_CONDITION="auditCondition";

    /*
     * 提交人标识
     */
    public static final String SUBMIT_USER = "submitUser";

    /*
     * 临时测试使用
     */
    public static final String TEMP_TEST = "TEST";

    /*
     * 流程申请节点标识
     */
    public static final String FLOW_START = "S";
    /*
     * 标识符
     */
   /* public static final String PASS = "P|"; // 通过标识
    
    public static final String RETURN = "R|";// 退回标识
   
    public static final String START = "S|"; // 开始标识
    
    public static final String RESTART = "RS|";// 重整后提交
*/   
    public static final String SEPARATION = "|";
    
    public static final String SPLIT = "\\|"; // 分隔符

    /*
     * activiti类型判断
     */ 
    public static final String ACT_TYPE_TASK = "userTask";      //任务节点

    public static final String ACT_TYPE_COMMENT = "AddComment"; //描述数据
    
    public static final String CONDITION_TEXT="conditionText";  //流程线上表达式

    /*
     * 操作状态
     */
    
    public static final String START_STATUS="提交审批";
    
    public static final String ADJUST_STATUS = "调整中";

    public static final String AUDIT_STATUS = "审核中";

    public static final String UNDEFIEND_STATUS = "操作不确定";

    /*
     * 业务数据url
     */
    public static final String BUSINESS_URL = "businessURL";
    
    /*
     * 业务数据参数名
     */
    public static final String BUSINESS_PARAM = "businessParam";


    /*
     * 条件判断参数
     */
    public static final String CONDITION_PARAM = "conditionParam";
    
    /*
     * 并行网关输入口
     */
    public static final String PARALEL_IN = "PIn";

    /*
     * 并行网关输出口
     */
    public static final String PARALEL_OUT = "POut";
    
    /*
     * 流程审核类型判断
     */
    public static final String DESCRIPTION ="Y";
    
    /*
     * 数据库类型
     *
     */
    public static final String DB2 ="DB2";
    
    public static final String ORACLE ="ORACLE";
    
    public static final String MYSQL ="MySQL";
    
    
}
