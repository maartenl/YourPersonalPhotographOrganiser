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
 * <p>Indicates the rotation required for a picture, to display it properly.
 * Also indicates the angle at which the original pictures were taken,
 * if supported by the camera/mobile/device.</p>
 * <img src="../../images/ImageAngle.png"/>
 * @author maartenl
 *
 * @startuml
 * "java.lang.Enum<ImageAngle>" <|-- enum ImageAngle
 * ImageAngle : +NORMAL
 * ImageAngle : +UPSIDE_DOWN
 * ImageAngle : +NINETYDEGREE_CLOCKWISE
 * ImageAngle : +NINETYDEGREE_COUNTER_CLOCKWISE
 * ImageAngle : +getAngle() : Integer
 * ImageAngle : +toString() : String
 * ImageAngle : +getAngle(angle : int) : ImageAngle
 * @enduml
 * @see http://www.impulseadventure.com/photo/exif-orientation.html
 */
public enum ImageAngle
{

    /**
     * Normal angle. (1)
     * No rotation is required.
     * <pre>        ___
     *  _____/___|
     * /         |
     * |    O    |
     * |_________|</pre>
     */
    NORMAL(1, "Top/Left side"),
    /**
     * Not used. (2)
     */
    TOP_RIGHTSIDE(2, "Top/Right side"),
    /**
     * Photocamera was upside down. (3)
     * 180 degrees rotation either way.
     * <pre>  _________
     * |         |
     * |    O    |
     * |_________|
     * \___|
     * </pre>
     */
    UPSIDE_DOWN(3, "Bottom/Right side"),
    /**
     * Not used.
     */
    BOTTOM_LEFTSIDE(4, "Bottom/Left side"),
    /**
     * Not used.
     */
    LEFTSIDE_TOP(5, "Left side/Top"),
    /**
     * Photocamera was turned 90 degrees counter clockwise originally. (6)
     * Rotation 90 degrees clockwise required.
     * <pre>   _______
     *  |       |
     *  |       |
     * /|   O   |
     * ||       |
     * |________|</pre>
     */
    NINETYDEGREE_CLOCKWISE(6, "Right side/Top"),
    /**
     * Not used.
     */
    RIGHTSIDE_BOTTOM(7, "Right side/Bottom"),
    /**
     * Photocamera was turned 90 degrees clockwise originally. (8)
     * Rotation 90 degrees counter clockwise required.
     * <pre>   _______
     *  |       |
     *  |       |
     *  |   O   |\
     *  |       ||
     *  |________|</pre>
     */
    NINETYDEGREE_COUNTER_CLOCKWISE(8, "Left side/Bottom");

    private Integer angle;
    private String description;

    private ImageAngle()
    {
    }

    private ImageAngle(Integer angle, String description)
    {
        this.angle = angle;
                this.description = description;
    }

    public String toString()
    {
        return description;
    }

    /**
     * Retrieves the angle of the picture as stored in image information.
     * @return Integer indicating the angle.
     */
    public Integer getAngle()
    {
        return angle;
    }

    /**
     * Retrieves the enum corresponding to the image angle as stored
     * in the picture.
     * @return ImageAngle indicating the angle. Will return null
     * if angle not found.
     */
    public static ImageAngle getAngle(Integer angle)
    {
        if (angle == null)
        {
            return null;
        }
        for (ImageAngle imageAngle : ImageAngle.values())
        {
            if (angle.equals(imageAngle.getAngle()))
            {
                return imageAngle;
            }
        }
        return null;
    }
}

