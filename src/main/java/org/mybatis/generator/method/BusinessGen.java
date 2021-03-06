package org.mybatis.generator.method;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.constant.MethodEnum;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.utils.CommentUtils;
import org.mybatis.generator.utils.ExceptionUtils;
import org.mybatis.generator.utils.MethodUtils;

/**
 * Description: business方法生成
 * Author: guos
 * Date: 2019/1/31 12:37
 **/
public class BusinessGen {

    private String responseMethod;

    private String countAlias;
    /**
     * 对象转换类
     */
    private String modelConvertUtils;

    /**
     * 是否生成logger日志
     */
    private boolean enableLogger;


    public BusinessGen() {
    }

    public BusinessGen(String responseMethod, String countAlias, String modelConvertUtils, boolean enableLogger) {
        this.responseMethod = responseMethod;
        this.countAlias = countAlias;
        this.modelConvertUtils = modelConvertUtils;
        this.enableLogger = enableLogger;
    }

    /**
     * 添加方法
     */
    public Method selectByPrimaryKey(FullyQualifiedJavaType serviceType, IntrospectedTable introspectedTable, String alias) {
        Method method = new Method();
        method.setName(alias);
        String poName = introspectedTable.getDomainObjectName();
        String voName = CommonGen.getObjectWithSuffix(poName, CommonConstant.VO_SUFFIX);
        method.setReturnType(new FullyQualifiedJavaType(voName));
        CommonGen.setMethodPrimaryKey(introspectedTable, method);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);

        StringBuilder sb = new StringBuilder();
        sb.append(CommonGen.getShortName(serviceType));
        sb.append(alias);
        sb.append("(");
        String idPro = introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty();
        sb.append(idPro);
        sb.append(")");
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, idPro);
        }
        String lowPo = MethodUtils.toLowerCase(poName);
        String lowVo = MethodUtils.toLowerCase(voName);
        method.addBodyLine(poName + " " + lowPo + " = " + sb.toString() + ";");

        method.addBodyLine(voName + " " + lowVo + " = new " + voName + "();");
        method.addBodyLine("if (" + lowPo + " == null) {");
        method.addBodyLine("return " + lowVo + ";");
        method.addBodyLine("}");
        method.addBodyLine("BeanUtils.copyProperties(" + lowPo + ", " + lowVo + ");");
        method.addBodyLine("return " + lowVo + ";");
        return method;
    }


    /**
     * 添加方法
     */
    public Method delete(FullyQualifiedJavaType serviceType, IntrospectedTable introspectedTable, String alias) {
        Method method = new Method();
        method.setName(alias);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        CommonGen.setMethodPrimaryKey(introspectedTable, method);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);

        StringBuilder sb = new StringBuilder();
        sb.append(CommonGen.getShortName(serviceType));
        sb.append(alias);
        sb.append("(");
        String idPro = introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty();
        sb.append(idPro);
        sb.append(")");
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, idPro);
        }
        method.addBodyLine("return " + sb.toString() + ";");
        return method;
    }


    /**
     * Description:新增或修改
     * param serviceType
     * param introspectedTable
     * param alias
     * Author: guos
     * Date: 2019/1/31 14:33
     * Return:
     **/
    public Method insertOrUpdate(FullyQualifiedJavaType serviceType, IntrospectedTable introspectedTable, String alias, String exceptionPack, String versions, boolean enableVersions) {
        Method method = new Method();
        method.setName(alias);
        String poName = introspectedTable.getDomainObjectName();
        String voName = CommonGen.getObjectWithSuffix(poName, CommonConstant.VO_SUFFIX);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        CommonGen.setMethodParameter(method, poName, CommonConstant.VO_SUFFIX);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);

        String param = MethodUtils.toLowerCase(poName);
        StringBuilder sb = new StringBuilder();
        sb.append(CommonGen.getShortName(serviceType));
        sb.append(alias);
        sb.append("(");
        sb.append(param);
        sb.append(")");
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, MethodUtils.toLowerCase(poName + CommonConstant.VO_SUFFIX));
        }

        String lowPo = MethodUtils.toLowerCase(poName);
        String lowVo = MethodUtils.toLowerCase(voName);
        String remarks = introspectedTable.getRemarks();
        String oldName = "old" + poName;
        boolean hasVersions = false;
        if (MethodEnum.UPDATE.getValue().equals(alias)) {
            //查询
            for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
                if (enableVersions && column.getActualColumnName().equals(versions)) {
                    hasVersions = true;
                    break;
                }
            }
        }
        method.addBodyLine("if (" + lowVo + " == null) {");
        method.addBodyLine(ExceptionUtils.generateCode(new FullyQualifiedJavaType(exceptionPack).getShortName(), remarks, "不能为空!"));
        method.addBodyLine("}");
        if (hasVersions) {
            method.addBodyLine(poName + " " + oldName + " = this." + MethodEnum.GET.getValue() + "(" + lowVo + ".get" + MethodUtils.toUpperCase(introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty()) + "());");
            method.addBodyLine("if (" + oldName + " == null) {");
            method.addBodyLine(ExceptionUtils.generateCode(new FullyQualifiedJavaType(exceptionPack).getShortName(), remarks, "不存在!") + "");
            method.addBodyLine("}");
        }
        method.addBodyLine(poName + " " + lowPo + " = new " + poName + "();");
        method.addBodyLine("BeanUtils.copyProperties(" + lowVo + ", " + lowPo + ");");
        if (hasVersions) {
            method.addBodyLine(lowPo + ".set" + MethodUtils.toUpperCase(versions) + "(" + oldName + ".get" + MethodUtils.toUpperCase(versions) + "());");
        }
        method.addBodyLine("return " + sb.toString() + ";");
        return method;
    }


    /**
     * 添加方法
     */
    public Method listByCondition(FullyQualifiedJavaType serviceType, IntrospectedTable introspectedTable, String alias, String page) {

        Method method = new Method();
        method.setName(alias);
        String poName = introspectedTable.getDomainObjectName();
        String voName = CommonGen.getObjectWithSuffix(poName, CommonConstant.VO_SUFFIX);
        String condName = CommonGen.getObjectWithSuffix(poName, CommonConstant.COND_SUFFIX);

        FullyQualifiedJavaType returnType = MethodUtils.getResponseType(responseMethod, "List<" + voName + ">");
        String resMethod = MethodUtils.getResponseMethod(responseMethod, 2);
        method.setReturnType(returnType);
        CommonGen.setMethodParameter(method, poName, CommonConstant.VO_SUFFIX);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);

        String param = MethodUtils.toLowerCase(condName);
        String paramVo = MethodUtils.toLowerCase(voName);

        String methodPrefix = CommonGen.getShortName(serviceType);

        StringBuilder sb = new StringBuilder();
        sb.append(methodPrefix);
        sb.append(MethodEnum.LIST_BY_CONDITION.getValue());
        sb.append("(");
        sb.append(param);
        sb.append(")");

        String res = returnType.getShortName() + " result = " + resMethod.replaceFirst("\\$", "Lists.newArrayList()") + ";";
        res = res.replace("$", " 0");
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, paramVo);
        }
        String voListName = MethodUtils.toLowerCase(voName) + "List";
        method.addBodyLine(res);
        String ltCondName = MethodUtils.toLowerCase(condName);
        method.addBodyLine("Condition<" + poName + "> " + ltCondName + " = new Condition<>();");
        if (StringUtility.stringHasValue(page)) {
            method.addBodyLine(ltCondName + ".limit(" + paramVo + ".getNum(), " + paramVo + ".getRow());");
        }
        method.addBodyLine("int count = " + methodPrefix + MethodEnum.COUNT_BY_CONDITION.getValue() + "(" + ltCondName + ");");
        method.addBodyLine("if (count == 0){");
        method.addBodyLine("return result;");
        method.addBodyLine("}");


        //如果有对象转换工具类
        if (StringUtility.stringHasValue(modelConvertUtils)) {
            method.addBodyLine("List<" + voName + "> " + voListName + " = ModelConvertUtils.convertList(" + voName + ".class, " + sb.toString() + ");");
        } else {
            method.addBodyLine("List<" + voName + "> " + voListName + " = " + sb.toString() + ".stream().map(e -> {");
            method.addBodyLine(voName + " vo = new " + voName + "();");
            method.addBodyLine("BeanUtils.copyProperties(e, vo);");
            method.addBodyLine("return vo;");
            method.addBodyLine("}).collect(Collectors.toList());");
        }
        res = "return " + resMethod.replaceFirst("\\$", voListName) + ";";
        sb.setLength(0);
        res = res.replace("$", " count");
        method.addBodyLine(res);
        return method;
    }


    /**
     * 添加方法
     */
    public Method count(FullyQualifiedJavaType serviceType, IntrospectedTable introspectedTable, String alias) {
        Method method = new Method();
        method.setName(alias);
        String poName = introspectedTable.getDomainObjectName();
        String voName = CommonGen.getObjectWithSuffix(poName, CommonConstant.VO_SUFFIX);
        String condName = CommonGen.getObjectWithSuffix(poName, CommonConstant.COND_SUFFIX);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        CommonGen.setMethodParameter(method, poName, CommonConstant.VO_SUFFIX);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);

        String param = MethodUtils.toLowerCase(condName);
        String paramVo = MethodUtils.toLowerCase(voName);
        StringBuilder sb = new StringBuilder();
        sb.append(CommonGen.getShortName(serviceType));
        sb.append(MethodEnum.COUNT_BY_CONDITION.getValue());
        sb.append("(");
        sb.append(param);
        sb.append(")");
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, paramVo);
        }
        String ltCondName = MethodUtils.toLowerCase(condName);
        method.addBodyLine("Condition<" + poName + "> " + ltCondName + " = new Condition<>();");
        method.addBodyLine("return " + sb.toString() + ";");
        return method;
    }


    /**
     * 处理分批查询的数据
     * param serviceType
     * param introspectedTable
     * param alias
     * author  guos
     * date 2019/3/22 16:42
     * return
     **/
    public Method doBatch(FullyQualifiedJavaType serviceType, IntrospectedTable introspectedTable, String alias, String page) {
        Method method = new Method();
        method.setName(alias);
        String poName = introspectedTable.getDomainObjectName();
        String voName = CommonGen.getObjectWithSuffix(poName, CommonConstant.VO_SUFFIX);
        String condName = CommonGen.getObjectWithSuffix(poName, CommonConstant.COND_SUFFIX);
        method.setReturnType(new FullyQualifiedJavaType("void"));
        CommonGen.setMethodParameter(method, poName, CommonConstant.VO_SUFFIX);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);

        String paramVo = MethodUtils.toLowerCase(voName);
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, paramVo);
        }
        String ltCondName = MethodUtils.toLowerCase(condName);
        String pageSizeStr = MethodUtils.getClassName(page) + ".getMaxRow() - 1";
        method.addBodyLine("Condition<" + poName + "> " + ltCondName + " = new Condition<>();");
        method.addBodyLine("int size = " + pageSizeStr + " ;");
        method.addBodyLine("int gtId = 0;");
        method.addBodyLine("while (size >= " + pageSizeStr + ") {");
        method.addBodyLine("List<" + poName + "> list = " + CommonGen.getShortName(serviceType) + MethodEnum.BATCH_LIST.getValue() + "(gtId," + ltCondName + ");");
        method.addBodyLine("if (CollectionUtils.isEmpty(list)) {");
        method.addBodyLine("break;");
        method.addBodyLine("}");
        method.addBodyLine("size = list.size();");
        method.addBodyLine("gtId = list.get(size - 1).getId();");
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, paramVo);
        }
        method.addBodyLine("}");
        return method;
    }
}
