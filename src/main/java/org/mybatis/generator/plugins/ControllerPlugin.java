package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.constant.MethodEnum;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.method.ControllerGen;
import org.mybatis.generator.utils.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Description:controller生成插件
 * Author: guos
 * Date: 2019/1/30 14:26
 **/
public class ControllerPlugin extends PluginAdapter {

    private FullyQualifiedJavaType slf4jLogger;
    private FullyQualifiedJavaType slf4jLoggerFactory;
    private FullyQualifiedJavaType classAnnotation;

    private FullyQualifiedJavaType controllerType;

    private FullyQualifiedJavaType businessType;

    private FullyQualifiedJavaType listType;

    private FullyQualifiedJavaType returnType;

    private FullyQualifiedJavaType pojoType;

    private FullyQualifiedJavaType voType;

    private FullyQualifiedJavaType condType;


    /**
     * 是否添加注解
     */
    private boolean enableAnnotation = true;

    /**
     * controller包路径
     */
    private String controllerPack;

    /**
     * controller所在模块
     */
    private String controllerProject;

    /**
     * controller类后缀
     */
    private String controllerSuffix;

    /**
     * vo包路径
     */
    private String voPack;

    /**
     * vo类后缀
     */
    private String voSuffix;

    /**
     * 返回类方法
     */
    private String responseMethod;


    /**
     * 要继承的基础controller
     */
    private String baseController;

    /**
     * 所有的方法
     */
    private List<Method> methods;

    /**
     * 新增方法
     **/
    private String insertMethod = null;

    /**
     * 更新方法
     **/
    private String updateMethod = null;

    /**
     * 单个方法
     **/
    private String selectMethod = null;

    /**
     * 条件方法
     **/
    private String listMethod = null;

    /**
     * 查询总数
     **/
    private String countMethod = null;

    /**
     * 条件方法
     **/
    private String listByIds = null;


    /**
     * business包路径
     */
    private String businessPack;

    /**
     * business类后缀
     */
    private String businessSuffix;

    /**
     * 编码
     **/
    private String fileEncoding;


    /**
     * 是否生成controller
     **/
    private boolean generatorController = false;

    /**
     * 基础方法插件类
     **/
    private PluginConfiguration baseMethodPlugin;

    /**
     * 基础对象插件
     **/
    private PluginConfiguration extendModelPlugin;

    /**
     * businessPlugin插件
     **/
    private PluginConfiguration businessPlugin;

    /**
     * 表配置列表
     */
    private List<TableConfiguration> tableConfigurationList;

    /**
     * 是否生成logger日志
     */
    private boolean enableLogger;

    public ControllerPlugin() {
        super();
        // default is slf4j
        slf4jLogger = new FullyQualifiedJavaType("org.slf4j.Logger");
        slf4jLoggerFactory = new FullyQualifiedJavaType("org.slf4j.LoggerFactory");
        methods = new ArrayList<Method>();
    }

    /**
     * 读取配置文件
     */
    @Override
    public boolean validate(List<String> warnings) {
        baseMethodPlugin = ContextUtils.getPlugin(context, CommonConstant.BASE_METHOD_PLUGIN);
        extendModelPlugin = ContextUtils.getPlugin(context, CommonConstant.EXTEND_MODEL_PLUGIN);
        businessPlugin = ContextUtils.getPlugin(context, CommonConstant.BUSINESS_PLUGIN);
        String enableAnnotation = properties.getProperty("enableAnnotation");
        this.controllerProject = properties.getProperty("controllerProject");
        this.controllerPack = properties.getProperty("controllerPack");
        this.controllerSuffix = properties.getProperty("controllerSuffix");

        this.businessPack = ContextUtils.getProperty(businessPlugin, "businessPack");
        this.businessSuffix = ContextUtils.getProperty(businessPlugin, "businessSuffix");

        this.voPack = ContextUtils.getProperty(extendModelPlugin, "voPack");
        this.voSuffix = ContextUtils.getProperty(extendModelPlugin, "voSuffix");


        this.responseMethod = properties.getProperty("responseMethod");

        this.baseController = properties.getProperty("baseController");

        this.insertMethod = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.SAVE.getName());
        this.updateMethod = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.UPDATE.getName());
        this.selectMethod = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.GET.getName());
        this.listMethod = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.LIST_BY_CONDITION.getName());
        this.countMethod = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.COUNT_BY_CONDITION.getName());
        this.listByIds = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.LIST_BY_IDS.getName());

        this.fileEncoding = context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING);

        //是否生成logger
        enableLogger = StringUtility.isTrue(properties.getProperty("enableLogger"));


        tableConfigurationList = context.getTableConfigurations();

        if (StringUtility.stringHasValue(enableAnnotation)) {
            this.enableAnnotation = StringUtility.isTrue(enableAnnotation);
        }

        if (this.enableAnnotation) {
            classAnnotation = new FullyQualifiedJavaType("org.springframework.web.bind.annotation.*");
        }
        return true;
    }

    /**
     *
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        //是否生成controller
        for (TableConfiguration tableConfiguration : tableConfigurationList) {
            if (tableConfiguration.getTableName().equals(introspectedTable.getTableName())) {
                this.generatorController = tableConfiguration.isControllerEnabled();
                break;
            }
        }


        if (!StringUtility.stringHasValue(responseMethod)) {
            throw new RuntimeException(responseMethod + "不能为空");
        }

        List<GeneratedJavaFile> files = new ArrayList<>();

        PluginConfiguration extendModelPlugin = ContextUtils.checkExtendModelPlugin(context);
        //po全路径
        pojoType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());

        String domainObjectName = introspectedTable.getDomainObjectName();
        //business全路径
        businessType = new FullyQualifiedJavaType(businessPack + "." + domainObjectName + businessSuffix);

        //vo全路径
        voType = MethodGeneratorUtils.getVoType(extendModelPlugin, introspectedTable);

        controllerType = new FullyQualifiedJavaType(controllerPack + "." + domainObjectName + controllerSuffix);


        listType = new FullyQualifiedJavaType("java.util.List");

        String tableName = introspectedTable.getBaseRecordType();

        if (generatorController) {
            //controller
            TopLevelClass controllerClass = new TopLevelClass(controllerType);
            if (StringUtility.stringHasValue(baseController)) {
                controllerClass.setSuperClass(MethodUtils.getClassName(baseController));
                controllerClass.addImportedType(new FullyQualifiedJavaType(baseController));
            } else {
                controllerClass.addAnnotation("@CrossOrigin");
            }
            //生成日志信息
            if (enableLogger) {
                controllerClass.addImportedType(slf4jLogger);
                controllerClass.addImportedType(slf4jLoggerFactory);
            }
            controllerClass.addImportedType(voType);
            controllerClass.addImportedType(listType);
            if (StringUtility.stringHasValue(responseMethod)) {
                FullyQualifiedJavaType response = new FullyQualifiedJavaType(MethodUtils.getFullClass(responseMethod, ":"));
                controllerClass.addImportedType(response);
            }
            CommentUtils.addControllerClassComment(controllerClass, introspectedTable);
            addController(controllerClass, introspectedTable, tableName, files);
        }

        return files;
    }


    /**
     * add implements class
     *
     * @param introspectedTable
     * @param tableName
     * @param files
     */
    protected void addController(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName, List<GeneratedJavaFile> files) {
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);

        if (enableAnnotation) {
            topLevelClass.addAnnotation("@RestController");
            topLevelClass.addAnnotation(AnnotationUtils.generateAnnotation("@RequestMapping", MethodUtils.humpToMiddleLine(introspectedTable.getTableName())));
            topLevelClass.addImportedType(classAnnotation);
        }
        //添加Log属性
        if (enableLogger) {
            ClassUtils.addLogger(topLevelClass);
        }
        // add import service
        ClassUtils.addField(topLevelClass, businessType, null);


        ControllerGen controllerGen = new ControllerGen(responseMethod, countMethod, enableLogger);

        if (StringUtility.stringHasValue(selectMethod)) {
            topLevelClass.addMethod(controllerGen.selectByPrimaryKey(businessType, introspectedTable, selectMethod));
        }
        if (StringUtility.stringHasValue(insertMethod)) {
            topLevelClass.addMethod(controllerGen.insertOrUpdate(businessType, introspectedTable, insertMethod));
        }

        if (StringUtility.stringHasValue(updateMethod)) {
            topLevelClass.addMethod(controllerGen.insertOrUpdate(businessType, introspectedTable, updateMethod));
        }

        if (StringUtility.stringHasValue(listMethod)) {
            topLevelClass.addMethod(controllerGen.listByCondition(businessType, introspectedTable, listMethod));
        }

        //此外报错[已修2016-03-22，增加:",context.getJavaFormatter()"]
        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, controllerProject, fileEncoding, context.getJavaFormatter());
        files.add(file);
    }


    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        returnType = method.getReturnType();
        return true;
    }
}
