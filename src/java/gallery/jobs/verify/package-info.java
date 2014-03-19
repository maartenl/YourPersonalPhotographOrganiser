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
/**
 * <p>
 * Provides the batch artifacts for the job VerifyPhotographs.</p>
 * <p>
 * <img src="../../../images/package-info-verify.png"/></p>
 *
 * @startuml package-info-verify.png
 * participant Alice
 * participant VerifyPhotographListener
 * participant verifyPhotographReader
 * participant verifyPhotographWriter
 * participant verifyPhotographProcessor
 * Alice -> VerifyPhotographListener: beforeJob
 * Alice -> verifyPhotographReader: open
 * Alice -> verifyPhotographWriter: open
 * Alice -> verifyPhotographReader: readItem
 * Alice -> verifyPhotographProcessor: processItem
 * Alice -> verifyPhotographReader: readItem
 * Alice -> verifyPhotographProcessor: processItem
 * Alice -> verifyPhotographReader: readItem
 * Alice -> verifyPhotographProcessor: processItem
 * Alice -> verifyPhotographReader: readItem
 * Alice -> verifyPhotographProcessor: processItem
 * Alice -> verifyPhotographWriter: writeItems
 * Alice -> verifyPhotographWriter: checkpointInfo
 * Alice -> verifyPhotographWriter: ...
 * Alice -> verifyPhotographReader: close
 * Alice -> verifyPhotographWriter: close
 * Alice -> VerifyPhotographListener: afterJob
 *
 * @enduml
 */
package gallery.jobs.verify;
