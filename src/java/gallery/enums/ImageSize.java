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
package gallery.enums;

/**
 *
 * <p>Indicates the different sizes that are possible in the displaying
 * of pictures. BIG being un-scaled.</p>
 * <img src="../../images/ImageSize.png"/>
 * @author maartenl
 *
 * @startuml
 * "java.lang.Enum<ImageSize>" <|-- enum ImageSize
 * ImageSize : +BIG
 * ImageSize : +LARGE
 * ImageSize : +MEDIUM
 * ImageSize : +THUMB
 * ImageSize : -maxHeight
 * ImageSize : -maxWidth
 * ImageSize : +getHeight() : Integer
 * ImageSize : +getWidth() : Integer
 * @enduml
 */
public enum ImageSize
{

    /**
     * Indicates the pictures is not to be modified, but shown
     * in its true dimensions.
     */
    BIG(),
    MEDIUM(350, 350), THUMB(100, 100), LARGE(1024,1024);

    private Integer maxHeight;
    private Integer maxWidth;

    private ImageSize()
    {
    }

    private ImageSize(Integer width, Integer height)
    {
        this.maxHeight = height;
        this.maxWidth = width;
    }

    /**
     * Retrieves the height of the picture.
     * @return Integer indicating the height in pixels.
     */
    public Integer getHeight()
    {
        return maxHeight;
    }

     /**
      * Retrieves the width of the picture.
      * @return Integer indicating the width in pixels.
      */
    public Integer getWidth()
    {
        return maxWidth;
    }
}
