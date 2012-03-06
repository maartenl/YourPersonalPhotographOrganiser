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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Comments provided by people on photographs in a gallery.
 * @author maartenl
 */
@Entity
@Table(name = "Comment")
@XmlRootElement
@NamedQueries(
{
    @NamedQuery(name = "Comment.findAll", query = "SELECT c FROM Comment c"),
    @NamedQuery(name = "Comment.findById", query = "SELECT c FROM Comment c WHERE c.id = :id"),
    @NamedQuery(name = "Comment.findByAuthor", query = "SELECT c FROM Comment c WHERE c.author = :author"),
    @NamedQuery(name = "Comment.findBySubmitted", query = "SELECT c FROM Comment c WHERE c.submitted = :submitted")
})
public class Comment implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    // @NotNull
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "author")
    private String author;
    @Basic(optional = false)
    @NotNull
    @Column(name = "submitted")
    @Temporal(TemporalType.TIMESTAMP)
    private Date submitted;
    @Lob
    @Size(max = 65535)
    @Column(name = "comment")
    private String comment;
    @JoinColumn(name = "galleryphotograph_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private GalleryPhotograph galleryphotograph;

    public Comment()
    {
    }

    public Comment(Long id)
    {
        this.id = id;
    }

    public Comment(Long id, Date submitted)
    {
        this.id = id;
        this.submitted = submitted;
    }

    /**
     * Unique number, primary key, identification of a comment.
     * @return Long containing identifier.
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Sets the identifier
     * @param id the unique identifier, Long.
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Author of the comment.
     * @return String containing the name of the Author.
     */
    public String getAuthor()
    {
        return author;
    }

    /**
     * Sets the author of the comment.
     * @param author the new author.
     */
    public void setAuthor(String author)
    {
        this.author = author;
    }

    /**
     * date/time when the comment was submitted.
     * @return Date/time when the comment was submitted.
     */
    public Date getSubmitted()
    {
        return submitted;
    }

    /**
     * Set the date/time when the comment was submitted.
     * @param submitted the new Date/time.
     */
    public void setSubmitted(Date submitted)
    {
        this.submitted = submitted;
    }

    /**
     * The actual comment.
     * @return String containing the comment.
     */
    public String getComment()
    {
        return comment;
    }

    /**
     * Sets the comment. May be large.
     * @param comment the (new) comment
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    /**
     * The photograph in the gallery, on which this comment applies.
     * @return the photograph in the gallery
     */
    public GalleryPhotograph getGalleryphotograph()
    {
        return galleryphotograph;
    }

    /**
     * Sets the photograph to which this comment applies.
     * @param galleryphotograph the photograph in the gallery
     */
    public void setGalleryphotograph(GalleryPhotograph galleryphotograph)
    {
        this.galleryphotograph = galleryphotograph;
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
        if (!(object instanceof Comment))
        {
            return false;
        }
        Comment other = (Comment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "gallery.database.entities.Comment[ id=" + id + " author=" + author + " comment=" + comment + "]";
    }
}
