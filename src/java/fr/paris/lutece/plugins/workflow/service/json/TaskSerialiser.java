package fr.paris.lutece.plugins.workflow.service.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import fr.paris.lutece.plugins.workflowcore.service.task.ITask;

public class TaskSerialiser  extends JsonSerializer<ITask>
{

    @Override
    public void serialize( ITask value, JsonGenerator gen, SerializerProvider serializers ) throws IOException
    {
        String taskType = value.getTaskType( ).getKey( );
        int action = value.getAction( ).getId( );
        
        gen.writeStartObject( );
        gen.writeNumberField( "order", value.getOrder( ) );
        gen.writeNumberField( "action", action );
        gen.writeNumberField( "id", value.getId( ) );
        gen.writeStringField( "taskType", taskType );
        gen.writeEndObject( );
    }

}

