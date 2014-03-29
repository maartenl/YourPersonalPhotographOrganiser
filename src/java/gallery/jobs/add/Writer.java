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
package gallery.jobs.add;

import gallery.beans.LogBean;
import gallery.beans.PhotographBean;
import gallery.database.entities.Log;
import gallery.database.entities.Photograph;
import java.io.Serializable;
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
@Named("addPhotographWriter")
@ApplicationScoped
public class Writer extends AbstractItemWriter
{

    private static final Logger logger = Logger.getLogger(Processor.class.getName());

    @EJB
    private PhotographBean photographBean;

    @EJB
    private LogBean logBean;

    @Override
    public void open(Serializable checkpoint) throws Exception
    {
        if (checkpoint == null)
        {
            logger.finest("addPhotographWriter open start");
        } else
        {
            logger.finest("addPhotographWriter open restart");
        }
    }

    @Override
    public void close() throws Exception
    {
        logger.finest("addPhotographWriter close");
    }

    @Override
    public void writeItems(List<Object> items) throws Exception
    {
        logger.entering(this.getClass().getName(), "writeItems " + items);
        for (Object i : items)
        {
            logger.log(Level.FINEST, "addPhotographWriter writeItem {0}", i);
            Photograph photograph = (Photograph) i;
            photographBean.create(photograph);
            logBean.createLog("verifyPhotograph", "Photograph " + photograph.getId() + " with " + photograph.getFullPath() + " created.", null, Log.LogLevel.INFO);
        }
        logger.exiting(this.getClass().getName(), "writeItems");
    }

}
