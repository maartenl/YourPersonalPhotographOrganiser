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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "Tag")
@XmlRootElement
@NamedQueries(
{
    @NamedQuery(name = "Tag.findAll", query = "SELECT t FROM Tag t"),
    @NamedQuery(name = "Tag.findByTagname", query = "SELECT t FROM Tag t WHERE t.tagPK.tagname = :tagname"),
    @NamedQuery(name = "Tag.findByPhotographId", query = "SELECT t FROM Tag t WHERE t.tagPK.photographId = :photographId")
})
public class Tag implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TagPK tagPK;
    @JoinColumn(name = "photograph_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Photograph photograph;

    public Tag()
    {
    }

    public Tag(TagPK tagPK)
    {
        this.tagPK = tagPK;
    }

    public Tag(String tagname, long photographId)
    {
        this.tagPK = new TagPK(tagname, photographId);
    }

    public TagPK getTagPK()
    {
        return tagPK;
    }

    public void setTagPK(TagPK tagPK)
    {
        this.tagPK = tagPK;
    }

    public Photograph getPhotograph()
    {
        return photograph;
    }

    public void setPhotograph(Photograph photograph)
    {
        this.photograph = photograph;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (tagPK != null ? tagPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tag))
        {
            return false;
        }
        Tag other = (Tag) object;
        if ((this.tagPK == null && other.tagPK != null) || (this.tagPK != null && !this.tagPK.equals(other.tagPK)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "gallery.database.entities.Tag[ tagPK=" + tagPK + " ]";
    }

}
