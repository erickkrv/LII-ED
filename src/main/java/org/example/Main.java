package org.example;

import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    //Creación de uin hashmap para guardar los libros
    static public HashMap<String, Libro> HashLibros = new HashMap<String, Libro>();
    //Creación de un hashmap para guardar los nombres de los libros y su isbn
    static public HashMap<String, String> nameISBNMap = new HashMap<String, String>();

    //Contadores para los tipos de compresión
//    private static int equalCounter = 0;
//    private static int decompressCounter = 0;
//    private static int huffmanCounter = 0;
//
//    private static int arithmeticCounter = 0;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("Laboratorio 4 Erick Rivas");
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
                     BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("libros_encontrados.txt", false)))) {
                    String linea;

                    // Leer archivo línea por línea
                    StringBuilder sb = new StringBuilder();
                    while ((linea = br.readLine()) != null) {
                        ultimaLinea = linea;
                        // Procesar línea
                        if (linea.startsWith("INSERT;")) {
                            String datos = linea.substring(7).trim();
                            JSONObject json = new JSONObject(datos);

                            // Verificación de campos obligatorios (isbn y name)
                            if (!json.has("isbn") || json.getString("isbn").isEmpty()) {
                                throw new IllegalArgumentException("Error: Falta el ISBN del libro.");
                            }
                            if (!json.has("name") || json.getString("name").isEmpty()) {
                                throw new IllegalArgumentException("Error: Falta el nombre del libro.");
                            }

                            // Obtener valores del JSON, verificando si están presentes y no vacíos
                            String isbnActual = json.getString("isbn");

                            String author = json.has("author") && !json.getString("author").isEmpty()
                                    ? json.getString("author")
                                    : null;

                            String category = json.has("category") && !json.getString("category").isEmpty()
                                    ? json.getString("category")
                                    : null;

                            String price = json.has("price") && !json.getString("price").isEmpty()
                                    ? json.getString("price")
                                    : null;

                            Integer quantity = json.has("quantity") && !json.isNull("quantity")
                                    ? json.getInt("quantity")
                                    : null;

                            // Crear libro solo si los campos obligatorios están presentes
                            Libro libro = new Libro(
                                    json.getLong("isbn"),
                                    json.getString("name"),
                                    author,
                                    category,
                                    price,
                                    quantity
                            );

                            // Agregar libro a HashMap
                            HashLibros.put(isbnActual, libro);
                            nameISBNMap.put(json.getString("name"), isbnActual);
                        }


                        if (linea.startsWith("DELETE;")) {
                            String datos = linea.substring(7).trim();
                            JSONObject json = new JSONObject(datos);
                            String isbnActual = json.getString("isbn");
                            //Obtener nombre de libro a eliminar
                            if(HashLibros.containsKey(isbnActual)){
                                String nombreLibro = HashLibros.get(isbnActual).getTitulo();
                                //Eliminar de HashMap de nombres
                                nameISBNMap.remove(nombreLibro);
                                //Eliminar de HashMap de libros
                                HashLibros.remove(isbnActual);
                            }
                        }

                        if (linea.startsWith("PATCH;")) {
                            String datos = linea.substring(6).trim();
                            JSONObject json = new JSONObject(datos);
                            String isbnActual = json.getString("isbn");
                            Libro libro = HashLibros.get(isbnActual);
                            // Actualizar libro
                            if (libro != null) {
                                if (json.has("name")) {
                                    nameISBNMap.remove(libro.getTitulo());
                                    nameISBNMap.put(json.getString("name"), isbnActual);
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
                        //Buscar libro
                        if (linea.startsWith("SEARCH;")) {
                            String datos = linea.substring(7).trim();
                            JSONObject json = new JSONObject(datos);
                            String nombreABuscar = json.getString("name");
                            String isbnBuscado = nameISBNMap.get(nombreABuscar);
                            Libro libro = HashLibros.get(isbnBuscado);
                            if(libro != null){
                                sb.append(libro.toString()).append("\r\n");
                            }
                        }
                    }
                    writer.write(sb.toString());
                }
                System.out.println("CSV importado correctamente");
                Path path = Path.of("libros_encontrados.txt");
                try{
                    String contenido = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                    if(!contenido.trim().isEmpty()){
                        System.out.println("Búsqueda detectada! Encriptando resultado...");
                        encriptarDES();
                        System.out.println("Resultado encriptado correctamente");
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            } catch (Exception e) {
                System.err.println("Error al importar los libros: " + e.getMessage() + " en la línea: " + ultimaLinea);
            }
        }
    }
    //Método para encriptar
    private static void encriptarDES(){
        //Llave de encriptación
        String key = "ok:uo1IN";
        //Convertir la llave a bytes
        var bytesKey = key.getBytes();
        String data = "";
        //Leer el archivo de texto
        Path path = Path.of("libros_encontrados.txt");
        try{
            //Convierte el contenido del archivo a un string.
            data = Files.readString(path, StandardCharsets.UTF_8);
        }catch (IOException e){
            e.printStackTrace();
        }
        //Convertir el texto a bytes
        byte[] bytesData = data.getBytes();
        //Encriptar el texto
        byte[] encryptedData = DES.encriptar(bytesData, bytesKey, true);

        // Guardar el texto cifrado en un archivo
        try{
            Files.write(Path.of("encriptado.enc"), encryptedData);
            System.out.println("El resultado cifrado se ha escrito en encriptado.enc");
        }catch(IOException e){
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }

    }
}