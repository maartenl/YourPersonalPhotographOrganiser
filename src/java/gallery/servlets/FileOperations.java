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

import gallery.enums.ImageAngle;
import gallery.images.ImageOperations;
import gallery.enums.ImageSize;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;

/**
 * Operations on files.
 * @author maartenl
 */
public class FileOperations
{

    /**
     * Reads from inputStream and dumps it to the outputStream. Does not close input nor outputstream.
     * @param inputStream the input
     * @param outputStream the output
     * @throws IOException an error occurred reading/writing to streams
     */
    public static void dumpFile(InputStream inputStream, ServletOutputStream outputStream) throws IOException
    {
        byte buf[] = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) > 0)
        {
            outputStream.write(buf, 0, len);
        }
    }

    /**
     * Defeat instantiation
     */
    private FileOperations()
    {
    }

    /**
     * Writes an image to the outputStream that has been scaled appropriately and angled.
     * @param photo original photograph, can only process images of type <ul><li>JPEG</li><li>TIFF</li><li>Camera Raw (NEF/CRW/CR2/...) </li></ul>
     * @param outputStream the outputstream to write the image to
     * @param size the size of the image, can be "thumb", "medium" or the default.
     * @throws IOException thrown when the file cannot be access in some way.
     */
    public static void outputImage(File file, ServletOutputStream outputStream, String size, ImageAngle angle) throws IOException
    {
        if (!ImageOperations.isImage(file.getCanonicalPath()))
        {
            System.out.println("outputImage not an image!");
            return;
        }
        BufferedImage image = null;
        try
        {
            image = ImageIO.read(file);
        }
        catch (javax.imageio.IIOException e)
        {
            throw new RuntimeException("Error reading file " + file.getCanonicalPath(), e);
        }

        if (image == null)
        {
            throw new IOException("Image " + file.getAbsolutePath() + " is empty");
        }
        if (size == null)
        {
            ImageIO.write(ImageOperations.rotate(image, angle), "jpg", outputStream);
            return;
        }
        ImageSize imageSize = null;        // JDK7: the new switch statement
        switch (size)
        {
            case "thumb":
                imageSize = ImageSize.THUMB;
                break;
            case "medium":
                imageSize = ImageSize.MEDIUM;
                break;
            case "large":
                imageSize = ImageSize.LARGE;
                break;
        }
        image = ImageOperations.scaleImage(image, imageSize.getWidth(), imageSize.getHeight());
        ImageIO.write(ImageOperations.rotate(image, angle), "jpg", outputStream);
    }

    /**
     * Returns a string containing a 512bit hash in hexadecimal of the file.
     * @param file the file to hash
     * @return String with hash
     * @throws NoSuchAlgorithmException if SHA-512 is not supported (should be, though)
     * @throws FileNotFoundException if file is not found on the filesystem
     * @throws IOException if an error occurred whilst reading the file.
     */
    public static String computeHash(File file) throws NoSuchAlgorithmException, FileNotFoundException, IOException
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
