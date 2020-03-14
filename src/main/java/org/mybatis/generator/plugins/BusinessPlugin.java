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
import org.mybatis.generator.method.BusinessGen;
import org.mybatis.generator.utils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 自定义方法生成
 * Author: guos
 * Date: 2019/2/1 11:31
 **/
public class BusinessPlugin extends PluginAdapter {

    private FullyQualifiedJavaType slf4jLogger;
    private FullyQualifiedJavaType slf4jLoggerFactory;
    private FullyQualifiedJavaType classAnnotation;

    private FullyQualifiedJavaType serviceType;

    private FullyQualifiedJavaType businessImplType;


    private FullyQualifiedJavaType voType;

    private FullyQualifiedJavaType listType;

    private FullyQualifiedJavaType pojoType;

    private FullyQualifiedJavaType interfaceType;

    /**
     * 是否添加注解
     */
    private boolean enableAnnotation = true;


    /**
     * vo包路径
     */
    private String voPack;

    /**
     * vo类后缀
     */
    private String voSuffix;

    /**
     * 所有的方法
     */
    private List<Method> methods;

    /**
     * service插件类
     **/
    PluginConfiguration servicePlugin;

    /**
     * business包路径
     */
    private String businessPack;

    /**
     * business所在模块
     */
    private String businessProject;

    /**
     * business包路径
     */
    private String businessImplPack;

    /**
     * business所在模块
     */
    private String businessImplProject;

    /**
     * business类后缀
     */
    private String businessSuffix;

    /**
     * 是否生成doBatch方法
     */
    private String doBatchMethod = null;

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
     * 物理删除
     */
    private String deleteByCondition = null;

    /**
     * 返回类方法
     */
    private String responseMethod;

    /**
     * 编码
     **/
    private String fileEncoding;

    /**
     * 远程注入注解
     **/
    private String remoteResource;

    /**
     * 是否生成business
     **/
    private boolean generatorBusiness = false;

    /**
     * 是否启用乐观锁,只有versions配置才行
     */
    private boolean enableVersions = false;

    /**
     * 乐观锁列名
     */
    private String versions;

    /**
     * 基础方法插件类
     **/
    private PluginConfiguration baseMethodPlugin;

    /**
     * 基础对象插件
     **/
    private PluginConfiguration extendModelPlugin;

    /**
     * controllerPlugin插件
     **/
    private PluginConfiguration controllerPlugin;


    /**
     * 对象转换类
     */
    private String modelConvertUtils;

    /**
     * 表配置列表
     */
    private List<TableConfiguration> tableConfigurationList;

    /**
     * 是否生成logger日志
     */
    private boolean enableLogger;

    /**
     * 分页类路径
     */
    private String page;


    /**
     * 自定义异常类全路径
     **/
    private String exceptionPack;


    public BusinessPlugin() {
        super();
        // default is slf4j

        slf4jLogger = new FullyQualifiedJavaType("org.slf4j.Logger");
        slf4jLoggerFactory = new FullyQualifiedJavaType("org.slf4j.LoggerFactory");
        methods = new ArrayList<Method>();
    }


    @Override
    public boolean validate(List<String> warnings) {
        baseMethodPlugin = ContextUtils.getPlugin(context, CommonConstant.BASE_METHOD_PLUGIN);
        controllerPlugin = ContextUtils.getPlugin(context, CommonConstant.CONTROLLER_PLUGIN);
        extendModelPlugin = ContextUtils.checkExtendModelPlugin(context);
        String enableAnnotation = properties.getProperty("enableAnnotation");
        this.voPack = ContextUtils.getProperty(extendModelPlugin, "voPack");
        this.voSuffix = ContextUtils.getProperty(extendModelPlugin, "voSuffix");

        this.voPack = ContextUtils.getProperty(extendModelPlugin, "voPack");
        this.voSuffix = ContextUtils.getProperty(extendModelPlugin, "voSuffix");


        this.insertMethod = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.SAVE.getName());
        this.updateMethod = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.UPDATE.getName());
        this.selectMethod = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.GET.getName());
        this.listMethod = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.LIST_BY_CONDITION.getName());
        this.countMethod = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.COUNT_BY_CONDITION.getName());
        this.doBatchMethod = properties.getProperty(MethodEnum.DO_BATCH.getName());
        this.deleteByCondition = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.REAL_DELETE.getName());

        this.businessSuffix = properties.getProperty("businessSuffix");

        this.responseMethod = ContextUtils.getProperty(controllerPlugin, "responseMethod");

        this.modelConvertUtils = properties.getProperty("modelConvertUtils");
        this.businessProject = properties.getProperty("businessProject");
        this.businessPack = properties.getProperty("businessPack");
        this.businessSuffix = properties.getProperty("businessSuffix");

        this.businessImplProject = properties.getProperty("businessImplProject");
        this.businessImplPack = properties.getProperty("businessImplPack");

        this.remoteResource = properties.getProperty("remoteResource");

        this.fileEncoding = context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING);

        tableConfigurationList = context.getTableConfigurations();

        //是否生成logger
        enableLogger = StringUtility.isTrue(properties.getProperty("enableLogger"));

        page = context.getProperty("page");

        this.exceptionPack = properties.getProperty("exceptionPack");

        if (StringUtility.stringHasValue(enableAnnotation)) {
            this.enableAnnotation = StringUtility.isTrue(enableAnnotation);
        }

        if (this.enableAnnotation) {
            classAnnotation = new FullyQualifiedJavaType("org.springframework.web.bind.annotation.*");
        }

        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        //是否生成business
        for (TableConfiguration tableConfiguration : tableConfigurationList) {
            if (tableConfiguration.getTableName().equals(introspectedTable.getTableName())) {
                this.generatorBusiness = tableConfiguration.isBusinessEnabled();
                this.versions = tableConfiguration.getVersionCol();
                this.enableVersions = tableConfiguration.isEnableVersions();
                break;
            }
        }

        List<GeneratedJavaFile> files = new ArrayList<>();
        List<GeneratedJavaFile> businessFiles = new ArrayList<GeneratedJavaFile>();
        List<GeneratedJavaFile> businessImplFiles = new ArrayList<GeneratedJavaFile>();

        servicePlugin = ContextUtils.getPlugin(context, CommonConstant.SERVICE_PLUGIN);
        if (servicePlugin == null) {
            throw new RuntimeException("service插件存在");
        }

        if (!StringUtility.stringHasValue(responseMethod)) {
            throw new RuntimeException(responseMethod + "不能为空");
        }

        String domainObjectName = introspectedTable.getDomainObjectName();
        //service全路径
        String servicePack = servicePlugin.getProperty("servicePack");
        String serviceName = domainObjectName + CommonConstant.SERVICE_SUFFIX;
        serviceType = new FullyQualifiedJavaType(servicePack + "." + serviceName);
        String businessName = domainObjectName + this.businessSuffix;
        String businessPath = businessPack + "." + businessName;
        String businessImplPath = businessImplPack + "." + businessName + "Impl";

        interfaceType = new FullyQualifiedJavaType(businessPath);
        businessImplType = new FullyQualifiedJavaType(businessImplPath);

        //查询条件类
        String conditionType = extendModelPlugin.getProperty(CommonConstant.CONDITION);

        Interface interface1 = new Interface(interfaceType);
        TopLevelClass businessImplClass = new TopLevelClass(businessImplType);
        businessImplClass.addImportedType(new FullyQualifiedJavaType(conditionType));
        FullyQualifiedJavaType responseType = new FullyQualifiedJavaType(MethodUtils.getFullClass(responseMethod, ":"));
        businessImplClass.addImportedType(responseType);
        interface1.addImportedType(responseType);

        listType = new FullyQualifiedJavaType("java.util.*");

        this.voType = MethodGeneratorUtils.getVoType(extendModelPlugin, introspectedTable);
        this.pojoType = MethodGeneratorUtils.getPoType(context, introspectedTable);

        String suffix = CommonConstant.JAVA_FILE_SUFFIX;

        String businessFilePath = businessProject + LocalFileUtils.getPath(businessPath) + suffix;
        String businessImplFilePath = businessImplProject + LocalFileUtils.getPath(businessImplPath) + suffix;

        File businessFile = new File(businessFilePath);
        File businessImplFile = new File(businessImplFilePath);
        String businessPackStr = "package " + businessPack + ";";
        String businessImplPackStr = "package " + businessImplPack + ";";
        boolean businessFileExist = businessFile.exists();
        boolean businessImplFileExist = businessImplFile.exists();

        String tableName = introspectedTable.getBaseRecordType();

        if (generatorBusiness) {//生成business
            Method method;
            boolean hasModify = false;
            BusinessGen businessGen = new BusinessGen(responseMethod, MethodEnum.COUNT_BY_CONDITION.getValue(), modelConvertUtils, enableLogger);
            if (businessFileExist) {
                method = businessGen.selectByPrimaryKey(serviceType, introspectedTable, selectMethod);
                method.removeAllBodyLines();
                if (!LocalFileUtils.findStr(businessFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(businessFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }
                method = businessGen.insertOrUpdate(serviceType, introspectedTable, insertMethod, exceptionPack,versions,enableVersions);
                method.removeAllBodyLines();
                if (!LocalFileUtils.findStr(businessFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(businessFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }

                if (StringUtility.stringHasValue(deleteByCondition)) {
                    method = businessGen.delete(serviceType, introspectedTable, deleteByCondition);
                    method.removeAllBodyLines();
                    if (!LocalFileUtils.findStr(businessFilePath, MethodUtils.getMethodSign(method))) {
                        LocalFileUtils.modifyLine(businessFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                        hasModify = true;
                    }
                }

                method = businessGen.insertOrUpdate(serviceType, introspectedTable, updateMethod, exceptionPack,versions,enableVersions);
                method.removeAllBodyLines();
                if (!LocalFileUtils.findStr(businessFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(businessFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }
                method = businessGen.listByCondition(serviceType, introspectedTable, listMethod, page);
                method.removeAllBodyLines();
                if (!LocalFileUtils.findStr(businessFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(businessFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }
                method = businessGen.count(serviceType, introspectedTable, countMethod);
                method.removeAllBodyLines();
                if (!LocalFileUtils.findStr(businessFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(businessFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }

                //是否生doBatchMethod
                if (StringUtility.stringHasValue(doBatchMethod)) {
                    method = businessGen.doBatch(serviceType, introspectedTable, MethodEnum.DO_BATCH.getValue(), page);
                    method.removeAllBodyLines();
                    if (!LocalFileUtils.findStr(businessFilePath, MethodUtils.getMethodSign(method))) {
                        LocalFileUtils.modifyLine(businessFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                        hasModify = true;
                    }
                }

                if (hasModify) {
                    LocalFileUtils.modifyLine(businessFilePath, businessPackStr, businessPackStr + "\n\n\nimport java.util.*;");
                }
            } else {
                // 添加类
                interface1.addImportedType(listType);
                interface1.addImportedType(voType);
                addBusiness(interface1, introspectedTable, tableName, businessFiles);
                //添加接口注释
                CommentUtils.addGeneralInterfaceComment(interface1, introspectedTable);
                files.addAll(businessFiles);
            }

            if (businessImplFileExist) {
                method = businessGen.selectByPrimaryKey(serviceType, introspectedTable, selectMethod);
                if (!LocalFileUtils.findStr(businessImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(businessImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }
                method = businessGen.insertOrUpdate(serviceType, introspectedTable, insertMethod, exceptionPack,versions,enableVersions);
                if (!LocalFileUtils.findStr(businessImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(businessImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }

                if (StringUtility.stringHasValue(deleteByCondition)) {
                    method = businessGen.delete(serviceType, introspectedTable, deleteByCondition);
                    if (!LocalFileUtils.findStr(businessImplFilePath, MethodUtils.getMethodSign(method))) {
                        LocalFileUtils.modifyLine(businessImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                        hasModify = true;
                    }
                }

                method = businessGen.insertOrUpdate(serviceType, introspectedTable, updateMethod, exceptionPack,versions,enableVersions);
                if (!LocalFileUtils.findStr(businessImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(businessImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }
                method = businessGen.listByCondition(serviceType, introspectedTable, listMethod, page);
                if (!LocalFileUtils.findStr(businessImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(businessImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }
                method = businessGen.count(serviceType, introspectedTable, countMethod);
                if (!LocalFileUtils.findStr(businessImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(businessImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }
                //是否生doBatchMethod
                if (StringUtility.stringHasValue(doBatchMethod)) {
                    method = businessGen.doBatch(serviceType, introspectedTable, MethodEnum.DO_BATCH.getValue(), page);
                    if (!LocalFileUtils.findStr(businessImplFilePath, MethodUtils.getMethodSign(method))) {
                        LocalFileUtils.modifyLine(businessImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                        hasModify = true;
                    }
                }

                if (hasModify) {
                    StringBuilder importType = new StringBuilder("\n\n\nimport org.springframework.beans.BeanUtils;");
                    if (StringUtility.stringHasValue(doBatchMethod)) {
                        importType.append("\nimport org.springframework.util.CollectionUtils;");
                    }
                    LocalFileUtils.modifyLine(businessImplFilePath, businessImplPackStr, businessImplPackStr + importType);
                }
            } else {
                //生成日志信息
                if (enableLogger) {
                    businessImplClass.addImportedType(slf4jLogger);
                    businessImplClass.addImportedType(slf4jLoggerFactory);
                }
                businessImplClass.addImportedType(voType);
                businessImplClass.addImportedType(pojoType);
                businessImplClass.addImportedType(listType);
                businessImplClass.addImportedType(interfaceType);
                businessImplClass.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.BeanUtils"));
                //businessImplClass.addImportedType(new FullyQualifiedJavaType("org.springframework.util.CollectionUtils"));
                businessImplClass.addImportedType(new FullyQualifiedJavaType("com.google.common.collect.*"));
                //businessImplClass.addImportedType(new FullyQualifiedJavaType("java.util.stream.Collectors"));

                if (StringUtility.stringHasValue(exceptionPack)) {
                    FullyQualifiedJavaTypeUtils.importType(null, businessImplClass, exceptionPack);
                }

                // 添加类
                addBusinessImpl(businessImplClass, introspectedTable, tableName, businessImplFiles);

                //添加类注释
                CommentUtils.addBusinessClassComment(businessImplClass, introspectedTable);
                files.addAll(businessImplFiles);
            }
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
    protected void addBusiness(Interface interface1, IntrospectedTable introspectedTable, String tableName, List<GeneratedJavaFile> files) {
        interface1.setVisibility(JavaVisibility.PUBLIC);

        BusinessGen businessGen = new BusinessGen(responseMethod, countMethod, modelConvertUtils, enableLogger);
        Method method;
        if (StringUtility.stringHasValue(selectMethod)) {
            method = businessGen.selectByPrimaryKey(serviceType, introspectedTable, selectMethod);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }

        if (StringUtility.stringHasValue(insertMethod)) {
            method = businessGen.insertOrUpdate(serviceType, introspectedTable, insertMethod, exceptionPack,versions,enableVersions);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }

        if (StringUtility.stringHasValue(deleteByCondition)) {
            method = businessGen.delete(serviceType, introspectedTable, deleteByCondition);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }


        if (StringUtility.stringHasValue(updateMethod)) {
            method = businessGen.insertOrUpdate(serviceType, introspectedTable, updateMethod, exceptionPack,versions,enableVersions);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }

        if (StringUtility.stringHasValue(listMethod)) {
            method = businessGen.listByCondition(serviceType, introspectedTable, listMethod, page);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }

        if (StringUtility.stringHasValue(countMethod)) {
            method = businessGen.count(serviceType, introspectedTable, countMethod);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }

        if (StringUtility.stringHasValue(doBatchMethod)) {
            if (StringUtility.stringHasValue(page)) {
                method = businessGen.doBatch(serviceType, introspectedTable, MethodEnum.DO_BATCH.getValue(), page);
                MethodUtils.clear(method);
                interface1.addMethod(method);
            }
        }

        //此外报错[已修2016-03-22，增加:",context.getJavaFormatter()"]
        GeneratedJavaFile file = new GeneratedJavaFile(interface1, businessProject, fileEncoding, context.getJavaFormatter());
        files.add(file);
    }

    /**
     * add implements class
     *
     * @param introspectedTable
     * @param tableName
     * @param files
     */
    protected void addBusinessImpl(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName, List<GeneratedJavaFile> files) {
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addSuperInterface(interfaceType);
        if (enableAnnotation) {
            topLevelClass.addAnnotation("@Service");
            topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
        }
        //添加Log属性
        if (enableLogger) {
            ClassUtils.addLogger(topLevelClass);
        }
        // add import service
        ClassUtils.addField(topLevelClass, serviceType, remoteResource);
        BusinessGen businessGen = new BusinessGen(responseMethod, countMethod, modelConvertUtils, enableLogger);

        Method method;
        if (StringUtility.stringHasValue(selectMethod)) {
            method = businessGen.selectByPrimaryKey(serviceType, introspectedTable, selectMethod);
            method.addAnnotation("@Override");
            topLevelClass.addMethod(method);
        }

        if (StringUtility.stringHasValue(insertMethod)) {
            method = businessGen.insertOrUpdate(serviceType, introspectedTable, insertMethod, exceptionPack,versions,enableVersions);
            method.addAnnotation("@Override");
            topLevelClass.addMethod(method);
        }

        if (StringUtility.stringHasValue(deleteByCondition)) {
            method = businessGen.delete(serviceType, introspectedTable, deleteByCondition);
            method.addAnnotation("@Override");
            topLevelClass.addMethod(method);
        }


        if (StringUtility.stringHasValue(updateMethod)) {
            method = businessGen.insertOrUpdate(serviceType, introspectedTable, updateMethod, exceptionPack,versions,enableVersions);
            method.addAnnotation("@Override");
            topLevelClass.addMethod(method);
        }

        if (StringUtility.stringHasValue(listMethod) || StringUtility.stringHasValue(countMethod)) {
            method = businessGen.listByCondition(serviceType, introspectedTable, listMethod, page);
            if (StringUtility.stringHasValue(modelConvertUtils)) {
                topLevelClass.addImportedType(new FullyQualifiedJavaType(modelConvertUtils));
            }
            if (StringUtility.stringHasValue(page)) {
                topLevelClass.addImportedType(new FullyQualifiedJavaType(page));
            }
            method.addAnnotation("@Override");
            topLevelClass.addMethod(method);
        }

        if (StringUtility.stringHasValue(countMethod)) {
            method = businessGen.count(serviceType, introspectedTable, countMethod);
            method.addAnnotation("@Override");
            topLevelClass.addMethod(method);
        }

        if (StringUtility.stringHasValue(doBatchMethod)) {
            if (StringUtility.stringHasValue(page)) {
                method = businessGen.doBatch(serviceType, introspectedTable, MethodEnum.DO_BATCH.getValue(), page);
                method.addAnnotation("@Override");
                topLevelClass.addMethod(method);
                topLevelClass.addImportedType("org.springframework.util.CollectionUtils");
            }
        }

        //此外报错[已修2016-03-22，增加:",context.getJavaFormatter()"]
        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, businessImplProject, fileEncoding, context.getJavaFormatter());
        files.add(file);
    }
}
