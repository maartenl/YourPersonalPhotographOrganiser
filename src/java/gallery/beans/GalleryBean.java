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
import gallery.database.entities.Photograph;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
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
 * Gallery Enterprise Java Bean, maps to a Gallery Hibernate Entity.
 * Also a REST service mapping to /YourPersonalPhotographOrganiser/resources/galleries.
 *
 * @author maartenl
 */
@Stateless
@Path("/galleries")
public class GalleryBean extends AbstractBean<Gallery>
{

    private static final Logger logger = Logger.getLogger(GalleryBean.class.getName());

    @EJB
    private JobBean jobBean;

    @EJB
    private PhotographBean photographBean;

    @PersistenceContext(unitName = "YourPersonalPhotographOrganiserPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }

    public GalleryBean()
    {
        super(Gallery.class);
    }

    /**
     * Creates an empty Gallery. POST to /YourPersonalPhotographOrganiser/resources/galleries.
     * Will accept both application/xml as well as application/json.
     *
     * @param entity Gallery
     */
    @POST
    @Override
    @Consumes(
            {
                "application/xml", "application/json"
            })
    public void create(Gallery entity)
    {
        entity.setCreationDate(new Date());
        if (entity.getParent() != null && entity.getParent().getId() != null)
        {
            Gallery parentGallery = find(entity.getParent().getId());
            entity.setParent(parentGallery);
        } else
        {
            entity.setParent(null);
        }
        if (entity.getHighlight() != null && entity.getHighlight().getId() != null)
        {
            Photograph highlight = photographBean.find(entity.getHighlight().getId());
            entity.setHighlight(highlight);
        } else
        {
            entity.setHighlight(null);
        }
        try
        {
            super.create(entity);
        } catch (ConstraintViolationException e)
        {
            for (ConstraintViolation<?> violation : e.getConstraintViolations())
            {
                logger.fine(violation.toString());
            }
        }
    }

    /**
     * Updates a Gallery. PUT to /YourPersonalPhotographOrganiser/resources/galleries.
     * Will accept both application/xml as well as application/json.
     *
     * @param entity Gallery
     */
    @PUT
    @Override
    @Consumes(
            {
                "application/xml", "application/json"
            })
    public void edit(Gallery entity)
    {
        try
        {
            Gallery updatedGallery = find(entity.getId());
            updatedGallery.setDescription(entity.getDescription());
            updatedGallery.setName(entity.getName());
            updatedGallery.setSortorder(entity.getSortorder());
            if (entity.getParent() != null && entity.getParent().getId() != null)
            {
                Gallery parent = find(entity.getParent().getId());
                updatedGallery.setParent(parent);
            } else
            {
                updatedGallery.setParent(null);
            }
            if (entity.getHighlight() != null && entity.getHighlight().getId() != null)
            {
                Photograph highlight = photographBean.find(entity.getHighlight().getId());
                updatedGallery.setHighlight(highlight);
            } else
            {
                updatedGallery.setHighlight(null);
            }

            // super.edit(entity);
        } catch (ConstraintViolationException e)
        {
            for (ConstraintViolation<?> violation : e.getConstraintViolations())
            {
                logger.fine(violation.toString());
            }
        }
    }

    /**
     * Removes a Gallery. DELETE to /YourPersonalPhotographOrganiser/resources/galleries/{id}.
     *
     * @param id unique identifier for the gallery, present in the url.
     */
    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id)
    {
        super.remove(find(id));
    }

    /**
     * Retrieves a Gallery. GET to /YourPersonalPhotographOrganiser/resources/galleries/{id}.
     * Can produce both application/xml as well as application/json when asked.
     *
     * @param id unique identifier for the Gallery, present in the url.
     * @return Gallery entity
     * @throws WebApplicationException with status {@link Status#NOT_FOUND} if Gallery with that id does not exist (any more).
     */
    @GET
    @Path("{id}")
    @Produces(
            {
                "application/xml", "application/json"
            })
    @Override
    public Gallery find(@PathParam("id") Long id)
    {
        Gallery found = super.find(id);
        if (found == null)
        {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return found;
    }

    /**
     * Imports photographs into a gallery.
     *
     * @param id id of the gallery
     * @param location location (in full) to check for photographs.
     * @return
     */
    public String importPhotographs(Long id, String location)
    {
        Gallery found = find(id);
        return jobBean.importPhotographs(found, location);
    }

    @GET
    @Path("{id}/photographs")
    @Produces(
            {
                "application/xml", "application/json"
            })
    public Collection<GalleryPhotograph> getPhotographs(@PathParam("id") Long id)
    {
        logger.entering(this.getClass().getName(), "getPhotographs");
        Gallery gallery = find(id);
        Collection<GalleryPhotograph> result = gallery.getGalleryPhotographCollection();
        if (result == null || result.isEmpty())
        {
            logger.fine("getPhotographs emptyLIst");
            return Collections.emptyList();
        }
        logger.exiting(this.getClass().getName(), "getPhotographs size=" + result.size());
        return result;
    }

    @GET
    @Path("{id}/galleries")
    @Produces(
            {
                "application/xml", "application/json"
            })
    public Collection<Gallery> getGalleries(@PathParam("id") Long id)
    {
        Gallery gallery = find(id);
        Collection<Gallery> collection = gallery.getGalleryCollection();
        if (collection == null || collection.isEmpty())
        {
            return Collections.emptyList();
        }
        return collection;
    }

    /**
     * Retrieves all galleries ordered by id (with a parent, if they have one).
     * GET to /YourPersonalPhotographOrganiser/resources/galleries.
     * Can produce both application/xml as well as application/json when asked.
     *
     * @return List<Gallery> list of all galleries
     */
    @GET
    @Override
    @Produces(
            {
                "application/xml", "application/json"
            })
    public List<Gallery> findAll()
    {
        Query query = em.createNamedQuery("Gallery.findAll");
        @SuppressWarnings("unchecked")
        List<Gallery> list = query.getResultList();
        return list;
    }

    @GET
    @Path("{from}/{to}")
    @Produces(
            {
                "application/xml", "application/json"
            })
    public List<Gallery> findRange(@PathParam("from") Integer from,
            @PathParam("to") Integer to)
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

    public void reorderGalleries(Long id)
    {
        Gallery gallery = find(id);
        gallery.reorderGalleries();
    }

    public void reorderPhotographs(Long id)
    {
        Gallery gallery = find(id);
        gallery.reorderPhotographs();
    }

}
