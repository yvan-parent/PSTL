package org.sample.mavensample;

/**
 * Ce fichier/classe est le fichier contenant les préférences du guitariste
 * pour mon projet de stage de L3, 
 * 
 * @author Robinson LANGLOIS
 * @since 02/06/2025
 */
public class ClassePreferences {
    /**
     * Si fretteMoyenneFix -> entre bornes obligatoire sinon minimisation pour fretteMoyenne
     */
    public boolean fretteMoyenneFix;
    /**
     * cas fix sup
     */
    public int fretteMoyenneSup;
    /**
     * cas fix inf
     */
    public int fretteMoyenneInf;
    /**
     * cas minimisation moyenne
     */
    public int fretteMoyenne;
    /**
     * cas minimisation poids à accorder
     */
    public int fretteMoyennePoids;

    
    /**
     *  Si fonda BASS fix
     */
    public boolean fondaBassFix;
    /**
     * Si fonda BASS minimisation poids à accorder
     */
    public int fondaBassPoids;

    /**
     * Si Nb de cordes non jouées Fix ou minimisation
     */
    public boolean cordesNonJoueesFix;
    /**
     * Cas fix max de cordes non jouées
     */
    public int cordesNonJoueesMax;
    /**
     * Cas minimisation poids à accorder
     */
    public int cordesNonJoueesPoids;


    /**
     * Si Nb de cordes à vide Fix ou minimisation
     */
    public boolean cordesAVidesFix;
    /**
     * Cas fix max de cordes à vide
     */
    public int cordesAVidesMax; 
    /**
     * Cas minimisation poids accordé
     */
    public int cordesAVidesPoids;

    /**
     * Distance max     N'est pas fix car distanceFirstFrette.
     * Poids accordé
     */
    public int distanceMaxSouhaiteePoids;
    /**
     * Distance max à laquelle comprarer
     */
    public int distanceMaxSouhaitee;
    

    /**
     * Si Nb doigts utilisés Fix ou minimisation ne marche pas encore
     */
    public boolean doigtsUtilisesFix;
    /**
     * Cas fix max nb de doigts
     */
    public int doigtsUtilisesMaxNb;
    /**
     * Cas minimisation poids accordé
     */
    public int doigtsUtilisesPoids;

    /**
     * Norme 1  cas minimisation toujours le cas.
     * Poids accordé
     */
    public int norme1Poids;
    /**
     * Savoir si on prend en compte la norme 1 dans la minimisation
     */
    public boolean norme1Fix;

    /**
     * Si on ne veut pas de répétition de notes (même fréquence) en Fix ou en minimisation
     */
    public boolean pasRepetitionFix;
    /**
     * Si Fix le nombre de répétition qu'on autorise
     */
    public int pasRepetitionMax;
    /**
     * Si minimisation le poids accordé
     */
    public int pasRepetitionPoids;
}
