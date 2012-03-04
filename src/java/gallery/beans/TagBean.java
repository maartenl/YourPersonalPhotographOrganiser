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

import gallery.database.entities.Tag;
import java.util.List;
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

/**
 * Tag Enterprise Java Bean, maps to a Tag Hibernate Entity.
 * Also a REST service mapping to /YourPersonalPhotographOrganiser/resources/tags.
 * @author maartenl
 */
@Stateless
@Path("/tags")
public class TagBean extends AbstractBean<Tag>
{
    @PersistenceContext(unitName = "YourPersonalPhotographOrganiserPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }

    public TagBean()
    {
        super(Tag.class);
    }

    /**
     * Creates a Tag. POST to /YourPersonalPhotographOrganiser/resources/tags.
     * Will accept both application/xml as well as application/json.
     * @param entity Tag
     */
    @POST
    @Override
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public void create(Tag entity)
    {
        super.create(entity);
    }

    /**
     * Updates a Tag. PUT to /YourPersonalPhotographOrganiser/resources/tags.
     * Will accept both application/xml as well as application/json.
     * @param entity Tag
     */
    @PUT
    @Override
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public void edit(Tag entity)
    {
        super.edit(entity);
    }

    /**
     * Removes a Tag. DELETE to /YourPersonalPhotographOrganiser/resources/tags/{id}.
     * @param id unique identifier for the Tag, present in the url.
     */
    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id)
    {
        super.remove(find(id));
    }

    /**
     * Retrieves a Tag. GET to /YourPersonalPhotographOrganiser/resources/tags/{id}.
     * Can produce both application/xml as well as application/json when asked.
     * @param id unique identifier for the comment, present in the url.
     * @return Tag entity
     * @throws WebApplicationException with status {@link Status#NOT_FOUND} if Tag with that id does not exist (any more).
     */
    @GET
    @Path("{id}")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public Tag find(@PathParam("id") Long id)
    {
        return super.find(id);
    }

    @GET
    @Override
    @Produces(
    {
        "application/xml", "application/json"
    })
    public List<Tag> findAll()
    {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public List<Tag> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to)
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
