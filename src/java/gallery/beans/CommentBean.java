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

/**
 *
 * @author maartenl
 */
@Stateless
@Path("/comments")
public class CommentBean extends AbstractBean<Comment>
{
    @PersistenceContext(unitName = "YourPersonalPhotographOrganiserPU")
    private EntityManager em;

    @EJB
    GalleryPhotographBean galleryphotographBean;

    protected EntityManager getEntityManager()
    {
        return em;
    }

    public CommentBean()
    {
        super(Comment.class);
    }

    @POST
    @Override
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public void create(Comment entity)
    {
        GalleryPhotograph photo = galleryphotographBean.find(entity.getGalleryphotographId().getId());
        entity.setGalleryphotographId(photo);
        Comment newComment = new Comment();
        newComment.setAuthor(entity.getAuthor());
        newComment.setComment(entity.getComment());
        newComment.setSubmitted(new Date());
        newComment.setGalleryphotographId(photo);
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
    public Comment find(@PathParam("id") Long id)
    {
        return super.find(id);
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
