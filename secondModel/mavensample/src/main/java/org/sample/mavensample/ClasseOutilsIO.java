package org.sample.mavensample;

/**
 * Ce fichier/classe est le fichier contenant les fonctions d'affichages / input
 * pour mon projet de stage de L3, 
 * 
 * @implNote il est à compléter notament en vue d'un portage avec une interface 'user frendly'
 *
 * @author Robinson LANGLOIS
 * @since 02/06/2025
 */
public class ClasseOutilsIO {
    // Pour les couleurs 
    // Declaring ANSI_RESET so that we can reset the color
    public static final String ANSI_RESET = "\u001B[0m";

    // Declaring the color
    // Custom declaration
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_GREEN = "\u001B[32m";

	/**
	 * Affiche un accord sous forme de tablature dans la console
	 * @param guitare
	 * @param chord
	 * @param numero
	 */
	static void	printChord(ClasseGuitare guitare, int[] chord, int numero) 
	{
		System.out.println("Accord n°"+numero+" :");
		for (int j=guitare.nc-1; j>=0; j--)
		{
			if (chord[j]==0)
			{
				System.out.print(ANSI_BLUE+"o"+ANSI_RESET);
			} else if (chord[j] == -1)
			{
				System.out.print(ANSI_RED+"x"+ANSI_RESET);
			} else {
				System.out.print(" ");
			}
			System.out.print("╟");
			for (int k=-1; k<=guitare.nf; k++)
			{
				if (chord[j]==k)
				{
					if (k<10 && k>0) System.out.print("┄"+ANSI_GREEN+k+ANSI_RESET);
					else if (k==0 || k==-1) System.out.print("─");
					else System.out.print(ANSI_GREEN+k+ANSI_RESET);
				}
				else if (k>0) System.out.print("─");
			}
			System.out.print("\n");
		}
	}
}
