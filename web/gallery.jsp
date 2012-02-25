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
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Your Personal Photograph Organiser</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
        <script type="text/javascript" src="/fancybox/jquery.fancybox-1.3.4.pack.js"></script>
        <script type="text/javascript" src="/fancybox/jquery.mousewheel-3.0.4.pack.js"></script>
        <script type="text/javascript">

            var YourPersonalPhotographOrganiserBag = {
                debug : true, // debugging=false is debugging off
                index : 0
            };

            function createGallery()
            {
                if (window.console && YourPersonalPhotographOrganiserBag.debug)
                {
                    console.debug("createGallery");
                }
                var gallery = {
                    name:$("#name").val(),
                    description:$("#description").val(),
                    sortorder:$("#sortorder").val(),
                    parentId:{id:$("#parentid").val()},
                    highlight:{id:$('#highlight').val()}
                };
                $.ajax({
                    type: "POST",
                    url: "/YourPersonalPhotographOrganiser/resources/galleries",
                    data: JSON.stringify(gallery),
                    success: function()
                    {
                        if (window.console && YourPersonalPhotographOrganiserBag.debug)
                        {
                            console.debug("Success");
                        }
                    },
                    contentType: "application/json"
                }).done(function( msg ) {
                    if (window.console && YourPersonalPhotographOrganiserBag.debug)
                    {
                        console.debug("Data Saved: " + msg );
                    }
                });
            }

            function updateGallery()
            {
                var gallery = YourPersonalPhotographOrganiserBag.galleries[YourPersonalPhotographOrganiserBag.index];
                if (gallery == null)
                {
                    return;
                }
                gallery.id = $('#galleryid').html();
                gallery.name= $('#name').val();
                gallery.description = $('#description').val();
                gallery.sortorder = $('#sortorder').val();
                gallery.parentId = {id:$('#parentid').val()};
                gallery.highlight = {id:$('#highlight').val()};
                $.ajax({
                    type: "PUT",
                    url: "/YourPersonalPhotographOrganiser/resources/galleries",
                    data: JSON.stringify(gallery),
                    success: function()
                    {
                        if (window.console && YourPersonalPhotographOrganiserBag.debug)
                        {
                            console.debug("Success");
                        }
                    },
                    contentType: "application/json"
                }).done(function( msg ) {
                    if (window.console && YourPersonalPhotographOrganiserBag.debug)
                    {
                        console.debug("Data Saved: " + msg );
                    }
                });
            }

            function deleteGallery()
            {
                var r=confirm("Are you sure?");
                if (r!=true)
                {
                    return;
                }
                var id = YourPersonalPhotographOrganiserBag.galleries[YourPersonalPhotographOrganiserBag.index].id;
                $.ajax({
                    type: "DELETE",
                    url: "/YourPersonalPhotographOrganiser/resources/galleries/" + id,
                    success: function()
                    {
                        if (window.console && YourPersonalPhotographOrganiserBag.debug)
                        {
                            console.debug("Success");
                        }
                    },
                    contentType: "application/json"
                }).done(function( msg ) {
                    if (window.console && YourPersonalPhotographOrganiserBag.debug)
                    {
                        console.debug("Data Saved: " + msg );
                    }                   });
            }

            function refreshPage()
            {
                $.get(
                '/YourPersonalPhotographOrganiser/resources/galleries'
                ,
                function(data)
                {
                    if (window.console && YourPersonalPhotographOrganiserBag.debug)
                    {
                        console.debug(data);
                    }
                    if (data == null)
                    {
                        alert("No galleries found.");
                        return;
                    }
                    YourPersonalPhotographOrganiserBag.galleries = data;
                    var buffer="<table><tr><th>id</th><th>name</th><th>description</th><th>parent</th></tr>";
                    for (i in data)
                    {
                        buffer+="<tr><td><a onclick=\"YourPersonalPhotographOrganiserBag.index="+i+";refreshPage();\">" + data[i].id + "</a></td><td>" + data[i].name + "</td><td>" + data[i].description + "</td><td>" + (data[i].parentId != null ? data[i].parentId.id : "-") + "</td></tr>";
                    }
                    buffer+="</table>";
                    $("#galleries").html(buffer);
                    var currentGallery = data[YourPersonalPhotographOrganiserBag.index];
                    $('#galleryid').html(currentGallery.id);
                    $('#name').val(currentGallery.name);
                    $('#description').val(currentGallery.description == null ? "" : currentGallery.description);
                    $('#sortorder').val(currentGallery.sortorder);
                    $('#parentid').val(currentGallery.parentId == null ? "" : currentGallery.parentId.id);
                    $('#highlight').val(currentGallery.highlight == null ? "" : currentGallery.highlight.id);
                } // end function data
                , "json"); // endget galleryphotograph
                // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )

            }

            function importPhotographs()
            {
                var id = YourPersonalPhotographOrganiserBag.galleries[YourPersonalPhotographOrganiserBag.index].id;
                $.get(
                '/YourPersonalPhotographOrganiser/resources/galleries/' + id + '/import',
                { location: $('#location').val() }
                ,
                function(data)
                {
                    if (window.console && YourPersonalPhotographOrganiserBag.debug)
                    {
                        console.debug(data);
                    }
                    alert(data);
                } // end function data
                , "json"); // endget galleryphotograph
                // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )

            }


            $(document).ready(function()
            {
                refreshPage();

                $('.refreshGallery').click(function(){refreshPage();});
                $('.createGallery').click(function(){createGallery();});
                $('.updateGallery').click(function(){updateGallery();});
                $('.deleteGallery').click(function(){deleteGallery();});
                $('.importPhotographs').click(function(){importPhotographs();});

                $('.backButton').click(function(){history.go(-1);});
            }); // end document ready
        </script>
        <link rel="stylesheet" href="/fancybox/jquery.fancybox-1.3.4.css" type="text/css" media="screen" />
        <link rel="stylesheet" href="css/yppo.css" type="text/css" media="screen" />
    </head>
    <body>
        <h1>Galleries</h1>
        <hr/>
        <div id="galleries"></div>
        <p>Gallery information</p>
        <p>Gallery id: <span id="galleryid"></span></p>
        <label for="name">Name</label>
        <input type="text" name="name" id="name"/>
        <br/>
        <label for="description">Description</label>
        <textarea name="description" id="description" rows="9" cols="120"></textarea>
        <br/>
        <label for="sortorder">Sort order</label>
        <input type="text" name="sortorder" id="sortorder" rows="9" cols="120"/>
        <br/>
        <label for="parentid">Parent id</label>
        <input type="text" name="parentid" id="parentid" />
        <br/>
        <label for="highlight">Highlight id</label>
        <input type="text" name="highlight" id="highlight" />
        <br/>
        <label for="location">Location</label>
        <input type="text" name="location" id="location" /><div class="importPhotographs myButton">Import Photographs</div>
        <br/>
        <hr/>
        <div class="backButton myButton">Back</div>
        <div class="refreshGallery myButton">Refresh</div>
        <div class="createGallery myButton">Create</div>
        <div class="updateGallery myButton">Update</div>
        <div class="deleteGallery myButton">Delete</div>
        <br/>
        <a href="location.jsp" >Locations</a>
        <a href="issues.jsp" >Issues</a>

        <a href="index.jsp" >Return</a>
    </body>
</html>
