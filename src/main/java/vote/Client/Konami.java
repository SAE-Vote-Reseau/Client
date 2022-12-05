package vote.Client;

import java.util.Map;
import java.util.TreeMap;

public class Konami {
    static private int[] code = {38,38,40,40,37,39,37,39,66,65};


    static private int currentNode =0;



    static public boolean CheckK(int keyPressed,Map<Integer,Integer>[] graph){
        Integer nextNode = graph[currentNode].get(keyPressed);
        currentNode = nextNode == null ? 0 : nextNode;
        return currentNode == code.length-1;
    }



    static Map<Integer, Integer>[] generateSequenceMap(int[] sequence) {

        //Create map
        Map<Integer, Integer>[] graph = new Map[sequence.length];
        for(int i=0 ; i<sequence.length ; i++) {
            graph[i] = new TreeMap<Integer,Integer>();
        }

        //i is delta
        for(int i=0 ; i<sequence.length ; i++) {
            loop: for(int j=i ; j<sequence.length-1 ; j++) {
                if(sequence[j-i] == sequence[j]) {


                    //Ensure that the longest possible sub-sequence is recognized
                    Integer value = graph[j].get(sequence[j-i+1]);
                    if(value == null || value < j-i+1)
                        graph[j].put(sequence[j-i+1], j-i+1);
                }
                else
                    break loop;
            }
        }
        return graph;
    }
}