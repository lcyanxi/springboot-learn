import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author lichang
 * @date 2020/12/18
 */
public class JdbcConnectionDemo {

    static  final String url = "jdbc:mysql://localhost:3306/test" ;
    static  final String username = "root" ;
    static  final String password = "root" ;
    public static void main(String[] args) {
        try{
            //加载MySql的驱动类
            Class<?> forName = Class.forName("com.mysql.jdbc.Driver");
        }catch(ClassNotFoundException e){
            System.out.println("找不到驱动程序类 ，加载驱动失败！");
            e.printStackTrace() ;
        }
        try{
            Connection con = DriverManager.getConnection(url , username , password ) ;
        }catch(SQLException se){
            System.out.println("数据库连接失败！");
            se.printStackTrace() ;
        }
    }
}
