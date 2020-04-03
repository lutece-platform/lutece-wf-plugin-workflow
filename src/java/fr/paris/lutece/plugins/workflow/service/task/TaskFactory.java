/*
 * Copyright (c) 2002-2020, City of Paris
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

import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskFactory;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.util.Collection;
import java.util.Locale;

/**
 *
 * TaskFactory
 *
 */
public class TaskFactory implements ITaskFactory
{
    /**
     * The name of the bean of this service
     */
    public static final String BEAN_SERVICE = "workflow.taskFactory";

    /**
     * {@inheritDoc}
     */
    @Override
    public ITask newTask( String strKey, Locale locale )
    {
        ITask task = this.newTask( strKey );
        if ( task == null )
        {
            return null;
        }

        if ( ( locale != null ) && ( task.getTaskType( ) != null ) && StringUtils.isNotBlank( task.getTaskType( ).getTitleI18nKey( ) ) )
        {
            task.getTaskType( ).setTitle( I18nService.getLocalizedString( task.getTaskType( ).getTitleI18nKey( ), locale ) );
        }

        return task;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITaskConfig newTaskConfig( String strKey )
    {
        if ( StringUtils.isBlank( strKey ) )
        {
            AppLogService.error( "TaskFactory ERROR : The key is empty." );

            return null;
        }

        Collection<ITaskType> listTaskType = getAllTaskTypes( );

        for ( ITaskType taskType : listTaskType )
        {
            if ( strKey.equals( taskType.getKey( ) ) )
            {
                try
                {
                    return SpringContextService.getBean( taskType.getConfigBeanName( ) );
                }
                catch( BeansException e )
                {
                    logBeanException( e );
                }
            }
        }

        AppLogService.error( "TaskFactory ERROR : The task type is not found." );

        return null;
    }

    private void logBeanException( BeansException e )
    {
        String beanName = "unknown bean";
        if ( e instanceof NoSuchBeanDefinitionException )
        {
            beanName = ( (NoSuchBeanDefinitionException) e ).getBeanName( );
        }
        if ( e instanceof CannotLoadBeanClassException )
        {
            beanName = ( (CannotLoadBeanClassException) e ).getBeanName( );
        }
        if ( e instanceof BeanDefinitionStoreException )
        {
            beanName = ( (BeanDefinitionStoreException) e ).getBeanName( );
        }
        AppLogService.error( "TaskFactory ERROR : could not load bean '" + beanName + "' - CAUSE : " + e.getMessage( ), e );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ITaskType> getAllTaskTypes( )
    {
        return SpringContextService.getBeansOfType( ITaskType.class );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ITaskType> getAllTaskTypes( Locale locale )
    {
        Collection<ITaskType> listTaskTypes = getAllTaskTypes( );
        if ( locale == null )
        {
            return listTaskTypes;
        }

        if ( CollectionUtils.isNotEmpty( listTaskTypes ) )
        {
            for ( ITaskType taskType : listTaskTypes )
            {
                taskType.setTitle( I18nService.getLocalizedString( taskType.getTitleI18nKey( ), locale ) );
            }
        }

        return listTaskTypes;
    }

    /**
     * Get new instance of {@link ITask}
     * 
     * @param strKey
     *            the task type key
     * @return a new instance of {@link ITask}
     */
    private ITask newTask( String strKey )
    {
        if ( StringUtils.isBlank( strKey ) )
        {
            AppLogService.error( "TaskFactory ERROR : The key is empty." );

            return null;
        }

        Collection<ITaskType> listTaskType = getAllTaskTypes( );

        for ( ITaskType taskType : listTaskType )
        {
            if ( strKey.equals( taskType.getKey( ) ) )
            {
                try
                {
                    ITask task = SpringContextService.getBean( taskType.getBeanName( ) );
                    task.setTaskType( taskType );

                    return task;
                }
                catch( BeansException e )
                {
                    logBeanException( e );
                }
            }
        }

        AppLogService.error( "TaskFactory ERROR : The task type is not found." );

        return null;
    }
}
