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
package gallery.images;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author maartenl
 */
public class ImageOperations
{

    private ImageOperations()
    {
        // defeat instantiation
    }

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

    public static List<PhotoMetadata> getMetadata(File jpegFile) throws ImageProcessingException, IOException
    {
        // JDK7 : empty diamond
        List<PhotoMetadata> metadatas = new ArrayList<>();
        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
        for (Directory directory : metadata.getDirectories())
        {
            PhotoMetadata mymetadata = new PhotoMetadata();
            mymetadata.name = directory.getName();

            mymetadata.taken = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            for (Tag tag : directory.getTags())
            {
                mymetadata.tags.add(new PhotoTag(tag.getTagName(),
                        tag.getDescription()));
            }
            metadatas.add(mymetadata);
        }
        return metadatas;
    }

    /**
     * Returns the date and time when the photograph was taken, null if unable to retrieve.
     * @param jpegFile
     * @return null or Date
     * @throws ImageProcessingException
     * @throws IOException
     */
    public static Date getDateTimeTaken(File jpegFile) throws ImageProcessingException, IOException
    {
        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
        Directory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
        return directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
    }

}
