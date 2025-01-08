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
package fr.paris.lutece.plugins.workflow.service.task;

import fr.paris.lutece.plugins.workflow.modules.archive.business.ArchiveConfig;
import fr.paris.lutece.plugins.workflow.modules.choice.business.ChoiceTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.comment.business.TaskCommentConfig;
import fr.paris.lutece.plugins.workflow.modules.confirmaction.business.ConfirmActionTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.notification.business.TaskNotificationConfig;
import fr.paris.lutece.plugins.workflow.modules.state.business.ChangeStateTaskConfigDao;
import fr.paris.lutece.plugins.workflow.modules.state.business.ChooseStateTaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.config.TaskConfigService;
import fr.paris.lutece.portal.service.util.RemovalListenerService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class TaskConfigServiceProducer
{

    @Produces
    @ApplicationScoped
    @Named( "workflow.chooseStateTaskConfigService" )
    public ITaskConfigService produceChooseTaskService(
            @Named( "workflow.chooseStateTaskConfigDao" ) ITaskConfigDAO<ChooseStateTaskConfig> chooseStateTaskConfigDao )
    {
        TaskConfigService taskService = new TaskConfigService( );
        taskService.setTaskConfigDAO( (ITaskConfigDAO) chooseStateTaskConfigDao );
        return taskService;
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow.changeStateTaskConfigService" )
    public ITaskConfigService produceChangeTaskService( @Named( "workflow.changeStateTaskConfigDao" ) ChangeStateTaskConfigDao changeStateTaskConfigDao )
    {
        TaskConfigService taskService = new TaskConfigService( );
        taskService.setTaskConfigDAO( (ITaskConfigDAO) changeStateTaskConfigDao );
        return taskService;
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow.taskCommentConfigService" )
    public ITaskConfigService produceTaskCommentConfigService(
            @Named( "workflow.taskCommentConfigDAO" ) ITaskConfigDAO<TaskCommentConfig> taskCommentConfigDAO )
    {
        TaskConfigService taskService = new TaskConfigService( );
        taskService.setTaskConfigDAO( (ITaskConfigDAO) taskCommentConfigDAO );
        return taskService;
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow.taskNotificationConfigService" )
    public ITaskConfigService produceTaskNotificationConfigService(
            @Named( "workflow.taskNotificationConfigDAO" ) ITaskConfigDAO<TaskNotificationConfig> taskNotificationConfigDAO )
    {
        TaskConfigService taskService = new TaskConfigService( );
        taskService.setTaskConfigDAO( (ITaskConfigDAO) taskNotificationConfigDAO );
        return taskService;
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow.taskArchiveConfigService" )
    public ITaskConfigService produceTaskArchiveConfigService( @Named( "workflow.taskArchiveConfigDao" ) ITaskConfigDAO<ArchiveConfig> taskArchiveConfigDao )
    {
        TaskConfigService taskService = new TaskConfigService( );
        taskService.setTaskConfigDAO( (ITaskConfigDAO) taskArchiveConfigDao );
        return taskService;
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow.confirmActionTaskConfigService" )
    public ITaskConfigService produceConfirmActionTaskConfigService(
            @Named( "workflow.confirmActionTaskConfigDAO" ) ITaskConfigDAO<ConfirmActionTaskConfig> confirmActionTaskConfigDAO )
    {
        TaskConfigService taskService = new TaskConfigService( );
        taskService.setTaskConfigDAO( (ITaskConfigDAO) confirmActionTaskConfigDAO );
        return taskService;
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow.choiceTaskConfigService" )
    public ITaskConfigService produceChoiceTaskConfigService( @Named( "workflow.choiceTaskConfigDAO" ) ITaskConfigDAO<ChoiceTaskConfig> choiceTaskConfigDAO )
    {
        TaskConfigService taskService = new TaskConfigService( );
        taskService.setTaskConfigDAO( (ITaskConfigDAO) choiceTaskConfigDAO );
        return taskService;
    }

    @Produces
    @ApplicationScoped
    @Named( "workflow.taskRemovalService" )
    public RemovalListenerService produceRemovalListenerService( )
    {
        return new RemovalListenerService( );
    }

}
