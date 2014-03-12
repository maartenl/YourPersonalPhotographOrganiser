/*
 * Copyright (C) 2014 maartenl
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

package gallery.admin.util;

import java.util.ResourceBundle;

/**
 * A GalleryException, a specific Exception that can be used to 
 * validate things. Primarily good for JSF.
 * @author maartenl
 */
public class GalleryException extends Exception
{

    /**
     * Creates a GalleryException. The message is supposed
     * to be a key to a value in the bundle.properties file.
     * @param message 
     */
    public GalleryException(String message)
    {
        super(ResourceBundle.getBundle("/bundle").getString(message));       
    }
    
}
