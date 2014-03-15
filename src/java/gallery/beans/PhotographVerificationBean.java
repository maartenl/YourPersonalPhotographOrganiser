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
package gallery.beans;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * @author maartenl
 */
@MessageDriven(activationConfig =
{
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/VerificationQueue")
})
public class PhotographVerificationBean implements MessageListener
{

    private static final Logger logger = Logger.getLogger(PhotographVerificationBean.class.getName());

    @Resource
    private MessageDrivenContext mdc;

    public PhotographVerificationBean()
    {
        logger.entering("PhotographVerificationBean", "PhotographVerificationBean constructor");
    }

    @Override
    public void onMessage(Message message)
    {
        logger.entering(this.getClass().getName(), "onMessage " + message);
        try
        {
            if (message instanceof TextMessage)
            {
                logger.log(Level.INFO,
                        "MESSAGE BEAN: Message received: {0}",
                        message.getBody(String.class));
            } else
            {
                logger.log(Level.WARNING,
                        "Message of wrong type: {0}",
                        message.getClass().getName());
            }
        } catch (JMSException e)
        {
            logger.throwing(this.getClass().getName(), "onMessage", e);
            mdc.setRollbackOnly();
        }
        logger.exiting(this.getClass().getName(), "onMessage ");
    }

}
