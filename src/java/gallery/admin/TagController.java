package gallery.admin;

import gallery.admin.util.JsfUtil;
import gallery.admin.util.PaginationHelper;
import gallery.beans.TagBean;
import gallery.database.entities.Tag;
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

@Named("tagController")
@SessionScoped
public class TagController implements Serializable
{

    private Tag current;
    private DataModel items = null;
    @EJB
    private TagBean tagBean;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public TagController()
    {
    }

    public Tag getSelected()
    {
        if (current == null)
        {
            current = new Tag();
            current.setTagPK(new gallery.database.entities.TagPK());
            selectedItemIndex = -1;
        }
        return current;
    }

    private TagBean getFacade()
    {
        return tagBean;
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
        current = (Tag) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate()
    {
        current = new Tag();
        current.setTagPK(new gallery.database.entities.TagPK());
        selectedItemIndex = -1;
        return "Create";
    }

    public String create()
    {
        try
        {
            current.getTagPK().setPhotographId(current.getPhotograph().getId());
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("TagCreated"));
            return prepareCreate();
        } catch (Exception e)
        {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit()
    {
        current = (Tag) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update()
    {
        try
        {
            current.getTagPK().setPhotographId(current.getPhotograph().getId());
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("TagUpdated"));
            return "View";
        } catch (Exception e)
        {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy()
    {
        current = (Tag) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/bundle").getString("TagDeleted"));
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
        return JsfUtil.getSelectItems(tagBean.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne()
    {
        return JsfUtil.getSelectItems(tagBean.findAll(), true);
    }

    public Tag getTag(gallery.database.entities.TagPK id)
    {
        // TODO: fix this
        return null; // return tagBean.find(id);
    }

    @FacesConverter(forClass = Tag.class)
    public static class TagControllerConverter implements Converter
    {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value)
        {
            if (value == null || value.length() == 0)
            {
                return null;
            }
            TagController controller = (TagController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "tagController");
            return controller.getTag(getKey(value));
        }

        gallery.database.entities.TagPK getKey(String value)
        {
            gallery.database.entities.TagPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new gallery.database.entities.TagPK();
            key.setTagname(values[0]);
            key.setPhotographId(Long.parseLong(values[1]));
            return key;
        }

        String getStringKey(gallery.database.entities.TagPK value)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getTagname());
            sb.append(SEPARATOR);
            sb.append(value.getPhotographId());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object)
        {
            if (object == null)
            {
                return null;
            }
            if (object instanceof Tag)
            {
                Tag o = (Tag) object;
                return getStringKey(o.getTagPK());
            } else
            {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Tag.class.getName());
            }
        }

    }

}
