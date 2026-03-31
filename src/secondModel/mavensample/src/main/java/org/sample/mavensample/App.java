package org.sample.mavensample;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Settings;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.search.strategy.Search;

import java.util.HashMap;
import java.util.Map;

/**
 * Ce fichier/classe est le fichier principal de mon projet de stage de L3
 *
 * @author Robinson LANGLOIS
 * @since 02/06/2025
 */
public class App
{
	/**
	 * permet, d'afficher les tablatures de la suite d'accords déterminées selon 
	 * les caractéristques de l'instrumentiste.
	 * Pour cela on a besoin de :
	 * @param guitare 		le modèle de la guitare
	 * @param player 		le modèle de l'instrumentiste
	 * @param partition		le modèle de la pratition
	 */
	static int[][] findChords(ClasseGuitare guitare, ClasseGuitariste player, ClassePartition partition, boolean print, int LimitSeconds)
	{
		// 1. Définition du modèle
		// 1.1. Définition
		// Model model = new Model("Chord", Settings.init().setLCG(true));
		Model model = new Model("Chord");
		
		long time1 = System.currentTimeMillis();
		
		// 1.2. Définition de l'espace physiquement possible
		Map<int[], Tuples> positions_legales = new HashMap<>();
		Map<int[], ClasseCritere> criteresStock = new HashMap<>();
		for (int i=0; i<partition.chords.length; i++)
		{
			positions_legales.put(partition.chords[i], new Tuples(true));
		}
		ClasseOutilsMusique.creerTuple(guitare, player, partition, model, positions_legales, criteresStock);
		
		System.out.println("step 1");

		if (print) {
			for (int i=0; i<partition.chords.length; i++)
			{
				System.out.println(positions_legales.get(partition.chords[i]));	
			}
			
			System.out.println("Espace solus time: " + (System.currentTimeMillis() - time1) + " ms");
		}

		// 1.3. on créé nos indéterminées en ajoutant les contraintes
		IntVar[][] doigtes = ClasseOutilsMusique.ajoutContraintes(guitare, player, partition, model, positions_legales);

		/* Affichage des positions légales
		for (int i=0; i<partition.chords.length; i++)
		{
			System.out.println(positions_legales.get(partition.chords[i]));
		}
		*/

		System.out.println("step 2");
		
		// 2. Résolution
		Solver solver = model.getSolver();
		//solver.setSearch(
        //        Search.lastConflict(Search.inputOrderLBSearch(ArrayUtils.flatten(doigtes)))
        //);
        /**/
		System.out.println("step 3");

		solver.setSearch(
                Search.inputOrderLBSearch(ArrayUtils.flatten(doigtes))
        );
		/**/
		/*
		solver.setSearch(
        	Search.conflictOrderingSearch(
                Search.inputOrderLBSearch(ArrayUtils.flatten(doigtes))
        ));
		*/
		System.out.println("step 4");
		if (print) solver.showShortStatistics();
		IntVar toMinimize = ClasseOutilsMusique.fonctionToMinimize(model, doigtes, guitare, partition, player, criteresStock);		// La quantité à minimiser
		System.out.println("step 5");
		solver.limitTime(LimitSeconds+"s");
		Solution solution = solver.findOptimalSolution(toMinimize, Model.MINIMIZE);		// Une solution minimisant toMinimize
		System.out.println("step 6");

		if (solution == null)
		{
			throw new RuntimeException("Aucune solution trouvée");
		}

		// 3. Affichage
		int[][] chords_v = new int[partition.chords.length][guitare.nc];
		for (int j=0; j<partition.chords.length; j++)
		{
			for (int k=0; k<guitare.nc; k++)
			{
				chords_v[j][k] = solution.getIntVal(doigtes[j][k]);
			}
			if (print) { 
				ClasseOutilsIO.printChord(guitare, chords_v[j], j);
			}
		}
		System.out.println("step 7");
		if (print) { System.out.println("toMinimize ---> "+solution.getIntVal(toMinimize)); }

		return chords_v;
	}


	/**
	 * Fonction main du programme,
	 * Dans laquelle on définit les préférences de l'instrumentiste, 
	 * la suite d'accords et l'instrument utilisé.
	 * @param args
	 */
    public static void main( String[] args )
    {
		// 1. Model of the guitar
		ClasseGuitare guitare = new ClasseGuitare();
                // Pour une guitare classique
        guitare.nf = 13;
        guitare.nc = 6;
        guitare.scordatura = new int[]{52, 57, 62, 67, 71, 76};

		// 2. Model of the player
		ClasseGuitariste player = new ClasseGuitariste();
		player.distanceFirstFrette = 4;							// Max distance from the first fret
		player.nbDoigts = 4;									// Number of finger excluding the thumb
		player.barre = 3;										// Wether barrés are possible -1 impossible, else number of fret after the barre in first position
		
		player.preferences = new ClassePreferences();
		player.preferences.fretteMoyenneFix = false;
		player.preferences.fretteMoyenne = 0;
		player.preferences.fretteMoyennePoids = 100;

		player.preferences.fondaBassFix = false;
		player.preferences.fondaBassPoids = 0;
		
		player.preferences.cordesNonJoueesFix = true;
		player.preferences.cordesNonJoueesMax = 1;

		player.preferences.cordesAVidesFix = false;
		player.preferences.cordesAVidesPoids = 0;

		player.preferences.distanceMaxSouhaitee = 3;
		player.preferences.distanceMaxSouhaiteePoids = 5;

		player.preferences.doigtsUtilisesFix = true;
		player.preferences.doigtsUtilisesMaxNb = 3;

		player.preferences.norme1Fix = true;
		player.preferences.norme1Poids = 5;

		player.preferences.pasRepetitionFix = false;
		player.preferences.pasRepetitionMax = 7;


		// 3. Modele de la suite d'accords
		ClassePartition partition = new ClassePartition();
		// Les Feuilles Mortes Y.M J.P J.K
		partition.chords = new int[][]{
			{57, 0, 3, 7, 10},
			{62, 0, 4, 7, 10},										
			{67, 0, 4, 7},
			{57, 0, 3, 7, 10},
			{59, 0, 4, 7, 10},
			{64, 0, 3, 7},
		};

		// 4. Resolution 
		long time = System.currentTimeMillis();
		int[][] res = findChords(guitare, player, partition, true, 1000);
		System.out.println("Resolution time: " + (System.currentTimeMillis() - time) + " ms");
	}

	public static int[][] getInfos (int[][] partition_chords, int TimeLimit) {
		// 1. Model of the guitar
		ClasseGuitare guitare = new ClasseGuitare();
		// Pour une guitare classique
		guitare.nf = 13;
		guitare.nc = 6;
		guitare.scordatura = new int[]{52, 57, 62, 67, 71, 76};

		// 2. Model of the player
		ClasseGuitariste player = new ClasseGuitariste();
		player.distanceFirstFrette = 4;							// Max distance from the first fret
		player.nbDoigts = 4;									// Number of finger excluding the thumb
		player.barre = 3;										// Wether barrés are possible -1 impossible, else number of fret after the barre in first position

		player.preferences = new ClassePreferences();
		player.preferences.fretteMoyenneFix = false;
		player.preferences.fretteMoyenne = 0;
		player.preferences.fretteMoyennePoids = 100;

		player.preferences.fondaBassFix = false;
		player.preferences.fondaBassPoids = 0;

		player.preferences.cordesNonJoueesFix = true;
		player.preferences.cordesNonJoueesMax = 1;

		player.preferences.cordesAVidesFix = false;
		player.preferences.cordesAVidesPoids = 0;

		player.preferences.distanceMaxSouhaitee = 3;
		player.preferences.distanceMaxSouhaiteePoids = 5;

		player.preferences.doigtsUtilisesFix = true;
		player.preferences.doigtsUtilisesMaxNb = 3;

		player.preferences.norme1Fix = true;
		player.preferences.norme1Poids = 5;

		player.preferences.pasRepetitionFix = false;
		player.preferences.pasRepetitionMax = 7;


		// 3. Modele de la suite d'accords
		ClassePartition partition = new ClassePartition();
		// Les Feuilles Mortes Y.M J.P J.K
		// partition.chords = new int[][]{
		// 		{57, 0, 3, 7, 10},
		// 		{62, 0, 4, 7, 10},
		// 		{67, 0, 4, 7},
		// 		{57, 0, 3, 7, 10},
		// 		{59, 0, 4, 7, 10},
		// 		{64, 0, 3, 7},
		// };
		partition.chords = partition_chords;

		// 4. Resolution
		int[][] res = findChords(guitare, player, partition, false, TimeLimit);
		return res;
	}
	
}
