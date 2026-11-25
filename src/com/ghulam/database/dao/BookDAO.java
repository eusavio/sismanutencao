package com.ghulam.database.dao;
 
import com.ghulam.database.Database;
import com.ghulam.database.table.Book;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
 
public class BookDAO {
    // query (ALTERADAS: agora incluem GENRE e TAGS)
    private static final String INSERT_BOOK = "INSERT INTO BOOKDB(ID,NAME,AUTHOR,DESCRIPTION,STOCK,DATE1,GENRE,TAGS) VALUES(?,?,?,?,?,?,?,?)";
    private static final String DISPLAY_ALL_BOOK = "SELECT ID,NAME,AUTHOR,DESCRIPTION,STOCK,DATE1,GENRE,TAGS FROM BOOKDB";
    private static final String LAST_BOOK_ID = "SELECT ID FROM BOOKDB ORDER BY ID DESC";
    private static final String UPDATE_BOOK = "UPDATE BOOKDB SET NAME=?,AUTHOR=?,DESCRIPTION=?,STOCK=?,GENRE=?,TAGS=? WHERE ID=?";
    private static final String DELETE_BOOK = "DELETE FROM BOOKDB WHERE ID IN(";
    private static final String SEARCH_BOOKS = "SELECT ID,NAME,AUTHOR,DESCRIPTION,STOCK,DATE1,GENRE,TAGS FROM BOOKDB WHERE ID LIKE ? OR UPPER(NAME) LIKE ? OR UPPER(AUTHOR) LIKE ? OR UPPER(DESCRIPTION) LIKE ? OR STOCK LIKE ?";
    private static final String SEARCH_BOOK = "SELECT ID,NAME,AUTHOR,DESCRIPTION,STOCK,DATE1,GENRE,TAGS FROM BOOKDB WHERE ID=?";
    private static final String SEARCH_BOOKS_DATE = "SELECT ID,NAME,AUTHOR,DESCRIPTION,STOCK,DATE1,GENRE,TAGS FROM BOOKDB WHERE DATE1=?";
    // Variable
    private Connection conn = null;
    private PreparedStatement pStatement = null;
    private ResultSet resultSet=null;
    private Book book;
    // method
 
    //resolvendo issue 3 taina
    private static final Logger LOGGER = Logger.getLogger(BookDAO.class.getName());
 
    public BookDAO() {
        conn = Database.getConnection();
    }
 
    // NOVO: monta um objeto Book a partir de uma linha do banco
    private Book mapBook(ResultSet rs) throws SQLException {
        Book b = new Book(rs.getInt("ID"), rs.getString("NAME"), rs.getString("AUTHOR"),
                rs.getString("DESCRIPTION"), rs.getInt("STOCK"),
                rs.getDate("DATE1"));
        b.setGenre(rs.getString("GENRE"));
        b.setTags(rs.getString("TAGS"));
        return b;
    }
 
    public int addBook(Book book) {
        try {
            pStatement = conn.prepareStatement(INSERT_BOOK);
            pStatement.setInt(1, book.getId());
            pStatement.setString(2, book.getName());
            pStatement.setString(3, book.getAuthorName());
            pStatement.setString(4, book.getDescription());
            pStatement.setInt(5, book.getStock());
            pStatement.setDate(6, book.getDate());
            pStatement.setString(7, book.getGenre());
            pStatement.setString(8, book.getTags());
            return pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public Book getBook(int id){
        book=null;
        try {
            pStatement=conn.prepareStatement(SEARCH_BOOK);
            pStatement.setInt(1,id);
            resultSet=pStatement.executeQuery();
            if(resultSet.next()){
                book = mapBook(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }
    public int getLast() {
        try {
            pStatement = conn.prepareStatement(LAST_BOOK_ID);
            resultSet = pStatement.executeQuery();
            if(resultSet.next())
                return resultSet.getInt("ID");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int updateBook(Book book) {
        try {
            pStatement = conn.prepareStatement(UPDATE_BOOK);
            pStatement.setString(1, book.getName());
            pStatement.setString(2, book.getAuthorName());
            pStatement.setString(3, book.getDescription());
            pStatement.setInt(4, book.getStock());
            pStatement.setString(5, book.getGenre());
            pStatement.setString(6, book.getTags());
            pStatement.setInt(7, book.getId());
            return pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
 
    //resolvendo issue 3 taina
    public int deleteBook(int... id) {
        if (id == null || id.length == 0) {
            return 0;
        }
 
        StringBuilder deleteQuery = new StringBuilder(DELETE_BOOK);
        for (int i = 0; i < id.length; i++) {
            deleteQuery.append("?");
            if (i != id.length - 1) {
                deleteQuery.append(",");
            }
        }
        deleteQuery.append(")");
 
        try (PreparedStatement ps = conn.prepareStatement(deleteQuery.toString())) {
            for (int i = 0; i < id.length; i++) {
                ps.setInt(i + 1, id[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao excluir livro(s)", e);
        }
        return 0;
    }
 
    public ArrayList<Book> getBooksArrayList() {
        ArrayList<Book> booksList = new ArrayList<>();
        resultSet = getBooksResultSet();
        try {
            while (resultSet.next()) {
                book = mapBook(resultSet);
                booksList.add(book);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return booksList;
    }
    public ArrayList<Book> getBooksArrayList(String text) {
        ArrayList<Book> booksList = new ArrayList<>();
        resultSet = getBooksResultSet(text);
        try {
            while (resultSet.next()) {
                book = mapBook(resultSet);
                booksList.add(book);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return booksList;
    }
    public ArrayList<Book> getBooksArrayList(Date date) {
        ArrayList<Book> booksList = new ArrayList<>();
        resultSet = getBooksResultSet(date);
        try {
            while (resultSet.next()) {
                book = mapBook(resultSet);
                booksList.add(book);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return booksList;
    }
    private ResultSet getBooksResultSet() {
        try {
            pStatement = conn.prepareStatement(DISPLAY_ALL_BOOK);
            return pStatement.executeQuery();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return null;
    }
    private ResultSet getBooksResultSet(String text) {
        try {
            pStatement = conn.prepareStatement(SEARCH_BOOKS);
            pStatement.setString(1, (text + "%").toUpperCase());
            pStatement.setString(2, (text + "%").toUpperCase());
            pStatement.setString(3, (text + "%").toUpperCase());
            pStatement.setString(4, (text + "%").toUpperCase());
            pStatement.setString(5, (text + "%").toUpperCase());
            return pStatement.executeQuery();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return null;
    }
    private ResultSet getBooksResultSet(Date date) {
        try {
            pStatement = conn.prepareStatement(SEARCH_BOOKS_DATE);
            pStatement.setDate(1,date);
            return pStatement.executeQuery();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return null;
    }
 
    // ===================== NOVO: FILTROS =====================
 
    // Filtra por autor, gênero e/ou tag (qualquer um pode ser null ou vazio)
    public ArrayList<Book> filterBooks(String author, String genre, String tag) {
        ArrayList<Book> result = new ArrayList<>();
        if (conn == null) return result;
 
        StringBuilder q = new StringBuilder(
                "SELECT ID,NAME,AUTHOR,DESCRIPTION,STOCK,DATE1,GENRE,TAGS FROM BOOKDB WHERE 1=1");
        ArrayList<String> params = new ArrayList<>();
        if (author != null && !author.isEmpty()) {
            q.append(" AND AUTHOR = ?");
            params.add(author);
        }
        if (genre != null && !genre.isEmpty()) {
            q.append(" AND GENRE = ?");
            params.add(genre);
        }
        if (tag != null && !tag.isEmpty()) {
            // TAGS fica salva normalizada: "a,b,c" (sem espaços ao redor das vírgulas)
            q.append(" AND CONCAT(',', TAGS, ',') LIKE ?");
            String esc = tag.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
            params.add("%," + esc + ",%");
        }
 
        try (PreparedStatement ps = conn.prepareStatement(q.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapBook(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao filtrar livros", e);
        }
        return result;
    }
 
    // Valores distintos de uma coluna (AUTHOR ou GENRE) para preencher os combos
    public ArrayList<String> getDistinctValues(String column) {
        ArrayList<String> values = new ArrayList<>();
        if (conn == null) return values;
        String q = "SELECT DISTINCT " + column + " FROM BOOKDB WHERE " + column
                + " IS NOT NULL AND " + column + " <> '' ORDER BY " + column;
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(q)) {
            while (rs.next()) {
                values.add(rs.getString(1));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar valores distintos", e);
        }
        return values;
    }
 
    // Lista de tags únicas (separa as tags que estão guardadas como "a,b,c")
    public ArrayList<String> getDistinctTags() {
        TreeSet<String> tags = new TreeSet<>();
        if (conn == null) return new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT TAGS FROM BOOKDB WHERE TAGS IS NOT NULL AND TAGS <> ''")) {
            while (rs.next()) {
                for (String t : rs.getString(1).split(",")) {
                    if (!t.trim().isEmpty()) tags.add(t.trim());
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar tags", e);
        }
        return new ArrayList<>(tags);
    }
}
 