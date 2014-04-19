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
package gallery.util;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author maartenl
 */
@XmlRootElement
public class TagCount
{

    private String text;

    private long weight;

    public TagCount(String text, long weight)
    {
        this.text = text;
        this.weight = weight;
    }

    public TagCount()
    {
        // empty constructor for JAXB.
    }

    /**
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * @return the weight
     */
    public long getWeight()
    {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(long weight)
    {
        this.weight = weight;
    }

}
