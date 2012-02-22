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

import gallery.database.entities.Comment;
import gallery.database.entities.GalleryPhotograph;
import java.util.Collection;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
 *
 * @author maartenl
 */
@Stateless
@Path("/galleryphotographs")
public class GalleryPhotographBean extends AbstractBean<GalleryPhotograph>
{

    @PersistenceContext(unitName = "YourPersonalPhotographOrganiserPU")
    private EntityManager em;
    @EJB
    PhotographBean photographBean;

    protected EntityManager getEntityManager()
    {
        return em;
    }

    public GalleryPhotographBean()
    {
        super(GalleryPhotograph.class);
    }

    @POST
    @Override
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public void create(GalleryPhotograph entity)
    {
        super.create(entity);
    }

    @PUT
    @Override
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public void edit(GalleryPhotograph entity)
    {
        GalleryPhotograph photo = find(entity.getId());
        photo.setDescription(entity.getDescription());
        photo.setName(entity.getName());
        photo.setPhotographId(photographBean.find(entity.getPhotographId().getId()));

        if (entity.getSortorder() != null && !entity.getSortorder().equals(photo.getSortorder()))
        {
            // sort order has been changed, reordering required!
            //List<GalleryPhotograph> list = photo.getGalleryId().getGalleryPhotographCollection();
        }
        photo.setSortorder(entity.getSortorder());
        // super.edit(entity);

    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id)
    {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}/comments")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public Collection<Comment> getComments(@PathParam("id") Long id)
    {
        GalleryPhotograph photo = find(id);
        if (photo == null)
        {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return photo.getCommentCollection();
    }

    @GET
    @Path("{id}")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public GalleryPhotograph find(@PathParam("id") Long id)
    {
        return super.find(id);
    }

    @GET
    @Override
    @Produces(
    {
        "application/xml", "application/json"
    })
    public List<GalleryPhotograph> findAll()
    {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public List<GalleryPhotograph> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to)
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
