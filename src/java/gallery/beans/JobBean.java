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

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import gallery.database.entities.Gallery;
import gallery.database.entities.GalleryPhotograph;
import gallery.database.entities.Location;
import gallery.database.entities.Photograph;
import gallery.enums.ImageAngle;
import gallery.images.ImageOperations;
import gallery.servlets.FileOperations;
import gallery.servlets.PhotographVisitor;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Enterprise Java Bean that executes jobs mostly on gallery level. The
 * ("batch") job is (in most cases) not actually a batch job but produces a
 * series of JMS messages. In other words, this stateless session bean is a
 * Producer of JMS Messages.
 *
 *
 * @author maartenl
 */
@Stateless
@LocalBean
public class JobBean
{

    private static final Logger logger = Logger.getLogger(JobBean.class.getName());

    @PersistenceContext(unitName = "YourPersonalPhotographOrganiserPU")
    private EntityManager em;

    protected EntityManager getEntityManager()
    {
        return em;
    }

    /**
     * Checks a directory stored in "location" for new photographs or films.
     * Will start walking the directory tree.
     *
     * @param location the location that *may* have new photographs.
     * @throws IOException when a problem occurred with accessing the file, or
     * the file system
     */
    public void checkDirectory(Location location) throws IOException
    {
        logger.entering(this.getClass().getName(), "checkDirectory");

        JobOperator operator = BatchRuntime.getJobOperator();
        Properties jobParameters = new Properties();
        jobParameters.setProperty("location", location.getId() + "");
        operator.start("ImportPhotographs", jobParameters);

        String errorMessage = null;
        try
        {
            // JDK7: Path class and walking the filetree.
            Path startingDir = FileSystems.getDefault().getPath(location.getFilepath());
            PhotographVisitor pf = new PhotographVisitor();
            Files.walkFileTree(startingDir, pf);
            List<Path> result = pf.getFileList();

            int i = 0;
            for (Path path : result)
            {                //do stuff with path
            }

        } catch (ConstraintViolationException e)
        {
            for (ConstraintViolation<?> violation : e.getConstraintViolations())
            {
                errorMessage = violation.toString();
            }
        }
        logger.log(Level.FINE, "errorMessage {0}", errorMessage);
        logger.exiting(this.getClass().getName(), "checkDirectory=");
    }

    /**
     * Imports photographs/films matching a certain mask (location) into an
     * existing gallery.
     *
     * @param found the gallery to import photographs into
     * @param location the location that should match, can use SQL wildcards
     * like %
     * @return a String, error message or null if successful.
     */
    public String importPhotographs(Gallery found, String location)
    {
        logger.entering(this.getClass().getName(), "importPhotographs");
        try
        {
            // get all photographs that match the mask
            Query query = em.createNamedQuery("Photograph.findByLocation");
            query.setParameter("mask", location);
            query.setParameter("gallery", found);
            List list = query.getResultList();
            if (list == null || list.isEmpty())
            {
                logger.exiting(this.getClass().getName(), "importPhotographs");
                return "No photographs match " + location + ".";
            }
            int i = 0;
            for (Object r : list)
            {
                Photograph photo = (Photograph) r;
                logger.log(Level.FINE, "importPhotographs {0}", photo);
                GalleryPhotograph gphoto = new GalleryPhotograph();
                gphoto.setGallery(found);
                gphoto.setName(photo.getFilename());
                gphoto.setPhotograph(photo);
                gphoto.setSortorder(BigInteger.valueOf(i++));
                em.persist(gphoto);
            }
            logger.exiting(this.getClass().getName(), "importPhotographs");
            return null;
        } catch (ConstraintViolationException e)
        {
            for (ConstraintViolation<?> violation : e.getConstraintViolations())
            {
                logger.fine(violation.toString());
                logger.exiting(this.getClass().getName(), "importPhotographs");
                return violation.toString();
            }
        }
        logger.exiting(this.getClass().getName(), "importPhotographs");
        return "We shouldn't even be here!";

    }

    /**
     * Verifies existing Photographs upon a Location. Do they exist on the hard
     * drive as well, do the hashes correspond? Will put error messages in the
     * log, for each Photograph so they can be fixed later by the user.
     *
     * @param location the location of which the photographs need to be checked
     */
    public void verifyPhotographs(Location location)
    {
        logger.entering(this.getClass().getName(), "verifyPhotographs");
        JobOperator operator = BatchRuntime.getJobOperator();
        Properties jobParameters = new Properties();
        jobParameters.setProperty("location", location.getId() + "");
        operator.start("VerifyPhotographs", jobParameters); // maps to VerifyPhotographs.xml
        logger.exiting(this.getClass().getName(), "verifyPhotographs");
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
    private String processPhoto(Location location, Path path) throws NoSuchAlgorithmException, IOException, ImageProcessingException, MetadataException
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
        List list = query.getResultList();
        if (list != null && !list.isEmpty())
        {
            logger.log(Level.FINE, "{0} already exists.", path.toString());
            return null;
        }
        // check if hash and filesize already exist in database
        File file = path.toFile();
        String computeHash = FileOperations.computeHash(file);
        Long size = file.length();
        query = em.createNamedQuery("Photograph.findByStats");
        query.setParameter("hashstring", computeHash);
        query.setParameter("filesize", size);
        list = query.getResultList();
        if (list != null && !list.isEmpty())
        {
            Photograph alreadyPhoto = (Photograph) list.get(0);
            String result = "File with filename " + relativePath.toString() + ":" + filename.toString() + " with hash " + computeHash + " already exists with id " + alreadyPhoto.getId() + ".";
            logger.fine(result);
            return result;
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
        em.persist(photo);
        return null;
    }
}
