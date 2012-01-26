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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
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
    @NamedQuery(name = "GalleryPhotograph.findByAngle", query = "SELECT g FROM GalleryPhotograph g WHERE g.angle = :angle"),
    @NamedQuery(name = "GalleryPhotograph.findBySortorder", query = "SELECT g FROM GalleryPhotograph g WHERE g.sortorder = :sortorder")
})
public class GalleryPhotograph implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Column(name = "angle")
    private Integer angle;
    @Column(name = "sortorder")
    private BigInteger sortorder;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "galleryphotographId")
    private Collection<Comment> commentCollection;
    @JoinColumn(name = "photograph_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Photograph photographId;
    @JoinColumn(name = "gallery_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Gallery galleryId;

    public GalleryPhotograph()
    {
    }

    public GalleryPhotograph(Long id)
    {
        this.id = id;
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

    public Integer getAngle()
    {
        return angle;
    }

    public void setAngle(Integer angle)
    {
        this.angle = angle;
    }

    public BigInteger getSortorder()
    {
        return sortorder;
    }

    public void setSortorder(BigInteger sortorder)
    {
        this.sortorder = sortorder;
    }

    @XmlTransient
    public Collection<Comment> getCommentCollection()
    {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection)
    {
        this.commentCollection = commentCollection;
    }

    public Photograph getPhotographId()
    {
        return photographId;
    }

    public void setPhotographId(Photograph photographId)
    {
        this.photographId = photographId;
    }

    @XmlTransient
    public Gallery getGalleryId()
    {
        return galleryId;
    }

    public void setGalleryId(Gallery galleryId)
    {
        this.galleryId = galleryId;
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
