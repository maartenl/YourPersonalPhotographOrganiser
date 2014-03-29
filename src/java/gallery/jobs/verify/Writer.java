/*
 * Copyright (C) 2014 maartenl
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
package gallery.jobs.verify;

import gallery.beans.LogBean;
import gallery.database.entities.Log.LogLevel;
import gallery.database.entities.Photograph;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.batch.api.chunk.AbstractItemWriter;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author maartenl
 */
@Named("verifyPhotographWriter")
@ApplicationScoped
public class Writer extends AbstractItemWriter
{

    private static final Logger logger = Logger.getLogger(Writer.class.getName());

    @EJB
    private LogBean logBean;

    @Override
    public void writeItems(List<Object> items) throws Exception
    {
        logger.entering(this.getClass().getName(), "verifyPhotographWriter writeItems {0}", items);
        for (Object i : items)
        {
            Photograph photograph = (Photograph) i;
            logBean.createLog("verifyPhotograph", "Photograph " + photograph.getId() + ": File " + photograph.getFullPath() + " is okay.", null, LogLevel.INFO);
            logger.log(Level.FINEST, "Photograph {0}: File {1} is okay.", new Object[]
            {
                photograph.getId(), photograph.getFullPath()
            });
        }
        logger.exiting(this.getClass().getName(), "verifyPhotographWriter writeItems");
    }

}
