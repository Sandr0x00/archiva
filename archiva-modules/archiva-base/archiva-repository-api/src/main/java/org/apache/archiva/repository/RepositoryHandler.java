package org.apache.archiva.repository;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.archiva.configuration.Configuration;
import org.apache.archiva.repository.validation.RepositoryChecker;
import org.apache.archiva.repository.validation.RepositoryValidator;

import java.util.Collection;
import java.util.Map;

/**
 *
 * This is the generic interface that handles different repository flavours.
 *
 * @author Martin Stockhammer <martin_s@apache.org>
 */
public interface RepositoryHandler<R extends Repository, C>
{

    /**
     * Creates instances from the archiva configuration. The instances are not registered in the registry.
     *
     * @return A map of (repository id, Repository) pairs
     */
    Map<String, R> newInstancesFromConfig();

    /**
     * Creates a new instance without registering and without updating the archiva configuration
     *
     * @param type the repository type
     * @param id the repository identifier
     * @return the repository instance
     * @throws RepositoryException if the creation failed
     */
    R newInstance(RepositoryType type, String id) throws RepositoryException;

    /**
     * Creates a new instance and updates the given configuration object.
     *
     * @param repositoryConfiguration the configuration instance
     * @return a newly created instance
     * @throws RepositoryException if the creation failed
     */
    R newInstance( C repositoryConfiguration ) throws RepositoryException;

    /**
     * Adds the given repository to the registry or replaces a already existing repository in the registry.
     * If an error occurred during the update, it will revert to the old repository status.
     *
     * @param repository the repository
     * @return the created or updated repository instance
     * @throws RepositoryException if the update or creation failed
     */
    R put( R repository ) throws RepositoryException;

    /**
     * Adds the repository to the registry, based on the given configuration.
     * If there is a repository registered with the given id, it is updated.
     * The archiva configuration is updated. The status is not defined, if an error occurs during update. The
     * The repository instance is registered and initialized if no error occurs
     *
     * @param repositoryConfiguration the repository configuration
     * @return the updated or created repository instance
     * @throws RepositoryException if the update or creation failed
     */
    R put( C repositoryConfiguration ) throws RepositoryException;

    /**
     * Adds a repository from the given repository configuration. The changes are stored in
     * the configuration object. The archiva registry is not updated.
     * The returned repository instance is a clone of the registered repository instance. It is not registered
     * and not initialized. References are not updated.
     *
     * @param repositoryConfiguration the repository configuration
     * @param configuration the configuration instance
     * @return the repository instance that was created or updated
     * @throws RepositoryException if the update or creation failed
     */
    R put( C repositoryConfiguration, Configuration configuration ) throws RepositoryException;

    /**
     * Adds or updates a repository from the given configuration data. The resulting repository is
     * checked by the repository checker and the result is returned.
     * If the checker returns a valid result, the registry is updated and configuration is saved.
     *
     * @param repositoryConfiguration the repository configuration
     * @param checker the checker that validates the repository data
     * @return the repository and the check result
     * @throws RepositoryException if the creation or update failed
     */
    <D> CheckedResult<R, D>
    putWithCheck( C repositoryConfiguration, RepositoryChecker<R, D> checker) throws RepositoryException;

    /**
     * Removes the given repository from the registry and updates references and saves the new configuration.
     *
     * @param id The repository identifier
     * @throws RepositoryException if the repository could not be removed
     */
    void remove( final String id ) throws RepositoryException;

    /**
     * Removes the given repository from the registry and updates only the given configuration instance.
     * The archiva registry is not updated
     *
     * @param id the repository identifier
     * @param configuration the configuration to update
     * @throws RepositoryException if the repository could not be removed
     */
    void remove( String id, Configuration configuration ) throws RepositoryException;

    /**
     * Returns the repository with the given identifier or <code>null</code>, if it is not registered.
     *
     * @param id the repository id
     * @return if the retrieval failed
     */
    R get( String id );

    /**
     * Clones a given repository without registering.
     *
     * @param repo the repository that should be cloned
     * @return a newly created instance with the same repository data
     */
    R clone(R repo) throws RepositoryException;

    /**
     * Updates the references and stores updates in the given <code>configuration</code> instance.
     * The references that are updated depend on the concrete repository subclass <code>R</code>.
     * This method may register/unregister repositories depending on the implementation. That means there is no simple
     * way to roll back, if an error occurs.
     *
     * @param repo the repository for which references are updated
     * @param repositoryConfiguration the repository configuration
     */
    void updateReferences( R repo, C repositoryConfiguration ) throws RepositoryException;

    /**
     * Returns all registered repositories.
     *
     * @return the list of repositories
     */
    Collection<R> getAll();

    /**
     * Returns a validator that can be used to validate repository data
     * @return a validator instance
     */
    RepositoryValidator<R> getValidator( );

    /**
     * Returns <code>true</code>, if the repository is registered with the given id, otherwise <code>false</code>
     * @param id the repository identifier
     * @return <code>true</code>, if it is registered, otherwise <code>false</code>
     */
    boolean has(String id);

    /**
     * Initializes
     */
    void init();

    /**
     * Closes the handler
     */
    void close();

}
