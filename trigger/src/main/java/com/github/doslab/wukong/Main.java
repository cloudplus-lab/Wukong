/**
 * Copyright (2020, ) Institute of Software, Chinese Academy of Sciences
 */
package com.github.doslab.wukong;

/**
 * @author wuheng@otcaix.iscas.ac.cn
 * 
 * @version 2.3.0
 * @since 2021.2.15
 * 
 **/
public class Main {

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			usage();
			System.exit(1);
		}
		
		int num = Integer.parseInt(args[0]);
		switch(num){
	    case 1 :
	       new NewVersionAnalyzer().analyse();
	       break; //��ѡ
	    case 2 :
	       new GenerateConfigAnalyzer().analyse();
	       break; //��ѡ
	    case 3 :
		   new GenerateCloudletAnalyzer().analyse();
		   break; //��ѡ
	    case 4 :
		   new NewAPIAnalyzer().analyse();    //���
		   break; //��ѡ
	    default : //��ѡ
	       usage();
	}
	}

	private static void usage() {
		System.out.println("Usage: ");
		System.out.println("\t1:" + NewVersionAnalyzer.class.getSimpleName());
		System.out.println("\t2:" + GenerateConfigAnalyzer.class.getSimpleName());
		System.out.println("\t3:" + GenerateCloudletAnalyzer.class.getSimpleName());
		System.out.println("\t3:" + NewAPIAnalyzer.class.getSimpleName());
	}

}
