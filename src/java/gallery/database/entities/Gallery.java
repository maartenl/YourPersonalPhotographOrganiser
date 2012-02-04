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
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "Gallery")
@XmlRootElement
@NamedQueries(
{
    @NamedQuery(name = "Gallery.findAll", query = "SELECT g FROM Gallery g"),
    @NamedQuery(name = "Gallery.findById", query = "SELECT g FROM Gallery g WHERE g.id = :id"),
    @NamedQuery(name = "Gallery.findByName", query = "SELECT g FROM Gallery g WHERE g.name = :name"),
    @NamedQuery(name = "Gallery.findByCreationDate", query = "SELECT g FROM Gallery g WHERE g.creationDate = :creationDate"),
    @NamedQuery(name = "Gallery.findBySortorder", query = "SELECT g FROM Gallery g WHERE g.sortorder = :sortorder")
})
public class Gallery implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 80)
    @Column(name = "name")
    private String name;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "sortorder")
    private int sortorder;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "galleryId")
    private Collection<GalleryPhotograph> galleryPhotographCollection;
    @JoinColumn(name = "highlight", referencedColumnName = "id")
    @ManyToOne
    private Photograph highlight;
    @OneToMany(mappedBy = "parentId")
    private Collection<Gallery> galleryCollection;
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne
    private Gallery parentId;

    public Gallery()
    {
    }

    public Gallery(Long id)
    {
        this.id = id;
    }

    public Gallery(Long id, Date creationDate, int sortorder)
    {
        this.id = id;
        this.creationDate = creationDate;
        this.sortorder = sortorder;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    public int getSortorder()
    {
        return sortorder;
    }

    public void setSortorder(int sortorder)
    {
        this.sortorder = sortorder;
    }

    @JsonIgnore
    @XmlTransient
    public Collection<GalleryPhotograph> getGalleryPhotographCollection()
    {
        return galleryPhotographCollection;
    }

    public void setGalleryPhotographCollection(Collection<GalleryPhotograph> galleryPhotographCollection)
    {
        this.galleryPhotographCollection = galleryPhotographCollection;
    }

    public Photograph getHighlight()
    {
        return highlight;
    }

    public void setHighlight(Photograph highlight)
    {
        this.highlight = highlight;
    }

    @JsonIgnore
    @XmlTransient
    public Collection<Gallery> getGalleryCollection()
    {
        return galleryCollection;
    }

    public void setGalleryCollection(Collection<Gallery> galleryCollection)
    {
        this.galleryCollection = galleryCollection;
    }

    public Gallery getParentId()
    {
        return parentId;
    }

    public void setParentId(Gallery parentId)
    {
        this.parentId = parentId;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Gallery))
        {
            return false;
        }
        Gallery other = (Gallery) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "gallery.database.entities.Gallery[ id=" + id + " ]";
    }
}
