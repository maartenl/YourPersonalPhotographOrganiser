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

import gallery.admin.util.GalleryException;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Location where a number of files, which are pictures, reside.
 * @author maartenl
 */
@Entity
@Table(name = "Location")
@XmlRootElement
@NamedQueries(
{
    @NamedQuery(name = "Location.findAll", query = "SELECT l FROM Location l"),
    @NamedQuery(name = "Location.findById", query = "SELECT l FROM Location l WHERE l.id = :id"),
    @NamedQuery(name = "Location.findByFilepath", query = "SELECT l FROM Location l WHERE l.filepath = :filepath")
})
public class Location implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 512)
    @Column(name = "filepath")
    private String filepath;

    public Location()
    {
    }

    public Location(Long id)
    {
        this.id = id;
    }

    /**
     * Unique identified/primary key.
     * @return Long containing the identifier.
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Sets the unique identifier/primary key.
     * @param id the new primary key.
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * The absolute file path correctly identifying the location on the filesystem. Should point to a directory.
     * @return String containing the file path.
     */
    public String getFilepath()
    {
        return filepath;
    }

    /**
     * Sets the file path.
     * @param filepath the new filepath.
     */
    public void setFilepath(String filepath)
    {
        this.filepath = filepath;
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
        if (!(object instanceof Location))
        {
            return false;
        }
        Location other = (Location) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    public void checkPath() throws GalleryException
    {
        final Path path = FileSystems.getDefault().getPath(filepath);
        if (!path.toFile().exists())
        {
            throw new GalleryException("path.does.not.exist");
        }
        if (!path.toFile().isDirectory())
        {
            throw new GalleryException("path.is.not.a.directory");
        }
        if (!path.toFile().canRead())
        {
            throw new GalleryException("path.is.unreadable");
        }
    }
    
    @Override
    public String toString()
    {
        return "gallery.database.entities.Location[ id=" + id + " ]";
    }

}
