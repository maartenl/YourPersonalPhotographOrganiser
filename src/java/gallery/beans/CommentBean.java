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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

/**
 * Comment Enterprise Java Bean, maps to a Comment Hibernate Entity.
 * Also a REST service mapping to /YourPersonalPhotographOrganiser/resources/comments.
 * @author maartenl
 */
@Stateless
@Path("/comments")
public class CommentBean extends AbstractBean<Comment>
{
    @PersistenceContext(unitName = "YourPersonalPhotographOrganiserPU")
    private EntityManager em;

    @EJB
    private GalleryPhotographBean galleryphotographBean;

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }

    public CommentBean()
    {
        super(Comment.class);
    }

    /**
     * Creates a comment. POST to /YourPersonalPhotographOrganiser/resources/comments.
     * Will accept both application/xml as well as application/json.
     * @param entity comment
     */
    @POST
    @Override
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public void create(Comment entity)
    {
        GalleryPhotograph photo = galleryphotographBean.find(entity.getGalleryphotograph().getId());
        entity.setGalleryphotograph(photo);
        Comment newComment = new Comment();
        newComment.setAuthor(entity.getAuthor());
        newComment.setComment(entity.getComment());
        newComment.setSubmitted(new Date());
        newComment.setGalleryphotograph(photo);
        photo.getCommentCollection().add(newComment);
        System.out.println(newComment);
        try
        {
        super.create(newComment);
        }
        catch (ConstraintViolationException e)
        {
            for (ConstraintViolation<?> violation : e.getConstraintViolations())
            {
                System.out.println(violation);
            }

        }
        // super.create(entity);
    }

    /**
     * Updates a Comment. PUT to /YourPersonalPhotographOrganiser/resources/comments.
     * Will accept both application/xml as well as application/json.
     * @param entity Comment
     */
    @PUT
    @Override
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public void edit(Comment entity)
    {
        super.edit(entity);
    }

    /**
     * Removes a comment. DELETE to /YourPersonalPhotographOrganiser/resources/comments/{id}.
     * @param id unique identifier for the comment, present in the url.
     */
    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id)
    {
        super.remove(find(id));
    }

    /**
     * Retrieves a Comment. GET to /YourPersonalPhotographOrganiser/resources/comments/{id}.
     * Can produce both application/xml as well as application/json when asked.
     * @param id unique identifier for the comment, present in the url.
     * @return Comment entity
     * @throws WebApplicationException with status {@link Status#NOT_FOUND} if comment with that id does not exist (any more).
     */
    @Override
    @GET
    @Path("{id}")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public Comment find(@PathParam("id") Long id)
    {
        Comment comment = super.find(id);
        if (comment == null)
        {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return comment;
    }

    @GET
    @Override
    @Produces(
    {
        "application/xml", "application/json"
    })
    public List<Comment> findAll()
    {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public List<Comment> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to)
    {
        return super.findRange(new int[]
                {
                    from, to
                });
    }

    @GET
    @Path("count")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public String countREST()
    {
        return String.valueOf(super.count());
    }
}
