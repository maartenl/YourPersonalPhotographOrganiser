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
package gallery.jobs.verify;

import gallery.beans.LogBean;
import gallery.database.entities.Log.LogLevel;
import gallery.database.entities.Photograph;
import gallery.servlets.FileOperations;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.logging.Logger;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * <p>
 * Verifies an existing photograph, by retrieving its data and checking
 * the file on the filesystem.</p><p>
 * <img src="../../../images/Processor-verify.png"/></p>
 * @startuml Processor-verify.png
 * (*) --> "Initialisation"
 * --> "checkFileExist"
 * --> "checkFileisFile"
 * --> "checkFileReadable"
 * --> "checkSameSize"
 * --> "checkSameHash"
 * --> (*)
 *
 * @enduml
 * @author maartenl
 */
@Named("verifyPhotographProcessor")
@ApplicationScoped
public class Processor implements ItemProcessor
{

    private static final Logger logger = Logger.getLogger(Processor.class.getName());

    @EJB
    private LogBean logBean;

    @Inject
    private JobContext jobContext;

    @Override
    public Object processItem(Object item) throws Exception
    {
        logger.entering(this.getClass().getName(), "verifyPhotographProcessor processItem " + item);
        // return null; > do not process item
        if (item == null)
        {
            return null;
        }
        Photograph photograph = (Photograph) item;
        // assemble full path
        Path path = FileSystems.getDefault().getPath(photograph.getFullPath());
        // verify that the file exists
        if (!path.toFile().exists())
        {
            logBean.createLog("verifyPhotograph", "Photograph " + photograph.getId() + ": File " + path + " does not exist.", null, LogLevel.WARNING);
            return null;
        }
        // verify if file is a file
        if (!path.toFile().isFile())
        {
            logBean.createLog("verifyPhotograph", "Photograph " + photograph.getId() + ": File " + path + " is not a file.", null, LogLevel.WARNING);
            return null;
        }
        // verify if file is readable
        if (!path.toFile().canRead())
        {
            logBean.createLog("verifyPhotograph", "Photograph " + photograph.getId() + ": File " + path + " cannot be read.", null, LogLevel.WARNING);
            return null;
        }
        // verify the same file fileSize
        final Long fileSize = path.toFile().length();
        final Long databaseSize = photograph.getFilesize();
        if (!fileSize.equals(databaseSize))
        {
            logBean.createLog("verifyPhotograph", "Photograph " + photograph.getId() + ": File " + path + " wrong size.", "File has size " + fileSize + ", but we were expecting a size of " + databaseSize, LogLevel.WARNING);
            return null;
        }
        // verify the hash
        final String fileHash = FileOperations.computeHash(path.toFile());
        final String databaseHash = photograph.getHashstring();
        if (!fileHash.equals(databaseHash))
        {
            logBean.createLog("verifyPhotograph", "Photograph " + photograph.getId() + ": File " + path + " wrong hash.", "File has hash " + fileHash + ", but we were expecting the hash " + databaseHash, LogLevel.WARNING);
            return null;
        }

        return item;
    }

}
