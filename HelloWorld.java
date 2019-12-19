public class HelloWorld{

     public static void main(String []args){
        System.out.println("Hello World");
        
        
        String str = "Tef ta 1we  ea";
        String[] result = str.split("\\s+", 0);
        for(String s : result){
            System.out.println(s);
        }
     }
     
}