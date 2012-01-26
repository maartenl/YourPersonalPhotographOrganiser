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
package gallery.servlets;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

/**
 *
 * @author maartenl
 */
public class ImageOperations
{

    /**
     * Scale an image down to a new size. Keeps ratio.
     * @param originalImage the original image
     * @param newWidth the new width
     * @param newHeight the new height
     * @return a new image that is at most newWidth
     */
    public static BufferedImage scaleImage(BufferedImage originalImage, int newWidth, int newHeight)
    {
        int height = originalImage.getHeight();
        int width = originalImage.getWidth();
        double newRatio = (newWidth + 0.0) / (newHeight + 0.0);
        double ratio = (width + 0.0) / (height + 0.0);
        if (ratio > newRatio)
        {
            // original picture is larger
            // take newWidth
            newHeight = (int) Math.round((newWidth + 0.0) / ratio);
        } else
        {
            // original picture is taller
            // take newHeight
            newWidth = (int) Math.round((newHeight + 0.0) * ratio);
        }
        int type = (originalImage.getTransparency() == Transparency.OPAQUE)
                ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage tmp = new BufferedImage(newWidth, newHeight, type);
        Graphics2D g2 = tmp.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2.dispose();
        return tmp;
    }

}
