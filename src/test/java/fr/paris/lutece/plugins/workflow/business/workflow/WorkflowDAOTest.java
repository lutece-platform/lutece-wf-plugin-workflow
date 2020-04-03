package fr.paris.lutece.plugins.workflow.business.workflow;

import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.test.LuteceTestCase;

public class WorkflowDAOTest extends LuteceTestCase
{

    private WorkflowDAO _dao = new WorkflowDAO( );

    public void testCRUD( )
    {
        Workflow wf1 = new Workflow( );
        wf1.setName( "wf1" );
        wf1.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
        wf1.setDescription( "strDescription" );
        wf1.setEnabled( true );
        wf1.setWorkgroup( "group" );

        _dao.insert( wf1 );
        Workflow wf2 = _dao.load( wf1.getId( ) );
        assertEquals( wf1.getName( ), wf2.getName( ) );

        wf1.setName( "wf11" );
        _dao.store( wf1 );
        wf2 = _dao.load( wf1.getId( ) );
        assertEquals( wf1.getName( ), wf2.getName( ) );
        
        _dao.delete( wf1.getId( ) );
        wf2 = _dao.load( wf1.getId( ) );
        assertNull( wf2 );
    }
}
