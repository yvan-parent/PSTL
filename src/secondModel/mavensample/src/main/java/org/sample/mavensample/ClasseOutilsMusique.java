package org.sample.mavensample;

import java.util.Map;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.IntVar;

/**
 * Ce fichier/classe est le fichier traitant des fonctions 
 * inhérentes à la musique de mon projet de stage de L3
 *
 * @author Robinson LANGLOIS
 * @since 02/06/2025
 */
public class ClasseOutilsMusique {
	/**
	 * Vérifie, si une position (position_courante) est légale dans 
	 * le sens où elle peut être jouée par l'instrumentiste
	 * @param position_courante		la position à vérifier
	 * @param guitare				le modèle de la guitare
	 * @param player				le modèle de l'instrumentiste
	*/
    static Boolean est_legal(int[] position_courante, ClasseGuitare guitare, ClasseGuitariste player)
	{
		// 1. on détermine la position min, la seconde position min (pour savoir ou faire éventuellement le barré), et la position max
		int min=guitare.nf;					// Y a t-il un 0 entre des frettes appuyées
		int premiere_frette=guitare.nf;
		int max=0;
		boolean flag=true;			// tant qu'on n'utilise pas de doigt, on peut jouer à vide ou ne pas jouer sans problème
		for (int i=0; i<guitare.nc; i++)
		{
			if (!((position_courante[i]==-1)||(position_courante[i]==0 && flag)))
			{
				flag=false;
				if (min>position_courante[i]) {
					min = position_courante[i];
				} if (position_courante[i]>0 && position_courante[i]<premiere_frette) {
					premiere_frette=position_courante[i];
				} if (max<position_courante[i]) {
					max = position_courante[i];
				}
			}
		}

		int occPremiereFrette=0;
		int occAutre=0;
		for (int i=0; i<guitare.nc; i++)
		{
			if (position_courante[i] == premiere_frette)
			{
				occPremiereFrette++;
			}
			else if (!((position_courante[i] == 0)||(position_courante[i] == -1)))
			{
				occAutre++;
			}
		}
		if (player.barre < 0 || min==0)
		{	// On ne peut pas faire de barrés
			return (occAutre+occPremiereFrette<=player.nbDoigts) && (max-premiere_frette<ClasseOutilsMaths.distance_legale(player, premiere_frette));	
		} 
		else 
		{	
			return ((occAutre<=player.nbDoigts-1) && (max-premiere_frette<ClasseOutilsMaths.distance_legale(player, premiere_frette))) // on fait un barré
					||
					((occAutre+occPremiereFrette<=player.nbDoigts) && (max-premiere_frette<ClasseOutilsMaths.distance_legale(player, premiere_frette)));	// on ne fait pas de barré
		}
	}

	/**
	 * renvoit la quantité à minimiser en fonction des préférences
	 * @param model				le modèle du solveur
	 * @param doigtes			les indéterminées
	 * @param guitare			le modèle de la guitare
	 * @param partition			le modèle de la pratition
	 * @param player			le modèle de l'instrumentiste
	 * @param criteresStock		les critères qui ont été stockés lors du filtrage des positions
	 * @return res				une quantité indéterminée
	 */
    static IntVar fonctionToMinimize(Model model, IntVar[][] doigtes, ClasseGuitare guitare, ClassePartition partition, ClasseGuitariste player, Map<int[], ClasseCritere> criteresStock)
	{	// 1. définie la quantité à minimiser pour la solution
		IntVar res = model.intVar(0);
		
		// 2. Frette Moyenne
		if (!player.preferences.fretteMoyenneFix)
		{	// Le plus proche de la tête du manche !! gerer le pb du -1
			for (int i=0; i<doigtes.length; i++)
			{
				for (int j=0; j<doigtes[i].length; j++)
				{
					res = res.add(((model.intView(-1,doigtes[i][j], player.preferences.fretteMoyenne)).abs().mul(ClasseOutilsMaths.injectionPositif(model, doigtes[i][j]))).mul(player.preferences.fretteMoyennePoids)).intVar();
				}
			}
		}
		// 3. Norme 1
		if (!player.preferences.norme1Fix)
		{
			for (int i=0; i<doigtes.length-1; i++)
			{	// Gerer le pb du 0 et du -1
				for (int j=0; j<doigtes[i].length; j++)
				{
					res = res.add(
						(((doigtes[i][j].sub(doigtes[i+1][j])).abs()).mul(ClasseOutilsMaths.injectionPositif(model, doigtes[i][j]))
						).mul(ClasseOutilsMaths.injectionPositif(model, doigtes[i][j])).mul(player.preferences.norme1Poids))	
					.intVar();
				}	
			}
		}
		// 4. Minimiser le nombre de cordes non jouées
		if (!player.preferences.cordesNonJoueesFix)
		{
			for (int i=0; i<doigtes.length; i++)
			{
				for (int j=0; j<doigtes[i].length; j++)
				{
					res = res.add(((ClasseOutilsMaths.injectionPositif(model, doigtes[i][j].add(1).intVar())).sub(1)).mul(-1).mul(player.preferences.cordesNonJoueesPoids)).intVar();
				}
			}
		}

		// 5. Minimiser le nombre de cordes à vide
		if (!player.preferences.cordesAVidesFix)
		{
			for (int i=0; i<doigtes.length; i++)
			{
				for (int j=0; j<doigtes[i].length; j++)
				{
					res = res.add(((ClasseOutilsMaths.injectionPositif(model, doigtes[i][j].abs().intVar())).sub(1)).mul(-1).mul(player.preferences.cordesNonJoueesPoids)).intVar();
				}
				/*
				for (int i=0; i<doigtes.length; i++)
				{
					res = res.add((criteresStock.get(doigtes[i]).nbCordesAVides).mul(player.preferences.cordesNonJoueesPoids)).intVar();
				}
				*/
			}
		}

		// 6. FondaBass
		if (!player.preferences.fondaBassFix)
		{
			for (int i=0; i<doigtes.length; i++)
			{
				res = res.add(ClasseOutilsMaths.injectionPositif(model, 
					((ClasseOutilsMaths.injectionPositif(model, ((doigtes[i][0].sub(partition.chords[i][0]+partition.chords[i][1]-guitare.scordatura[0])).mod(12)).intVar())).add(
					ClasseOutilsMaths.injectionPositif(model, (((doigtes[i][0].sub(partition.chords[i][0]+partition.chords[i][1]-guitare.scordatura[0])).mod(12)).neg()).intVar()))
					).intVar()).mul(player.preferences.fondaBassPoids)).intVar();
			}
		}

		// 7. on ne veut pas plusieurs fois la même fréquence
		if (!player.preferences.pasRepetitionFix)
		{
			for (int i=0; i<doigtes.length; i++)
			{
				for (int k=0; k<doigtes[i].length-1; k++)
				{
					for (int l=k+1; l<doigtes[i].length; l++)
					{
						res = res.add(ClasseOutilsMaths.injectionPositif(model, (((doigtes[i][k].sub(doigtes[i][l])).add(guitare.scordatura[k]-guitare.scordatura[l])).abs().neg().add(1)).intVar())).intVar();
					}
				}
			}
		}

		return res;
	}

	/**
	 * renvoit si les critères sont vérifiés par les préférences de l'instrumentiste
	 * @param critere		les critères
	 * @param player		le modèle de l'instrumentiste
	 * @return res			un booléen indiquant si les critères sont valides
	 */
	static boolean respect(ClasseCritere critere, ClasseGuitariste player)
	{
		boolean res = true;
		if (player.preferences.fretteMoyenneFix)
		{
			res = res && critere.moyenneFrette < player.preferences.fretteMoyenneSup && critere.moyenneFrette > player.preferences.fretteMoyenneInf;
		}
		if (player.preferences.fondaBassFix)
		{
			res = res && ((critere.fonda - critere.basse)%12 == 0);
		}
		if (player.preferences.cordesNonJoueesFix)
		{
			res = res && (critere.nbCordesNonJouees<player.preferences.cordesNonJoueesMax);
		}
		if (player.preferences.cordesAVidesFix)
		{
			res = res && (critere.nbCordesAVide<player.preferences.cordesAVidesMax);
		}
		if (player.preferences.pasRepetitionFix)
		{
			res = res && (critere.pasRepetitionCpt < player.preferences.pasRepetitionMax);
		}
		
		return res;
	}

	/**
	 * renvoit la frette moyenne d'une position
	 * sans compter la corde à vide ou non jouée
	 * @param position la position
	 * @return res la frette moyenne de la position
	 */
	static int calculFretteMoyenne(int[] position)
	{	// Calcule la frette sur laquelle la position est centrée 
		int res = 0;
		int cpt = 0;
		for (int i=0; i<position.length; i++)
		{
			if (position[i]>0)
			res = res + position[i];
			cpt++;
		}
		return res/cpt;
	}

	/**
	 * ajoute {@code position_courante} au tuple de 
	 * {@code position_légale} corespondant à {@code accord} si 
	 * {@code position_courante} est jouable 
	 * et si c'est bien un accord de la suite d'accords
	 * @param guitare
	 * @param player
	 * @param accord
	 * @param position_courante
	 * @param positions_legales
	 * @param criteresStock
	 * @param i
	 */
	static void estAccord(ClasseGuitare guitare, ClasseGuitariste player,int[] accord, int[] position_courante,  Map<int[], Tuples> positions_legales, Map<int[], ClasseCritere> criteresStock, int i)
	{
		// 1. On vérifie que c'est un des accords
		//Les notes jouées sont dans l'accord --->
		boolean flag1 = true;
		for (int j=0; j<guitare.nc; j++)
		{
			boolean flag2 = false;
			for (int k=0; k<accord.length-1; k++) 
			{
				 if((position_courante[j] == -1) || (position_courante[j] + guitare.scordatura[j] - accord[0] - accord[k + 1]) % 12 == 0)
				 {
					flag2 = true;
				 }
			}
			flag1 = flag1 && flag2;
		}
		// --->
		// Les notes de l'accord sont jouées au moins une fois --->
		for (int j=0; j<accord.length-1; j++)
		{
			boolean flag2 = false;
			for (int k=0; k<guitare.nc; k++) 
			{
				if((position_courante[k] != -1) && (position_courante[k] + guitare.scordatura[k] - accord[0] - accord[j + 1]) % 12 == 0)
				{
					flag2 = true;
				}
			}
			flag1 = flag1 && flag2;
		}
		// --->
		// 2. On ajoute la position à la table et on ajoute les critères de la position à la table.
		if (flag1)
		{
			ClasseCritere criteres = new ClasseCritere();
			criteres.moyenneFrette = calculFretteMoyenne(position_courante);
			int max = 0;
			int min = guitare.nf;
			int nbCordesAVide = 0;
			int nbCordesNonJouees = 0;
			int basse = -1;
			for (int j=0; j<position_courante.length; j++)
			{
				if (position_courante[j]==0) nbCordesAVide++;
				else if (position_courante[j]==-1) nbCordesNonJouees++;
				else
				{
					if (position_courante[j] < min) min = position_courante[j];
					if (position_courante[j] > max) max = position_courante[j];
				}

				if (position_courante[j]!=-1 && basse==-1)
				{
					basse = guitare.scordatura[j]+position_courante[j];
				}
			}
			criteres.distance = max-min;
			criteres.nbCordesAVide = nbCordesAVide;
			criteres.nbCordesNonJouees = nbCordesNonJouees; 
			criteres.basse = basse;
			criteres.fonda = accord[0]+accord[1];
			
			criteres.pasRepetitionCpt = 0;
			for (int k=0; k<position_courante.length-1; k++)
			{
				for (int l=k+1; l<position_courante.length; l++)
				{
					if (position_courante[k]+guitare.scordatura[k] == position_courante[l]+guitare.scordatura[l]) criteres.pasRepetitionCpt++;
				}
			}

			if (respect(criteres, player)) {
			
				(positions_legales.get(accord)).add(position_courante);
				criteresStock.put(position_courante, criteres);
			}
		}
	}
	/**
	 * @deprecated
	 * @param guitare
	 * @param accord
	 * @param position_courante
	 * @param positions_legales
	 * @param criteresStock
	 * @param i
	 */
	static void estAccord1(ClasseGuitare guitare, int[] accord, int[] position_courante,  Map<int[], Tuples> positions_legales, Map<int[], int[]> criteresStock, int i)
	{	// Vérifie si la position courante est un des accords de la suite, si oui, l'ajoute à la table de l'accord et ajoute sa propriété dans la hashtbl.
		// Les notes jouées sont dans l'accord --->
		boolean flag1 = true;
		for (int j=0; j<guitare.nc; j++)
		{
			if (position_courante[j]!=-1) 
			{
				boolean flag2 = false;
				for (int k=1; k<accord.length; k++) 
				{
					if((position_courante[j] + guitare.scordatura[j] - accord[0] - accord[k]) % 12 == 0)
					{
						flag2 = true;
					}
				}
				flag1 = flag1 && flag2;
			}
		}
		// --->
		// Les notes de l'accord sont jouées au moins une fois --->
		for (int j=1; j<accord.length; j++)
		{
			boolean flag2 = false;
			for (int k=0; k<guitare.nc; k++) 
			{
				if((position_courante[k]!=-1) && ((position_courante[k] + guitare.scordatura[k] - accord[0] - accord[j]) % 12 == 0))
				{
					flag2 = true;
				}
			}
			flag1 = flag1 && flag2;
		}
		// --->
		if (flag1)
		{
			(positions_legales.get(accord)).add(position_courante);
			//int[] criteres = new int[3];
			//criteres[0] = fretteMoyenne(position_courante);
			//criteresStock.put(position_courante, criteres);
		}
	}
	
	/**
	 * Remplis les tuples {@code positions_legales} et {@code criteresStock} 
	 * en réalisant un parcours sur l'univers des position possibles.
	 * @param guitare
	 * @param player
	 * @param partition
	 * @param model
	 * @param positions_legales
	 * @param criteresStock
	 */
    static void creerTuple(ClasseGuitare guitare, ClasseGuitariste player, ClassePartition partition, Model model, Map<int[], Tuples> positions_legales, Map<int[], ClasseCritere> criteresStock)
	{	
		int tmp = (int)Math.pow((double)guitare.nf+2, (double)(guitare.nc));
		int[] position_courante = new int[guitare.nc];
		for (int i=0; i<tmp; i++)
		{
			for (int j=0; j<guitare.nc; j++)
			{
				position_courante[j]=(i/((int)Math.pow((double)guitare.nf+2, (double)j))%(guitare.nf+2))-1;	
			}
			if (ClasseOutilsMusique.est_legal(position_courante, guitare, player))
			{
				for (int j=0; j<partition.chords.length; j++)
				{
					estAccord(guitare, player,partition.chords[j], position_courante, positions_legales, criteresStock, i);
				}
			}
		}
	}

	static void creerTupleR(ClasseGuitare guitare, ClasseGuitariste player, ClassePartition partition, Model model, Map<int[], Tuples> positions_legales, Map<int[], ClasseCritere> criteresStock)
	{	
		int tmp = (int)Math.pow((double)guitare.nf, (double)(guitare.nc+1));
		int[] position_courante = new int[guitare.nc];
		for (int i=0; i<tmp; i++)
		{
			for (int j=0; j<guitare.nc; j++)
			{
				position_courante[j]=(i/((int)Math.pow((double)guitare.nf, (double)j))%guitare.nf)-1;	
			}

			/*
			for (int k=0; k<guitare.nc; k++)
			{
				System.out.printf("%d | ", position_courante[k]);
			}
			System.out.printf("\n");
			*/
			if (ClasseOutilsMusique.est_legal(position_courante, guitare, player))
			{
				for (int j=0; j<partition.chords.length; j++)
				{
					estAccord(guitare, player,partition.chords[j], position_courante, positions_legales, criteresStock, i);
				}
			}
		}
	}
	
	/**
	 * @deprecated
	 * @param guitare
	 * @param player
	 * @param model
	 * @param positions_legales
	 */
    static void creerTuple2(ClasseGuitare guitare, ClasseGuitariste player, Model model, Tuples positions_legales)
	{	// S'occupe de créer les tables (domaines de chaques positions) ancien
		int tmp = (int)Math.pow((double)guitare.nf, (double)(guitare.nc+1));
        int[] position_courante = new int[guitare.nc];
        for (int i=0; i<guitare.nc; i++)
        {
            position_courante[i] = 0;
        }
		for (int i=0; i<tmp; i++)
		{
            if (ClasseOutilsMusique.est_legal(position_courante, guitare, player))
			{
				positions_legales.add(position_courante);
			}
            for (int j=guitare.nc-1; j>0; j--)
            {
                if (position_courante[j-1]==guitare.nf)
                {
                    position_courante[j]=(position_courante[j]+1)%guitare.nf;
                }
            }
            position_courante[0]=(position_courante[0]+1)%guitare.nf;
		}
	}

	/**
	 * Créé les indéterminés et ajoute les contraintes au modèle
	 * @param guitare
	 * @param player
	 * @param partition
	 * @param model
	 * @param positions_legales
	 * @return les positions indéterminés
	 */
	static IntVar[][] ajoutContraintes(ClasseGuitare guitare, ClasseGuitariste player, ClassePartition partition, Model model, Map<int[], Tuples> positions_legales)
	{	// Ajoutes les contraintes au model
		// ## Définition des variables indéterminées
		IntVar[][] doigtes = model.intVarMatrix("doigtés", partition.chords.length, guitare.nc, -1, guitare.nf);
		// # Contraintes
		for (int i=0; i<partition.chords.length; i++)
		{
			model.table(doigtes[i], positions_legales.get(partition.chords[i]), "CT+").post();	// On s'assure que l'accord est physiquement joueable.
		}
		return doigtes;
	}
}
