/*
 *  Copyright (C) 2012 maartenl
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
package gallery.servlets;

import com.drew.imaging.ImageProcessingException;
import gallery.beans.PhotographBean;
import gallery.enums.ImageAngle;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author maartenl
 */
@WebServlet(name = "ImageServlet", urlPatterns =
{
    "/ImageServlet"
})
public class ImageServlet extends HttpServlet
{

    @EJB
    PhotographBean photographBean;

    private void writeError(String error, HttpServletResponse response) throws IOException
    {

        try (PrintWriter out =
                        response.getWriter();)
        {
            response.setContentType("text/html;charset=UTF-8");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ImageServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>" + error + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        String idString = request.getParameter("id");
        if (idString == null || "".equals(idString.trim()))
        {
            writeError("Photograph id not found.", response);
            return;
        }
        Long id = null;

        try
        {
            id = new Long(idString);
        } catch (NumberFormatException e)
        {
            writeError("Photograph id not a number.", response);
            return;
        }
        if (id == null || id <= 0l)
        {
            writeError("Photograph id invalid.", response);
            return;
        }

        File file = photographBean.getFile(id);
        ImageAngle angle;
        try
        {
            angle = photographBean.getAngle(id);
        } catch (ImageProcessingException ex)
        {
            throw new IOException(ex);
        }

        String filename = file.getName();
        String contentType = "text/html;charset=UTF-8";
        if (filename.toLowerCase().endsWith(".jpg"))
        {
            contentType = "image/jpeg";
        }
        if (filename.toLowerCase().endsWith(".jpeg"))
        {
            contentType = "image/jpeg";
        }
        if (filename.toLowerCase().endsWith(".gif"))
        {
            contentType = "image/gif";
        }
        if (filename.toLowerCase().endsWith(".png"))
        {
            contentType = "image/png";
        }
        response.setContentType(contentType);
        FileOperations.outputImage(file, response.getOutputStream(), request.getParameter("size"), angle);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
    {
        return "Short description";
    }// </editor-fold>
}
