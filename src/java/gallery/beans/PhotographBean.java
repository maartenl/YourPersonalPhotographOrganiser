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
import gallery.database.entities.Photograph;
import gallery.database.entities.Tag;
import gallery.enums.ImageAngle;
import gallery.images.ImageOperations;
import gallery.images.PhotoMetadata;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

/**
 * Photograph Enterprise Java Bean, maps to a Photograph Hibernate Entity.
 * Also a REST service mapping to /YourPersonalPhotographOrganiser/resources/photographs.
 *
 * @author maartenl
 */
@Stateless
@Path("/photographs")
public class PhotographBean extends AbstractBean<Photograph>
{

    private static final Logger logger = Logger.getLogger(PhotographBean.class.getName());

    @PersistenceContext(unitName = "YourPersonalPhotographOrganiserPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }

    public PhotographBean()
    {
        super(Photograph.class);
    }

    /**
     * Creates an Photograph. POST to /YourPersonalPhotographOrganiser/resources/photographs.
     * Will accept both application/xml as well as application/json.
     *
     * @param entity Photograph
     */
    @POST
    @Override
    @Consumes(
            {
                "application/xml", "application/json"
            })
    public void create(Photograph entity)
    {
        super.create(entity);
    }

    /**
     * Updates a Photograph. PUT to /YourPersonalPhotographOrganiser/resources/photographs.
     * Will accept both application/xml as well as application/json.
     *
     * @param entity Photograph
     */
    @PUT
    @Override
    @Consumes(
            {
                "application/xml", "application/json"
            })
    public void edit(Photograph entity)
    {
        super.edit(entity);
    }

    /**
     * Removes a Photograph. DELETE to /YourPersonalPhotographOrganiser/resources/photographs/{id}.
     *
     * @param id unique identifier for the Photograph, present in the url.
     */
    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id)
    {
        super.remove(find(id));
    }

    /**
     * Retrieves a Photograph. GET to /YourPersonalPhotographOrganiser/resources/photographs/{id}.
     * Can produce both application/xml as well as application/json when asked.
     *
     * @param id unique identifier for the comment, present in the url.
     * @return Photograph entity
     * @throws WebApplicationException with status {@link Status#NOT_FOUND} if Photograph with that id does not exist (any more).
     */
    @GET
    @Path("{id}")
    @Produces(
            {
                "application/xml", "application/json"
            })
    @Override
    public Photograph find(@PathParam("id") Long id)
    {
        return super.find(id);
    }

    /**
     * Retrieves an array of Tags belonging to a photograph. GET to /YourPersonalPhotographOrganiser/resources/photographs/{id}/tags.
     * Can produce both application/xml as well as application/json when asked.
     *
     * @param id unique identifier for the photograph, present in the url.
     * @return array of Tags
     * @throws WebApplicationException with status {@link Status#NOT_FOUND} if Photograph with that id does not exist (any more).
     */
    @GET
    @Path("{id}/tags")
    @Produces(
            {
                "application/xml", "application/json"
            })
    public Collection<Tag> getTags(@PathParam("id") Long id)
    {
        Photograph photograph = find(id);
        return photograph.getTagCollection();
    }

    @GET
    @Override
    @Produces(
            {
                "application/xml", "application/json"
            })
    public List<Photograph> findAll()
    {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces(
            {
                "application/xml", "application/json"
            })
    public List<Photograph> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to)
    {
        return super.findRange(new int[]
        {
            from, to
        });
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST()
    {
        return String.valueOf(super.count());
    }

    public File getFile(Long id)
    {
        Photograph photo = find(id);
        if (photo == null)
        {
            return null;
        }

        java.nio.file.Path newPath = FileSystems.getDefault().getPath(photo.getLocation().getFilepath(), photo.getRelativepath(), photo.getFilename());
        File file = newPath.toFile();
        return file;
    }

    @GET
    @Path("{id}/metadata")
    @Produces(
            {
                "application/xml", "application/json"
            })
    public List<PhotoMetadata> getMetadata(@PathParam("id") Long id)
    {
        Photograph photo = find(id);
        if (photo == null)
        {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        if (!ImageOperations.isImage(photo.getFullPath()))
        {
            throw new WebApplicationException(Status.NO_CONTENT);
        }
        File jpegFile = getFile(id);
        try
        {
            List<PhotoMetadata> result = ImageOperations.getMetadata(jpegFile);
            return result;
        } catch (ImageProcessingException | IOException ex)
        {
            Logger.getLogger(PhotographBean.class.getName()).log(Level.SEVERE, null, ex);
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);

        }
    }

    public ImageAngle getAngle(Long id) throws ImageProcessingException, IOException, MetadataException
    {
        Logger.getLogger(PhotographBean.class.getName()).log(Level.FINE, "getAngle {0}", id);
        Photograph photo = find(id);
        Logger.getLogger(PhotographBean.class.getName()).log(Level.FINE, "getAngle returns {0}", photo.getAngle());
        return photo.getAngle();
    }

    /**
     * Provides a list of Photographs that are not assigned to any GalleryPhotographs
     * yet.
     *
     * @return List<Photograph> a list of photographs currently not assigned
     * to any galleries.
     */
    public List<Photograph> unusedPhotographs()
    {
        try
        {
            // get all photographs that are unused
            Query query = em.createNamedQuery("Photograph.findUnused");
            @SuppressWarnings("unchecked")
            List<Photograph> list = query.getResultList();
            if (list != null)
            {
                return list;
            }
            return Collections.emptyList();
        } catch (ConstraintViolationException e)
        {
            logger.throwing(this.getClass().getName(), "unusedPhotographs", e);
            for (ConstraintViolation<?> violation : e.getConstraintViolations())
            {
                logger.warning(violation.toString());
                logger.exiting(this.getClass().getName(), "issues");
            }
            throw e;
        }
    }

    /**
     * Provides a list of Photographs that are assigned more than once.
     *
     * @return List<Photograph> a list of photographs currently assigned
     * to any galleries more than once.
     */
    public List<Photograph> findDoubleUsedPhotographs()
    {
        try
        {
            // get all photographs that are used more than once
            Query query = em.createNamedQuery("Photograph.findDoubleUsed");
            @SuppressWarnings("unchecked")
            List<Photograph> list = query.getResultList();
            if (list != null)
            {
                return list;
            }
            return Collections.emptyList();
        } catch (ConstraintViolationException e)
        {
            logger.throwing(this.getClass().getName(), "findDoubleUsedPhotographs", e);
            for (ConstraintViolation<?> violation : e.getConstraintViolations())
            {
                logger.warning(violation.toString());
                logger.exiting(this.getClass().getName(), "issues");
            }
            throw e;
        }
    }

}
