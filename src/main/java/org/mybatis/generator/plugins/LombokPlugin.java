package org.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.utils.CommentUtils;
import org.mybatis.generator.utils.ContextUtils;
import org.mybatis.generator.utils.MethodUtils;

import java.util.List;

/**
 * @author Liweizhou  2018/6/6
 */
public class LombokPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        boolean hasLombok = this.hasLombok();
        if (hasLombok) {
            PluginConfiguration extendModelPlugin = ContextUtils.checkExtendModelPlugin(context);
            //添加domain的import
            if (!topLevelClass.getType().getShortName().contains(CommonConstant.VO_SUFFIX)) {
                String tableAnno = extendModelPlugin.getProperty("table");
                topLevelClass.addImportedType(tableAnno);
                topLevelClass.addAnnotation("@" + MethodUtils.getClassName(tableAnno, ".") + "(name = \"" + introspectedTable.getTableName() + "\")");
            }
            topLevelClass.addImportedType("lombok.Data");
            //添加domain的注解
            topLevelClass.addAnnotation("@Data");
        }
        //添加domain的注释
        CommentUtils.addModelClassComment(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //Mapper文件的注释
        CommentUtils.addMapperComment(interfaze, introspectedTable);
        return true;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        //不生成getter
        if (this.hasLombok()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        //不生成setter
        //不生成getter
        if (this.hasLombok()) {
            return false;
        }
        return true;
    }


    private boolean hasLombok() {
        String hasLombok = ContextUtils.getPlugin(context, CommonConstant.LOMBOK_PLUGIN_NAME).getProperty("hasLombok");
        if ("true".equalsIgnoreCase(hasLombok)) {
            return true;
        }
        return false;
    }
}