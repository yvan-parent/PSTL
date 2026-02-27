package org.sample.mavensample;

/**
 * Ce fichier/classe est le fichier défininisant les partitions/ encodage de suites d'accords
 * pour mon projet de stage de L3, 
 *
 * @author Robinson LANGLOIS
 * @since 02/06/2025
 */
public class ClassePartition {
	/**
	 * le tableau conteanant la suite d'accord
	 * Un accord est encodé par un {@code int[]} ou :
	 * - le premier élément est l'encodage midi de la note de construction
	 * - le  reste est un ensemble de shifts par rapport à la note de construction
	 * Par soucis de simplicité on demande à ce que les notes de l'accord soient ajoutées dans l'ordre de construction : fondamentale tierce/quarte quinte sixte septieme neuvième ...
	 */
	public int[][] chords;	// Tableau de l'encodage des accords
}
