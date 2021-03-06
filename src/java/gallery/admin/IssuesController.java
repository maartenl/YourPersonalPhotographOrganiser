package gallery.admin;

import gallery.beans.PhotographBean;
import gallery.database.entities.Photograph;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named("issuesController")
@SessionScoped
public class IssuesController implements Serializable
{

    @EJB
    private PhotographBean photographBean;

    private List<Photograph> unusedPhotographs;

    private List<Photograph> doubleUsedPhotographs;

    public IssuesController()
    {
    }

    private PhotographBean getFacade()
    {
        return photographBean;
    }

    public List<Photograph> getUnusedPhotographs()
    {
        if (unusedPhotographs == null)
        {
            unusedPhotographs = getFacade().unusedPhotographs();
        }
        return unusedPhotographs;
    }

    public List<Photograph> getDoubleUsedPhotographs()
    {
        if (doubleUsedPhotographs == null)
        {
            doubleUsedPhotographs = getFacade().findDoubleUsedPhotographs();
        }
        return doubleUsedPhotographs;
    }

    public void refresh()
    {
        unusedPhotographs = null;
    }
}
