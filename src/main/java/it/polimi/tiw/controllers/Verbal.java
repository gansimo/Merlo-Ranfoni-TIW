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
import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.polimi.tiw.beans.RegisteredStudent;
import it.polimi.tiw.beans.UserBean;
import it.polimi.tiw.beans.VerbalBean;
import it.polimi.tiw.daos.StudentTableDAO;
import it.polimi.tiw.daos.VerbalDAO;

/**
 * Servlet implementation class Verbal
 */
@WebServlet("/Verbal")
public class Verbal extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Verbal() {
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
		
		StudentTableDAO stDAO = new StudentTableDAO(connection);
		int selectedCourseID = (Integer) s.getAttribute("selectedCourseID");
		String selectedDate = (String) s.getAttribute("selectedDate");
		List<RegisteredStudent> students = new ArrayList<>();
		
		try {
			students = stDAO.getNewVerbalizedStudents(selectedCourseID, selectedDate);
			System.out.println("ciao");
		} catch (SQLException e) {
			//throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database finding student table");
 		}
		
		VerbalDAO vDAO = new VerbalDAO(connection);
		VerbalBean newVerbal = new VerbalBean();
		try {
			newVerbal = vDAO.createVerbal(selectedCourseID, selectedDate);
			System.out.println("ciao");
		} catch (SQLException e) {
			throw new ServletException(e);
			//response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database finding student table");
 		}
		
		try {
			vDAO.insertNewVerbalizedStudents(students, newVerbal);
			System.out.println("ciao");
		} catch (SQLException e) {
			throw new ServletException(e);
			//response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database finding student table");
 		}
		
		String path = "/WEB-INF/Verbal.html";
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
        ctx.setVariable("studentTable", students);
        ctx.setVariable("verbal", newVerbal);

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
