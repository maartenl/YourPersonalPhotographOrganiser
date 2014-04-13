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
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * For storing log messages. In our case these are mostly concerned with the
 * logging of Batch operations.
 *
 * @author maartenl
 */
@Entity
@Table(name = "Log")
@XmlRootElement
@NamedQueries(
        {
            @NamedQuery(name = "Log.findAll", query = "SELECT l FROM Log l"),
            @NamedQuery(name = "Log.findById", query = "SELECT l FROM Log l WHERE l.id = :id"),
            @NamedQuery(name = "Log.deleteAll", query = "DELETE FROM Log l")
        })
public class Log implements Serializable
{

    public enum LogLevel
    {

        INFO, WARNING, ERROR
    }

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 512)
    @Column(name = "source")
    private String source;
    @Size(max = 512)
    @Column(name = "message")
    private String message;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Lob
    @Size(max = 65535)
    @Column(name = "description")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "loglevel", nullable = false, updatable = false)
    private LogLevel logLevel;

    public Log(String source, String message, String description, LogLevel logLevel)
    {
        this.creationDate = new Date();
        this.source = source;
        this.message = message;
        this.description = description;
        this.logLevel = logLevel;
    }

    public Log()
    {
    }

    public Log(Long id)
    {
        this.id = id;
    }

    /**
     * Unique identified/primary key.
     *
     * @return Long containing the identifier.
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Sets the unique identifier/primary key.
     *
     * @param id the new primary key.
     */
    public void setId(Long id)
    {
        this.id = id;
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
        if (!(object instanceof Log))
        {
            return false;
        }
        Log other = (Log) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "gallery.database.entities.Log[ id=" + id + " ]";
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    /**
     * @return the logLevel
     */
    public LogLevel getLogLevel()
    {
        return logLevel;
    }

    /**
     * @param logLevel the logLevel to set
     */
    public void setLogLevel(LogLevel logLevel)
    {
        this.logLevel = logLevel;
    }
}
