/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.workflow.service.prerequisite;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.business.prerequisite.IPrerequisiteDAO;
import fr.paris.lutece.plugins.workflow.business.prerequisite.PrerequisiteDAO;
import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.IPrerequisiteConfig;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.IPrerequisiteConfigDAO;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.plugins.workflowcore.service.prerequisite.IAutomaticActionPrerequisiteService;
import fr.paris.lutece.plugins.workflowcore.service.prerequisite.IPrerequisiteManagementService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * Implementation of the prerequisite management service
 */
public class PrerequisiteManagementService implements IPrerequisiteManagementService
{
    /**
     * Name of the bean of this service
     */
    public static final String BEAN_NAME = "workflow.prerequisiteManagementService";
    private IPrerequisiteDAO _dao;
    private Plugin _plugin;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IAutomaticActionPrerequisiteService> getPrerequisiteServiceList( )
    {
        return SpringContextService.getBeansOfType( IAutomaticActionPrerequisiteService.class );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAutomaticActionPrerequisiteService getPrerequisiteService( String strPrerequisiteType )
    {
        for ( IAutomaticActionPrerequisiteService prerequisiteService : getPrerequisiteServiceList( ) )
        {
            if ( StringUtils.equals( strPrerequisiteType, prerequisiteService.getPrerequisiteType( ) ) )
            {
                return prerequisiteService;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Prerequisite> getListPrerequisite( int nIdAction )
    {
        return getPrerequisiteDao( ).findByIdAction( nIdAction, getPlugin( ) );
    }

    @Override
    public Prerequisite findPrerequisite( int nIdPrerequisite )
    {
        return getPrerequisiteDao( ).findByPrimaryKey( nIdPrerequisite, getPlugin( ) );
    }

    @Override
    public void createPrerequisiteConfiguration( IPrerequisiteConfig config, IAutomaticActionPrerequisiteService prerequisiteService )
    {
        IPrerequisiteConfigDAO configDAO = getConfigurationDAO( prerequisiteService );

        if ( configDAO != null )
        {
            configDAO.createConfig( config );
        }
    }

    @Override
    public void updatePrerequisiteConfiguration( IPrerequisiteConfig config, IAutomaticActionPrerequisiteService prerequisiteService )
    {
        IPrerequisiteConfigDAO configDAO = getConfigurationDAO( prerequisiteService );

        if ( configDAO != null )
        {
            configDAO.updateConfig( config );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPrerequisiteConfig getPrerequisiteConfiguration( int nIdPrerequisite, IAutomaticActionPrerequisiteService prerequisiteService )
    {
        IPrerequisiteConfigDAO configDAO = getConfigurationDAO( prerequisiteService );

        if ( configDAO == null )
        {
            return null;
        }

        return configDAO.findByPrimaryKey( nIdPrerequisite );
    }

    @Override
    public void createPrerequisite( Prerequisite prerequisite )
    {
        getPrerequisiteDao( ).create( prerequisite, getPlugin( ) );
    }

    @Override
    public void modifyPrerequisite( Prerequisite prerequisite )
    {
        getPrerequisiteDao( ).update( prerequisite, getPlugin( ) );
    }

    @Override
    public void deletePrerequisite( int nIdPrerequisite )
    {
        Prerequisite prerequisite = getPrerequisiteDao( ).findByPrimaryKey( nIdPrerequisite, getPlugin( ) );
        getPrerequisiteDao( ).remove( nIdPrerequisite, getPlugin( ) );

        IAutomaticActionPrerequisiteService prerequisiteService = getPrerequisiteService( prerequisite.getPrerequisiteType( ) );

        if ( prerequisiteService.hasConfiguration( ) )
        {
            IPrerequisiteConfigDAO dao = getConfigurationDAO( prerequisiteService );

            if ( dao != null )
            {
                dao.removeConfig( nIdPrerequisite );
            }
        }
    }

    /**
     * Get the prerequisite DAO
     * 
     * @return The prerequisite DAO
     */
    private IPrerequisiteDAO getPrerequisiteDao( )
    {
        if ( _dao == null )
        {
            _dao = SpringContextService.getBean( PrerequisiteDAO.BEAN_NAME );
        }

        return _dao;
    }

    /**
     * Get the workflow plugin
     * 
     * @return The workflow plugin
     */
    private Plugin getPlugin( )
    {
        if ( _plugin == null )
        {
            _plugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );
        }

        return _plugin;
    }

    /**
     * Get the configuration DAO of a prerequisite service
     * 
     * @param prerequisiteService
     *            the service
     * @return The DAO, or null if no DAO was found
     */
    private IPrerequisiteConfigDAO getConfigurationDAO( IAutomaticActionPrerequisiteService prerequisiteService )
    {
        String strDaoBeanName = prerequisiteService.getConfigurationDaoBeanName( );

        if ( StringUtils.isEmpty( strDaoBeanName ) )
        {
            return null;
        }

        return SpringContextService.getBean( strDaoBeanName );
    }

    @Override
    public void copyPrerequisite( int nIdActionSource, int nIdActionTarget )
    {
        List<Prerequisite> listLinkedPrerequisite = getListPrerequisite( nIdActionSource );

        for ( Prerequisite prerequisite : listLinkedPrerequisite )
        {
            IAutomaticActionPrerequisiteService prerequisiteService = getPrerequisiteService( prerequisite.getPrerequisiteType( ) );
            IPrerequisiteConfig config = getPrerequisiteConfiguration( prerequisite.getIdPrerequisite( ), prerequisiteService );

            prerequisite.setIdAction( nIdActionTarget );
            createPrerequisite( prerequisite );

            if ( config != null )
            {
                config.setIdPrerequisite( prerequisite.getIdPrerequisite( ) );
                createPrerequisiteConfiguration( config, prerequisiteService );
            }
        }
    }

    @Override
    public void deletePrerequisiteByAction( int nIdAction )
    {
        List<Prerequisite> listLinkedPrerequisite = getListPrerequisite( nIdAction );
        for ( Prerequisite prerequisite : listLinkedPrerequisite )
        {
            deletePrerequisite( prerequisite.getIdPrerequisite( ) );
        }
    }
}
