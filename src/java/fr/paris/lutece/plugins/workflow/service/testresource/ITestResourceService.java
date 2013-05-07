/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.service.testresource;

import fr.paris.lutece.plugins.workflow.business.testresource.TestResource;
import fr.paris.lutece.portal.service.plugin.Plugin;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 *
 * ITestResourceService
 *
 */
public interface ITestResourceService
{
    /**
    * Creation of an instance of state
    * @param testResource The instance of TestResource which contains the informations to store
    * @param plugin the plugin
    */
    @Transactional( "workflow.transactionManager" )
    void create( TestResource testResource, Plugin plugin );

    /**
     * Remove testResource which is specified in parameter
     * @param nIdTestResource   The id of testResiurce to remove
     * @param plugin the Plugin
     */
    @Transactional( "workflow.transactionManager" )
    void remove( int nIdTestResource, Plugin plugin );

    /**
    * Load the  TestResource Object
    * @param nIdTestResource the test resource id
    * @param plugin the plugin
    * @return TestResource Object
    */
    TestResource findByPrimaryKey( int nIdTestResource, Plugin plugin );

    /**
     * Select TestResource
     * @param plugin the plugin
     * @return TestResource List
     */
    List<TestResource> getList( Plugin plugin );
}
