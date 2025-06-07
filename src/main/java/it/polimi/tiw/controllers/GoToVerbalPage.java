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
 * Servlet implementation class GoToVerbalPage
 */
@WebServlet("/GoToVerbalPage")
public class GoToVerbalPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToVerbalPage() {
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
		
		if(request.getParameter("verbalID") == null) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "You have not selected a verbal!");
			return;
		}
		
		VerbalDAO vDAO = new VerbalDAO(connection);
		VerbalBean verb = new VerbalBean();
		int verbID;
		
		try {
			verbID = Integer.parseInt(request.getParameter("verbalID"));
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error: bad verbal ID parameter!");
			return;
		}
		
		String isNew = request.getParameter("isNew");
		
		try {
			verb = vDAO.getVerbal(verbID, u.getId());
		} catch (SQLException e) {
			//throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database finding a verbal");
			return;
 		}
		
		if(verb.getExamDate() == null) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error: you have not the authorization to see this verbal");
			return;
		}
		
		StudentTableDAO stDAO = new StudentTableDAO(connection);
		List<RegisteredStudent> studs = new ArrayList<RegisteredStudent>();
		
		try {
			studs = stDAO.getStudentsFromVerbal(verbID);		//i do not need to authorize the query because if this point is reached it means the previous check passed
		} catch (SQLException e) {
			//throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database finding student table");
			return;
 		}
		
		String path = "/WEB-INF/Verbal.html";
		JakartaServletWebApplication webApplication = JakartaServletWebApplication.buildApplication(getServletContext());
        WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
        ctx.setVariable("verbal", verb);
        ctx.setVariable("studentTable", studs);
        ctx.setVariable("isNew", isNew);

		templateEngine.process(path, ctx, response.getWriter());
		
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
