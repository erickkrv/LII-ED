package org.example;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ArithmeticCompression {
    private Map<Character, Range> probabilidades;
    private String source;
    private BigInteger underflow_bits;

    private static final int default_low = 0;
    private static final int default_high = 0xffff;

    private static final int MSD = 0x8000;

    private static final int SSD = 0x4000;

    private int scale;

    public ArithmeticCompression(String source){
        this.source = source;
        this.probabilidades = new HashMap<Character, Range>();
        CalcularProbabilidades();
    }
    public ArithmeticCompression(HashMap<Character, Range> probabilidades, int scale){
        this.source = "";
        this.probabilidades = probabilidades;
        this.scale = scale;
    }
    private void CalcularProbabilidades(){
        HashMap<Character, Integer> frecuencias = new HashMap<Character, Integer>();
        for(char symbol: source.toCharArray()){
            frecuencias.put(symbol, frecuencias.getOrDefault(symbol, 0) + 1);
        }
        frecuencias = frecuencias.entrySet().stream().sorted(Map.Entry.<Character, Integer>comparingByValue().thenComparing(Map.Entry.comparingByKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, HashMap::new));
        scale = source.length();
        int low = 0;
        for(Map.Entry<Character, Integer> entry: frecuencias.entrySet()){
            int high = low + entry.getValue();
            probabilidades.put(entry.getKey(), new Range(low, high));
            low = high;
        }
    }

}
