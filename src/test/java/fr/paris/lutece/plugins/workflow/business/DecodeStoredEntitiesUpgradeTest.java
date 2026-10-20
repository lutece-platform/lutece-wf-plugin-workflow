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
package fr.paris.lutece.plugins.workflow.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/**
 * The upgrade changeset that decodes, once, the HTML entities the removed input filter of lutece-core (sanitizeFilterMode,
 * LUT-34109) wrote into the plain text columns. The script is read and its REPLACE calls are applied to typical stored
 * values as the database would; the columns it decodes and the ones it must leave as they are (rich text, identifiers,
 * histories) are checked against the inventory. No database is needed.
 */
public class DecodeStoredEntitiesUpgradeTest
{
    private static final String UPGRADE_SCRIPT = "src/sql/plugins/workflow/upgrade/update_db_workflow-7.0.1-7.0.2.sql";
    private static final String CHANGESET = "-- changeset workflow:update_db_workflow-7.0.1-7.0.2.sql-decode-entities";

    /** The plain text columns the changeset decodes, as table.column, in the order of the script. */
    private static final List<String> DECODED_COLUMNS = List.of( "workflow_workflow.name",
            "workflow_workflow.description",
            "workflow_state.name",
            "workflow_state.description",
            "workflow_action.name",
            "workflow_action.description",
            "workflow_icon.name",
            "workflow_task_comment_config.title",
            "workflow_task_comment_value.comment_value",
            "workflow_task_notification_cf.sender_name",
            "workflow_task_notification_cf.subject",
            "workflow_task_assignment_cf.title",
            "workflow_task_assignment_cf.subject" );

    /**
     * Columns holding HTML, identifiers or histories, which the changeset must not touch (the history columns that copy a plain
     * text value are decoded by the history changeset).
     */
    private static final List<String> COLUMNS_LEFT_AS_IS = List.of( "workflow_task_notification_cf.message",
            "workflow_task_assignment_cf.message",
            "workflow_task_confirm_action_config.message",
            "workflow_task_choice_config.message",
            "workflow_resource_user_history.first_name",
            "workflow_resource_history.user_access_code",
            "workflow_workflow.workgroup_key" );

    /** The changeset that decodes the histories the back office shows, separate from the first one. */
    private static final String HISTORY_CHANGESET = "-- changeset workflow:update_db_workflow-7.0.1-7.0.2.sql-decode-entities-history";

    /** The history columns that hold a copy of a plain text value, which the history changeset decodes, in the order of the script. */
    private static final List<String> HISTORY_DECODED_COLUMNS = List.of( "workflow_resource_user_history.first_name",
            "workflow_resource_user_history.last_name",
            "workflow_resource_user_history.email",
            "workflow_task_change_state_information.new_state",
            "workflow_task_choose_state_information.new_state" );

    /** History columns holding rendered texts, HTML or identifiers, which neither changeset must touch. */
    private static final List<String> HISTORY_COLUMNS_LEFT_AS_IS = List.of( "workflow_resource_user_history.user_access_code",
            "workflow_resource_user_history.realm",
            "workflow_resource_history.user_access_code",
            "workflow_resource_history.resource_type",
            "workflow_assignment_history.workgroup_key" );

    /** The conditions the history changeset adds to the guard LIKE '%&%;%', by table.column. */
    private static final Map<String, String> HISTORY_CONDITIONS = Map.of( );

    /** The decoding of the changeset rev20 of lutece-core (update_db_lutece_core-8.0.1-8.0.2.sql), &amp; last. */
    private static final List<String> CORE_DECODING = List.of( "&#39;=>'", "&#x27;=>'", "&#34;=>\"", "&quot;=>\"", "&#96;=>`", "&#61;=>=",
            "&#64;=>@", "&#43;=>+", "&lt;=><", "&gt;=>>", "&amp;=>&" );

    private static final Pattern UPDATE = Pattern.compile( "UPDATE (\\w+) SET (\\w+) = (REPLACE\\(.*?\\)) WHERE ([^\\n]*);" );
    private static final Pattern REPLACED = Pattern.compile( "'((?:[^']|'')*)', '((?:[^']|'')*)'\\)" );
    private static final String GUARD = "LIKE '%&%;%'";

    /**
     * Reads a decoding changeset: from its header to the next changeset or the end of the script.
     *
     * @param strHeader
     *            the header of the changeset
     * @return the text of the changeset
     * @throws IOException
     *             if the script cannot be read
     */
    private static String changeset( String strHeader ) throws IOException
    {
        String strScript = Files.readString( Path.of( UPGRADE_SCRIPT ), StandardCharsets.UTF_8 );
        int nStart = strScript.indexOf( strHeader );
        assertTrue( nStart >= 0, "The changeset " + strHeader + " is missing from " + UPGRADE_SCRIPT );
        int nEnd = strScript.indexOf( "-- changeset ", nStart + strHeader.length( ) );

        return nEnd < 0 ? strScript.substring( nStart ) : strScript.substring( nStart, nEnd );
    }

    /**
     * Reads, for each UPDATE of the first decoding changeset, the replacements of its REPLACE calls in the order they apply.
     *
     * @return the replacements (entity=>character) by table.column
     * @throws IOException
     *             if the script cannot be read
     */
    private static Map<String, List<String>> decodings( ) throws IOException
    {
        return decodings( CHANGESET );
    }

    /**
     * Reads, for each UPDATE of a changeset, the replacements of its REPLACE calls in the order they apply.
     *
     * @param strHeader
     *            the header of the changeset
     * @return the replacements (entity=>character) by table.column
     * @throws IOException
     *             if the script cannot be read
     */
    private static Map<String, List<String>> decodings( String strHeader ) throws IOException
    {
        Map<String, List<String>> mapDecodings = new LinkedHashMap<>( );
        Matcher update = UPDATE.matcher( changeset( strHeader ) );
        while ( update.find( ) )
        {
            List<String> listReplacements = new ArrayList<>( );
            Matcher replaced = REPLACED.matcher( update.group( 3 ) );
            while ( replaced.find( ) )
            {
                listReplacements.add( replaced.group( 1 ).replace( "''", "'" ) + "=>" + replaced.group( 2 ).replace( "''", "'" ) );
            }
            assertTrue( update.group( 4 ).contains( update.group( 2 ) + " " + GUARD ),
                    "The UPDATE of " + update.group( 1 ) + "." + update.group( 2 ) + " must only touch the values holding an entity" );
            mapDecodings.put( update.group( 1 ) + "." + update.group( 2 ), listReplacements );
        }
        return mapDecodings;
    }

    /**
     * Reads the WHERE clause of each UPDATE of a changeset.
     *
     * @param strHeader
     *            the header of the changeset
     * @return the WHERE clauses by table.column
     * @throws IOException
     *             if the script cannot be read
     */
    private static Map<String, String> whereClauses( String strHeader ) throws IOException
    {
        Map<String, String> mapWhere = new LinkedHashMap<>( );
        Matcher update = UPDATE.matcher( changeset( strHeader ) );
        while ( update.find( ) )
        {
            mapWhere.put( update.group( 1 ) + "." + update.group( 2 ), update.group( 4 ) );
        }
        return mapWhere;
    }

    /**
     * Applies the changeset to a stored value of a column, as its WHERE clause and REPLACE calls do.
     *
     * @param strColumn
     *            the column, as table.column
     * @param strValue
     *            the stored value
     * @return the value after the upgrade
     * @throws IOException
     *             if the script cannot be read
     */
    private static String applyUpgrade( String strColumn, String strValue ) throws IOException
    {
        String strUpgraded = strValue;
        int nAmpersand = strUpgraded.indexOf( '&' );
        if ( nAmpersand < 0 || strUpgraded.indexOf( ';', nAmpersand ) < 0 )
        {
            // WHERE ... LIKE '%&%;%': the value is not updated
            return strUpgraded;
        }
        List<String> listReplacements = decodings( ).get( strColumn );
        if ( listReplacements == null )
        {
            listReplacements = decodings( HISTORY_CHANGESET ).get( strColumn );
        }
        for ( String strReplacement : listReplacements )
        {
            int nArrow = strReplacement.indexOf( "=>" );
            strUpgraded = strUpgraded.replace( strReplacement.substring( 0, nArrow ), strReplacement.substring( nArrow + 2 ) );
        }
        return strUpgraded;
    }

    /**
     * The changeset decodes exactly the plain text columns of the inventory, with the decoding of the core.
     */
    @Test
    public void testDecodesThePlainTextColumnsLikeTheCore( ) throws IOException
    {
        Map<String, List<String>> mapDecodings = decodings( );

        assertEquals( DECODED_COLUMNS, new ArrayList<>( mapDecodings.keySet( ) ) );
        mapDecodings.forEach( ( strColumn, listReplacements ) -> assertEquals( CORE_DECODING, listReplacements, strColumn ) );
    }

    /**
     * The rich text, the identifiers and the histories are left as they are.
     */
    @Test
    public void testLeavesTheOtherColumnsAsTheyAre( ) throws IOException
    {
        Map<String, List<String>> mapDecodings = decodings( );

        for ( String strColumn : COLUMNS_LEFT_AS_IS )
        {
            assertFalse( mapDecodings.containsKey( strColumn ), strColumn + " must be left as it is" );
        }
    }

    /**
     * The values the filter encoded get back the characters that were typed, decoded once: a typed entity stays an entity.
     */
    @Test
    public void testDecodesTypicalValuesOnce( ) throws IOException
    {
        for ( String strColumn : DECODED_COLUMNS )
        {
            assertDecodedOnce( strColumn );
        }
    }

    /**
     * Checks that the upgrade decodes once the typical values of a column.
     *
     * @param strColumn
     *            the column, as table.column
     * @throws IOException
     *             if the script cannot be read
     */
    private static void assertDecodedOnce( String strColumn ) throws IOException
    {
        assertEquals( "L'école de l'Est", applyUpgrade( strColumn, "L&#39;école de l&#x27;Est" ), strColumn );
        assertEquals( "\"Tom\" & \"Jerry\"", applyUpgrade( strColumn, "&#34;Tom&#34; &amp; &quot;Jerry&quot;" ), strColumn );
        assertEquals( "<b>2 + 2 = 4</b> @ `x`", applyUpgrade( strColumn, "&lt;b&gt;2 &#43; 2 &#61; 4&lt;/b&gt; &#64; &#96;x&#96;" ), strColumn );
        assertEquals( "&lt;", applyUpgrade( strColumn, "&amp;lt;" ), strColumn );
        assertEquals( "&amp; &#39;", applyUpgrade( strColumn, "&amp;amp; &amp;#39;" ), strColumn );
        assertEquals( "Tom & Jerry", applyUpgrade( strColumn, "Tom & Jerry" ), strColumn );
        assertEquals( "Sans entité", applyUpgrade( strColumn, "Sans entité" ), strColumn );
    }

    /**
     * The histories have their own changeset, with the header of the first one: to run once, after a backup.
     */
    @Test
    public void testHistoryChangesetIsSeparateAndRunsOnce( ) throws IOException
    {
        String strChangeset = changeset( HISTORY_CHANGESET );

        assertTrue( strChangeset.contains( "-- preconditions onFail:MARK_RAN onError:WARN" ), HISTORY_CHANGESET );
        assertTrue( strChangeset.contains( "-- comment decodes, once," ), HISTORY_CHANGESET );
        assertTrue( strChangeset.contains( "Back up the database before the upgrade. It must run ONCE only" ), HISTORY_CHANGESET );
        for ( String strColumn : HISTORY_DECODED_COLUMNS )
        {
            assertFalse( decodings( ).containsKey( strColumn ), strColumn + " is decoded by the history changeset only" );
        }
    }

    /**
     * The history changeset decodes exactly the history columns that copy a plain text value, with the decoding of the core,
     * and only the values holding an entity, with the conditions of the inventory.
     */
    @Test
    public void testDecodesTheHistoryColumnsLikeTheCore( ) throws IOException
    {
        Map<String, List<String>> mapDecodings = decodings( HISTORY_CHANGESET );

        assertEquals( HISTORY_DECODED_COLUMNS, new ArrayList<>( mapDecodings.keySet( ) ) );
        mapDecodings.forEach( ( strColumn, listReplacements ) -> assertEquals( CORE_DECODING, listReplacements, strColumn ) );

        Map<String, String> mapWhere = whereClauses( HISTORY_CHANGESET );
        for ( String strColumn : HISTORY_DECODED_COLUMNS )
        {
            String strCondition = HISTORY_CONDITIONS.getOrDefault( strColumn, "" );
            String strColumnName = strColumn.substring( strColumn.indexOf( '.' ) + 1 );
            assertEquals( ( strColumnName + " " + GUARD + " " + strCondition ).trim( ), mapWhere.get( strColumn ), strColumn );
        }
    }

    /**
     * The rendered texts, the HTML and the identifiers of the histories are left as they are by both changesets.
     */
    @Test
    public void testLeavesTheOtherHistoryColumnsAsTheyAre( ) throws IOException
    {
        Map<String, List<String>> mapDecodings = decodings( );
        Map<String, List<String>> mapHistoryDecodings = decodings( HISTORY_CHANGESET );

        for ( String strColumn : HISTORY_COLUMNS_LEFT_AS_IS )
        {
            assertFalse( mapDecodings.containsKey( strColumn ), strColumn + " must be left as it is" );
            assertFalse( mapHistoryDecodings.containsKey( strColumn ), strColumn + " must be left as it is" );
        }
    }

    /**
     * The history values the filter encoded get back the characters that were typed, decoded once.
     */
    @Test
    public void testDecodesTypicalHistoryValuesOnce( ) throws IOException
    {
        for ( String strColumn : HISTORY_DECODED_COLUMNS )
        {
            assertDecodedOnce( strColumn );
        }
    }

    /**
     * The header warns that the changeset rewrites the data of the site and must run once only.
     */
    @Test
    public void testHeaderSaysItRunsOnce( ) throws IOException
    {
        String strChangeset = changeset( CHANGESET );

        assertTrue( strChangeset.contains( "rewrites the data of the site" ) );
        assertTrue( strChangeset.contains( "must run ONCE only" ) );
    }
}
