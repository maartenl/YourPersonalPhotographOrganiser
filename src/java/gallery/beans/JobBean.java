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

import gallery.database.entities.Gallery;
import gallery.database.entities.GalleryPhotograph;
import gallery.database.entities.Location;
import gallery.database.entities.Photograph;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Enterprise Java Bean that starts jobs mostly on location level.
 * <p>
 * <img src="../../images/JobBean.png"/></p>
 * @startuml
 * JobBean -> BatchRuntime: getJobOperator()
 * JobBean <- BatchRuntime: operator
 * create Properties
 * JobBean -> Properties: new
 * JobBean -> Properties: setProperty()
 * JobBean -> JobOperator: start
 * @enduml
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

    @EJB
    private GalleryBean galleryBean;

    /**
     * Checks a directory stored in "location" for new photographs or films.
     * Will start walking the directory tree.
     *
     * @param location the location that *may* have new photographs.
     */
    public void addPhotographs(Location location)
    {
        logger.entering(this.getClass().getName(), "addPhotographs");

        JobOperator operator = BatchRuntime.getJobOperator();
        Properties jobParameters = new Properties();
        jobParameters.setProperty("location", location.getId() + "");
        operator.start("AddPhotographs", jobParameters);

        logger.exiting(this.getClass().getName(), "addPhotographs");
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
     * @param location the location at which the photographs need to be checked
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

    public void initGalleries(Location location)
    {
        logger.entering(this.getClass().getName(), "initGalleries");
        // get all photographs that match the mask
        Query query = em.createNamedQuery("Photograph.getPaths");
        query.setParameter("location", location);
        List<String> list = query.getResultList();
        if (list == null || list.isEmpty())
        {
            logger.log(Level.WARNING, "No photographs match {0}.", location);
            logger.exiting(this.getClass().getName(), "initGalleries");
            return;
        }
        List<Gallery> galleries = new ArrayList<>();
        int i = 0;
        for (String path : list)
        {
            logger.log(Level.INFO, "initGalleries path={0}.", path);
            Gallery gallery = new Gallery();
            gallery.setDescription(path);
            gallery.setName(path);
            gallery.setSortorder(i++);
            galleries.add(gallery);
        }
        query = em.createNamedQuery("Photograph.getPhotographsByLocation");
        query.setParameter("location", location);
        List<Photograph> photographs = query.getResultList();
        for (Photograph photograph : photographs)
        {
            logger.log(Level.INFO, "initGalleries photograph={0}.", photograph.getId());
            Gallery found = null;
            for (Gallery gallery : galleries)
            {
                if (gallery.getName().equals(photograph.getRelativepath()))
                {
                    found = gallery;
                    break;
                }
            }
            GalleryPhotograph gphoto = new GalleryPhotograph();
            gphoto.setGallery(found);
            gphoto.setName(photograph.getFilename());
            gphoto.setPhotograph(photograph);
            found.addGalleryPhotograph(gphoto);
        }
        for (Gallery gallery : galleries)
        {
            logger.log(Level.INFO, "initGalleries persist gallery {0}.", gallery);
            galleryBean.create(gallery);//em.persist(gallery);
        }
        logger.exiting(this.getClass().getName(), "initGalleries");
    }

}
