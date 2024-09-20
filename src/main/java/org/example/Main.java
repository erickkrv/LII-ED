package org.example;

import javafx.util.Pair;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    static public HashMap<String, Libro> HashLibros = new HashMap<String, Libro>();
    static public HashMap<String, String> HashNombres= new HashMap<String, String>();
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while(true){
            HuffmanTree huffmanTree = new HuffmanTree();
            String mensaje = "Don Quijote de la Mancha";
            // Codificar
            ArithmeticCompression codificar = new ArithmeticCompression(mensaje);
            var encoded = codificar.Compress(mensaje);
            String comprimido = "";
            for(byte b : encoded.toByteArray()){
                comprimido += b;
            }
            // System.out.println("Mensaje codificado: " + comprimido);

            ByteArrayInputStream input = new ByteArrayInputStream(encoded.toByteArray());
            // Decodificar
            var decoded = codificar.Decompress(input, mensaje.length());
            System.out.println("Mensaje original: " + mensaje);
            System.out.println("Mensaje decodificado Aritmetico: " + decoded);

            huffmanTree.BuildTree(mensaje, null);
            Pair<BitSet, Integer> encodedHuffman = huffmanTree.Encode(mensaje);
            // var decodedHuffman = huffmanTree.Decode(encodedHuffman);
            // System.out.println("Mensaje decodificado Huffman: " + decodedHuffman);

            //Debug
            System.out.println("Mensaje original: " + mensaje);
            System.out.println("Mensaje codificado Huffman: " + HuffmanTree.bitSetToString(encodedHuffman.getKey(), encodedHuffman.getValue()));
            System.out.println("Mensaje codificado aritmético: " + ArithmeticCompression.GetBinaryString(encoded));

            // Calcular tamaño
            calcularTamaño(mensaje, HuffmanTree.bitSetToString(encodedHuffman.getKey(), encodedHuffman.getValue()), ArithmeticCompression.GetBinaryString(encoded));

            System.out.println("Laboratorio 2 Erick Rivas");
            System.out.println("1. Importar CSV");
            System.out.println("2. Salir");
            System.out.println("Ingrese una opción: ");

            int opcion = 0;
            //Validar opción
            try{
                opcion = scanner.nextInt();
            }catch(Exception e){
            }
            scanner.nextLine();
            //Evaluar opción
            switch(opcion){
                case 1:
                    importarLibros();
                    break;
                case 2:
                    System.out.print("Saliendo del programa...");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }
    private static void importarLibros() {
        String ultimaLinea = "";
        JFileChooser archivo = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos CSV", "csv");
        archivo.setFileFilter(filtro);

        int seleccion = archivo.showOpenDialog(null);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            try {
                File archivoSeleccionado = archivo.getSelectedFile();
                // Leer archivo
                try (BufferedReader br = new BufferedReader(new FileReader(archivoSeleccionado));
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("libros_encontrados.txt", true)))) {
                    String linea;

                    StringBuilder sb = new StringBuilder();
                    while ((linea = br.readLine()) != null) {
                        ultimaLinea = linea;

                        if (linea.startsWith("INSERT;")) {
                            String datos = linea.substring(7).trim();
                            JSONObject json = new JSONObject(datos);
                            String isbnActual = json.getString("isbn");
                            // Crear libro
                            Libro libro = new Libro(
                                    json.getLong("isbn"),
                                    json.getString("name"),
                                    json.getString("author"),
                                    json.getString("category"),
                                    json.getString("price"),
                                    json.getInt("quantity")
                            );
                            HashLibros.put(isbnActual, libro);
                            HashNombres.put(json.getString("name"), isbnActual);
                        }

                        if (linea.startsWith("DELETE;")) {
                            String datos = linea.substring(7).trim();
                            JSONObject json = new JSONObject(datos);
                            String isbnActual = json.getString("isbn");
                            HashLibros.remove(isbnActual);
                            HashNombres.remove(isbnActual);
                        }

                        if (linea.startsWith("PATCH;")) {
                            String datos = linea.substring(6).trim();
                            JSONObject json = new JSONObject(datos);
                            String isbnActual = json.getString("isbn");
                            Libro libro = HashLibros.get(isbnActual);
                            if (libro != null) {
                                if (json.has("name")) {
                                    HashNombres.remove(libro.getTitulo());
                                    HashNombres.put(json.getString("name"), isbnActual);
                                    libro.setTitulo(json.getString("name"));
                                }
                                if (json.has("author")) {
                                    libro.setAutor(json.getString("author"));
                                }
                                if(json.has("category")){
                                    libro.setCategoria(json.getString("category"));
                                }
                                if (json.has("price")) {
                                    libro.setPrecio(json.getString("price"));
                                }
                                if (json.has("quantity")) {
                                    libro.setStock(json.getInt("quantity"));
                                }
                            }
                        }

                        if (linea.startsWith("SEARCH;")) {
                            String datos = linea.substring(7).trim();
                            JSONObject json = new JSONObject(datos);
                            String nombreABuscar = json.getString("name");
                            String isbnBuscado = HashNombres.get(nombreABuscar);
                            Libro libro = HashLibros.get(isbnBuscado);
                            if (libro != null) {
                                sb.append(libro.toString()).append("\n");
                            }
                        }
                    }
                    writer.write(sb.toString());
                }
                System.out.println("CSV importado correctamente");
            } catch (Exception e) {
                System.err.println("Error al importar los libros: " + e.getMessage() + " en la línea: " + ultimaLinea);
            }
        }
    }
    private static void calcularTamaño(String original, String comprimidoHuffman, String comprimidoAritmetico){
        //Tamaño original en bytes (cada caracter ocupa 2 bytes)
        int tamañoOriginal = original.length() * 2;
        //Huffman en bits
        int tamañoHuffman = comprimidoHuffman.length();
        //Aritmético en bytes
        int tamañoAritmetico = comprimidoAritmetico.length() / 8;

        System.out.println("namesize: " + tamañoOriginal + " Huffman: " + tamañoHuffman + " Arithmetic: " + tamañoAritmetico);
    }
}