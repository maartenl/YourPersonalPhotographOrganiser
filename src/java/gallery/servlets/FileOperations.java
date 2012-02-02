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

import gallery.images.ImageOperations;
import gallery.enums.ImageSizeEnum;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;

/**
 *
 * @author maartenl
 */
public class FileOperations
{

    /**
     * Writes an image to the outputStream that has been scaled appropriately.
     * @param photo original photograph
     * @param outputStream the outputstream to write the image to
     * @param size the size of the image, can be "thumb", "medium" or the default.
     * @throws IOException thrown when the file cannot be access in some way.
     */
    public static void outputImage(File file, ServletOutputStream outputStream, String size) throws IOException
    {
        BufferedImage image = ImageIO.read(file);
        if (image == null)
        {
            throw new IOException("Image is empty");
        }
        if (size == null)
        {
            ImageIO.write(image, "jpg", outputStream);
            return;
        }
        // JDK7: the new switch statement
        switch (size)
        {
            case "thumb":
                image = ImageOperations.scaleImage(image, ImageSizeEnum.THUMB.getWidth(), ImageSizeEnum.THUMB.getHeight());
                ImageIO.write(image, "jpg", outputStream);
                break;
            case "medium":
                image = ImageOperations.scaleImage(image, ImageSizeEnum.MEDIUM.getWidth(), ImageSizeEnum.MEDIUM.getHeight());
                ImageIO.write(image, "jpg", outputStream);
                break;
            case "large":
                image = ImageOperations.scaleImage(image, ImageSizeEnum.LARGE.getWidth(), ImageSizeEnum.LARGE.getHeight());
                ImageIO.write(image, "jpg", outputStream);
                break;
            default:
                ImageIO.write(image, "jpg", outputStream);
                break;
        }
    }

    /**
     * Returns a string containing a 512bit hash in hexadecimal of the file.
     * @param file the file to hash
     * @return String with hash
     * @throws NoSuchAlgorithmException if SHA-512 is not supported (should be, though)
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String computeHash(File file) throws NoSuchAlgorithmException, FileNotFoundException, IOException
    {
        // Obtain a message digest object.
        MessageDigest md = MessageDigest.getInstance("SHA-512");

        // Calculate the digest for the given file.
        DigestInputStream in = new DigestInputStream(
                new FileInputStream(file), md);
        byte[] buffer = new byte[8192];
        while (in.read(buffer) != -1); // empty statement, we're just computing hashes here.
        byte[] hash = md.digest();
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < hash.length; i++)
        {
            hexString.append(Integer.toHexString(0xFF & hash[i]));
        }
        return hexString.toString();
    }
}
