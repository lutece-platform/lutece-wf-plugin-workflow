/*
 * Copyright (c) 2002-2026, City of Paris
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.junit.jupiter.api.Test;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * Renders, with HTML auto-escaping, the row macros of macro_workflows.html (LUT-34142): the order buttons and the
 * actions menu of a list row carry the name of their row in their accessible name, escaped. The core macros they call
 * are replaced by stand-ins that write their params map as attributes: only the FreeMarker engine is needed.
 */
public class WorkflowMacrosAccessibleNamesRenderTest
{
    private static final String TEMPLATES_ROOT = "webapp/WEB-INF/templates";
    private static final String MACROS = "macros.ftl";
    private static final String PAGE = "page.ftl";
    private static final String ATTRIBUTES = "<#list params as name, value> ${name}=\"${value}\"</#list>";
    private static final String MACROS_SOURCE = "<#macro btnGroup ariaLabel='' rest...><div role=\"group\" aria-label=\"${ariaLabel}\"><#nested></div></#macro>"
            + "<#macro aButton title='' params={} rest...><a title=\"${title}\"" + ATTRIBUTES + "></a></#macro>"
            + "<#macro button title='' params={} rest...><button title=\"${title}\"" + ATTRIBUTES + "><#nested></button></#macro>";
    private static final String ROW_NAME = "State <b>1</b>";
    private static final String ROW_NAME_ESCAPED = "State &lt;b&gt;1&lt;/b&gt;";

    /**
     * Renders a page that includes macro_workflows.html.
     *
     * @param strPage
     *            the source of the page
     * @return the HTML
     * @throws IOException
     *             if a template cannot be read
     * @throws TemplateException
     *             if the rendering fails
     */
    private static String render( String strPage ) throws IOException, TemplateException
    {
        StringTemplateLoader strings = new StringTemplateLoader( );
        strings.putTemplate( MACROS, MACROS_SOURCE );
        strings.putTemplate( PAGE, "<#include \"/admin/plugins/workflow/macro_workflows.html\" />" + strPage );
        Configuration cfg = new Configuration( Configuration.VERSION_2_3_0 );
        cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
        cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        cfg.setTemplateLoader( new MultiTemplateLoader( new TemplateLoader [ ] {
                strings, new FileTemplateLoader( new File( TEMPLATES_ROOT ) )
        } ) );
        cfg.addAutoInclude( MACROS );
        StringWriter writer = new StringWriter( );
        cfg.getTemplate( PAGE ).process( Map.of( "rowName", ROW_NAME ), writer );

        return writer.toString( );
    }

    /**
     * The order group and its up and down buttons, enabled or not, are named after their row.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testOrderButtonsCarryTheRowName( ) throws Exception
    {
        String strHtml = render( "<@orderButtons url='u?id=1' orderParam='order' order=1 max=2 token='t' label=rowName />" );

        assertTrue( strHtml.contains( "aria-label=\"#i18n{workflow.modify_workflow.order} " + ROW_NAME_ESCAPED + "\"" ), strHtml );
        assertTrue( strHtml.contains( "aria-disabled=\"true\" aria-label=\"#i18n{workflow.modify_workflow.moveUp} " + ROW_NAME_ESCAPED + "\"" ), strHtml );
        assertTrue( strHtml.contains( "aria-label=\"#i18n{workflow.modify_workflow.moveDown} " + ROW_NAME_ESCAPED + "\"" ), strHtml );
        assertFalse( strHtml.contains( ROW_NAME ), strHtml );
    }

    /**
     * The actions menu button is named after its row; without a row name, it keeps its title alone.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testActionsMenuCarriesTheRowName( ) throws Exception
    {
        String strHtml = render( "<@actionsMenu id='m1' label=rowName></@actionsMenu>" );
        assertTrue( strHtml.contains( "aria-label=\"#i18n{portal.util.labelActions} " + ROW_NAME_ESCAPED + "\"" ), strHtml );

        strHtml = render( "<@actionsMenu id='m1'></@actionsMenu>" );
        assertFalse( strHtml.contains( "aria-label" ), strHtml );
    }
}
