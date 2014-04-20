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

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import gallery.beans.LocationBean;
import gallery.beans.LogBean;
import gallery.database.entities.Location;
import gallery.database.entities.Log.LogLevel;
import gallery.database.entities.Photograph;
import gallery.enums.ImageAngle;
import gallery.images.ImageOperations;
import gallery.servlets.FileOperations;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * <p>
 * Processes a new photograph, by retrieving its data and adding
 * it to the database.</p><p>
 * <img src="../../../images/Processor-add.png"/></p>
 * @startuml Processor-add.png
 * (*) --> "Initialisation"
 * if "check Photograph" then
 * -->[not exists] "computeHash"
 * if "check Hash" then
 * -left-> [hash exists] "log: another\nPhotograph already\nhas that hash"
 * if "is other \nPhotograph\n good?" then
 * -down-> [yes] (*)
 * else
 * partition "Move Photograph"
 * --> [no] "log: Photograph has moved"
 * ---> "Update old photograph\nto new location"
 * -down-> (*)
 * end partition
 * endif
 * else
 * partition "Add Photograph"
 * -->[hash not exists] "getDateTime"
 * --> getAngle
 * -->store Photo
 * --> (*)
 * end partition
 * endif
 * else
 * ->[already exists] "log: Photograph\nalready in\ndatabase"
 * --> (*)
 * endif
 * @enduml * @author maartenl
 * */
@Named("addPhotographProcessor")
@ApplicationScoped
public class Processor implements ItemProcessor
{

    private static final Logger logger = Logger.getLogger(Processor.class.getName());

    @EJB
    private LocationBean locationBean;

    @EJB
    private LogBean logBean;

    @Inject
    private JobContext jobContext;

    @PersistenceContext(unitName = "YourPersonalPhotographOrganiserPU")
    private EntityManager em;

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
    public Object processItem(Object item) throws Exception
    {
        logger.entering(this.getClass().getName(), "addPhotographProcessor processItem " + item);
        // return null; > do not process item
        if (item == null)
        {
            return null;
        }
        Path path = (Path) item;
        Location location = getLocation();
        try
        {
            final Photograph photograph = processPhoto(location, path);
            return photograph;

        } catch (ImageProcessingException | MetadataException | IOException | NoSuchAlgorithmException e)
        {
            StringWriter stackTrace = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stackTrace);
            e.printStackTrace(printWriter);
            logBean.createLog("addPhotograph", "Unable to add photograph "
                    + item
                    + " to location "
                    + location.getId()
                    + ", exception " + e.getClass().getName() + " caught.",
                    stackTrace.toString(), LogLevel.ERROR);
        }
        return null;
    }

    /**
     * @param location
     * @param path
     * @throws NoSuchAlgorithmException if unable to create a hash using the
     * algorithm.
     * @throws ImageProcessingException when unable to verify the image.
     * @throws com.drew.metadata.MetadataException
     * @return
     */
    private Photograph processPhoto(Location location, Path path) throws NoSuchAlgorithmException, IOException, ImageProcessingException, MetadataException
    {
        logger.entering(this.getClass().getName(), "processPhoto");
        if (path == null)
        {
            throw new NullPointerException();
        }
        logger.log(Level.FINE, "processPhoto {0} {1}", new Object[]
        {
            location.getFilepath(), path.toString()
        });
        Path filename = path.getFileName();
        Path locationPath = FileSystems.getDefault().getPath(location.getFilepath());
        Path relativePath = locationPath.relativize(path).getParent();

        // check if photo already exists in database
        Query query = em.createNamedQuery("Photograph.findByFilename");
        query.setParameter("filename", filename.toString());
        query.setParameter("relativepath", relativePath.toString());
        @SuppressWarnings("unchecked")
        List<Photograph> listByFilename = query.getResultList();
        if (listByFilename != null && !listByFilename.isEmpty())
        {
            logger.log(Level.FINE, "{0} already exists.", path.toString());
            logBean.createLog("addPhotograph", "Photograph " + path + " already exists.", null, LogLevel.INFO);
            return null;
        }
        // check if hash and filesize already exist in database
        File file = path.toFile();
        String computeHash = FileOperations.computeHash(file);
        Long size = file.length();
        query = em.createNamedQuery("Photograph.findByStats");
        query.setParameter("hashstring", computeHash);
        query.setParameter("filesize", size);
        @SuppressWarnings("unchecked")
        List<Photograph> listByStats = query.getResultList();
        if (listByStats != null && !listByStats.isEmpty())
        {
            Photograph alreadyPhoto = (Photograph) listByStats.get(0);
            String result = "File with filename " + relativePath.toString() + ":" + filename.toString() + " with hash " + computeHash + " already exists with id " + alreadyPhoto.getId() + ".";
            logger.fine(result);
            logBean.createLog("addPhotograph", result, null, LogLevel.WARNING);
            return null;
        }
        // JDK7: lots of nio.Path calls
        Date taken = null;
        ImageAngle angle = null;

        if (ImageOperations.isImage(path))
        {
            taken = ImageOperations.getDateTimeTaken(file);
            angle = ImageOperations.getAngle(file);
        }
        Photograph photo = new Photograph();
        photo.setFilesize(size);
        photo.setHashstring(computeHash);
        photo.setLocation(location);
        photo.setTaken(taken);
        photo.setFilename(filename.toString());
        photo.setAngle(angle);
        photo.setRelativepath(relativePath.toString());
        if (taken != null && taken.before(new Date(0l)))
        {
            photo.setTaken(null);
            if (logger.isLoggable(Level.FINE))
            {
                logger.log(Level.FINE, "processPhoto cannot determine date/time! {0} {1} {2} {3}", new Object[]
                {
                    photo.getFilename(), photo.getFilesize(), photo.getHashstring(), taken
                });
            }
            logBean.createLog("addPhotograph", "Cannot determine date/time of photograph " + path, null, LogLevel.WARNING);

        } else
        {
            if (logger.isLoggable(Level.FINE))
            {
                logger.log(Level.FINE, "processPhoto {0} {1} {2} {3}", new Object[]
                {
                    photo.getFilename(), photo.getFilesize(), photo.getHashstring(), photo.getTaken()
                });
            }
        }
        return photo;
    }
}
