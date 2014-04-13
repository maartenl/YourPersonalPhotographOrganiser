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
package gallery.jobs;

import gallery.beans.LogBean;
import gallery.database.entities.Log;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.batch.api.listener.JobListener;
import javax.batch.api.listener.StepListener;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author maartenl
 */
@ApplicationScoped
public abstract class Listener implements JobListener, StepListener
{

    private static final Logger logger = Logger.getLogger(Listener.class.getName());

    @EJB
    private LogBean logBean;

    protected abstract String getName();

    @Override
    public void beforeJob() throws Exception
    {
        logger.log(Level.INFO, "Job Started: {0}", getName());
        logBean.createLog(getName(), "Job Started: " + getName(), null, Log.LogLevel.INFO);
    }

    @Override
    public void afterJob() throws Exception
    {
        logger.log(Level.INFO, "Job Ended: {0}", getName());
        logBean.createLog(getName(), "Job Ended: " + getName(), null, Log.LogLevel.INFO);
    }

    @Override
    public void beforeStep() throws Exception
    {
        logger.log(Level.INFO, "Step Started: {0}", getName());
        logBean.createLog(getName(), "Step Started: " + getName(), null, Log.LogLevel.INFO);
    }

    @Override
    public void afterStep() throws Exception
    {
        logger.log(Level.INFO, "Step Ended: {0}", getName());
        logBean.createLog(getName(), "Step Ended: " + getName(), null, Log.LogLevel.INFO);
    }

}
