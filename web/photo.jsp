<%--
Document : index
Created on : Dec 18, 2011, 11:46:17 AM
Author : maartenl
--%>
<%@ page import="gallery.database.entities.Photograph"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.nio.file.Files"%>
<%@ page import="java.nio.file.FileSystems"%>
<%@ page import="java.nio.file.FileVisitResult"%>
<%@ page import="java.nio.file.FileVisitor"%>
<%@ page import="java.nio.file.Path"%>
<%@ page import="java.nio.file.PathMatcher"%>
<%@ page import="java.nio.file.attribute.BasicFileAttributes"%>
<%@ page import="java.nio.file.attribute.PosixFileAttributes"%>
<%@ page import="java.nio.file.LinkOption"%>

<%@ page import="java.util.logging.Level"%>
<%@ page import="java.util.logging.Logger"%>
<%@ page import="javax.naming.InitialContext"%>
<%@ page import="javax.naming.NamingException"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%!// jdk7 doesn't work with jsp pages yet
// http://java.net/jira/browse/GLASSFISH-17429
    private static final long KILOBYTES = 1024;// 1_024;
    private static final long MEGABYTES = 1048576; // 1_048_576;
%>
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
            function createPhotograph()
            {
                alert("createPhotograph");
            }

            function updatePhotograph()
            {
                var photo = YourPersonalPhotographOrganiserBag.photograph;
                if (photo == null)
                {
                    return;
                }
                photo.description = $("#description").val();
                photo.angle = $("#angle").val();
                photo.name = $("#name").val();
                photo.sortorder = $("#sortorder").val();
                $.ajax({
                    type: "PUT",
                    url: "/YourPersonalPhotographOrganiser/resources/galleryphotographs",
                    data: JSON.stringify(photo),
                    success: function()
                    {
                        // alert("Success!");
                    },
                    contentType: "application/json"
                }).done(function( msg ) {
                    // alert( "Data Saved: " + msg );
                });
            }

            function deletePhotograph()
            {
                alert("deletePhotograph");
                var photo = YourPersonalPhotographOrganiserBag.photograph;
                if (photo == null)
                {
                    return;
                }
                $.ajax({
                    type: "DELETE",
                    url: "/YourPersonalPhotographOrganiser/resources/galleryphotographs/" + photo.id,
                    success: function()
                    {
                        alert("Success!");
                    },
                    contentType: "application/json"
                }).done(function( msg ) {
                    alert( "Data Saved: " + msg );
                });
            }

            function refreshPage(id)
            {
                $.get(
                '/YourPersonalPhotographOrganiser/resources/galleryphotographs/' + id
                ,
                function(data)
                {
                    if (window.console && YourPersonalPhotographOrganiserBag.debug)
                    {
                        console.debug(data);
                    }
                    if (data == null)
                    {
                        alert("No photograph found.");
                        return;
                    }
                    YourPersonalPhotographOrganiserBag.photograph = data;
                    $("#photographname").html(data.id + ". " + data.name);
                    $("#photographnumber").html(data.id);
                    $("#name").val(data.name);
                    $("#angle").val(data.angle);
                    $("#description").val(data.description);
                    $("#sortorder").val(data.sortorder);
                    $("#photograph").html("<img src=\"/YourPersonalPhotographOrganiser/ImageServlet?id=" + data.photographId.id + "&size=medium\"/>");

                    $("#photo_photographId_id").val(data.photographId.id);
                    $("#photo_photographId_relativepath").html(data.photographId.relativepath);
                    $("#photo_photographId_taken").html(data.photographId.taken);
                    $("#photo_photographId_filename").html(data.photographId.filename);

                    $("#photo_photographId_locationId_id").html(data.photographId.locationId.id);
                    $("#photo_photographId_locationId_filepath").html(data.photographId.locationId.filepath);

                } // end function data
                , "json"); // endget
                // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )
            }

            $(document).ready(function()
            {
                refreshPage(<%= id%>);

                $('.createPhotograph').click(function(){createPhotograph();});
                $('.updatePhotograph').click(function(){updatePhotograph();});
                $('.deletePhotograph').click(function(){deletePhotograph();});
            }); // end document ready
        </script>
        <link rel="stylesheet" href="/fancybox/jquery.fancybox-1.3.4.css" type="text/css" media="screen" />
        <link rel="stylesheet" href="css/yppo.css" type="text/css" media="screen" />
    </head>
    <body>
        <h1 id="photographname">Photograph</h1>
        <hr/>
        <div id="photograph"></div>
        <p>Photograph id: <span id="photographnumber"></span></p>
        <label for="name">Name</label>
        <input type="text" name="name" id="name"/>
        <br/>
        <label for="description">Description</label>
        <textarea name="description" id="description" ></textarea>
        <br/>
        <label for="sortorder">Sort order</label>
        <input type="text" name="sortorder" id="sortorder" />
        <br/>
        <label for="angle">Angle</label>
        <input type="text" name="angle" id="angle" />
        <br/>
        <h3>File</h3>
        <p>
            <label for="photo_photographId_id">File id</label>
            <input type="text" name="photo_photographId_id" id="photo_photographId_id"/>
        </p>
        <p>
            Filename: <span id="photo_photographId_filename"></span>
        </p>
        <p>
            Relative path:<span id="photo_photographId_relativepath"></span>
        </p>
        <p>
            Taken:<span id="photo_photographId_taken"></span>
        </p>
        <h3>Location</h3>
        <p>
            Id: <span id="photo_photographId_locationId_id"></span>
        </p>
        <p>
            File path:<span id="photo_photographId_locationId_filepath"></span>
        </p>
        <hr/>
        <div class="createPhotograph myButton">Create</div>
        <div class="updatePhotograph myButton">Update</div>
        <div class="deletePhotograph myButton">Delete</div>
        <br/>
        <a href="index.jsp" >Return</a>
    </body>
</html>
