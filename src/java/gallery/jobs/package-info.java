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
 * Top package containing the jobs in subpackages.</p>
 * <p>
 * <img src="../../../images/package-info-jobs.png"/></p>
 * <p>
 * In the example/picture above, it shows that the item-count is set to three,
 * even
 * though
 * in the code it is 1. It serves as a good example of the behaviour,
 * better then when item-count of 1 would be diagrammed.</p>
 *
 * @startuml package-info-jobs.png
 * participant JavaBatch
 * participant verifyPhotographListener
 * participant verifyPhotographReader
 * participant verifyPhotographWriter
 * participant verifyPhotographProcessor
 * participant Transaction
 *
 * JavaBatch -> verifyPhotographListener: beforeJob
 * JavaBatch -> verifyPhotographListener: beforeStep
 * JavaBatch -> verifyPhotographReader: open
 * JavaBatch -> verifyPhotographWriter: open
 * JavaBatch -> Transaction: begin
 * JavaBatch -> verifyPhotographReader: readItem
 * JavaBatch <-- verifyPhotographReader: item
 * JavaBatch -> verifyPhotographProcessor: processItem(item)
 * JavaBatch <- verifyPhotographProcessor: processed item
 * JavaBatch -> verifyPhotographReader: readItem
 * JavaBatch <-- verifyPhotographReader: item
 * JavaBatch -> verifyPhotographProcessor: processItem(item)
 * JavaBatch <-- verifyPhotographProcessor: processed item
 * JavaBatch -> verifyPhotographReader: readItem
 * JavaBatch <-- verifyPhotographReader: item
 * JavaBatch -> verifyPhotographProcessor: processItem(item)
 * JavaBatch <-- verifyPhotographProcessor: processed item
 * JavaBatch -> verifyPhotographWriter: writeItems(processed items)
 * JavaBatch -> Transaction: commit
 * JavaBatch -> verifyPhotographWriter: checkpointInfo
 * JavaBatch -> verifyPhotographWriter: ...
 * JavaBatch -> verifyPhotographReader: close
 * JavaBatch -> verifyPhotographWriter: close
 * JavaBatch -> verifyPhotographListener: afterStep
 * JavaBatch -> verifyPhotographListener: afterJob
 *
 * @enduml
 *
 * @startuml package-info-hierarchy.png
 * interface javax.batch.api.listener.JobListener
 * interface javax.batch.api.listener.StepListener
 * interface javax.batch.api.chunk.ItemProcessor
 * interface javax.batch.api.chunk.ItemReader
 * interface javax.batch.api.chunk.ItemWriter
 * javax.batch.api.listener.JobListener <|-- gallery.jobs.verify.Listener
 * javax.batch.api.listener.StepListener <|-- gallery.jobs.verify.Listener
 * javax.batch.api.chunk.ItemProcessor <|-- gallery.jobs.verify.Processor
 * javax.batch.api.chunk.ItemReader <|-- gallery.jobs.verify.Reader
 * javax.batch.api.chunk.ItemWriter <|-- gallery.jobs.verify.Writer
 *
 * @enduml
 *
 */
package gallery.jobs;
