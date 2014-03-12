package gallery.admin;

import gallery.admin.util.GalleryException;
import gallery.database.entities.Location;
import gallery.admin.util.JsfUtil;
import gallery.admin.util.PaginationHelper;
import gallery.beans.LocationBean;

import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("locationController")
@SessionScoped
public class LocationController implements Serializable
{

    private static final Logger logger = Logger.getLogger(LocationController.class.getName());

    private Location current;
    private DataModel items = null;
    @EJB
    private LocationBean locationBean;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public LocationController()
    {
    }

    public Location getSelected()
    {
        if (current == null)
        {
            current = new Location();
            selectedItemIndex = -1;
        }
        return current;
    }

    private LocationBean getFacade()
    {
        return locationBean;
    }

    public PaginationHelper getPagination()
    {
        if (pagination == null)
        {
            pagination = new PaginationHelper(10)
            {

                @Override
                public int getItemsCount()
                {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel()
                {
                    return new ListDataModel(getFacade().findRange(new int[]
                    {
                        getPageFirstItem(), getPageFirstItem() + getPageSize()
                    }));
                }
            };
        }
        return pagination;
    }

    public String prepareList()
    {
        recreateModel();
        return "List";
    }

    public String prepareView()
    {
        current = (Location) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate()
    {
        current = new Location();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create()
    {
        try
        {
            current.checkPath();
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("LocationCreated"));
            return prepareCreate();
        } catch (GalleryException ge)
        {
            JsfUtil.addErrorMessage(ge, ge.getMessage());
        } catch (Exception e)
        {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/bundle").getString("PersistenceErrorOccured"));
        }
        return null;
    }

    public String prepareEdit()
    {
        current = (Location) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update()
    {
        try
        {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("LocationUpdated"));
            return "View";
        } catch (Exception e)
        {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy()
    {
        current = (Location) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView()
    {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0)
        {
            return "View";
        } else
        {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy()
    {
        try
        {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("LocationDeleted"));
        } catch (Exception e)
        {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem()
    {
        int count = getFacade().count();
        if (selectedItemIndex >= count)
        {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count)
            {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0)
        {
            current = getFacade().findRange(new int[]
            {
                selectedItemIndex, selectedItemIndex + 1
            }).get(0);
        }
    }

    public DataModel getItems()
    {
        if (items == null)
        {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel()
    {
        items = null;
    }

    private void recreatePagination()
    {
        pagination = null;
    }

    public String next()
    {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous()
    {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany()
    {
        return JsfUtil.getSelectItems(locationBean.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne()
    {
        return JsfUtil.getSelectItems(locationBean.findAll(), true);
    }

    public Location getLocation(java.lang.Long id)
    {
        return locationBean.find(id);
    }

    @FacesConverter(forClass = Location.class)
    public static class LocationControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value)
        {
            if (value == null || value.length() == 0)
            {
                return null;
            }
            LocationController controller = (LocationController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "locationController");
            return controller.getLocation(getKey(value));
        }

        java.lang.Long getKey(String value)
        {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object)
        {
            if (object == null)
            {
                return null;
            }
            if (object instanceof Location)
            {
                Location o = (Location) object;
                return getStringKey(o.getId());
            } else
            {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Location.class.getName());
            }
        }

    }

    public String verify()
    {
        logger.entering(this.getClass().getName(), "verify");
        getFacade().verify(current.getId());
        logger.exiting(this.getClass().getName(), "verify");
        return null;
    }

    public String discover()
    {
        logger.entering(this.getClass().getName(), "discover");
        getFacade().discover(current.getId());
        logger.exiting(this.getClass().getName(), "discover");
        return null;
    }

}
