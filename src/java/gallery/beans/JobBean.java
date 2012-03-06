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
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Enterprise Java Bean that executes jobs monstly on gallery level.
 * @author maartenl
 */
@Stateless
@LocalBean
public class JobBean
{

    @PersistenceContext(unitName = "YourPersonalPhotographOrganiserPU")
    private EntityManager em;

    protected EntityManager getEntityManager()
    {
        return em;
    }

    /**
     * @param location
     * @param path
     * @return
     */
    private String processPhoto(Location location, Path path) throws NoSuchAlgorithmException, IOException, ImageProcessingException, MetadataException
    {
        // System.out.println("processPhoto " + location.getFilepath());
        if (path == null)
        {
            throw new NullPointerException();
        }
        // System.out.println(path.toString());
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
            // System.out.println(path.toString() + " already exists.");
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
            System.out.println(result);
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
            System.out.println("processPhoto cannot determine date/time! " + photo.getFilename() + " " + photo.getFilesize() + " " + photo.getHashstring() + " " + taken);

        } else
        {
            System.out.println("processPhoto " + photo.getFilename() + " " + photo.getFilesize() + " " + photo.getHashstring() + " " + photo.getTaken());
        }
        em.persist(photo);
        return null;
    }

    /**
     * Checks a directory stored in "location" for new photographs or films.
     * @param location the location that *may* have new photographs.
     * @return String containing an error message, or null upon success.
     * @throws IOException when a problem occurred with accessing the file, or
     * the file system
     * @throws NoSuchAlgorithmException if unable to create a hash using the algorithm.
     * @throws ImageProcessingException when unable to verify the image.
     */
    public String checkDirectory(Location location) throws IOException, NoSuchAlgorithmException, ImageProcessingException, MetadataException
    {
        System.out.println("checkDirectory start");
        String errorMessage = null;
        try
        {
            // JDK7: Path class and walking the filetree.
            Path startingDir = FileSystems.getDefault().getPath(location.getFilepath());
            PhotographVisitor pf = new PhotographVisitor();
            Files.walkFileTree(startingDir, pf);
            List<Path> result = pf.getFileList();

            int size = result.size();
            int i = 0;
            for (Path path : result)
            {
                errorMessage = processPhoto(location, path);
                if (errorMessage != null)
                {
                    break;
                }
                i++;
                if (i % 1000 == 999)
                {
                    em.flush();
                }
            }

        } catch (ConstraintViolationException e)
        {
            for (ConstraintViolation<?> violation : e.getConstraintViolations())
            {
                errorMessage = violation.toString();
            }
        }
        System.out.println("errorMessage " + errorMessage);
        System.out.println("checkDirectory end");
        return errorMessage;
    }

    /**
     * Imports photographs/films matching a certain mask (location) into an existing
     * gallery.
     * @param found the gallery to import photographs into
     * @param location the location that should match, can use SQL wildcards like %
     * @return a String, error message or null if successful.
     */
    public String importPhotographs(Gallery found, String location)
    {
        System.out.println("importPhotographs start");
        try
        {
            // get all photographs that match the mask
            Query query = em.createNamedQuery("Photograph.findByLocation");
            query.setParameter("mask", location);
            query.setParameter("gallery", found);
            List list = query.getResultList();
            if (list == null || list.isEmpty())
            {
                System.out.println("importPhotographs end");
                return "No photographs match " + location + ".";
            }
            int i = 0;
            for (Object r : list)
            {
                Photograph photo = (Photograph) r;
                System.out.println("importPhotographs " + photo);
                GalleryPhotograph gphoto = new GalleryPhotograph();
                gphoto.setGallery(found);
                gphoto.setName(photo.getFilename());
                gphoto.setPhotograph(photo);
                gphoto.setSortorder(BigInteger.valueOf(i++));
                em.persist(gphoto);
            }
            System.out.println("importPhotographs end");
            return null;
        } catch (ConstraintViolationException e)
        {
            for (ConstraintViolation<?> violation : e.getConstraintViolations())
            {
                System.out.println(violation);
                System.out.println("importPhotographs end");
                return violation.toString();
            }
        }
        System.out.println("importPhotographs end");
        return "We shouldn't even be here!";

    }
}
