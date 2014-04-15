/*
 *  Copyright (C) 2012 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gallery.beans;

import java.util.List;
import javax.persistence.EntityManager;

/**
 * Abstract class that provides default implementations for retrieving
 * entities.
 *
 * @author maartenl
 * @param <T> The Entity to work on
 */
public abstract class AbstractBean<T>
{

    private final Class<T> entityClass;

    /**
     * Constructor
     *
     * @param entityClass the class of the entity on which this class
     * is to perform its operations.
     */
    public AbstractBean(Class<T> entityClass)
    {
        this.entityClass = entityClass;
    }

    /**
     * Returns the Entity manager.
     *
     * @return EntityManager
     */
    protected abstract EntityManager getEntityManager();

    /**
     * Persist an entity to the database.
     *
     * @param entity
     */
    public void create(T entity)
    {
        getEntityManager().persist(entity);
    }

    /**
     * Updates an entity.
     *
     * @param entity
     */
    public void edit(T entity)
    {
        getEntityManager().merge(entity);
    }

    /**
     * Deletes an entity.
     *
     * @param entity
     */
    public void remove(T entity)
    {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    /**
     * Retrieves an identity.
     *
     * @param id the primary key/unique identifier of the entity
     * @return the entity found, or null if not found.
     */
    public T find(Long id)
    {
        return getEntityManager().find(entityClass, id);
    }

    /**
     * Retrieve <i>all</i> instances of the entity.
     *
     * @return List of entities
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll()
    {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     * TODO: an array? Seems weird.
     * Retrieve a range of entities, for paging possibilities.
     *
     * @param range integer array, containing row numbers, size of two, first is the first row
     * and last is the last row (exclusive).
     * @return List of entities
     */
    @SuppressWarnings("unchecked")
    public List<T> findRange(int[] range)
    {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    /**
     * Returns the number of entities available.
     *
     * @return integer indicating how many entities there are.
     */
    @SuppressWarnings("unchecked")
    public int count()
    {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
