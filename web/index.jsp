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
            String idString = request.getParameter("id");
            if (idString == null || idString.trim().equals(""))
            {
                idString = "0";
            }

            Long id = null;
            try
            {
                id = Long.decode(idString);
            } catch (NumberFormatException e)
            {
                id = 0l;
            }
        %>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Your Personal Photograph Organiser</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
        <script type="text/javascript" src="/fancybox/jquery.fancybox-1.3.4.pack.js"></script>
        <script type="text/javascript" src="/fancybox/jquery.mousewheel-3.0.4.pack.js"></script>
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
                YourPersonalPhotographOrganiserBag.index += (YourPersonalPhotographOrganiserBag.view == "multiple" ? 9 : 1);
                if (YourPersonalPhotographOrganiserBag.index>=YourPersonalPhotographOrganiserBag.photos.length) {YourPersonalPhotographOrganiserBag.index =YourPersonalPhotographOrganiserBag.photos.length - 1;}
                displayPhotos();
            }

            function changeView()
            {
                YourPersonalPhotographOrganiserBag.view = (YourPersonalPhotographOrganiserBag.view == "multiple" ? "single" : "multiple");
                displayPhotos();
            }

            function go()
            {
                var i = parseInt($("#go_number_foto").val()) - 1;
                if (window.console && YourPersonalPhotographOrganiserBag.debug) {console.debug(i);}
                if (i < 0 || i > YourPersonalPhotographOrganiserBag.photos.length - 1){return;}
                YourPersonalPhotographOrganiserBag.index = i;
                displayPhotos();
            }

            function displayPhotos()
            {
                var buffer = "";
                var photos = YourPersonalPhotographOrganiserBag.photos;

                if (YourPersonalPhotographOrganiserBag.view != "multiple")
                {
                    var i=YourPersonalPhotographOrganiserBag.index;
                    var description = photos[i].description;
                    if (description == null) {description = '';}
                    buffer += "<div class=\"photographCenter\">";
                    if (photos[i].photographId.filename.toLowerCase().search('[.]avi$') == -1)
                    {
                        buffer += "<a href=\"photo.jsp?id=" + photos[i].id + "\">" +
                            "<img src=\"/YourPersonalPhotographOrganiser/ImageServlet?id=" + photos[i].photographId.id + "&size=large\" alt=\"\"/>";
                    }
                    else
                    {
                        buffer += "<a><img src=\"/YourPersonalPhotographOrganiser/images/movie.png\" alt=\"\"/>";
                    }
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
                for (i=YourPersonalPhotographOrganiserBag.index;i<=YourPersonalPhotographOrganiserBag.index+8;i++)
                {
                    var description = photos[i].description;
                    if (description == null) {description = '';}
                    if (photos[i].photographId.filename.toLowerCase().search('[.]avi$') == -1)
                    {

                        buffer +='<div class="photograph ' + (j % 3 == 0 ? 'photographBegin ':' ') + (j % 3 == 2 ? 'photographEnd':'') +'"><a class=\"group\" rel=\"group1\" href=\"/YourPersonalPhotographOrganiser/ImageServlet?id=' + photos[i].photographId.id + '\">' +
                            '<img src=\"/YourPersonalPhotographOrganiser/ImageServlet?id=' + photos[i].photographId.id + '&size=medium\" alt=\"\"/>' +
                            '</a><br/><div class=\"name\"><a href=\"photo.jsp?id=' + photos[i].id + '\">' + (i+1) + '. ' +
                            photos[i].name
                            + '</a></div><div class=\"description\">' + description + '</div></div>';}
                    else
                    {
                        buffer +='<div class=\"photograph ' + (j % 3 == 0 ? 'photographBegin ':' ') + (j % 3 == 2 ? 'photographEnd':'') +'">' +
                            '<a><img src=\"/YourPersonalPhotographOrganiser/images/movie.png\" alt=\"\"/></a>' +
                            '<br/><div class=\"name\">' + (i+1) + '. ' +
                            photos[i].name
                            + '</div><div class=\"description\">' + description + '</div></div>';
                    }
                    j++;
                }
                $('#pictureDiv').html(buffer);
                doThisThing();
            }

            function refreshPage(id)
            {
                $.get('/YourPersonalPhotographOrganiser/resources/galleries/' + id,
                function(data){
                    if (window.console && YourPersonalPhotographOrganiserBag.debug) {console.debug(data);}
                    if (data == null)
                    {
                        alert("No photographs found.");return;
                    }
                    $("#galleryname").html(data.name);
                    $("#gallerydescription").html(data.description);
                    if (data.parentId != null)
                    {
                        $("#gallery_up").html("Up");
                        $("#gallery_up").onclick(function() {
                            // similar behavior as an HTTP redirect
                            window.location.replace('/YourPersonalPhotographOrganiser/resources/galleries/' + data.parentId.id);
                        });// end function onclick
                    } // end if parentId
                }, // end function(data)
                "json"); // endget
                // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )

                $.get('/YourPersonalPhotographOrganiser/resources/galleries/' + id + '/photographs',
                function(data){
                    if (window.console && YourPersonalPhotographOrganiserBag.debug) {console.debug(data);}
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

            $(document).ready(function() {
                refreshPage(<%= id%>);
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
        <div class="gallery_up myButton">No up</div>
        <div class="page_up myButton">Page up</div>
        <div class="page_down myButton">Page down</div>
        <div class="changeview myButton">Change view</div>
        <input id="go_number_foto" type="text"></input>
        <div class="goButton myButton">Go</div>
        <hr/>
        <div id="pictureDiv"></div><hr>
        <div class="gallery_up myButton">No up</div>
        <div class="page_up myButton">Page up</div>
        <div class="page_down myButton">Page down</div>
        <div class="changeview myButton">Change view</div>
    </body>
</html>
