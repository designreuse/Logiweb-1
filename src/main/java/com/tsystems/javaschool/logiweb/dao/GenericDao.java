package com.tsystems.javaschool.logiweb.dao;

import java.util.Set;

/**
 * Generic DAO interface for basic CRUD operations.
 * 
 * @author Andrey Baliushin
 *
 * @param <T>
 *            persistent object
 */
public interface GenericDao<T> {

    /**
     * Persist new object into database.
     * 
     * @param newInstance
     *            persistent object
     * @return same persistance object
     */
    T create(T newInstance);

    /**
     * Get persistent object by its primary key.
     * 
     * @param id
     *            primary key of object
     * @return persistent object or null if not found
     */
    T read(Integer id);

    /**
     * Update persistent object.
     * 
     * @param changedObject
     *            persistent object
     */
    void update(T changedObject);

    /**
     * Remove persistent object from database.
     * 
     * @param objectToDelete
     *            persistent object
     */
    void delete(T objectToDelete);
    
    /**
     * Find all objects of that persistent class.
     * 
     * @return set of objects or null
     */
    Set<T> findAll();
}