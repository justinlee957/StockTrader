

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet("/getFavorites")
public class getFavorites extends HttpServlet {
	private static final long serialVersionUID = 1L;  

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		int UserID = Integer.parseInt(request.getParameter("UserID").replaceAll("\\s+",""));
		try {
		    Class.forName(Utils.driver);
		} 
		catch (ClassNotFoundException e) {
		    e.printStackTrace();
		} 
		Connection conn = null;
		Statement st = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		PrintWriter out = response.getWriter();
		try {	
			JsonArray jsonArray = new JsonArray();
			conn = DriverManager.getConnection(Utils.connecter);
			String query = "SELECT ticker, name_ FROM Favorites f, Users u WHERE u.UserID = ? and f.UserID=u.UserID";
			ps = conn.prepareStatement(query);
			ps.setInt(1, UserID);
			rs = ps.executeQuery();
			if(rs.next()) {
				do {
					String ticker = rs.getString("ticker");
					String name = rs.getString("name_");
					URL tiingo = new URL("https://api.tiingo.com/iex?tickers=" + ticker + "&token=bbeccb121922092c60ef9bcbc189a32d84881c2f");
					URLConnection yc = tiingo.openConnection();
					BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
					String inputLine = in.readLine();
					JsonObject jsonObj = (JsonObject) JsonParser.parseString(inputLine).getAsJsonArray().get(0);
					jsonObj.add("name", new Gson().toJsonTree(name));
					jsonArray.add(jsonObj);
				}while(rs.next());
				response.setContentType("application/json");
				out.println(jsonArray);
			}else {
				response.setContentType("text/plain");
				out.println("No Favorites");
			}
			
		}catch(SQLException sqle) {
			System.out.println ("SQLException: " + sqle.getMessage());
		}finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				System.out.println("sqle: " + sqle.getMessage());
			}
		}
	}
}
