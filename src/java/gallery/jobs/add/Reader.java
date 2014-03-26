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
package gallery.jobs.add;

import java.io.Serializable;
import javax.batch.api.chunk.ItemReader;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author maartenl
 */
@Named("addPhotographReader")
@ApplicationScoped
public class Reader implements ItemReader
{

    private String[] stuff;

    private int index = 0;

    private String checkpoint = null;

    @Override
    public void open(Serializable checkpoint) throws Exception
    {
        if (checkpoint == null)
        {
            System.out.println("addPhotographReader open start");
        } else
        {
            System.out.println("addPhotographReader open restart");
        }
        stuff = new String[]
        {
            "Maarten", "Jasper", "Jeanne", "Rini", "Frank", "Francien", "Erik", "Marieke", "Freek", "Ralf", null
        };
    }

    @Override
    public void close() throws Exception
    {
        System.out.println("addPhotographReader close");
    }

    @Override
    public Object readItem() throws Exception
    {
        System.out.println("addPhotographReader readItem");
        checkpoint = stuff[index++];
        return checkpoint;
    }

    @Override
    public Serializable checkpointInfo() throws Exception
    {
        return checkpoint;
    }

}
