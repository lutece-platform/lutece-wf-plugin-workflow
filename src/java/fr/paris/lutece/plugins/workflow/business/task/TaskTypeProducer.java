/*
 * Copyright (c) 2002-2025, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.business.task;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class TaskTypeProducer
{

    @Produces
    @ApplicationScoped
    @Named( "workflow.taskTypeComment" )
    public ITaskType produceTaskTypeComment( @ConfigProperty( name = "workflow.taskTypeComment.key" ) String key,
            @ConfigProperty( name = "workflow.taskTypeComment.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow.taskTypeComment.beanName" ) String beanName,
            @ConfigProperty( name = "workflow.taskTypeComment.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow.taskTypeComment.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow.taskTypeComment.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow.taskTypeComment.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return TaskTypeBuilder.buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow.taskTypeNotification" )
    public ITaskType produceTaskTypeNotification( @ConfigProperty( name = "workflow.taskTypeNotification.key" ) String key,
            @ConfigProperty( name = "workflow.taskTypeNotification.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow.taskTypeNotification.beanName" ) String beanName,
            @ConfigProperty( name = "workflow.taskTypeNotification.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow.taskTypeNotification.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow.taskTypeNotification.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow.taskTypeNotification.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return TaskTypeBuilder.buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow.taskTypeAssignment" )
    public ITaskType produceTaskTypeAssignment( @ConfigProperty( name = "workflow.taskTypeAssignment.key" ) String key,
            @ConfigProperty( name = "workflow.taskTypeAssignment.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow.taskTypeAssignment.beanName" ) String beanName,
            @ConfigProperty( name = "workflow.taskTypeAssignment.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow.taskTypeAssignment.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow.taskTypeAssignment.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow.taskTypeAssignment.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return TaskTypeBuilder.buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow.taskTypeArchive" )
    public ITaskType produceTaskTypeArchive( @ConfigProperty( name = "workflow.taskTypeArchive.key" ) String key,
            @ConfigProperty( name = "workflow.taskTypeArchive.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow.taskTypeArchive.beanName" ) String beanName,
            @ConfigProperty( name = "workflow.taskTypeArchive.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow.taskTypeArchive.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow.taskTypeArchive.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow.taskTypeArchive.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return TaskTypeBuilder.buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow.taskTypeConfirmAction" )
    public ITaskType produceTaskTypeConfirmAction( @ConfigProperty( name = "workflow.taskTypeConfirmAction.key" ) String key,
            @ConfigProperty( name = "workflow.taskTypeConfirmAction.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow.taskTypeConfirmAction.beanName" ) String beanName,
            @ConfigProperty( name = "workflow.taskTypeConfirmAction.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow.taskTypeConfirmAction.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow.taskTypeConfirmAction.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow.taskTypeConfirmAction.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return TaskTypeBuilder.buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow.taskTypeChoice" )
    public ITaskType produceTaskTypeChoice( @ConfigProperty( name = "workflow.taskTypeChoice.key" ) String key,
            @ConfigProperty( name = "workflow.taskTypeChoice.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow.taskTypeChoice.beanName" ) String beanName,
            @ConfigProperty( name = "workflow.taskTypeChoice.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow.taskTypeChoice.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow.taskTypeChoice.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow.taskTypeChoice.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return TaskTypeBuilder.buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow.chooseStateTaskType" )
    public ITaskType produceChooseStateTaskType( @ConfigProperty( name = "workflow.chooseStateTaskType.key" ) String key,
            @ConfigProperty( name = "workflow.chooseStateTaskType.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow.chooseStateTaskType.beanName" ) String beanName,
            @ConfigProperty( name = "workflow.chooseStateTaskType.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow.chooseStateTaskType.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow.chooseStateTaskType.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow.chooseStateTaskType.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return TaskTypeBuilder.buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow.changeStateTaskType" )
    public ITaskType produceChangeStateTaskType( @ConfigProperty( name = "workflow.changeStateTaskType.key" ) String key,
            @ConfigProperty( name = "workflow.changeStateTaskType.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow.changeStateTaskType.beanName" ) String beanName,
            @ConfigProperty( name = "workflow.changeStateTaskType.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow.changeStateTaskType.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow.changeStateTaskType.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow.changeStateTaskType.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return TaskTypeBuilder.buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
    }

}
