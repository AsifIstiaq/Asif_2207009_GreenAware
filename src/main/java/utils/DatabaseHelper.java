//package utils;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//public class DatabaseHelper {
//    private static final String DB_URL = "jdbc:sqlite:greenaware.db";
//    private static boolean initialized = false;
//
//    public static Connection getConnection() throws SQLException {
//        try {
//            Class.forName("org.sqlite.JDBC");
//            Connection conn = DriverManager.getConnection(DB_URL);
//            if (!initialized) {
//                initializeDatabase(conn);
//                initialized = true;
//            }
//            return conn;
//        } catch (ClassNotFoundException e) {
//            throw new SQLException("SQLite JDBC driver not found", e);
//        }
//    }
//
//    private static void initializeDatabase(Connection conn) {
//        try (Statement stmt = conn.createStatement()) {
//
//            // Workers table
//            stmt.execute("""
//                CREATE TABLE IF NOT EXISTS workers (
//                    id INTEGER PRIMARY KEY AUTOINCREMENT,
//                    name TEXT NOT NULL,
//                    phone TEXT,
//                    email TEXT UNIQUE,
//                    username TEXT UNIQUE,
//                    password TEXT,
//                    specialization TEXT,
//                    status TEXT DEFAULT 'AVAILABLE',
//                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
//                )
//            """);
//
//            // Work Progress table
//            stmt.execute("""
//                CREATE TABLE IF NOT EXISTS work_progress (
//                    id INTEGER PRIMARY KEY AUTOINCREMENT,
//                    action_id INTEGER NOT NULL,
//                    worker_id INTEGER NOT NULL,
//                    worker_name TEXT NOT NULL,
//                    worker_phone TEXT,
//                    description TEXT,
//                    photo_path TEXT,
//                    location TEXT,
//                    status TEXT DEFAULT 'PENDING',
//                    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//                    FOREIGN KEY (action_id) REFERENCES actions(id),
//                    FOREIGN KEY (worker_id) REFERENCES workers(id)
//                )
//            """);
//
//            stmt.execute("""
//                CREATE TABLE IF NOT EXISTS actions (
//                    id INTEGER PRIMARY KEY AUTOINCREMENT,
//                    worker_id INTEGER,
//                    worker_name TEXT,
//                    action_note TEXT,
//                    deadline TEXT,
//                    status TEXT DEFAULT 'PENDING',
//                    resolution_details TEXT,
//                    completed_date TEXT,
//                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//                    location TEXT
//                )
//            """);
//
//            stmt.execute("""
//                CREATE TABLE IF NOT EXISTS users (
//                    id INTEGER PRIMARY KEY AUTOINCREMENT,
//                    full_name TEXT NOT NULL,
//                    email TEXT NOT NULL UNIQUE,
//                    username TEXT NOT NULL UNIQUE,
//                    password TEXT NOT NULL,
//                    phone TEXT,
//                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
//                )
//            """);
//
//            stmt.execute("""
//                CREATE TABLE IF NOT EXISTS admins (
//                    id INTEGER PRIMARY KEY AUTOINCREMENT,
//                    username TEXT NOT NULL UNIQUE,
//                    password TEXT NOT NULL,
//                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
//                )
//            """);
//
//            stmt.execute("""
//                CREATE TABLE IF NOT EXISTS reports (
//                    id INTEGER PRIMARY KEY AUTOINCREMENT,
//                    user_id INTEGER NOT NULL,
//                    category_name TEXT NOT NULL,
//                    incident_type TEXT,
//                    location TEXT NOT NULL,
//                    date_reported TEXT NOT NULL,
//                    severity TEXT NOT NULL,
//                    description TEXT,
//                    image_path TEXT,
//                    final_photo_path TEXT,
//                    status TEXT DEFAULT 'PENDING',
//                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//                    FOREIGN KEY (user_id) REFERENCES users(id)
//                )
//            """);
//
//            stmt.execute("""
//                CREATE TABLE IF NOT EXISTS report_categories (
//                    id INTEGER PRIMARY KEY AUTOINCREMENT,
//                    name TEXT NOT NULL UNIQUE
//                )
//            """);
//
//            System.out.println("Database initialized successfully");
//        } catch (SQLException e) {
//            System.err.println("Error initializing database: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}
