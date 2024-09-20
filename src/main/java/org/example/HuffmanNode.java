package org.example;

import java.util.ArrayList;
import java.util.List;

public class HuffmanNode {
    public char Symbol;

    public int Frequency;

    public HuffmanNode Left;

    public HuffmanNode Right;

    public HuffmanNode(char symbol, int frequency){
        Symbol = symbol;
        Frequency = frequency;
    }
    public HuffmanNode(char symbol, int frequency, HuffmanNode left, HuffmanNode right){
        Symbol = symbol;
        Frequency = frequency;
        Left = left;
        Right = right;
    }
    public void PrintTree(int nivel){
        for(int i = 0; i < nivel; i++){
            System.out.print("  ");
        }
        System.out.println(Frequency + " (" + Symbol + ")");
        if(Left != null){
            Left.PrintTree(nivel + 1);
        }
        if(Right != null){
            Right.PrintTree(nivel + 1);
        }
    }
    public List<Boolean> Traverse(char symbol, List<Boolean> data) {
        if(Left == null && Right == null){
            if(symbol == Symbol){
                return data;
            }else{
                return null;
            }
        }else{
            List<Boolean> left = null;
            List<Boolean> right = null;
            if(Left != null){
                List<Boolean> leftPath = new ArrayList<>(data);
                leftPath.add(false);
                left = Left.Traverse(symbol, leftPath);
            }
            if(Right != null){
                List<Boolean> rightPath = new ArrayList<>(data);
                rightPath.add(true);
                right = Right.Traverse(symbol, rightPath);
            }
            if(left != null){
                return left;
            }else{
                return right;
            }
        }
    }
    public char getSymbol() {
        return Symbol;
    }
    public int getFrequency() {
        return Frequency;
    }
}
