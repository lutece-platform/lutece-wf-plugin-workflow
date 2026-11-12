/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.workflow.business.workflowimport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * DAO of the workflow import. It is the only one reading and writing the import fingerprints, so that a save from the administration screens never
 * touches them.
 */
public class WorkflowImportDAO implements IWorkflowImportDAO
{
    public static final String BEAN_NAME = "workflow.workflowImportDAO";

    private static final String SQL_QUERY_SELECT_WORKFLOW_BY_UID = "SELECT id_workflow FROM workflow_workflow WHERE uid_workflow = ? ORDER BY id_workflow";
    private static final String SQL_QUERY_UPDATE_WORKFLOW_IMPORT = "UPDATE workflow_workflow SET uid_workflow = ?, last_import_date = ? WHERE id_workflow = ?";
    private static final String SQL_QUERY_SELECT_LAST_IMPORT_DATE = "SELECT last_import_date FROM workflow_workflow WHERE id_workflow = ?";
    private static final String SQL_QUERY_SELECT_RESOURCE_COUNT_BY_STATE = "SELECT id_state, COUNT(*) FROM workflow_resource_workflow WHERE id_workflow = ? GROUP BY id_state";
    private static final String SQL_QUERY_SELECT_HISTORY_BY_ACTION = "SELECT id_action, COUNT(*), MAX(creation_date) FROM workflow_resource_history WHERE id_workflow = ? GROUP BY id_action";

    @Override
    public List<Integer> selectWorkflowIdsByUid( String strUid )
    {
        List<Integer> listId = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_WORKFLOW_BY_UID, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setString( 1, strUid );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listId.add( daoUtil.getInt( 1 ) );
            }
        }
        return listId;
    }

    @Override
    public Map<Integer, String> selectImportHashes( ImportElement element, int nIdWorkflow )
    {
        Map<Integer, String> mapHash = new HashMap<>( );
        try ( DAOUtil daoUtil = new DAOUtil( element.getSelectHashByWorkflow( ), WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdWorkflow );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                mapHash.put( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }
        }
        return mapHash;
    }

    @Override
    public void updateImportHash( ImportElement element, int nId, String strHash )
    {
        try ( DAOUtil daoUtil = new DAOUtil( element.getUpdateHash( ), WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setString( 1, strHash );
            daoUtil.setInt( 2, nId );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void updateUid( ImportElement element, int nId, String strUid )
    {
        try ( DAOUtil daoUtil = new DAOUtil( element.getUpdateUid( ), WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setString( 1, strUid );
            daoUtil.setInt( 2, nId );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void updateWorkflowImport( int nIdWorkflow, String strUid, Timestamp date )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_WORKFLOW_IMPORT, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setString( 1, strUid );
            daoUtil.setTimestamp( 2, date );
            daoUtil.setInt( 3, nIdWorkflow );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public Timestamp selectLastImportDate( int nIdWorkflow )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LAST_IMPORT_DATE, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdWorkflow );
            daoUtil.executeQuery( );
            return daoUtil.next( ) ? daoUtil.getTimestamp( 1 ) : null;
        }
    }

    @Override
    public Map<Integer, Integer> selectResourceCountByState( int nIdWorkflow )
    {
        Map<Integer, Integer> mapCount = new HashMap<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_RESOURCE_COUNT_BY_STATE, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdWorkflow );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                mapCount.put( daoUtil.getInt( 1 ), daoUtil.getInt( 2 ) );
            }
        }
        return mapCount;
    }

    @Override
    public Map<Integer, Integer> selectHistoryCountByAction( int nIdWorkflow )
    {
        Map<Integer, Integer> mapCount = new HashMap<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_HISTORY_BY_ACTION, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdWorkflow );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                mapCount.put( daoUtil.getInt( 1 ), daoUtil.getInt( 2 ) );
            }
        }
        return mapCount;
    }

    @Override
    public Map<Integer, Timestamp> selectLastExecutionByAction( int nIdWorkflow )
    {
        Map<Integer, Timestamp> mapDate = new HashMap<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_HISTORY_BY_ACTION, WorkflowUtils.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdWorkflow );
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                mapDate.put( daoUtil.getInt( 1 ), daoUtil.getTimestamp( 3 ) );
            }
        }
        return mapDate;
    }
}
