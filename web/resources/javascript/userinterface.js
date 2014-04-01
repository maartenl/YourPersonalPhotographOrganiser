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

var YourPersonalPhotographOrganiserBag = {
    debug: true, // debugging=false is debugging off
    view: "multiple", // indicates that 9 pictures should be shown at once, single being only one picture.
    index: 0 // the offset on which photos are being watched.
};

function doThisThing()
{
    /* Apply fancybox to multiple items */

    $("a.group").fancybox({
        'transitionIn': 'elastic',
        'transitionOut': 'elastic',
        'speedIn': 600,
        'speedOut': 200,
        'overlayShow': false,
        'type': 'image'
    });
}

function pageUp()
{
    YourPersonalPhotographOrganiserBag.index -= (YourPersonalPhotographOrganiserBag.view === "multiple" ? 9 : 1);
    if (YourPersonalPhotographOrganiserBag.index < 0) {
        YourPersonalPhotographOrganiserBag.index = 0;
    }
    displayPhotos();
}

function pageDown()
{
    var sizeme = (YourPersonalPhotographOrganiserBag.view === "multiple" ? 9 : 1);
    if (YourPersonalPhotographOrganiserBag.index + sizeme >= YourPersonalPhotographOrganiserBag.photos.length)
    {
        return;
    }
    YourPersonalPhotographOrganiserBag.index += sizeme;
    displayPhotos();
}

function beginning()
{
    YourPersonalPhotographOrganiserBag.index = 0;
    displayPhotos();
}

function end()
{
    var lengthy = YourPersonalPhotographOrganiserBag.photos.length;
    YourPersonalPhotographOrganiserBag.index = lengthy - 1;
    if (YourPersonalPhotographOrganiserBag.view === "multiple")
    {
        YourPersonalPhotographOrganiserBag.index -= ((lengthy - 1) % 9);
    }
    displayPhotos();
}

function changeView()
{
    YourPersonalPhotographOrganiserBag.view = (YourPersonalPhotographOrganiserBag.view === "multiple" ? "single" : "multiple");
    displayPhotos();
}

function go()
{
    if (isNaN(parseInt($("#go_number_foto").val())))
    {
        alert("Number expected.");
    }
    else
    {
        var i = parseInt($("#go_number_foto").val()) - 1;
    }
    log.debug(i);
    if (i < 0 || i > YourPersonalPhotographOrganiserBag.photographs.length - 1) {
        return;
    }
    YourPersonalPhotographOrganiserBag.index = i;
    displayPhotos();
}

function displaySinglePhotos()
{
    log.debug("displaySinglePhotos");
    var buffer = "";
    var photos = YourPersonalPhotographOrganiserBag.photographs;
    var i = YourPersonalPhotographOrganiserBag.index;
    var description = photos[i].description;
    if (description === null) {
        description = '';
    }
    buffer += "<div class=\"photographCenter\">";
    if (photos[i].photograph === undefined) // is gallery!
    {
        // gallery found
        buffer += "<a href=\"/YourPersonalPhotographOrganiser/?id=" + photos[i].id + "\">";
        if (photos[i].highlight === null)
        {
            buffer += "<img src=\"/YourPersonalPhotographOrganiser/images/gallery.png\" alt=\"\"/>";
        }
        else
        {
            buffer += "<img src=\"/YourPersonalPhotographOrganiser/ImageServlet?id=" + photos[i].highlight.id + "&size=large\" alt=\"\"/>";
        }
        buffer += '</a>' +
                '<br/><div class=\"name\"><img width="45px" height="45px" src=\"/YourPersonalPhotographOrganiser/images/gallery.png\" alt=\"\"/>' + (i + 1) + '. ' +
                photos[i].name
                + '</div><div class=\"description\">' + description + '</div></div>';
        $('#pictureDiv').html(buffer);
        return;
    }
    var imageSize = "large";
    if (photos[i].photograph.filename.toLowerCase().search('[.]avi$') !== -1)
    {
        imageSize = "big";
    }
    buffer += "<a href=\"photo.jsp?id=" + photos[i].id + "\">" +
            "<img src=\"/YourPersonalPhotographOrganiser/ImageServlet?id=" + photos[i].photograph.id + "&size=" + imageSize + "\" alt=\"\"/>";
    buffer += '</a>' +
            '<br/><div class=\"name\">' + (i + 1) + '. ' +
            photos[i].name
            + '</div><div class=\"description\">' + description + '</div><div class=\"comments\"></div></div>';
    $('#pictureDiv').html(buffer);

    $.get('/YourPersonalPhotographOrganiser/resources/galleryphotographs/' + photos[i].id + '/comments',
            function(data) {
                log.debug(data);
                if (data === null)
                {
                    alert("No comments found.");
                    return;
                }
                var buffer = "";
                for (i in data)
                {
                    buffer += "<p>" + data[i].comment + "<p/><p>" + data[i].author + ", " + (new Date(data[i].submitted)) + "</p>";
                }
                $(".comments").html(buffer);
            }, // end function(data)
            "json"); // endget
    // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )
    return;
}

function displayMultiplePhotos()
{
    log.debug("displayMultiplePhotos");
    var buffer = "";
    var photos = YourPersonalPhotographOrganiserBag.photographs;
    var i = 0;
    var j = 0;
    var continue_until = YourPersonalPhotographOrganiserBag.index + 8;
    if (continue_until >= photos.length)
    {
        continue_until = photos.length - 1;
    }
    for (i = YourPersonalPhotographOrganiserBag.index; i <= continue_until; i++)
    {
        var description = photos[i].description;
        if (description === null) {
            description = '';
        }
        if (photos[i].photograph === undefined) // is gallery!
        {
            // gallery found
            if ((photos[i].highlight !== undefined) && (photos[i].highlight !== null))
            {

                buffer += '<div class="photograph ' + (j % 3 === 0 ? 'photographBegin ' : ' ') + (j % 3 === 2 ? 'photographEnd' : '') + '"><a href=\"/YourPersonalPhotographOrganiser/?id=' + photos[i].id + '\">' +
                        '<img src=\"/YourPersonalPhotographOrganiser/ImageServlet?id=' + photos[i].highlight.id + '&size=medium\" alt=\"\"/>' +
                        '</a><br/><div class=\"name\"><img width="45px" height="45px" src=\"/YourPersonalPhotographOrganiser/images/gallery.png\" alt=\"\"/>' + (i + 1) + '. ' +
                        photos[i].name
                        + '</div></div>';
            }
            else
            {
                buffer += '<div class=\"photograph ' + (j % 3 === 0 ? 'photographBegin ' : ' ') + (j % 3 === 2 ? 'photographEnd' : '') + '">' +
                        '<a href=\"/YourPersonalPhotographOrganiser/?id=' + photos[i].id + '\"><img width="350px" height="350px" src=\"/YourPersonalPhotographOrganiser/images/gallery.png\" alt=\"\"/></a>' +
                        '<br/><div class=\"name\"><img width="45px" height="45px" src=\"/YourPersonalPhotographOrganiser/images/gallery.png\" alt=\"\"/>' + (i + 1) + '. ' +
                        photos[i].name
                        + '</div></div>';
            }
        }
        else
        {
            var isImage = "";
            if (photos[i].photograph.filename.toLowerCase().search('[.]avi$') === -1)
            {
                isImage = " class=\"group\" rel=\"group1\"";
            }
            buffer += '<div class="photograph ' + (j % 3 == 0 ? 'photographBegin ' : ' ') + (j % 3 == 2 ? 'photographEnd' : '') + '"><a ' + isImage + ' href=\"/YourPersonalPhotographOrganiser/ImageServlet?id=' + photos[i].photograph.id + '\">' +
                    '<img src=\"/YourPersonalPhotographOrganiser/ImageServlet?id=' + photos[i].photograph.id + '&size=medium\" alt=\"\"/>' +
                    '</a><br/><div class=\"name\"><a href=\"photo.jsp?id=' + photos[i].id + '\">' + (i + 1) + '. ' +
                    photos[i].name
                    + '</a></div></div>';
        }
        j++;
    }
    $('#pictureDiv').html(buffer);
    doThisThing();
}

function displayPhotos()
{
    log.debug("displayPhotos");
    $("#gallerystats").html(YourPersonalPhotographOrganiserBag.photographs.length);
    if (YourPersonalPhotographOrganiserBag.view !== "multiple")
    {
        displaySinglePhotos();
        return;
    }
    displayMultiplePhotos();
}

function showGalleryInfo()
{
    log.debug("showGalleryInfo");
    data = YourPersonalPhotographOrganiserBag.gallery;
    if (data === null)
    {
        alert("No gallery found.");
        return;
    }
    $("#galleryname").html(data.name);
    $("#gallerydescription").html(data.description);
}

function loadPage()
{
    yppo.galleries.getGallery(1, function(data) {
        YourPersonalPhotographOrganiserBag.gallery = data;
        yppo.galleries.getAllGalleries(function(data) {
            YourPersonalPhotographOrganiserBag.galleries = data;
            var buffer = "";
            // massage return value so jsTree can do something with it.
            for (var x in YourPersonalPhotographOrganiserBag.galleries)
            {
                var item = YourPersonalPhotographOrganiserBag.galleries[x];
                if (item.hasOwnProperty(parent))
                {
                    item.parent = item.parent.id;
                } else
                {
                    item.parent = "#";
                }
                item.text = item.name;
                item.icon = "/YourPersonalPhotographOrganiser/faces/javax.faces.resource/24/gallery-icon.png?ln=images";
            }
            //$("#galleryDiv").html(buffer);
            $('#galleryDiv').jstree({'core': {
                    'data': YourPersonalPhotographOrganiserBag.galleries
                }});
        });
        yppo.photographs.getPhotographs(1, function(data) {
            YourPersonalPhotographOrganiserBag.photographs = data;
            displayPhotos();
        });
        showGalleryInfo();
    });
}
