/**
 * Copyright 2006-2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.model;

import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.utils.ContextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.mybatis.generator.internal.util.messages.Messages.getString;


/**
 * Description: vo生成类
 * Author: guos
 * Date: 2019/1/30 16:31
 **/
public class BaseRecordVoGenerator extends AbstractJavaGenerator {

    private Properties properties;

    private boolean hasControllerPlugin = false;

    public BaseRecordVoGenerator() {
        super();
    }

    public BaseRecordVoGenerator(Context context) {
        PluginConfiguration extentModelPlugin = ContextUtils.checkExtendModelPlugin(context);
        this.properties = extentModelPlugin.getProperties();
        hasControllerPlugin = true;
    }

    public boolean isHasControllerPlugin() {
        return hasControllerPlugin;
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {

        if (!hasControllerPlugin) {
            return new ArrayList<>();
        }

        //String voProject = properties.getProperty("voProject");
        String voPack = properties.getProperty("voPack");
        String voSuffix = properties.getProperty("voSuffix") == null ? CommonConstant.VO_SUFFIX : properties.getProperty("voSuffix");


        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString(
                "Progress.8", table.toString())); //$NON-NLS-1$

        String voType = voPack + "." + introspectedTable.getDomainObjectName() + voSuffix;
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                voType);
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);

        FullyQualifiedJavaType poType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        // 继承po类
        topLevelClass.setSuperClass(poType);
        topLevelClass.addImportedType(poType);

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();

        if (context.getPlugins().modelBaseRecordClassGenerated(
                topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }
        return answer;
    }


    @Override
    public String getModelTargetProject() {
        return properties.getProperty("voProject");
    }
}
