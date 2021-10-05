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
package fr.paris.lutece.plugins.workflow.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileItem;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.icon.IIconService;
import fr.paris.lutece.plugins.workflowcore.service.icon.IconService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * JspBean to manage workflow icons
 */
public class IconJspBean extends PluginAdminPageJspBean
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 1953322601057873584L;

    // templates
    private static final String TEMPLATE_MANAGE_ICON = "admin/plugins/workflow/manage_icon.html";
    private static final String TEMPLATE_CREATE_ICON = "admin/plugins/workflow/create_icon.html";
    private static final String TEMPLATE_MODIFY_ICON = "admin/plugins/workflow/modify_icon.html";

    // Markers
    private static final String MARK_ICON_LIST = "icon_list";
    private static final String MARK_MODIFY_ICON = "modify_icon";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";

    // parameters form
    private static final String PARAMETER_ID_ICON = "id_icon";
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_ID_FILE = "id_file";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_WIDTH = "width";
    private static final String PARAMETER_HEIGHT = "height";
    private static final String PARAMETER_CANCEL = "cancel";

    // other constants
    private static final String EMPTY_STRING = "";

    // message
    private static final String MESSAGE_CONFIRM_REMOVE_ICON = "workflow.message.confirm_remove_icon";
    private static final String MESSAGE_MANDATORY_FIELD = "workflow.message.mandatory.field";
    private static final String MESSAGE_CAN_NOT_REMOVE_ICON_ACTIONS_ARE_ASSOCIATE = "workflow.message.can_not_remove_icon_actions_are_associate";
    private static final String MESSAGE_NUMERIC_FIELD = "workflow.message.numeric_field";

    // properties
    private static final String PROPERTY_ITEM_PER_PAGE = "workflow.itemsPerPage";
    private static final String PROPERTY_MANAGE_ICON = "workflow.manage_icon.page_title";
    private static final String PROPERTY_MODIFY_ICON = "workflow.modify_icon.page_title";
    private static final String PROPERTY_CREATE_ICON = "workflow.create_icon.page_title";
    private static final String FIELD_NAME = "workflow.create_icon.label_name";
    private static final String FIELD_WIDTH = "workflow.create_icon.label_width";
    private static final String FIELD_HEIGHT = "workflow.create_icon.label_height";
    private static final String FIELD_FILE = "workflow.create_icon.label_file";

    // Jsp Definition
    private static final String JSP_MANAGE_ICON = "jsp/admin/plugins/workflow/ManageIcon.jsp";
    private static final String JSP_DO_REMOVE_ICON = "jsp/admin/plugins/workflow/DoRemoveIcon.jsp";

    // session fields
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 15 );
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;
    private IIconService _iconService = SpringContextService.getBean( IconService.BEAN_SERVICE );
    private IActionService _actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );

    /**
     * Return management icon ( list of icon )
     * 
     * @param request
     *            The Http request
     * @return Html management icon
     */
    public String getManageIcon( HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<>( );
        List<Icon> listDirectoryXsl = _iconService.getListIcons( );
        _strCurrentPageIndex = AbstractPaginator.getPageIndex( request, AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        LocalizedPaginator<Icon> paginator = new LocalizedPaginator<>( listDirectoryXsl, _nItemsPerPage, getJspManageIcon( request ), PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex, getLocale( ) );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPage );
        model.put( MARK_ICON_LIST, paginator.getPageItems( ) );
        setPageTitleProperty( PROPERTY_MANAGE_ICON );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ICON, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Gets the icon creation page
     * 
     * @param request
     *            The HTTP request
     * @return The directory xsl creation page
     */
    public String getCreateIcon( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_CREATE_ICON );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_ICON, getLocale( ) );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the icon creation
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateIcon( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            Icon icon = new Icon( );
            String strError = getIconData( request, icon );

            if ( strError != null )
            {
                return strError;
            }

            _iconService.create( icon );
        }

        return getJspManageIcon( request );
    }

    /**
     * Gets the icon modification page
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The icon creation page
     */
    public String getModifyIcon( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdIcon = request.getParameter( PARAMETER_ID_ICON );
        int nIdIcon = WorkflowUtils.convertStringToInt( strIdIcon );
        Icon icon = _iconService.findByPrimaryKey( nIdIcon );

        if ( icon == null )
        {
            throw new AccessDeniedException( "The icon is not found for ID " + nIdIcon );
        }

        Map<String, Object> model = new HashMap<>( );

        model.put( MARK_MODIFY_ICON, icon );
        setPageTitleProperty( PROPERTY_MODIFY_ICON );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_ICON, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the icon modification
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doModifyIcon( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strIdIcon = request.getParameter( PARAMETER_ID_ICON );
            int nIdIcon = WorkflowUtils.convertStringToInt( strIdIcon );
            Icon icon = _iconService.findByPrimaryKey( nIdIcon );

            if ( icon == null )
            {
                throw new AccessDeniedException( "The icon is not found for ID " + nIdIcon );
            }

            String strError = getIconData( request, icon );

            if ( strError != null )
            {
                return strError;
            }

            if ( icon.getValue( ) != null )
            {
                _iconService.update( icon );
            }
            else
            {
                _iconService.updateMetadata( icon );
            }
        }

        return getJspManageIcon( request );
    }

    /**
     * Gets the confirmation page of delete icon
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return the confirmation page of delete directory xsl
     */
    public String getConfirmRemoveIcon( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdIcon = request.getParameter( PARAMETER_ID_ICON );
        int nIdIcon = WorkflowUtils.convertStringToInt( strIdIcon );
        ActionFilter filter = new ActionFilter( );
        filter.setAutomaticReflexiveAction( false );
        filter.setIdIcon( nIdIcon );

        List<Action> listAction = _actionService.getListActionByFilter( filter );

        if ( CollectionUtils.isNotEmpty( listAction ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CAN_NOT_REMOVE_ICON_ACTIONS_ARE_ASSOCIATE, AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_ICON );
        url.addParameter( PARAMETER_ID_ICON, strIdIcon );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_ICON, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the icon supression
     * 
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             the {@link AccessDeniedException}
     * @return The URL to go after performing the action
     */
    public String doRemoveIcon( HttpServletRequest request ) throws AccessDeniedException
    {
        String strIdIcon = request.getParameter( PARAMETER_ID_ICON );
        int nIdIcon = WorkflowUtils.convertStringToInt( strIdIcon );
        ActionFilter filter = new ActionFilter( );
        filter.setAutomaticReflexiveAction( false );
        filter.setIdIcon( nIdIcon );

        List<Action> listAction = _actionService.getListActionByFilter( filter );

        if ( !listAction.isEmpty( ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_CAN_NOT_REMOVE_ICON_ACTIONS_ARE_ASSOCIATE, AdminMessage.TYPE_STOP );
        }

        _iconService.remove( nIdIcon );

        return getJspManageIcon( request );
    }

    /**
     * Get the request data and if there is no error insert the data in the icon object specified in parameter. return null if there is no error or else return
     * the error page url
     * 
     * @param request
     *            the request
     * @param icon
     *            the Icon Object
     * @return null if there is no error or else return the error page url
     */
    private String getIconData( HttpServletRequest request, Icon icon )
    {
        String strError = WorkflowUtils.EMPTY_STRING;
        String strName = request.getParameter( PARAMETER_NAME );
        String strWidth = request.getParameter( PARAMETER_WIDTH );
        String strHeight = request.getParameter( PARAMETER_HEIGHT );

        int nWidth = WorkflowUtils.convertStringToInt( strWidth );
        int nHeight = WorkflowUtils.convertStringToInt( strHeight );

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        FileItem fileItem = multipartRequest.getFile( PARAMETER_ID_FILE );

        if ( ( strName == null ) || strName.trim( ).equals( EMPTY_STRING ) )
        {
            strError = FIELD_NAME;
        }
        else
            if ( ( icon.getValue( ) == null )
                    && ( ( fileItem == null ) || ( ( fileItem.getName( ) == null ) && WorkflowUtils.EMPTY_STRING.equals( fileItem.getName( ) ) ) ) )
            {
                strError = FIELD_FILE;
            }

        // Mandatory fields
        if ( !strError.equals( EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strError, getLocale( ) )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        if ( ( strWidth != null ) && ( !strWidth.trim( ).equals( WorkflowUtils.EMPTY_STRING ) ) && ( nWidth == -1 ) )
        {
            strError = FIELD_WIDTH;
        }
        else
            if ( ( strHeight != null ) && ( !strHeight.trim( ).equals( WorkflowUtils.EMPTY_STRING ) ) && ( nHeight == -1 ) )
            {
                strError = FIELD_HEIGHT;
            }

        if ( !strError.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strError, getLocale( ) )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        icon.setName( strName );

        if ( ( fileItem != null ) && ( fileItem.getName( ) != null ) && !WorkflowUtils.EMPTY_STRING.equals( fileItem.getName( ) ) )
        {
            icon.setValue( fileItem.get( ) );
            icon.setMimeType( fileItem.getContentType( ) );
        }
        else
        {
            icon.setValue( null );
        }

        icon.setWidth( nWidth );
        icon.setHeight( nHeight );

        return null;
    }

    /**
     * return the url of manage export format
     * 
     * @param request
     *            the request
     * @return the url of manage export format
     */
    private String getJspManageIcon( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_ICON;
    }
}
