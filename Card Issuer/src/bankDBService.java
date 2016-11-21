import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class bankDBService {
	Connection conn;
	
	public bankDBService(){
		this.conn = null;
	}
	
	public void makeConnection(){
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			conn =  DriverManager.getConnection("jdbc:mysql://localhost:3306/card_validation","root","root");
		}catch(Exception e){ 
		System.out.println(e);
		}
	}
	
	public String validateCard(String card){
		String query = "select card from cardvalidation where card = " + "'" + card + "'";
		System.out.println(query);
		String result = null;
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()){
				result = rs.getString(1);
			}
			
		}catch(SQLException e){
			System.out.println(e);
		}
		return result;		
	}
		
}



