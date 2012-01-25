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
package gallery.database.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Embeddable
public class TagPK implements Serializable
{
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "tagname")
    private String tagname;
    @Basic(optional = false)
    @NotNull
    @Column(name = "photograph_id")
    private long photographId;

    public TagPK()
    {
    }

    public TagPK(String tagname, long photographId)
    {
        this.tagname = tagname;
        this.photographId = photographId;
    }

    public String getTagname()
    {
        return tagname;
    }

    public void setTagname(String tagname)
    {
        this.tagname = tagname;
    }

    public long getPhotographId()
    {
        return photographId;
    }

    public void setPhotographId(long photographId)
    {
        this.photographId = photographId;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (tagname != null ? tagname.hashCode() : 0);
        hash += (int) photographId;
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TagPK))
        {
            return false;
        }
        TagPK other = (TagPK) object;
        if ((this.tagname == null && other.tagname != null) || (this.tagname != null && !this.tagname.equals(other.tagname)))
        {
            return false;
        }
        if (this.photographId != other.photographId)
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "gallery.database.entities.TagPK[ tagname=" + tagname + ", photographId=" + photographId + " ]";
    }

}
