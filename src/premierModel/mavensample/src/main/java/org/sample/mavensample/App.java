package org.sample.mavensample;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.Solver;

import java.util.List;


public class App
{
	static IntVar dist_from(IntVar s, int nfmi, Model model)
	{
		return model.intScaleView(s, nfmi);
	}

	static void	printChord(int nc, int nf, int[] chord) 
	{	// Affiche un accord sous forme de tablature dans la console
		for (int i=0; i<nc; i++)
		{
			System.out.printf("%d ", chord[i]);
		}
		System.out.printf("\n");
		for (int j=nc-1; j>=0; j--)
		{
			if (chord[j]==0)
			{
				System.out.print("o");
			} else if (chord[j] == -1)
			{
				System.out.print("x");
			} else {
				System.out.print(" ");
			}
			System.out.print("╟");
			for (int k=-1; k<=nf; k++)
			{
				if (chord[j]==k)
				{
					if (k<10 && k>0) System.out.print("┄"+k);
					else if (k==0 || k==-1) System.out.print("──");
					else System.out.print(k);
				}
				else System.out.print("─");
			}
			System.out.print("\n");
		}
	}

	static void enumChords(int nc, int nf, Model model, IntVar[] position)
	{
		Solver solver = model.getSolver();
		int cpt = 0;
		List<Solution> solutions = solver.findAllSolutions();
		for(int i=0; i<solutions.toArray().length; i++)
		{
			System.out.println("Solus n°"+cpt+" :");
			cpt = cpt+1;

			int[] chord = new int[nc];
			for (int j=0; j<nc; j++)
			{
				chord[j] = solutions.get(i).getIntVal(position[j]);
			}
			printChord(nc, nf, chord);
		}
	}

	static void findChord(int nc, int nf, int[] sc, int la, int[] ac, int nfmi, boolean isba, int nbdo)
	{
		// Model def ------>>
		Model model = new Model("Chord");

		IntVar[] position = new IntVar[nc];									// unknown positions
		for (int i=0; i<nc; i++)
		{
			position[i] = model.intVar("String n°"+i, 0, nf);		// Need to add the possibility to not play a string
		}
		//------>>


		// Constraint ------>>
		//		simple constraint, every string play a chord note, every chord note is played at least once
		BoolVar[][] b1 = new BoolVar[nc][la];
		for (int i=0; i<nc; i++)
		{
			for (int j=0; j<la; j++) 
			{
				b1[i][j] = model.mod(model.intOffsetView(position[i], sc[i] - ac[0] - ac[j+1]), 12, 0).reify();
			}
		}
		for (int i=0; i<nc; i++) 
		{
			for (int j = 0; j < la-1; j++) 
			{
				model.or(b1[i]).post();
			}
		}

		BoolVar[][] b2 = new BoolVar[la][nc];
		for (int i=0; i<la; i++)
		{
			for (int j=0; j<nc; j++) 
			{
				b2[i][j] = model.mod(model.intOffsetView(position[j], sc[j] - ac[0] - ac[i+1]), 12, 0).reify();
			}
		}
		for (int i=0; i<la; i++) 
		{
			for (int j=0; j<nc-1; j++) 
			{
				model.or(b2[i]).post();
			}
		}

		// Physical constraint
		// The distance between fingers is bounded
		
		for (int i=0; i<nc; i++){
			for (int j=0; j<nc; j++){
				model.ifThen(
				model.arithm(position[i], ">", 0),																												// if the chord is not 0 -> min would be better
				model.arithm(model.intScaleView(position[i], 6), "-", model.intScaleView(position[j], 6), "<=", dist_from(position[i], nfmi, model))	// The maximal distance is lower than 2
				);
			}
		}
		
		
		
		// At least two strings are on fret 0
		/*
		if (isba)		// Barré possible 
		{
											// At least 1+nc-nbdo positions on the minimum fret
		} else 			// Barré impossible
		{
											// At least 1+nc-nbdo positions on the 0 fret
		}
		*/
		// When barrés are not possible
		/*
		System.err.printf("blablabla : %d\n", nc-nbdo);
		BoolVar[] occs = new BoolVar[nc];
		for (int i=0; i<nc; i++)
		{
			IntVar occN = model.intVar("occ"+i, nc-nbdo, nc);
			occs[i] = model.count(i, position, occN).reify();
		}
		model.or(occs).post();		// /!\ --> Problème, on a plein de doublons ! on règle comme suis pour l'instant mais pas sur que ce soit opti ...
		*/
		//------>>
		enumChords(nc, nf, model, position);
	}

    public static void main( String[] args )
    {
		// Model of the guitar ------>>
		int nc = 6;															// number of strings
		int nf = 13;														// number of frets
		int[] sc = new int[]{52, 57, 62, 67, 71, 76};						// scordatura
		//------>>
		
		// Model of the player ------>>
		int nfmi = 4;														// Max distance from the first fret
		int nbdo = 4;														// Number of finger including the thumb
		boolean isba = true;												// Wether barrés are possible
		//------>>

		// The song ------>>
		int nb_ac = 6;														// number of chords
		int[] la = new int[]{4, 4, 3, 4, 4, 3};								// lengths of each chord
		int[][] ac = new int[][]{											// enum of each chord
				{57, 0, 3, 7, 10},
				{62, 0, 4, 7, 10},											// Les Feuilles Mortes Y.M J.P J.K
				{67, 0, 4, 7},
				{57, 0, 3, 7, 10},
				{59, 0, 4, 7, 10},
				{64, 0, 3, 7},
		};


		//------>>

		// Resolution ------>>
		for (int i=0; i<nb_ac; i++)
		{
			System.out.println("######### Accord n°"+i);
			findChord(nc, nf, sc, la[i], ac[i], nfmi, isba, nbdo);
		}
		//------>>
	}
}
