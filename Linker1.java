import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.regex.Pattern;

public class Linker1 {
	static int module = 0;
	static int machineSize = 200;
	static HashMap<String, Integer> SymbolTable = new LinkedHashMap<>();
	static ArrayList<Integer> BaseAddr = new ArrayList<>();
	static ArrayList<Integer> ModuleSize = new ArrayList<>();
	static ArrayList MemoryMap = new ArrayList();
	static ArrayList<String[]> insText = new ArrayList<String[]>();
	static ArrayList<String[]> finalText = new ArrayList<String[]>();
	static ArrayList<String> referIndex = new ArrayList<>();
	static HashMap<String, Integer> Use = new LinkedHashMap<>();
    static HashMap<String, Integer> Def = new LinkedHashMap<>();
    static HashMap<String, String> ErrorSym = new HashMap<>();
    static HashMap<Integer, String> ErrorAddr = new HashMap<>();
    static int mSize = 0;
    static ArrayList<String> UseWarning = new ArrayList<>();
    
	public static void main(String[] args) {
		try {
            System.out.print("Enter the file name: ");
            Scanner input = new Scanner(System.in);
            File file = new File(input.nextLine());
            input = new Scanner(file);
            
            passOne(input);           
            
            System.out.println("Symbol Table");
            for (Map.Entry<String, Integer> entry : SymbolTable.entrySet()) {
            		if(ErrorSym.containsKey(entry.getKey())) {
            			System.out.println(entry.getKey() + " = " + entry.getValue()+"  "+ErrorSym.get(entry.getKey()));
            		}
            		else {
            			System.out.println(entry.getKey() + " = " + entry.getValue());
            		}                                
            }
            /*for(Integer t : BaseAddr) {
            		System.out.println(t);
            }
            for(Integer t : ModuleSize) {
        			System.out.println(t);
            }
            for(String t : referIndex) {
        			System.out.println(t);
            }*/
            System.out.println();
            passTwo(input);
            String[][] MemoryMap = (String [][])finalText.toArray(new String[0][0]);
            for(int r=0; r<MemoryMap.length; r++) {
            		if(ErrorAddr.containsKey(r)) {
            			System.out.println(MemoryMap[r][0]+" "+MemoryMap[r][1]+"  "+ErrorAddr.get(r));
            		}
            		else {
            			System.out.println(MemoryMap[r][0]+" "+MemoryMap[r][1]);
            		}            		
            }
            System.out.println();
            for (Map.Entry<String, Integer> entry : Def.entrySet()) {
            		if(!Use.containsKey(entry.getKey())) {
            			System.out.println("Warning: "+entry.getKey()+" was defined in module "+entry.getValue()+" but never used.");
            		}                           
            }
            
            String[] usewarning = (String[])UseWarning.toArray(new String[0]);
            for(int uw=0; uw<usewarning.length; uw++) {
            		System.out.println(usewarning[uw]);
            }
            /*int[] test = (int [])BaseAddr.toArray(new int[0]);
            for(int i = 0; i < test.length; i++)
            {
               
            System.out.println(test[i]);
               
            }*/
            
            input.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }// TODO Auto-generated method stub

	}
	
	public static void passOne(Scanner input) {
		String m = input.next();
        module = Integer.parseInt(m);
        
        int addr = 0;
        
        while(input.hasNext()) {
        		for(int i=0; i<module; i++) {
        			BaseAddr.add(i,addr);
        			
        			//definition
        			int d1 = input.nextInt();
        			if(d1 != 0) {
        				for(int j=0; j<d1; j++) {
        					String symbol = input.next();
        					int sv = input.nextInt() + addr;
        					
        					if(Def.containsKey(symbol)) {
        						ErrorSym.put(symbol, "Error: This variable "+symbol+" is mutiply defined; first value used. ");
        					}
        					else {
        						SymbolTable.put(symbol, sv);
        						Def.put(symbol, i);
        					}       					        					
        				}
        			}
        			//use
        			int u1 = input.nextInt();
        			String uses = "";
        			if(u1 != 0) {
        			for(int j=0; j<u1; j++) {
        				String usy = input.next();
        				Use.put(usy, i);
        				String[] locate = {usy, Integer.toString(i)};
        				
        				uses = uses+usy+",";
        			}}
        			referIndex.add(uses);
        			
        			//Text
        			int plus = input.nextInt();
        			ModuleSize.add(plus);
        			addr = addr+plus;
        			for(int q=0; q<plus; q++) {
        				String opCode = input.next();
        				String pread = input.next();
        				String[] total = {opCode, pread};
        				insText.add(total);
        			}
        			
        		}   	
        }
        
        for(Map.Entry<String, Integer> def : Def.entrySet()) {        		
        		int lo1 = def.getValue();
        		String sy = def.getKey();
        		int val = SymbolTable.get(sy);
        		int si = ModuleSize.get(lo1)+BaseAddr.get(lo1);
        		if (val>si) {
        			SymbolTable.put(def.getKey(), BaseAddr.get(lo1));
        			ErrorSym.put(def.getKey(), "In module "+lo1+" the def of "+sy+" exceeds the size of the module; zero (relative) used. ");
        		}
        }
        /*mSize = BaseAddr.get(BaseAddr.size()-1)+ModuleSize.get(ModuleSize.size()-1);
        for (HashMap.Entry<String, Integer> entry : SymbolTable.entrySet()) {
        		if(entry.getValue() >= mSize) {
        			SymbolTable.put(entry.getKey(), 0);
        			ErrorSym.put(entry.getKey(), "The address of definition exceeds the size of the module. ");
        		}
        }*/
        }
	public static void passTwo(Scanner input) {
		
		System.out.println("Memory Map");
		String[][] memMap = (String [][])insText.toArray(new String[0][0]);
		String type = "";
		for(int k=0; k<BaseAddr.size(); k++) {
			int num1 = BaseAddr.get(k);
			int num2 = ModuleSize.get(k);
			String item = referIndex.get(k);
			//System.out.println(item);
			String[] items = item.split(",");
			HashMap<Integer, Integer> indexSet = new HashMap<>();
			
			List<String> list = new ArrayList<String>();
					
				for(int l=0; l<items.length; l++) {
					list.add(items[l]);
					//System.out.println(items[l]);
					//System.out.println(list.get(l));
				}
			
				
			for(int t=num1; t<num1+num2; t++) {
				type = memMap[t][0];
				String tt = Integer.toString(t) + ":";
		           if(type.equals("I")) {
		        	       String[] immediate = {tt, memMap[t][1]};
		        	   	   finalText.add(t, immediate);		        	   	   
		           }
		           else if(type.equals("A")) {
		        	   		String abAddr1 = memMap[t][1];
		        	   		String abAddr2 = abAddr1.substring(1,4);
		        	   		int abAddr3 = Integer.parseInt(abAddr2);
		        	   		char result1 = abAddr1.charAt(0);
		        	   		String result = Character.toString(result1);
		        	   		if(abAddr3 > machineSize-1) {
		        	   			result = result + "000";
		        	   			ErrorAddr.put(t, "Error: Absolute address exceeds machine size; zero used");
		        	   		}
		        	   		else {
		        	   			result = result + abAddr2;	
		        	   		}
		        	   		
		        	   		
		        	   		String[] absolute = {tt, result};
		        	   		finalText.add(t, absolute);		        	   		
		           }
		           else if(type.equals("R")) {
		        	   		String r1 = memMap[t][1];
		        	   		String rr1 = r1.substring(1, 4);
		        	   		int r2 = Integer.parseInt(r1);
		        	   		int rr2 = Integer.parseInt(rr1);
		        	   		String r4 = "";
		        	   		if(rr2 > num1+num2-1) {
		        	   			r4 = r1.substring(0, 1)+"000";
		        	   			ErrorAddr.put(t, "Error: Relative address exceeds module size; zero used");
		        	   		}
		        	   		else {
		        	   			int r3 = r2 + num1;
		        	   			r4 = Integer.toString(r3);
		        	   		}
		        	   		//int r3 = r2 + num1;
		        	   		//String r4 = Integer.toString(r3);
		        	   		String[] relative = {tt, r4};
		        	   		
		        	   		finalText.add(t, relative);
		           }
		           else if(type.equals("E")) {		
		        	   		String e1 = memMap[t][1];
		        	   		String index = e1.substring(3);
		        	   		int ind = Integer.parseInt(index);//index
		        	   		//String[] items = item.split(",");//use line
		        	   		//List<String> list = new ArrayList<String>();
		        	   		/*for(int l=0; l<items.length; l++) {
		        	   			list.add(items[l]);
		        	   		}*/
		        	   		if(ind > items.length) {
		        	   			ErrorAddr.put(t, "Error: External addreaa exceeds length of use list; treated as immediate. ");
		        	   			String[] eImmediate = {tt, memMap[t][1]};
		        	   			finalText.add(t, eImmediate);
		        	   		}
		        	   		else {
		        	   			
		        	   			indexSet.put(ind, 1);
		        	   			String e2 = items[ind];
		        	   			int ie = list.indexOf(e2);
		        	   			if(ie>-1) {list.remove(ie);}
		        	   			
			        	   		int e3 = 0;
			        	   		if(SymbolTable.containsKey(e2)) {
			        	   			e3 = SymbolTable.get(e2);
			        	   		}
			        	   		else {
			        	   			ErrorAddr.put(t, "Error: "+e2+" is not defined; zero used");
			        	   		}		        	   		
			        	   		int e4 = Integer.parseInt(e1);
			        	   		int e5 = e3 + e4 - ind;
			        	   		String e6 = Integer.toString(e5);
			        	   		String[] external = {tt, e6};
			        	   		finalText.add(t, external);
			        	   		
		        	   		}		       	   		
		           }
			}
			
			
			
			String[] newItems = list.toArray(new String[0]);			
			if(newItems.length>0 && newItems[0]!="") {
				for(int w=0; w<newItems.length; w++) {
					//System.out.println(newItems[w]);
					UseWarning.add("Warning: In module "+k+" "+newItems[w]+" appeared in the use list but was not actually used.");
				}
			}
			
		}
	}
}

