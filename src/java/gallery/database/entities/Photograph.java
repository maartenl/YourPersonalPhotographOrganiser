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
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * An entity representing the file containing the image.
 *
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
                    + "WHERE concat(p.location.filepath, '/', p.relativepath, '/', p.filename) like :mask "
                    + "AND not exists (select gp from GalleryPhotograph gp where gp.gallery = :gallery and gp.photograph = p) "
                    + "order by p.taken, p.filename"),
            @NamedQuery(name = "Photograph.findUnused", query = "SELECT p FROM Photograph p WHERE not exists (select gp from GalleryPhotograph gp where gp.photograph = p)"),
            @NamedQuery(name = "Photograph.getPhotographsByLocation", query = "SELECT p FROM Photograph p WHERE p.location = :location"),
            @NamedQuery(name = "Photograph.getPaths", query = "SELECT DISTINCT p.relativepath FROM Photograph p WHERE p.location = :location")

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
    private Location location;
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

    /**
     * Identification/Primary Key for Photographs.
     *
     * @return
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Sets the id of this Photograph.
     *
     * @param id new primary key/unique number of this photograph
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * The filename and extension of the photograph.
     *
     * @return String containing the filename, for example "DSCN0034.JPG".
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * Sets the appropriate filename of the picture.
     *
     * @param filename String containing the filename without a path.
     */
    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    /**
     * The path to the filename, a relative path based from the Location
     *
     * @see #getLocation()
     * @return String with the relative path, for example "vacation/2010".
     */
    public String getRelativepath()
    {
        return relativepath;
    }

    /**
     * Sets the relative path.
     *
     * @param relativepath String, for example "vacation/2010"
     */
    public void setRelativepath(String relativepath)
    {
        this.relativepath = relativepath;
    }

    /**
     * Indicates when the picture was taken. This is, if possible, taken
     * from the information contained in the picture and is usually stored
     * by the device that took the picture. Does not automatically retrieve
     * this from the picture. This is done when the picture is imported.
     *
     * @return the Date, or null if not set.
     */
    public Date getTaken()
    {
        return taken;
    }

    /**
     * Sets the date at which the picture was taken.
     *
     * @param taken the new date when the picture was taken.
     */
    public void setTaken(Date taken)
    {
        this.taken = taken;
    }

    /**
     * Indicates in what location the picture resides (amongst other pictures).
     *
     * @return Location of the picture, contains an absolute path.
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Sets the location.
     *
     * @param location the new location of this photograph.
     */
    public void setLocation(Location location)
    {
        this.location = location;
    }

    /**
     * Returns the tags associated with this picture.
     *
     * @return a collection of tags.
     */
    @XmlTransient
    public Collection<Tag> getTagCollection()
    {
        return tagCollection;
    }

    /**
     * Provides a full absolute file path to this file.
     *
     * @return an absolute path to the file, for example
     * "/home/mrbear/gallery/vacation/2012/DSCN00244.JPG".
     */
    @XmlTransient
    public String getFullPath()
    {
        return getLocation().getFilepath() + File.separator + getRelativepath() + File.separator + getFilename();
    }

    /**
     * Sets the tags collection.
     *
     * @param tagCollection a new tags collection.
     */
    public void setTagCollection(Collection<Tag> tagCollection)
    {
        this.tagCollection = tagCollection;
    }

    /**
     * Returns the size of the file in bytes.
     *
     * @return Long containing the filesize in bytes.
     */
    public Long getFilesize()
    {
        return filesize;
    }

    /**
     * Sets the filesize of the file in bytes. Only really used when creating a
     * new
     * photograph.
     *
     * @param filesize a new filesize.
     */
    public void setFilesize(Long filesize)
    {
        this.filesize = filesize;
    }

    /**
     * Returns a hash string representing the file. This makes it easier to
     * compare this file
     * to other files, without having to do a byte-wise compare of file
     * contents. Slight risk
     * of double hashes, but we don't mind.
     *
     * @return String containing a hash.
     */
    public String getHashstring()
    {
        return hashstring;
    }

    /**
     * Sets the hashstring.
     *
     * @param hashstring the new hash string.
     */
    public void setHashstring(String hashstring)
    {
        this.hashstring = hashstring;
    }

    /**
     * Indicates if an angle has been stored in the entity.
     *
     * @return true if no angle is stored.
     */
    public boolean hasNoAngle()
    {
        return angle == null;
    }

    /**
     * Indicates the angle at which the picture was taken. This is, if possible,
     * stored
     * in the information contained in the picture and is usually stored
     * by the device that took the picture. If not set for this picture, will
     * attempt
     * to retrieve it from the metadata.
     *
     * @return an ImageAngle or null if unable to determine.
     */
    public ImageAngle getAngle() throws ImageProcessingException, IOException, MetadataException
    {
        Logger.getLogger(Photograph.class.getName()).log(Level.FINE, "getAngle {0}", angle);
        if (angle == null && ImageOperations.isImage(getFilename()))
        {
            ImageAngle result = ImageOperations.getAngle(new File(getFullPath()));
            if (result != null)
            {
                angle = result.getAngle();
            }
            Logger.getLogger(Photograph.class.getName()).log(Level.FINE, "getAngle 1 returns {0}", result);
            return result;
        }
        ImageAngle result = ImageAngle.getAngle(angle);
        Logger.getLogger(Photograph.class.getName()).log(Level.FINE, "getAngle 2 returns {0}", result);
        return result;
    }

    /**
     * Sets the angle of the photograph.
     *
     * @param angle the new angle, may be null.
     */
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
