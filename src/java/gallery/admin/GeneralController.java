package gallery.admin;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Contains general stuff that is not page-specific.
 *
 * @author maartenl
 */
@Named("generalController")
@SessionScoped
public class GeneralController implements Serializable
{

    private static final Logger logger = Logger.getLogger(GeneralController.class.getName());

    /**
     * Default number of rows to display on one page.
     */
    private static final int DEFAULT_PAGESIZE = 30;

    @PersistenceContext(unitName = "YourPersonalPhotographOrganiserPU")
    private EntityManager em;

    public GeneralController()
    {
    }

    /**
     * Empties the Shared Object Cache of EclipseLink JPA Provider.
     * This is sometimes required, when changing data and dealing with
     * stale data somewhere else.
     *
     * @return
     */
    public String refresh()
    {
        logger.entering(this.getClass().getName(), "refresh");
        em.getEntityManagerFactory().getCache().evictAll();
        return null;
    }

    /**
     * Number of rows to display on one page.
     */
    public static int getPageSize()
    {
        return DEFAULT_PAGESIZE;
    }
}
