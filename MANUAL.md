
# MANUAL

## For Users

Browse to [http://localhost:8080/YourPersonalPhotographOrganiser](http://localhost:8080/YourPersonalPhotographOrganiser).

### Viewing picture metadata

It is possible to view all the information stored in the picture by your camera/smartphone/etc. Just "Change view" to single-photograph, and then click on the "Metadata" label in the properties of the current photograph visible on the left of the browserwindow.

## For Administrators

Browse to [http://localhost:8080/YourPersonalPhotographOrganiser/faces/admin/index.xhtml](http://localhost:8080/YourPersonalPhotographOrganiser/faces/admin/index.xhtml).

In general the following three functions are available at all levels:
* View
* Edit
* Destroy
 
### Initialize galleries

    Select "Show all locations", at the location of your choice, click on "Init"

Will initialize your galleries. Extremely handy if you are just starting out with a fresh slate.

Galleries will be named after the directorynames. Basically, the gallery tree will mirror the file system tree.

Then, afterwards, you can make any changes you need.

### Add new photographs

    Select "Show all locations", at the location of your choice, click on "Discover"

Will discover new photographs at the given location. The checks are as follows:
* verify if photo already exists in the database, if so -> skip
* verify if hash of photo matches up with another photo in the database (i.e. a rename or move operation has occurred) -> change path/filename of photo in database appropriately
* otherwise -> adds the new photo to the database

New photographs are not automatically added to any gallery.

### Verify existing photographs

    Select "Show all locations", at the location of your choice, click on "Verify"

You can verify all photographs at a location. The checksum of a photograph is recomputed and compared with the checksum stored in the database.

This will be done in the background, so once started, you can continue your administration tasks normally, whilst the server does its stuff.

The log will indicate which photographs are:
* Faulty
* Missing

Depending on the amount of photographs that are stored at a location, this verification can take a long time.

However, the site should remain perfectly usable, if maybe slightly slower.

