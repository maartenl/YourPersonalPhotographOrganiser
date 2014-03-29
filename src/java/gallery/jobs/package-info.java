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
 * <img src="../../images/package-info-jobs.png"/></p>
 * <p>
 * In the example/picture above, it shows that the item-count is set to three,
 * even
 * though
 * in the code it is 1. It serves as a good example of the behaviour,
 * better then when item-count of 1 would be diagrammed.</p>
 *
 * @startuml package-info-jobs.png
 * participant JavaBatch
 * participant Listener
 * participant Reader
 * participant Writer
 * participant Processor
 * participant Transaction
 *
 * JavaBatch -> Listener: beforeJob
 * JavaBatch -> Listener: beforeStep
 * JavaBatch -> Reader: open
 * JavaBatch -> Writer: open
 * JavaBatch -> Transaction: begin
 * JavaBatch -> Reader: readItem
 * JavaBatch <-- Reader: item
 * JavaBatch -> Processor: processItem(item)
 * JavaBatch <- Processor: processed item
 * JavaBatch -> Reader: readItem
 * JavaBatch <-- Reader: item
 * JavaBatch -> Processor: processItem(item)
 * JavaBatch <-- Processor: processed item
 * JavaBatch -> Reader: readItem
 * JavaBatch <-- Reader: item
 * JavaBatch -> Processor: processItem(item)
 * JavaBatch <-- Processor: processed item
 * JavaBatch -> Writer: writeItems(processed items)
 * JavaBatch -> Transaction: commit
 * JavaBatch -> Writer: checkpointInfo
 * JavaBatch -> Writer: ...
 * JavaBatch -> Reader: close
 * JavaBatch -> Writer: close
 * JavaBatch -> Listener: afterStep
 * JavaBatch -> Listener: afterJob
 *
 * @enduml
 */
package gallery.jobs;
