package fr.paris.lutece.plugins.workflow.business.state;

import java.util.List;

import fr.paris.lutece.plugins.workflow.business.icon.IconDAO;
import fr.paris.lutece.plugins.workflow.business.workflow.WorkflowDAO;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.test.LuteceTestCase;

public class StateDaoTest extends LuteceTestCase
{
    private StateDAO _dao = new StateDAO( );
    
    private WorkflowDAO workflowDAO = new WorkflowDAO( );
    private IconDAO iconDAO = new IconDAO( );
    private Workflow wf;
    private Icon icon;
    
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        
        icon = new Icon( );
        icon.setName( "icon" );
        iconDAO.insert( icon );
        
        wf = new Workflow( );
        wf.setName( "wf1" );
        wf.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
        wf.setDescription( "strDescription" );
        wf.setEnabled( true );
        wf.setWorkgroup( "group" );
        workflowDAO.insert( wf );
    }
    
    @Override
    protected void tearDown( ) throws Exception
    {
        iconDAO.delete( icon.getId( ) );
        workflowDAO.delete( wf.getId( ) );
        
        super.tearDown( );
    }
    
    public void testCRUD( )
    {
        State state = new State( );
        state.setName( "strName" );
        state.setDescription( "strDescription" );
        state.setIcon( icon );
        state.setInitialState( false );
        state.setRequiredWorkgroupAssigned( false );
        state.setWorkflow( wf );
        state.setOrder( 3 );
        
        _dao.insert( state );
        
        State load = _dao.load( state.getId( ) );
        assertEquals( state.getName( ), load.getName( ) );
        
        state.setName( "new name" );
        _dao.store( state );
        load = _dao.load( state.getId( ) );
        assertEquals( state.getName( ), load.getName( ) );
        
        _dao.delete( state.getId( ) );
        load = _dao.load( state.getId( ) );
        assertNull( load );
    }
    
    public void testSelectStatesByFilter( )
    {
        State state = new State( );
        state.setName( "strName" );
        state.setDescription( "strDescription" );
        state.setIcon( icon );
        state.setInitialState( false );
        state.setRequiredWorkgroupAssigned( false );
        state.setWorkflow( wf );
        state.setOrder( 3 );
        
        _dao.insert( state );
        
        StateFilter filter = new StateFilter( );
        filter.setIdWorkflow( wf.getId( ) );
        filter.setIsInitialState( 0 );
        
        List<State> list = _dao.selectStatesByFilter( filter );
        assertEquals( 1, list.size( ) );
        
        _dao.delete( state.getId( ) );
    }
    
    public void testFindStatesBetweenOrders( )
    {
        State state = new State( );
        state.setName( "strName" );
        state.setDescription( "strDescription" );
        state.setIcon( icon );
        state.setInitialState( false );
        state.setRequiredWorkgroupAssigned( false );
        state.setWorkflow( wf );
        state.setOrder( 3 );
        
        _dao.insert( state );
        
        StateFilter filter = new StateFilter( );
        filter.setIdWorkflow( wf.getId( ) );
        filter.setIsInitialState( 0 );
        
        List<State> list = _dao.findStatesBetweenOrders( 2, 4, wf.getId( ) );
        assertEquals( 1, list.size( ) );
        
        _dao.delete( state.getId( ) );
    }
    
    public void testFindStatesAfterOrder( )
    {
        State state = new State( );
        state.setName( "strName" );
        state.setDescription( "strDescription" );
        state.setIcon( icon );
        state.setInitialState( false );
        state.setRequiredWorkgroupAssigned( false );
        state.setWorkflow( wf );
        state.setOrder( 3 );
        
        _dao.insert( state );
        
        StateFilter filter = new StateFilter( );
        filter.setIdWorkflow( wf.getId( ) );
        filter.setIsInitialState( 0 );
        
        List<State> list = _dao.findStatesAfterOrder( 2, wf.getId( ) );
        assertEquals( 1, list.size( ) );
        
        _dao.delete( state.getId( ) );
    }
}
