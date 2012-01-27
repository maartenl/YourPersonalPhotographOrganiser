<%--
    Document   : index
    Created on : Dec 18, 2011, 11:46:17 AM
    Author     : maartenl
--%>
<%@ page import="gallery.beans.GalleryPhotographFacadeLocal"%>
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
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Your Personal Photograph Organiser</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
        <script type="text/javascript" src="/fancybox/jquery.fancybox-1.3.4.pack.js"></script>
        <script type="text/javascript" src="/fancybox/jquery.mousewheel-3.0.4.pack.js"></script>
        <script type="text/javascript">
            $(document).ready(function() {

                /* This is basic - uses default settings */

                $("a#single_image").fancybox({'type' : 'image'});
                /* Using custom settings */

                $("a#inline").fancybox({
                    'hideOnContentClick': true
                });
            });
        </script>
        <link rel="stylesheet" href="/fancybox/jquery.fancybox-1.3.4.css" type="text/css" media="screen" />
        <link rel="stylesheet" href="css/yppo.css" type="text/css" media="screen" />
    </head>
    <body>
        <h1>Photograph</h1>
        <hr/>
        <a href="index.jsp">Return</a>
    </body>
</html>
