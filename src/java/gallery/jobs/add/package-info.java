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
 * Provides the batch artifacts for the job addPhotographs.</p>
 * <p>
 * <img src="../../../images/package-info-add.png"/></p>
 * <p>
 * In the picture above, it shows that the item-count is set to three, even
 * though
 * in the code it is 1. It serves as a good example of the behaviour,
 * better then when item-count of 1 would be diagrammed.</p>
 * <p>
 * <img src="../../../images/package-info-add-hierarchy.png"/></p>
 *
 * @startuml package-info-add.png
 * participant JavaBatch
 * participant addPhotographListener
 * participant addPhotographReader
 * participant addPhotographWriter
 * participant addPhotographProcessor
 * participant Transaction
 *
 * JavaBatch -> addPhotographListener: beforeJob
 * JavaBatch -> addPhotographListener: beforeStep
 * JavaBatch -> addPhotographReader: open
 * JavaBatch -> addPhotographWriter: open
 * JavaBatch -> Transaction: begin
 * JavaBatch -> addPhotographReader: readItem
 * JavaBatch <-- addPhotographReader: item
 * JavaBatch -> addPhotographProcessor: processItem(item)
 * JavaBatch <- addPhotographProcessor: processed item
 * JavaBatch -> addPhotographReader: readItem
 * JavaBatch <-- addPhotographReader: item
 * JavaBatch -> addPhotographProcessor: processItem(item)
 * JavaBatch <-- addPhotographProcessor: processed item
 * JavaBatch -> addPhotographReader: readItem
 * JavaBatch <-- addPhotographReader: item
 * JavaBatch -> addPhotographProcessor: processItem(item)
 * JavaBatch <-- addPhotographProcessor: processed item
 * JavaBatch -> addPhotographWriter: writeItems(processed items)
 * JavaBatch -> Transaction: commit
 * JavaBatch -> addPhotographWriter: checkpointInfo
 * JavaBatch -> addPhotographWriter: ...
 * JavaBatch -> addPhotographReader: close
 * JavaBatch -> addPhotographWriter: close
 * JavaBatch -> addPhotographListener: afterStep
 * JavaBatch -> addPhotographListener: afterJob
 *
 * @enduml
 *
 * @startuml package-info-add-hierarchy.png
 * interface javax.batch.api.listener.JobListener
 * interface javax.batch.api.listener.StepListener
 * interface javax.batch.api.chunk.ItemProcessor
 * interface javax.batch.api.chunk.ItemReader
 * interface javax.batch.api.chunk.ItemWriter
 * javax.batch.api.listener.JobListener <|-- gallery.jobs.add.Listener
 * javax.batch.api.listener.StepListener <|-- gallery.jobs.add.Listener
 * javax.batch.api.chunk.ItemProcessor <|-- gallery.jobs.add.Processor
 * javax.batch.api.chunk.ItemReader <|-- gallery.jobs.add.Reader
 * javax.batch.api.chunk.ItemWriter <|-- gallery.jobs.add.Writer
 *
 * @enduml
 *
 */
package gallery.jobs.add;
