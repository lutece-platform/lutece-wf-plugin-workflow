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
package fr.paris.lutece.plugins.workflow.web.prerequisite;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.workflow.service.prerequisite.PrerequisiteManagementService;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.IPrerequisiteConfig;
import fr.paris.lutece.plugins.workflowcore.business.prerequisite.Prerequisite;
import fr.paris.lutece.plugins.workflowcore.service.prerequisite.IAutomaticActionPrerequisiteService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.bean.BeanUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * JspBean to manage prerequisite of automatic actions
 */
public class PrerequisiteJspBean extends PluginAdminPageJspBean
{
    private static final long serialVersionUID = 2375784363589766595L;
    private static final String MESSAGE_CONFIRM_REMOVE_PREREQUISITE = "workflow.modify_action.manage_prerequisite.confirmRemovePrerequisite";
    private static final String MARK_PREREQUISITE_TYPE = "prerequisite_type";
    private static final String MARK_ID_ACTION = "id_action";
    private static final String MARK_CONTENT = "content";
    private static final String MARK_ERRORS = "errors";
    private static final String MARK_ID_PREREQUISITE = "id_prerequisite";
    private static final String MARK_CANCEL = "cancel";
    private static final String TEMPLATE_CREATE_MODIFY_PREREQUISITE = "admin/plugins/workflow/create_modify_prerequisite.html";
    private static final String JSP_URL_MODIFY_ACTION = "jsp/admin/plugins/workflow/ModifyAction.jsp";
    private static final String JSP_URL_CREATE_PREREQUISITE = "jsp/admin/plugins/workflow/prerequisite/GetCreatePrerequisite.jsp";
    private static final String JSP_URL_MODIFY_PREREQUISITE = "jsp/admin/plugins/workflow/prerequisite/GetModifyPrerequisite.jsp";
    private static final String JSP_URL_DO_REMOVE_PREREQUISITE = "jsp/admin/plugins/workflow/prerequisite/DoRemovePrerequisite.jsp";
    private static final String SESSION_ERRORS = "workflow.prerequisite.session.errors";
    private static final String SESSION_CONFIG = "workflow.prerequisite.session.config";
    private PrerequisiteManagementService _prerequisiteManagementService = SpringContextService.getBean( PrerequisiteManagementService.BEAN_NAME );

    /**
     * Creates a new prerequisite. If the prerequisite needs to display a configuration form, then the form is returned. Otherwise, redirects the user to the
     * action management screen.
     * 
     * @param request
     *            The request
     * @param response
     *            The response
     * @return The HTML content to display, or null if the reponse has been redirected
     */
    public String getCreatePrerequisite( HttpServletRequest request, HttpServletResponse response )
    {
        String strPrerequisiteType = request.getParameter( MARK_PREREQUISITE_TYPE );
        int nIdAction = Integer.parseInt( request.getParameter( MARK_ID_ACTION ) );

        IAutomaticActionPrerequisiteService service = _prerequisiteManagementService.getPrerequisiteService( strPrerequisiteType );

        if ( !service.hasConfiguration( ) )
        {
            Prerequisite prerequisite = new Prerequisite( );
            prerequisite.setIdAction( nIdAction );
            prerequisite.setPrerequisiteType( strPrerequisiteType );
            _prerequisiteManagementService.createPrerequisite( prerequisite );

            UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_MODIFY_ACTION );
            url.addParameter( MARK_ID_ACTION, nIdAction );

            try
            {
                response.sendRedirect( url.getUrl( ) );
            }
            catch( IOException e )
            {
                AppLogService.error( e.getMessage( ), e );
            }

            return null;
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_PREREQUISITE_TYPE, strPrerequisiteType );
        model.put( MARK_ID_ACTION, nIdAction );

        Set<ConstraintViolation<IPrerequisiteConfig>> listErrors = (Set<ConstraintViolation<IPrerequisiteConfig>>) request.getSession( )
                .getAttribute( SESSION_ERRORS );
        IPrerequisiteConfig config;

        if ( listErrors != null )
        {
            model.put( MARK_ERRORS, listErrors );
            request.getSession( ).removeAttribute( SESSION_ERRORS );
            config = (IPrerequisiteConfig) request.getSession( ).getAttribute( SESSION_CONFIG );
            request.getSession( ).removeAttribute( SESSION_CONFIG );
        }
        else
        {
            config = service.getEmptyConfiguration( );
        }

        config.setPrerequisiteType( strPrerequisiteType );

        String strContent = service.getConfigHtml( config, request, getLocale( ) );
        model.put( MARK_CONTENT, strContent );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_MODIFY_PREREQUISITE, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Do create a prerequisite
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    public String doCreatePrerequisite( HttpServletRequest request )
    {
        String strPrerequisiteType = request.getParameter( MARK_PREREQUISITE_TYPE );
        int nIdAction = Integer.parseInt( request.getParameter( MARK_ID_ACTION ) );

        if ( StringUtils.isEmpty( request.getParameter( MARK_CANCEL ) ) )
        {
            IAutomaticActionPrerequisiteService service = _prerequisiteManagementService.getPrerequisiteService( strPrerequisiteType );

            if ( service != null )
            {
                IPrerequisiteConfig config = service.getEmptyConfiguration( );

                if ( config != null )
                {
                    BeanUtil.populate( config, request );

                    Set<ConstraintViolation<IPrerequisiteConfig>> listErrors = validate( config );

                    if ( CollectionUtils.isNotEmpty( listErrors ) )
                    {
                        request.getSession( ).setAttribute( SESSION_ERRORS, listErrors );
                        request.getSession( ).setAttribute( SESSION_CONFIG, config );

                        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_CREATE_PREREQUISITE );
                        url.addParameter( MARK_ID_ACTION, nIdAction );
                        url.addParameter( MARK_PREREQUISITE_TYPE, strPrerequisiteType );

                        return url.getUrl( );
                    }
                }

                Prerequisite prerequisite = new Prerequisite( );
                prerequisite.setIdAction( nIdAction );
                prerequisite.setPrerequisiteType( strPrerequisiteType );

                _prerequisiteManagementService.createPrerequisite( prerequisite );

                if ( config != null )
                {
                    config.setIdPrerequisite( prerequisite.getIdPrerequisite( ) );
                    _prerequisiteManagementService.createPrerequisiteConfiguration( config, service );
                }
            }
        }

        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_MODIFY_ACTION );
        url.addParameter( MARK_ID_ACTION, nIdAction );

        return url.getUrl( );
    }

    /**
     * Get the page to modify a prerequisite configuration
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     * @return the HTML content to display, or null if the response has been redirected
     */
    public String getModifyPrerequisite( HttpServletRequest request, HttpServletResponse response )
    {
        int nIdAction = Integer.parseInt( request.getParameter( MARK_ID_ACTION ) );
        int nIdPrerequisite = Integer.parseInt( request.getParameter( MARK_ID_PREREQUISITE ) );
        Prerequisite prerequisite = _prerequisiteManagementService.findPrerequisite( nIdPrerequisite );

        IAutomaticActionPrerequisiteService service = _prerequisiteManagementService.getPrerequisiteService( prerequisite.getPrerequisiteType( ) );

        if ( !service.hasConfiguration( ) )
        {
            UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_MODIFY_ACTION );
            url.addParameter( MARK_ID_ACTION, nIdAction );

            try
            {
                response.sendRedirect( url.getUrl( ) );
            }
            catch( IOException e )
            {
                AppLogService.error( e.getMessage( ), e );
            }

            return null;
        }

        IPrerequisiteConfig config;

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_PREREQUISITE_TYPE, prerequisite.getPrerequisiteType( ) );
        model.put( MARK_ID_ACTION, nIdAction );
        model.put( MARK_ID_PREREQUISITE, nIdPrerequisite );

        Set<ConstraintViolation<IPrerequisiteConfig>> listErrors = (Set<ConstraintViolation<IPrerequisiteConfig>>) request.getSession( )
                .getAttribute( SESSION_ERRORS );

        if ( listErrors != null )
        {
            model.put( MARK_ERRORS, listErrors );
            request.getSession( ).removeAttribute( SESSION_ERRORS );
            config = (IPrerequisiteConfig) request.getSession( ).getAttribute( SESSION_CONFIG );
            request.getSession( ).removeAttribute( SESSION_CONFIG );
        }
        else
        {
            config = _prerequisiteManagementService.getPrerequisiteConfiguration( nIdPrerequisite, service );
        }

        config.setIdPrerequisite( nIdPrerequisite );
        config.setPrerequisiteType( prerequisite.getPrerequisiteType( ) );

        String strContent = service.getConfigHtml( config, request, getLocale( ) );
        model.put( MARK_CONTENT, strContent );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_MODIFY_PREREQUISITE, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Do modify a prerequisite configuration
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    public String doModifyPrerequisite( HttpServletRequest request )
    {
        int nIdAction = Integer.parseInt( request.getParameter( MARK_ID_ACTION ) );

        if ( StringUtils.isEmpty( request.getParameter( MARK_CANCEL ) ) )
        {
            int nIdPrerequisite = Integer.parseInt( request.getParameter( MARK_ID_PREREQUISITE ) );
            Prerequisite prerequisite = _prerequisiteManagementService.findPrerequisite( nIdPrerequisite );
            IAutomaticActionPrerequisiteService service = _prerequisiteManagementService.getPrerequisiteService( prerequisite.getPrerequisiteType( ) );

            IPrerequisiteConfig config = _prerequisiteManagementService.getPrerequisiteConfiguration( nIdPrerequisite, service );

            if ( config != null )
            {
                BeanUtil.populate( config, request );

                Set<ConstraintViolation<IPrerequisiteConfig>> listErrors = validate( config );

                if ( CollectionUtils.isNotEmpty( listErrors ) )
                {
                    request.getSession( ).setAttribute( SESSION_ERRORS, listErrors );
                    request.getSession( ).setAttribute( SESSION_CONFIG, config );

                    UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_MODIFY_PREREQUISITE );
                    url.addParameter( MARK_ID_ACTION, nIdAction );
                    url.addParameter( MARK_PREREQUISITE_TYPE, prerequisite.getPrerequisiteType( ) );
                    url.addParameter( MARK_ID_PREREQUISITE, nIdPrerequisite );

                    return url.getUrl( );
                }

                _prerequisiteManagementService.updatePrerequisiteConfiguration( config, service );
            }
        }

        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_MODIFY_ACTION );
        url.addParameter( MARK_ID_ACTION, nIdAction );

        return url.getUrl( );
    }

    /**
     * Get a confirmation message before removing a prerequisite
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    public String getConfirmRemovePrerequisite( HttpServletRequest request )
    {
        UrlItem urlItem = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_DO_REMOVE_PREREQUISITE );
        urlItem.addParameter( MARK_ID_ACTION, request.getParameter( MARK_ID_ACTION ) );
        urlItem.addParameter( MARK_ID_PREREQUISITE, request.getParameter( MARK_ID_PREREQUISITE ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_PREREQUISITE, urlItem.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Do remove a prerequisite
     * 
     * @param request
     *            the request
     * @return the next URL to redirect to
     */
    public String doRemovePrerequisite( HttpServletRequest request )
    {
        int nIdAction = Integer.parseInt( request.getParameter( MARK_ID_ACTION ) );
        int nIdPrerequisite = Integer.parseInt( request.getParameter( MARK_ID_PREREQUISITE ) );

        _prerequisiteManagementService.deletePrerequisite( nIdPrerequisite );

        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_URL_MODIFY_ACTION );
        url.addParameter( MARK_ID_ACTION, nIdAction );

        return url.getUrl( );
    }
}
