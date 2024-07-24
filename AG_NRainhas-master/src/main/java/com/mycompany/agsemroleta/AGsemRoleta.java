
package com.mycompany.agsemroleta;

/**
 *
 * @author Pichau
 */
public class AGsemRoleta {

    public static void main(String[] args) {
                int nRainhas =16 ;
		int nPop = 30;
		int nElite = 4;
		int numGer = 10000;
		
		
		IndividuoFactory indFact = new IndividuoNRainhasFactory(nRainhas);
		AG ag = new AG();
		Individuo ind = ag.executar(indFact, nPop, nElite, numGer);
		
		System.out.println(ind);
		
    }
}
