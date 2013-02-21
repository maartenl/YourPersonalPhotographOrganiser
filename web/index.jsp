<%--
    Document   : index
    Created on : Dec 18, 2011, 11:46:17 AM
    Author     : maartenl
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <%
            Long id = null;
            String idString = request.getParameter("id");
            if (idString == null || idString.trim().equals(""))
            {
                id = null;
            } else
            {
                try
                {
                    id = Long.decode(idString);
                } catch (NumberFormatException e)
                {
                    id = null;
                }
            }
        %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Your Personal Photograph Organiser</title>
        <%@ include file="WEB-INF/includes/javascripts.jspf" %>
        <script type="text/javascript">

            var YourPersonalPhotographOrganiserBag = {
                debug : true, // debugging=false is debugging off
                view : "multiple" // indicates that 9 pictures should be shown at once, single being only one picture.
            };

            function doThisThing()
            {
                /* Apply fancybox to multiple items */

                $("a.group").fancybox({
                    'transitionIn'	:	'elastic',
                    'transitionOut'	:	'elastic',
                    'speedIn'		:	600,
                    'speedOut'		:	200,
                    'overlayShow'	:	false,
                    'type'              :       'image'
                });
            }

            function pageUp()
            {
                YourPersonalPhotographOrganiserBag.index -= (YourPersonalPhotographOrganiserBag.view == "multiple" ? 9 : 1);
                if (YourPersonalPhotographOrganiserBag.index<0) {YourPersonalPhotographOrganiserBag.index =0;}
                displayPhotos();
            }

            function pageDown()
            {
                var sizeme = (YourPersonalPhotographOrganiserBag.view == "multiple" ? 9 : 1);
                if (YourPersonalPhotographOrganiserBag.index + sizeme>=YourPersonalPhotographOrganiserBag.photos.length)
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
                if (YourPersonalPhotographOrganiserBag.view == "multiple")
                {
                    YourPersonalPhotographOrganiserBag.index -= ((lengthy - 1) % 9)
                }
                displayPhotos();
            }

            function changeView()
            {
                YourPersonalPhotographOrganiserBag.view = (YourPersonalPhotographOrganiserBag.view == "multiple" ? "single" : "multiple");
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
                if (window.console && YourPersonalPhotographOrganiserBag.debug) {console.debug(i);}
                if (i < 0 || i > YourPersonalPhotographOrganiserBag.photos.length - 1){return;}
                YourPersonalPhotographOrganiserBag.index = i;
                displayPhotos();
            }

            function displayPhotos()
            {
                if (window.console && YourPersonalPhotographOrganiserBag.debug) {console.debug("displayPhotos");}
                var buffer = "";
                var photos = YourPersonalPhotographOrganiserBag.photos;

                if (YourPersonalPhotographOrganiserBag.view != "multiple")
                {
                    var i=YourPersonalPhotographOrganiserBag.index;
                    var description = photos[i].description;
                    if (description == null) {description = '';}
                    buffer += "<div class=\"photographCenter\">";
                    if (photos[i].photograph === undefined) // is gallery!
                    {
                        // gallery found
                        buffer += "<a href=\"/YourPersonalPhotographOrganiser/?id=" + photos[i].id + "\">";
                        if (photos[i].highlight == null)
                        {
                            buffer += "<img src=\"/YourPersonalPhotographOrganiser/images/gallery.png\" alt=\"\"/>";
                        }
                        else
                        {
                            buffer += "<img src=\"/YourPersonalPhotographOrganiser/ImageServlet?id=" + photos[i].highlight.id + "&size=large\" alt=\"\"/>";                                           }
                        buffer += '</a>' +
                            '<br/><div class=\"name\"><img width="45px" height="45px" src=\"/YourPersonalPhotographOrganiser/images/gallery.png\" alt=\"\"/>' + (i+1) + '. ' +
                            photos[i].name
                            + '</div><div class=\"description\">' + description + '</div></div>';
                        $('#pictureDiv').html(buffer);
                        return;
                    }
                    var imageSize = "large";
                    if (photos[i].photograph.filename.toLowerCase().search('[.]avi$') != -1)
                    {
                        imageSize = "big";
                    }
                    buffer += "<a href=\"photo.jsp?id=" + photos[i].id + "\">" +
                        "<img src=\"/YourPersonalPhotographOrganiser/ImageServlet?id=" + photos[i].photograph.id + "&size=" + imageSize + "\" alt=\"\"/>";
                    buffer += '</a>' +
                        '<br/><div class=\"name\">' + (i+1) + '. ' +
                        photos[i].name
                        + '</div><div class=\"description\">' + description + '</div><div class=\"comments\"></div></div>';
                    $('#pictureDiv').html(buffer);

                    $.get('/YourPersonalPhotographOrganiser/resources/galleryphotographs/' + photos[i].id + '/comments',
                    function(data){
                        if (window.console && YourPersonalPhotographOrganiserBag.debug) {console.debug(data);}
                        if (data == null)
                        {
                            alert("No comments found.");return;
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

                var i = 0;
                var j = 0;
                var continue_until = YourPersonalPhotographOrganiserBag.index+8;
                if (continue_until >= YourPersonalPhotographOrganiserBag.photos.length)
                {
                    continue_until = YourPersonalPhotographOrganiserBag.photos.length-1;
                }
                for (i=YourPersonalPhotographOrganiserBag.index;i<=continue_until;i++)
                {
                    var description = photos[i].description;
                    if (description == null) {description = '';}
                    if (photos[i].photograph === undefined) // is gallery!
                    {
                        // gallery found
                        if (photos[i].highlight != null)
                        {

                            buffer +='<div class="photograph ' + (j % 3 == 0 ? 'photographBegin ':' ') + (j % 3 == 2 ? 'photographEnd':'') +'"><a href=\"/YourPersonalPhotographOrganiser/?id=' + photos[i].id + '\">' +
                                '<img src=\"/YourPersonalPhotographOrganiser/ImageServlet?id=' + photos[i].highlight.id + '&size=medium\" alt=\"\"/>' +
                                '</a><br/><div class=\"name\"><img width="45px" height="45px" src=\"/YourPersonalPhotographOrganiser/images/gallery.png\" alt=\"\"/>' + (i+1) + '. ' +
                                photos[i].name
                                + '</div></div>';
                        }
                        else
                        {
                            buffer +='<div class=\"photograph ' + (j % 3 == 0 ? 'photographBegin ':' ') + (j % 3 == 2 ? 'photographEnd':'') +'">' +
                                '<a href=\"/YourPersonalPhotographOrganiser/?id=' + photos[i].id + '\"><img width="350px" height="350px" src=\"/YourPersonalPhotographOrganiser/images/gallery.png\" alt=\"\"/></a>' +
                                '<br/><div class=\"name\"><img width="45px" height="45px" src=\"/YourPersonalPhotographOrganiser/images/gallery.png\" alt=\"\"/>' + (i+1) + '. ' +
                                photos[i].name
                                + '</div></div>';
                        }
                    }
                    else
                    {
                        var isImage = "";
                        if (photos[i].photograph.filename.toLowerCase().search('[.]avi$') == -1)
                        {
                            isImage = " class=\"group\" rel=\"group1\"";
                        }
                        buffer +='<div class="photograph ' + (j % 3 == 0 ? 'photographBegin ':' ') + (j % 3 == 2 ? 'photographEnd':'') +'"><a ' + isImage + ' href=\"/YourPersonalPhotographOrganiser/ImageServlet?id=' + photos[i].photograph.id + '\">' +
                            '<img src=\"/YourPersonalPhotographOrganiser/ImageServlet?id=' + photos[i].photograph.id + '&size=medium\" alt=\"\"/>' +
                            '</a><br/><div class=\"name\"><a href=\"photo.jsp?id=' + photos[i].id + '\">' + (i+1) + '. ' +
                            photos[i].name
                            + '</a></div></div>';
                    }
                    j++;
                }
                $('#pictureDiv').html(buffer);
                doThisThing();
            }


            function addGalleries(id)
            {
                if (window.console && YourPersonalPhotographOrganiserBag.debug) {console.debug("addGalleries");}
                var temp_array= [];
                var url;
                if (id === undefined)
                {
                    url = '/YourPersonalPhotographOrganiser/resources/galleries';
                }
                else
                {
                    url = '/YourPersonalPhotographOrganiser/resources/galleries/' + id + '/galleries';
                }
                $.get(url,
                function(data){
                    if (window.console && YourPersonalPhotographOrganiserBag.debug) {console.debug(data);}

                    for (i in data)
                    {
                        data[i].sortorder -= 100;
                        if (id === undefined && data[i].parent === null)
                        {
                            temp_array.push(data[i]);
                        }
                    }
                    if (id === undefined)
                    {
                        data = temp_array;
                    }
                    YourPersonalPhotographOrganiserBag.galleries = data;
                    if (YourPersonalPhotographOrganiserBag.photos === undefined)
                    {
                        YourPersonalPhotographOrganiserBag.photos = [];
                    }
                    YourPersonalPhotographOrganiserBag.photos = YourPersonalPhotographOrganiserBag.photos.concat(data);
                    // filter out null values
                    // TODO: find out why there are null values in there in the first place.
                    YourPersonalPhotographOrganiserBag.photos = YourPersonalPhotographOrganiserBag.photos.filter(function(e){return e}); 
                    YourPersonalPhotographOrganiserBag.photos.sort(function (a, b)
                    {
                        return (a.sortorder - b.sortorder)
                    });
                    if (window.console && YourPersonalPhotographOrganiserBag.debug) {console.debug("Combo");}
                    if (window.console && YourPersonalPhotographOrganiserBag.debug) {console.debug(YourPersonalPhotographOrganiserBag.photos);}
                    displayPhotos();
                }, // end function(data)
                "json"); // endget
                // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )
            }

            function refreshPage(id)
            {
                if (window.console && YourPersonalPhotographOrganiserBag.debug) {console.debug("refreshPage");}
                if (id === undefined)
                {
                    YourPersonalPhotographOrganiserBag.index = 0;
                    addGalleries(id);
                    return;
                }
                $.get('/YourPersonalPhotographOrganiser/resources/galleries/' + id,
                function(data){
                    if (window.console && YourPersonalPhotographOrganiserBag.debug) {console.debug(data);}
                    if (data == null)
                    {
                        alert("No photographs found.");return;
                    }
                    $("#galleryname").html(data.name);
                    $("#gallerydescription").html(data.description);
                    if (data.parent !== null)
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

                $.get('/YourPersonalPhotographOrganiser/resources/galleries/' + id + '/photographs',
                function(data){
                    data = data.galleryPhotograph;
                    if (window.console && YourPersonalPhotographOrganiserBag.debug) {console.debug(data);}
                    data.sort(function (a, b)
                    {
                        return (a.sortorder - b.sortorder)
                    });
                    YourPersonalPhotographOrganiserBag.photos = data;
                    $("#gallerystats").html(YourPersonalPhotographOrganiserBag.photos.length);

                    YourPersonalPhotographOrganiserBag.index = 0;
                    addGalleries(id);
                }, // end function(data)
                "json"); // endget
                // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )
            }

            function gotoGallery()
            {
                window.location.replace('/YourPersonalPhotographOrganiser/gallery.jsp');
            }

            $(document).ready(function() {
                refreshPage(<%= id == null ? "" : id%>);
                $('.beginning').click(function(){beginning();});
                $('.end').click(function(){end  ();});
                $('.page_up').click(function(){pageUp();});
                $('.page_down').click(function(){pageDown();});
                $('#galleryname').click(function(){gotoGallery();});
                $('.changeview').click(function(){changeView();});
                $('.goButton').click(function(){go();});
            }); // end document ready
        </script>
        <link rel="stylesheet" href="/fancybox/jquery.fancybox-1.3.4.css" type="text/css" media="screen" />
        <link rel="stylesheet" href="css/yppo.css" type="text/css" media="screen" />
    </head>
    <body>
        <h1 id="galleryname">Your Personal Photograph Organiser</h1>
        <div id="gallerydescription"></div>
        <div><span id="gallerystats"></span> photos</div>
        <div class="gallery_up myButton">Home</div>
        <div class="beginning myButton">Beginning</div>
        <div class="page_up myButton">Page up</div>
        <div class="page_down myButton">Page down</div>
        <div class="end myButton">End</div>
        <div class="changeview myButton">Change view</div>
        <input id="go_number_foto" type="text"></input>
        <div class="goButton myButton">Search</div>
        <hr/>
        <div id="pictureDiv"></div><hr>
        <div class="gallery_up myButton">Home</div>
        <div class="beginning myButton">Beginning</div>
        <div class="page_up myButton">Page up</div>
        <div class="page_down myButton">Page down</div>
        <div class="end myButton">End</div>
        <div class="changeview myButton">Change view</div>
    </body>
</html>
