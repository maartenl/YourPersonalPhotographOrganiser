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
    galleries: [], // empty galleries, not sure this is actually used
    photos: [], // all the photos (interspersed with galleries!
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
    if (window.console && YourPersonalPhotographOrganiserBag.debug) {
        console.debug(i);
    }
    if (i < 0 || i > YourPersonalPhotographOrganiserBag.photos.length - 1) {
        return;
    }
    YourPersonalPhotographOrganiserBag.index = i;
    displayPhotos();
}

function displaySinglePhotos()
{
    if (window.console && YourPersonalPhotographOrganiserBag.debug) {
        console.debug("displaySinglePhotos");
    }
    var buffer = "";
    var photos = YourPersonalPhotographOrganiserBag.photos;
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
                if (window.console && YourPersonalPhotographOrganiserBag.debug) {
                    console.debug(data);
                }
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
    if (window.console && YourPersonalPhotographOrganiserBag.debug) {
        console.debug("displayMultiplePhotos");
    }
    var buffer = "";
    var photos = YourPersonalPhotographOrganiserBag.photos;
    var i = 0;
    var j = 0;
    var continue_until = YourPersonalPhotographOrganiserBag.index + 8;
    if (continue_until >= YourPersonalPhotographOrganiserBag.photos.length)
    {
        continue_until = YourPersonalPhotographOrganiserBag.photos.length - 1;
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
    if (window.console && YourPersonalPhotographOrganiserBag.debug) {
        console.debug("displayPhotos");
    }
    if (YourPersonalPhotographOrganiserBag.view !== "multiple")
    {
        displaySinglePhotos();
        return;
    }
    displayMultiplePhotos();
}


function addGalleries(id)
{
    if (window.console && YourPersonalPhotographOrganiserBag.debug) {
        console.debug("addGalleries");
    }
    var url = '/YourPersonalPhotographOrganiser/resources/galleries/' + id + '/galleries';
    $.get(url,
            function(data) {
                if (window.console && YourPersonalPhotographOrganiserBag.debug) {
                    console.debug(data);
                }
                if (YourPersonalPhotographOrganiserBag.photos === undefined)
                {
                    YourPersonalPhotographOrganiserBag.photos = [];
                }
                if (data === null)
                {
                    return;
                }
                data = data.gallery;
                for (i in data)
                {
                    data[i].sortorder -= 100;
                }
                YourPersonalPhotographOrganiserBag.galleries = data;
                YourPersonalPhotographOrganiserBag.photos = YourPersonalPhotographOrganiserBag.photos.concat(data);
                // filter out null values
                // TODO: find out why there are null values in there in the first place.
                YourPersonalPhotographOrganiserBag.photos = YourPersonalPhotographOrganiserBag.photos.filter(function(e) {
                    return e;
                });
                YourPersonalPhotographOrganiserBag.photos.sort(function(a, b)
                {
                    return (a.sortorder - b.sortorder);
                });
                if (window.console && YourPersonalPhotographOrganiserBag.debug) {
                    console.debug("Combo");
                }
                if (window.console && YourPersonalPhotographOrganiserBag.debug) {
                    console.debug(YourPersonalPhotographOrganiserBag.photos);
                }
                displayPhotos();
            }, // end function(data)
            "json"); // endget
    // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )
}

function showRootGalleries()
{
    if (window.console && YourPersonalPhotographOrganiserBag.debug) {
        console.debug("showRootGalleries");
    }
    var url = '/YourPersonalPhotographOrganiser/resources/galleries';
    $.get(url,
            function(data) {
                if (window.console && YourPersonalPhotographOrganiserBag.debug) {
                    console.debug(data);
                }
                if (YourPersonalPhotographOrganiserBag.photos === undefined)
                {
                    YourPersonalPhotographOrganiserBag.photos = [];
                }
                if (data === null)
                {
                    return;
                }
                data = data.gallery;
                for (i in data)
                {
                    data[i].sortorder -= 100;
                }
                YourPersonalPhotographOrganiserBag.galleries = data;
                YourPersonalPhotographOrganiserBag.photos = YourPersonalPhotographOrganiserBag.photos.concat(data);
                // filter out null values
                // TODO: find out why there are null values in there in the first place.
                YourPersonalPhotographOrganiserBag.photos = YourPersonalPhotographOrganiserBag.photos.filter(function(e) {
                    return e;
                });
                YourPersonalPhotographOrganiserBag.photos.sort(function(a, b)
                {
                    return (a.sortorder - b.sortorder);
                });
                if (window.console && YourPersonalPhotographOrganiserBag.debug) {
                    console.debug("Combo");
                }
                if (window.console && YourPersonalPhotographOrganiserBag.debug) {
                    console.debug(YourPersonalPhotographOrganiserBag.photos);
                }
                displayPhotos();
            }, // end function(data)
            "json"); // endget
    // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )
}

function showGalleryInfo(id)
{
    $.get('/YourPersonalPhotographOrganiser/resources/galleries/' + id,
            function(data) {
                if (window.console && YourPersonalPhotographOrganiserBag.debug) {
                    console.debug(data);
                }
                if (data === null)
                {
                    alert("No photographs found.");
                    return;
                }
                $("#galleryname").html(data.name);
                $("#gallerydescription").html(data.description);
                if ((data.parent !== undefined) && (data.parent !== null) && (data.parent.id !== undefined) && (data.parent.id !== null))
                {
                    $(".gallery_up").html("Up");
                    $(".gallery_up").click(function() {
                        // similar behavior as an HTTP redirect
                        window.location.replace('/YourPersonalPhotographOrganiser/?id=' + data.parent.id);
                    });// end function onclick
                } // end if parent
                else
                {
                    $(".gallery_up").click(function() {
                        // similar behavior as an HTTP redirect
                        window.location.replace('/YourPersonalPhotographOrganiser/');
                    });// end function onclick
                }
            }, // end function(data)
            "json"); // endget
    // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )
}

function refreshPage(id)
{
    if (window.console && YourPersonalPhotographOrganiserBag.debug) {
        console.debug("refreshPage");
    }
    if (id === null)
    {
        YourPersonalPhotographOrganiserBag.index = 0;
        showRootGalleries();
        return;
    }
    showGalleryInfo(id);
    addGalleries(id);
    $.get('/YourPersonalPhotographOrganiser/resources/galleries/' + id + '/photographs',
            function(data) {
                if (window.console && YourPersonalPhotographOrganiserBag.debug) {
                    console.debug(data);
                }
                if (data === null)
                {
                    return;
                }
                data = data.galleryPhotograph;
                data.sort(function(a, b)
                {
                    return (a.sortorder - b.sortorder);
                });
                YourPersonalPhotographOrganiserBag.photos = data;
                $("#gallerystats").html(YourPersonalPhotographOrganiserBag.photos.length);

                YourPersonalPhotographOrganiserBag.index = 0;
                displayPhotos();
            }, // end function(data)
            "json"); // endget
    // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )
}

function gotoGallery()
{
    window.location.replace('/YourPersonalPhotographOrganiser/gallery.jsp');
}
