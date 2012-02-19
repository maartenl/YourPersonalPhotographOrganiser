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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author maartenl
 */
@Stateless
@Path("/galleries")
public class GalleryBean extends AbstractBean<Gallery>
{

    @EJB
    JobBean jobBean;
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

    @POST
    @Override
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public void create(Gallery entity)
    {
        entity.setCreationDate(new Date());
        if (entity.getParentId() != null && entity.getParentId().getId() != null)
        {
            Gallery parentGallery = find(entity.getParentId().getId());
            entity.setParentId(parentGallery);
        } else
        {
            entity.setParentId(null);
        }
        try
        {
            super.create(entity);
        } catch (ConstraintViolationException e)
        {
            for (ConstraintViolation<?> violation : e.getConstraintViolations())
            {
                System.out.println(violation);
            }
        }
    }

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
            if (entity.getParentId() != null && entity.getParentId().getId() != null)
            {
                Gallery parent = find(entity.getParentId().getId());
                updatedGallery.setParentId(parent);
            } else
            {
                updatedGallery.setParentId(null);
            }

            // super.edit(entity);
        } catch (ConstraintViolationException e)
        {
            for (ConstraintViolation<?> violation : e.getConstraintViolations())
            {
                System.out.println(violation);
            }
        }
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id)
    {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public Gallery find(@PathParam("id") Long id)
    {
        Gallery found = super.find(id);
        if (found == null)
        {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return found;
    }

    @GET
    @Path("{id}/import")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public String importPhotographs(@PathParam("id") Long id, @QueryParam("location") String location)
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
        Gallery gallery = find(id);
        return gallery.getGalleryPhotographCollection();
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
        return collection;
    }

    @GET
    @Override
    @Produces(
    {
        "application/xml", "application/json"
    })
    public List<Gallery> findAll()
    {
        return super.findAll();
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
}
