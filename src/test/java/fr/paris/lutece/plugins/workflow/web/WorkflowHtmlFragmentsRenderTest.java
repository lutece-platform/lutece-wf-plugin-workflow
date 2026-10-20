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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.template.HtmlMarkup;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * Renders the workflow template that writes the task forms of an action (tasks_form.html) with HTML auto-escaping, as
 * WorkflowProvider builds its model (LUT-33153): the forms the task components return are marked with HtmlMarkup and
 * keep their tags, and a form left as a String would print as text. Only the FreeMarker engine is needed.
 */
public class WorkflowHtmlFragmentsRenderTest
{
    private static final String TEMPLATES_ROOT = "webapp/WEB-INF/templates";
    private static final String TEMPLATE_TASKS_FORM = "admin/plugins/workflow/tasks_form.html";
    private static final String MARK_TASK_FORM_ENTRY_LIST = "task_form_entry_list";
    private static final String FORM = "<div class=\"mb-3\"><label for=\"x\">a &lt;b&gt;</label><input id=\"x\" name=\"x\"></div>";

    /**
     * Renders tasks_form.html.
     *
     * @param listEntries
     *            the task forms
     * @return the HTML
     * @throws IOException
     *             if the template cannot be read
     * @throws TemplateException
     *             if the rendering fails
     */
    private static String render( List<?> listEntries ) throws IOException, TemplateException
    {
        Configuration cfg = new Configuration( Configuration.VERSION_2_3_0 );
        cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
        cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        cfg.setDirectoryForTemplateLoading( new File( TEMPLATES_ROOT ) );
        StringWriter writer = new StringWriter( );
        cfg.getTemplate( TEMPLATE_TASKS_FORM ).process( Map.of( MARK_TASK_FORM_ENTRY_LIST, listEntries ), writer );

        return writer.toString( );
    }

    /**
     * The task forms marked as markup are written as they are.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testMarkedTaskFormsKeepTheirTags( ) throws Exception
    {
        String strHtml = render( HtmlMarkup.ofAll( List.of( FORM, FORM ) ) );

        assertTrue( strHtml.contains( FORM ), strHtml );
    }

    /**
     * A task form left as a String is escaped: the reason the consumer marks it.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testAStringTaskFormIsEscaped( ) throws Exception
    {
        String strHtml = render( List.of( FORM ) );

        assertTrue( strHtml.contains( "&lt;div class=&quot;mb-3&quot;&gt;" ), strHtml );
    }
}
