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
import gallery.enums.ImageAngle;
import gallery.images.ImageOperations;
import gallery.images.PhotoMetadata;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Collections;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

/**
 * Photograph Enterprise Java Bean, maps to a Photograph Hibernate Entity.
 * Also a REST service mapping to /YourPersonalPhotographOrganiser/resources/photographs.
 * @author maartenl
 */
@Stateless
@Path("/photographs")
public class PhotographBean extends AbstractBean<Photograph>
{

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
    public Photograph find(@PathParam("id") Long id)
    {
        return super.find(id);
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
        if (!ImageOperations.isImage(photo.getFullPath()))
        {
            return Collections.emptyList();
        }
        File jpegFile = getFile(id);
        try
        {
            return ImageOperations.getMetadata(jpegFile);
            // JDK 7 - Multicatch , woohoo!
        } catch (ImageProcessingException | IOException ex)
        {
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        }

    }

    public ImageAngle getAngle(Long id) throws ImageProcessingException, IOException, MetadataException
    {
        Photograph photo = find(id);
        return photo.getAngle();
    }
}
