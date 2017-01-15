package projektbiblioteka;


import javafx.scene.image.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javafx.event.ActionEvent;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;

public class FXMLDocumentController implements Initializable{
    
    ObservableList<Book> books = FXCollections.observableArrayList();
    SortedList<Book> sortLista;
    FilteredList<Book> filterLista;
    private Connection polaczenie;
    
    @FXML 
    private TableColumn<Book, String> tableAutor;
    
    @FXML 
    private TableColumn<Book, String> tableNazwa;
    
    @FXML 
    private TableColumn<Book, String> tableDesc;
    
    @FXML 
    private TableColumn<Book, String> tableKategoria;
    
    @FXML 
    private TableColumn<Book, String> tableISBN;
    
    @FXML
    private TableView<Book> tableBooks;

    @FXML
    private TextField Nazwa;

    @FXML
    private TextArea Desc;

    @FXML
    private Button Add;
    
    @FXML
    private Button Usun;

    @FXML
    private TextField Autor;
    
    @FXML
    private TextField Kategoria;
     
    @FXML
    private TextField ISBN;
    
    @FXML
    private TextField Filter;

    @FXML
    private Button Anuluj;
    
    @FXML
    private Button Edytuj;
    
    @FXML
    private Button Dodaj;
    
    @FXML
    private ImageView showImage;
    
    @FXML
    private ListView Lista;

    
    //RealIndex index który naprawia błąd listy po filtrowaniu przy sortowaniu.
    
    public void alertError(String error) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Napotkano bląd");
        alert.setContentText(error);

        alert.showAndWait();
    }
    
        public void ButtonZdjecie(ActionEvent event) {
        FileChooser fc = new FileChooser();
        
        fc.getExtensionFilters().addAll(
            new ExtensionFilter("JPG", "*.jpg"),
            new ExtensionFilter("PNG", "*.png")
        );
        File selectedFile = fc.showOpenDialog(null);
        System.out.println(selectedFile);
        
        if(selectedFile != null) {
            Lista.getItems().add(selectedFile.getName());
            int index = tableBooks.getSelectionModel().getSelectedIndex();
            if(index != -1) {
                try {
                    int realIndex = sortLista.getSourceIndexFor(books, index);
                    Book book = books.get(realIndex);
                    PreparedStatement stat = polaczenie.prepareStatement("UPDATE books SET zdjecie = ? WHERE id = ?");
                    FileInputStream fis = new FileInputStream(selectedFile);
                    byte[] bye = new byte[(int)selectedFile.length()];
                    fis.read(bye);
                    
                    stat.setBytes(1, bye);
                    stat.setInt(2, book.id.get());
                    stat.execute();
                    stat.close();
                    
                    BufferedImage i = ImageIO.read(selectedFile);
                    Image i2 = SwingFXUtils.toFXImage(i, null);
                    showImage.setImage(i2);
                    
                    book.zdjecie = i2;
                  
                }
                catch(IOException e) {
                  alertError(e.toString());
                }
                catch(SQLException e) {
                    alertError(e.toString());
                }
            }
            
        } else {
            alertError("file is not valid");
        }
    }
    
    
    @FXML
    private void dodaj(ActionEvent event) {
        System.out.println("elo");
	try {
            
                {
                    if (Autor.getText().isEmpty() || Nazwa.getText().isEmpty() || Desc.getText().isEmpty()) {
                        System.out.println("cos pustego nie pusty");
                        alertError("Uzupełni wszystkie pola");
                        
                       
                    }
                    else {
                        PreparedStatement stat = polaczenie.prepareStatement("INSERT INTO books(autor, nazwa, desc, kategoria, ISBN) VALUES(?, ?, ?, ?, ?)");
                        stat.setString(1, Autor.getText());
                        stat.setString(2, Nazwa.getText());
                        stat.setString(3, Desc.getText());
                        stat.setString(4, Kategoria.getText());
                        stat.setString(5, ISBN.getText());
                        stat.execute();
                    }
                   
                }
                {  
                    if (Autor.getText().isEmpty() || Nazwa.getText().isEmpty() || Desc.getText().isEmpty()) {
                        System.out.println("cos pustego nie pusty drugieeeeeeeeee");
                       
                    }
                    else {
                    
                        Statement stat = polaczenie.createStatement();
                        String sql = "SELECT * FROM books ORDER BY id DESC LIMIT 1";
                        ResultSet rs = stat.executeQuery(sql);

                        books.add(new Book(rs.getInt("id"), rs.getString("autor"), rs.getString("nazwa"), rs.getString("desc"), rs.getString("kategoria"), rs.getString("ISBN")));
                    }
                }
	}
	catch(SQLException e) {
            alertError(e.getMessage());
	}
        catch(Exception e) {}
    }
    
    @FXML
    private void usun(ActionEvent event) {
	int index = tableBooks.getSelectionModel().getSelectedIndex();
	if(index != -1) {
	    try {
                int realIndex = sortLista.getSourceIndexFor(books, index);
                Book book = books.get(realIndex);
		PreparedStatement stat = polaczenie.prepareStatement("DELETE FROM books WHERE id = ?");
                stat.setInt(1, book.id.get());
		stat.executeUpdate();
		stat.close();
                
                books.remove(realIndex);
	    }
	    catch(SQLException e) {
		alertError(e.getMessage()); 
	    }
	}
    }
    
     @FXML
    private void edytuj(ActionEvent event) {
	int index = tableBooks.getSelectionModel().getSelectedIndex();
	if(index != -1) {
	    try {
                int realIndex = sortLista.getSourceIndexFor(books, index);
                Book book = books.get(realIndex);
                PreparedStatement stat = polaczenie.prepareStatement("UPDATE books SET autor = ?, nazwa = ?, desc = ?, kategoria = ?, ISBN = ? WHERE id = ?");
                stat.setString(1, Autor.getText());
                stat.setString(2, Nazwa.getText());
                stat.setString(3, Desc.getText());
                stat.setString(4, Kategoria.getText());
                stat.setString(5, ISBN.getText());
                stat.setInt(6, book.id.get());
                stat.execute();
                stat.close();
                
                book.autor.set(Autor.getText());
                book.nazwa.set(Nazwa.getText());
                book.desc.set(Desc.getText());
                book.kategoria.set(Kategoria.getText());
                book.ISBN.set(ISBN.getText());
            }
	    catch(SQLException e) {
		alertError(e.getMessage());
	    }
	}
    }
  
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
      try {
	    DriverManager.registerDriver(new com.mysql.jdbc.Driver());
	    DriverManager.registerDriver(new org.sqlite.JDBC());
	    polaczenie = DriverManager.getConnection("jdbc:sqlite:books.db", "root", "");
            System.out.println("Połączyłem się z bazą ");
            
            {
                Statement stat = polaczenie.createStatement();
                stat.execute("CREATE  TABLE  IF NOT EXISTS \"main\".\"books\" (\"id\" INTEGER PRIMARY KEY AUTOINCREMENT, \"nazwa\" VARCHAR, \"autor\" VARCHAR, \"desc\" TEXT, \"kategoria\" VARCHAR, \"ISBN\" VARCHAR, \"zdjecie\" BLOB)");
            }
	   
	    Statement stat = polaczenie.createStatement();
	    String sql = "SELECT * FROM books";
	    ResultSet rs = stat.executeQuery(sql);
	    while(rs.next()) {
                byte[] zdj = rs.getBytes("zdjecie");
                Image i2 = null;
                if(zdj != null) {
                    BufferedImage i = ImageIO.read(new ByteArrayInputStream(zdj));
                    i2 = SwingFXUtils.toFXImage(i, null);
                }
                books.add(new Book(rs.getInt("id"), rs.getString("autor"), rs.getString("nazwa"), rs.getString("desc"), rs.getString("kategoria"), rs.getString("ISBN"), i2));
	    }
	    
	    rs.close();
	}
	catch(SQLException e) {
	    e.printStackTrace();
            
	}
	catch(Exception e) {
	    e.printStackTrace();
	}
	
	tableAutor.setCellValueFactory(cellData -> cellData.getValue().autor);
        tableNazwa.setCellValueFactory(cellData -> cellData.getValue().nazwa);
        tableDesc.setCellValueFactory(cellData -> cellData.getValue().desc);
        tableKategoria.setCellValueFactory(cellData -> cellData.getValue().kategoria);
        tableISBN.setCellValueFactory(cellData -> cellData.getValue().ISBN);
        
        tableBooks.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null) return;
            Autor.setText(newValue.autor.get());
            Nazwa.setText(newValue.nazwa.get());
            Desc.setText(newValue.desc.get());
            Kategoria.setText(newValue.kategoria.get());
            ISBN.setText(newValue.ISBN.get());
            showImage.setImage(newValue.zdjecie);
	});
        
        filterLista = new FilteredList<>(books, p -> true);
        
        Filter.textProperty().addListener((observable, oldValue, newValue) -> {
            filterLista.setPredicate(book -> {
                if(newValue == null || newValue.isEmpty())
                    return true;
                
                String lowerCaseFilter = newValue.toLowerCase();

                if(book.autor.get().toLowerCase().contains(lowerCaseFilter))
                    return true;
                else if(book.nazwa.get().toLowerCase().contains(lowerCaseFilter))
                    return true;
                return false;
            });
        });
        
        sortLista = new SortedList(filterLista);
        sortLista.comparatorProperty().bind(tableBooks.comparatorProperty()); //Bindowanie przycisków do tabeli books
        tableBooks.setItems(sortLista);

    }
    

}
