<%--
    Document   : index
    Created on : Dec 18, 2011, 11:46:17 AM
    Author     : maartenl
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
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Your Personal Photograph Organiser</title>
        <%@ include file="WEB-INF/includes/javascripts.jspf" %>
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
        <h1>File</h1>
        <%
            Photograph photo = new Photograph();
            String path = "";
            Long id = new Long(request.getParameter("id"));
        %><div class="photograph""><a id="single_image" href="/YourPersonalPhotographOrganiser/ImageServlet?id=<%= photo.getId()%>">
        <img src="/YourPersonalPhotographOrganiser/ImageServlet?id=<%= photo.getId()%>&size=medium"/></a></div>
        <div>
            <p>Id: <%= photo.getId()%></p>
            <p>Filename: <%= photo.getFilename()%></p>
            <p>Relative path: <%= photo.getRelativepath()%></p>
        </div><hr/><h3>General info</h3><div>
            <p>Path: <%= path%></p>
            <p>Size: <%= Files.size(path)%> bytes or <%= Files.size(path) / KILOBYTES%> kbytes or <%= Files.size(path) / MEGABYTES%> mbytes</p>
            <p>is Directory: <%= Files.isDirectory(path)%></p>
            <p>Is Regular file: <%= Files.isRegularFile(path)%></p>
            <p>Is Symbolic Link: <%= Files.isSymbolicLink(path)%></p>
            <p>Is hidden: <%= Files.isHidden(path)%></p>
        </div><hr/><h3>Basic file attributes</h3>
        <%
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        %>
        <p>Creation time: <%= attr.creationTime()%></p>
        <p>Access time: <%= attr.lastAccessTime()%></p>
        <p>Modified time: <%= attr.lastModifiedTime()%></p>

        <hr/><h3>Posix file attributes</h3>
        <%
            PosixFileAttributes attrPosix = Files.readAttributes(path, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        %>
        <p>Group: <%= attrPosix.group()%></p>
        <p>Owner: <%= attrPosix.owner()%></p>
        <p>Permissions: <%= attrPosix.permissions()%></p>
        <hr/>
        <a href="index.jsp">Return to Gallery</a>
        <a href="photo.jsp">Return to Photograph</a>
    </body>
</html>
