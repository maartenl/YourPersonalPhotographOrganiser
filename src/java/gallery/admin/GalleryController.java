package gallery.admin;

import gallery.admin.util.JsfUtil;
import gallery.admin.util.PaginationHelper;
import gallery.beans.GalleryBean;
import gallery.database.entities.Gallery;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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

@Named("galleryController")
@SessionScoped
public class GalleryController implements Serializable
{

    private static final Logger logger = Logger.getLogger(GalleryController.class.getName());

    private Gallery current;
    private DataModel items = null;
    @EJB
    private GalleryBean galleryBean;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private String location;
    private Gallery parentGallery;

    public GalleryController()
    {
    }

    public Gallery getSelected()
    {
        if (current == null)
        {
            current = new Gallery();
            selectedItemIndex = -1;
        }
        return current;
    }

    private GalleryBean getFacade()
    {
        return galleryBean;
    }

    public List<Gallery> getGalleries()
    {
        return getFacade().findAll();
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
                    if (getParentGallery() != null)
                    {
                        return getParentGallery().getGalleryCollection().size();
                    }
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel()
                {
                    if (getParentGallery() == null)
                    {
                        return new ListDataModel(getFacade().findRange(new int[]
                        {
                            getPageFirstItem(), getPageFirstItem() + getPageSize()
                        }));
                    }
                    return new ListDataModel(new ArrayList<>(getFacade().getGalleries(getParentGallery().getId())));
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
        current = (Gallery) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate()
    {
        current = new Gallery();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create()
    {
        try
        {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("GalleryCreated"));
            return prepareCreate();
        } catch (Exception e)
        {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit()
    {
        current = (Gallery) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update()
    {
        try
        {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("GalleryUpdated"));
            return "View";
        } catch (Exception e)
        {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy()
    {
        current = (Gallery) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("GalleryDeleted"));
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

    public String importPhotographs()
    {
        if (current == null)
        {
            return null;
        }
        logger.log(Level.FINEST, "importPhotographs {0} {1}", new Object[]
        {
            location, current.getName()
        });
        String message = getFacade().importPhotographs(current.getId(), location);
        if (message != null)
        {
            logger.warning(message);
        }
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("PhotographsImported"));
        return null;
    }

    public SelectItem[] getItemsAvailableSelectMany()
    {
        return JsfUtil.getSelectItems(galleryBean.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne()
    {
        return JsfUtil.getSelectItems(galleryBean.findAll(), true);
    }

    public Gallery getGallery(java.lang.Long id)
    {
        return galleryBean.find(id);
    }

    /**
     * @return the location
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location)
    {
        this.location = location;
    }

    /**
     * @return the parentGallery
     */
    public Gallery getParentGallery()
    {
        return parentGallery;
    }

    /**
     * @param parentGallery the parentGallery to set
     */
    public void setParentGallery(Gallery parentGallery)
    {
        if (this.parentGallery != parentGallery)
        {
            // parentGallery has changed, refresh the data model and the pagination.
            recreateModel();
            recreatePagination();
        }
        this.parentGallery = parentGallery;
    }

    @FacesConverter(forClass = Gallery.class)
    public static class GalleryControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value)
        {
            if (value == null || value.length() == 0)
            {
                return null;
            }
            GalleryController controller = (GalleryController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "galleryController");
            return controller.getGallery(getKey(value));
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
            if (object instanceof Gallery)
            {
                Gallery o = (Gallery) object;
                return getStringKey(o.getId());
            } else
            {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Gallery.class.getName());
            }
        }

    }

    public String reorderPhotographs()
    {
        current = (Gallery) getItems().getRowData();
        getFacade().reorderPhotographs(current.getId());
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("PhotographsReordered"));
        return "List";
    }

    public String reorderGalleries()
    {
        current = (Gallery) getItems().getRowData();
        getFacade().reorderGalleries(current.getId());
        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("GalleriesReordered"));
        return "List";
    }

    public String getImageurl()
    {
        if (getSelected() == null || getSelected().getHighlight() == null)
        {
            return null;
        }
        return "ImageServlet?id=" + getSelected().getHighlight().getId() + "&size=medium";
    }

}
