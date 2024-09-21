package org.example;

import javax.swing.plaf.basic.BasicOptionPaneUI;
import java.util.*;
import java.util.stream.Collectors;
import javafx.util.Pair;
public class HuffmanTree {
    private List<HuffmanNode> nodes = new ArrayList<>();
    public HuffmanNode root;
    public Map<Character, Integer> Frequencies = new LinkedHashMap<>();

    //Verifica si el nodo es una hoja
    public static boolean IsLeaf(HuffmanNode node){
        return node.Left == null && node.Right == null;
    }
    public void BuildTree(String source, Map<Character, Integer> frequencies){
        //Si las frecuencias no son nulas, se asignan a la variable global
        if(frequencies != null){
            Frequencies = frequencies;
        }else{
            //Si las frecuencias son nulas, se calculan
            Frequencies.clear();
            CalculateFrequencies(source);
        }
        //Se crean los nodos con los símbolos y sus frecuencias
        for(var symbol : Frequencies.keySet()){
            //Se añaden los nodos a la lista de nodos
            nodes.add(new HuffmanNode(symbol, Frequencies.get(symbol)));
            //Debug
//            System.out.println("Simbolo: " + symbol + " - Frecuencia: " + Frequencies.get(symbol));
        }
        //Se ordenan los nodos
        while(nodes.size() > 1){
            //Se ordenan los nodos por frecuencia y símbolo
            List<HuffmanNode> orderedNodes = nodes.stream()
                    .sorted(Comparator.comparingInt(HuffmanNode::getFrequency)
                            .thenComparing(HuffmanNode::getSymbol))
                    .toList();
            // Debug
//            System.out.println("Nodos ordenados:");
//            for(HuffmanNode nodo : orderedNodes){
//                System.out.println("Simbolo: " + nodo.Symbol + " - Frecuencia: " + nodo.Frequency);
//            }
            //Se crea un nodo padre con los dos nodos con menor frecuencia
            if(orderedNodes.size() >= 2){
                //Se obtienen los dos nodos con menor frecuencia
                var left = orderedNodes.get(0);
                var right = orderedNodes.get(1);
                //Se crea un nodo padre con los dos nodos con menor frecuencia
                var parent = new HuffmanNode('|', left.Frequency + right.Frequency, left, right);
                nodes.remove(left);
                nodes.remove(right);
                nodes.add(parent);
            }
            //Se asigna el nodo raíz
            root = nodes.get(0);
        }
        //Debug
//        PrintTree();
//        PrintSymbols();
    }
    private void CalculateFrequencies(String source){
        //Se calculan las frecuencias de los símbolos
        for(var symbol : source.toCharArray()){
            //Si el símbolo no está en el hashmap, se añade
            if(!Frequencies.containsKey(symbol)){
                Frequencies.put(symbol, 0);
            }
            //Se incrementa la frecuencia del símbolo
            Frequencies.put(symbol, Frequencies.get(symbol) + 1);
        }
        // Imprimir frecuencias (Debug)
//        System.out.println("Frecuencias:");
//        for(var item : Frequencies.keySet()){
//            if(item != '\n'){
//                System.out.println("Simbolo:" + item + " - Frecuencia: " + Frequencies.get(item));
//            }
//        }
    }
    public Pair<BitSet, Integer> Encode(String source){
        //Se crea un bitset para guardar los bits
        BitSet bits = new BitSet();
        int bitIndex = 0;
        //Se recorren los símbolos del texto
        for(var symbol : source.toCharArray()){
            //Se obtienen los bits del símbolo
            var symbolBits = root.Traverse(symbol, new ArrayList<>());
            //Se añaden los bits al bitset
            for(var bit : symbolBits){
                if(bit){
                    bits.set(bitIndex);
                }
                bitIndex++;
            }
        }
        //Se retorna el bitset y el índice
        return new Pair<>(bits, bitIndex);
    }
    //Decodifica un bitset (para debug)
    public String Decode(BitSet bits){
        //Se crea un nodo actual
        HuffmanNode current = root;
        String decoded = "";
        //Se recorren los bits
        for(int i = 0; i <= bits.length(); i++){
            boolean bit = bits.get(i);
            //Si el bit es 1, se va al nodo derecho
            if(bit){
                if(current.Right != null){
                    current = current.Right;
                }
            }else{ //Si el bit es 0, se va al nodo izquierdo
                if(current.Left != null){
                    current = current.Left;
                }
            }
            //Si el nodo es una hoja, se añade el símbolo al texto decodificado
            if(IsLeaf(current)){
                decoded += current.Symbol;
                current = root;
            }
        }
        return decoded;
    }

    //Convierte un bitset a string
    public static String bitSetToString(BitSet bitSet, int actualLength) {
        StringBuilder sb = new StringBuilder(actualLength);
        for (int i = 0; i < actualLength; i++) {
            sb.append(bitSet.get(i) ? '1' : '0');
        }
        return sb.toString();
    }

    //Imprimir (debug)
    public void PrintTree(){
        root.PrintTree(0);
    }
    private void PrintSymbols(){
        for(var item : Frequencies.keySet()){
            var symbol = root.Traverse(item, new ArrayList<>());
            if(symbol != null){
                System.out.println("Simbolo: " + item + " - Codigo: " + symbol.stream().map(b -> b ? "1" : "0").collect(Collectors.joining()));
            }
        }
    }
}
