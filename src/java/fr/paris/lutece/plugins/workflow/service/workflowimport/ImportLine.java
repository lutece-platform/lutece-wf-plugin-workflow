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
package fr.paris.lutece.plugins.workflow.service.workflowimport;

import java.sql.Timestamp;

import fr.paris.lutece.plugins.workflow.business.workflowimport.ImportElement;

/**
 * What the import does with one element : the element of the file, the element of the target it updates, or one of the two alone
 */
public final class ImportLine
{
    /**
     * The outcome of the comparison
     */
    public enum Status
    {
        /** same UUID in the file and in the target : updated in place */
        MATCHED,
        /** paired by name or by hand : updated in place, and takes the UUID of the file */
        PAIRED,
        /** only in the file : created */
        NEW,
        /** only in the target, used by nothing : removed */
        ABSENT_UNUSED,
        /** only in the target, but holding resources, history, or referred to by what is kept : kept */
        ABSENT_USED
    }

    private final ImportElement _element;
    private final ImportItem _fileItem;
    private final ImportItem _targetItem;
    private final Status _status;
    private final boolean _bPairedByHand;
    private boolean _bChanged;
    private boolean _bWithoutCounterpart;
    private boolean _bModifiedOnTarget;
    private int _nUsage;
    private Timestamp _lastExecution;

    ImportLine( ImportElement element, ImportItem fileItem, ImportItem targetItem, Status status, boolean bPairedByHand )
    {
        _element = element;
        _fileItem = fileItem;
        _targetItem = targetItem;
        _status = status;
        _bPairedByHand = bPairedByHand;
    }

    public ImportElement getElement( )
    {
        return _element;
    }

    public ImportItem getFileItem( )
    {
        return _fileItem;
    }

    public ImportItem getTargetItem( )
    {
        return _targetItem;
    }

    public Status getStatus( )
    {
        return _status;
    }

    public boolean isPairedByHand( )
    {
        return _bPairedByHand;
    }

    /**
     * @return true when the update changes the element of the target
     */
    public boolean isChanged( )
    {
        return _bChanged;
    }

    void setChanged( boolean bChanged )
    {
        _bChanged = bChanged;
    }

    /**
     * @return true when the element of the target was modified since the last import : the update overwrites that change
     */
    public boolean isModifiedOnTarget( )
    {
        return _bModifiedOnTarget;
    }

    void setModifiedOnTarget( boolean bModifiedOnTarget )
    {
        _bModifiedOnTarget = bModifiedOnTarget;
    }

    /**
     * @return the resources in a state, the history rows of an action, 0 otherwise
     */
    public int getUsage( )
    {
        return _nUsage;
    }

    void setUsage( int nUsage )
    {
        _nUsage = nUsage;
    }

    /**
     * @return the last execution of an action, null otherwise
     */
    public Timestamp getLastExecution( )
    {
        return _lastExecution;
    }

    void setLastExecution( Timestamp lastExecution )
    {
        _lastExecution = lastExecution;
    }

    /**
     * @return true for an element of the file created although the target has elements no UUID matched : nothing was found to pair it with
     */
    public boolean isWithoutCounterpart( )
    {
        return _bWithoutCounterpart;
    }

    void setWithoutCounterpart( boolean bWithoutCounterpart )
    {
        _bWithoutCounterpart = bWithoutCounterpart;
    }

    /**
     * @return true when the line asks for attention : an element modified on the target, an element kept although the file removed it, an element of the
     *         file nothing was paired with, or a pairing whose names differ
     */
    public boolean isDanger( )
    {
        return _bModifiedOnTarget || _bWithoutCounterpart || _status == Status.ABSENT_USED
                || ( _status == Status.PAIRED && !ImportNames.same( _fileItem.getLabel( ), _targetItem.getLabel( ) ) );
    }
}
