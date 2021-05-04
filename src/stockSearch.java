

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Servlet implementation class stockSearch
 */
@WebServlet("/stockSearch")
public class stockSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		String ticker = request.getParameter("ticker");
		String UserID = request.getParameter("UserID").replaceAll("\\s+","");
		PrintWriter out = response.getWriter();
		try {
			//company description
			URL tiingo = new URL("https://api.tiingo.com/tiingo/daily/" + ticker + "?token=bbeccb121922092c60ef9bcbc189a32d84881c2f");
	        URLConnection yc = tiingo.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
	        String inputLine = in.readLine();
	        JsonObject jsonObj = JsonParser.parseString(inputLine).getAsJsonObject();
	        //company daily quote
			tiingo = new URL("https://api.tiingo.com/tiingo/daily/" + ticker + "/prices?token=bbeccb121922092c60ef9bcbc189a32d84881c2f");
	        yc = tiingo.openConnection();
	        in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
	        inputLine = in.readLine();
	        JsonArray jsonArray = JsonParser.parseString(inputLine).getAsJsonArray();
	        jsonArray.add(jsonObj);
	        //company latest stock price
			tiingo = new URL("https://api.tiingo.com/iex?tickers=" + ticker + "&token=bbeccb121922092c60ef9bcbc189a32d84881c2f");
	        yc = tiingo.openConnection();
	        in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
	        inputLine = in.readLine();
	        JsonArray jsonArray2 = JsonParser.parseString(inputLine).getAsJsonArray();
	        jsonObj = (JsonObject)jsonArray2.get(0); 
	        jsonArray.add(jsonObj);
	        //checking if this company is a favorite
	        if(!UserID.contains("none")) {
	        	int ID = Integer.parseInt(UserID);
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
	    		try {	
	    			conn = DriverManager.getConnection(Utils.connecter);
	    			String query = "SELECT * from Users u, Favorites f WHERE u.UserID = ? and f.ticker = ? and u.UserID = f.UserID";
	    			ps = conn.prepareStatement(query);
	    			ps.setInt(1, ID);
	    			ps.setString(2, ticker);
	    			rs = ps.executeQuery();		
	    			
	    			if(rs.next()) {
	    				response.setContentType("text/plain");
	    				jsonObj = JsonParser.parseString("{ \"Favorite\": \"yes\"}").getAsJsonObject();
	    			}else {
	    				response.setContentType("text/plain");
	    				jsonObj = JsonParser.parseString("{ \"Favorite\": \"no\"}").getAsJsonObject();
	    			}
	    			jsonArray.add(jsonObj);
	    			
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
	        response.setContentType("application/json");
	        out.println(jsonArray);
	        in.close();
		}catch(FileNotFoundException e) {
			out.println("Not Found");
		}catch (Exception e){
		
			System.out.println(e);
		}
	}
}
