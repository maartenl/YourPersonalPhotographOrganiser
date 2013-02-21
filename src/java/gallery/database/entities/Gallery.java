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

/**
 * A collection of references to Photographs and other galleries.
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gallery")
    private Collection<GalleryPhotograph> galleryPhotographCollection;
    @JoinColumn(name = "highlight", referencedColumnName = "id")
    @ManyToOne
    private Photograph highlight;
        @OneToMany(mappedBy = "parent")
    private Collection<Gallery> galleryCollection;
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @ManyToOne
    private Gallery parent;

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

    /**
     * Gets the unique identifier/primary key.
     * @return Long containing the identifier.
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Sets the unique identifier/primary key.
     * @param id Long containing the (new) identifier.
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Name of the gallery
     * @return String containing the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the gallery.
     * @param name the new name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Description of the gallery
     * @return String containing the description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the description of the gallery
     * @param description String with the new description.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Creation date of the gallery.
     * @return the date of creation, default the current date.
     */
    public Date getCreationDate()
    {
        return creationDate;
    }

    /**
     * Sets the creation date of the gallery.
     * @param creationDate the (new) creation date
     */
    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    /**
     * Sort order, indicates the order in which the galleries should be displayed,
     * where 0 is the first gallery, 1 is the next, etc.
     * @return the sort order, 0..infinity
     */
    public int getSortorder()
    {
        return sortorder;
    }

    /**
     * Sets the sort order.
     * @param sortorder the sort order, 0..infinity.
     */
    public void setSortorder(int sortorder)
    {
        this.sortorder = sortorder;
    }

    /**
     * Photographs contained in this gallery.
     * @return Collection of photographs contained in the gallery.
     */
    @XmlTransient
    public Collection<GalleryPhotograph> getGalleryPhotographCollection()
    {
        return galleryPhotographCollection;
    }

    /**
     * Sets the photographs in the gallery.
     * @param galleryPhotographCollection the new collection of photographs
     * contained in this gallery.
     */
    public void setGalleryPhotographCollection(Collection<GalleryPhotograph> galleryPhotographCollection)
    {
        this.galleryPhotographCollection = galleryPhotographCollection;
    }

    /**
     * The photograph providing a visual cue about what this gallery is about.
     * @return the Photograph resembling the gallery
     */
    public Photograph getHighlight()
    {
        return highlight;
    }

    /**
     * Sets the photograph to use as visual identifier for this gallery
     * @param highlight the Photograph resembling the gallery
     */
    public void setHighlight(Photograph highlight)
    {
        this.highlight = highlight;
    }

    /**
     * All the galleries contained in this gallery.
     * @return Collection of all galleries contained.
     */
    @XmlTransient
    public Collection<Gallery> getGalleryCollection()
    {
        return galleryCollection;
    }

    /**
     * Sets the collection of all galleries contained in this gallery.
     * @param galleryCollection the new collection of galleries.
     */
    public void setGalleryCollection(Collection<Gallery> galleryCollection)
    {
        this.galleryCollection = galleryCollection;
    }

    /**
     * Provides the gallery of which this one is a child.
     * @return parent gallery
     */
    public Gallery getParent()
    {
        return parent;
    }

    /**
     * Sets the parent of this gallery
     * @param parent new parent gallery.
     */
    public void setParent(Gallery parent)
    {
        this.parent = parent;
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
