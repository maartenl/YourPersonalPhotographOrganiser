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

import gallery.beans.LocationBean;
import gallery.database.entities.Location;
import gallery.servlets.PhotographVisitor;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author maartenl
 */
@Named("addPhotographReader")
@ApplicationScoped
public class Reader extends AbstractItemReader
{

    private static final Logger logger = Logger.getLogger(Reader.class.getName());

    private int index = 0;

    @EJB
    private LocationBean locationBean;

    @Inject
    private JobContext jobContext;

    /**
     * The files that need to be checked.
     */
    private List<Path> files;

    private Location getLocation() throws JobSecurityException, NumberFormatException, RuntimeException, NoSuchJobExecutionException
    {
        Properties jobParameters = BatchRuntime.getJobOperator().getParameters(jobContext.getExecutionId());
        Long locationId = Long.parseLong((String) jobParameters.get("location"));
        if (locationId == null)
        {
            throw new RuntimeException("Location id not provided..");
        }
        logger.log(Level.FINEST, "location id={0}", locationId);
        Location location = locationBean.find(locationId);
        if (location == null)
        {
            throw new RuntimeException("Location with id " + locationId + " not found.");
        }
        logger.log(Level.FINEST, "location={0}", location.getFilepath());
        return location;
    }

    @Override
    public void open(Serializable checkpoint) throws Exception
    {
        logger.entering(this.getClass().getName(), "open");
        if (checkpoint == null)
        {
            logger.finest("addPhotographReader open start");
        } else
        {
            logger.finest("addPhotographReader open restart");
        }
        Location location = getLocation();

        // JDK7: Path class and walking the filetree.
        Path startingDir = FileSystems.getDefault().getPath(location.getFilepath());
        PhotographVisitor pf = new PhotographVisitor();
        Files.walkFileTree(startingDir, pf);
        files = pf.getFileList();

        logger.exiting(this.getClass().getName(), "open");
    }

    @Override
    public Object readItem() throws Exception
    {
        logger.finest("addPhotographReader readItem");
        if (files == null || files.isEmpty())
        {
            throw new RuntimeException("No files found.");
        }
        if (index >= files.size())
        {
            return null;
        }
        if (index < 0)
        {
            throw new RuntimeException("Negative index.");
        }
        return files.get(index++);
    }

}
