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

/**
 * The elements of a workflow the import matches one by one, with the table each one lives in
 */
public enum ImportElement
{
    STATE( "workflow_state", "id_state", "uid_state", "SELECT e.id_state, e.import_hash FROM workflow_state e WHERE e.id_workflow = ?" ),
    ACTION( "workflow_action", "id_action", "uid_action", "SELECT e.id_action, e.import_hash FROM workflow_action e WHERE e.id_workflow = ?" ),
    TASK( "workflow_task", "id_task", "uid_task",
            "SELECT e.id_task, e.import_hash FROM workflow_task e, workflow_action a WHERE e.id_action = a.id_action AND a.id_workflow = ?" ),
    PREREQUISITE( "workflow_prerequisite", "id_prerequisite", "uid_prerequisite",
            "SELECT e.id_prerequisite, e.import_hash FROM workflow_prerequisite e, workflow_action a WHERE e.id_action = a.id_action AND a.id_workflow = ?" );

    private final String _strTable;
    private final String _strIdColumn;
    private final String _strUidColumn;
    private final String _strSelectHashByWorkflow;

    ImportElement( String strTable, String strIdColumn, String strUidColumn, String strSelectHashByWorkflow )
    {
        _strTable = strTable;
        _strIdColumn = strIdColumn;
        _strUidColumn = strUidColumn;
        _strSelectHashByWorkflow = strSelectHashByWorkflow;
    }

    String getSelectHashByWorkflow( )
    {
        return _strSelectHashByWorkflow;
    }

    String getUpdateHash( )
    {
        return "UPDATE " + _strTable + " SET import_hash = ? WHERE " + _strIdColumn + " = ?";
    }

    String getUpdateUid( )
    {
        return "UPDATE " + _strTable + " SET " + _strUidColumn + " = ? WHERE " + _strIdColumn + " = ?";
    }
}
