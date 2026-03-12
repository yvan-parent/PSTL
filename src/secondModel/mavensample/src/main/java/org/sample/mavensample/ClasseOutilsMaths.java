package org.sample.mavensample;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

/**
 * Ce fichier/classe est le fichier contenant les fonctions utiles pour traiter des IntVar ou des distances
 * pour mon projet de stage de L3, 
 * 
 * @author Robinson LANGLOIS
 * @since 02/06/2025
 */
public class ClasseOutilsMaths {
	/**
	 * Trouve la distance maximum accessible par la main en posant l'index sur une frette
	 * @param player
	 * @param min
	 * @return la distance max accessible
	 */
    static Integer distance_legale(ClasseGuitariste player, int min)
	{	// Calcule le nombre de frettes accessibles depuis la plus haute fette (non nulle) de l'accord en fonction de distanceFirstFrette
		double m = ((double)min)/12;
		return (int)((double)player.distanceFirstFrette*Math.pow(2,m));
	}
	/**
	 * Envoie tous les positifs stricts sur 1 et tous les négatifs larges sur 0
	 * @param model
	 * @param n
	 * @return IntVar{0 ou 1}
	 */
    static IntVar injectionPositif(Model model, IntVar n)
	{	// Fonction en indéterminé qui à un entier str positif associe 1 et à un entier negatif 0
		return (
			(((n.sub((n.sub(1)).abs())).div((n.sub((n.sub(1)).abs())).abs())).add(1)).div(2).intVar()
		);
	}
}

