package gallery.admin.util;

import gallery.admin.GeneralController;
import javax.faces.model.DataModel;

public abstract class PaginationHelper
{

    private final int pageSize;
    private int page;

    public PaginationHelper(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public PaginationHelper()
    {
        this.pageSize = GeneralController.getPageSize();
    }

    public abstract int getItemsCount();

    public abstract DataModel createPageDataModel();

    public int getPageFirstItem()
    {
        return page * pageSize;
    }

    public int getPageLastItem()
    {
        int i = getPageFirstItem() + pageSize - 1;
        int count = getItemsCount() - 1;
        if (i > count)
        {
            i = count;
        }
        if (i < 0)
        {
            i = 0;
        }
        return i;
    }

    public boolean isHasNextPage()
    {
        return (page + 1) * pageSize + 1 <= getItemsCount();
    }

    public void nextPage()
    {
        if (isHasNextPage())
        {
            page++;
        }
    }

    public void firstPage()
    {
        page = 0;
    }

    public void lastPage()
    {
        page = getItemsCount() / pageSize;
    }

    public boolean isHasPreviousPage()
    {
        return page > 0;
    }

    public void previousPage()
    {
        if (isHasPreviousPage())
        {
            page--;
        }
    }

    public int getPageSize()
    {
        return pageSize;
    }

}
