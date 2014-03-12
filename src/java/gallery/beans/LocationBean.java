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
import gallery.database.entities.Location;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Logger;
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
 * Location Enterprise Java Bean, maps to a Location Hibernate Entity. Also a
 * REST service mapping to /YourPersonalPhotographOrganiser/resources/locations.
 *
 * @author maartenl
 */
@Stateless
@Path("/locations")
public class LocationBean extends AbstractBean<Location>
{
    private static final Logger logger = Logger.getLogger(LocationBean.class.getName());

    @EJB
    JobBean jobBean;

    @PersistenceContext(unitName = "YourPersonalPhotographOrganiserPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager()
    {
        return em;
    }

    public LocationBean()
    {
        super(Location.class);
    }

    /**
     * Creates a Location. POST to
     * /YourPersonalPhotographOrganiser/resources/locations. Will accept both
     * application/xml as well as application/json.
     *
     * @param entity Location
     */
    @POST
    @Override
    @Consumes(
            {
                "application/xml", "application/json"
            })
    public void create(Location entity)
    {
        super.create(entity);
    }

    /**
     * Updates a Location. PUT to
     * /YourPersonalPhotographOrganiser/resources/locations. Will accept both
     * application/xml as well as application/json.
     *
     * @param entity Location
     */
    @PUT
    @Override
    @Consumes(
            {
                "application/xml", "application/json"
            })
    public void edit(Location entity)
    {
        super.edit(entity);
    }

    /**
     * Removes a Location. DELETE to
     * /YourPersonalPhotographOrganiser/resources/locations/{id}.
     *
     * @param id unique identifier for the Location, present in the url.
     */
    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Long id)
    {
        super.remove(super.find(id));
    }

    /**
     * Retrieves a Location. GET to
     * /YourPersonalPhotographOrganiser/resources/locations/{id}. Can produce
     * both application/xml as well as application/json when asked.
     *
     * @param id unique identifier for the comment, present in the url.
     * @return Location entity
     * @throws WebApplicationException with status {@link Status#NOT_FOUND} if
     * Location with that id does not exist (any more).
     */
    @GET
    @Path("{id}")
    @Produces(
            {
                "application/xml", "application/json"
            })
    @Override
    public Location find(@PathParam("id") Long id)
    {
        Location location = super.find(id);
        if (location == null)
        {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        return location;
    }

    /**
     * Checks a directory stored in "location" for new photographs or films.
     *
     * @param id a Long representing the id of the Location entity.
     * @return String containing an error message, or null upon success.
     * @see JobBean#checkDirectory(gallery.database.entities.Location)
     */
    @GET
    @Path("{id}/discover")
    public String discover(@PathParam("id") Long id)
    {
        Location location = find(id);
        String result = "";
        try
        {
            result = jobBean.checkDirectory(location);

        } catch (IOException | NoSuchAlgorithmException | ImageProcessingException | MetadataException ex)
        {
            logger.throwing(this.getClass().getName(), "discover", ex);
            if (location == null)
            {
                throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
            }
        }
        return result;
    }

    @GET
    @Override
    @Produces(
            {
                "application/xml", "application/json"
            })
    public List<Location> findAll()
    {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces(
            {
                "application/xml", "application/json"
            })
    public List<Location> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to)
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

    /**
     * Checks all the Photographs in a Location, based on their hash map and the
     * contents of the file. Basically walks the directory tree.
     *
     * @param id a Long representing the id of the Location entity.
     * @return String containing an error message, or null upon success.
     * @see JobBean#processPhoto(gallery.database.entities.Location,
     * java.nio.file.Path) checkDirectory(gallery.database.entities.Location)
     */
    public String verify(Long id)
    {
        Location location = find(id);
        String result = "";
        result = jobBean.verifyPhotographs(location);
        return result;
    }
}
