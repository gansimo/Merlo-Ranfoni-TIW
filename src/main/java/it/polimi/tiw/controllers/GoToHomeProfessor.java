package it.polimi.tiw.controllers;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.polimi.tiw.beans.Course;
import it.polimi.tiw.beans.Exam;
import it.polimi.tiw.beans.UserBean;
import it.polimi.tiw.daos.CourseDAO;
import it.polimi.tiw.daos.ProfessorDAO;


/**
 * Servlet implementation class HomeProfessor
 */
@WebServlet("/GoToHomeProfessor")
public class GoToHomeProfessor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;

	public GoToHomeProfessor() {
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
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserBean u = null;
		HttpSession s = request.getSession();
		u = (UserBean) s.getAttribute("user");
	
		ProfessorDAO pDAO = new ProfessorDAO(connection);
		List<Course> courses = new ArrayList<>();
		try {
			courses = pDAO.findProfessorCourses(u.getId());
		} catch (SQLException e) {
			//throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database finding professor courses");
 		}

		String path = "/WEB-INF/HomeProfessor.html";
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
        ctx.setVariable("availableCourses", courses);

		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserBean u = null;
		HttpSession s = request.getSession();
		u = (UserBean) s.getAttribute("user");
		int selectedCourseID;
		
		try {
		selectedCourseID = Integer.parseInt(request.getParameter("scelta"));
		}catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "SQL injection is forbidden!");
			return;
		}

		CourseDAO cDAO = new CourseDAO(connection);
		List<Exam> exams = new ArrayList<>();
		
		try {
			exams = cDAO.findExams(selectedCourseID, u.getId());
		} catch (SQLException e) {
			//throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database finding professor courses");
 		}
		
		
		ProfessorDAO pDAO = new ProfessorDAO(connection);
		List<Course> courses = new ArrayList<>();
		try {
			courses = pDAO.findProfessorCourses(u.getId());
		} catch (SQLException e) {
			//throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database finding professor courses");
 		}
		
		String path = "/WEB-INF/HomeProfessor.html";
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
        ctx.setVariable("availableDates", exams);
        ctx.setVariable("availableCourses", courses);
        ctx.setVariable("selectedCourseID", selectedCourseID);

		templateEngine.process(path, ctx, response.getWriter());
		
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
