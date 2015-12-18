package com.bornapp.dailyquote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomString {

    private static RandomString instance = null;
    private List<Integer> seqOfNumbers;
    private int iteratorIndex;

    //ctor
    private RandomString() {
        instance = this;
    }

    //private methods
    private static String getStringResource(int index) {
        //Retrieve strings from resources
        String[] resources = App.getContext().getResources().getStringArray(R.array.My_String_Array);
        //return string at specified index
        return resources[index];
    }

    //public methods
    public static boolean isInitialized() {
        return (instance != null);
    }

    public static void init() {
        // Singleton private instantiation
        new RandomString();

        //Retrieve strings from resources
        String[] resources = App.getContext().getResources().getStringArray(R.array.My_String_Array);

        //Initialize a random sequence of strings
        instance.seqOfNumbers = new ArrayList<>();
        for (int i = 0; i < resources.length; i++) {
            instance.seqOfNumbers.add(i);
        }
        Collections.shuffle(instance.seqOfNumbers);
    }

    public static String current() {
        return getStringResource(instance.seqOfNumbers.get(instance.iteratorIndex));
    }

    public static String next() {

        if (instance.iteratorIndex < instance.seqOfNumbers.size() - 1)
            instance.iteratorIndex++;
        else
            instance.iteratorIndex = 0;

        return getStringResource(instance.seqOfNumbers.get(instance.iteratorIndex));
    }

    public static String previous() {

        if (instance.iteratorIndex > 0)
            instance.iteratorIndex--;
        else
            instance.iteratorIndex = instance.seqOfNumbers.size() - 1;

        return getStringResource(instance.seqOfNumbers.get(instance.iteratorIndex));
    }
}