package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.constant.MethodEnum;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.utils.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * service生成插件
 * <p>
 * guos
 * 2019/1/17 11:51
 **/
public class ServicePlugin extends PluginAdapter {

    private FullyQualifiedJavaType slf4jLogger;
    private FullyQualifiedJavaType slf4jLoggerFactory;
    private FullyQualifiedJavaType serviceType;
    private FullyQualifiedJavaType daoType;
    private FullyQualifiedJavaType interfaceType;
    private FullyQualifiedJavaType pojoType;
    private FullyQualifiedJavaType listType;
    private FullyQualifiedJavaType autowired;
    private FullyQualifiedJavaType service;
    private FullyQualifiedJavaType returnType;
    private String servicePack;
    private String serviceImplPack;
    private String serviceProject;
    private String serviceImplProject;
    private String pojoUrl;
    /**
     * 所有的方法
     */
    private List<Method> methods;
    /**
     * 是否添加注解
     */
    private boolean enableAnnotation = true;
    private boolean enableInsert = false;
    //private String deleteByPrimaryKey = null;
    private String deleteByCondition = null;
    private boolean enableUpdateByPrimaryKey = false;
    private boolean generatorService = false;
    private String insertSelective = MethodEnum.SAVE.getValue();
    private String updateByPrimaryKeySelective = MethodEnum.UPDATE.getValue();
    private String selectByPrimaryKey = MethodEnum.GET.getValue();
    private String listByIds = MethodEnum.LIST_BY_IDS.getValue();
    private String countByCondition = MethodEnum.COUNT_BY_CONDITION.getValue();
    private String listByCondition = MethodEnum.LIST_BY_CONDITION.getValue();
    private String count = MethodEnum.COUNT.getValue();
    private String list = MethodEnum.LIST.getValue();
    private String fileEncoding;


    private String mapByIds = MethodEnum.MAP_BY_IDS.getValue();

    private String map = MethodEnum.MAP_BY_CONDITION.getValue();

    private String listId = MethodEnum.LIST_ID.getValue();

    private String saveAndGet = MethodEnum.SAVE_AND_GET.getValue();

    /**
     * 获取用户名方法
     **/
    private String userNameMethod = null;

    /**
     * 日期格式方法
     **/
    private String dateMethod = null;

    /**
     * remote注解所在包
     **/
    private String remote = null;

    /**
     * applicationName类所在包
     **/
    private String applicationName = null;

    /**
     * 创建时间
     **/
    private String createTime;

    /**
     * 修改时间
     **/
    private String updateTime;

    /**
     * 自定义异常类全路径
     **/
    private String exceptionPack;

    /**
     * 乐观锁
     **/
    private String versions;

    /**
     * 表的列list
     **/
    private List<IntrospectedColumn> columns;

    /**
     * extentModel插件类
     **/
    private PluginConfiguration extentModelPlugin;

    /**
     * 基础方法插件类
     **/
    private PluginConfiguration baseMethodPlugin;

    /**
     * 是否生成logger日志
     */
    private boolean enableLogger;

    /**
     * 分页类路径
     */
    private String page;

    /**
     * 表配置列表
     */
    private List<TableConfiguration> tableConfigurationList;


    public ServicePlugin() {
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

        extentModelPlugin = ContextUtils.getPlugin(context, CommonConstant.EXTEND_MODEL_PLUGIN);

        baseMethodPlugin = ContextUtils.getPlugin(context, CommonConstant.BASE_METHOD_PLUGIN);

        String enableAnnotation = properties.getProperty("enableAnnotation");

        String enableInsert = ContextUtils.getProperty(baseMethodPlugin, "enableInsert");

        String insertSelective = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.SAVE.getName());

        this.deleteByCondition = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.REAL_DELETE.getName());

        String updateByPrimaryKeySelective = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.UPDATE.getName());

        String selectByPrimaryKey = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.GET.getName());

        String listByIds = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.LIST_BY_IDS.getName());

        String countByCondition = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.COUNT.getName());

        String listByCondition = ContextUtils.getProperty(baseMethodPlugin, MethodEnum.LIST.getName());

        String mapByIds = properties.getProperty(MethodEnum.MAP_BY_IDS.getName());
        String map = properties.getProperty(MethodEnum.MAP.getName());
        String listId = properties.getProperty(MethodEnum.LIST_ID.getName());
        String saveAndGet = properties.getProperty(MethodEnum.SAVE_AND_GET.getName());

        //是否生成logger
        enableLogger = StringUtility.isTrue(properties.getProperty("enableLogger"));

        page = context.getProperty("page");

        tableConfigurationList = context.getTableConfigurations();


        if (StringUtility.stringHasValue(enableAnnotation)) {
            this.enableAnnotation = StringUtility.isTrue(enableAnnotation);
        }

        if (StringUtility.stringHasValue(selectByPrimaryKey)) {
            this.selectByPrimaryKey = selectByPrimaryKey;
        }

        if (StringUtility.stringHasValue(enableInsert)) {
            this.enableInsert = StringUtility.isTrue(enableInsert);
        }

        if (StringUtility.stringHasValue(insertSelective)) {
            this.insertSelective = insertSelective;
        }

        if (StringUtility.stringHasValue(updateByPrimaryKeySelective)) {
            this.updateByPrimaryKeySelective = updateByPrimaryKeySelective;
        }


        if (StringUtility.stringHasValue(listByIds)) {
            this.listByIds = listByIds;
        }
        if (StringUtility.stringHasValue(countByCondition)) {
            this.countByCondition = countByCondition;
        }
        if (StringUtility.stringHasValue(listByCondition)) {
            this.listByCondition = listByCondition;
        }

        if (StringUtility.stringHasValue(map)) {
            this.map = map;
        }
        if (StringUtility.stringHasValue(mapByIds)) {
            this.mapByIds = mapByIds;
        }
        if (StringUtility.stringHasValue(listId)) {
            this.listId = listId;
        }

        if (StringUtility.stringHasValue(saveAndGet)) {
            this.saveAndGet = saveAndGet;
        }


        this.fileEncoding = context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING);
        this.servicePack = properties.getProperty("servicePack");
        this.serviceImplPack = properties.getProperty("serviceImplPack");
        this.serviceProject = properties.getProperty("serviceProject");
        this.serviceImplProject = properties.getProperty("serviceImplProject");
        this.pojoUrl = context.getJavaModelGeneratorConfiguration().getTargetPackage();

        this.remote = properties.getProperty("remote");
        this.applicationName = properties.getProperty("applicationName");
        this.userNameMethod = properties.getProperty("userNameMethod");
        this.dateMethod = properties.getProperty("dateMethod");
        this.createTime = properties.getProperty("create_time");
        this.updateTime = properties.getProperty("update_time");
        this.exceptionPack = properties.getProperty("exceptionPack");

        if (this.enableAnnotation) {
            autowired = new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired");
            service = new FullyQualifiedJavaType("org.springframework.stereotype.Service");
        }
        return true;
    }

    /**
     *
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {


        //是否生成business
        for (TableConfiguration tableConfiguration : tableConfigurationList) {
            if (tableConfiguration.getTableName().equals(introspectedTable.getTableName())) {
                this.generatorService = tableConfiguration.isServiceEnabled();
                break;
            }
        }

        columns = introspectedTable.getNonPrimaryKeyColumns();

        // 取Service名称【com.coolead.service.PetService】
        String table = introspectedTable.getBaseRecordType();
        String tableName = table.replaceAll(this.pojoUrl + ".", "");
        String servicePath = servicePack + "." + tableName + "Service";
        String serviceImplPath = serviceImplPack + "." + tableName + "ServiceImpl";

        interfaceType = new FullyQualifiedJavaType(servicePath);

        // 【com.coolead.mapper.UserMapper】
        daoType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());

        // 【com.coolead.service.impl.PetServiceImpl】logger.info(toLowerCase(daoType.getShortName()));
        serviceType = new FullyQualifiedJavaType(serviceImplPath);

        // 【com.coolead.domain.Pet】
        pojoType = MethodGeneratorUtils.getPoType(context, introspectedTable);

        listType = new FullyQualifiedJavaType("java.util.*");

        //查询条件类
        String conditionType = extentModelPlugin.getProperty(CommonConstant.CONDITION);

        //分页查询条件类
        String limitConditionType = extentModelPlugin.getProperty(CommonConstant.LIMIT_CONDITION);

        String suffix = CommonConstant.JAVA_FILE_SUFFIX;
        String serviceFilePath = serviceProject + LocalFileUtils.getPath(servicePath) + suffix;
        String serviceImplFilePath = serviceImplProject + LocalFileUtils.getPath(serviceImplPath) + suffix;
        File serviceFile = new File(serviceFilePath);
        File serviceImplFile = new File(serviceImplFilePath);
        String servicePackStr = "package " + servicePack + ";";
        String serviceImplPackStr = "package " + serviceImplPack + ";";
        boolean serviceFileExist = serviceFile.exists();
        boolean serviceImplFileExist = serviceImplFile.exists();

        List<GeneratedJavaFile> serviceFiles = new ArrayList<GeneratedJavaFile>();
        List<GeneratedJavaFile> serviceImplFiles = new ArrayList<GeneratedJavaFile>();

        Interface interface1 = new Interface(interfaceType);
        interface1.addImportedType(new FullyQualifiedJavaType(conditionType));
        interface1.addImportedType(new FullyQualifiedJavaType(limitConditionType));
        TopLevelClass topLevelClass = new TopLevelClass(serviceType);
        topLevelClass.addImportedType(new FullyQualifiedJavaType(conditionType));
        topLevelClass.addImportedType(new FullyQualifiedJavaType(limitConditionType));
        List<GeneratedJavaFile> files = new ArrayList<>();
        if (generatorService) {//是否生成service
            Method method;
            boolean hasModify = false;
            if (serviceFileExist) {

                method = selectByPrimaryKey(introspectedTable, selectByPrimaryKey, tableName);
                if (!LocalFileUtils.findStr(serviceFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }

                method = selectByModel(introspectedTable, MethodEnum.GET_ONE.getValue());
                if (!LocalFileUtils.findStr(serviceFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }

                method = getOtherInteger(insertSelective, introspectedTable, tableName, 1);
                if (!LocalFileUtils.findStr(serviceFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }

                if (StringUtility.stringHasValue(deleteByCondition)) {
                    method = delete(introspectedTable, deleteByCondition, tableName, 1);
                    if (!LocalFileUtils.findStr(serviceFilePath, MethodUtils.getMethodSign(method))) {
                        LocalFileUtils.modifyLine(serviceFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                        hasModify = true;
                    }
                }

                method = getOtherInteger(saveAndGet, introspectedTable, tableName, 1);
                if (!LocalFileUtils.findStr(serviceFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }


                method = getOtherInteger(updateByPrimaryKeySelective, introspectedTable, tableName, 1);
                if (!LocalFileUtils.findStr(serviceFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }


                method = listByIds(introspectedTable, listByIds, 1);
                if (!LocalFileUtils.findStr(serviceFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }

                method = countByCondition(introspectedTable, count);
                if (!LocalFileUtils.findStr(serviceFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }

                method = listByCondition(introspectedTable, list, 1);
                if (!LocalFileUtils.findStr(serviceFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }


                method = countByCondition(introspectedTable, countByCondition);
                if (!LocalFileUtils.findStr(serviceFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }

                method = listByCondition(introspectedTable, listByCondition, 4);
                if (!LocalFileUtils.findStr(serviceFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }


                method = listByCondition(introspectedTable, listId, 2);
                if (!LocalFileUtils.findStr(serviceFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }

                method = listByIds(introspectedTable, mapByIds, 2);
                if (!LocalFileUtils.findStr(serviceFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }

                method = listByCondition(introspectedTable, map, 3);
                if (!LocalFileUtils.findStr(serviceFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }


                method = batchList(null, introspectedTable, MethodEnum.BATCH_LIST.getValue());
                if (!LocalFileUtils.findStr(serviceFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 1) + "\n\n\n}");
                    hasModify = true;
                }

                if (hasModify) {
                    LocalFileUtils.modifyLine(serviceFilePath, servicePackStr, servicePackStr + "\n\n\nimport java.util.*;");
                }
            } else {
                // 导入必须的类
                addImport(interface1, null);
                interface1.addImportedType(MethodGeneratorUtils.getPoType(context, introspectedTable));
                // 接口
                addService(interface1, introspectedTable, tableName, serviceFiles);
                //添加接口注释
                CommentUtils.addGeneralInterfaceComment(interface1, introspectedTable);
                files.addAll(serviceFiles);
            }
            if (serviceImplFileExist) {
                hasModify = false;
                method = selectByPrimaryKey(introspectedTable, selectByPrimaryKey, tableName);
                if (!LocalFileUtils.findStr(serviceImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }
                method = selectByModel(introspectedTable, MethodEnum.GET_ONE.getValue());
                if (!LocalFileUtils.findStr(serviceImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }

                method = getOtherInteger(insertSelective, introspectedTable, tableName, 1);
                if (!LocalFileUtils.findStr(serviceImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }

                if (StringUtility.stringHasValue(deleteByCondition)) {
                    method = delete(introspectedTable, deleteByCondition, tableName, 1);
                    if (!LocalFileUtils.findStr(serviceImplFilePath, MethodUtils.getMethodSign(method))) {
                        LocalFileUtils.modifyLine(serviceImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                        hasModify = true;
                    }
                }

                method = getOtherInteger(saveAndGet, introspectedTable, tableName, 1);
                if (!LocalFileUtils.findStr(serviceImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }

                method = getOtherInteger(updateByPrimaryKeySelective, introspectedTable, tableName, 1);
                if (!LocalFileUtils.findStr(serviceImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }

                method = getOtherList(listByIds, introspectedTable, tableName, 6);
                if (!LocalFileUtils.findStr(serviceImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }


                method = getOtherInteger(count, introspectedTable, tableName, 5);
                if (!LocalFileUtils.findStr(serviceImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }


                method = getOtherList(list, introspectedTable, tableName, 5);
                if (!LocalFileUtils.findStr(serviceImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }


                method = getOtherInteger(countByCondition, introspectedTable, tableName, 8);
                if (!LocalFileUtils.findStr(serviceImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }


                method = getOtherList(listByCondition, introspectedTable, tableName, 8);
                if (!LocalFileUtils.findStr(serviceImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }


                method = getOtherList(listId, introspectedTable, tableName, 7);
                if (!LocalFileUtils.findStr(serviceImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }

                method = getOtherMap(mapByIds, introspectedTable, tableName, 6);
                if (!LocalFileUtils.findStr(serviceImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }
                method = getOtherMap(map, introspectedTable, tableName, 7);
                if (!LocalFileUtils.findStr(serviceImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }

                method = batchList(topLevelClass, introspectedTable, MethodEnum.BATCH_LIST.getValue());
                if (!LocalFileUtils.findStr(serviceImplFilePath, MethodUtils.getMethodSign(method))) {
                    LocalFileUtils.modifyLine(serviceImplFilePath, "}", "\t" + MethodUtils.getMethodStr(method, 2) + "\n\n\n}");
                    hasModify = true;
                }

                if (hasModify) {
                    LocalFileUtils.modifyLine(serviceImplFilePath, serviceImplPackStr, serviceImplPackStr + "\n\n\nimport java.util.*;\nimport org.springframework.util.CollectionUtils;\nimport java.util.stream.Collectors;\nimport com.google.common.collect.*;\nimport org.springframework.transaction.annotation.Transactional;");
                }
            } else {
                // 导入必须的类
                addImport(null, topLevelClass);
                //添加类注释
                CommentUtils.addGeneralClassComment(topLevelClass, introspectedTable);

                if (this.hasDateColumn(dateMethod, createTime, updateTime)) {//是否需要导入date类
                    FullyQualifiedJavaTypeUtils.importType(null, topLevelClass, "java.util.Date");
                }

                if (StringUtility.stringHasValue(exceptionPack) && StringUtility.stringHasValue(versions)) {
                    FullyQualifiedJavaTypeUtils.importType(null, topLevelClass, exceptionPack);
                }
                // 实现类
                addServiceImpl(topLevelClass, introspectedTable, tableName, serviceImplFiles);
                files.addAll(serviceImplFiles);
            }
        }
        return files;
    }

    /**
     * add interface
     *
     * @param tableName
     * @param files
     */
    protected void addService(Interface interface1, IntrospectedTable introspectedTable, String tableName, List<GeneratedJavaFile> files) {

        interface1.setVisibility(JavaVisibility.PUBLIC);

        // add method
        Method method;

        if (enableUpdateByPrimaryKey) {
            method = getOtherInteger("updateByPrimaryKey", introspectedTable, tableName, 1);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }

        if (enableInsert) {
            method = getOtherInteger("insert", introspectedTable, tableName, 1);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }
        method = selectByPrimaryKey(introspectedTable, selectByPrimaryKey, tableName);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = selectByModel(introspectedTable, MethodEnum.GET_ONE.getValue());
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = getOtherInteger(insertSelective, introspectedTable, tableName, 1);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = getOtherInteger(saveAndGet, introspectedTable, tableName, 1);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = getOtherInteger(updateByPrimaryKeySelective, introspectedTable, tableName, 1);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        if (StringUtility.stringHasValue(deleteByCondition)) {
            method = delete(introspectedTable, deleteByCondition, tableName, 1);
            MethodUtils.clear(method);
            interface1.addMethod(method);
        }

        method = listByIds(introspectedTable, listByIds, 1);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = listByCondition(introspectedTable, list, 1);
        MethodUtils.clear(method);
        interface1.addMethod(method);


        method = countByCondition(introspectedTable, count);
        MethodUtils.clear(method);
        interface1.addMethod(method);


        method = listByCondition(introspectedTable, listByCondition, 4);
        MethodUtils.clear(method);
        interface1.addMethod(method);


        method = countByCondition(introspectedTable, countByCondition);
        MethodUtils.clear(method);
        interface1.addMethod(method);


        method = listByCondition(introspectedTable, listId, 2);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = listByIds(introspectedTable, mapByIds, 2);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = listByCondition(introspectedTable, map, 3);
        MethodUtils.clear(method);
        interface1.addMethod(method);

        method = batchList(null, introspectedTable, MethodEnum.BATCH_LIST.getValue());
        MethodUtils.clear(method);
        interface1.addMethod(method);

        //此外报错[已修2016-03-22，增加:"context.getJavaFormatter()"]
        GeneratedJavaFile file = new GeneratedJavaFile(interface1, serviceProject, fileEncoding, context.getJavaFormatter());

        files.add(file);
    }


    /**
     * add implements class
     *
     * @param introspectedTable
     * @param tableName
     * @param files
     */
    protected void addServiceImpl(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName, List<GeneratedJavaFile> files) {
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        // set implements interface
        topLevelClass.addSuperInterface(interfaceType);

        if (enableAnnotation) {
            topLevelClass.addAnnotation("@Service");
            topLevelClass.addImportedType(service);
        }
        topLevelClass.addImportedType(serviceType);
        //topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.ArrayList"));
        //topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.BeanUtils"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.util.*"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.stream.Collectors"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.google.common.collect.*"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.util.Assert"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.transaction.annotation.Transactional"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.function.Function"));
        //添加Log属性
        if (enableLogger) {
            ClassUtils.addLogger(topLevelClass);
        }
        // add import dao
        addField(topLevelClass, tableName);

        /**
         * type:  pojo 1 ;key 2 ;example 3 ;pojo+example 4
         */


        topLevelClass.addMethod(selectByPrimaryKey(introspectedTable, selectByPrimaryKey, tableName));

        topLevelClass.addMethod(selectByModel(introspectedTable, MethodEnum.GET_ONE.getValue()));

        topLevelClass.addMethod(getOtherInteger(insertSelective, introspectedTable, tableName, 1));

        if (StringUtility.stringHasValue(deleteByCondition)) {
            topLevelClass.addMethod(delete(introspectedTable, deleteByCondition, tableName, 1));
        }

        if (enableUpdateByPrimaryKey) {
            topLevelClass.addMethod(getOtherInteger("updateByPrimaryKey", introspectedTable, tableName, 1));
        }
        if (enableInsert) {
            topLevelClass.addMethod(getOtherInsertBoolean("insert", introspectedTable, tableName));
        }

        topLevelClass.addMethod(getOtherInteger(saveAndGet, introspectedTable, tableName, 1));

        topLevelClass.addMethod(getOtherInteger(updateByPrimaryKeySelective, introspectedTable, tableName, 1));

        topLevelClass.addMethod(getOtherList(listByIds, introspectedTable, tableName, 6));

        topLevelClass.addMethod(getOtherList(list, introspectedTable, tableName, 5));

        topLevelClass.addMethod(countByCondition(introspectedTable, count));

        topLevelClass.addMethod(getOtherList(listByCondition, introspectedTable, tableName, 8));

        topLevelClass.addMethod(countByCondition(introspectedTable, countByCondition));

        topLevelClass.addMethod(getOtherList(listId, introspectedTable, tableName, 7));

        topLevelClass.addMethod(getOtherMap(map, introspectedTable, tableName, 7));

        topLevelClass.addMethod(getOtherMap(mapByIds, introspectedTable, tableName, 6));

        topLevelClass.addMethod(batchList(topLevelClass, introspectedTable, MethodEnum.BATCH_LIST.getValue()));

        //此外报错[已修2016-03-22，增加:",context.getJavaFormatter()"]
        GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, serviceImplProject, fileEncoding, context.getJavaFormatter());
        files.add(file);
    }

    /**
     * 添加字段
     *
     * @param topLevelClass
     */
    protected void addField(TopLevelClass topLevelClass, String tableName) {
        // add dao
        Field field = new Field();
        field.setName(MethodUtils.toLowerCase(daoType.getShortName())); // set var name
        topLevelClass.addImportedType(daoType);
        field.setType(daoType); // type
        field.setVisibility(JavaVisibility.PRIVATE);
        if (enableAnnotation) {
            field.addAnnotation("@Autowired");
        }
        field.addJavaDocLine("");
        topLevelClass.addField(field);
    }

    /**
     * 添加方法
     * flag 1:根据id查询
     */
    protected Method selectByPrimaryKey(IntrospectedTable introspectedTable, String alias, String tableName) {
        Method method = new Method();
        method.setName(alias);
        String domainObjectName = introspectedTable.getDomainObjectName();
        method.setReturnType(new FullyQualifiedJavaType(domainObjectName));
        method.addAnnotation("@Override");
        method.setVisibility(JavaVisibility.PUBLIC);
        List<IntrospectedColumn> columns = introspectedTable.getPrimaryKeyColumns();
        if (columns == null || columns.size() == 0) {
            throw new RuntimeException("请设置表的唯一主键列！");
        }
        String primaryKey = columns.get(0).getJavaProperty();
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
            method.addParameter(new Parameter(type, "key"));
        } else {
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
                method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
            }
        }
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, primaryKey);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        sb.append(getDaoShort());
        sb.append(alias + "x");
        sb.append("(");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        sb.append(");");
        method.addBodyLine("Assert.notNull(" + primaryKey + ",\"" + primaryKey + "不能为空\");");
        method.addBodyLine(sb.toString());
        return method;
    }


    /**
     * 添加方法
     * flag 1:根据id查询
     */
    protected Method selectByModel(IntrospectedTable introspectedTable, String alias) {
        Method method = new Method();
        method.setName(alias);
        String domainObjectName = introspectedTable.getDomainObjectName();
        String lowPo = MethodUtils.toLowerCase(domainObjectName);
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(domainObjectName);
        method.addParameter(new Parameter(type, lowPo));
        method.setReturnType(new FullyQualifiedJavaType(domainObjectName));
        method.addAnnotation("@Override");
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, lowPo);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        sb.append(getDaoShort());
        sb.append(alias + "x");
        sb.append("(");
        sb.append(lowPo);
        sb.append(");");
        method.addBodyLine("Assert.notNull(" + lowPo + ",\"" + lowPo + "不能为空\");");
        method.addBodyLine(sb.toString());
        return method;
    }


    /**
     * 添加方法
     * 删除
     */
    protected Method delete(IntrospectedTable introspectedTable, String alias, String tableName, int flag) {
        Method method = new Method();
        method.setName(alias);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addAnnotation("@Override");
        method.setVisibility(JavaVisibility.PUBLIC);
        List<IntrospectedColumn> columns = introspectedTable.getPrimaryKeyColumns();
        if (columns == null || columns.size() == 0) {
            throw new RuntimeException("请设置表的唯一主键列！");
        }
        String primaryKey = columns.get(0).getJavaProperty();
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
            method.addParameter(new Parameter(type, "key"));
        } else {
            for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
                method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
            }
        }
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, primaryKey);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        sb.append(getDaoShort());
        sb.append(alias + "x");
        sb.append("(");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        sb.append(");");
        method.addBodyLine("Assert.notNull(" + primaryKey + ",\"" + primaryKey + "不能为空\");");
        method.addBodyLine(sb.toString());
        return method;
    }


    /**
     * add method
     */
    protected Method countByCondition(IntrospectedTable introspectedTable, String methodName) {
        String condName = MethodUtils.toLowerCase(MethodGeneratorUtils.getCondName(extentModelPlugin, introspectedTable.getDomainObjectName()));
        Method method = new Method();
        method.setName(methodName);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addAnnotation("@Override");
        //method.addParameter(new Parameter(this.getShortPojoType(introspectedTable), condName));
        String domainObjectName = introspectedTable.getDomainObjectName();
        String poName = MethodUtils.toLowerCase(domainObjectName);
        Parameter parameter;
        String tip;
        if (MethodEnum.COUNT.getValue().equals(methodName)) {
            parameter = new Parameter(new FullyQualifiedJavaType(domainObjectName), poName);
            tip = poName;
        } else {
            parameter = new Parameter(new FullyQualifiedJavaType("Condition<" + domainObjectName + ">"), condName);
            tip = condName;
        }
        method.addParameter(parameter);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        StringBuilder sb = new StringBuilder();
        sb.append(getDaoShort());
        sb.append(methodName + "x");
        sb.append("(");
        sb.append(tip);
        sb.append(");");
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, condName);
        }
        method.addBodyLine("Assert.notNull(" + tip + ",\"" + tip + "不能为空\");");
        method.addBodyLine("return " + sb.toString());
        return method;
    }


    /**
     * param introspectedTable
     * param type              :返回类型：1,po集合 2 id列表集合
     * return
     */
    protected Method listByCondition(IntrospectedTable introspectedTable, String methodName, int type) {
        String domainObjectName = introspectedTable.getDomainObjectName();
        String condName = MethodUtils.toLowerCase(MethodGeneratorUtils.getCondName(extentModelPlugin, domainObjectName));
        String poName = MethodUtils.toLowerCase(domainObjectName);
        Method method = new Method();
        method.setName(methodName);
        String returnType = null;
        Parameter parameter;
        String tip;
        //1:list 2:listId 3:map 4: listByCondition
        if (type == 1 || type == 4) {
            returnType = "List<" + domainObjectName + ">";
        } else if (type == 2) {
            returnType = "List<Integer>";
        } else if (type == 3) {
            returnType = "Map<Integer," + domainObjectName + ">";
        }
        if (type == 1) {
            parameter = new Parameter(new FullyQualifiedJavaType(domainObjectName), poName);
        } else {
            parameter = new Parameter(new FullyQualifiedJavaType("Condition<" + domainObjectName + ">"), condName);
        }
        method.setReturnType(new FullyQualifiedJavaType(returnType));
        method.addParameter(parameter);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        return method;
    }


    /**
     * 批量查询列表
     * param introspectedTable
     * param type
     * return
     */
    protected Method batchList(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String methodName) {
        String domainObjectName = introspectedTable.getDomainObjectName();
        String condName = MethodUtils.toLowerCase(MethodGeneratorUtils.getCondName(extentModelPlugin, domainObjectName));
        Method method = new Method();
        method.setName(methodName);
        String returnType = "List<" + domainObjectName + ">";
        method.setReturnType(new FullyQualifiedJavaType(returnType));
        method.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), CommonConstant.GT_ID));
        //method.addParameter(new Parameter(this.getShortPojoType(introspectedTable), condName));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Condition<" + domainObjectName + ">"), condName));
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addAnnotation("@Override");
        CommentUtils.addGeneralMethodComment(method, introspectedTable);

        StringBuilder sb = new StringBuilder();
        sb.append(getDaoShort());
        sb.append(methodName);
        sb.append("(");
        sb.append(condName);
        sb.append(");");
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, new String[]{CommonConstant.GT_ID, condName});
        }
        method.addBodyLine("Assert.notNull(" + condName + ",\"" + condName + "不能为空\");");
        if (StringUtility.stringHasValue(page)) {
            if (topLevelClass != null) {
                topLevelClass.addImportedType(page);
            }
            method.addBodyLine(condName + ".limit(1," + MethodUtils.getClassName(page) + ".getMaxRow() - 1);");
            method.addBodyLine(condName + ".setOrderBy(" + domainObjectName + ".ID);");
            method.addBodyLine(condName + ".andCriteria().andGreaterThan(" + domainObjectName + ".ID, gtId);");
        }
        method.addBodyLine("return this.listByCondition(" + condName + ");");
        return method;
    }


    /**
     * add method
     */
    protected Method listByIds(IntrospectedTable introspectedTable, String methodName, int type) {
        Method method = new Method();
        method.setName(methodName);
        FullyQualifiedJavaType paramType = new FullyQualifiedJavaType("List");
        String domainObjectName = introspectedTable.getDomainObjectName();
        if (type == 1) {
            method.setReturnType(new FullyQualifiedJavaType("List<" + domainObjectName + ">"));
        } else {
            method.setReturnType(new FullyQualifiedJavaType("Map<Integer," + domainObjectName + ">"));
        }
        paramType.addTypeArgument(new FullyQualifiedJavaType("java.lang.Integer"));
        method.addParameter(new Parameter(paramType, "ids"));
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        return method;
    }


    /**
     * add method
     */
    protected Method getOtherInteger(String methodName, IntrospectedTable introspectedTable, String tableName, int type) {
        Method method = new Method();
        method.setName(methodName);
        String params = addParams(introspectedTable, method, type);
        String domainObjectName = introspectedTable.getDomainObjectName();
        String domainName = MethodUtils.toLowerCase(domainObjectName);
        method.setVisibility(JavaVisibility.PUBLIC);
        if (MethodEnum.SAVE_AND_GET.getValue().equals(methodName)) {
            method.addAnnotation("@Transactional");
            method.addAnnotation("@Override");
            method.setReturnType(new FullyQualifiedJavaType(domainObjectName));
        } else {
            method.addAnnotation("@Override");
            method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        }
        CommentUtils.addGeneralMethodComment(method, introspectedTable);

        StringBuilder sb = new StringBuilder();
        //生成日志信息
        if (enableLogger) {
            MethodUtils.addLoggerInfo(method, params);
        }

        //是否生成乐观锁
        for (TableConfiguration tableConfiguration : tableConfigurationList) {
            if (tableConfiguration.getTableName().equals(introspectedTable.getTableName())) {
                this.versions = tableConfiguration.getVersionCol();
                break;
            }
        }

        if (MethodEnum.SAVE.getValue().equals(methodName) || MethodEnum.UPDATE.getValue().equals(methodName)) {
            String getUserName;
            String getDate;
            method.addBodyLine("Assert.notNull(" + domainName + "," + "\"" + domainName + "不能为空\");");
            if (MethodEnum.SAVE.getValue().equals(methodName)) {
                String creator = properties.getProperty("creator");
                getUserName = this.getMethodName(userNameMethod, creator, CommonConstant.DEFAULT_USER);
                getDate = this.getMethodName(dateMethod, createTime, CommonConstant.DEFAULT_TIME);

                this.setMethodValue(method, params, creator, getUserName);//设置用户名
                this.setMethodValue(method, params, createTime, getDate);//设置时间
            } else {
                String updater = properties.getProperty("updater");
                getUserName = this.getMethodName(userNameMethod, updater, CommonConstant.DEFAULT_USER);
                getDate = this.getMethodName(dateMethod, updateTime, CommonConstant.DEFAULT_TIME);
                this.setMethodValue(method, params, updater, getUserName);//设置用户名
                this.setMethodValue(method, params, updateTime, getDate);//设置时间
            }
        }
        //saveAndGet
        if (MethodEnum.SAVE_AND_GET.getValue().equals(methodName)) {
            method.addBodyLine("this." + MethodEnum.SAVE.getValue() + "(" + params + ");");
            method.addBodyLine("return this." + MethodEnum.GET.getValue() + "(" + params + ".getId());");
            return method;
        }
        sb.append("return ");
        sb.append(getDaoShort());
        sb.append(methodName);
        sb.append("x");
       /* if (introspectedTable.hasBLOBColumns() && (!MethodEnum.UPDATE.getName().equals(methodName))) {
            sb.append(methodName + "WithoutBLOBs");
        } else {
            sb.append(methodName);
        }*/


        sb.append("(");
        sb.append(params);
        sb.append(");");
        method.addBodyLine(sb.toString());
        return method;
    }


    /**
     * add method
     */
    protected Method getOtherList(String methodName, IntrospectedTable introspectedTable, String tableName, int type) {
        Method method = new Method();
        String domainObjectName = introspectedTable.getDomainObjectName();
        String returnType = "List<" + domainObjectName + ">";
        method.setName(methodName);
        method.addAnnotation("@Override");
        String idsStr = "ids";
        if (type == 7) {
            method.setReturnType(new FullyQualifiedJavaType("List<Integer>"));
        } else {
            method.setReturnType(new FullyQualifiedJavaType(returnType));
        }
        String params = addParams(introspectedTable, method, type);
        String condName = MethodGeneratorUtils.getCondName(extentModelPlugin, domainObjectName);
        String lowCond = MethodUtils.toLowerCase(condName);
        String lowPo = MethodUtils.toLowerCase(domainObjectName);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        if (type == 7) {
            //生成日志信息
            if (enableLogger) {
                MethodUtils.addLoggerInfo(method, params);
            }
        } else if (type == 6) {
            params = idsStr;
            //生成日志信息
            if (enableLogger) {
                MethodUtils.addLoggerInfo(method, params);
            }
        } else if (type == 5) {
            params = MethodUtils.toLowerCase(domainObjectName);
            //生成日志信息
            if (enableLogger) {
                MethodUtils.addLoggerInfo(method, params);
            }
        } else if (type == 8) {
            params = lowCond;
            //生成日志信息
            if (enableLogger) {
                MethodUtils.addLoggerInfo(method, params);
            }
        }
        String checkStr = "Assert.notNull(" + params + ",\"" + params + "不能为空\");";
        if (type == 7) {//listId
            method.addBodyLine(returnType + " list = this.listByCondition(" + lowCond + ");");
            String getKey = "get" + MethodUtils.toUpperCase(introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty());
            method.addBodyLine("return list.stream().map(" + domainObjectName + "::" + getKey + ").distinct().collect(Collectors.toList());");
        } else if (type == 6) {//listByIds
            method.addBodyLine("if (CollectionUtils.isEmpty(ids)) {");
            method.addBodyLine("return Lists.newArrayList();");
            method.addBodyLine("}");
            method.addBodyLine("Condition<" + domainObjectName + "> " + lowCond + " = new Condition<>();");
            method.addBodyLine(lowCond + ".createCriteria().andIn(" + domainObjectName + ".ID, ids);");
            if (StringUtility.stringHasValue(page)) {
                method.addBodyLine(lowCond + ".limit(Page.getMaxRow());");
            }
            method.addBodyLine("return this.listByCondition(" + lowCond + ");");
        } else if (type == 5) {//list
            String resStr = getDaoShort() + "listLimitx(" + params + ", new LimitCondition(" + lowPo + ".getStart(), " + lowPo + ".getRow()));";
            method.addBodyLine(checkStr);
            method.addBodyLine("return " + resStr);
        } else if (type == 8) {//listByCondition
            String resStr = getDaoShort() + "listByConditionx(" + params + ");";
            method.addBodyLine(checkStr);
            method.addBodyLine("return " + resStr);
        }
        return method;
    }


    /**
     * add method
     */
    protected Method getOtherMap(String methodName, IntrospectedTable introspectedTable, String tableName, int type) {
        Method method = new Method();
        String domainObjectName = introspectedTable.getDomainObjectName();
        method.setName(methodName);
        method.addAnnotation("@Override");
        String returnType = "Map<Integer," + domainObjectName + ">";
        method.setReturnType(new FullyQualifiedJavaType(returnType));
        String params = addParams(introspectedTable, method, type);
        method.setVisibility(JavaVisibility.PUBLIC);
        CommentUtils.addGeneralMethodComment(method, introspectedTable);
        String invokeMethod;
        String lowPo = MethodUtils.toLowerCase(domainObjectName);
        String lowPoList = lowPo + "List";
        if (MethodEnum.MAP.getValue().equals(methodName) || MethodEnum.MAP_BY_CONDITION.getValue().equals(methodName)) {
            //生成日志信息
            if (enableLogger) {
                MethodUtils.addLoggerInfo(method, params);
            }
            invokeMethod = listByCondition;
            method.addBodyLine("List<" + domainObjectName + "> " + lowPoList + " = this." + invokeMethod + "(" + params + ");");
        } else {
            //生成日志信息
            if (enableLogger) {
                MethodUtils.addLoggerInfo(method, "ids");
            }
            invokeMethod = listByIds;
            method.addBodyLine("List<" + domainObjectName + "> " + lowPoList + " = this." + invokeMethod + "(ids);");
        }
        String getKey = "get" + MethodUtils.toUpperCase(introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty());
        method.addBodyLine("return " + lowPoList + ".stream().collect(Collectors.toMap(" + domainObjectName + "::" + getKey + "," + lowPo + " -> " + lowPo + ", (k1, k2) -> k2));");

        return method;
    }


    /**
     * add method
     */
    protected Method getOtherInsertBoolean(String methodName, IntrospectedTable introspectedTable, String tableName) {
        Method method = new Method();
        method.setName(methodName);
        method.addAnnotation("Override");
        method.setReturnType(returnType);
        method.addParameter(new Parameter(pojoType, MethodGeneratorUtils.getCondName(extentModelPlugin, introspectedTable.getDomainObjectName())));
        method.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder sb = new StringBuilder();
        if (returnType == null) {
            sb.append("this.");
        } else {
            sb.append("return ");
        }
        sb.append(getDaoShort());
        sb.append(methodName);
        sb.append("(");
        sb.append(MethodGeneratorUtils.getCondName(extentModelPlugin, introspectedTable.getDomainObjectName()));
        sb.append(");");
        method.addBodyLine(sb.toString());
        return method;
    }

    /**
     * type: pojo 1 key 2 example 3 pojo+example 4
     */
    protected String addParams(IntrospectedTable introspectedTable, Method method, int type1) {
        String domainObjectName = introspectedTable.getDomainObjectName();
        String lowCond = MethodUtils.toLowerCase(MethodGeneratorUtils.getCondName(extentModelPlugin, introspectedTable.getDomainObjectName()));
        Parameter parameter = new Parameter(new FullyQualifiedJavaType("Condition<" + domainObjectName + ">"), lowCond);
        String lowPo = MethodUtils.toLowerCase(domainObjectName);
        Parameter parameter2 = new Parameter(new FullyQualifiedJavaType(domainObjectName), lowPo);
        switch (type1) {
            case 1:
                method.addParameter(new Parameter(pojoType, lowPo)); //$NON-NLS-1$
                return lowPo;
            case 2:
                if (introspectedTable.getRules().generatePrimaryKeyClass()) {
                    FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
                    method.addParameter(new Parameter(type, "key"));
                } else {
                    for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                        FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
                        method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
                    }
                }
                StringBuffer sb = new StringBuffer();
                for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
                    sb.append(introspectedColumn.getJavaProperty());
                    sb.append(",");
                }
                sb.setLength(sb.length() - 1);
                return sb.toString();
            case 5:
                method.addParameter(parameter2);
                return lowPo;
            case 6:
                //设置参数类型是List
                FullyQualifiedJavaType paramType = new FullyQualifiedJavaType("List<Integer>");
                method.addParameter(new Parameter(paramType, "ids")); //$NON-NLS-1$
                return "ids";
            case 7:
                method.addParameter(parameter);
                return lowCond;
            case 8:
                method.addParameter(parameter);
                return lowCond;
            default:
                break;
        }
        return null;
    }


    /**
     * import must class
     */
    private void addImport(Interface interfaces, TopLevelClass topLevelClass) {
        if (interfaces != null) {
            interfaces.addImportedType(pojoType);
            interfaces.addImportedType(listType);
            FullyQualifiedJavaType mapType = new FullyQualifiedJavaType("java.util.Map");
            interfaces.addImportedType(mapType);
            //添加Remote注解
            if (StringUtility.stringHasValue(remote)) {
                FullyQualifiedJavaType remoteAnno = new FullyQualifiedJavaType(remote);
                interfaces.addImportedType(remoteAnno);
                String annotation = "@" + remoteAnno.getShortName();
                if (StringUtility.stringHasValue(applicationName)) {
                    int index = applicationName.lastIndexOf('.');
                    FullyQualifiedJavaType applicationNameAnno = new FullyQualifiedJavaType(applicationName.substring(0, index));
                    interfaces.addImportedType(applicationNameAnno);
                    annotation = annotation + "(applicationName = " + applicationNameAnno.getShortName() + applicationName.substring(index) + ")";
                    interfaces.addAnnotation(annotation);
                }
            }
        }
        if (topLevelClass != null) {
            topLevelClass.addImportedType(daoType);
            topLevelClass.addImportedType(interfaceType);
            topLevelClass.addImportedType(pojoType);
            topLevelClass.addImportedType(listType);
            if (enableLogger) {
                topLevelClass.addImportedType(slf4jLogger);
                topLevelClass.addImportedType(slf4jLoggerFactory);
            }
            if (enableAnnotation) {
                topLevelClass.addImportedType(service);
                topLevelClass.addImportedType(autowired);
            }

            if (topLevelClass.getType().getShortName().endsWith("Impl")) {
                FullyQualifiedJavaType override = new FullyQualifiedJavaType("java.lang.Override");
                topLevelClass.addImportedType(override);
                if (StringUtility.stringHasValue(userNameMethod)) {
                    FullyQualifiedJavaTypeUtils.importType(null, topLevelClass, MethodUtils.getFullClass(userNameMethod, "."));
                }

                if (StringUtility.stringHasValue(dateMethod)) {
                    FullyQualifiedJavaTypeUtils.importType(interfaces, topLevelClass, MethodUtils.getFullClass(dateMethod, "."));
                }

            }
        }
    }


    private String getDaoShort() {
        return MethodUtils.toLowerCase(daoType.getShortName()) + ".";
    }

    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        returnType = method.getReturnType();
        return true;
    }


    private String getMethodName(String fullMethodName, String str, String defaultValue) {
        String setValue = null;
        if (!hasColumn(str)) {
            return null;
        }
        if (StringUtility.stringHasValue(fullMethodName)) {
            setValue = MethodUtils.getFullMethod(fullMethodName, ".");
        }
        if (!StringUtility.stringHasValue(setValue)) {//没有配置默认方法，则使用默认值
            setValue = defaultValue;
        }
        return setValue;
    }


    private boolean hasColumn(String str) {
        for (IntrospectedColumn column : columns) {
            if (str.equals(column.getActualColumnName())) {
                return true;
            }
        }
        return false;
    }

    //设置方法值
    private void setMethodValue(Method method, String params, String column, String val) {
        if (!hasColumn(column)) {
            return;
        }
        method.addBodyLine("if (StringUtils.isEmpty(" + MethodUtils.generateGet(params, column) + ")) {");
        if (CommonConstant.DEFAULT_USER.equals(val)) {
            method.addBodyLine(MethodUtils.generateSet(params, column, "\"" + val + "\"") + ";");
        } else {
            method.addBodyLine(MethodUtils.generateSet(params, column, val) + ";");
        }
        method.addBodyLine("}");
    }

    private boolean hasDateColumn(String dateMethod, String... actualColumns) {
        if (!StringUtility.stringHasValue(dateMethod)) {
            if (actualColumns != null && actualColumns.length > 0) {
                for (String column : actualColumns) {
                    if (hasColumn(column)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
