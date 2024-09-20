package org.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class ArithmeticCompression {
    private LinkedHashMap<Character, Range> probabilidades;
    private String source;
    private long underflow_bits;

    private static final int default_low = 0;
    private static final int default_high = 0xffff;

    private static final int MSD = 0x8000;

    private static final int SSD = 0x4000;

    private int scale;

    public ArithmeticCompression(String source){
        this.source = source;
        this.probabilidades = new LinkedHashMap<Character, Range>();
        CalcularProbabilidades();
    }
    public ArithmeticCompression(LinkedHashMap<Character, Range> probabilidades, int scale){
        this.source = "";
        this.probabilidades = probabilidades;
        this.scale = scale;
    }
    private void CalcularProbabilidades() {
        Map<Character, Integer> frequencies = new HashMap<>();
        for (char symbol : source.toCharArray()) {
            frequencies.put(symbol, frequencies.getOrDefault(symbol, 0) + 1);
        }

        // Sort the map by values and then keys
        Map<Character, Integer> sortedFrequencies = frequencies.entrySet().stream()
                .sorted(Map.Entry.<Character, Integer>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        scale = source.length();

        int low = 0;

        for (Map.Entry<Character, Integer> entry : sortedFrequencies.entrySet()) {
            int high = low + entry.getValue();
            probabilidades.put(entry.getKey(), new Range(low, high));
            low = high;
        }

        // Print probabilities
//        for (Map.Entry<Character, Range> entry : probabilidades.entrySet()) {
//            System.out.println("Symbol: " + entry.getKey() + " - Low: " + entry.getValue().getLow() + " - High: " + entry.getValue().getHigh());
//        }
    }

    public ByteArrayOutputStream Compress(String input) {
        int low, high;

        // Initialize output buffer
        BitWriter output_stream = new BitWriter();

        // Initialize low and high to their default values
        low = default_low;
        high = default_high;
        underflow_bits = 0;
        long range;
        String output = "";

        // Iterate through each character in the input string
        for (char symbol : input.toCharArray()) {
            //Letra a evaluar
//            System.out.println("SIMBOLO EVALUADO: " + symbol);
            range = (long)(high - low) + 1;
            high = (int)(low + range * probabilidades.get(symbol).getHigh() / scale - 1);
            low = (int)(low + range * probabilidades.get(symbol).getLow() / scale);

            // Normalize the range to avoid overflow and underflow
            while (true) {
                if ((high & MSD) == (low & MSD)) {
                    output_stream.WriteBit((high & MSD) != 0);
                    output += (high & MSD) != 0 ? "1" : "0";
//                    System.out.println("Quedan bits? " + (underflow_bits > 0));
                    while (underflow_bits > 0) {
                        output_stream.WriteBit((high & MSD) == 0);
                        output += (~high & MSD) == 0 ? "0" : "1";
                        underflow_bits--;
//                        System.out.println("Output Compressed String with Underflow: " + output);
                    }
                } else{
                    if ((low & SSD) != 0 && (high & SSD) == 0) {
                        underflow_bits++;
                        low = low & 0x3fff;
                        high = high | 0x4000;
                        //Debug
//                        System.out.println("Underflow: " + underflow_bits);
                    } else {
                        break;
                    }
                }
//                //Debug
//                System.out.println("Antes de mover: ");
//                System.out.println("Low: " + low + " - " + Integer.toBinaryString(low) + " - High: " + high + " - " + Integer.toBinaryString(high));
                low = (low << 1) & 0xFFFF;
                high = (high << 1) & 0xFFFF;
                high = high | 1;
                //Debug
//                System.out.println("Luego de mover: ");
//                System.out.println("Low: " + low + " - " + Integer.toBinaryString(low) + " - High: " + high + " - " + Integer.toBinaryString(high));
//                System.out.println("Output Compressed Binary: " + output);
            }
//            System.out.println("---------------------------------------------");
        }

        // Output the final bits
        output_stream.WriteBit((low & 0x4000) != 0);
        output += (low & 0x4000) != 0 ? "1" : "0";
        underflow_bits++;
        while (underflow_bits-- > 0) {
            output_stream.WriteBit((low & 0x4000) == 0);
            output += (~low & 0x4000) == 0 ? "0" : "1";
        }
        //System.out.println("Output Compressed Binary: " + output);
        // Write the last byte to the output buffer
        return output_stream.Flush();
    }
    public static String GetBinaryString(ByteArrayOutputStream stream){
        int originalPosition = stream.size();
        byte[] bytes = stream.toByteArray();
        StringBuilder binary = new StringBuilder();
        for(byte b : bytes){
            String binaryStr = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            binary.append(binaryStr);
        }
        return binary.toString();
    }
    public String Decompress(ByteArrayInputStream input, int size){
        char c;
        int scaledValue, low, high;
        String retval = "";
        int code = 0;

        BitReader input_buffer = new BitReader(input);
        for(int i = 0; i < 16; i++){
            code = (code << 1);
            code |= (input_buffer.ReadBit() ? 1 : 0);
        }

        low = default_low;
        high = default_high;

        for(int i = 0; ;){
            //System.out.println("Siguiente símbolo: ");
            long range = (long)(high - low) + 1;
            //System.out.println("Low: " + low + " - High: " + high + " - Range: " + range);
            scaledValue = (int)(((code - low + 1) * scale - 1) / range);
            //System.out.println("Scaled Value: " + scaledValue);

            c = '\0';
            for(Map.Entry<Character, Range> entry: probabilidades.entrySet()){
                if(scaledValue >= entry.getValue().getLow() && scaledValue < entry.getValue().getHigh()){
                    c = entry.getKey();
                    //System.out.println("Symbol: " + c);
                    break;
                }
            }
            if(c == '\0'){
                throw new IllegalStateException("Symbol not found");
            }
            retval += c;
            //System.out.println("Output: " + retval);
            if(++i == size){
                //System.out.println("Simbolo final: " + c);
                break;
            }

            range = (long)(high - low) + 1;
            //System.out.println("Nuevo rango: " + range);
            high = (int)(low + range * probabilidades.get(c).getHigh() / scale - 1);
            low = (int)(low + range * probabilidades.get(c).getLow() / scale);
            //System.out.println("Nuevo Low: " + low + " - Nuevo High: " + high);

            while(true){
                if((high & MSD) == (low & MSD)){
                    //No hacer nada
                } else {
                    if((low & SSD) == 0x4000 && (high & SSD) == 0){
                        code = code ^ 0x4000;
                        low = low & 0x3fff;
                        high = high | 0x4000;
                    }else{
//                        System.out.println("Simbolo final: " + c);
                        break;
                    }
                }
                low = (low << 1) & 0xFFFF;
                high = (high << 1) & 0xFFFF;
                high = high | 1;
//                System.out.println("Nuevo Low: " + low + " - Nuevo High: " + high);
//                System.out.println("Anterior code: " + code + " - " + Integer.toBinaryString(code));
                code = (code << 1) & 0xFFFF;
                code = code | (input_buffer.ReadBit() ? 1 : 0);
//                System.out.println("Nuevo Code: " + code + " - " + Integer.toBinaryString(code));
            }
        }
        return retval;
    }
}
