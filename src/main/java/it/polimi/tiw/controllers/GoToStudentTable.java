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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.polimi.tiw.beans.RegisteredStudent;
import it.polimi.tiw.beans.UserBean;
import it.polimi.tiw.daos.ExamDAO;
import it.polimi.tiw.daos.StudentTableDAO;

/**
 * Servlet implementation class GoToAppello
 */
@WebServlet("/GoToStudentTable")
public class GoToStudentTable extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToStudentTable() {
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
		String loginpath = getServletContext().getContextPath() + "/index.html";
		UserBean u = null;
		HttpSession s = request.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		} else {
			u = (UserBean) s.getAttribute("user");
			if (!u.getCourse().equals("Docente")) {
				response.sendRedirect(loginpath);
				return;
			}
		}
		
		
		if(request.getParameter("selectedCourseID") == null || request.getParameter("date") == null) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "You have not selected a course or a date!");
			return;
		}
		
		
		StudentTableDAO stDAO = new StudentTableDAO(connection);
		List<RegisteredStudent> students = new ArrayList<>();
		
		
		int selectedCourseID =  Integer.parseInt(request.getParameter("selectedCourseID"));
		String stringDate = request.getParameter("date");
		//s.setAttribute("selectedDate", stringDate);
		//s.setAttribute("lastOrderCol", null);
        //s.setAttribute("lastOrderDir", null);

		try {
			students = stDAO.getStudentTable(selectedCourseID, stringDate, u.getId());
			System.out.println("ciao");
		} catch (SQLException e) {
			//throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database finding student table");
 		}
		
		ExamDAO eDAO = new ExamDAO(connection);
		String courseName = null;
		try {
			courseName = eDAO.findExamName(selectedCourseID);
		} catch (SQLException e) {
			//throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database finding student table");
 		}
		

		String path = "/WEB-INF/StudentTable.html";
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
        ctx.setVariable("studentTable", students);
        ctx.setVariable("courseID", selectedCourseID);
        ctx.setVariable("date", stringDate);
        ctx.setVariable("courseName", courseName);
        ctx.setVariable("publish", request.getParameter("pub"));   
        ctx.setVariable("verbalize", request.getParameter("verb"));   

		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
