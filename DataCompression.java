import java.io.File;
import java.util.*;

public class DataCompression {
    public char[] diffChar;
    public int[] frequency;
    //driver method
    public static void main(String[] args) throws Exception{

        //object of DataCompression
        DataCompression obj=new DataCompression();

        //creating object of file class by pass location of the file
        File file=new File("inputData2.txt");
        Scanner sc=new Scanner (file);
        // String input=sc.next();

        //reading row data from file
        String input="";
        while(sc.hasNextLine()) input+=sc.nextLine();
        int n=input.length();

        //map table of different character with it's count of occurance(frequency)
        Map<Character, Integer> freqMap=obj.getFrequencyCount(input);
        int m=freqMap.size();

        //creating array to store charaters and corresponding count of occurance
        char[] diffChar=new char[m];
        int[] frequency=new int[m];

        //convert the map to set and put the character and correspoing count of occurance
        Set keyset=freqMap.keySet();
        int i=0;
        for(Iterator iter=keyset.iterator(); iter.hasNext();){
            char ch=(char) iter.next();
            diffChar[i]= ch;
            frequency[i]=freqMap.get(ch);
            i++;
        }

        //creating huffman tree
        HaffNode root=null;
        root=obj.constructHaffTree(diffChar,frequency);

        //code table with help of map
        Code codeObj=new Code();
        Map<Character,String> codeMap=codeObj.codeChart(root);

        System.out.println("\n************row data********************");
        System.out.println("\nYour row data: "+input);

        System.out.println("\n*****************code table*******************\n");
        for(int j=0;j<m;j++){
            System.out.println(diffChar[j]+": "+codeMap.get(diffChar[j]));
        }
        System.out.println("\n------------Encoding---------\n");
        String encoded="";
        for(int j=0;j<n;j++){
            char ch=input.charAt(j);
            encoded+=codeMap.get(ch);
        }
        System.out.println("Your encoded data: "+encoded);

        System.out.println("\n**************decoding**************\n");
        //reverse codeMap
        Map<String,Character> revMap=new HashMap<>();
        for(int j=0;j<diffChar.length;j++){
            char ch=diffChar[j];
            String tempCode=codeMap.get(ch);
            revMap.put(tempCode,ch);
        }
        String decoded="";
        String temp="";
        for(int j=0;j<encoded.length();j++){
            temp=temp+encoded.charAt(j);
            if(revMap.containsKey(temp)){
                decoded=decoded+revMap.get(temp);
                temp="";
            }
        }
        System.out.println("Your decoded data: "+decoded);
    }

    //constructing huffman tree
    HaffNode constructHaffTree(char[] diffChar,int[] frequency){
        int n=diffChar.length;
        HaffNode root=null;

        //min heap and inserting all nodes
        PriorityQueue<HaffNode> minHeap=new PriorityQueue<>(new Mycomparator());
        for(int i=0;i<n;i++){
            HaffNode temp=new HaffNode();
            temp.data=diffChar[i];
            temp.val=frequency[i];
            temp.left=null;
            temp.right=null;
            minHeap.add(temp);
        }

        //constructing huffman tree
        while(minHeap.size()>1){
            //finding 2 smallest
            HaffNode first=minHeap.poll();
            HaffNode second=minHeap.poll();

            //creating a new node for sum of count
            HaffNode temp=new HaffNode();
            temp.data='-';
            temp.val=first.val+second.val;
            temp.left=first;
            temp.right=second;
            minHeap.add(temp);
        }
        
        return minHeap.peek();
    }

    //finding count of occurance mathod
    Map<Character,Integer> getFrequencyCount(String str){
        int n=str.length();
        Map<Character,Integer> table=new HashMap<>();

        //a for loop to iterate in row data 
        for(int i=0;i<n;i++){
            char ch=str.charAt(i);//finding char at index i in row data
            if(table.containsKey(ch)){//if char is present in table then just increase it's count
                int count=table.get(ch);
                count++;
                table.put(ch,count);
            }
            else table.put(ch,1);//not present then put with count 1
        }

        return table;//returning table
    }
}

//this is building block of huffman tree
class HaffNode{
    HaffNode left;
    char data;
    int val;
    HaffNode right;
}

//comparator to compare two HaffNode
class Mycomparator implements Comparator<HaffNode>{
    public int compare(HaffNode x,HaffNode y){
        return x.val-y.val;
    }
}

//code class to finding code of character with help of huffman tree
class Code{
    Map<Character,String> map;
    Code(){
        map=new HashMap<>();
    }

    //code generation
    public void codeGen(HaffNode root,String str){
        if(root.left==null && root.right==null){
            map.put(root.data,str);
        }
        if(root.left!=null) codeGen(root.left, str+"0");
        if(root.right!=null) codeGen(root.right, str+"1");
    } 

    public Map<Character,String> codeChart(HaffNode root){
        codeGen(root, "");
        return map;
    }
}