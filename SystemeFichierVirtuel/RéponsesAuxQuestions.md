***Question 1 : Représentation binaire***

**Pourquoi est-il nécessaire de définir explicitement une convention telle que big-endian lorsqu’une structure est stockée dans un tableau de byte ? Que se passerait-il si writeInt utilisait big-endian mais readInt supposait little-endian sur une autre machine ?**



Un entier 32 bits est découpé en 4 octets. Sans convention, on ne sait pas si l'octet significatif doit être placé au début (Big-Endian) ou à la fin (Little-Endian).



Si "writeInt" utilise Big-Endian et "readInt" Little-Endian, la valeur lue sera corrompue (les octets de poids fort et faible seront inversés)

\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_

***Question 2 : Signes et octets***

**Pourquoi une opération telle que *memory\[offset] \& 0xFF* est-elle importante lors de la reconstruction d’une valeur entière ? Expliquez le rôle de l’opérateur binaire en Java.**



En Java, le type "byte" est signé de -128 à 127. Lors d'une conversion d'un "byte" vers un "int", Java remplit les bits de poids fort avec des "1" si le nombre est négatif. 



Appliquer le masque permet de traiter l'octet comme une valeur non signée (de 0 à 255).

Rôle de l'opérateur "\&" : C'est un ET binaire bit à bit qui ne conserve que les 8 bits de poids faible de la valeur.

\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_

***Question 3 : Sérialisation***

**Pourquoi tester uniquement *readInt(writeInt(x)) == x* peut-il être insuffisant ? Et si oui comment sampler correctement les valeurs de x ? Expliquez en quoi l’inspection directe des octets permet de détecter davantage de classes d’erreurs.**



Si l'écriture et la lecture ont le même bug symétrique, le test inverse sera valide alors que l'organisation dans la mémoire sera fausse. De plus, tester une seule valeur ne garantit pas la gestion des cas limites.



Pour sampler la valeur de x il faut appliquer des valeurs extrêmes *("Integer.MIN\_VALUE", "Integer.MAX\_VALUE")*, zéro, valeurs positives et/ou négatives, et masques de bits spécifiques.



Intérêt de l'inspection directe :\*\* Vérifier directement les valeurs octet par octet dans le tableau "byte\[]" garantit que le layout physique correspond exactement aux exigences du format binaire attendu, indépendamment du code de lecture.



\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_

***Question 4 : Bitmap***

**Pourquoi un bitmap est-il plus compact qu’une représentation utilisant un entier par bloc ? Exprimez la taille du bitmap en fonction du nombre *N* de blocs.**



Un bitmap utilise un seul bit pour représenter l'état d'un bloc (0 = libre et 1 = occupé), alors qu'un entier consomme 32 bits soit 4 octets. Le bitmap réduit donc l'occupation mémoire de la table d'allocation.

Pour N blocs, le bitmap nécessite (N / 8) octets (soit N / 8 octets si N est un multiple de 8).

\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_

***Question 5 : Layout***

**Pourquoi les structures du système de fichiers doivent-elles occuper des zones mémoire déterministes ? Que se passerait-il si la position du bitmap changeait sans que les méthodes qui l’utilisent soient modifiées ?**



Le système n'a pas de métadonnées de démarrage pour localiser sa propre structure. Des offsets fixes permettent de retrouver le Superbloc, le Bitmap et la table des Inodes lors de l'initialisation.



Si le bitmap change de position sans mise à jour des méthodes, le système va lire / écrire des bits d'allocation au mauvais endroit en mémoire, ce qui va écraser accidentellement la table des inodes.

\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_

***Question 6 : Inode***

**Pourquoi séparer les métadonnées du fichier de son contenu ? Expliquez pourquoi un inode peut être considéré comme une structure permettant de retrouver le contenu d’un fichier sans contenir directement ce contenu.**



Séparer les métadonnées permet de manipuler les attributs d'un fichier (taille, permissions, dates) sans charger son contenu en mémoire.



L'inode ne stocke pas directement le texte ou les octets du fichier, mais contient une liste d'adresses. Ces adresses renvoient vers les numéros de blocs où le contenu est écrit.

\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_

***Question 7 : Allocation***

**Pourquoi allocateBlock() doit-il commencer à rechercher à partir du bloc 129 ? Que se passerait-il si la recherche commençait au bloc 0 ?**



Les 129 premiers blocs sont réservés aux structures système : le Superbloc, le Bitmap et la table des Inodes. 	



Si la recherche commence a 0, l'allocateur renverrait des blocs réservés au système, et l'écriture de données utilisateur écraserait les métadonnées du système de fichiers (Superbloc, Bitmap ou Inodes), ce qui corromprait la mémoire.

\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_

***Question 8 : Fragmentation***

**Deux systèmes peuvent-ils posséder exactement le même nombre de blocs libres mais présenter des niveaux de fragmentation différents ? Justifiez votre réponse avec une représentation sous forme de séquence de blocs libres et occupés.**



Oui, deux systèmes peuvent avoir exactement le même nombre de blocs libres tout en ayant un niveau de fragmentation différent.



Justification : exemple : 2 systèmes de 8 blocs de données avec 4 blocs libres et 4 blocs occupés ->



\- Système A (Non fragmenté) : "\[1]\[1]\[1]\[1] \[0]\[0]\[0]\[0]" (1 = occupé et 0 = libre)

Le système peut allouer un fichier de 4 blocs d'un seul coup.





\- Système B (Fragmenté) : "\[1]\[0]\[1]\[0] \[1]\[0]\[1]\[0]"

Il est impossible d'y allouer un fichier de plus de 1 bloc.



