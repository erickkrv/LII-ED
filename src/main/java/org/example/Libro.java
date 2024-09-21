package org.example;

import javafx.util.Pair;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.BitSet;

public class Libro{
    private long ISBN;
    private String titulo;
    private String autor;
    private String categoria;
    private String precio;
    private int stock;

    private String encodedNameHuffman;
    private String encodedNameArithmetic;

    public Libro(long ISBN, String titulo, String autor, String categor, String precio, int stock) {
        this.ISBN = ISBN;
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categor;
        this.precio = precio;
        this.stock = stock;
    }
    //Gets y sets
    public long getISBN() {
        return ISBN;
    }
    public void setISBN(int ISBN) {
        this.ISBN = ISBN;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public String getPrecio() {
        return precio;
    }
    public void setPrecio(String precio) {
        this.precio = precio;
    }
    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }

    //Método para codificar el nombre del libro en Huffman y en Arithmetic
    public void encodeNames(){
        HuffmanTree arbol = new HuffmanTree();
        arbol.BuildTree(this.titulo, null);
        Pair<BitSet, Integer> encoded = arbol.Encode(this.titulo);
        this.encodedNameHuffman = HuffmanTree.bitSetToString(encoded.getKey(), encoded.getValue());

        ArithmeticCompression arithmeticCompression = new ArithmeticCompression(this.titulo);
        var encodedArithmetic = arithmeticCompression.Compress(this.titulo);
        this.encodedNameArithmetic = ArithmeticCompression.GetBinaryString(encodedArithmetic);
    }
    public int getOriginalSize(){
        return this.titulo.length() * 2;
    }
    public int getHuffmanSize(){
        return this.encodedNameHuffman.length();
    }
    public int getArithmeticSize(){
        return this.encodedNameArithmetic.length() / 8;
    }
    //Método toString
    @Override
    public String toString() {
        return "{\"isbn\":"  + "\"" + ISBN + "\"" + ",\"name\":" + "\"" + titulo +"\"" + ",\"author\":" + "\"" + autor + "\"" +
                ",\"category\":" + "\"" + categoria + "\"" + ",\"price\":" + "\"" + precio + "\"" + ",\"quantity\":" + "\"" + stock + "\"" + ",\"namesize\":" + "\"" + getOriginalSize() + "\"" + ",\"namesizehuffman\":" + "\"" + getHuffmanSize() + "\"" + ",\"namesizearithmetic\":" + "\"" + getArithmeticSize() + "\"}";
    }
}
