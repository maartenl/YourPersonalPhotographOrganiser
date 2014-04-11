package gallery.admin;

import gallery.admin.util.JsfUtil;
import gallery.admin.util.PaginationHelper;
import gallery.beans.GalleryPhotographBean;
import gallery.database.entities.GalleryPhotograph;
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

@Named("galleryPhotographController")
@SessionScoped
public class GalleryPhotographController implements Serializable
{

    private GalleryPhotograph current;
    private DataModel items = null;
    @EJB
    private GalleryPhotographBean galleryPhotographBean;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private Long gotoId;

    public GalleryPhotographController()
    {
    }

    public GalleryPhotograph getSelected()
    {
        if (current == null)
        {
            current = new GalleryPhotograph();
            selectedItemIndex = -1;
        }
        return current;
    }

    private GalleryPhotographBean getFacade()
    {
        return galleryPhotographBean;
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

    public String gotoPhotograph()
    {
        current = getFacade().find(gotoId);
        return null;
    }

    public String prepareView()
    {
        current = (GalleryPhotograph) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate()
    {
        current = new GalleryPhotograph();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create()
    {
        try
        {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("GalleryPhotographCreated"));
            return prepareCreate();
        } catch (Exception e)
        {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit()
    {
        current = (GalleryPhotograph) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update()
    {
        try
        {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("GalleryPhotographUpdated"));
            return null;
        } catch (Exception e)
        {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy()
    {
        current = (GalleryPhotograph) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("GalleryPhotographDeleted"));
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
        return JsfUtil.getSelectItems(galleryPhotographBean.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne()
    {
        return JsfUtil.getSelectItems(galleryPhotographBean.findAll(), true);
    }

    public GalleryPhotograph getGalleryPhotograph(java.lang.Long id)
    {
        return galleryPhotographBean.find(id);
    }

    /**
     * @return the gotoId
     */
    public Long getGotoId()
    {
        return gotoId;
    }

    /**
     * @param gotoId the gotoId to set
     */
    public void setGotoId(Long gotoId)
    {
        this.gotoId = gotoId;
    }

    @FacesConverter(forClass = GalleryPhotograph.class)
    public static class GalleryPhotographControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value)
        {
            if (value == null || value.length() == 0)
            {
                return null;
            }
            GalleryPhotographController controller = (GalleryPhotographController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "galleryPhotographController");
            return controller.getGalleryPhotograph(getKey(value));
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
            if (object instanceof GalleryPhotograph)
            {
                GalleryPhotograph o = (GalleryPhotograph) object;
                return getStringKey(o.getId());
            } else
            {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + GalleryPhotograph.class.getName());
            }
        }

    }

    public String getImageurl()
    {
        if (getSelected() == null || getSelected().getPhotograph() == null)
        {
            return null;
        }
        return "ImageServlet?id=" + getSelected().getPhotograph().getId() + "&size=medium";
    }
}
