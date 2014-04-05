package gallery.admin;

import gallery.admin.util.JsfUtil;
import gallery.admin.util.PaginationHelper;
import gallery.beans.PhotographBean;
import gallery.database.entities.Photograph;
import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Named;

@Named("photographController")
@SessionScoped
public class PhotographController implements Serializable
{

    private Photograph current;
    private DataModel items = null;
    @EJB
    private PhotographBean photographBean;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public PhotographController()
    {
    }

    public Photograph getSelected()
    {
        if (current == null)
        {
            current = new Photograph();
            selectedItemIndex = -1;
        }
        return current;
    }

    private PhotographBean getFacade()
    {
        return photographBean;
    }

    public PaginationHelper getPagination()
    {
        if (pagination == null)
        {
            pagination = new PaginationHelper()
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
        current = (Photograph) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate()
    {
        current = new Photograph();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create()
    {
        try
        {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("PhotographCreated"));
            return prepareCreate();
        } catch (Exception e)
        {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit()
    {
        current = (Photograph) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update()
    {
        try
        {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("PhotographUpdated"));
            return "View";
        } catch (Exception e)
        {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy()
    {
        current = (Photograph) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("PhotographDeleted"));
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
        return JsfUtil.getSelectItems(photographBean.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne()
    {
        return JsfUtil.getSelectItems(photographBean.findAll(), true);
    }

    public Photograph getPhotograph(java.lang.Long id)
    {
        return photographBean.find(id);
    }

    @FacesConverter(forClass = Photograph.class)
    public static class PhotographControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value)
        {
            if (value == null || value.length() == 0)
            {
                return null;
            }
            PhotographController controller = (PhotographController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "photographController");
            return controller.getPhotograph(getKey(value));
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
            if (object instanceof Photograph)
            {
                Photograph o = (Photograph) object;
                return getStringKey(o.getId());
            } else
            {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Photograph.class.getName());
            }
        }

    }

    public String getImageurl()
    {
        if (getSelected() == null)
        {
            return null;
        }
        return "ImageServlet?id=" + getSelected().getId() + "&size=medium";
    }

}
