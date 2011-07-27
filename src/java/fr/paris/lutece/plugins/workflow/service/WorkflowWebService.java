/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.service;

import fr.paris.lutece.plugins.workflow.utils.signrequest.UserAttributeRequestAuthenticator;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * WorkflowWebService
 *
 */
public final class WorkflowWebService
{
    private static final String BEAN_WORKFLOW_WEBSERVICE = "workflow.workflowWebService";

    // CONSTANTS
    private static final String JSON_EXTENSION = ".json";
    private static final String SLASH = "/";

    // TAGS
    private static final String TAG_USER_ATTRIBUTES = "user-attributes";
    private static final String TAG_USER_ATTRIBUTE_KEY = "user-attribute-key";
    private static final String TAG_USER_ATTRIBUTE_VALUE = "user-attribute-value";

    // MARKS
    private static final String MARK_FIRST_NAME = "first_name";
    private static final String MARK_LAST_NAME = "last_name";
    private static final String MARK_EMAIL = "email";
    private static final String MARK_PHONE_NUMBER = "phone_number";

    // PROPERTIES
    private static final String PROPERTY_REST_USER_ATTRIBUTE_WEBAPP_URL = "workflow.rest.userAttribute.webapp.url";
    private static final String PROPERTY_REST_USER_ATTRIBUTE_GETATTRIBUTES_URL = "workflow.rest.userAttribute.getAttributes.url";

    // PARAMETERS
    private static final String PARAMETER_USER_GUID = "user_guid";

    /**
     * Private constructor
     */
    private WorkflowWebService(  )
    {
    }

    /**
     * Get the instance of the service
     * @return the instance of the service
     */
    public static WorkflowWebService getService(  )
    {
        return (WorkflowWebService) SpringContextService.getPluginBean( WorkflowPlugin.PLUGIN_NAME,
            BEAN_WORKFLOW_WEBSERVICE );
    }

    /**
     * Get the user attribute rest webapp url
     * @return the user attribute rest webapp url
     */
    public static String getRestUserAttributeWebappUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_REST_USER_ATTRIBUTE_WEBAPP_URL );
    }

    /**
     * Get the url to get the user attributes
     * @return the url to get the user attributes
     */
    public static String getRestUserAttributeGetAttributesUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_REST_USER_ATTRIBUTE_GETATTRIBUTES_URL );
    }

    /**
     * Calls the rest ws to fetch the user attributes from the given user guid
     * @param strUserGuid the user guid
     * @return a map containing the user attributes
     */
    public Map<String, String> getUserAttributes( String strUserGuid )
    {
        Map<String, String> mapUserAttributes = new HashMap<String, String>(  );

        if ( isUserAttributeWSActive(  ) )
        {
            StringBuilder sbRestUrl = new StringBuilder(  );
            sbRestUrl.append( AppPropertiesService.getProperty( PROPERTY_REST_USER_ATTRIBUTE_WEBAPP_URL ) );
            sbRestUrl.append( AppPropertiesService.getProperty( PROPERTY_REST_USER_ATTRIBUTE_GETATTRIBUTES_URL ) );
            sbRestUrl.append( strUserGuid );
            sbRestUrl.append( JSON_EXTENSION );
            sbRestUrl.append( "?" + PARAMETER_USER_GUID + "=" + strUserGuid );

            List<String> listElements = new ArrayList<String>(  );
            listElements.add( strUserGuid );

            String strJson = StringUtils.EMPTY;

            try
            {
                HttpAccess httpAccess = new HttpAccess(  );
                strJson = httpAccess.doGet( sbRestUrl.toString(  ),
                        UserAttributeRequestAuthenticator.getRequestAuthenticator(  ), listElements );

                JSONObject jsonAttributes = (JSONObject) JSONSerializer.toJSON( strJson );
                JSONArray jsonArray = jsonAttributes.getJSONArray( TAG_USER_ATTRIBUTES );

                for ( int i = 0; i < jsonArray.size(  ); i++ )
                {
                    JSONObject jsonAttribute = jsonArray.getJSONObject( i );
                    String strKey = jsonAttribute.getString( TAG_USER_ATTRIBUTE_KEY );
                    String strValue = jsonAttribute.getString( TAG_USER_ATTRIBUTE_VALUE );
                    mapUserAttributes.put( strKey, strValue );
                }
            }
            catch ( HttpAccessException e )
            {
                String strError = "WorkflowWebServices - Error connecting to '" + sbRestUrl.toString(  ) +
                    "' when fetching user attributes of the user '" + strUserGuid + "' : ";
                AppLogService.error( strError + e.getMessage(  ), e );
                throw new AppException( e.getMessage(  ), e );
            }
        }

        return mapUserAttributes;
    }

    /**
     * Calls the rest ws to fetch the user attribute
     * @param strUserGuid the user guid
     * @param strAttributeKey the attribute key
     * @return the user attribute value
     */
    public String getUserAttribute( String strUserGuid, String strAttributeKey )
    {
        String strUserAttribute = StringUtils.EMPTY;

        if ( isUserAttributeWSActive(  ) )
        {
            StringBuilder sbRestUrl = new StringBuilder(  );
            sbRestUrl.append( AppPropertiesService.getProperty( PROPERTY_REST_USER_ATTRIBUTE_WEBAPP_URL ) );
            sbRestUrl.append( AppPropertiesService.getProperty( PROPERTY_REST_USER_ATTRIBUTE_GETATTRIBUTES_URL ) );
            sbRestUrl.append( strUserGuid );
            sbRestUrl.append( SLASH );
            sbRestUrl.append( strAttributeKey );

            try
            {
                HttpAccess httpAccess = new HttpAccess(  );
                strUserAttribute = httpAccess.doGet( sbRestUrl.toString(  ) );
            }
            catch ( HttpAccessException e )
            {
                String strError = "WorkflowWebServices - Error connecting to '" + sbRestUrl.toString(  ) +
                    "' when fetching the user attribute '" + strAttributeKey + "' of the user '" + strUserGuid +
                    "' : ";
                AppLogService.error( strError + e.getMessage(  ), e );
                throw new AppException( e.getMessage(  ), e );
            }
        }

        return strUserAttribute;
    }

    /**
     * Check if the user attribute rest webapp is active.
     * <br />
     * This method just checks if the property <b>workflow.rest.userAttribute.webapp.url</b>
     * and the property <b>workflow.rest.userAttribute.getAttributes.url</b> are filled or not.
     * @return true if it is active, false otherwise
     */
    public static boolean isUserAttributeWSActive(  )
    {
        boolean bIsActive = false;
        String strRestWebappUrl = getRestUserAttributeWebappUrl(  );
        String strRestGetAttributesUrl = getRestUserAttributeGetAttributesUrl(  );

        if ( StringUtils.isNotBlank( strRestWebappUrl ) && StringUtils.isNotBlank( strRestGetAttributesUrl ) )
        {
            bIsActive = true;
        }

        return bIsActive;
    }

    /**
     * Fill the model with user attributes
     * @param model the model
     * @param strUserGuid the user guid
     */
    public void fillUserAttributesToModel( Map<String, String> model, String strUserGuid )
    {
        if ( isUserAttributeWSActive(  ) && StringUtils.isNotBlank( strUserGuid ) && ( model != null ) )
        {
            Map<String, String> mapUserAttributes = getUserAttributes( strUserGuid );
            String strFirstName = mapUserAttributes.get( LuteceUser.NAME_GIVEN );
            String strLastName = mapUserAttributes.get( LuteceUser.NAME_FAMILY );
            String strEmail = mapUserAttributes.get( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL );
            String strPhoneNumber = mapUserAttributes.get( LuteceUser.BUSINESS_INFO_TELECOM_TELEPHONE_NUMBER );

            model.put( MARK_FIRST_NAME, StringUtils.isNotEmpty( strFirstName ) ? strFirstName : StringUtils.EMPTY );
            model.put( MARK_LAST_NAME, StringUtils.isNotEmpty( strLastName ) ? strLastName : StringUtils.EMPTY );
            model.put( MARK_EMAIL, StringUtils.isNotEmpty( strEmail ) ? strEmail : StringUtils.EMPTY );
            model.put( MARK_PHONE_NUMBER, StringUtils.isNotEmpty( strPhoneNumber ) ? strPhoneNumber : StringUtils.EMPTY );
        }
    }
}
