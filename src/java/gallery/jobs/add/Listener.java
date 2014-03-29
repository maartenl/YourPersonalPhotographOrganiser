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

import java.util.logging.Logger;
import javax.batch.api.listener.JobListener;
import javax.batch.api.listener.StepListener;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author maartenl
 */
@Named("addPhotographListener")
@ApplicationScoped
public class Listener implements JobListener, StepListener
{

    private static final Logger logger = Logger.getLogger(Listener.class.getName());

    @Override
    public void beforeJob() throws Exception
    {
        logger.info("Job Started: addPhotograph");
    }

    @Override
    public void afterJob() throws Exception
    {
        logger.info("Job Ended: addPhotograph");
    }

    @Override
    public void beforeStep() throws Exception
    {
        logger.info("Step Started: addPhotograph");
    }

    @Override
    public void afterStep() throws Exception
    {
        logger.info("Step Ended: addPhotograph");
    }

}
