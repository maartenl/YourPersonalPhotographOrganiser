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

import gallery.database.entities.Log;
import gallery.database.entities.Log.LogLevel;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

/**
 * Log Enterprise Java Bean, maps to a Log Hibernate Entity. Not exposed
 * as a REST resource.
 *
 * @author maartenl
 */
@Stateless
public class LogBean extends AbstractBean<Log>
{

    private static final Logger logger = Logger.getLogger(LogBean.class.getName());

    @PersistenceContext(unitName = "YourPersonalPhotographOrganiserPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }

    public LogBean()
    {
        super(Log.class);
    }

    public void createLog(String source, String message, String description, LogLevel logLevel)
    {
        Log log = new Log(source, message, description, logLevel);
        create(log);
    }

    public List findRange(int[] range, LogLevel logLevel)
    {
        logger.entering(this.getClass().getName(), "findRange range={0}, logLevel={1}", new Object[]
        {
            range, logLevel
        });
        final CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        final Root<Log> root = cq.from(Log.class);
        if (logLevel == null)
        {
            cq.select(root);

        } else
        {
            ParameterExpression<LogLevel> p = cb.parameter(LogLevel.class);
            cq.select(root).where(cb.equal(root.get("logLevel"), logLevel));
        }
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count(LogLevel logLevel)
    {
        logger.entering(this.getClass().getName(), "count logLevel={0}", logLevel);
        final CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery();
        final Root<Log> root = cq.from(Log.class);
        if (logLevel == null)
        {
            cq.select(cb.count(root));

        } else
        {
            ParameterExpression<LogLevel> p = cb.parameter(LogLevel.class);
            cq.select(cb.count(root)).where(cb.equal(root.get("logLevel"), logLevel));
        }
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public int deleteAll()
    {
        Query query = em.createNamedQuery("Log.deleteAll");
        return query.executeUpdate();
    }
}
