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

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import gallery.enums.ImageAngle;
import gallery.images.ImageOperations;
import java.io.File;
import java.io.IOException;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author maartenl
 */
@Entity
@Table(name = "Photograph")
@XmlRootElement
@NamedQueries(
{
    @NamedQuery(name = "Photograph.findAll", query = "SELECT p FROM Photograph p"),
    @NamedQuery(name = "Photograph.findByFilename", query = "SELECT p FROM Photograph p WHERE p.filename = :filename and p.relativepath = :relativepath"),
    @NamedQuery(name = "Photograph.findByStats", query = "SELECT p FROM Photograph p WHERE p.hashstring = :hashstring and p.filesize = :filesize"),
    @NamedQuery(name = "Photograph.findByLocation", query = "SELECT p FROM Photograph p "
    + "WHERE concat(p.locationId.filepath, '/', p.relativepath, '/', p.filename) like :mask "
    + "AND not exists (select gp from GalleryPhotograph gp where gp.galleryId = :gallery and gp.photographId = p) "
    + "order by p.taken, p.filename"),
    @NamedQuery(name = "Photograph.findUnused", query = "SELECT p FROM Photograph p WHERE not exists (select gp from GalleryPhotograph gp where gp.photographId = p)")
})
public class Photograph implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "filename")
    private String filename;
    @Size(max = 1024)
    @Column(name = "relativepath")
    private String relativepath;
    @Basic(optional = false)
    @Column(name = "taken")
    @Temporal(TemporalType.TIMESTAMP)
    private Date taken;
    @Basic(optional = false)
    @Size(max = 1024)
    @Column(name = "hashstring")
    private String hashstring;
    @Basic(optional = false)
    @Column(name = "filesize")
    private Long filesize;
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Location locationId;
    @Column(name = "angle")
    private Integer angle;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "photograph")
    private Collection<Tag> tagCollection;

    public Photograph()
    {
    }

    public Photograph(Long id)
    {
        this.id = id;
    }

    public Photograph(Long id, Date taken)
    {
        this.id = id;
        this.taken = taken;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getRelativepath()
    {
        return relativepath;
    }

    public void setRelativepath(String relativepath)
    {
        this.relativepath = relativepath;
    }

    /**
     * Indicates when the picture was taken. This is, if possible, taken
     * from the information contained in the picture and is usually stored
     * by the device that took the picture.
     * @return
     */
    public Date getTaken()
    {
        return taken;
    }

    public void setTaken(Date taken)
    {
        this.taken = taken;
    }

    public Location getLocationId()
    {
        return locationId;
    }

    public void setLocationId(Location locationId)
    {
        this.locationId = locationId;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<Tag> getTagCollection()
    {
        return tagCollection;
    }

    @XmlTransient
    @JsonIgnore
    public String getFullPath()
    {
        return getLocationId().getFilepath() + File.separator + getRelativepath() + File.separator + getFilename();
    }

    public void setTagCollection(Collection<Tag> tagCollection)
    {
        this.tagCollection = tagCollection;
    }

    public Long getFilesize()
    {
        return filesize;
    }

    public void setFilesize(Long filesize)
    {
        this.filesize = filesize;
    }

    public String getHashstring()
    {
        return hashstring;
    }

    public void setHashstring(String hashstring)
    {
        this.hashstring = hashstring;
    }

    /**
     * Indicates the angle at which the picture was taken. This is, if possible, stored
     * in the information contained in the picture and is usually stored
     * by the device that took the picture.
     * @return
     */
    public ImageAngle getAngle() throws ImageProcessingException, IOException, MetadataException
    {
        if (angle == null)
        {
            ImageAngle result = ImageOperations.getAngle(new File(getFullPath()));
            if (result != null)
            {
                angle = result.getAngle();
            }
            return result;
        }
        return ImageAngle.getAngle(angle);
    }

    public void setAngle(ImageAngle angle)
    {
        if (angle == null)
        {
            this.angle = null;
            return;
        }
        this.angle = angle.getAngle();
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
        if (!(object instanceof Photograph))
        {
            return false;
        }
        Photograph other = (Photograph) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "gallery.database.entities.Photograph[ id=" + id + " ]";
    }
}
