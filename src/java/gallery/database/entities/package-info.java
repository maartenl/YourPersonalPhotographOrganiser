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

/**
 * <p>Provides the database Entities used by an ORM, in our case, Hibernate.</p>
 * <p><img src="../../../images/package-info.png"/></p>
 *
 * @startuml
 * Gallery "1" *-- "many" GalleryPhotograph : contains
 * Gallery "1" *-- "many" Gallery : contains
 * GalleryPhotograph "1" *-- "1" Photograph : contains
 * Photograph "many" *-- "1" Location : contains
 * GalleryPhotograph "1" *-- "many" Comment : contains
 * Photograph "1" *-- "many" Tag : contains
 * class Gallery {
 *    -BigInteger id
 *    -String name
 *    -String description
 *    -Date creation_date
 *    -BigInteger parent_id
 *    -BigInteger highlight
 *    -int sortorder
 * }
 * class GalleryPhotograph {
 *    -BigInteger id
 *    -String name
 *    -String description
 *    -BigInteger gallery_id
 *    -BigInteger photograph_id
 *    -int sortorder
 * }
 * class Location {
 *    -BigInteger id
 *    -String filepath
 * }
 * class Comment {
 *    -BigInteger id
 *    -BigInteger galleryphotograph_id
 *    -String author
 *    -Date submitted
 *    -String comment
 * }
 * class Tag {
 *    -String tagname
 *    -BigInteger photograph_id
 * }
 * class Photograph {
 *    -BigInteger id
 *    -BigInteger location_id
 *    -String filename
 *    -String relativepath
 *    -Date taken
 *    -String hashstring
 *    -BigInteger filesize
 *    -int angle
 * }
 * class Log {
 *    -BigInteger id
 *    -Date jobdate
 *    -String joblog
 * }
 * @enduml
 */
package gallery.database.entities;
