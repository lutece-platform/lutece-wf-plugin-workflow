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
package fr.paris.lutece.plugins.workflow.business.testresource;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * TestResourceDAO
 *
 */
public class TestResourceDAO implements ITestResourceDAO
{
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_test_resource ) FROM workflow_test_resource";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_test_resource,title FROM " +
        "workflow_test_resource WHERE id_test_resource=?";
    private static final String SQL_QUERY_SELECT = "SELECT id_test_resource,title FROM " +
        "workflow_test_resource ORDER BY id_test_resource";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_test_resource " +
        "(id_test_resource,title)VALUES(?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_test_resource  WHERE id_test_resource=? ";

    /**
     * Generates a new primary key
     * @param plugin the plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( TestResource testResource, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nPos = 0;
        testResource.setIdResource( newPrimaryKey( plugin ) );

        daoUtil.setInt( ++nPos, testResource.getIdResource(  ) );
        daoUtil.setString( ++nPos, testResource.getTitle(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TestResource load( int nIdTestResource, Plugin plugin )
    {
        TestResource testResource = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );

        daoUtil.setInt( 1, nIdTestResource );

        daoUtil.executeQuery(  );

        int nPos = 0;

        if ( daoUtil.next(  ) )
        {
            testResource = new TestResource(  );
            testResource.setIdResource( daoUtil.getInt( ++nPos ) );
            testResource.setTitle( daoUtil.getString( ++nPos ) );
        }

        daoUtil.free(  );

        return testResource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdTestResource, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );

        daoUtil.setInt( 1, nIdTestResource );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TestResource> selectTestResource( Plugin plugin )
    {
        List<TestResource> listTestResource = new ArrayList<TestResource>(  );
        TestResource testResource = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );

        daoUtil.executeQuery(  );

        int nPos;

        while ( daoUtil.next(  ) )
        {
            nPos = 0;
            testResource = new TestResource(  );
            testResource.setIdResource( daoUtil.getInt( ++nPos ) );
            testResource.setTitle( daoUtil.getString( ++nPos ) );
            listTestResource.add( testResource );
        }

        daoUtil.free(  );

        return listTestResource;
    }
}
