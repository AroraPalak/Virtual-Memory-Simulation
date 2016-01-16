import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Simulation {

	static int P = 2000;
	static int t = 700;
	
	public static void main(String args[]) throws FileNotFoundException{	
		PageRefg.generate(P);
		Scanner in = new Scanner(PageRefGeneration.returnFile());


		//Variable Interval Sampled Working set algotrithm
		VSWorkingSet WS = new VSWorkingSet( P, t, new Scanner(PageRefGeneration.returnFile()));
		WS.PageFaultCalc();
		System.out.println("WS--> The page fault is: "+WS.returnPageFault());
		System.out.println("WS--> The page fault rate is: "+WS.returnFaultRate()*100+"%");
		System.out.println("WS--> The Minimum frames required is: "+WS.returnF());
		//Page fault frequency algorithm
		PageFaultFreq PFF = new PageFaultFreq( P, t, new Scanner(PageRefGeneration.returnFile()));
		PFF.PageFaultCalc();
		System.out.println("PFF--> The page fault is: "+PFF.returnPageFault());
		System.out.println("PFF--> The page fault rate is: "+PFF.returnFaultRate()*100+"%");
		System.out.println("PFF--> The Minimum frames required is: "+PFF.returnF());
		
		//compare the variations of page fault when t changes
		Range(50,1001,50);
		
	}
	
	static void Range(int min, int max, int interval) throws FileNotFoundException{
		PrintWriter out = new PrintWriter(new File("Random.txt"));
		out.flush();
		for(int i = min; i < max; i+=interval){
			out.print(i);
			out.print(",");
			
			VSWorkingSet WS = new VSWorkingSet( P, i, new Scanner(PageRefGeneration.returnFile()));
			WS.PageFaultCalc();
			out.print(WS.returnPageFault());
			out.print(",");
			out.print(WS.returnFaultRate()*100);
			out.print(",");
			out.print(WS.returnF());
			out.print(",");
			
			
			PageFaultFreq PFF = new PageFaultFreq( P, i, new Scanner(PageRefGeneration.returnFile()));
			PFF.PageFaultCalc();
			out.print(PFF.returnPageFault());
			out.print(",");
			out.print(PFF.returnFaultRate()*100);
			out.print(",");
			out.println(PFF.returnF());
		}
		out.close();
		System.out.println("Finished!");
	}
class PageRefGeneration {

	/*Used to generate a reference string uesd for 
	  virtual memory replacement algorithms*/
	private static int times = 250;
	private static int P = 2000;  //P:  Assumed size of allocated virtual memory (thousands)
	private static int m = 200;   //m: times of picking references
	private static int p = 100;/*p,e: reference address range, choose references randomly from [p,p+e] each time*/
	private static int e = 90;  
	private static int t = 2000;
	private static File f = new File("random.txt");
	
	public static void main(String args[]) throws FileNotFoundException{	
		PageRefGeneration.generate(P);
		Scanner in = new Scanner(PageRefGeneration.returnFile());

		/*Variable Interval Sampled Working set algotrithm*/
		VSWorkingSet WS = new VSWorkingSet( P, t, new Scanner(PageRefGeneration.returnFile()));
		WS.PageFaultCalc();
		System.out.println("WS--> The page fault is: "+WS.returnPageFault()); // No. Of Page Faults in VSWS
		System.out.println("WS--> The page fault rate is: "+WS.returnFaultRate()*100+"%"); // Page Fault Rate
		System.out.println("WS--> The Minimum frames required is: "+WS.returnF()); // No. of Minimum Frames Required
		
		/*Page fault frequency algorithm*/
		PageFaultFreq PFF = new PageFaultFreq( P, t, new Scanner(PageRefGeneration.returnFile()));
		PFF.PageFaultCalc();
		System.out.println("PFF--> The page fault is: "+PFF.returnPageFault());  // No. Of Page Faults in PFF
		System.out.println("PFF--> The page fault rate is: "+PFF.returnFaultRate()*100+"%");  // Page Fault Rate
		System.out.println("PFF--> The Minimum frames required is: "+PFF.returnF());  //// No. of Minimum Frames Required
		
		
		/*compare the variations of page fault when t ranges */
		Range(50,1001,50);
		
	}
	
	static void Range(int min, int max, int interval) throws FileNotFoundException{
		PrintWriter out = new PrintWriter(new File("Random.txt"));
		out.flush();
		for(int i = min; i < max; i+=interval){
			out.print(i);
			out.print(",");
			
			VSWorkingSet WS = new VSWorkingSet( P, i, new Scanner(PageRefGeneration.returnFile()));
			WS.PageFaultCalc();
			out.print(WS.returnPageFault());
			out.print(",");
			out.print(WS.returnFaultRate()*100);
			out.print(",");
			out.print(WS.returnF());
			out.print(",");
			
			PageFaultFreq PFF = new PageFaultFreq( P, i, new Scanner(PageRefGeneration.returnFile()));
			PFF.PageFaultCalc();
			out.print(PFF.returnPageFault());
			out.print(",");
			out.print(PFF.returnFaultRate()*100);
			out.print(",");
			out.println(PFF.returnF());
		}
		out.close();
		System.out.println("Finished!");
	}
	
}// Function to generate Page Reference
	PageRefGeneration(int times, int P, int m, int p, int e, int t, File f){
		PageRefGeneration.times = times;
		PageRefGeneration.P = P;
		PageRefGeneration.m = m;
		PageRefGeneration.p = p;
		PageRefGeneration.e = e;
		PageRefGeneration.t = t;
		PageRefGeneration.f = f;
	}
	
	static void generate(int P){
		PageRefGeneration.P = P;
		try {
			PrintWriter out = new PrintWriter(f);
			out.flush();
			int tmp = 0; //reference
			
			for(int j = 0; j < times; j ++){				
				for(int i = 0; i < m; i ++){
					tmp = p + (int)(Math.random() * e); //generate a reference addressed [p,p+e]
					out.println(tmp);
				}
		
			}
			out.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
	}
	
	static File returnFile(){
		return f;
	}
	
	static int totalRef(){
		return times*m;
	}
	

    }

    class VSWorkingSet {

	private int M;
	private int L;   // t: constant
	private int[] VM = null;     //VM[] records whether the page p is currently resident
	private Queue<Integer> WIN = new LinkedList<Integer>();
	private Scanner in = null;
	private int totalPage = 0;
	private int pageFault = 0;
	private int F;
	
	VSWorkingSet(int P, int t,Scanner in){
		this.M = M;
		this.L = L;
		this.in = in;
		VM = new int[P];// Virtual Memory Array
	}
	
	//WIN(t+1) contains page references during the last t+1 steps
	void PageFaultCalc(){
		int q;
		while(in.hasNext()){
			int page = in.nextInt();
			WIN.add(page);
				if(WIN.size() > t+1){
					q = WIN.remove();
						if(!WIN.contains(q)){
							VM[q] = 0;
						}
				}
			if(VM[page]==0){
				VM[page] = 1;
				pageFault++;
			}
			totalPage++;
			Fupdate();
		}
	}
	
	void Fupdate(){
		int f = 0;
		for(int i = 0; i < VM.length; i++){
			if(VM[i] == 1)
				f++;
		}
		F+=f; 
	}
	
	double returnF(){
		return Math.ceil((double) F/totalPage); //Frames  Calculation
	}
	
	int returnPageFault(){
		return pageFault;  // No. Of Page Faults
	}
	
	double returnFaultRate(){
		return (double) pageFault/totalPage;  // Page Fault Rate
	}
}

    private static class PageFaultFreq {

        public PageFaultFreq() {
        }
    }
}

class PageFaultFreq {
	private int P;
	private int t;
	private PFFNode[] VM = null;
	private Scanner in = null;
	private int totalPage = 0;
	private int pageFault = 0;
	private int F;
	
	PageFaultFreq(int P, int t,Scanner in){
		this.P = P;
		this.t = t;
		this.in = in;
		VM = new PFFNode[P];
		for(int i = 0; i < VM.length; i++){
			VM[i] = new PFFNode();

		}
	}
	//Calculating Page fault
	void PageFaultCalc(){
		int faultRecord = 0;
		while(in.hasNext()){
			int page = in.nextInt();
			VM[page].u = 1;
				if(VM[page].res == 0){
					pageFault++;
					VM[page].res = 1;
					if((totalPage - faultRecord) > t){
						VMclean();
					}
					faultRecord = totalPage; 
					VMreset();
				}
			totalPage++;
			Fupdate();
		}	
	} 
	void VMclean(){
		for(int i = 0; i < VM.length; i++){
			if(VM[i].res == 1 && VM[i].u == 0){
				VM[i].res = 0;
			}
		}
	}
	//Reset the array VM
	void VMreset(){
		for(int i = 0; i < VM.length; i++){
			VM[i].u = 0;
		}
	}
	
	
	void Fupdate(){
		int f = 0;
		for(int i = 0; i < VM.length; i++){
			if(VM[i].res == 1)
				f++;
		}
		F+=f; 
	}
	
	double returnF(){
		return Math.ceil((double) F/totalPage); //Frames Calculation
	}
	int returnPageFault(){
		return pageFault; // Returning Page Faults
	}
	double returnFaultRate(){
		return (double) pageFault/totalPage // Fault Rate
	}

    class PFFNode {
       {
	int res = 0;
	int u = 0;
	PFFNode()
        }
    }
}

// Performance evaluation based on No. of Page Faults, Page Fault Rate, No. of Frames.
//Page fault in PFF is inversely proportional to Frame Size. As we increase frame size , Page faults decrease.
//Page Fault in VSWS is also inversely Proportional to Range M-L and Q. As we increase the range M L interval and Q , Page Fault Decreases.