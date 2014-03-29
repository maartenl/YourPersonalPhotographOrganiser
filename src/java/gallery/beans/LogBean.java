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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Log Enterprise Java Bean, maps to a Log Hibernate Entity. Not exposed
 * as a REST resource.
 *
 * @author maartenl
 */
@Stateless
public class LogBean extends AbstractBean<Log>
{

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
}
