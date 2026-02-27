package org.sample.mavensample;

/**
 * Ce fichier/classe est le fichier décrivant le modèle
 * de l'instrumentiste pour mon projet de stage de L3
 *
 * @author Robinson LANGLOIS
 * @since 02/06/2025
 */
public class ClasseGuitariste {
	/**
	 *  La distance accessible par les autres doigts en plaçant l'index sur la première frette
	 */
    public int distanceFirstFrette;

	/**
	 * Le nombre de doigts posables sur le manche
	 */
	public int nbDoigts;
	
	/**
	 * Si l'on peut faire des barrés et le nombre accessible de frettes avec un barré en première frette
	 */
	public int barre;
	
	/**
	 * Les préférences de la fonction toMinimise à modifier pour que ce soit plus comprhéensible
	 */
	public ClassePreferences preferences;
	
	//public int[] distanceEntreDoigts;	// idem mais pour plus tard
	//public boolean fingerSpan;				// Si l'on utilise la méthode de distance entre doigts ou pas
}
