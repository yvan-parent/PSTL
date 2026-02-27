package org.sample.mavensample;

/**
 * Ce fichier/classe est le fichier définissant les critères associés à une position
 * pour mon projet de stage de L3, 
 * 
 * @implNote il est à compléter notament en vue d'un portage avec une interface 'user frendly'
 *
 * @author Robinson LANGLOIS
 * @since 02/06/2025
 */
public class ClasseCritere {
    /**
     * Frette moyenne de la position
     */
    public int moyenneFrette;

    /**
     * Distance de l'accord (sur combien de frêtes s'étale-t-il)
     */
    public int distance;

    /**
     * nombre de cordes à vide
     */
    public int nbCordesAVide;

    /**
     * nombre de cordes non jouées
     */
    public int nbCordesNonJouees;

    /**
     * nombre de doigts utilisés
     */
    public int nbDoigtsUtilises;

    /**
     * la basse jouée
     */
    public int basse;

    /**
     * la fondamentale de l'accord associé
     */
    public int fonda;

    /**
     * On ne veut pas plusieurs fois la même fréquence
     */
    public int pasRepetitionCpt;
}
