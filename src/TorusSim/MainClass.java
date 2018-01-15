package TorusSim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class MainClass {
	public static void main(String[] args)  
	{
		test_Param_Com();
		
	}
	public static void test_Param_Com()
	{
		try
		{
		int sideLeng = 50;
		FileOutputStream fs1 = new FileOutputStream(new File("E:\\GANSHENME\\TEST1\\text1.txt"));
		FileOutputStream fs2 = new FileOutputStream(new File("E:\\GANSHENME\\TEST1\\text2.txt"));
		PrintStream p1 = new PrintStream(fs1);
		PrintStream p2 = new PrintStream(fs2);
		double starterP = 1.02;
		double tempP =starterP;
		/*double growParameter = 1.1795;
		
		p1.println(growParameter);
		System.out.println("Test"+0+":======================================="
				+ " growParameter: "+ growParameter
				+ "	sideLeng: " + 25);
		singleTest(25, growParameter, p2);
		*/
		for (int i = 0; i<50; i++)
		{
			tempP = tempP + 0.04;
			p1.println(tempP);
			
			System.out.println("Test"+i+":======================================="
					+ " growParameter: "+ tempP
					+ "	sideLeng: " + sideLeng);
			
			singleTest(sideLeng, tempP, p2);
		}
		p1.close();
		p2.close();
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	public static void testN_Com()
	{
		try
		{
		FileOutputStream fs1 = new FileOutputStream(new File("E:\\GANSHENME\\TEST1\\text1.txt"));
		FileOutputStream fs2 = new FileOutputStream(new File("E:\\GANSHENME\\TEST1\\text2.txt"));
		PrintStream p1 = new PrintStream(fs1);
		PrintStream p2 = new PrintStream(fs2);
		double growParameter = 1.1795;
		for (int i = 1 ; i<50; i++)
		{
			p1.println(i*10);
			
			System.out.println("Test"+i+":======================================="
					+ " growParameter: "+ growParameter
					+ "	sideLeng: " + i*10);
			
			singleTest(i*10, growParameter, p2);
		}
		p1.close();
		p2.close();
		}catch (Exception e)
			{
				e.printStackTrace();
			}
		
		
	}
	public static void testPrint()
	{
		FileOutputStream fs3;
		try {
			fs3 = new FileOutputStream(new File("E:\\text3.txt"));
			PrintStream p3 = new PrintStream(fs3);
			for (int i = 1 ; i<50; i++)
			{
				double temp = i*Math.log(i);
				p3.println(temp);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public static void singleTest(int sideLeng, double growParameter, PrintStream p2)
	{
		try{
			//Torus torus = new Torus(20,1.1795);
			Torus torus = new Torus(sideLeng, growParameter);
			torus.initialize();
			System.out.println("p:"+torus.getMaxFinalPhase());
			Position pos = torus.electLeader();
			System.out.println("leader x:" + pos.getX() + "	y:" + pos.getY());
			System.out.println("Message Count: "+ torus.getMessageCount());
			p2.println(torus.getMessageCount());
			
			}
			catch (Exception ex)
			{
				System.out.println(ex.toString());
				ex.printStackTrace();
			}
	}
}
