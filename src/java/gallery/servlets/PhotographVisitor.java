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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The photograph visitor. JDK7: derived from the JDK7 class containing
 * file tree visiting. Simply adds files found to a list.
 * @author maartenl
 */
public class PhotographVisitor implements FileVisitor<Path>
{

    private List<Path> fileList = new ArrayList<>();

    public List<Path> getFileList()
    {
        return Collections.unmodifiableList(fileList);
    }

    public PhotographVisitor()
    {
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
    {
        Logger.getLogger(PhotographVisitor.class.getName()).entering(PhotographVisitor.class.getName(), "preVisitDirectory", dir);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
    {
        PathMatcher matcher =
                FileSystems.getDefault().getPathMatcher("glob:**.highlight.*");
        if (matcher.matches(file))
        {
            Logger.getLogger(PhotographVisitor.class.getName()).log(Level.FINEST, "visitFile ignored, contains highlight {0}", file);
            return FileVisitResult.CONTINUE;
        }
        matcher =
                FileSystems.getDefault().getPathMatcher("glob:**.thumb.*");
        if (matcher.matches(file))
        {
            Logger.getLogger(PhotographVisitor.class.getName()).log(Level.FINEST, "visitFile ignored, contains thumb {0}", file);
            return FileVisitResult.CONTINUE;
        }
        matcher =
                FileSystems.getDefault().getPathMatcher("glob:**.sized.*");
        if (matcher.matches(file))
        {
            Logger.getLogger(PhotographVisitor.class.getName()).log(Level.FINEST, "visitFile ignored, contains sized {0}", file);
            return FileVisitResult.CONTINUE;
        }
        matcher =
                FileSystems.getDefault().getPathMatcher("glob:**.{jpg,jpeg,gif,png,bmp,tiff,avi,JPG,JPEG,GIF,PNG,BMP,TIFF,AVI}");
        if (!matcher.matches(file))
        {
            Logger.getLogger(PhotographVisitor.class.getName()).log(Level.FINEST, "visitFile ignored, not an image {0}", file);
            return FileVisitResult.CONTINUE;
        }
        if (!attrs.isRegularFile())
        {
            Logger.getLogger(PhotographVisitor.class.getName()).log(Level.FINEST, "visitFile ignored, not a regular file {0}", file);
            return FileVisitResult.CONTINUE;
        }
        if (attrs.size() == 0)
        {
            Logger.getLogger(PhotographVisitor.class.getName()).log(Level.FINEST, "visitFile ignored, size is 0 {0}", file);
            return FileVisitResult.CONTINUE;
        }
        Logger.getLogger(PhotographVisitor.class.getName()).log(Level.FINEST, "visitFile file found", file);
        fileList.add(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
    {
        Logger.getLogger(PhotographVisitor.class.getName()).entering(PhotographVisitor.class.getName(), "visitFileFailed", file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
    {
        Logger.getLogger(PhotographVisitor.class.getName()).entering(PhotographVisitor.class.getName(), "postVisitDirectory", dir);
        return FileVisitResult.CONTINUE;
    }
}