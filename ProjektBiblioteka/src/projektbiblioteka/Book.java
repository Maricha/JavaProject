/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projektbiblioteka;

import javafx.scene.image.Image;
import java.sql.Blob;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Marcin
 */
public class Book {
    public IntegerProperty id;
    public StringProperty autor;
    public StringProperty nazwa;
    public StringProperty desc;
    public StringProperty kategoria;
    public StringProperty ISBN;
    public Image zdjecie;
    
    public Book(Integer id, String autor, String nazwa, String desc, String kategoria, String ISBN) {
        this.id = new SimpleIntegerProperty(id);
	this.autor = new SimpleStringProperty(autor);
	this.nazwa = new SimpleStringProperty(nazwa);
        this.desc = new SimpleStringProperty(desc);
        this.kategoria = new SimpleStringProperty(kategoria);
        this.ISBN = new SimpleStringProperty(ISBN);
    }
    
    public Book(Integer id, String autor, String nazwa, String desc, String kategoria, String ISBN, Image zdjecie) {
        this.id = new SimpleIntegerProperty(id);
	this.autor = new SimpleStringProperty(autor);
	this.nazwa = new SimpleStringProperty(nazwa);
        this.desc = new SimpleStringProperty(desc);
        this.kategoria = new SimpleStringProperty(kategoria);
        this.ISBN = new SimpleStringProperty(ISBN);
        this.zdjecie = zdjecie;
    }
    
    public Book() {
    }
}
