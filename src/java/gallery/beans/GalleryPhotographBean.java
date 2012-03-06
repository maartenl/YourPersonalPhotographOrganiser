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
import gallery.database.entities.Comment;
import gallery.database.entities.GalleryPhotograph;
import gallery.enums.ImageAngle;
import java.io.IOException;
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
 * GalleryPhotograph Enterprise Java Bean, maps to a GalleryPhotograph Hibernate Entity.
 * Also a REST service mapping to /YourPersonalPhotographOrganiser/resources/galleryphotographs.
 * @author maartenl
 */
@Stateless
@Path("/galleryphotographs")
public class GalleryPhotographBean extends AbstractBean<GalleryPhotograph>
{

    @PersistenceContext(unitName = "YourPersonalPhotographOrganiserPU")
    private EntityManager em;

    @EJB
    private PhotographBean photographBean;

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }

    public GalleryPhotographBean()
    {
        super(GalleryPhotograph.class);
    }

    /**
     * Creates an GalleryPhotograph. POST to /YourPersonalPhotographOrganiser/resources/galleryphotographs.
     * Will accept both application/xml as well as application/json.
     * @param entity GalleryPhotograph
     */
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

    /**
     * Updates a GalleryPhotograph. PUT to /YourPersonalPhotographOrganiser/resources/galleryphotographs.
     * Will accept both application/xml as well as application/json.
     * @param entity GalleryPhotograph
     */
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
        photo.setPhotograph(photographBean.find(entity.getPhotograph().getId()));

        if (!entity.getPhotograph().hasNoAngle())
        {
            try
            {
                photo.getPhotograph().setAngle(entity.getPhotograph().getAngle());
            } catch (ImageProcessingException | MetadataException | IOException ex)
            {
            }
        }

        if (entity.getSortorder() != null && !entity.getSortorder().equals(photo.getSortorder()))
        {
            // sort order has been changed, reordering required!
            //List<GalleryPhotograph> list = photo.getGallery().getGalleryPhotographCollection();
        }
        photo.setSortorder(entity.getSortorder());
        // super.edit(entity);

    }

    /**
     * Removes a GalleryPhotograph. DELETE to /YourPersonalPhotographOrganiser/resources/galleryphotographs/{id}.
     * @param id unique identifier for the GalleryPhotograph, present in the url.
     */
    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id)
    {
        super.remove(find(id));
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

    /**
     * Retrieves a GalleryPhotograph. GET to /YourPersonalPhotographOrganiser/resources/galleryphotographs/{id}.
     * Can produce both application/xml as well as application/json when asked.
     * @param id unique identifier for the comment, present in the url.
     * @return GalleryPhotograph entity
     * @throws WebApplicationException with status {@link Status#NOT_FOUND} if GalleryPhotograph with that id does not exist (any more).
     */
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
