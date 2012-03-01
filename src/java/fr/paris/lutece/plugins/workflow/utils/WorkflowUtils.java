/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.utils;

import fr.paris.lutece.portal.business.workflow.IReferenceItem;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

import java.sql.Timestamp;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * class DirectoryUtils
 *
 */
public final class WorkflowUtils
{
    // Constants
    public static final String CONSTANT_WHERE = " WHERE ";
    public static final String CONSTANT_AND = " AND ";
    public static final int CONSTANT_ID_NULL = -1;
    public static final String EMPTY_STRING = "";
    public static final String PROPERTY_SELECT_EMPTY_CHOICE = "workflow.select_empty_choice";

    // property
    private static final String REGEX_ID = "^[\\d]+$";

    /**
     * DirectoryUtils
     *
     */
    private WorkflowUtils(  )
    {
    }

    /**
     * return current Timestamp
     *
     * @return return current Timestamp
     */
    public static Timestamp getCurrentTimestamp(  )
    {
        return new Timestamp( GregorianCalendar.getInstance(  ).getTimeInMillis(  ) );
    }

    /**
     * write the http header in the response
     *
     * @param request
     *            the httpServletRequest
     * @param response
     *            the http response
     * @param strFileName
     *            the name of the file who must insert in the response
     *
     */
    public static void addHeaderResponse( HttpServletRequest request, HttpServletResponse response, String strFileName )
    {
        response.setHeader( "Content-Disposition", "attachment ;filename=\"" + strFileName + "\"" );
        response.setHeader( "Pragma", "public" );
        response.setHeader( "Expires", "0" );
        response.setHeader( "Cache-Control", "must-revalidate,post-check=0,pre-check=0" );
    }

    /**
     * convert a string to int
     *
     * @param strParameter
     *            the string parameter to convert
     * @return the conversion
     */
    public static int convertStringToInt( String strParameter )
    {
        int nIdParameter = -1;

        try
        {
            if ( ( strParameter != null ) && strParameter.matches( REGEX_ID ) )
            {
                nIdParameter = Integer.parseInt( strParameter );
            }
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        return nIdParameter;
    }

    /**
     * Returns a copy of the string , with leading and trailing whitespace
     * omitted.
     *
     * @param strParameter
     *            the string parameter to convert
     * @return null if the strParameter is null other return with leading and
     *         trailing whitespace omitted.
     */
    public static String trim( String strParameter )
    {
        if ( strParameter != null )
        {
            return strParameter.trim(  );
        }

        return strParameter;
    }

    /**
     * Builds a query with filters placed in parameters
     * @param strSelect the select of the  query
     * @param listStrFilter the list of filter to add in the query
     * @param strOrder the order by of the query
     * @return a query
     */
    public static String buildRequestWithFilter( String strSelect, List<String> listStrFilter, String strOrder )
    {
        StringBuffer strBuffer = new StringBuffer(  );
        strBuffer.append( strSelect );

        int nCount = 0;

        for ( String strFilter : listStrFilter )
        {
            if ( ++nCount == 1 )
            {
                strBuffer.append( CONSTANT_WHERE );
            }

            strBuffer.append( strFilter );

            if ( nCount != listStrFilter.size(  ) )
            {
                strBuffer.append( CONSTANT_AND );
            }
        }

        if ( strOrder != null )
        {
            strBuffer.append( strOrder );
        }

        return strBuffer.toString(  );
    }

    /**
     * return a referenceList
     * @param listReferenceItem a list of referenc Item
     * @param bWitdthEmptyChoice true if a empty item must be insert in the reference list
     * @param locale the locale
     * @return referencelist
     */
    public static ReferenceList getRefList( Collection listReferenceItem, boolean bWitdthEmptyChoice, Locale locale )
    {
        return getRefList( listReferenceItem, bWitdthEmptyChoice,
            I18nService.getLocalizedString( PROPERTY_SELECT_EMPTY_CHOICE, locale ), locale );
    }

    /**
     * return a referenceList
     * @param listReferenceItem a list of referenc Item
     * @param bWitdthEmptyChoice true if a empty item must be insert in the reference list
     * @param strLabelEmptyChoice the empty choice label
     * @param locale the locale
     * @return referencelist
     */
    public static ReferenceList getRefList( Collection listReferenceItem, boolean bWitdthEmptyChoice,
        String strLabelEmptyChoice, Locale locale )
    {
        ReferenceList refList = new ReferenceList(  );

        if ( bWitdthEmptyChoice )
        {
            refList.addItem( WorkflowUtils.CONSTANT_ID_NULL, strLabelEmptyChoice );
        }

        for ( Object item : listReferenceItem )
        {
            refList.addItem( ( (IReferenceItem) item ).getId(  ), ( (IReferenceItem) item ).getName(  ) );
        }

        return refList;
    }
}
