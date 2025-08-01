package it.polimi.tiw.controllers;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.polimi.tiw.beans.RegisteredStudent;
import it.polimi.tiw.beans.UserBean;
import it.polimi.tiw.beans.VerbalBean;
import it.polimi.tiw.daos.StudentTableDAO;
import it.polimi.tiw.daos.VerbalDAO;

/**
 * Servlet implementation class VerbalizeGrades
 */
@WebServlet("/VerbalizeGrades")
public class VerbalizeGrades extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VerbalizeGrades() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(servletContext);
		WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(webApplication);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserBean u = null;
		HttpSession s = request.getSession();
		u = (UserBean) s.getAttribute("user");
		
		if(request.getParameter("selectedCourseID") == null || request.getParameter("date") == null) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "You have not selected a course or a date!");
			return;
		}
		
		int selectedCourseID;
		
		try {
		selectedCourseID =  Integer.parseInt(request.getParameter("selectedCourseID"));
		LocalDate date = LocalDate.parse(request.getParameter("date"), DateTimeFormatter.ISO_LOCAL_DATE);
		}catch (DateTimeParseException | NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "SQL injection is forbidden!");
			return;
		}
		
		String selectedDate = request.getParameter("date");
		StudentTableDAO stDAO = new StudentTableDAO(connection);
		int updatedStudents = 0;
		VerbalDAO vDAO = new VerbalDAO(connection);
		VerbalBean newVerbal = new VerbalBean();
		boolean transactionCompletedSuccessfully = false;
		
		List<RegisteredStudent> students = new ArrayList<>();
		
		try {
			connection.setAutoCommit(false);
			updatedStudents = stDAO.verbalizeGrades(selectedCourseID, selectedDate, u.getId());
			if(updatedStudents > 0) {
				students = stDAO.getNewVerbalizedStudents(selectedCourseID, selectedDate, u.getId());
				newVerbal = vDAO.createVerbal(selectedCourseID, selectedDate);
				vDAO.insertNewVerbalizedStudents(students, newVerbal);
			}
			connection.commit();
		    transactionCompletedSuccessfully = true;
		} catch (SQLException e) {
			e.printStackTrace();
		    try {
		        if (connection != null) {
		            //rollback in caso di errore
		            connection.rollback();
		        }
		    } catch (SQLException ex) {
		        System.err.println("Errore durante il tentativo di rollback: " + ex.getMessage());
		        ex.printStackTrace();
		        //eventuale errore di rollback (grave, problemi di connessione)
		    }
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database verbalizing grades");
			return;
 		} finally {
 		    try {
 		        if (connection != null) {
 		            //ripristino auto-commit allo stato di default
 		            connection.setAutoCommit(true);
 		        }
 		    } catch (SQLException ex) {
 		        System.err.println("Errore durante il ripristino dell'auto-commit: " + ex.getMessage());
 		        ex.printStackTrace();
 		    }
 		}
		
		if(transactionCompletedSuccessfully) {
			if(updatedStudents == 0)
				response.sendRedirect(request.getContextPath() + "/GoToStudentTable?selectedCourseID=" + selectedCourseID + "&date=" + URLEncoder.encode(selectedDate, "UTF-8") + "&verb=false");
			else 
				response.sendRedirect(request.getContextPath() + "/GoToVerbalPage?verbalID=" + newVerbal.getID() + "&isNew=true");	
			}
	}
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}

}
