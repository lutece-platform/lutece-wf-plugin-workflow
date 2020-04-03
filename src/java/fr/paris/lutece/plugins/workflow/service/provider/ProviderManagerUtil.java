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
package fr.paris.lutece.plugins.workflow.service.provider;

import fr.paris.lutece.plugins.workflowcore.service.provider.AbstractProviderManager;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * This class provides utility methods for {@link AbstractProviderManager} objects
 *
 */
public final class ProviderManagerUtil
{
    private static final String SEPARATOR = ".@.";

    /**
     * Private constructor
     */
    private ProviderManagerUtil( )
    {

    }

    /**
     * Retrieves the {@link AbstractProviderManager} from its id
     * 
     * @param strProviderManagerId
     *            the id of the {@code AbstractProviderManager}
     * @return the {@code AbstractProviderManager} object corresponding to the specified id, or {@code null} if no {@code AbstractProviderManager} can be found.
     */
    public static AbstractProviderManager fetchProviderManager( String strProviderManagerId )
    {
        AbstractProviderManager result = null;

        try
        {
            result = SpringContextService.getBean( strProviderManagerId );
        }
        catch( Exception e )
        {
            AppLogService.error( "Unable to retrieve the provider manager '" + strProviderManagerId + "'" );
        }

        return result;
    }

    /**
     * <p>
     * Builds the complete id of a provider.
     * </p>
     * <p>
     * The complete id is built from the specified {@code AbstractProviderManager} id and {@code IProvider} id
     * </p>
     * 
     * @param strProviderManagerId
     *            the {@code AbstractProviderManager} id
     * @param strProviderId
     *            the {@code IProvider} id
     * @return the complete id
     */
    public static String buildCompleteProviderId( String strProviderManagerId, String strProviderId )
    {
        return strProviderManagerId + SEPARATOR + strProviderId;
    }

    /**
     * Retrieves the {@code AbstractProviderManager} id from the complete id of a provider
     * 
     * @param strCompleteProviderId
     *            the complete id of a provider
     * @return the {@code AbstractProviderManager} id
     */
    public static String fetchProviderManagerId( String strCompleteProviderId )
    {
        if ( !StringUtils.isBlank( strCompleteProviderId ) )
        {
            return strCompleteProviderId.split( SEPARATOR ) [0];
        }

        return null;
    }

    /**
     * Retrieves the {@code IProvider} id from the complete id of a provider
     * 
     * @param strCompleteProviderId
     *            the complete id of a provider
     * @return the {@code IProvider} id
     */
    public static String fetchProviderId( String strCompleteProviderId )
    {
        String strResult = null;

        if ( !StringUtils.isBlank( strCompleteProviderId ) )
        {
            String [ ] listIds = strCompleteProviderId.split( SEPARATOR );

            if ( listIds.length > 1 )
            {
                strResult = listIds [1];
            }
        }

        return strResult;
    }
}
