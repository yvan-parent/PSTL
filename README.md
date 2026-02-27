# Jouabilité d'une partition de guitare

Stage de L3 à l'IRCAM encadré par Charlotte Truchet et Charles Prud'homme.

## Joueabilité d'une partition de guitare
Le but de ce stage est de décider si une partition (suite d'accords) est physiquement jouable pour un guitariste.\
Pour cela on créé un modèle de programmation sous contraintes pour répondre à la question et trouver une suite de positions minimisant certaines contraintes.

## Description
Ce dépot est créé dans le cadre du stage que je réalise à l'IRCAM.\
Il contient deux parties différentes :

- Une partie rapport (dans laquelle on peut trouver des informations par rapport au travail mené)
- Une partie code (ici en java en utilisant le gestionaire de projet maven)

## Partie rapport
Comme l'indique les nom, ce dossier contient les fichier `.tex` du rapport de stage.\
Dans le raport on trouvera une explication plus précise de ce que l'on fait.
Voir [ici](https://forge-2.ircam.fr/rlangloi/jouabilite_partition_guitare/-/blob/main/ra_pr/rapport/rapport.pdf?ref_type=heads) pour lire le rapport.\
Il y a les illustrations sur le git et la commande pour afficher les tablatures (et d'autres commandes) sont présentes dans le fichier `commandes.tex`.

## Partie code
La partie code est un projet maven avec la version 24 de java.\
J'ai utilisé intellij qui propose une gestion simple de projet.\
J'imagine que ça fonctionne avec n'importe quel ide.\
Il y a deux parties code, une est pour le premier modèle, l'autre est pour le
second. Les deux modèles sont décrits dans le rapport.\
Pour éxecuter avec intellij :

- Ouvrir intellij
- Importer un projet -> projetJava/mavensample
- Dans intellij ouvrir -> src/main/java/org.sample.mavensample/App.java
- On a accès au code
- Pour éxécuter le code : appuyer sur la flèche

Pour executer depuis un terminal :

- Rendez vous dans : premierModel ou secondModel
- Executez la commande suivante :

`/usr/bin/env /usr/lib/jvm/java-24-openjdk/bin/java @/tmp/cp_7nyxrg2n44aat2k1a2zllkpzv.argfile org.sample.mavensample.App`

Le problème est qu'il y a une partie de hash qui est modifiée à chaque compilation ce qui rend la précédente commande partiellement utilisable.

### Second modèle

Le second modèle (le plus intéressant) est organisé dans `secondModel/mavensample/src/main/java/org/sample/mavensample/`.\
Il est réparti dans plusieurs fichiers, le principal est `App.java`.\
Toutes les fonctions sont commentées. Si vous avez des questions, n'hésitez pas à me contacter.


## Bibliothèque Choco
On utilise choco comme solver, on trouve [ici](https://choco-solver.org/docs/) sa documentation.


# État du projet

Nous avons un programme dans lequel plusieurs paramètres sont
ajustables. Il permet suivant une suite d'accords, d'un modèle de guitare et de guitariste de
donner des accords suivant les préférences entrées dans le modèle.

Les accords sont ensuite affichés sous forme de tablature dans le terminale dans le forme suivante :
```
0 10 9 9 8 7  :
╟┄0──────────────
╟──────────10────
╟─────────┄9─────
╟─────────┄9─────
╟────────┄8──────
╟───────┄7───────
```

## Auteur

Robinson Langlois\
mail: robinson dot langlois at ens-lyon dot fr
