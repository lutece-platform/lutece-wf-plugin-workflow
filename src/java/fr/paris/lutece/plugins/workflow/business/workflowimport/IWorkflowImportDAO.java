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
import java.util.List;
import java.util.Map;

/**
 * Access to what the workflow import keeps : the fingerprints written at each import, and the usage of the elements it may update
 */
public interface IWorkflowImportDAO
{
    /**
     * @param strUid
     *            a workflow UUID
     * @return the identifiers of the workflows carrying it
     */
    List<Integer> selectWorkflowIdsByUid( String strUid );

    /**
     * @param element
     *            the kind of element
     * @param nIdWorkflow
     *            the workflow
     * @return the fingerprint written by the last import, by element identifier; an element created by hand has none
     */
    Map<Integer, String> selectImportHashes( ImportElement element, int nIdWorkflow );

    /**
     * Writes the fingerprint of an element, only ever done by the import
     *
     * @param element
     *            the kind of element
     * @param nId
     *            the element identifier
     * @param strHash
     *            the fingerprint
     */
    void updateImportHash( ImportElement element, int nId, String strHash );

    /**
     * Gives an element the UUID of the element it was paired with in the imported file
     *
     * @param element
     *            the kind of element
     * @param nId
     *            the element identifier
     * @param strUid
     *            the UUID
     */
    void updateUid( ImportElement element, int nId, String strUid );

    /**
     * Gives a workflow the UUID of the imported one, and the date of this import
     *
     * @param nIdWorkflow
     *            the workflow
     * @param strUid
     *            the UUID
     * @param date
     *            the import date
     */
    void updateWorkflowImport( int nIdWorkflow, String strUid, Timestamp date );

    /**
     * @param nIdWorkflow
     *            the workflow
     * @return the date of the last import into it, null when it was never updated by an import
     */
    Timestamp selectLastImportDate( int nIdWorkflow );

    /**
     * @param nIdWorkflow
     *            the workflow
     * @return the number of resources currently in each state
     */
    Map<Integer, Integer> selectResourceCountByState( int nIdWorkflow );

    /**
     * @param nIdWorkflow
     *            the workflow
     * @return the number of history rows of each action
     */
    Map<Integer, Integer> selectHistoryCountByAction( int nIdWorkflow );

    /**
     * @param nIdWorkflow
     *            the workflow
     * @return the date each action was last executed
     */
    Map<Integer, Timestamp> selectLastExecutionByAction( int nIdWorkflow );
}
