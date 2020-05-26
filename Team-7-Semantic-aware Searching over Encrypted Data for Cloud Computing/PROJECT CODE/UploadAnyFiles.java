package Fileupload;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.security.*;
import java.security.spec.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import databaseconnection.*;
@WebServlet("/UploadAnyFiles")
@MultipartConfig(maxFileSize = 16177215)	// upload file's size up to 16MB
public class UploadAnyFiles extends HttpServlet {
	
	
	Connection conn = null;	
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// gets values of text fields
		HttpSession session=request.getSession();


		//out.println(user);
		try{

			conn =databasecon.getconnection();
			Statement st2 = conn.createStatement();
			
		
		InputStream inputStream = null,inputStream2 = null;	// input stream of the upload file
		// obtains the upload file part in this multipart request
		Part filePart = request.getPart("file");
		
		String fileName = getFileName(filePart);
		
        //Get all the parts from request and write it to the file on server
        


		if (filePart != null) {
		
			// prints out some information for debugging
			
			System.out.println(filePart.getName());
			System.out.println(filePart.getSize());
			System.out.println(filePart.getContentType());
			System.out.println("fileName"+fileName);
			// obtains input stream of the upload file
			
			inputStream = filePart.getInputStream();
			//inputStream2 = filePart.getInputStream();
	
		}
			//	 byte[] buffer = new byte[inputStream2.available()];
			//		inputStream2.read(buffer);

			//		 File targetFile = new File(fileName);
			//		OutputStream outStream = new FileOutputStream(targetFile);
			//		outStream.write(buffer);
		
			
				Statement st1=conn.createStatement();
				st1.executeUpdate("delete from loadfile");
			String sql = "insert into loadfile values(?,?)";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, fileName);					
			if (inputStream != null) {
				// fetches input stream of the upload file for the blob column
				statement.setBinaryStream(2, inputStream,(int) filePart.getSize());
			}
			

			// sends the statement to the database server
			int row = statement.executeUpdate();
			System.out.println("r="+row);
			if (row > 0) {
				Random randomGenerator = new Random();
			int randomInt=0;
		randomInt = randomGenerator.nextInt(10000000);
				getServletContext().getRequestDispatcher("/access.jsp?id="+randomInt+" ").forward(request, response);
			}

			}catch(Exception e1){e1.printStackTrace();}
		 finally {
			if (conn != null) {
				// closes the database connection
				try {
					conn.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			// sets the message in request scope
		//	request.setAttribute("Message", message);
			
			// forwards to the message page
			
		}
	}
	
	
	  private String getFileName(final Part part) {

    final String partHeader = part.getHeader("content-disposition");

    String[] sections = partHeader.split(";");

    for (String content : sections) {

      if (content.trim().startsWith("filename")) {

        return content.substring(content.indexOf('=') + 1).trim()

            .replace("\"", "");

      }

    }

    return null;

  }
	
}