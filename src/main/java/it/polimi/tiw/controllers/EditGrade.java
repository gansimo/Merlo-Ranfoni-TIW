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
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.polimi.tiw.beans.RegisteredStudent;
import it.polimi.tiw.beans.UserBean;
import it.polimi.tiw.daos.CourseDAO;
import it.polimi.tiw.daos.ExamDAO;
import it.polimi.tiw.daos.StudentTableDAO;

/**
 * Servlet implementation class EditGrade
 */
@WebServlet("/EditGrade")
public class EditGrade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditGrade() {
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
		
		if(request.getParameter("selectedStudID") == null) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "You have not selected a student!");
			return;
		}
		
		int selectedStudentID;
		int selectedCourseID;
		
		try {
			selectedCourseID = Integer.parseInt(request.getParameter("selectedCourseID"));
			selectedStudentID = Integer.parseInt(request.getParameter("selectedStudID"));
			LocalDate.parse(request.getParameter("date"), DateTimeFormatter.ISO_LOCAL_DATE).toString();
		} catch(NumberFormatException | DateTimeParseException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error: wrong students or date parameters!");
			return;
		}
		
		String selectedDate = request.getParameter("date");
		
		StudentTableDAO stDAO = new StudentTableDAO(connection);
		RegisteredStudent stud = new RegisteredStudent();
	
		try {
			stud = stDAO.getRegisteredStudent(selectedCourseID, selectedDate, selectedStudentID, u.getId());
		} catch (SQLException e) {
			//throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database finding student");
			return;
 		}
		
		if(stud.getMatr() == null || stud.getMatr().isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "You cannot update the grade of this student!");
			return;
		}
		
		if(!(stud.getState().equals("inserito") || stud.getState().equals("non inserito"))) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "You have already updated the grade of this student!");
			return;
		}
		
		ExamDAO eDAO = new ExamDAO(connection);
		String courseName = null;
		
		try {
			courseName = eDAO.findExamName(selectedCourseID);
		} catch (SQLException e) {
			//throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database finding student");
			return;
 		}

		String path = "/WEB-INF/Edit-Grade.html";
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
        ctx.setVariable("selectedStudent", stud);
        ctx.setVariable("examDate", selectedDate);
        ctx.setVariable("courseID", selectedCourseID);
        ctx.setVariable("studID", selectedStudentID);
        ctx.setVariable("courseName", courseName);

		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserBean u = null;
		HttpSession s = request.getSession();
		u = (UserBean) s.getAttribute("user");
		
		if(request.getParameter("selectedCourseID") == null || request.getParameter("date") == null) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "You have not selected a course or a date!");
			return;
		}
		
		if(request.getParameter("selectedStudentID") == null) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "You have not selected a student!");
			return;
		}
		
		if(request.getParameter("newGrade") == null) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "You have not selected a new grade!");
			return;
		}
		
		try {
			Integer.parseInt(request.getParameter("selectedCourseID"));
			Integer.parseInt(request.getParameter("selectedStudentID"));
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error: invalid course ID or student ID!");
			return;
		}
		
		StudentTableDAO stDAO = new StudentTableDAO(connection);
		
		String studentID = request.getParameter("selectedStudentID");
		int selectedStudID = Integer.parseInt(studentID);
		int selectedCourseID =  Integer.parseInt(request.getParameter("selectedCourseID"));
		String selectedDate = request.getParameter("date");
		String grade = request.getParameter("newGrade");

	    try {
	    	LocalDate date = LocalDate.parse(request.getParameter("date"), DateTimeFormatter.ISO_LOCAL_DATE);
	    	if(!grade.equals("assente") && !grade.equals("rimandato") && !grade.equals("riprovato") && !grade.equals("30 e lode")) {
		        int value = Integer.parseInt(grade);
		        if(value < 18 || value > 30) {
		        	response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error: invalid grade!");
					return;
		        }
	    	}
	    } catch (DateTimeParseException | NumberFormatException e) {
	    	response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "SQL injection is forbidden!");
	    	return;
	    }
		
		try {
			stDAO.updateGrade(selectedCourseID, selectedDate, selectedStudID, grade, "inserito", u.getId());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error: cannot find any of your students with this parameters!");
			return;
		}
		
		
		response.sendRedirect(request.getContextPath() + "/GoToStudentTable?selectedCourseID=" + selectedCourseID + "&date=" + URLEncoder.encode(selectedDate, "UTF-8"));
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
