package org.example;

import javax.swing.plaf.basic.BasicOptionPaneUI;
import java.util.*;
import java.util.stream.Collectors;
import javafx.util.Pair;
public class HuffmanTree {
    private List<HuffmanNode> nodes = new ArrayList<>();
    public HuffmanNode root;
    public Map<Character, Integer> Frequencies = new LinkedHashMap<>();

    public static boolean IsLeaf(HuffmanNode node){
        return node.Left == null && node.Right == null;
    }
    public void BuildTree(String source, Map<Character, Integer> frequencies){
        if(frequencies != null){
            Frequencies = frequencies;
        }else{
            Frequencies.clear();
            CalculateFrequencies(source);
        }
        for(var symbol : Frequencies.keySet()){
            nodes.add(new HuffmanNode(symbol, Frequencies.get(symbol)));
//            System.out.println("Simbolo: " + symbol + " - Frecuencia: " + Frequencies.get(symbol));
        }
        while(nodes.size() > 1){
            List<HuffmanNode> orderedNodes = nodes.stream()
                    .sorted(Comparator.comparingInt(HuffmanNode::getFrequency)
                            .thenComparing(HuffmanNode::getSymbol))
                    .toList();
            // Debug
//            System.out.println("Nodos ordenados:");
//            for(HuffmanNode nodo : orderedNodes){
//                System.out.println("Simbolo: " + nodo.Symbol + " - Frecuencia: " + nodo.Frequency);
//            }
            if(orderedNodes.size() >= 2){
                var left = orderedNodes.get(0);
                var right = orderedNodes.get(1);
                var parent = new HuffmanNode('|', left.Frequency + right.Frequency, left, right);
                nodes.remove(left);
                nodes.remove(right);
                nodes.add(parent);
            }
            root = nodes.get(0);
        }
//        PrintTree();
//        PrintSymbols();
    }
    private void CalculateFrequencies(String source){
        for(var symbol : source.toCharArray()){
            if(!Frequencies.containsKey(symbol)){
                Frequencies.put(symbol, 0);
            }
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
        BitSet bits = new BitSet();
        int bitIndex = 0;
        for(var symbol : source.toCharArray()){
            var symbolBits = root.Traverse(symbol, new ArrayList<>());
            for(var bit : symbolBits){
                if(bit){
                    bits.set(bitIndex);
                }
                bitIndex++;
            }
        }
        return new Pair<>(bits, bitIndex);
    }
    public String Decode(BitSet bits){
        HuffmanNode current = root;
        String decoded = "";
        for(int i = 0; i <= bits.length(); i++){
            boolean bit = bits.get(i);
            if(bit){
                if(current.Right != null){
                    current = current.Right;
                }
            }else{
                if(current.Left != null){
                    current = current.Left;
                }
            }
            if(IsLeaf(current)){
                decoded += current.Symbol;
                current = root;
            }
        }
        return decoded;
    }

    public static String bitSetToString(BitSet bitSet, int actualLength) {
        StringBuilder sb = new StringBuilder(actualLength);
        for (int i = 0; i < actualLength; i++) {
            sb.append(bitSet.get(i) ? '1' : '0');
        }
        return sb.toString();
    }

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
