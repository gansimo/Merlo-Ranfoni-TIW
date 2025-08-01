package it.polimi.tiw.controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import it.polimi.tiw.beans.UserBean;
import it.polimi.tiw.beans.*;
import it.polimi.tiw.daos.*;



@WebServlet("/Reject")
public class Reject extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public Reject() {
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
		
		if(request.getParameter("selectedCourseID") == null || request.getParameter("selectedExam") == null) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error: you have not selected a course ID or an exam date!");
			return;
		}
		
		int selectedCourseID;
		
		try {
			selectedCourseID = Integer.parseInt(request.getParameter("selectedCourseID"));
			LocalDate date = LocalDate.parse(request.getParameter("selectedExam"), DateTimeFormatter.ISO_LOCAL_DATE);
		}catch (DateTimeParseException | NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "SQL injection is forbidden!");
			return;
		}
		
		String date = request.getParameter("selectedExam");
		
		
		String path = "/WEB-INF/StudentRejectMarkPage.html";
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
        ctx.setVariable("selectedCourseID", selectedCourseID);
        ctx.setVariable("selectedExam", date);
        
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserBean u = null;
		HttpSession s = request.getSession();
		u = (UserBean) s.getAttribute("user");
		
		if(request.getParameter("selectedCourseID") == null || request.getParameter("selectedExam") == null) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error: you have not selected a course ID or an exam date!");
			return;
		}
		
		int selectedCourseID;
		
		try {
		selectedCourseID = Integer.parseInt(request.getParameter("selectedCourseID"));
		LocalDate date = LocalDate.parse(request.getParameter("selectedExam"), DateTimeFormatter.ISO_LOCAL_DATE);
		}catch (DateTimeParseException | NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "SQL injection is forbidden!");
			return;
		}
		
		String date = request.getParameter("selectedExam");

		ExamDAO eDAO = new ExamDAO(connection);
		
		try {
			eDAO.rejectMark(selectedCourseID, date, u.getId());
		} catch (SQLException e) {
			//throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database rejecting the mark");
 		}
		
		
		response.sendRedirect(request.getContextPath() + "/SearchRound?selectedCourseID="+ selectedCourseID + "&selectedExam=" + URLEncoder.encode(date, "UTF-8"));

		
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
