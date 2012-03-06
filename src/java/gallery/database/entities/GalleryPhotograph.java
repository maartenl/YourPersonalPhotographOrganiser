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
import java.math.BigInteger;
import java.util.Collection;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * A photograph contained in a gallery, with references to both the gallery and
 * the original photograph itself.
 * @author maartenl
 */
@Entity
@Table(name = "GalleryPhotograph")
@XmlRootElement
@NamedQueries(
{
    @NamedQuery(name = "GalleryPhotograph.findAll", query = "SELECT g FROM GalleryPhotograph g"),
    @NamedQuery(name = "GalleryPhotograph.findById", query = "SELECT g FROM GalleryPhotograph g WHERE g.id = :id"),
    @NamedQuery(name = "GalleryPhotograph.findByName", query = "SELECT g FROM GalleryPhotograph g WHERE g.name = :name"),
    @NamedQuery(name = "GalleryPhotograph.findBySortorder", query = "SELECT g FROM GalleryPhotograph g WHERE g.sortorder = :sortorder")
})
public class GalleryPhotograph implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Column(name = "sortorder")
    private BigInteger sortorder;
        @OneToMany(cascade = CascadeType.ALL, mappedBy = "galleryphotograph")
    private Collection<Comment> commentCollection;
    @JoinColumn(name = "photograph_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Photograph photograph;
    @JoinColumn(name = "gallery_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Gallery gallery;

    public GalleryPhotograph()
    {
    }

    public GalleryPhotograph(Long id)
    {
        this.id = id;
    }

    /**
     * Primary key/unique identifier.
     * @return Long containing the id.
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Sets the unique identifier.
     * @param id the new primary key/unique identifier
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Name belonging to the picture, originally this is the name of the file,
     * but can be anything.
     * @return the name of the photograph
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the photograph, may be anything.
     * @param name the new name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Full description of the photograph. Can be large.
     * @return the description of the photograph
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the full description of the photograph.
     * @param description String with the new description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Sort order, indicates the order in which the photographs should be displayed,
     * where 0 is the first photograph, 1 is the next, etc.
     * @return the sort order, 0..infinity.
     */
    public BigInteger getSortorder()
    {
        return sortorder;
    }

    /**
     * Sets the sort order.
     * @param sortorder BigInteger, 0..infinity.
     */
    public void setSortorder(BigInteger sortorder)
    {
        this.sortorder = sortorder;
    }

    /**
     * Comments on this photograph.
     * @return Collection of comments.
     */
    @JsonIgnore
    @XmlTransient
    public Collection<Comment> getCommentCollection()
    {
        return commentCollection;
    }

    /**
     * Sets the collection of comments.
     * @param commentCollection a new collection of comments.
     */
    public void setCommentCollection(Collection<Comment> commentCollection)
    {
        this.commentCollection = commentCollection;
    }

    /**
     * Reference to the original file containing the photograph.
     * @return the photograph/file itself.
     */
    public Photograph getPhotograph()
    {
        return photograph;
    }

    /**
     * Sets the photograph where this galleryphotograph refers to.
     * @param photograph the new photograph
     */
    public void setPhotograph(Photograph photograph)
    {
        this.photograph = photograph;
    }

    /**
     * Reference to the gallery, that contains this photograph.
     * @return the Gallery
     */
    @JsonIgnore
    @XmlTransient
    public Gallery getGallery()
    {
        return gallery;
    }

    /**
     * For setting to which gallery this galleryphotograph belongs.
     * @param gallery the gallery
     */
    @JsonIgnore
    @XmlTransient
    public void setGallery(Gallery gallery)
    {
        this.gallery = gallery;
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
        if (!(object instanceof GalleryPhotograph))
        {
            return false;
        }
        GalleryPhotograph other = (GalleryPhotograph) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "gallery.database.entities.GalleryPhotograph[ id=" + id + " ]";
    }
}
