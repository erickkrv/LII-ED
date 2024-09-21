package org.example;

import java.util.ArrayList;
import java.util.List;

public class HuffmanNode {
    // Declaramos las variables que vamos a utilizar
    public char Symbol;
    public int Frequency;
    public HuffmanNode Left;
    public HuffmanNode Right;

    // Constructor de la clase HuffmanNode
    public HuffmanNode(char symbol, int frequency){
        // Asignamos los valores de los parámetros a las variables de instancia
        Symbol = symbol;
        Frequency = frequency;
    }

    // Otro constructor de la clase HuffmanNode
    public HuffmanNode(char symbol, int frequency, HuffmanNode left, HuffmanNode right){
        // Asignamos los valores de los parámetros a las variables de instancia
        Symbol = symbol;
        Frequency = frequency;
        Left = left;
        Right = right;
    }

    // Método para imprimir el árbol
    public void PrintTree(int nivel){
        // Imprimimos espacios en blanco según el nivel
        for(int i = 0; i < nivel; i++){
            System.out.print("  ");
        }
        // Imprimimos la frecuencia y el símbolo
        System.out.println(Frequency + " (" + Symbol + ")");
        // Si hay un nodo a la izquierda, imprimimos ese subárbol
        if(Left != null){
            Left.PrintTree(nivel + 1);
        }
        // Si hay un nodo a la derecha, imprimimos ese subárbol
        if(Right != null){
            Right.PrintTree(nivel + 1);
        }
    }

    // Método para recorrer el árbol
    public List<Boolean> Traverse(char symbol, List<Boolean> data) {
        // Si el nodo es una hoja
        if(Left == null && Right == null){
            // Si el símbolo es el que estamos buscando, devolvemos los datos
            if(symbol == Symbol){
                return data;
            }else{
                // Si no, devolvemos null
                return null;
            }
        }else{
            // Si el nodo no es una hoja, recorremos los subárboles
            List<Boolean> left = null;
            List<Boolean> right = null;
            if(Left != null){
                // Creamos una nueva lista con los datos y un false al final
                List<Boolean> leftPath = new ArrayList<>(data);
                leftPath.add(false);
                // Recorremos el subárbol izquierdo
                left = Left.Traverse(symbol, leftPath);
            }
            if(Right != null){
                // Creamos una nueva lista con los datos y un true al final
                List<Boolean> rightPath = new ArrayList<>(data);
                rightPath.add(true);
                // Recorremos el subárbol derecho
                right = Right.Traverse(symbol, rightPath);
            }
            // Devolvemos el resultado del subárbol que haya encontrado el símbolo
            if(left != null){
                return left;
            }else{
                return right;
            }
        }
    }

    // Método para obtener el símbolo
    public char getSymbol() {
        return Symbol;
    }

    // Método para obtener la frecuencia
    public int getFrequency() {
        return Frequency;
    }
}
